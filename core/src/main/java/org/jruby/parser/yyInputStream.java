package org.jruby.parser;
import java.awt.TextArea;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
/** used to reroute standard input from a {@link java.awt.TextArea}.
    Feeds all read methods from listening to typed keys.
	Should not deadlock because one should generally not
    read from within the event thread.
 */
public class yyInputStream extends InputStream implements KeyListener {
  /** line edit buffer.
    */
  protected final StringBuffer line = new StringBuffer();
  /** completed lines, ready to be read.
      Invariant: null after {@link #close}.
    */
  protected ArrayList queue = new ArrayList();
  
  public synchronized int available () throws IOException {
	if (queue == null) throw new IOException("closed");
	return queue.isEmpty() ? 0 : ((byte[])queue.get(0)).length;
  }

  public synchronized void close () throws IOException {
	if (queue == null) throw new IOException("closed");
	queue = null;
  }

  public synchronized int read () throws IOException {
	if (queue == null) throw new IOException("closed");
	while (queue.isEmpty())
	  try {
		wait();
	  } catch (InterruptedException ie) {
		throw new IOException("interrupted");
	  }

	byte[] buf = (byte[])queue.get(0);
	switch (buf.length) {
	case 0:
	  return -1;
	case 1:
	  queue.remove(0);
	  break;
	default:
	  byte[] nbuf = new byte[buf.length-1];
	  System.arraycopy(buf, 1, nbuf, 0, nbuf.length);
	  queue.set(0, nbuf); notifyAll(); // others could be waiting...
	}
	return buf[0] & 255;
  }

  public synchronized int read(byte[] b, int off, int len) throws IOException {
	if (queue == null) throw new IOException("closed");
	while (queue.isEmpty())
	  try {
		wait();
	  } catch (InterruptedException ie) {
		throw new IOException("interrupted");
	  }

	byte[] buf = (byte[])queue.get(0);
	if (buf.length == 0) return -1;

	if (buf.length <= len) {
	  System.arraycopy(buf, 0, b, off, buf.length);
	  queue.remove(0);
	  return buf.length;
	}
	
	System.arraycopy(buf, 0, b, off, len);
	byte[] nbuf = new byte[buf.length-len];
	System.arraycopy(buf, len, nbuf, 0, nbuf.length);
	queue.set(0, nbuf); notifyAll(); // others could be waiting...
	return len;
  }
  /** returns 0: cannot skip on a terminal.
    */
  public long skip (long len) {
    return 0;
  }
  /** this one ensures that you can only type at the end.
      This is executed within the event thread.
    */
  public void keyPressed (KeyEvent ke) {
    TextArea ta = (TextArea)ke.getComponent();
	int pos = ta.getText().length();
	ta.select(pos, pos);
	ta.setCaretPosition(pos);
  }
  
  // BUG: Rhapsody DR2 seems to not send some keys to keyTyped()
  //	e.g. German keyboard + is dropped, but numeric pad + is processed

  public void keyTyped (KeyEvent ke) {
    TextArea ta = (TextArea)ke.getComponent();
    char ch = ke.getKeyChar();

    switch (ch) {
      case '\n': case '\r':		// \n|\r -> \n, release line
		line.append('\n');
		break;

      case 'D'&31:			// ^D: release line
		ta.append("^D"); ta.setCaretPosition(ta.getText().length());
		break;

      case '\b':			// \b: erase char, if any
		int len = line.length();
		if (len > 0) line.setLength(len-1);
		return;

      case 'U'&31:			// ^U: erase line, if any
		line.setLength(0);
		ta.append("^U\n"); ta.setCaretPosition(ta.getText().length());
		return;

      default:
		line.append(ch);
		return;
    }
    synchronized (this) {
      queue.add(line.toString().getBytes());
      notifyAll(); // there could be several reading threads 
    }
    line.setLength(0);
  }

  public void keyReleased (KeyEvent ke) {
  }
}

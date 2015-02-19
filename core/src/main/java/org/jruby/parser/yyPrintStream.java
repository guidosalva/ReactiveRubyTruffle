package org.jruby.parser;
import java.io.FilterOutputStream;
import java.io.PrintStream;
/** used to reroute standard and diagnostic output, auto-flushes.
    All print methods delegate to the write-buffer method;
    subclass must implement the write and close methods
	to route the actual output.
  */
public abstract class yyPrintStream extends PrintStream {
  /** <tt>line.separator</tt> property.
    */
  protected static final String nl = System.getProperty("line.separator", "\n");
  /** layered on top of a {@link java.io.FilterOutputStream} which is
      itself layered on <tt>null</tt>. Any access would cause a <tt>NullPointerException</tt>.
	*/
  public yyPrintStream () {
    super(new FilterOutputStream(null), true);	// null results in NullPointerException...
  }
  public boolean checkError () { return false; } // fake
  public abstract void close ();
  public void flush() { } // nothing to do, avoid NullPointerException
  public void print (boolean b) { print(""+b); }
  public void print (char c) { print(""+(char)c); }
  public void print (char[] s) { print(s != null ? ""+s : ""+null); }
  public void print (double d) { print(""+d); }
  public void print (float f) { print(""+f); }
  public void print (int i) { print(""+i); }
  public void print (long l) { print(""+l); }
  public void print (Object obj) { print(""+obj); }
  public void print (String s) {
    byte[] buf = (s != null ? s : ""+null).getBytes();
    if (buf.length > 0) write(buf, 0, buf.length);
  }
  public void println () { print(nl); }
  public void println (boolean b) { print(""+b+nl); }
  public void println (char c) { print(""+(char)c+nl); }
  public void println (char[] s) { print(s != null ? s+nl : null+nl); }
  public void println (double d) { print(""+d+nl); }
  public void println (float f) { print(""+f+nl); }
  public void println (int i) { print(""+i+nl); }
  public void println (long l) { print(""+l+nl); }
  public void println (Object obj) { print(""+obj+nl); }
  public void println (String s) { print(s != null ? s+nl : null+nl); }
// inherited, not supported   public void setError ()
  public abstract void write (byte[] buf, int off, int len);
  public abstract void write (int b);
}

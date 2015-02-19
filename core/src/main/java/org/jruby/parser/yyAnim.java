package org.jruby.parser;
import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.io.PrintStream;
/** delegates messages to a {@link yyAnimPanel} and optionally intercepts standard i/o.
    This is not {@link java.io.Serializable}.

		<p>If the panel is to simulate standard input it must be instantiated
		before standard input is accessed. Otherwise the reader might be
		waiting for a different stream.

		@see System#setIn
  */
public class yyAnim extends Frame implements yyDebug {
  /** counts instances to exit on last close.
    */
  protected static int nFrames;
  { ++ nFrames;	// new instance
  }
  /** trap {@link System#in}.
    */
  public static final int IN = 1;
  /** trap {@link System#out}.
    */
  public static final int OUT = 2;
  /** input, stack, and comments.
    */
  protected yyAnimPanel panel;
  /** set by the checkbox listener.
    */
  protected Thread eventThread;
  /** breakpoint, only(!) set in GUI.
    */
  protected boolean outputBreak = false;
  /** creates and displays the frame.
	    @param io flags to trap standard input, and/or standard and diagnostic output.
    */
  public yyAnim (String title, int io) { this(System.class, title, io); }
  /** creates and displays the frame.
	    @param system hook to spoof {@link System}.
	    @param io flags to trap standard input, and/or standard and diagnostic output.
	*/
  public yyAnim (final Class system, String title, int io) {
    super(title);
	
		Font font = new Font("Monospaced", Font.PLAIN, 12);
		
		MenuBar mb = new MenuBar();
		Menu m = new Menu("yyAnim");
		MenuItem mi = new MenuItem("Quit");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent ae) {
			  try {
				  system.getMethod("exit", new Class[]{ int.class })
			      .invoke(null, new Object[]{ new Integer(0) });
				} catch (Exception e) { System.exit(0); }
			}
		});
		m.add(mi);
		mb.add(m);
		setMenuBar(mb);
		  
		add(panel = new yyAnimPanel(font), "Center");
		
		if ((io & (IN|OUT)) != 0) {
			Panel p = new Panel(new BorderLayout());
			switch (io) {
			case IN:
				p.add(new Label("terminal input"), "North");
				break;
			case OUT: case IN|OUT:
				Checkbox c;
				String ct = (io&IN) != 0 ? "terminal i/o" : "terminal output";
				p.add(c = new Checkbox(ct, outputBreak), "North");
				c.addItemListener(new ItemListener() {
				  public void itemStateChanged (ItemEvent ie) {
						eventThread = Thread.currentThread();
						outputBreak = ie.getStateChange() == ie.SELECTED;
				  }
				});
			}
			
			final TextArea t;
			p.add(t = new TextArea(10, 50), "Center");
			t.setBackground(Color.white); t.setFont(font);
			
			if ((io&IN) != 0) {
			  yyInputStream in = new yyInputStream();
			  t.addKeyListener(in); t.setEditable(true);
				try {
  			  system.getMethod("setIn", new Class[]{ InputStream.class })
	  		  	.invoke(null, new Object[]{ in });
				} catch (Exception e) { System.setIn(in); }
			}
			
			if ((io&OUT) != 0) {
			  PrintStream out = new yyPrintStream() {	// PrintStream into TextArea
					public void close () { }
					public void write (byte b[], int off, int len) {
						String s = new String(b, off, len);
						t.append(s); t.setCaretPosition(t.getText().length());
						if (outputBreak && s.indexOf("\n") >= 0 && eventThread != null
							  && Thread.currentThread() != eventThread)
						  try {
							  synchronized (panel) { panel.wait(); }
						  } catch (InterruptedException ie) { }
					}
					public void write (int b) {
						write(new byte[] { (byte)b }, 0, 1);
					}
				};
				try {
			    system.getMethod("setOut", new Class[]{ PrintStream.class })
				    .invoke(null, new Object[]{ out });
				} catch (Exception e) { System.setOut(out); }
				try {
  			  system.getMethod("setErr", new Class[]{ PrintStream.class })
	    			.invoke(null, new Object[]{ out });
				} catch (Exception e) { System.setErr(out); }
			}
			add(p, "South");
		}
		  
		addWindowListener(new WindowAdapter() {
			public void windowClosing (WindowEvent we) {
			  dispose();
			  if (-- nFrames <= 0)
				  try {
    				system.getMethod("exit", new Class[]{ int.class }).invoke(null, new Object[]{ new Integer(0) });
					} catch (Exception e) { System.exit(0); }
			}
		});
		pack();
		setStaggeredLocation(this);
		show();
  }
  /** try to cascade multiple instances of components.
    */
  public static void setStaggeredLocation (Component c) {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension d = c.getPreferredSize();
	
    int x = (screen.width - d.width)/2 + (nFrames-1)*32;
    if (x < 32) x = 32;
    else if (x > screen.width-128) x = screen.width-128;
	
    int y = (screen.height - d.height)/2 + (nFrames-1)*32;
    if (y < 32) y = 32;
    else if (y > screen.height-128) y = screen.height-128;
	
    c.setLocation(x, y);
  }
  
  public synchronized void lex (int state, int token, String name, Object value)
  { panel.lex(state, token, name, value);
  }
  
  public void shift (int from, int to, int errorFlag) {
    panel.shift(from, to, errorFlag);
  }
  
  public void discard (int state, int token, String name, Object value) {
    panel.discard(state, token, name, value);
  }
  
  public void shift (int from, int to) {
    panel.shift(from, to);
  }
  
  public synchronized void accept (Object value) {
    panel.accept(value);
  }
  
  public void error (String message) {
    panel.error(message);
  }
  
  public void reject () {
    panel.reject();
  }
  
  public synchronized void push (int state, Object value) {
    panel.push(state, value);
  }
  
  public synchronized void pop (int state) {
    panel.pop(state);
  }
  
  public synchronized void reduce (int from, int to, int rule, String text,
								   int len) {
    panel.reduce(from, to, rule, text, len);
  }
}

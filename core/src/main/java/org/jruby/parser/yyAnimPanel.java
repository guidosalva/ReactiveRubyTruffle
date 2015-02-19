package org.jruby.parser;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
/** animates messages with a graphical interface.
    This is not {@link java.io.Serializable}.
  */
public class yyAnimPanel extends Panel implements yyDebug {
  /** current token and value.
    */
  protected transient TextField token, value;
  /** running explanations.
    */
  protected transient TextArea comments;
  /** state/value stack.
    */
  protected transient Stack stack;
  /** breakpoints, set in GUI.
    */
  protected transient boolean tokenBreak = true, stackBreak, commentsBreak;

  public yyAnimPanel (Font font) {
    super(new BorderLayout());

    Button b;
    Checkbox c;
    Panel p, q;

    p = new Panel(new BorderLayout());
      p.add(c = new Checkbox("token ", tokenBreak), "West");
        c.addItemListener(new ItemListener() {
	  public void itemStateChanged (ItemEvent ie) {
	    tokenBreak = ie.getStateChange() == ie.SELECTED;
          }
        });
      q = new Panel(new BorderLayout());
        q.add(token = new TextField(12), "West");
          token.setEditable(false); token.setBackground(Color.white);
          token.setFont(font);
        q.add(value = new TextField(24), "Center");
          value.setEditable(false); value.setBackground(Color.white);
          value.setFont(font);
      p.add(q, "Center");
      p.add(b = new Button(" continue "), "East");
        b.addActionListener(new ActionListener() {
	  public void actionPerformed (ActionEvent ae) {
	    synchronized (yyAnimPanel.this) {
	      yyAnimPanel.this.notify();
	    }
          }
        });
    add(p, "North");

    p = new Panel(new BorderLayout());
      q = new Panel(new BorderLayout());
        q.add(c = new Checkbox("stack", stackBreak), "North");
          c.addItemListener(new ItemListener() {
	    public void itemStateChanged (ItemEvent ie) {
	      stackBreak = ie.getStateChange() == ie.SELECTED;
            }
          });
        q.add(stack = new Stack(font), "Center");
      p.add(q, "Center");
      q = new Panel(new BorderLayout());
        q.add(c = new Checkbox("comments", commentsBreak), "North");
          c.addItemListener(new ItemListener() {
	    public void itemStateChanged (ItemEvent ie) {
	      commentsBreak = ie.getStateChange() == ie.SELECTED;
            }
          });
        q.add(comments = new TextArea(10, 40), "Center");
          comments.setEditable(false); comments.setBackground(Color.white);
          comments.setFont(font);
      p.add(q, "East");
    add(p, "Center");
  }
  /** animates state/value stack.
    */
  protected final static class Stack extends ScrollPane {
    /** describes one level.
	  */
    protected static final GridBagConstraints level = new GridBagConstraints();
    static {
      level.anchor = level.NORTH;
      level.fill = level.HORIZONTAL;
      level.gridheight = 1; level.gridwidth = level.REMAINDER;
      level.gridx = 0; level.gridy = level.RELATIVE;
      level.weightx = 1.0;
    }
	/** font for the entries.
	  */
    protected final Font font;
	/** real display area.
	  */
    protected final Panel panel;

    public Stack (Font font) {
      super(SCROLLBARS_AS_NEEDED);
      this.font = font;
      setSize(50, 100);
      add(panel = new Panel(new GridBagLayout()));
    }

    public void push (int state, Object value) {
      Panel q = new Panel(new BorderLayout());
      TextField t;
      q.add(t = new TextField(""+state, 5), "West");
	t.setEditable(false); t.setBackground(Color.white); t.setFont(font);
      q.add(t = new TextField(value != null ? value.toString() : ""), "Center");
	t.setEditable(false); t.setBackground(Color.white); t.setFont(font);
      panel.add(q, level, 0); validate();
    }

    public void pop (int len) {
      for (int n = 0; n < len; ++ n) {
	panel.remove(0);
        validate();	// Rhapsody DR2 java crashes if this is outside loop
      }
    }

    public void pop () {
      panel.removeAll(); validate();
    }
  }
  /** post a comment.
    */
  protected synchronized void explain (String what) {
    if (comments.getText().length() > 0) comments.append("\n");
    comments.append(what);
    if (commentsBreak)
      try {
	wait();
      } catch (InterruptedException ie) { }
  }

  public synchronized void lex (int state, int token, String name, Object value)
  { this.token.setText(name);
    this.value.setText(value == null ? "" : value.toString());
    explain("read "+name);
    if (tokenBreak && !commentsBreak)
      try {
	wait();
      } catch (InterruptedException ie) { }
  }

  public void shift (int from, int to, int errorFlag) {
    switch (errorFlag) {
    default:				// normally
      explain("shift to "+to);
      break;
    case 0: case 1: case 2:		// in error recovery
      explain("shift to "+to+", "+errorFlag+" left to recover");
      break;
    case 3:				// normally
      explain("shift to "+to+" on error");
    }
  }

  public void discard (int state, int token, String name, Object value) {
    explain("discard token "+name+", value "+value);
  }

  public void shift (int from, int to) {
    explain("go to "+to);
  }

  public synchronized void accept (Object value) {
    explain("accept, value "+value);
    stack.pop();
    if (stackBreak)
      try {
	wait();
      } catch (InterruptedException ie) { }
  }

  public void error (String message) {
    explain("error message");
  }

  public void reject () {
    explain("reject");
    stack.pop();
    if (stackBreak)
      try {
	wait();
      } catch (InterruptedException ie) { }
  }

  public synchronized void push (int state, Object value) {
    stack.push(state, value);
    if (stackBreak)
      try {
	wait();
      } catch (InterruptedException ie) { }
  }

  public synchronized void pop (int state) {
    explain("pop "+state+" on error");
    stack.pop(1);
    if (stackBreak)
      try {
	wait();
      } catch (InterruptedException ie) { }
  }

  public synchronized void reduce (int from, int to, int rule, String text,
								int len) {
    explain("reduce ("+rule+"), uncover "+to+"\n("+rule+") "+ text);
    stack.pop(len);
    if (stackBreak)
      try {
	wait();
      } catch (InterruptedException ie) { }
  }
}

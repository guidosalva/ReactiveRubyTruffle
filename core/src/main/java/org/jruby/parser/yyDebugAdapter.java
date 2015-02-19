package org.jruby.parser;
import java.io.PrintStream;
/** writes one-line messages to standard output or a stream.
  */
public class yyDebugAdapter implements yyDebug {
  /** message stream.
    */
  protected final PrintStream out;

  public yyDebugAdapter (PrintStream out) {
    this.out = out;
  }

  public yyDebugAdapter () {
    this(System.out);
  }

  public void push (int state, Object value) {
    out.println("push\tstate "+state+"\tvalue "+value);
  }

  public void lex (int state, int token, String name, Object value) {
    out.println("lex\tstate "+state+"\treading "+name+"\tvalue "+value);
  }

  public void shift (int from, int to, int errorFlag) {
    switch (errorFlag) {
    default:				// normally
      out.println("shift\tfrom state "+from+" to "+to);
      break;
    case 0: case 1: case 2:		// in error recovery
      out.println("shift\tfrom state "+from+" to "+to
				+"\t"+errorFlag+" left to recover");
      break;
    case 3:				// normally
      out.println("shift\tfrom state "+from+" to "+to+"\ton error");
      break;
    }
  }

  public void pop (int state) {
    out.println("pop\tstate "+state+"\ton error");
  }

  public void discard (int state, int token, String name, Object value) {
    out.println("discard\tstate "+state+"\ttoken "+name+"\tvalue "+value);
  }

  public void reduce (int from, int to, int rule, String text, int len) {
    out.println("reduce\tstate "+from+"\tuncover "+to
						+"\trule ("+rule+") "+text);
  }

  public void shift (int from, int to) {
    out.println("goto\tfrom state "+from+" to "+to);
  }

  public void accept (Object value) {
    out.println("accept\tvalue "+value);
  }

  public void error (String message) {
    out.println("error\t"+message);
  }

  public void reject () {
    out.println("reject");
  }
}

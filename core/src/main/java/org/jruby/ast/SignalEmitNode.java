package org.jruby.ast;

import org.jruby.ast.visitor.NodeVisitor;
import org.jruby.lexer.yacc.ISourcePosition;

import java.util.List;

/**
 * Created by me on 25.02.15.
 */
public class SignalEmitNode extends Node {


    private final Node expr;

    public SignalEmitNode(ISourcePosition position, Node expr) {
        super(position, expr != null && expr.containsVariableAssignment);
        this.expr = expr;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visitSignalEmitNode(this);

    }

    @Override
    public List<Node> childNodes() {
        return Node.createList(expr);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.SIGNALEMITNODE;
    }
}

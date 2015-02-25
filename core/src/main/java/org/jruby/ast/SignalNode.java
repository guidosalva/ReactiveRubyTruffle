package org.jruby.ast;

import org.jruby.ast.visitor.NodeVisitor;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.parser.StaticScope;

import java.util.List;

/**
 * Created by me on 25.02.15.
 */
public class SignalNode extends Node{
    private final Node body;
    private final StaticScope scope;

    public SignalNode(ISourcePosition position, StaticScope scope, Node body) {
        super(position, body != null && body.containsVariableAssignment);
        this.body = body;
        this.scope = scope;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visitSignalNode(this);

    }

    @Override
    public List<Node> childNodes() {
        return Node.createList(body);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.SIGNALNODE;
    }
}

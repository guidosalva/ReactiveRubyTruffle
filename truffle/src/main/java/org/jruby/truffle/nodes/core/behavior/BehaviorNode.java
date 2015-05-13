package org.jruby.truffle.nodes.core.behavior;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.runtime.Visibility;
import org.jruby.truffle.nodes.core.CoreClass;
import org.jruby.truffle.nodes.core.CoreMethod;
import org.jruby.truffle.nodes.core.CoreMethodArrayArgumentsNode;
import org.jruby.truffle.nodes.core.behavior.init.InitFilter;
import org.jruby.truffle.nodes.core.behavior.init.InitFold;
import org.jruby.truffle.nodes.core.behavior.init.InitMap;
import org.jruby.truffle.nodes.core.behavior.init.InitMerge;
import org.jruby.truffle.nodes.core.behavior.propagation.BehaviorPropagationHeadNode;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.core.behavior.utility.DependencyStaticScope;
import org.jruby.truffle.nodes.objects.ReadInstanceVariableNode;
import org.jruby.truffle.nodes.objects.SelfNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.*;
import org.jruby.truffle.runtime.signalRuntime.BehaviorObject;

/**
* Created by me on 26.02.15.
*/

/*
    Some part of the Behavior class are implemented in behavior.rb
*/
@CoreClass(name = "Behavior")
public abstract class BehaviorNode {


    //TODO remove into a normal Node and create there a new BehaviorObject without the allocator. we may then can put most stuff final
    @CoreMethod(names = "initialize", needsBlock = true, argumentsAsArray = true)
    public abstract static class InitializeNode extends CoreMethodArrayArgumentsNode {
        @Child
        private WriteHeadObjectFieldNode writeSignalExpr;
        @Child
        HandleBehaviorExprInitializationNode execSignalExpr;

        public InitializeNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            writeSignalExpr = new WriteHeadObjectFieldNode(BehaviorOption.SIGNAL_EXPR);
            execSignalExpr = new HandleBehaviorExprInitializationNode(context, sourceSection);
        }

        @Specialization
        public BehaviorObject init(VirtualFrame frame, BehaviorObject self, Object[] dependsOn, RubyProc signalExp) {
            if (dependsOn[0] instanceof BehaviorObject) {
                self.setupPropagationDep(dependsOn);
                writeSignalExpr.execute(self, signalExp);
                execSignalExpr.execute(frame, self,dependsOn);
            } else {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                throw new RuntimeException("args need to be object array of signals");
            }
            return self;
        }
    }


    @CoreMethod(names = "propagation", required = 3, visibility = Visibility.PRIVATE)
    public abstract static class PropagationMethodNode extends CoreMethodArrayArgumentsNode {
        @Child
        BehaviorPropagationHeadNode propNode;

        public PropagationMethodNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            propNode = new BehaviorPropagationHeadNode(context, sourceSection);
        }

        @Specialization
        Object update(VirtualFrame frame, BehaviorObject self, long sourceId, BehaviorObject lastNode, boolean changed) {
            propNode.execute(frame, self, sourceId,lastNode, changed);
            return self;
        }




    }


    @CoreMethod(names = "value")
    public abstract static class ValueNode extends CoreMethodArrayArgumentsNode {

        @Child
        ReadInstanceVariableNode readValue;

        public ValueNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            readValue = new ReadInstanceVariableNode(context, sourceSection, BehaviorOption.VALUE_VAR, new SelfNode(context, sourceSection), false);
        }


        @Specialization(rewriteOn = UnexpectedResultException.class)
        int valueInt(VirtualFrame frame, BehaviorObject obj) throws UnexpectedResultException {
            int value = readValue.executeInteger(frame);
            return value;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        long valueLong(VirtualFrame frame, BehaviorObject obj) throws UnexpectedResultException {
            long value = readValue.executeLong(frame);
            return value;
        }


        @Specialization(rewriteOn = UnexpectedResultException.class)
        double valueDouble(VirtualFrame frame, BehaviorObject obj) throws UnexpectedResultException {
            double value = readValue.executeDouble(frame);
            return value;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        Object valueObject(VirtualFrame frame, BehaviorObject obj) throws UnexpectedResultException {
            // System.out.println("now object");
            Object value = readValue.execute(frame);
            return value;
        }

        static boolean isInt(BehaviorObject s) {
            return true;
        }

        static boolean isDouble(BehaviorObject s) {
            return false;
        }

        static boolean isNotPrimitve(BehaviorObject s) {
            return !isInt(s) && !isDouble(s);
        }

    }


    @CoreMethod(names = "fold", required = 1, needsBlock = true  )
    public abstract static class FoldNode extends CoreMethodArrayArgumentsNode {

        @Child
        WriteHeadObjectFieldNode writeFoldValue;
        @Child
        WriteHeadObjectFieldNode writeFoldFunction;
        @Child
        InitFold initFold;

        public FoldNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            writeFoldValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
            writeFoldFunction = new WriteHeadObjectFieldNode(BehaviorOption.SIGNAL_EXPR);
            initFold = new InitFold(context);
        }

        @Specialization
        public BehaviorObject fold(VirtualFrame frame, BehaviorObject self, int init, RubyProc proc){
            BehaviorObject newSignal = initFold.execute(frame,new BehaviorObject[]{self});
            writeFoldFunction.execute(newSignal,proc);
            writeFoldValue.execute(newSignal,init);
            return newSignal;
        }
        @Specialization
        public BehaviorObject fold(VirtualFrame frame,BehaviorObject self, long init, RubyProc proc){
            BehaviorObject newSignal = initFold.execute(frame, new BehaviorObject[]{self});
            writeFoldFunction.execute(newSignal,proc);
            writeFoldValue.execute(newSignal,init);
            return newSignal;
        }
        @Specialization
        public BehaviorObject fold(VirtualFrame frame,BehaviorObject self, double init, RubyProc proc){
            BehaviorObject newSignal = initFold.execute(frame, new BehaviorObject[]{self});
            writeFoldFunction.execute(newSignal,proc);
            writeFoldValue.execute(newSignal,init);
            return newSignal;
        }
        @Specialization
        public BehaviorObject fold(VirtualFrame frame,BehaviorObject self, Object init, RubyProc proc){
            BehaviorObject newSignal = initFold.execute(frame, new BehaviorObject[]{self});
            writeFoldFunction.execute(newSignal,proc);
            writeFoldValue.execute(newSignal,init);
            return newSignal;
        }
    }

    @CoreMethod(names = "foldN", argumentsAsArray = true, needsBlock = true  )
    public abstract static class FoldNNode extends CoreMethodArrayArgumentsNode {

        @Child
        WriteHeadObjectFieldNode writeFoldValue;
        @Child
        WriteHeadObjectFieldNode writeFoldFunction;
        @Child
        InitFold initFold;

        public FoldNNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            writeFoldValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
            writeFoldFunction = new WriteHeadObjectFieldNode(BehaviorOption.SIGNAL_EXPR);
            initFold = new InitFold(context);
        }

        @Specialization
        public BehaviorObject fold(VirtualFrame frame,BehaviorObject self, Object[] args, RubyProc proc){
            Object value = args[0];
            BehaviorObject[] deps = new BehaviorObject[args.length];
            deps[0] = self;
            for(int i = 1; i < args.length; i++){
                deps[i] = (BehaviorObject) args[i];
            }
            BehaviorObject newSignal = initFold.execute(frame, deps);
            writeFoldFunction.execute(newSignal,proc);
            writeFoldValue.execute(newSignal,value);
            return newSignal;
        }
    }
    @CoreMethod(names = "foldExpr", required = 1, needsBlock = true  )
    public abstract static class FoldExprNode extends CoreMethodArrayArgumentsNode {

        @Child
        WriteHeadObjectFieldNode writeFoldValue;
        @Child
        WriteHeadObjectFieldNode writeFoldFunction;
        @Child
        DependencyStaticScope extractDeps;
        @Child
        InitFold initFold;

        public FoldExprNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            writeFoldValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
            writeFoldFunction = new WriteHeadObjectFieldNode(BehaviorOption.SIGNAL_EXPR);
            extractDeps = new DependencyStaticScope();
            initFold = new InitFold(context);
        }

        @Specialization
        public BehaviorObject fold(VirtualFrame frame,BehaviorObject self, int value, RubyProc proc){
            BehaviorObject[] deps = extractDeps.execute(frame,proc);
            BehaviorObject newSignal = initFold.execute(frame, deps);
            writeFoldFunction.execute(newSignal,proc);
            writeFoldValue.execute(newSignal,value);
            return newSignal;
        }
        @Specialization
        public BehaviorObject fold(VirtualFrame frame,BehaviorObject self, double value, RubyProc proc){
            BehaviorObject[] deps = extractDeps.execute(frame,proc);
            BehaviorObject newSignal = initFold.execute(frame, deps);
            writeFoldFunction.execute(newSignal,proc);
            writeFoldValue.execute(newSignal,value);
            return newSignal;
        }
        @Specialization
        public BehaviorObject fold(VirtualFrame frame,BehaviorObject self, Object value, RubyProc proc){
            BehaviorObject[] deps = extractDeps.execute(frame,proc);
            BehaviorObject newSignal = initFold.execute(frame, deps);
            writeFoldFunction.execute(newSignal,proc);
            writeFoldValue.execute(newSignal,value);
            return newSignal;
        }
    }

    //add a block that get called every time the behavior changes
    //the way to add side effects
    @CoreMethod(names = "onChange",needsBlock = true)
    public abstract static class OnChangeNode extends CoreMethodArrayArgumentsNode {
        public OnChangeNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }


        @Specialization(guards = "isEmpty(obj)")
        BehaviorObject onChange(VirtualFrame frame, BehaviorObject obj, RubyProc block){
            RubyProc[] tmp = new RubyProc[1];
            tmp[0] = block;
            obj.setFunctionStore(tmp);
            obj.setFunctionStoreSize(1);
            return obj;
        }
        @Specialization(guards = "oneBlockStored(obj)")
        BehaviorObject onChangeOneBlockStored(VirtualFrame frame, BehaviorObject obj, RubyProc block){
            RubyProc[] tmp = (RubyProc[]) obj.getFunctionStore();
            RubyProc[] store = new RubyProc[2];
            store[0] = tmp[0];
            store[1] = block;
            obj.setFunctionStore(store);
            obj.setFunctionStoreSize(2);
            return obj;
        }

        @Specialization
        BehaviorObject onChangeArrayStore(VirtualFrame frame, BehaviorObject obj, RubyProc block){
            RubyProc[] tmp = (RubyProc[]) obj.getFunctionStore();
            RubyProc[] store = new RubyProc[tmp.length+1];
            System.arraycopy(tmp,0,store,0,tmp.length);
            store[store.length-1] = block;
            obj.setFunctionStore(store);
            obj.setFunctionStoreSize(store.length);
            return obj;
        }

        boolean isEmpty(BehaviorObject obj){
            return obj.getFunctionStoreSize() == 0;
        }
        boolean oneBlockStored(BehaviorObject obj){
            return  obj.getFunctionStoreSize() == 1;
        }

    }

    @CoreMethod(names = "remove", needsBlock = true, needsSelf = true)
    public abstract static class RemoveNode extends CoreMethodArrayArgumentsNode {
        public RemoveNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }



        @Specialization(guards = "isEmpty(obj)")
        BehaviorObject remove(VirtualFrame frame, BehaviorObject obj, RubyProc block){
            return obj;
        }
        @Specialization(guards = "oneBlockStored(obj)")
        BehaviorObject removeOneBlockStored(VirtualFrame frame, BehaviorObject obj, RubyProc block){
            RubyProc[] tmp = (RubyProc[]) obj.getFunctionStore();
            if(tmp[0].equals(block)){
                obj.setFunctionStore(null);
                obj.setFunctionStoreSize(0);
            }
            return obj;
        }

        @Specialization
        BehaviorObject removeArrayStore(VirtualFrame frame, BehaviorObject obj, RubyProc block){
            RubyProc[] tmp = (RubyProc[]) obj.getFunctionStore();
            for(int i = 0; i < tmp.length ;i++){
                if(tmp[i].equals(block)){
                    RubyProc[] store = new RubyProc[tmp.length-1];
                    System.arraycopy(tmp,0,store,0,i);
                    System.arraycopy(tmp,i+1,store,0,tmp.length-i-1);
                    obj.setFunctionStore(store);
                    obj.setFunctionStoreSize(store.length);
                }
            }
            return obj;
        }

        boolean isEmpty(BehaviorObject obj){
            return obj.getFunctionStoreSize() == 0;
        }
        boolean oneBlockStored(BehaviorObject obj){
            return  obj.getFunctionStoreSize() == 1;
        }
    }

    @CoreMethod(names = "filter", needsBlock = true, needsSelf = true, required = 1)
    public abstract static class FilterNode extends CoreMethodArrayArgumentsNode {


        @Child
        InitFilter initFilter ;

        public FilterNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            initFilter = new InitFilter(context,sourceSection);
        }

        @Specialization
        public BehaviorObject filter(VirtualFrame frame, BehaviorObject self, int initValue, RubyProc proc){
            return initFilter.execute(frame,self,initValue,proc);
        }
        @Specialization
        public BehaviorObject filter(VirtualFrame frame, BehaviorObject self, long initValue, RubyProc proc){
            return initFilter.execute(frame,self,initValue,proc);
        }
        @Specialization
        public BehaviorObject filter(VirtualFrame frame, BehaviorObject self, double initValue, RubyProc proc){
            return initFilter.execute(frame,self,initValue,proc);
        }
        @Specialization
        public BehaviorObject filter(VirtualFrame frame, BehaviorObject self, Object initValue, RubyProc proc){
            return initFilter.execute(frame,self,initValue,proc);
        }
    }

    /**
     * the left most signal is used for the initial value
     */
    @CoreMethod(names = "merge", needsBlock = true, argumentsAsArray = true, needsSelf = true)
    public abstract static class MergeNode extends CoreMethodArrayArgumentsNode {
        @Child
        InitMerge initMerge;
        public MergeNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            initMerge = new InitMerge(context);
        }


        @Specialization
        public BehaviorObject merge(VirtualFrame frame, BehaviorObject self, Object[] toMerge){
            return initMerge.execute(frame, objArrayWithSelfToArray(self,toMerge));
        }
    }

    @CoreMethod(names = "map", needsBlock = true,needsSelf = true)
    public abstract static class MapNode extends CoreMethodArrayArgumentsNode {
        @Child
        InitMap initMap ;

        public MapNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            initMap = new InitMap(context);
        }

        @Specialization
        public BehaviorObject map(VirtualFrame frame, BehaviorObject self, RubyProc proc){
            return initMap.execute(frame, self,proc);
        }



    }

    @CoreMethod(names = "take")
    public abstract static class TakeNode extends CoreMethodArrayArgumentsNode {

        public TakeNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }
    }

    @CoreMethod(names = "skip")
    public abstract  static class SkipNode extends CoreMethodArrayArgumentsNode {

        public SkipNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }
    }


    //TODO the following method could be a problem
    // i may want to skip this method
    @CoreMethod(names = "delay")
    public abstract static class DelayNode extends CoreMethodArrayArgumentsNode {

        public DelayNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }
    }

    @CoreMethod(names = "throttle")
    public abstract  static class ThrottleNode extends CoreMethodArrayArgumentsNode {
        public ThrottleNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }
    }

    @CoreMethod(names = "bufferWithTime")
    public abstract static class BufferWithTime extends CoreMethodArrayArgumentsNode{

        public BufferWithTime(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
        }
    }


    @CoreMethod(names = "now")
    public abstract static class NowNode extends CoreMethodArrayArgumentsNode {

        @Child
        ReadInstanceVariableNode readValue;

        public NowNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            readValue = new ReadInstanceVariableNode(context, sourceSection, BehaviorOption.VALUE_VAR, new SelfNode(context, sourceSection), false);
        }


        @Specialization(rewriteOn = UnexpectedResultException.class)
        int nowInt(VirtualFrame frame, BehaviorObject obj) throws UnexpectedResultException {
            int value = readValue.executeInteger(frame);
            return value;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        long nowLong(VirtualFrame frame, BehaviorObject obj) throws UnexpectedResultException {
            long value = readValue.executeLong(frame);
            return value;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        double nowDouble(VirtualFrame frame, BehaviorObject obj) throws UnexpectedResultException {
            double value = readValue.executeDouble(frame);
            return value;
        }

        @Specialization(rewriteOn = UnexpectedResultException.class)
        Object nowObject(VirtualFrame frame, BehaviorObject obj) throws UnexpectedResultException {
            Object value = readValue.execute(frame);
            return value;
        }

        static boolean isInt(BehaviorObject s) {
            return true;
        }

        static boolean isDouble(BehaviorObject s) {
            return false;
        }

        static boolean isNotPrimitve(BehaviorObject s) {
            return !isInt(s) && !isDouble(s);
        }

    }

    static BehaviorObject[] objArrayToBevArray(Object[] objs){
        BehaviorObject[] res = new BehaviorObject[objs.length];
        for(int i = 0; i < objs.length; i++){
            res[i] = (BehaviorObject) objs[i];
        }
        return res;
    }
    static BehaviorObject[] objArrayWithSelfToArray(BehaviorObject self,Object[] objs){
        BehaviorObject[] res = new BehaviorObject[objs.length+1];
        res[0] = self;
        for(int i = 0; i < objs.length; i++){
            res[i+1] = (BehaviorObject) objs[i];
        }
        return res;
    }
}
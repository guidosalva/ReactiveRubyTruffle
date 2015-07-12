package org.jruby.truffle.nodes.core.behavior;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.runtime.Visibility;
import org.jruby.truffle.nodes.core.CoreClass;
import org.jruby.truffle.nodes.core.CoreMethod;
import org.jruby.truffle.nodes.core.CoreMethodArrayArgumentsNode;
import org.jruby.truffle.nodes.core.behavior.init.*;
import org.jruby.truffle.nodes.core.behavior.propagation.BehaviorPropagationHeadNode;
import org.jruby.truffle.nodes.core.behavior.utility.BehaviorOption;
import org.jruby.truffle.nodes.objects.ReadInstanceVariableNode;
import org.jruby.truffle.nodes.objects.SelfNode;
import org.jruby.truffle.nodes.objectstorage.ReadHeadObjectFieldNode;
import org.jruby.truffle.nodes.objectstorage.WriteHeadObjectFieldNode;
import org.jruby.truffle.nodes.yield.YieldDispatchHeadNode;
import org.jruby.truffle.runtime.RubyContext;
import org.jruby.truffle.runtime.core.*;
import org.jruby.truffle.runtime.core.BehaviorObject;

/**
* Created by me on 26.02.15.
*/

/*
    Some part of the Behavior class are implemented in behavior.rb
*/
@CoreClass(name = "Behavior")
public abstract class BehaviorNode {


    @CoreMethod(names = "propagation", required = 3, visibility = Visibility.PRIVATE)
    public abstract static class PropagationMethodNode extends CoreMethodArrayArgumentsNode {
        @Child protected
        BehaviorPropagationHeadNode propNode;

        public PropagationMethodNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            propNode = new BehaviorPropagationHeadNode(context, sourceSection);
        }

        @Specialization
        Object update(VirtualFrame frame, BehaviorObject self, long sourceId, BehaviorObject lastNode, boolean changed) {
            propNode.execute(frame, self, sourceId, changed,lastNode);
            return self;
        }
    }


       //hack
    @CoreMethod(names = "propagation2", required = 3, visibility = Visibility.PRIVATE)
    public abstract static class PropagationMethod2Node extends CoreMethodArrayArgumentsNode {
        @Child protected
        BehaviorPropagationHeadNode propNode;

        public PropagationMethod2Node(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            propNode = new BehaviorPropagationHeadNode(context, sourceSection);
        }

        @Specialization
        Object update(VirtualFrame frame, BehaviorObject self, long sourceId, BehaviorObject lastNode, boolean changed) {
            propNode.execute(frame, self, sourceId, changed,lastNode);
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
        ReadHeadObjectFieldNode readValueLastNode;
        @Child
        YieldDispatchHeadNode dispatchNode;
        @Child
        InitFold initFold;

        public FoldNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            writeFoldValue = new WriteHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
            writeFoldFunction = new WriteHeadObjectFieldNode(BehaviorOption.SIGNAL_EXPR);
            readValueLastNode = new ReadHeadObjectFieldNode(BehaviorOption.VALUE_VAR);
            dispatchNode = new YieldDispatchHeadNode(context);
            initFold = new InitFold(context);
        }

        @Specialization
        public BehaviorObject fold(VirtualFrame frame, BehaviorObject self, int init, RubyProc proc){
            return initFold.execute(frame,new BehaviorObject[]{self},init,proc);
        }
        @Specialization
        public BehaviorObject fold(VirtualFrame frame,BehaviorObject self, long init, RubyProc proc){
            return initFold.execute(frame,new BehaviorObject[]{self},init,proc);
        }
        @Specialization
        public BehaviorObject fold(VirtualFrame frame,BehaviorObject self, double init, RubyProc proc){
            return initFold.execute(frame,new BehaviorObject[]{self},init,proc);
        }
        @Specialization
        public BehaviorObject fold(VirtualFrame frame,BehaviorObject self, Object init, RubyProc proc){
            return initFold.execute(frame,new BehaviorObject[]{self},init,proc);
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


    @CoreMethod(names = "map", needsBlock = true,needsSelf = true, argumentsAsArray = true)
    public abstract static class MapNNode extends CoreMethodArrayArgumentsNode {
        @Child
        InitMap initMap;
        @Child
        InitMapN initMapN;

        public MapNNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            initMap = new InitMap(context);
            initMapN = new InitMapN(context);
        }

        @Specialization
        public BehaviorObject map(VirtualFrame frame, BehaviorObject self,Object[] deps, RubyProc proc){
            if(deps.length == 0)
                return initMap.execute(frame, self,proc);
            else
                return initMapN.execute(frame,objArrayWithSelfToArray(self,deps),proc);
        }
    }

    @CoreMethod(names = "sampleOn", needsSelf = true, required = 1 )
    public abstract static class SampleOnNode extends CoreMethodArrayArgumentsNode {
        @Child
        InitSampleOn initSOn;

        public SampleOnNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            initSOn = new InitSampleOn(context);
        }

        @Specialization
        public BehaviorObject map(VirtualFrame frame, BehaviorObject self, BehaviorObject arg){
            return initSOn.execute(frame,self,arg);
        }
    }

    @CoreMethod(names = "take", required = 1,needsSelf = true
    )
    public abstract static class TakeNode extends CoreMethodArrayArgumentsNode {

        @Child
        InitTake initTake;


        public TakeNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            initTake = new InitTake(context);
        }

        @Specialization
        public BehaviorObject executeInt(VirtualFrame frame, BehaviorObject self, int value){
            return initTake.execute(frame, self, value);
        }
    }

    @CoreMethod(names = "skip",required = 2)
    public abstract  static class SkipNode extends CoreMethodArrayArgumentsNode {

        @Child
        InitSkip initSkip;

        public SkipNode(RubyContext context, SourceSection sourceSection) {
            super(context, sourceSection);
            initSkip = new InitSkip(context);
        }

        @Specialization
        public BehaviorObject executeInt(VirtualFrame frame, BehaviorObject self, int dValue, int value){
            return initSkip.execute(frame,self,dValue,value);
        }
        @Specialization
        public BehaviorObject executeInt(VirtualFrame frame, BehaviorObject self, long dValue, int value){
            return initSkip.execute(frame,self,dValue,value);
        }
        @Specialization
        public BehaviorObject executeInt(VirtualFrame frame, BehaviorObject self,double dValue, int value){
            return initSkip.execute(frame,self,dValue,value);
        }
        @Specialization
        public BehaviorObject executeInt(VirtualFrame frame, BehaviorObject self, Object dValue, int value){
            return initSkip.execute(frame,self,dValue,value);
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
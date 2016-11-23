require File.expand_path('../../spec_helper', __FILE__)

class AliasObject
  attr :foo
  attr_reader :bar
  attr_accessor :baz

  def prep; @foo = 3; @bar = 4; end
  def value; 5; end
  def false_value; 6; end
end

describe "The alias keyword" do
  before :each do
    @obj = AliasObject.new
    @meta = class << @obj;self;end
  end

  it "creates a new name for an existing method" do
    @meta.class_eval do
      alias __value value
    end
    @obj.__value.should == 5
  end

  it "adds the new method to the list of methods" do
    original_methods = @obj.methods
    @meta.class_eval do
      alias __value value
    end
    (@obj.methods - original_methods).map {|m| m.to_s }.should == ["__value"]
  end

  it "adds the new method to the list of public methods" do
    original_methods = @obj.public_methods
    @meta.class_eval do
      alias __value value
    end
    (@obj.public_methods - original_methods).map {|m| m.to_s }.should == ["__value"]
  end

  it "overwrites an existing method with the target name" do
    @meta.class_eval do
      alias false_value value
    end
    @obj.false_value.should == 5
  end

  it "is reversible" do
    @meta.class_eval do
      alias __value value
      alias value false_value
    end
    @obj.value.should == 6

    @meta.class_eval do
      alias value __value
    end
    @obj.value.should == 5
  end

  it "operates on the object's metaclass when used in instance_eval" do
    @obj.instance_eval do
      alias __value value
    end

    @obj.__value.should == 5
    lambda { AliasObject.new.__value }.should raise_error(NoMethodError)
  end

  it "operates on methods defined via attr, attr_reader, and attr_accessor" do
    @obj.prep
    @obj.instance_eval do
      alias afoo foo
      alias abar bar
      alias abaz baz
    end

    @obj.afoo.should == 3
    @obj.abar.should == 4
    @obj.baz = 5
    @obj.abaz.should == 5
  end

  it "operates on methods with splat arguments" do
    class AliasObject2;end
    AliasObject2.class_eval do
      def test(*args)
        4
      end
      def test_with_check(*args)
        test_without_check(*args)
      end
      alias test_without_check test
      alias test test_with_check
    end
    AliasObject2.new.test(1,2,3,4,5).should == 4
  end

  it "operates on methods with splat arguments on eigenclasses" do
    @meta.class_eval do
      def test(*args)
        4
      end
      def test_with_check(*args)
        test_without_check(*args)
      end
      alias test_without_check test
      alias test test_with_check
    end
    @obj.test(1,2,3,4,5).should == 4
  end

  it "operates on methods with splat arguments defined in a superclass" do
    class AliasObject3;end
    class Sub3 < AliasObject3;end
    AliasObject3.class_eval do
      def test(*args)
        4
      end
      def test_with_check(*args)
        test_without_check(*args)
      end
    end
    Sub3.class_eval do
      alias test_without_check test
      alias test test_with_check
    end
    Sub3.new.test(1,2,3,4,5).should == 4
  end

  it "operates on methods with splat arguments defined in a superclass using text block for class eval" do
    class Sub < AliasObject;end
    AliasObject.class_eval <<-code
      def test(*args)
        4
      end
      def test_with_check(*args)
        test_without_check(*args)
      end
      alias test_without_check test
      alias test test_with_check
    code
    Sub.new.test("testing").should == 4
  end

  it "is not allowed against Fixnum or String instances" do
    lambda do
      1.instance_eval do
        alias :foo :to_s
      end
    end.should raise_error(TypeError)

    lambda do
      :blah.instance_eval do
        alias :foo :to_s
      end
    end.should raise_error(TypeError)
  end
end
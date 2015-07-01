require_relative 'rtest'
require_relative 'lib/button'
require_relative 'lib/textfield'
require_relative 'lib/sink'
require_relative 'lib/check'


test do
    describe "Form: really simple check".bold

    t1 = Textfield.new("")
    t2 = Textfield.new("")
    b = Button.new

    sink = Sink.new("")

    check1 = t1.asBehavior.map{ |x| x.length > 3}
    check2 = t2.asBehavior.map{ |x| x.length > 3}

    allChecks = check1.map(check2) {|x,y| x && y}

    register = b.asBehavior.map(allChecks) { |click,check|
        if(check && click == :click)
            true
        else
            false
        end
    }

    sink.sampleOn(register,t1.asBehavior.map(t2.asBehavior) {|x,y| x + y})


    t1.change("Hans")
    b.click
    assert_equal("",sink.value)
    t2.change("Peter")
    b.click
    assert_equal("HansPeter",sink.value)
end

test do
    describe "Form: flapjex form validation".bold

    t1 = Textfield.new("")
    t2 = Textfield.new("")
    
    f1 = Check.new
    f2 = Check.new

    checkBoxes = [Check.new, Check.new, Check.new, Check.new]
    checkBoxBev = checkBoxes.map do |x|
        x.asBehavior.map{|x| 
            if(x == :yes)
                1
            else
                0
            end
            }
    end
   

    def sum(arr)
        cur = arr[0]
        for i in 1 .. arr.length - 1
            cur = cur.map(arr[i]) {|x,y| x + y}
        end
        return cur
    end
    numCheck = sum(checkBoxBev)


    check1 = t1.asBehavior.map{ |x| x.length >= 3}
    check2 = t2.asBehavior.map{ |x| x.length >= 4}

    isValidForm1 = check1.map(check2) {|x,y| x && y}
    isValidForm2 = numCheck.map {|x| x >= 3}


    valid = f1.asBehavior.map(f2.asBehavior,isValidForm1,isValidForm2) {
        |f1,f2, vf1,vf2|
        if( (f1 == :yes) && vf1)
            true
        elsif ((f2 == :yes) && vf2)
            true
        else
            false
        end
    }
    assert_equal(false, valid.now)

    f2.check
    checkBoxes[1].check
    checkBoxes[3].check
    checkBoxes[2].check
    assert_equal(true, valid.now)
    
    checkBoxes[1].unCheck


    assert_equal(false, valid.now)

    checkBoxes[0].check
    
    assert_equal(true, valid.now)
    f2.unCheck

    t1.change("Hans")
    t2.change("Pet")
    assert_equal(false, valid.now)
    f1.check
    t2.change("Peter")
    assert_equal(true, valid.now)


end
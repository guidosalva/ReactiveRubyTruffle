require_relative 'mouse'

extend BehaviorCore

mouse = Mouse.new


#lbl = Lable.new(0,0)


pos = mouse.asBehavior.map{ |mice| [mice.x, mice.y] }

mouseDown = mouse.asBehavior.map{ |mice| mice.status}


pos.onChange{ |x| puts x}



#for i in 1 .. 100
#	mouse.set_x(mouse.value.x + 1)
#	mouse.set_y(mouse.value.y + 0.3)
#end

    s1 = source(1)
    sb = source(true)

    sample = s1.sampleOn(sb)
    puts sample.now
    s1.emit(2)
    puts sample.now
    s1.emit(3)
    puts sample.now
    sb.emit(false)
    puts sample.now
    s1.emit(2)
    puts sample.now
    s1.emit(6)
    puts sample.now
    sb.emit(true)
    puts sample.now
    s1.emit(7)
    puts sample.now
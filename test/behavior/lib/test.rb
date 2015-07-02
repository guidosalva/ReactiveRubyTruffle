require_relative 'mouse'
require_relative 'label'

extend BehaviorCore

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



mouse = Mouse.new
lbl = Label.new(0,0)


#lbl = Lable.new(0,0)


pos = mouse.asBehavior.map{ |mice| [mice.x, mice.y] }

mouseDown = mouse.asBehavior.map{ |mice| mice.status == :down}

def trag(element, mouseDownB, posB)
	elUnderMouse = posB.map { |pos |
		puts "#{pos[0]} == #{element.get_x} && #{pos[1]} == #{element.get_y}"
		if( pos[0] == element.get_x && pos[1] == element.get_y)
			element
		else
			NullLable.instance
		end
	}

	sample = elUnderMouse.sampleOn(mouseDownB)
	sample.onChange{"change"}

	return sample.map(posB) { |el, pos| 
		puts "afte sample On #{[el,pos[0],pos[1]]}"
		[el,pos[0],pos[1]]  
	}
end


tragObj = trag(lbl,mouseDown, pos)


tragObj.onChange{ |x| puts x}


mouse.mouse_down
for i in 1 .. 10

	mouse.set_x(mouse.value.x + 1)
	mouse.set_y(mouse.value.y + 0.3)
end


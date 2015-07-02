require_relative 'mouse'
require_relative 'label'

extend BehaviorCore

mouse = Mouse.new
lbl = Label.new(0,0)



pos = mouse.asBehavior.map{ |mice| [mice.x, mice.y] }

mouseDown = mouse.asBehavior.map{ |mice| mice.status == :down}


def trag(element, mouseDownB, posB)
	elUnderMouse = posB.map(mouseDownB) { |pos, dw|
		if( dw && pos[0] == element.get_x && pos[1] == element.get_y)
			element
		else
			NullLable.instance
		end
	}

	sample = elUnderMouse.sampleOn(mouseDownB)

	return sample.map(posB) { |el, pos| 
		[el,pos[0],pos[1]]  
	}
end

tragObj = trag(lbl,mouseDown, pos)


tragObj.onChange{ |x| puts x.join(" , ")}


mouse.mouse_down
for i in 1 .. 15
	puts "i: #{i}"
	
	mouse.set_x(mouse.value.x + 1)
	mouse.set_y(mouse.value.y + 0.3)
	if(i == 5)
		mouse.mouse_up
	end
	if(i == 7)
		mouse.mouse_down
	end
	if(i == 9)
		mouse.mouse_up
	end

	if(i == 13)
		mouse.set_x(lbl.get_x)
		mouse.set_y(lbl.get_y)
		mouse.mouse_down
	end
end


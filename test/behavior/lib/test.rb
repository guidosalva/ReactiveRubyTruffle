require_relative 'mouse'
require_relative 'label'

extend BehaviorCore

mouse = Mouse.new
lbl = Label.new(0,0)


#lbl = Lable.new(0,0)


pos = mouse.asBehavior.map{ |mice| [mice.x, mice.y] }

mouseDown = mouse.asBehavior.map{ |mice| mice.status}

def trag(element, mouseDownB, posB)
	elUnderMouse = posB.map { |pos |
		puts "#{pos[0]} == #{element.get_x} && #{pos[1]} == #{element.get_y}"
		if( pos[0] == element.get_x && pos[1] == element.get_y)
			element
		else
			NullLable.instance
		end
	}
	return (elUnderMouse.sampleOn(mouseDownB)).map(posB) { |el, pos| [el,pos[0],pos[1]]  }
end


tragObj = trag(lbl,mouseDown, pos)


tragObj.onChange{ |x| puts x}


mouse.mouse_down
for i in 1 .. 10

	mouse.set_x(mouse.value.x + 1)
	mouse.set_y(mouse.value.y + 0.3)
end


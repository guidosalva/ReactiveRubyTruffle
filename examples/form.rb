##lib
def textfield(name)
	extend BehaviorCore
	return source("")
end
def getSelectableMailingList
	extend BehaviorCore
	return [source(false),source(false),source(false)]
end
def check(name)
	extend BehaviorCore
	return source(false)
end
def checkMail(x)
	x.length >= 3
end


def visualizeChecks(a,b,c)
	com = a.map(b,c) {|p1,p2,p3| [p1,p2,p3]}
	puts "#{com.now}"
	com.onChange {|x| puts "#{x}"}
	return com
end

#helper method
def atLestOneListSelctedB(lists)
	return lists.first.map(*lists.drop(1)) { |*args| 
		args.inject { |acc, x | acc || x }
	}
end

#input behaviors
mail = textfield("inputEMail")
selectableLists = getSelectableMailingList()
immediatUpdeas = check("imUpdate")
dailyUpdates = check("dayUpdate")


#checks 
checkEmail = mail.map {|m| checkMail(m)}
checkListSelected = atLestOneListSelctedB(selectableLists)
checkUpdate = immediatUpdeas.map(dailyUpdates) {| imUp, dayUp| imUp ^ dayUp}


#visualize
visualizeChecks(checkEmail,checkListSelected,checkUpdate)
mail.emit("as@asdf.de")
immediatUpdeas.emit(true)
selectableLists[1].emit(true)
mail.emit("as")
dailyUpdates.emit(true)
selectableLists[2].emit(true)
selectableLists[1].emit(false)
selectableLists[2].emit(false)
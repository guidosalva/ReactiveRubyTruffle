class BehaviorSource

    def initialize(value)
        @value = value
    end

    def emit(newValue)
		@value = newValue
		startPropagation()
	end

    def now
        @value
    end

    def value()
        @value
    end

end
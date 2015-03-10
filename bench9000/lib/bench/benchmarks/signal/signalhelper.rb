def signal(&init)
  Behavior.new &init
end
def signalSource( val )
  Behavior.new { val }
end
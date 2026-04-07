local moduleTable = alya.modules.register("Timer", "Change the tick speed client side", "PLAYER")
local timerSpeed = moduleTable.addNumberSetting("Speed", "Multiplier", 1.0, 0.1, 10.0, 0.1)
alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if event.isPre and event.isPre() then
        alya.mc.setTimerSpeed(timerSpeed.getValueAsFloat())
    end
end)
moduleTable.onDisable(function()
    alya.mc.setTimerSpeed(1.0)
end)

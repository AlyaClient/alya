local moduleTable = alya.modules.register("Blink", "Holds packets until disabled", "PLAYER")
local mode        = moduleTable.addModeSetting("Mode", "", "Manual", "Manual", "AutoRelease")
local delay       = moduleTable.addNumberSetting("Delay", "", 2000, 100, 10000, 100)
delay.setVisibility(function() return mode.is("AutoRelease") end)
local timer       = alya.timer.create()
moduleTable.onEnable(function()
    alya.mc.holdPackets(true)
    timer.reset()
end)
moduleTable.onDisable(function()
    alya.mc.holdPackets(false)
    alya.mc.flushPackets()
end)
alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end
    if not mode.is("AutoRelease") then return end
    if timer.hasElapsedAndReset(delay.getValueAsInt(), true) then
        alya.mc.holdPackets(false)
        alya.mc.flushPackets()
        alya.mc.holdPackets(true)
    end
end)

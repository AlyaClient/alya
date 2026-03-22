local moduleTable = alya.modules.register("Ambience", "Sets the time of day client side", "VISUAL")

local timeOfDay = moduleTable.addNumberSetting("Time", "Time of day", 18.0, 0.0, 24.0, 1)

local originalTime = 0

moduleTable.onEnable(function()
    if alya.mc.isPlayerNull() then return end
    originalTime = alya.mc.getWorldTime()
end)

moduleTable.onDisable(function()
    if alya.mc.isPlayerNull() then return end
    alya.mc.setWorldTime(originalTime)
end)

alya.events.on("timeupdate", function(event)
    if not moduleTable.isEnabled() then return end
    event.cancel()
    local mcTime = timeOfDay.getValueAsInt() * 1000
    alya.mc.setWorldTime(mcTime)
end)

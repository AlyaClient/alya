local moduleTable = alya.modules.register("NoRightClickDelay", "Removes right click delay", "PLAYER")

local delay = moduleTable.addNumberSetting("Delay", "Delay in ticks", 1, 0, 4, 1)

alya.events.on("update", function(event)
    if not moduleTable.isEnabled() then return end
    if alya.mc.getTicksExisted() % (delay.getValueAsInt() + 1) == 0 then
        alya.mc.setRightClickDelay(0)
    end
end)

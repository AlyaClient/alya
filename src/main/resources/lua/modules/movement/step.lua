local moduleTable = alya.modules.register("Step", "Step up blocks instantly", "MOVEMENT")
local height = moduleTable.addNumberSetting("Height", "", 1, 1, 50, 0.5)
alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end
    alya.mc.setStepHeight(height.getValue())
end)
moduleTable.onDisable(function()
    alya.mc.resetStepHeight()
end)

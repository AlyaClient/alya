local moduleTable = alya.modules.register("Jesus", "Walk on water", "MOVEMENT")

alya.events.on("update", function(event)
    alya.mc.setJesusActive(moduleTable.isEnabled())
end)

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end
    if not alya.mc.isInWater() and not alya.mc.isOnLiquid() then return end
    event.setOnGround(true)
end)

moduleTable.onDisable(function()
    alya.mc.setJesusActive(false)
end)

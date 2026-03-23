local moduleTable = alya.modules.register("Jesus", "Walk on water", "MOVEMENT")
alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end
    if not alya.mc.isInWater() and not alya.mc.isOnLiquid() then return end
    
    if not alya.mc.isJumpDown() then
        alya.mc.setMotionY(0.04)
    end
    
    event.setOnGround(true)
end)

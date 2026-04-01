local moduleTable = alya.modules.register("Terrain", "Move faster on terrain surfaces", "MOVEMENT")
local fastIce = moduleTable.addBooleanSetting("Fast-Ice", "Move faster on ice", true)
local fastLadder = moduleTable.addBooleanSetting("Fast-Ladder", "Climb ladders faster", true)

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end

    if fastIce.isEnabled() and alya.mc.isOnIce() then
        local motionX = alya.mc.getMotionX()
        local motionZ = alya.mc.getMotionZ()
        if math.abs(motionX) > 0.001 or math.abs(motionZ) > 0.001 then
            alya.mc.setMotionX(motionX * 1.06)
            alya.mc.setMotionZ(motionZ * 1.06)
        end
    end

    if fastLadder.isEnabled() and alya.mc.isOnLadder() then
        if alya.mc.isJumpPressed() then
            alya.mc.setMotionY(0.25)
        elseif alya.mc.isSneakPressed() then
            alya.mc.setMotionY(-0.2)
        elseif alya.movement.isMoving() then
            alya.mc.setMotionY(0.2)
        end
    end
end)

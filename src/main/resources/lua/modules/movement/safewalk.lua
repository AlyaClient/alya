local moduleTable = alya.modules.register("Safewalk", "Prevents walking off edges", "MOVEMENT")
local airSetting = moduleTable.addBooleanSetting("Air", "Hug walls when in air after jumping off a block", false)

alya.events.on("update", function(event)
    if not moduleTable.isEnabled() then return end
    if not alya.mc.isOnGround() then return end

    local playerX = alya.mc.getPlayerX()
    local playerY = alya.mc.getPlayerY()
    local playerZ = alya.mc.getPlayerZ()
    local motionX = alya.mc.getMotionX()
    local motionZ = alya.mc.getMotionZ()
    local floorY = math.floor(playerY)

    if math.abs(motionX) > 0.001 then
        if not alya.mc.isBlockSolid(math.floor(playerX + motionX), floorY - 1, math.floor(playerZ)) then
            alya.mc.setMotionX(0)
        end
    end

    if math.abs(motionZ) > 0.001 then
        if not alya.mc.isBlockSolid(math.floor(playerX), floorY - 1, math.floor(playerZ + motionZ)) then
            alya.mc.setMotionZ(0)
        end
    end
end)

alya.events.on("playerinput", function(event)
    if not moduleTable.isEnabled() then return end
    if not alya.mc.isOnGround() then return end

    local moveForward = alya.mc.getInputMoveForward()
    local moveStrafe = alya.mc.getInputMoveStrafe()
    if math.abs(moveForward) < 0.001 and math.abs(moveStrafe) < 0.001 then return end

    local playerX = alya.mc.getPlayerX()
    local playerY = alya.mc.getPlayerY()
    local playerZ = alya.mc.getPlayerZ()
    local yaw = math.rad(alya.mc.getYaw())
    local floorY = math.floor(playerY)

    local worldX = -math.sin(yaw) * moveForward + math.cos(yaw) * moveStrafe
    local worldZ =  math.cos(yaw) * moveForward + math.sin(yaw) * moveStrafe

    local solidUnderX = alya.mc.isBlockSolid(math.floor(playerX + worldX * 0.4), floorY - 1, math.floor(playerZ))
    local solidUnderZ = alya.mc.isBlockSolid(math.floor(playerX), floorY - 1, math.floor(playerZ + worldZ * 0.4))

    if not solidUnderX or not solidUnderZ then
        alya.mc.setInputMoveForward(0)
        alya.mc.setInputMoveStrafe(0)
    end
end)

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end
    if alya.mc.isOnGround() then return end
    if not airSetting.isEnabled() then return end

    local motionX = alya.mc.getMotionX()
    local motionZ = alya.mc.getMotionZ()
    local playerX = alya.mc.getPlayerX()
    local playerY = alya.mc.getPlayerY()
    local playerZ = alya.mc.getPlayerZ()
    local floorY = math.floor(playerY)

    if math.abs(motionX) > 0.001 then
        local checkX = math.floor(playerX + motionX + (motionX > 0 and 0.3 or -0.3))
        if alya.mc.isBlockSolid(checkX, floorY, math.floor(playerZ)) or
           alya.mc.isBlockSolid(checkX, floorY + 1, math.floor(playerZ)) then
            alya.mc.setMotionX(0)
        end
    end

    if math.abs(motionZ) > 0.001 then
        local checkZ = math.floor(playerZ + motionZ + (motionZ > 0 and 0.3 or -0.3))
        if alya.mc.isBlockSolid(math.floor(playerX), floorY, checkZ) or
           alya.mc.isBlockSolid(math.floor(playerX), floorY + 1, checkZ) then
            alya.mc.setMotionZ(0)
        end
    end
end)

moduleTable.onDisable(function()
    alya.mc.setSneakPressed(false)
end)

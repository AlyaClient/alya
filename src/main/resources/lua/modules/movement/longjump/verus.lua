local function createVerusMode(moduleTable, mode, verusType)
    local timer = alya.timer.create()

    local function findFireballSlot()
        for i = 0, 8 do
            local name = alya.combat.getHotbarItemName(i)
            if name == "item.fireball" or name == "item.firecharge" then
                return i
            end
        end
        return -1
    end

    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not mode.is("Verus") then return end
        if not event.isPre() then return end

        if alya.mc.isOnGround() then
            alya.combat.setPlayerPitch(90)
        end

        if timer.hasElapsedAndReset(10, true) then
            local slot = findFireballSlot()
            if slot ~= -1 then
                alya.combat.setHotbarSlot(slot)
                alya.combat.useHeldItem()
                alya.combat.setPlayerPitch(0)
            end
        end

        if alya.combat.getHurtTime() > 0 then
            alya.mc.setCameraYaw(0)
            alya.combat.setCameraPitch(0)
            alya.combat.setPlayerPitch(0)

            if verusType.is("Fast") then
                alya.mc.setMotionY(0.4)
                alya.movement.setSpeed(9)
                alya.combat.setMoveForward(10)
            elseif verusType.is("High") then
                alya.combat.setPlayerHurtTime(20)
                alya.mc.setMotionY(5)
                alya.mc.setTimerSpeed(10)
                alya.movement.setSpeed(4)
                moduleTable.toggle()
            end
        end
    end)
end

return createVerusMode

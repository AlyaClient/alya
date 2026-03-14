local function createVulcanMode(moduleTable, mode)
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
        if not mode.is("Vulcan") then return end
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

        if alya.combat.getHurtTime() > 8 then
            alya.mc.setTimerSpeed(0.1)
            alya.mc.setMotionY(4)
            alya.movement.setSpeed(2.6)
            moduleTable.toggle()
        end
    end)
end

return createVulcanMode

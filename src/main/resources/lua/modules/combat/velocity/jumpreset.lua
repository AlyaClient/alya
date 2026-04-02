local function createJumpResetVelocityMode(moduleTable, modeSetting, mode, chance, miss)
    local needsJump = false

    alya.events.on("packetreceive", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSetting.is(mode) then return end
        local cls = event.getPacketClass()
        if cls ~= "S12PacketEntityVelocity" then return end
        if event.getEntityId() ~= alya.mc.getEntityId() then return end
        if not alya.mc.isOnGround() then return end

        if math.random() * 100 < chance.getValue() then
            needsJump = true
        end
    end)

    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSetting.is(mode) then return end
        if not event.isPre() then return end
        if not needsJump then return end
        needsJump = false

        if alya.mc.isOnGround() then
            if math.random() * 100 < miss.getValue() then
                alya.mc.jump()
            else
                alya.mc.jump()
            end
        end
    end)
end
return createJumpResetVelocityMode

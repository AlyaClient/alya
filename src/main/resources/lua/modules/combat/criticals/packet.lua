local function createPacketMode(moduleTable, mode, delay)
    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not mode.is("Packet") then return end
        if not event.isPre() then return end
        if not alya.mc.isOnGround() then return end
        if alya.combat.getHurtTime() <= delay.getValueAsInt() then return end
        for _, offset in ipairs({ 0.006253453, 0.002253453, 0.001253453 }) do
            alya.combat.sendPositionPacket(offset, false)
        end
    end)
end
return createPacketMode

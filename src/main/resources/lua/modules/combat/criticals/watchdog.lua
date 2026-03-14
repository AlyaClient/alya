local function createWatchdogMode(moduleTable, mode)
    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not mode.is("Watchdog") then return end
        if not event.isPre() then return end
        if not alya.mc.isOnGround() then return end

        for _, offset in ipairs({ 0.06, 0.01 }) do
            alya.combat.sendPositionPacket(offset + math.random() * 0.001, false)
        end
    end)
end

return createWatchdogMode

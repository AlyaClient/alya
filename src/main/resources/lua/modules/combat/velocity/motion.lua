local function createMotionVelocityMode(moduleTable, modeSettings, mode, hVel, vVel)
    alya.events.on("packetreceive", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is(mode) then return end

        local cls = event.getPacketClass()

        if cls == "S12PacketEntityVelocity" then
            if event.getEntityId() ~= alya.mc.getEntityId() then return end
            local h = hVel.getValue()
            local v = vVel.getValue()
            if h == 0 and v == 0 then
                event.cancel()
                return
            end
            event.setMotionX(math.floor(event.getMotionX() * (h / 100)))
            event.setMotionY(math.floor(event.getMotionY() * (v / 100)))
            event.setMotionZ(math.floor(event.getMotionZ() * (h / 100)))
        elseif cls == "S27PacketExplosion" then
            local h = hVel.getValue()
            local v = vVel.getValue()
            if h == 0 and v == 0 then
                event.cancel()
                return
            end
            event.setMotionX(event.getMotionX() * (h / 100))
            event.setMotionY(event.getMotionY() * (v / 100))
            event.setMotionZ(event.getMotionZ() * (h / 100))
        end
    end)
end

return createMotionVelocityMode

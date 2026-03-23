local function createOmniSprintMode(moduleTable, modeSettings)
    alya.events.on("packetsend", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("OmniSprint") then return end
        if event.getPacketClass() == "C0BPacketEntityAction" then
            event.cancel()
        end
    end)
end
return createOmniSprintMode

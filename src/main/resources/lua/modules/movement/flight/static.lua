local function createStaticFlightMode(moduleTable, modeSettings)
    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("Static") then return end
        alya.mc.setMotionY(0)
        alya.movement.setSpeed(0)
    end)
end
return createStaticFlightMode

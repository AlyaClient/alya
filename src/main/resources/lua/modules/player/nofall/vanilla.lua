local function createVanillaNoFallMode(moduleTable, modeSettings)
    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("Vanilla") then return end
        if alya.mc.getFallDistance() < 3.01 then
            event.setOnGround(true)
            alya.mc.setFallDistance(0)
        end
    end)
end
return createVanillaNoFallMode

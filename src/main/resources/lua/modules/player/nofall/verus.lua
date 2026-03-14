local function createVerusNoFallMode(moduleTable, modeSettings)
    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("Verus") then return end

        if alya.mc.getFallDistance() > 3.5 then
            alya.mc.setMotionY(0.0)
            event.setOnGround(true)
            alya.mc.setFallDistance(0)
        end
    end)
end

return createVerusNoFallMode

local function createVanillaSpeedMode(moduleTable, modeSettings)
    local speedAmount = moduleTable.addNumberSetting("Speed", "How fast", 0.6, 0.1, 10.0)
    local autoJump = moduleTable.addBooleanSetting("Auto Jump", "Automatically jump", true)
    local strafe = moduleTable.addBooleanSetting("Strafe", "Move mid-air", true)

    speedAmount.setVisibility(function() return modeSettings.is("Vanilla") end)
    autoJump.setVisibility(function() return modeSettings.is("Vanilla") end)
    strafe.setVisibility(function() return modeSettings.is("Vanilla") end)

    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("Vanilla") then return end
        if not alya.movement.isMoving() then return end

        if autoJump.isEnabled() and alya.mc.isOnGround() then
            alya.mc.jump()
        end

        local strafePercentage = strafe.isEnabled() and 1 or 0
        alya.movement.setSpeedStrafe(speedAmount.getValue(), strafePercentage)
    end)
end

return createVanillaSpeedMode

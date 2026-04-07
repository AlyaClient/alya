local function createBHopSpeedMode(moduleTable, modeSettings)
    local strafe = moduleTable.addBooleanSetting("Strafe", "Strafe while jumping", true)
    local omniSprint = moduleTable.addBooleanSetting("OmniSprint", "Sprint constantly in all directions", true)
    strafe.setVisibility(function() return modeSettings.is("BHop") end)
    omniSprint.setVisibility(function() return modeSettings.is("BHop") end)
    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("BHop") then return end
        if not alya.movement.isMoving() then return end
        if alya.mc.isOnGround() then
            alya.mc.jump()
        end
        local strafePercentage = strafe.isEnabled() and 1 or 0
        alya.mc.setSprinting(true)
        if omniSprint.isEnabled() then
            alya.movement.setSpeedStrafe(0.377, strafePercentage)
        else
            if alya.mc.isForwardDown() then
                alya.movement.setSpeedStrafe(0.377, strafePercentage)
            else
                alya.movement.setSpeedStrafe(0.32, strafePercentage)
            end
        end
    end)
    alya.events.on("playerinput", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("BHop") then return end
        if alya.mc.isJumpDown() then
            event.cancel()
        end
    end)
end
return createBHopSpeedMode

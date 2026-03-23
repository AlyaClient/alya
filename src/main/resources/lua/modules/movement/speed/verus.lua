local function createVerusSpeedMode(moduleTable, modeSettings)
    local strafe = moduleTable.addBooleanSetting("Strafe", "Strafe while jumping", true)
    local omniSprint = moduleTable.addBooleanSetting("OmniSprint", "Sprint constantly in all directions", true)
    local boost = moduleTable.addNumberSetting("Hit Boost", "Multiplier to boost by on damage", 1.0, 1.0, 5.0, 0.1)
    strafe.setVisibility(function() return modeSettings.is("Verus") end)
    omniSprint.setVisibility(function() return modeSettings.is("Verus") end)
    boost.setVisibility(function() return modeSettings.is("Verus") end)
    alya.events.on("motion", function(event)
        local boostAmount = 1.0
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("Verus") then return end
        if not alya.movement.isMoving() then return end
        if alya.mc.isOnGround() then
            alya.mc.jump()
        end
        local boostEnabled = boost.getValue() > 1.0
        local hurt = alya.mc.getHurtTime() > 4
        if boostEnabled and hurt then
            boostAmount = boost.getValue()
        end
        local strafePercentage = strafe.isEnabled() and 1 or 0
        alya.mc.setSprinting(true)
        if omniSprint.isEnabled() then
            alya.movement.setSpeedStrafe(0.33 * boostAmount, strafePercentage)
        else
            if alya.mc.isForwardDown() then
                alya.movement.setSpeedStrafe(0.33 * boostAmount, strafePercentage)
            else
                alya.movement.setSpeedStrafe(0.32, strafePercentage)
            end
        end
    end)
    alya.events.on("playerinput", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("Verus") then return end
        if alya.mc.isJumpDown() then
            event.cancel()
        end
    end)
end
return createVerusSpeedMode

local function createNcpSpeedMode(moduleTable, modeSettings)
    local strafe = moduleTable.addBooleanSetting("Strafe", "Strafe while jumping", true)
    local boost = moduleTable.addNumberSetting("Hit Boost", "Multiplier to boost by on damage", 1.0, 1.0, 2.0, 0.1)
    strafe.setVisibility(function() return modeSettings.is("NCP") end)
    boost.setVisibility(function() return modeSettings.is("NCP") end)

    local hasBoosted = false

    alya.events.on("motion", function(event)
        local boostAmount = 1.0
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("NCP") then return end
        if not alya.movement.isMoving() then return end
        if alya.mc.isOnGround() then
            alya.mc.jump()
        end
        local boostEnabled = boost.getValue() > 1.0
        local hurt = alya.mc.getHurtTime() > 0

        if boostEnabled and hurt and not hasBoosted then
            boostAmount = boost.getValue()
            hasBoosted = true
        elseif not hurt then
            hasBoosted = false
        end

        local strafePercentage = strafe.isEnabled() and 0.2 or 0
        local currentSpeed = alya.movement.getSpeed()

        if alya.mc.isForwardDown() then
			alya.mc.setSprinting(true)
			alya.movement.setSpeedStrafe(currentSpeed * boostAmount, strafePercentage)
        else
			alya.mc.setSprinting(false)
        end
    end)

    alya.events.on("playerinput", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("NCP") then return end
        if alya.mc.isJumpDown() then
            event.cancel()
        end
    end)
end
return createNcpSpeedMode

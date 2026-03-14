local function createMotionFlightMode(moduleTable, modeSettings, speedSetting)
    local glide = moduleTable.addBooleanSetting("Glide", "Slowly fall", false)
    local smooth = moduleTable.addBooleanSetting("Smooth", "Smooth movement", true)

    glide.setVisibility(function() return modeSettings.is("Motion") end)
    smooth.setVisibility(function() return modeSettings.is("Motion") end)

    alya.events.on("playermove", function(event)
        if not moduleTable.isEnabled() then return end
        if not modeSettings.is("Motion") then return end

        if not smooth.isEnabled() then
            alya.mc.setCameraYaw(0.1)
        end

        if alya.mc.isJumpPressed() then
            alya.mc.setMotionY(alya.mc.getMotionY() + speedSetting.getValue() / 4.0)
        elseif alya.mc.isSneakPressed() then
            alya.mc.setMotionY(alya.mc.getMotionY() - speedSetting.getValue() / 4.0)
        else
            alya.mc.setMotionY(glide.isEnabled() and -0.05 or 0)
        end

        alya.movement.setSpeed(speedSetting.getValue())
    end)
end

return createMotionFlightMode

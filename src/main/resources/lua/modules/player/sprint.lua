local moduleTable = alya.modules.register("Sprint", "Auto-Sprint", "PLAYER")

local omniSprint = moduleTable.addBooleanSetting("OmniSprint", "Sprint in all directions", false)

alya.events.on("playerinput", function(event)
    if not moduleTable.isEnabled() then return end
    if not alya.movement.isMoving() then return end

    if omniSprint.isEnabled() then
        alya.mc.setSprinting(true)
    else
        if alya.mc.isForwardPressed() and not alya.mc.isBackPressed() then
            alya.mc.setSprinting(true)
        end
    end
end)

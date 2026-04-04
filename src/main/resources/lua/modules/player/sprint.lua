local moduleTable = alya.modules.register("Sprint", "Auto-Sprint", "PLAYER")
local omniSprint = moduleTable.addBooleanSetting("OmniSprint", "Sprint in all directions", false)

alya.events.on("playerinput", function(event)
    if not moduleTable.isEnabled() then return end
    if not alya.movement.isMoving() then return end

    local scaffoldModule = alya.modules.get("Scaffold")
    if scaffoldModule and scaffoldModule.isEnabled() then
        local scaffoldSprint = scaffoldModule.getSetting("Sprint")
        if scaffoldSprint and not scaffoldSprint.isEnabled() then
            alya.mc.setSprinting(false)
            return
        end
    end

    if omniSprint.isEnabled() then
        alya.mc.setSprinting(true)
    else
        local forwardKey = alya.mc.getKeyCode("forward")
        local backwardKey = alya.mc.getKeyCode("back")
        if alya.mc.isKeyDown(forwardKey) and not alya.mc.isKeyDown(backwardKey) then
            alya.mc.setSprinting(true)
        else
            alya.mc.setSprinting(false)
        end
    end
end)
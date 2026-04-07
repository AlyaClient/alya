local function createGrimMode(moduleTable, mode)
    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not mode.is("Grim") then return end
        if not event.isPre() then return end
        alya.mc.setMotionY(0.4)
        alya.mc.setTimerSpeed(50)
        alya.movement.setSpeed(9)
        moduleTable.toggle()
    end)
end
return createGrimMode

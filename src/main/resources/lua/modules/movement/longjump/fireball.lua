local function createFireballMode(moduleTable, mode)
    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not mode.is("Fireball") then return end
        if not event.isPre() then return end
        if alya.combat.getHurtTime() < 7 then return end
        alya.movement.setSpeed(1)
        alya.mc.setMotionY(0.42)
        moduleTable.toggle()
    end)
end
return createFireballMode

local function createNcpMode(moduleTable, mode)
    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not mode.is("NCP") then return end
        if not event.isPre() then return end
        if alya.combat.getHurtTime() <= 0 then return end
        alya.mc.setMotionY(0.5)
        moduleTable.toggle()
    end)
end
return createNcpMode

local function createMinelandMode(moduleTable, mode)
    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not mode.is("Mineland") then return end
        if not event.isPre() then return end

        alya.movement.setSpeed(0.1)
        moduleTable.toggle()
    end)
end

return createMinelandMode

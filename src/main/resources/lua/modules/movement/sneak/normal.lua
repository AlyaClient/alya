local function createNormalMode(moduleTable, mode)
    alya.events.on("update", function()
        if not moduleTable.isEnabled() then return end
        if not mode.is("Normal") then return end
        alya.mc.setSneakPressed(true)
    end)
end


return createNormalMode

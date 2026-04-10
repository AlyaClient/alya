local function createTwerkMode(moduleTable, mode, timer)
    alya.events.on("update", function()
        if not moduleTable.isEnabled() then return end
        if not mode.is("Twerk") then return end
        if timer.hasElapsedAndReset(500, true) then
            alya.mc.setSneakPressed(false)
        else
            alya.mc.setSneakPressed(true)
        end
    end)
end

return createTwerkMode

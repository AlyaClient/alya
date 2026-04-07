local moduleTable = alya.modules.register("Scoreboard", "Move the scoreboard vertically", "VISUAL")
local yOffset = moduleTable.addNumberSetting("Y Offset", "Vertical offset in pixels (negative moves up)", 0, -500, 500, 1)

alya.events.on("update", function(event)
    if not moduleTable.isEnabled() then
        alya.mc.resetScoreboardYOffset()
        return
    end
    alya.mc.setScoreboardYOffset(math.floor(yOffset.getValue()))
end)

moduleTable.onDisable(function()
    alya.mc.resetScoreboardYOffset()
end)

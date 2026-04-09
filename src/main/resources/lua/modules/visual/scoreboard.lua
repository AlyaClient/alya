local moduleTable = alya.modules.register("Scoreboard", "Move the scoreboard vertically", "VISUAL")
local yOffset = moduleTable.addNumberSetting("Y Offset", "Vertical offset in pixels (negative moves up)", 0, -500, 500, 1)

alya.events.on("render2d", function(event)
    if moduleTable.isEnabled() then
        alya.mc.setScoreboardYOffset(yOffset.getValue())
    end
end)

moduleTable.onDisable(function()
    alya.mc.resetScoreboardYOffset()
end)

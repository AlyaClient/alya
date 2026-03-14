local moduleTable = alya.modules.register("NoJumpDelay", "Removes delay between jumps", "PLAYER")

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if alya.mc.isOnGround() then
        alya.mc.setJumpTicks(0)
    end
end)

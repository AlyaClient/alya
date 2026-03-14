local moduleTable = alya.modules.register("HighJump", "Jump high", "MOVEMENT")

local amount = moduleTable.addNumberSetting("Speed", "How fast to jump", 1.0, 0.1, 10.0, 0.1)

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not alya.mc.isOnGround() then return end
    if not alya.mc.isJumpPressed() then return end
    alya.mc.jump()
    alya.mc.setJumpTicks(10)
    alya.mc.setMotionY(amount.getValue())
end)

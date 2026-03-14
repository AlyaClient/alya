local moduleTable = alya.modules.register("LegitScaffold", "legit scaffold in 3 lines cus thoq is doesnt believe me", "PLAYER")

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end
    if not alya.mc.isKeyDown(44) then return end

    if alya.mc.isJumpDown() then
        alya.mc.setSneakPressed(true)
    else
        local shouldSneak = alya.mc.isAboveVoid()
            and alya.mc.isOnGround()
            and not alya.mc.isOnLadder()
        alya.mc.setSneakPressed(shouldSneak)
    end
end)

moduleTable.onDisable(function()
    alya.mc.setSneakPressed(false)
end)

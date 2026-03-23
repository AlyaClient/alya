local moduleTable = alya.modules.register("KeepSprint", "Keeps you sprinting after hitting a player", "COMBAT")
alya.events.on("packetsend", function(event)
    if not moduleTable.isEnabled() then return end
    if event.getPacketClass() ~= "C0BPacketEntityAction" then return end
    if event.getEntityAction() == "STOP_SPRINTING" then
        event.cancel()
    end
end)

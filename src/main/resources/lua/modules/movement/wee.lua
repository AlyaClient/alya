local moduleTable = alya.modules.register("Wee", "airplane", "MOVEMENT")
local mode = moduleTable.addModeSetting("Mode", "Wee mode", "Slime", "Slime")
local amount = moduleTable.addNumberSetting("Amount", "Speed to go wee", 1.0, 0.1, 10.0, 0.1)
alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not mode.is("Slime") then return end
    alya.mc.setMotionY(amount.getValue())
end)

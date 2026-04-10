local moduleTable = alya.modules.register("Sneak", "Makes you sneak", "MOVEMENT")
local mode = moduleTable.addModeSetting("Mode", "Sneak mode", "Normal", "Normal", "Twerk")
local createNormalMode = loadScript("/lua/modules/movement/sneak/normal.lua")
local createTwerkMode = loadScript("/lua/modules/movement/sneak/twerk.lua")

local twerkTimer = alya.timer.create()

createNormalMode(moduleTable, mode)
createTwerkMode(moduleTable, mode, twerkTimer)

moduleTable.onEnable(function()
    twerkTimer.reset()
end)

moduleTable.onDisable(function()
    alya.mc.setSneakPressed(false)
end)

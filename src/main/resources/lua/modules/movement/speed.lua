local moduleTable = alya.modules.register("Speed", "F1 car", "MOVEMENT")

local mode = moduleTable.addModeSetting("Mode", "Speed mode", "BHop", "BHop", "Vanilla")

local createBHopSpeedMode = loadScript("/lua/modules/movement/speed/bhop.lua")
local createVanillaSpeedMode = loadScript("/lua/modules/movement/speed/vanilla.lua")

createBHopSpeedMode(moduleTable, mode)
createVanillaSpeedMode(moduleTable, mode)

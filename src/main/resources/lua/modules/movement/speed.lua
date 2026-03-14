local moduleTable = alya.modules.register("Speed", "F1 car", "MOVEMENT")

local mode = moduleTable.addModeSetting("Mode", "Speed mode", "BHop", "BHop", "Vanilla", "Verus")

local createBHopSpeedMode = loadScript("/lua/modules/movement/speed/bhop.lua")
local createVanillaSpeedMode = loadScript("/lua/modules/movement/speed/vanilla.lua")
local createVerusSpeedMode = loadScript("/lua/modules/movement/speed/verus.lua")

createBHopSpeedMode(moduleTable, mode)
createVanillaSpeedMode(moduleTable, mode)
createVerusSpeedMode(moduleTable, mode)

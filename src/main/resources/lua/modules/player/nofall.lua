local moduleTable = alya.modules.register("NoFall", "No fall damage!!!1!!1", "PLAYER")
local mode = moduleTable.addModeSetting("Mode", "NoFallMode", "Vanilla", "Vanilla", "Verus")

local createVanillaNoFallMode = loadScript("/lua/modules/player/nofall/vanilla.lua")
createVanillaNoFallMode(moduleTable, mode)

local createVerusNoFallMode = loadScript("/lua/modules/player/nofall/verus.lua")
createVerusNoFallMode(moduleTable, mode)

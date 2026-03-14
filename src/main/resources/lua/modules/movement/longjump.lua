local moduleTable = alya.modules.register("LongJump", "Makes you jump farther/higher", "MOVEMENT")

local mode      = moduleTable.addModeSetting("Mode", "", "Mineland", "Mineland", "Verus", "Fireball", "Vulcan", "NCP", "Grim")
local verusType = moduleTable.addModeSetting("Type", "", "Fast", "Fast", "High")

verusType.setVisibility(function() return mode.is("Verus") end)

local createMineland = loadScript("/lua/modules/movement/longjump/mineland.lua")
local createVerus    = loadScript("/lua/modules/movement/longjump/verus.lua")
local createFireball = loadScript("/lua/modules/movement/longjump/fireball.lua")
local createVulcan   = loadScript("/lua/modules/movement/longjump/vulcan.lua")
local createNcp      = loadScript("/lua/modules/movement/longjump/ncp.lua")
local createGrim     = loadScript("/lua/modules/movement/longjump/grim.lua")

moduleTable.onDisable(function()
    alya.mc.setTimerSpeed(1)
    alya.combat.setPlayerPitch(0)
end)

createMineland(moduleTable, mode)
createVerus(moduleTable, mode, verusType)
createFireball(moduleTable, mode)
createVulcan(moduleTable, mode)
createNcp(moduleTable, mode)
createGrim(moduleTable, mode)

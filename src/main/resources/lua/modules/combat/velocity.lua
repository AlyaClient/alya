local moduleTable = alya.modules.register("Velocity", "nO KnoCkbaCK", "COMBAT")

local mode  = moduleTable.addModeSetting("Mode", "Velocity Mode", "Motion", "Motion")
local hVel  = moduleTable.addNumberSetting("Horizontal", "Horizontal velocity %", 0, 0, 100, 1)
local vVel  = moduleTable.addNumberSetting("Vertical", "Vertical velocity %", 0, 0, 100, 1)

local createMotionVelocityMode = loadScript("/lua/modules/combat/velocity/motion.lua")

createMotionVelocityMode(moduleTable, mode, "Motion", hVel, vVel)

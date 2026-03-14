local moduleTable = alya.modules.register("Flight", "Airplane", "MOVEMENT")

local mode = moduleTable.addModeSetting("Mode", "Flight mode", "Motion", "Motion", "Static")
local speed = moduleTable.addNumberSetting("Speed", "How fast", 1.0, 0.1, 10.0)

speed.setVisibility(function() return mode.is("Motion") end)

local createMotionFlightMode = loadScript("/lua/modules/movement/flight/motion.lua")
local createStaticFlightMode = loadScript("/lua/modules/movement/flight/static.lua")

createMotionFlightMode(moduleTable, mode, speed)
createStaticFlightMode(moduleTable, mode)

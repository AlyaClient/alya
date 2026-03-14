local moduleTable = alya.modules.register("AutoClicker", "Clicks for you.", "COMBAT")

local mode      = moduleTable.addModeSetting("Mode", "", "Normal", "Normal", "Drag")
local minCps    = moduleTable.addNumberSetting("Min CPS", "", 9, 0, 20, 0.01)
local maxCps    = moduleTable.addNumberSetting("Max CPS", "", 11, 0, 20, 0.01)
local dragTime  = moduleTable.addNumberSetting("Duration (MS)", "", 1200, 100, 2000, 100)
local dragDelay = moduleTable.addNumberSetting("Delay (MS)", "", 300, 100, 1000, 100)

minCps.setVisibility(function() return mode.is("Normal") end)
maxCps.setVisibility(function() return mode.is("Normal") end)
dragTime.setVisibility(function() return mode.is("Drag") end)
dragDelay.setVisibility(function() return mode.is("Drag") end)

local createNormalMode = loadScript("/lua/modules/combat/autoclicker/normal.lua")
local createDragMode   = loadScript("/lua/modules/combat/autoclicker/drag.lua")

createNormalMode(moduleTable, mode, minCps, maxCps)
createDragMode(moduleTable, mode, minCps, maxCps, dragTime, dragDelay)

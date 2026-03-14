local moduleTable = alya.modules.register("Criticals", "Crit attacks", "COMBAT")

local mode  = moduleTable.addModeSetting("Mode", "", "Watchdog", "Watchdog", "Packet")
local delay = moduleTable.addNumberSetting("Delay", "", 1, 0, 20, 1)

delay.setVisibility(function() return mode.is("Packet") end)

local createWatchdogMode = loadScript("/lua/modules/combat/criticals/watchdog.lua")
local createPacketMode   = loadScript("/lua/modules/combat/criticals/packet.lua")

createWatchdogMode(moduleTable, mode)
createPacketMode(moduleTable, mode, delay)

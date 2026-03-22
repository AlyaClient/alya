local moduleTable = alya.modules.register("Disabler", "Attempts to disable certain checks on anticheats", "OTHER")

local mode = moduleTable.addModeSetting("Mode", "Disabler Mode", "OmniSprint", "OmniSprint")

local createOmniSprintMode = loadScript("/lua/modules/misc/disabler/omnisprint.lua")

createOmniSprintMode(moduleTable, mode)

local moduleTable = alya.modules.register("NoSlowDown", "Prevents slowdown when using items", "PLAYER")
local eating    = moduleTable.addBooleanSetting("Eating", "", true)
local drinking  = moduleTable.addBooleanSetting("Drinking", "", true)
local blocking  = moduleTable.addBooleanSetting("Blocking", "", true)
local bow       = moduleTable.addBooleanSetting("Bow", "", true)
alya.events.on("slowdown", function(event)
    if not moduleTable.isEnabled() then return end
    local reason = event.getReason()
    if reason == "eat"   and eating.isEnabled()   then event.cancel() end
    if reason == "drink" and drinking.isEnabled()  then event.cancel() end
    if reason == "block" and blocking.isEnabled()  then event.cancel() end
    if reason == "bow"   and bow.isEnabled()       then event.cancel() end
end)

local moduleTable = alya.modules.register("Reach", "Extends your attack range", "COMBAT")
local reachRange = moduleTable.addNumberSetting("Reach Range", "", 3, 3, 6, 0.1)
reachRange.setSecondValue(6.0)

local extra = moduleTable.addBooleanSetting("Extra", "", false)

local function getEffectiveMax()
    if extra.isEnabled() then return 100 end
    return reachRange.getSecondValue()
end

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end
    
    local effectiveMax = getEffectiveMax()
    local minR = reachRange.getValue()
    local reach = minR + math.random() * (effectiveMax - minR)
    
    alya.mc.setReach(reach)
end)

moduleTable.onDisable(function()
    alya.mc.resetReach()
end)

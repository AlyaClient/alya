local moduleTable = alya.modules.register("Reach", "Extends your attack range", "COMBAT")
local reachRange = moduleTable.addNumberSetting("Reach Range", "", 3, 3, 6, 0.1)
local chance = moduleTable.addNumberSetting("Chance", "Chance for reach to be applied", 70, 0, 100, 1)

local function getEffectiveMax()
    return reachRange.getSecondValue()
end

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end

    local effectiveMax = getEffectiveMax()
    local minR = reachRange.getValue()
    local reach = minR + math.random() * (effectiveMax - minR)

    if math.random() < chance.getValue() / 100 then
        alya.mc.setReach(reach)
    else
        alya.mc.resetReach()
    end
end)

moduleTable.onDisable(function()
    alya.mc.resetReach()
end)
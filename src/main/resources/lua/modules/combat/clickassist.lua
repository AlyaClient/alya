local moduleTable = alya.modules.register("Click Assist", "Gives you two mice", "COMBAT")
local extraCps  = moduleTable.addNumberSetting("Added CPS", "", 1, 0, 3, 1)
local chance    = moduleTable.addNumberSetting("Chance", "", 100, 0, 100, 1)
local rightClick = moduleTable.addBooleanSetting("Right Click", "", false)
local leftClick  = moduleTable.addBooleanSetting("Left Click", "", true)
local recursing = false
alya.events.on("tick", function(event)
    if not moduleTable.isEnabled() then return end
    if recursing then return end
    if math.random(100) > chance.getValue() then return end
    local clicks = extraCps.getValueAsInt()
    recursing = true
    for i = 1, clicks do
        if leftClick.isEnabled() and alya.combat.isAttackKeyDown() then
            alya.combat.clickMouse()
        end
        if rightClick.isEnabled() and alya.combat.isUseKeyDown() then
            alya.combat.rightClickMouse()
        end
    end
    recursing = false
end)
moduleTable.onEnable(function() recursing = false end)
moduleTable.onDisable(function() recursing = false end)

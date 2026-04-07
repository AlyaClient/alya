local function createNormalMode(moduleTable, mode, cps)
    local clickTimer = alya.timer.create()
    local function getCpsTicks()
        local minVal = (20 - cps.getSecondValue()) * 20
        local maxVal = (20 - cps.getValue()) * 20
        if minVal > maxVal then
            minVal, maxVal = maxVal, minVal
        end
        return math.floor(minVal + math.random() * (maxVal - minVal))
    end
    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then
            return
        end
        if not mode.is("Normal") then
            return
        end
        if not event.isPre() then
            return
        end
        if not alya.combat.isAttackKeyDown() then
            clickTimer.reset()
            return
        end
        if alya.combat.isUseKeyDown() then
            return
        end
        local cps = getCpsTicks()
        if clickTimer.hasElapsedAndReset(cps, true) then
            alya.combat.clickMouse()
        end
        if clickTimer.hasElapsedAndReset(cps - 2, true) then
            alya.combat.clickMouse()
        end
    end)
end
return createNormalMode

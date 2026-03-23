local function createDragMode(moduleTable, mode, cps, dragTime, dragDelay)
    local clickTimer = alya.timer.create()
    local dragTimer  = alya.timer.create()
    local delayTimer = alya.timer.create()
    local function getCpsTicks()
        local minVal = (20 - cps.getSecondValue()) * 20
        local maxVal = (20 - cps.getValue()) * 20
        if minVal > maxVal then minVal, maxVal = maxVal, minVal end
        return math.floor(minVal + math.random() * (maxVal - minVal))
    end
    alya.events.on("motion", function(event)
        if not moduleTable.isEnabled() then return end
        if not mode.is("Drag") then return end
        if not event.isPre() then return end
        if not alya.combat.isAttackKeyDown() then
            clickTimer.reset()
            dragTimer.reset()
            delayTimer.reset()
            return
        end
        local cps = getCpsTicks()
        if dragTimer.hasElapsed(dragTime.getValueAsInt()) then
            if delayTimer.hasElapsedAndReset(dragDelay.getValueAsInt(), true) then
                dragTimer.reset()
            end
        else
            delayTimer.reset()
            if clickTimer.hasElapsedAndReset(cps, true) then
                alya.combat.clickMouse()
            end
        end
    end)
end
return createDragMode

local moduleTable =
	alya.modules.register("LegitScaffold", "Larp as someone with skill", "PLAYER")
local edgeOnly = moduleTable.addBooleanSetting("Edge Only", "Only sneak on block edges", false)
local pitchCheck = moduleTable.addBooleanSetting("Pitch Check", "Only sneak when looking down (~45 deg)", false)
local blockOnly = moduleTable.addBooleanSetting("Block Only", "Only run while holding a block", false)
local delay = moduleTable.addNumberSetting("Delay", "Delay after block placed before unsneaking (ms)", 100, 0, 500, 10)

delay.setRangeEnabled(true)
delay.setSecondValue(200)
local timer = alya.timer.create()
local currentDuration = 0
local sneaking = false
local placed = false
local wasAboveVoid = false

local function nextDuration()
	currentDuration = delay.getRandomValueAsInt()
	timer.reset()
end

alya.events.on("motion", function(event)
	if not moduleTable.isEnabled() then
		return
	end
	if not event.isPre() then
		return
	end
    if blockOnly.isEnabled() and not alya.mc.isHoldingBlock() then
        return
    end

	local aboveVoid = alya.mc.isAboveVoid() and alya.mc.isOnGround() and not alya.mc.isOnLadder()
    if sneaking then
        if not placed and not aboveVoid then
            placed = true
            nextDuration()
        end

        if placed and timer.hasElapsed(currentDuration) then
            alya.mc.setSneakPressed(false)
            sneaking = false
            placed = false
            wasAboveVoid = true
        end
        return
    end

    if wasAboveVoid and aboveVoid then
        return
    end
    wasAboveVoid = false

	local shouldSneak = aboveVoid
    if shouldSneak and edgeOnly.isEnabled() then
        shouldSneak = alya.mc.isOnEdge()
    end

    if shouldSneak and pitchCheck.isEnabled() then
        local pitch = alya.mc.getCameraPitch()
        local yaw = alya.mc.getCameraYaw()

        local pitchOk = alya.mathutil.isBetween(pitch, 72, 82)
        local yawOk = alya.mathutil.isBetween(yaw, yaw - 102, yaw + 102)

        shouldSneak = pitchOk and yawOk
    end

	if shouldSneak then
		sneaking = true
		placed = false
		alya.mc.setSneakPressed(true)
	end
end)

moduleTable.onDisable(function()
	if sneaking then
		alya.mc.setSneakPressed(false)
		sneaking = false
		placed = false
	end
	wasAboveVoid = false
end)

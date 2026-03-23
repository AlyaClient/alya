local moduleTable =
	alya.modules.register("LegitScaffold", "legit scaffold in 3 lines cus thoq is doesnt believe me", "PLAYER")
local edgeOnly = moduleTable.addBooleanSetting("Edge Only", "Only sneak on block edges", false)
local pitchCheck = moduleTable.addBooleanSetting("Pitch Check", "Only sneak when looking down (~45 deg)", false)
local blockOnly = moduleTable.addBooleanSetting("Block Only", "Only run while holding a block", false)
local delay = moduleTable.addNumberSetting("Delay", "Sneak duration (ms)", 100, 0, 500, 10)
delay.setRangeEnabled(true)
delay.setSecondValue(200)
local timer = alya.timer.create()
local currentDuration = 0
local sneaking = false
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
	if sneaking then
		if timer.hasElapsed(currentDuration) then
			alya.mc.setSneakPressed(false)
			sneaking = false
		end
		return
	end
	local shouldSneak = alya.mc.isAboveVoid() and alya.mc.isOnGround() and not alya.mc.isOnLadder()
	if shouldSneak and edgeOnly.isEnabled() then
		shouldSneak = alya.mc.isOnEdge()
	end
	if shouldSneak and pitchCheck.isEnabled() then
		local pitch = alya.mc.getCameraPitch()
		shouldSneak = pitch > 35
	end
	if shouldSneak then
		sneaking = true
		nextDuration()
		alya.mc.setSneakPressed(true)
	end
end)
moduleTable.onDisable(function()
	if sneaking then
		alya.mc.setSneakPressed(false)
		sneaking = false
	end
end)

local moduleTable = alya.modules.register("KillAura", "Automatically attacks players within range", "COMBAT")
local clicksPerSecond = moduleTable.addNumberSetting("APS", "", 10, 1, 30, 0.1)
clicksPerSecond.setRangeEnabled(true)
clicksPerSecond.setSecondValue(10)
local searchRange = moduleTable.addNumberSetting("Seek Range", "", 6, 1, 100, 0.1)
local swingRange = moduleTable.addNumberSetting("Swing Range", "", 4, 1, 16, 0.1)
local attackRange = moduleTable.addNumberSetting("Attack Range", "", 3.0, 1, 8, 0.1)
local rotate = moduleTable.addBooleanSetting("Rotate", "", true)
local rotationMode = moduleTable.addModeSetting("Rotation Mode", "", "Blatant", "Blatant", "Legit", "Snap", "Spin")
local automaticBlock = moduleTable.addBooleanSetting("Auto Block", "", false)
local automaticBlockMode = moduleTable.addModeSetting("Auto Block Mode", "", "Vanilla", "Vanilla", "Hurt Time", "Fake")
local enchantmentParticles = moduleTable.addBooleanSetting("Enchant Particle", "", false)
local criticalParticles = moduleTable.addBooleanSetting("Critical Particle", "", false)
local extraRandomization = moduleTable.addBooleanSetting("Extra Randomization", "", false)
local clientRotate = moduleTable.addBooleanSetting("Client Rotate", "", true)
local raycast = moduleTable.addBooleanSetting("Raycast", "", true)
local noAttackDelay = moduleTable.addBooleanSetting("No Attack Delay", "", false)
local tomfoolery = moduleTable.addBooleanSetting("Tomfoolery", "", false)
local jitterStrength = moduleTable.addNumberSetting("Jitter Strength", "", 2.0, 0.1, 10.0, 0.1)
local smoothness = moduleTable.addNumberSetting("Smoothness", "", 3.0, 1.0, 10.0, 0.1)
local targetPlayers = moduleTable.addBooleanSetting("Target Players", "", true)
local targetHostile = moduleTable.addBooleanSetting("Target Hostile", "", false)
local targetPassive = moduleTable.addBooleanSetting("Target Passive", "", false)

jitterStrength.setVisibility(function() return rotationMode.is("Legit") end)
smoothness.setVisibility(function() return rotationMode.is("Legit") end)

local timer = alya.timer.create()
local isBlocking = false
local wasPreviouslyBlocked = false
local lockedTargetIdentifier = nil
local lastRotationTime = alya.mc.getCurrentTime()
local previousDifferenceYaw = 0
local previousDifferencePitch = 0
local spinAngleProgress = 0
local randomWalkAngleYaw = 0
local randomWalkAnglePitch = 0

local function getClicksPerSecondRange()
    local firstValue = clicksPerSecond.getValue()
    local secondValue = clicksPerSecond.getSecondValue()
    if firstValue > secondValue then return secondValue, firstValue else return firstValue, secondValue end
end

local function getRandomNumberInRange(minimumValue, maximumValue)
    return minimumValue + alya.mathutil.makeRandom() * (maximumValue - minimumValue)
end

local function wrapAngleDifference(angleDifference)
    angleDifference = angleDifference % 360
    if angleDifference > 180 then angleDifference = angleDifference - 360 end
    if angleDifference < -180 then angleDifference = angleDifference + 360 end
    return angleDifference
end

local function calculateGreatestCommonDivisorRotation(currentYaw, currentPitch, targetYaw, targetPitch)
    local sensitivityMultiplier = alya.combat.getSensitivityMultiplier()
    local sensitivityFormula = sensitivityMultiplier * 0.6 + 0.2
    local greatestCommonDivisor = (sensitivityFormula * sensitivityFormula * sensitivityFormula) * 1.2

    local differenceYaw = targetYaw - currentYaw
    local differencePitch = targetPitch - currentPitch

    local mouseDeltaX = math.floor(differenceYaw / greatestCommonDivisor + 0.5)
    local mouseDeltaY = math.floor(differencePitch / greatestCommonDivisor + 0.5)

    local finalYaw = currentYaw + (mouseDeltaX * greatestCommonDivisor)
    local finalPitch = currentPitch + (mouseDeltaY * greatestCommonDivisor)

    return finalYaw, finalPitch
end

local function calculateHumanRotation(currentYaw, currentPitch, targetYaw, targetPitch)
    local currentTime = alya.mc.getCurrentTime()
    local deltaTime = math.min((currentTime - lastRotationTime) / 1000.0, 0.05)
    lastRotationTime = currentTime

    local strengthValue = jitterStrength.getValue()
    local baseSmoothness = smoothness.getValue()

    randomWalkAngleYaw = randomWalkAngleYaw * 0.4 + (alya.mathutil.makeRandom() - 0.5) * strengthValue * deltaTime * 120
    randomWalkAnglePitch = randomWalkAnglePitch * 0.4 + (alya.mathutil.makeRandom() - 0.5) * strengthValue * 0.5 * deltaTime * 120

    local differenceYaw = wrapAngleDifference((targetYaw + randomWalkAngleYaw) - currentYaw)
    local differencePitch = wrapAngleDifference((targetPitch + randomWalkAnglePitch) - currentPitch)

    local distance = math.sqrt(differenceYaw * differenceYaw + differencePitch * differencePitch)
    local speedMultiplier = (2.2 / math.max(1.0, baseSmoothness)) * (0.8 + alya.mathutil.makeRandom() * 0.4)

    if distance > 0.1 then
        speedMultiplier = speedMultiplier * math.min(1.0, distance / 5.0)
    end

    local targetDeltaYaw = differenceYaw * speedMultiplier
    local targetDeltaPitch = differencePitch * speedMultiplier

    local smoothedDeltaYaw = previousDifferenceYaw * 0.15 + targetDeltaYaw * 0.85
    local smoothedDeltaPitch = previousDifferencePitch * 0.15 + targetDeltaPitch * 0.85

    previousDifferenceYaw = smoothedDeltaYaw
    previousDifferencePitch = smoothedDeltaPitch

    local finalTargetYaw = currentYaw + smoothedDeltaYaw
    local finalTargetPitch = currentPitch + smoothedDeltaPitch

    finalTargetPitch = math.max(-90, math.min(90, finalTargetPitch))

    return calculateGreatestCommonDivisorRotation(currentYaw, currentPitch, finalTargetYaw, finalTargetPitch)
end

local function executeAttack(targetEntity)
    local entityDistance = targetEntity.distance
    if entityDistance > attackRange.getValue() then return end

    if entityDistance <= swingRange.getValue() then
        if enchantmentParticles.isEnabled() then alya.combat.onEnchantmentCritical(targetEntity.id) end
        if criticalParticles.isEnabled() then alya.combat.onCriticalHit(targetEntity.id) end
        alya.combat.attackEntity(targetEntity.id)
    else
        alya.combat.swingItem()
    end
end

local function shouldExecuteAttack()
    if noAttackDelay.isEnabled() then return true end
    local minimumClicksPerSecond, maximumClicksPerSecond = getClicksPerSecondRange()
    local currentClicksPerSecond = math.floor(getRandomNumberInRange(minimumClicksPerSecond, maximumClicksPerSecond) + 0.5)
    local intervalMilliseconds = 1000 / currentClicksPerSecond
    if extraRandomization.isEnabled() then
        intervalMilliseconds = intervalMilliseconds * (0.85 + alya.mathutil.makeRandom() * 0.3)
    end
    return timer.hasElapsedAndReset(math.floor(intervalMilliseconds), true)
end

local function isFacingEntity(targetEntity, currentYaw, currentPitch)
    local targetRotation = alya.combat.getRotationToEntity(targetEntity)
    local differenceYaw = math.abs(wrapAngleDifference(currentYaw - targetRotation.yaw))
    local differencePitch = math.abs(wrapAngleDifference(currentPitch - targetRotation.pitch))
    return differenceYaw < 45 and differencePitch < 45
end

local function executeBlocking()
    if not isBlocking then
        alya.combat.setForcedBlocking(false)
        return
    end

    local blockMode = automaticBlockMode.getValue()
    if blockMode == "Vanilla" then
        alya.combat.setForcedBlocking(true)
        if not wasPreviouslyBlocked then
            alya.combat.sendBlockPlacement()
            wasPreviouslyBlocked = true
        end
    elseif blockMode == "Hurt Time" then
        if alya.combat.getHurtTime() == 9 and not wasPreviouslyBlocked then
            alya.combat.setForcedBlocking(true)
            alya.combat.sendBlockPlacement()
            wasPreviouslyBlocked = true
        end
        if alya.combat.getHurtTime() == 0 and wasPreviouslyBlocked and not alya.combat.isSwingInProgress() then
            alya.combat.setForcedBlocking(false)
            alya.combat.sendReleaseUseItem()
            wasPreviouslyBlocked = false
        end
    elseif blockMode == "Fake" then
        alya.combat.setForcedBlocking(true)
    end
end

alya.events.on("motion", function(motionEvent)
    if not moduleTable.isEnabled() then return end
    if not motionEvent.isPre() then return end

    local targetEntities = alya.combat.getEntities(searchRange.getValue(), raycast.isEnabled(), targetPlayers.isEnabled(), targetHostile.isEnabled(), targetPassive.isEnabled())
    for index = #targetEntities, 1, -1 do
        if alya.combat.isFriend(targetEntities[index].name) or targetEntities[index].health <= 0 then
            table.remove(targetEntities, index)
        end
    end

    local primaryTarget = nil
    if lockedTargetIdentifier ~= nil then
        local lockedEntity = alya.combat.getEntityById(lockedTargetIdentifier)
        if lockedEntity ~= nil and lockedEntity.distance <= searchRange.getValue() and lockedEntity.health > 0 and not alya.combat.isFriend(lockedEntity.name) then
            primaryTarget = lockedEntity
        else
            lockedTargetIdentifier = nil
        end
    end

    if primaryTarget == nil then
        primaryTarget = targetEntities[1]
        if primaryTarget ~= nil then
            lockedTargetIdentifier = primaryTarget.id
        end
    end

    isBlocking = automaticBlock.isEnabled() and primaryTarget ~= nil and alya.combat.isHoldingSword()
    if primaryTarget == nil then
        alya.combat.setForcedBlocking(false)
        if wasPreviouslyBlocked then
            alya.combat.sendReleaseUseItem()
            wasPreviouslyBlocked = false
        end
        return
    end

    local currentServerYaw = alya.combat.getPlayerYaw()
    local currentServerPitch = alya.combat.getPlayerPitch()
    local calculatedYaw = currentServerYaw
    local calculatedPitch = currentServerPitch

    if rotate.isEnabled() then
        local currentMode = rotationMode.getValue()
        local rawTargetRotation

        if currentMode == "Legit" then
            local modifiedEntity = {}
            for entityKey, entityValue in pairs(primaryTarget) do modifiedEntity[entityKey] = entityValue end
            modifiedEntity.eyeHeight = primaryTarget.eyeHeight * (0.65 + alya.mathutil.makeRandom() * 0.1)
            rawTargetRotation = alya.combat.getRotationToEntity(modifiedEntity)
            calculatedYaw, calculatedPitch = calculateHumanRotation(currentServerYaw, currentServerPitch, rawTargetRotation.yaw, rawTargetRotation.pitch)
        elseif currentMode == "Blatant" then
            rawTargetRotation = alya.combat.getRotationToEntity(primaryTarget)
            calculatedYaw, calculatedPitch = calculateGreatestCommonDivisorRotation(currentServerYaw, currentServerPitch, rawTargetRotation.yaw, rawTargetRotation.pitch)
        elseif currentMode == "Snap" then
            rawTargetRotation = alya.combat.getRotationToEntity(primaryTarget)
            calculatedYaw, calculatedPitch = calculateGreatestCommonDivisorRotation(currentServerYaw, currentServerPitch, rawTargetRotation.yaw, rawTargetRotation.pitch)
        elseif currentMode == "Spin" then
            spinAngleProgress = (spinAngleProgress + 45) % 360
            calculatedYaw, calculatedPitch = calculateGreatestCommonDivisorRotation(currentServerYaw, currentServerPitch, spinAngleProgress, 90)
        end

        motionEvent.setYaw(calculatedYaw)
        motionEvent.setPitch(calculatedPitch)
        if clientRotate.isEnabled() then
            alya.combat.setClientRotation(calculatedYaw, calculatedPitch)
        end
    end

    if tomfoolery.isEnabled() then
        for _, targetEntity in ipairs(targetEntities) do
            if shouldExecuteAttack() and isFacingEntity(targetEntity, calculatedYaw, calculatedPitch) then
                executeAttack(targetEntity)
            end
        end
    else
        if shouldExecuteAttack() and isFacingEntity(primaryTarget, calculatedYaw, calculatedPitch) then
            executeAttack(primaryTarget)
        end
    end
    executeBlocking()
end)

moduleTable.onDisable(function()
    alya.combat.setForcedBlocking(false)
    if wasPreviouslyBlocked and not alya.combat.isSwingInProgress() then
        alya.combat.sendReleaseUseItem()
    end

    if clientRotate.isEnabled() then
        alya.combat.setClientRotation(alya.combat.getPlayerYaw(), alya.combat.getPlayerPitch())
    end

    isBlocking = false
    wasPreviouslyBlocked = false
    lockedTargetIdentifier = nil
    lastRotationTime = alya.mc.getCurrentTime()
    previousDifferenceYaw = 0
    previousDifferencePitch = 0
    spinAngleProgress = 0
    randomWalkAngleYaw = 0
    randomWalkAnglePitch = 0
end)

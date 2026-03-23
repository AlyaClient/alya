local moduleTable = alya.modules.register("KillAura", "Automatically attacks players within range", "COMBAT")
local minCps = moduleTable.addNumberSetting("Minimum CPS", "", 10, 1, 100, 0.1)
local maxCps = moduleTable.addNumberSetting("Maximum CPS", "", 10, 1, 100, 0.1)
local seekRange = moduleTable.addNumberSetting("Seek Range", "", 6, 1, 16, 0.1)
local swingRange = moduleTable.addNumberSetting("Swing Range", "", 4, 1, 8, 0.1)
local attackRange = moduleTable.addNumberSetting("Attack Range", "", 3.5, 1, 8, 0.1)
local rotate = moduleTable.addBooleanSetting("Rotate", "", true)
local rotMode = moduleTable.addModeSetting("Rotation Mode", "", "Blatant", "Blatant", "Legit", "Snap", "Spin")
local autoBlock = moduleTable.addBooleanSetting("Auto Block", "", false)
local blockMode = moduleTable.addModeSetting("Auto Block Mode", "", "Vanilla", "Vanilla", "Hurt Time", "Fake")
local enchPart = moduleTable.addBooleanSetting("Enchant Particle", "", false)
local critPart = moduleTable.addBooleanSetting("Critical Particle", "", false)
local extraRand = moduleTable.addBooleanSetting("Extra Randomization", "", false)
local clientRotate = moduleTable.addBooleanSetting("Client Rotate", "", true)
local raycast = moduleTable.addBooleanSetting("Raycast", "", true)
local noDelay = moduleTable.addBooleanSetting("No Attack Delay", "", false)
local tomfoolery = moduleTable.addBooleanSetting("Tomfoolery", "", false)
local jitterStr = moduleTable.addNumberSetting("Jitter Strength", "", 2.0, 0.1, 10.0, 0.1)
local smoothness = moduleTable.addNumberSetting("Smoothness", "", 3.0, 1.0, 10.0, 0.1)
local targetPlayers = moduleTable.addBooleanSetting("Target Players", "", true)
local targetHostile = moduleTable.addBooleanSetting("Target Hostile", "", false)
local targetPassive = moduleTable.addBooleanSetting("Target Passive", "", false)
jitterStr.setVisibility(function() return rotMode.is("Legit") end)
smoothness.setVisibility(function() return rotMode.is("Legit") end)
local timer = alya.timer.create()
local blocking    = false
local wasBlocked  = false
local lockedTargetId = nil
local lastYaw     = alya.combat.getPlayerYaw()
local lastPitch   = alya.combat.getPlayerPitch()
local lastRotTime = alya.mc.getCurrentTime()
local jitterPhase = 0
local spinProgress = 0
local function clampCps()
    if minCps.getValue() > maxCps.getValue() then minCps.setValue(maxCps.getValue()) end
    if maxCps.getValue() < minCps.getValue() then maxCps.setValue(minCps.getValue()) end
end
local function randomInRange(a, b)
    return a + math.random() * (b - a)
end
local function wrapDelta(delta)
    delta = delta % 360
    if delta > 180 then delta = delta - 360 end
    return delta
end
local rwYaw   = 0
local rwPitch = 0
local function addJitter(yaw, pitch)
    local now = alya.mc.getCurrentTime()
    local dt  = math.min((now - lastRotTime) / 1000.0, 0.1)
    lastRotTime = now
    local js = jitterStr.getValue()
    local sf = 1.0 / smoothness.getValue()
    rwYaw   = rwYaw   * 0.75 + (math.random() - 0.5) * js * dt * 60
    rwPitch = rwPitch * 0.75 + (math.random() - 0.5) * js * 0.55 * dt * 60
    rwYaw   = math.max(-js, math.min(js, rwYaw))
    rwPitch = math.max(-js * 0.6, math.min(js * 0.6, rwPitch))
    local ty = yaw + rwYaw
    local tp = pitch + rwPitch
    local dyaw   = wrapDelta(ty - lastYaw)
    local dpitch = wrapDelta(tp - lastPitch)
    local ny = lastYaw  + dyaw   * sf
    local np = lastPitch + dpitch * sf
    np = math.max(-90, math.min(90, np))
    lastYaw   = ny
    lastPitch = np
    return ny, np
end
local function snapRotation(yaw, pitch)
    local sens = alya.combat.getSensitivityMultiplier() / 14
    local fixedYaw = yaw - (yaw % sens)
    return fixedYaw, pitch
end
local function doAttack(target)
    if target.distance > attackRange.getValue() then return end
    if target.distance <= swingRange.getValue() then
        if enchPart.isEnabled() then alya.combat.onEnchantmentCritical(target.id) end
        if critPart.isEnabled() then alya.combat.onCriticalHit(target.id) end
        alya.combat.attackEntity(target.id)
    else
        alya.combat.swingItem()
    end
end
local function shouldAttack()
    if noDelay.isEnabled() then return true end
    clampCps()
    local cps = math.floor(randomInRange(minCps.getValue(), maxCps.getValue()) + 0.5)
    local interval = 1000 / cps
    if extraRand.isEnabled() then
        interval = interval * math.floor(randomInRange(1, 2) + 0.5)
    end
    return timer.hasElapsedAndReset(math.floor(interval), true)
end
local function doBlock()
    if not blocking then return end
    local mode = blockMode.getValue()
    if mode == "Vanilla" then
        if not wasBlocked then
            alya.combat.sendBlockPlacement()
            wasBlocked = true
        end
    elseif mode == "Hurt Time" then
        if alya.combat.getHurtTime() == 9 and not wasBlocked then
            alya.combat.sendBlockPlacement()
            wasBlocked = true
        end
        if alya.combat.getHurtTime() == 0 and wasBlocked and not alya.combat.isSwingInProgress() then
            alya.combat.sendReleaseUseItem()
            wasBlocked = false
        end
    end
end
alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end
    local targets = alya.combat.getEntities(seekRange.getValue(), raycast.isEnabled(), targetPlayers.isEnabled(), targetHostile.isEnabled(), targetPassive.isEnabled())
    for i = #targets, 1, -1 do
        if alya.combat.isFriend(targets[i].name) or targets[i].health <= 0 then
            table.remove(targets, i)
        end
    end
    local primary = nil
    if lockedTargetId ~= nil then
        local locked = alya.combat.getEntityById(lockedTargetId)
        if locked ~= nil and locked.distance <= seekRange.getValue() and locked.health > 0 and not alya.combat.isFriend(locked.name) then
            primary = locked
        else
            lockedTargetId = nil
        end
    end
    if primary == nil then
        primary = targets[1]
        if primary ~= nil then
            lockedTargetId = primary.id
        end
    end
    blocking = autoBlock.isEnabled() and primary ~= nil and alya.combat.isHoldingSword()
    if primary == nil then
        if wasBlocked then
            alya.combat.sendReleaseUseItem()
            wasBlocked = false
        end
        return
    end
    if rotate.isEnabled() and primary ~= nil then
        local rot = alya.combat.getRotationToEntity(primary)
        local yaw, pitch = rot.yaw, rot.pitch
        local mode = rotMode.getValue()
        if mode == "Blatant" then
            yaw, pitch = snapRotation(yaw, pitch)
        elseif mode == "Legit" then
            yaw, pitch = addJitter(yaw, pitch)
            yaw, pitch = snapRotation(yaw, pitch)
        elseif mode == "Snap" then
            local js = 4.0
            local jx = (math.random() - 0.5) * js
            local jy = (math.random() - 0.5) * js * 0.5
            local dy = wrapDelta((yaw + jx) - lastYaw)
            local dp = wrapDelta((pitch + jy) - lastPitch)
            local ny = lastYaw  + dy
            local np = lastPitch + dp
            np = math.max(-90, math.min(90, np))
            lastYaw, lastPitch = ny, np
            yaw, pitch = snapRotation(ny, np)
        elseif mode == "Spin" then
            spinProgress = spinProgress + 45
            if spinProgress >= 360 then spinProgress = 0 end
            yaw = spinProgress
            pitch = 90
        end
        event.setYaw(yaw)
        event.setPitch(pitch)
        if clientRotate.isEnabled() then
            alya.combat.setClientRotation(yaw, pitch)
        end
    end
    if tomfoolery.isEnabled() then
        for _, target in ipairs(targets) do
            if shouldAttack() then doAttack(target) end
        end
    else
        if shouldAttack() then doAttack(primary) end
    end
    doBlock()
end)
moduleTable.onDisable(function()
    if wasBlocked and not alya.combat.isSwingInProgress() then
        alya.combat.sendReleaseUseItem()
    end
    if clientRotate.isEnabled() then
        local yaw = alya.combat.getPlayerYaw()
        local pitch = alya.combat.getPlayerPitch()
        alya.combat.setClientRotation(yaw, pitch)
    end
    blocking = false
    wasBlocked = false
    lockedTargetId = nil
    lastYaw   = alya.combat.getPlayerYaw()
    lastPitch = alya.combat.getPlayerPitch()
    lastRotTime = alya.mc.getCurrentTime()
    jitterPhase = 0
    spinProgress = 0
    rwYaw = 0
    rwPitch = 0
end)

local moduleTable = alya.modules.register("KillAura", "Automatically attacks players within range", "COMBAT")

local minCps     = moduleTable.addNumberSetting("Minimum CPS", "", 10, 1, 100, 0.1)
local maxCps     = moduleTable.addNumberSetting("Maximum CPS", "", 10, 1, 100, 0.1)
local reach      = moduleTable.addNumberSetting("Reach", "", 4, 3, 8, 0.1)
local rotate     = moduleTable.addBooleanSetting("Rotate", "", true)
local rotMode    = moduleTable.addModeSetting("Rotation Mode", "", "Blatant", "Blatant", "Legit", "Snap", "Spin")
local autoBlock  = moduleTable.addBooleanSetting("Auto Block", "", false)
local blockMode  = moduleTable.addModeSetting("Auto Block Mode", "", "Vanilla", "Vanilla", "Hurt Time", "Fake")
local enchPart   = moduleTable.addBooleanSetting("Enchant Particle", "", false)
local critPart   = moduleTable.addBooleanSetting("Critical Particle", "", false)
local extraRand  = moduleTable.addBooleanSetting("Extra Randomization", "", false)
local raycast    = moduleTable.addBooleanSetting("Raycast", "", true)
local noDelay    = moduleTable.addBooleanSetting("No Attack Delay", "", false)
local tomfoolery = moduleTable.addBooleanSetting("Tomfoolery", "", false)
local jitterStr  = moduleTable.addNumberSetting("Jitter Strength", "", 2.0, 0.1, 10.0, 0.1)
local smoothness = moduleTable.addNumberSetting("Smoothness", "", 3.0, 1.0, 10.0, 0.1)
local targetPlayers = moduleTable.addBooleanSetting("Target Players", "", true)
local targetHostile = moduleTable.addBooleanSetting("Target Hostile", "", false)
local targetPassive = moduleTable.addBooleanSetting("Target Passive", "", false)

jitterStr.setVisibility(function() return rotMode.is("Legit") end)
smoothness.setVisibility(function() return rotMode.is("Legit") end)

local timer       = alya.timer.create()
local blocking    = false
local wasBlocked  = false
local lastYaw     = 0
local lastPitch   = 0
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

local function addJitter(yaw, pitch)
    local now = alya.mc.getCurrentTime()
    local dt = (now - lastRotTime) / 1000.0
    lastRotTime = now

    jitterPhase = jitterPhase + dt * 2.0

    local js = jitterStr.getValue()
    local jx = math.sin(jitterPhase * 1.3) * math.cos(jitterPhase * 0.7) * js
    local jy = math.cos(jitterPhase * 1.1) * math.sin(jitterPhase * 0.9) * js * 0.6

    jx = jx + (math.random() - 0.5) * js * 0.3
    jy = jy + (math.random() - 0.5) * js * 0.2

    local sf = 1.0 / smoothness.getValue()
    local ty = yaw + jx
    local tp = pitch + jy

    local ny = lastYaw + (ty - lastYaw) * sf
    local np = lastPitch + (tp - lastPitch) * sf
    np = math.max(-90, math.min(90, np))

    lastYaw = ny
    lastPitch = np
    return ny, np
end

local function snapRotation(yaw, pitch)
    local sens = alya.combat.getSensitivityMultiplier() / 14
    local fixedYaw = yaw - (yaw % sens)
    return fixedYaw, pitch
end

local function doAttack(target)
    if enchPart.isEnabled() then alya.combat.onEnchantmentCritical(target.id) end
    if critPart.isEnabled() then alya.combat.onCriticalHit(target.id) end
    alya.combat.attackEntity(target.id)
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

    local targets = alya.combat.getEntities(reach.getValue(), raycast.isEnabled(), targetPlayers.isEnabled(), targetHostile.isEnabled(), targetPassive.isEnabled())

    for i = #targets, 1, -1 do
        if alya.combat.isFriend(targets[i].name) then
            table.remove(targets, i)
        end
    end

    local primary = targets[1]

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
            local js = 30.0
            local now = alya.mc.getCurrentTime()
            local dt = (now - lastRotTime) / 1000.0
            lastRotTime = now
            jitterPhase = jitterPhase + dt * 2.0
            local jx = math.sin(jitterPhase * 1.3) * math.cos(jitterPhase * 0.7) * js
            local jy = math.cos(jitterPhase * 1.1) * math.sin(jitterPhase * 0.9) * js * 0.6
            jx = jx + (math.random() - 0.5) * js * 0.3
            jy = jy + (math.random() - 0.5) * js * 0.2
            local ny = lastYaw + ((yaw + jx) - lastYaw)
            local np = lastPitch + ((pitch + jy) - lastPitch)
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
    blocking = false
    wasBlocked = false
    lastYaw = 0
    lastPitch = 0
    lastRotTime = alya.mc.getCurrentTime()
    jitterPhase = 0
    spinProgress = 0
end)

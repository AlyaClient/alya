local moduleTable = alya.modules.register("TargetStrafe", "Circles around the closest target", "COMBAT")
local radius = moduleTable.addNumberSetting("Radius", "", 3.0, 1.0, 6.0, 0.1)
local circleAlpha = moduleTable.addNumberSetting("Circle Alpha", "", 180, 0, 255, 1)
local segments = moduleTable.addNumberSetting("Segments", "", 64, 16, 128, 1)
local lineWidth = moduleTable.addNumberSetting("Line Width", "", 1.5, 0.5, 4.0, 0.5)
local holdSpace = moduleTable.addBooleanSetting("Hold Space", "", false)
local targetPlayers = moduleTable.addBooleanSetting("Target Players", "", true)
local targetHostile = moduleTable.addBooleanSetting("Target Hostile", "", false)
local targetPassive = moduleTable.addBooleanSetting("Target Passive", "", false)
local direction = 1
local lockedTargetId = nil
local function isValidTarget(entity)
    if entity == nil then return false end
    if entity.health <= 0 then return false end
    if alya.combat.isFriend(entity.name) then return false end
    return true
end
local function getTarget()
    if lockedTargetId ~= nil then
        local locked = alya.combat.getEntityById(lockedTargetId)
        if locked ~= nil and locked.distance <= 32 and isValidTarget(locked) then
            return locked
        end
        lockedTargetId = nil
    end
    local targets = alya.combat.getEntities(32, false, targetPlayers.isEnabled(), targetHostile.isEnabled(),
        targetPassive.isEnabled())
    for i = #targets, 1, -1 do
        if not isValidTarget(targets[i]) then
            table.remove(targets, i)
        end
    end
    local best = targets[1]
    if best ~= nil then
        lockedTargetId = best.id
    end
    return best
end
alya.events.on("playerinput", function(event)
    if not moduleTable.isEnabled() then
        return
    end
    if holdSpace.isEnabled() and not alya.mc.isJumpDown() then
        return
    end
    local target = getTarget()
    if target == nil then
        return
    end
    local px = alya.mc.getPlayerX()
    local pz = alya.mc.getPlayerZ()
    local dx = px - target.x
    local dz = pz - target.z
    local dist = math.sqrt(dx * dx + dz * dz)
    if dist < 0.001 then
        return
    end
    local nx = dx / dist
    local nz = dz / dist
    local tx = -nz * direction
    local tz = nx * direction
    local r = radius.getValue()
    local radialDelta = dist - r
    local corrX = -nx * radialDelta * 0.5
    local corrZ = -nz * radialDelta * 0.5
    local moveX = tx + corrX
    local moveZ = tz + corrZ
    local moveLen = math.sqrt(moveX * moveX + moveZ * moveZ)
    if moveLen > 1 then
        moveX = moveX / moveLen
        moveZ = moveZ / moveLen
    end
    local yawRad = math.rad(alya.combat.getPlayerYaw())
    local fwdX = -math.sin(yawRad)
    local fwdZ = math.cos(yawRad)
    local rgtX = math.cos(yawRad)
    local rgtZ = math.sin(yawRad)
    local fwd = moveX * fwdX + moveZ * fwdZ
    local strafe = moveX * rgtX + moveZ * rgtZ
    event.setMoveForward(fwd)
    event.setMoveStrafe(strafe)
    alya.mc.setSprinting(true)
end)
alya.events.on("render3d", function(event)
    if not moduleTable.isEnabled() then
        return
    end
    local target = getTarget()
    if target == nil then
        return
    end
    local alpha = math.floor(circleAlpha.getValue())
    local color = alya.render.toARGB(alpha, 255, 80, 80)
    local r = radius.getValue()
    alya.render.drawCircle3D(target.x, target.y, target.z, r, math.floor(segments.getValue()), color,
        lineWidth.getValue())
end)
moduleTable.onDisable(function()
    direction = 1
    lockedTargetId = nil
end)

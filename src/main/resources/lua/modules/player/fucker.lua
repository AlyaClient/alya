local moduleTable = alya.modules.register("Fucker", "Break special blocks through other blocks", "PLAYER")

local range = moduleTable.addNumberSetting("Range", "Maximum distance to target", 6, 1, 10, 0.5)
local mode = moduleTable.addModeSetting("Mode", "Breaking mode", "Instant", "Instant", "Legit")
local beds = moduleTable.addBooleanSetting("Beds", "Target beds", true)
local delay = moduleTable.addNumberSetting("Delay", "Delay between break attempts (ms)", 50, 0, 500, 10)
local highlight = moduleTable.addBooleanSetting("Highlight", "Highlight target block", true)

local timer = alya.timer.create()
local currentTarget = nil
local pathBlocks = {}
local targetBlockPos = nil
local isMiningThrough = false
local currentYaw = 0
local currentPitch = 0
local rotationSpeed = 0.15

local bedBlockIds = {
    [26] = true,   -- bed
    [355] = true,  -- bed (white)
}

local function isTargetBlock(block)
    if beds.isEnabled() and bedBlockIds[block] then
        return true
    end
    return false
end

local function getBlockId(x, y, z)
    return alya.mc.getBlockId(x, y, z)
end

local function hasLineOfSight(x, y, z)
    local result = alya.mc.raycastBlock(alya.mc.getYaw(), alya.mc.getPitch(), range.getValue())
    if result then
        return result.x == x and result.y == y and result.z == z
    end
    return false
end

local function findPathToTarget(targetX, targetY, targetZ)
    local playerX = math.floor(alya.mc.getPlayerX())
    local playerY = math.floor(alya.mc.getPlayerY())
    local playerZ = math.floor(alya.mc.getPlayerZ())

    local path = {}
    local visited = {}

    local function posKey(x, y, z)
        return x .. "," .. y .. "," .. z
    end

    local queue = {
        { x = playerX, y = playerY, z = playerZ, dist = 0 }
    }
    visited[posKey(playerX, playerY, playerZ)] = true

    local found = nil
    local iterations = 0
    local maxIterations = 200

    while #queue > 0 and iterations < maxIterations do
        iterations = iterations + 1
        table.sort(queue, function(a, b) return a.dist < b.dist end)

        local current = table.remove(queue, 1)

        if current.x == targetX and current.y == targetY and current.z == targetZ then
            found = current
            break
        end

        local neighbors = {
            { x = current.x + 1, y = current.y, z = current.z },
            { x = current.x - 1, y = current.y, z = current.z },
            { x = current.x, y = current.y, z = current.z + 1 },
            { x = current.x, y = current.y, z = current.z - 1 },
            { x = current.x, y = current.y + 1, z = current.z },
            { x = current.x, y = current.y - 1, z = current.z },
        }

        for _, neighbor in ipairs(neighbors) do
            local key = posKey(neighbor.x, neighbor.y, neighbor.z)
            if not visited[key] then
                local blockId = getBlockId(neighbor.x, neighbor.y, neighbor.z)
                if blockId == 0 or isTargetBlock(blockId) then
                    visited[key] = true
                    table.insert(queue, {
                        x = neighbor.x,
                        y = neighbor.y,
                        z = neighbor.z,
                        dist = current.dist + 1,
                        parent = current
                    })
                end
            end
        end
    end

    if found then
        local result = {}
        local curr = found
        while curr.parent do
            table.insert(result, { x = curr.x, y = curr.y, z = curr.z })
            curr = curr.parent
        end
        return result
    end

    return {}
end

local function getBreakingOrder(targetX, targetY, targetZ)
    local playerX = alya.mc.getPlayerX()
    local playerY = alya.mc.getPlayerY()
    local playerZ = alya.mc.getPlayerZ()

    local blocks = {}

    local path = findPathToTarget(targetX, targetY, targetZ)

    for i = #path, 1, -1 do
        local block = path[i]
        local dist = math.sqrt(
            (block.x - playerX) ^ 2 +
            (block.y - playerY) ^ 2 +
            (block.z - playerZ) ^ 2
        )
        table.insert(blocks, { x = block.x, y = block.y, z = block.z, dist = dist })
    end

    table.sort(blocks, function(a, b) return a.dist < b.dist end)
    return blocks
end

local function getRotationToBlock(x, y, z)
    local playerX = alya.mc.getPlayerX()
    local playerY = alya.mc.getPlayerY() + 1.5
    local playerZ = alya.mc.getPlayerZ()
    
    local dx = x + 0.5 - playerX
    local dy = y + 0.5 - playerY
    local dz = z + 0.5 - playerZ
    
    local yaw = math.atan2(-dx, dz) * (180 / math.pi)
    local pitch = math.atan2(dy, math.sqrt(dx * dx + dz * dz)) * (180 / math.pi)
    
    return yaw, pitch
end

local function lerpAngle(from, to, factor)
    local diff = to - from
    while diff > 180 do diff = diff - 360 end
    while diff < -180 do diff = diff + 360 end
    return from + diff * factor
end

local function breakBlockInstant(x, y, z)
    alya.mc.breakBlock(0, x, y, z, 1)
    alya.mc.breakBlock(1, x, y, z, 1)
end

local function getTargetBlock()
    local playerX = alya.mc.getPlayerX()
    local playerY = alya.mc.getPlayerY()
    local playerZ = alya.mc.getPlayerZ()
    local rangeVal = range.getValue()

    local targets = {}

    for x = math.floor(playerX) - rangeVal, math.floor(playerX) + rangeVal do
        for z = math.floor(playerZ) - rangeVal, math.floor(playerZ) + rangeVal do
            for y = math.floor(playerY) - 2, math.floor(playerY) + 2 do
                local blockId = getBlockId(x, y, z)
                if isTargetBlock(blockId) then
                    local dist = math.sqrt(
                        (x - playerX) ^ 2 +
                        (y - playerY) ^ 2 +
                        (z - playerZ) ^ 2
                    )
                    table.insert(targets, { x = x, y = y, z = z, dist = dist, blockId = blockId })
                end
            end
        end
    end

    table.sort(targets, function(a, b) return a.dist < b.dist end)
    return targets[1]
end

moduleTable.onEnable(function()
    timer.reset()
    currentTarget = nil
    pathBlocks = {}
    targetBlockPos = nil
    isMiningThrough = false
end)

moduleTable.onDisable(function()
    currentTarget = nil
    pathBlocks = {}
    targetBlockPos = nil
    isMiningThrough = false
end)

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end
    if alya.mc.isPlayerNull() or alya.mc.isWorldNull() then return end

    local target = getTargetBlock()

    if not target then
        currentTarget = nil
        pathBlocks = {}
        targetBlockPos = nil
        isMiningThrough = false
        return
    end

    if not currentTarget or currentTarget.x ~= target.x or currentTarget.y ~= target.y or currentTarget.z ~= target.z then
        currentTarget = target
        pathBlocks = getBreakingOrder(target.x, target.y, target.z)
        targetBlockPos = { x = target.x, y = target.y, z = target.z }
        isMiningThrough = #pathBlocks > 1
        timer.reset()
    end

    if mode.is("Instant") then
        breakBlockInstant(target.x, target.y, target.z)
    elseif mode.is("Legit") then
        if not timer.hasElapsed(delay.getValueAsInt()) then
            return
        end
        timer.reset()

        if #pathBlocks > 0 then
            local nextBlock = pathBlocks[1]
            local targetYaw, targetPitch = getRotationToBlock(nextBlock.x, nextBlock.y, nextBlock.z)
            
            currentYaw = lerpAngle(currentYaw, targetYaw, rotationSpeed)
            currentPitch = lerpAngle(currentPitch, targetPitch, rotationSpeed)
            
            alya.combat.setClientRotation(currentYaw, currentPitch)
            
            if hasLineOfSight(nextBlock.x, nextBlock.y, nextBlock.z) then
                alya.mc.sendRotation(currentYaw, currentPitch)
                breakBlockInstant(nextBlock.x, nextBlock.y, nextBlock.z)
            end

            local blockId = getBlockId(nextBlock.x, nextBlock.y, nextBlock.z)
            if blockId == 0 then
                table.remove(pathBlocks, 1)
            end
        end
    end
end)

alya.events.on("render3d", function(event)
    if not moduleTable.isEnabled() then return end
    if not highlight.isEnabled() then return end
    if not targetBlockPos then return end
    
    local x = targetBlockPos.x
    local y = targetBlockPos.y
    local z = targetBlockPos.z
    
    local color = alya.visual.toARGB(255, 255, 0, 0)
    alya.visual.drawBox3D(x + 0.5, y + 0.5, z + 0.5, 1, 1, color, 2.0)
end)
local scaffoldModule = alya.modules.register("Scaffold", "Automatically bridges for you", "PLAYER")
local sprint = scaffoldModule.addBooleanSetting("Sprint", "Sprint", true)
local swing = scaffoldModule.addBooleanSetting("Swing", "Swing", true)
local rotate = scaffoldModule.addBooleanSetting("Rotate", "Rotate", true)
local tower = scaffoldModule.addBooleanSetting("Tower", "Tower", true)
local searchRange = scaffoldModule.addNumberSetting("Search Range", "Search Range", 3, 1, 6, 1)
local bruteForceIntensity = scaffoldModule.addNumberSetting("Brute Force Intensity", "Brute Force Intensity", 5, 1, 10, 1)
local towerSpeed = scaffoldModule.addNumberSetting("Tower Speed", "Tower Speed", 0.42, 0.1, 1, 0.1)
local switchItemMode = scaffoldModule.addModeSetting("Switch Item Mode", "Switch Item Mode", "Client", "Client", "Server")
local swingMode = scaffoldModule.addModeSetting("Swing Mode", "Swing Mode", "Client", "Client", "Server")
local rotationMode = scaffoldModule.addModeSetting("Rotation Mode", "Rotation Mode", "Enum", "Enum", "Brute Force RayCast")
local towerMode = scaffoldModule.addModeSetting("Tower Mode", "Tower Mode", "Vanilla", "Vanilla", "BlocksMC", "Verus", "Vulcan")
swingMode.setVisibility(function() return swing.isEnabled() end)
rotationMode.setVisibility(function() return rotate.isEnabled() end)
towerMode.setVisibility(function() return tower.isEnabled() end)
towerSpeed.setVisibility(function() return tower.isEnabled() and towerMode.is("Vanilla") end)
bruteForceIntensity.setVisibility(function() return rotationMode.is("Brute Force RayCast") end)
local blockCache = nil
local lastBlockCache = nil
local startSlot = -1
local currentSlot = -1
local lastSlot = -1
local timerUtil = alya.timer.create()
local yaw = 0
local pitch = 0
local enumFacings = {
    {0, -1, 0, "UP"},
    {0, 1, 0, "DOWN"},
    {0, 0, -1, "SOUTH"},
    {0, 0, 1, "NORTH"},
    {-1, 0, 0, "EAST"},
    {1, 0, 0, "WEST"}
}
local function getHitVec(cacheX, cacheY, cacheZ, facingName)
    local hitX = cacheX + 0.5
    local hitY = cacheY + 0.5
    local hitZ = cacheZ + 0.5
    if facingName ~= "UP" and facingName ~= "DOWN" then
        hitY = hitY + 0.5
    else
        hitX = hitX + 0.3
        hitZ = hitZ + 0.3
    end
    if facingName == "SOUTH" or facingName == "NORTH" then
        hitX = hitX + 0.15
    end
    if facingName == "EAST" or facingName == "WEST" then
        hitZ = hitZ + 0.15
    end
    return hitX, hitY, hitZ
end
local function checkSurroundingBlocks(posX, posY, posZ)
    for index = 1, #enumFacings do
        local direction = enumFacings[index]
        local directionX = direction[1]
        local directionY = direction[2]
        local directionZ = direction[3]
        local blockX = posX + directionX
        local blockY = posY + directionY
        local blockZ = posZ + directionZ
        if alya.mc.isBlockSolid(blockX, blockY, blockZ) then
            return {
                x = blockX,
                y = blockY,
                z = blockZ,
                facingX = -directionX,
                facingY = -directionY,
                facingZ = -directionZ,
                facingName = direction[4]
            }
        end
    end
    return nil
end
local function findBlockCacheAtPosition(belowPosX, belowPosY, belowPosZ, offsetX, offsetZ)
    for iterator = 1, -2, -2 do
        local blockPosX = belowPosX + offsetX * iterator
        local blockPosY = belowPosY
        local blockPosZ = belowPosZ + offsetZ * iterator
        if alya.mc.isBlockAir(blockPosX, blockPosY, blockPosZ) then
            local cache = checkSurroundingBlocks(blockPosX, blockPosY, blockPosZ)
            if cache ~= nil then
                return cache
            end
        end
    end
    return nil
end
local function getBlockCache()
    local playerX = alya.mc.getPlayerX()
    local playerY = alya.mc.getPlayerY()
    local playerZ = alya.mc.getPlayerZ()
    local belowPosX = math.floor(playerX)
    local belowPosY = math.floor(playerY) - 1
    local belowPosZ = math.floor(playerZ)
    if not alya.mc.isBlockAir(belowPosX, belowPosY, belowPosZ) then
        return nil
    end
    local range = searchRange.getValueAsInt()
    for x = 0, range - 1 do
        for z = 0, range - 1 do
            local cache = findBlockCacheAtPosition(belowPosX, belowPosY, belowPosZ, x, z)
            if cache ~= nil then
                return cache
            end
        end
    end
    return nil
end
local function updateRotations()
    if rotationMode.is("Enum") then
        local angleYaw = 0
        if lastBlockCache.facingName == "SOUTH" then angleYaw = 0
        elseif lastBlockCache.facingName == "WEST" then angleYaw = 90
        elseif lastBlockCache.facingName == "NORTH" then angleYaw = 180
        elseif lastBlockCache.facingName == "EAST" then angleYaw = -90
        elseif lastBlockCache.facingName == "UP" then angleYaw = 0
        elseif lastBlockCache.facingName == "DOWN" then angleYaw = 0
        end
        yaw = angleYaw - 180
        pitch = 77
    elseif rotationMode.is("Brute Force RayCast") then
        local moveYaw = alya.movement.getMoveYaw()
        local intensity = bruteForceIntensity.getValueAsFloat()
        local range = searchRange.getValueAsFloat()
        local found = false
        for currentYaw = moveYaw - 180, moveYaw + 180 - 1, intensity do
            for currentPitch = 90, -89, -intensity do
                local raycastResult = alya.mc.raycastBlock(currentYaw, currentPitch, range)
                if raycastResult ~= nil then
                    if raycastResult.x == lastBlockCache.x and raycastResult.y == lastBlockCache.y and raycastResult.z == lastBlockCache.z then
                        yaw = currentYaw
                        pitch = currentPitch
                        found = true
                        break
                    end
                end
            end
            if found then break end
        end
    end
end
local function placeBlock()
    if blockCache == nil or lastBlockCache == nil or currentSlot == -1 then
        return
    end
    local hitX, hitY, hitZ = getHitVec(lastBlockCache.x, lastBlockCache.y, lastBlockCache.z, lastBlockCache.facingName)
    local placed = alya.mc.rightClickBlock(currentSlot, lastBlockCache.x, lastBlockCache.y, lastBlockCache.z, lastBlockCache.facingX, lastBlockCache.facingY, lastBlockCache.facingZ, hitX, hitY, hitZ)
    if placed then
        if rotate.isEnabled() then
            updateRotations()
        end
        if swing.isEnabled() then
            if swingMode.is("Client") then
                alya.combat.swingItem()
            else
                alya.mc.sendAnimation()
            end
        end
    end
    blockCache = nil
end
scaffoldModule.onEnable(function()
    startSlot = alya.mc.getHotbarSlot()
    currentSlot = -1
    lastSlot = -1
    blockCache = nil
    lastBlockCache = nil
    yaw = alya.movement.getMoveYaw() + 180
    pitch = 80
end)
scaffoldModule.onDisable(function()
    if alya.mc.getHotbarSlot() ~= startSlot or lastSlot ~= startSlot then
        if switchItemMode.is("Client") then
            alya.combat.setHotbarSlot(startSlot)
        else
            alya.mc.sendHeldItemChange(startSlot)
        end
    end
end)
alya.events.on("motion", function(event)
    if not scaffoldModule.isEnabled() then return end
    currentSlot = alya.mc.getHotbarBlockSlot()
    local isFlying = false
    local flightModule = alya.modules.get("Flight")
    if flightModule ~= nil and flightModule.isEnabled() then isFlying = true end
    local speedModule = alya.modules.get("Speed")
    local isSpeeding = false
    if speedModule ~= nil and speedModule.isEnabled() then isSpeeding = true end
    if not isFlying and not isSpeeding then
        if sprint.isEnabled() and alya.movement.isMoving() then
            alya.mc.setSprinting(true)
            alya.movement.setSpeed(alya.movement.SPRINT_SPEED)
        else
            alya.mc.setSprinting(false)
            alya.movement.setSpeed(alya.movement.WALK_SPEED)
        end
    else
        if sprint.isEnabled() then
            alya.mc.setSprinting(true)
        else
            alya.mc.setSprinting(false)
        end
    end
    if currentSlot ~= -1 then
        if switchItemMode.is("Client") and alya.mc.getHotbarSlot() ~= currentSlot then
            alya.combat.setHotbarSlot(currentSlot)
        end
        if switchItemMode.is("Server") and lastSlot ~= currentSlot then
            alya.mc.sendHeldItemChange(currentSlot)
            lastSlot = currentSlot
        end
    end
    if event.isPre() then
        blockCache = getBlockCache()
        if blockCache ~= nil then
            lastBlockCache = blockCache
        end
        local sensitivityMultiplier = alya.combat.getSensitivityMultiplier()
        local fixedYaw = yaw - (yaw % sensitivityMultiplier)
        local fixedPitch = pitch - (pitch % sensitivityMultiplier)
        event.setYaw(fixedYaw)
        event.setPitch(fixedPitch)
        if alya.mc.isOnGround() then
            alya.mc.setMotionX(alya.mc.getMotionX() * 0.91)
            alya.mc.setMotionZ(alya.mc.getMotionZ() * 0.91)
        end
        if tower.isEnabled() and alya.mc.isJumpDown() and blockCache ~= nil then
            if towerMode.is("Vanilla") then
                alya.mc.setMotionY(towerSpeed.getValueAsFloat())
            elseif towerMode.is("BlocksMC") then
                if alya.mc.isJumpDown() then alya.mc.setMotionY(0.42) end
                if alya.movement.isMoving() then
                    alya.mc.setMotionY(0.15)
                end
            elseif towerMode.is("Verus") then
                if timerUtil.hasElapsed(10) and alya.mc.isOnGround() then
                    alya.mc.setMotionY(-0.1)
                    alya.mc.jump()
                else
                    alya.mc.setMotionY(1)
                end
            elseif towerMode.is("Vulcan") then
                if alya.mc.isJumpDown() then
                    alya.mc.setMotionY(0.2)
                    if timerUtil.hasElapsed(4000) then
                        alya.chat.warning("Towering further may result in a silent flag, please proceed with caution")
                    end
                end
            end
        end
    end
end)
alya.events.on("blockplaceable", function(event)
    if not scaffoldModule.isEnabled() then return end
    placeBlock()
end)
alya.events.on("packetsend", function(event)
    if not scaffoldModule.isEnabled() then return end
    if event.getPacketClass() == "C09PacketHeldItemChange" then
        if switchItemMode.is("Server") then
            startSlot = alya.mc.getHotbarSlot()
            event.cancel()
        end
    end
end)
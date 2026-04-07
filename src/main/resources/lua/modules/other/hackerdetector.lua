local hackerDetector = alya.modules.register("HackerDetector", "Watches player movements to detect cheats", "OTHER")
local reportInterval = hackerDetector.addNumberSetting("Report Interval", "Seconds between reports for the same player", 5, 1, 15, 1)
local checkSpeed = hackerDetector.addBooleanSetting("Check Speed", "Detects abnormal horizontal movement", true)
local checkFlight = hackerDetector.addBooleanSetting("Check Flight", "Detects hovering or abnormal vertical movement", true)
local checkHighJump = hackerDetector.addBooleanSetting("Check HighJump", "Detects jumping higher than normally possible", true)
local playersData = {}
local function getPlayerData(name)
    if not playersData[name] then
        playersData[name] = {
            vlSpeed = 0,
            vlHighJump = 0,
            hoverTicks = 0,
            glideTicks = 0,
            upTicks = 0,
            lastReportTime = 0
        }
    end
    return playersData[name]
end
local function flag(name, hackType, details)
    local pData = getPlayerData(name)
    local now = alya.mc.getCurrentTime()
    if now - pData.lastReportTime > (reportInterval.getValueAsInt() * 1000) then
        alya.chat.warning("[HackerDetector] " .. name .. " flagged for " .. hackType .. " " .. details)
        pData.lastReportTime = now
    end
end
local function isAirUnder(p)
    local width = p.width or 0.6
    local minX = math.floor(p.x - width / 2)
    local maxX = math.floor(p.x + width / 2)
    local minZ = math.floor(p.z - width / 2)
    local maxZ = math.floor(p.z + width / 2)
    for yOffset = 0.1, 1.1, 0.5 do
        local y = math.floor(p.y - yOffset)
        for x = minX, maxX do
            for z = minZ, maxZ do
                if not alya.mc.isBlockAir(x, y, z) and not alya.mc.isBlockLiquid(x, y, z) then
                    return false
                end
            end
        end
    end
    return true
end
hackerDetector.onEnable(function()
    playersData = {}
end)
alya.events.on("motion", function(event)
    if not hackerDetector.isEnabled() or not event.isPre() then return end
    local players = alya.combat.getAllPlayers()
    if not players then return end
    for i = 1, #players do
        local p = players[i]
        local name = p.name
        if p.health > 0 and name ~= "" and not p.isInvisible then
            local pData = getPlayerData(name)
            local dx = p.x - p.lastX
            local dy = p.y - p.lastY
            local dz = p.z - p.lastZ
            local speed = math.sqrt(dx * dx + dz * dz)
            local airUnder = isAirUnder(p)
            if checkSpeed.isEnabled() then
                if speed > 1.2 and airUnder then
                    pData.vlSpeed = pData.vlSpeed + 0.5
                elseif speed > 1.0 then
                    pData.vlSpeed = pData.vlSpeed + 1
                else
                    pData.vlSpeed = math.max(0, pData.vlSpeed - 0.2)
                end
                if pData.vlSpeed > 8 then
                    flag(name, "Speed", "(BPS: " .. string.format("%.1f", speed * 20) .. ")")
                    pData.vlSpeed = 0
                end
            end
            if checkFlight.isEnabled() then
                if airUnder and math.abs(dy) < 0.01 and speed > 0.05 then
                    pData.hoverTicks = pData.hoverTicks + 1
                    if pData.hoverTicks > 10 then
                        flag(name, "Flight", "(Hover)")
                        pData.hoverTicks = 0
                    end
                else
                    pData.hoverTicks = 0
                end
                if airUnder and dy < 0 and dy > -0.07 then
                    pData.glideTicks = pData.glideTicks + 1
                    if pData.glideTicks > 20 then
                        flag(name, "Flight", "(Glide)")
                        pData.glideTicks = 0
                    end
                else
                    pData.glideTicks = 0
                end
                if airUnder and dy > 0.01 and dy < 0.35 then
                    pData.upTicks = pData.upTicks + 1
                    if pData.upTicks > 15 then
                        flag(name, "Flight", "(Ascending)")
                        pData.upTicks = 0
                    end
                else
                    pData.upTicks = 0
                end
            end
            if checkHighJump.isEnabled() then
                if dy > 0.85 then
                    pData.vlHighJump = pData.vlHighJump + 1
                    if pData.vlHighJump > 1 then
                        flag(name, "HighJump/Step", "(MotionY: " .. string.format("%.2f", dy) .. ")")
                        pData.vlHighJump = 0
                    end
                else
                    pData.vlHighJump = math.max(0, pData.vlHighJump - 0.1)
                end
            end
        end
    end
end)
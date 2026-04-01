local moduleTable = alya.modules.register("DoubleJump", "Jump while in the air", "MOVEMENT")
local mode = moduleTable.addModeSetting("Mode", "Double jump mode", "Vanilla", "Vanilla")

local airJumpsUsed = 0
local jumpWasPressed = false

alya.events.on("motion", function(event)
    if not moduleTable.isEnabled() then return end
    if not event.isPre() then return end

    local onGround = alya.mc.isOnGround()
    local jumpPressed = alya.mc.isJumpPressed()

    if onGround then
        airJumpsUsed = 0
        jumpWasPressed = jumpPressed
        return
    end

    if jumpPressed and not jumpWasPressed then
        alya.mc.setMotionY(0.42)
        airJumpsUsed = airJumpsUsed + 1
    end

    jumpWasPressed = jumpPressed
end)

moduleTable.onDisable(function()
    airJumpsUsed = 0
    jumpWasPressed = false
end)

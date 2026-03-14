movement = {
    isMoving = function() return alya.movement.isMoving() end,
    jump = function() alya.movement.jump() end,
    getMoveYaw = function() return alya.movement.getMoveYaw() end,
    getMoveSpeed = function() return alya.movement.getMoveSpeed() end,
    getDirection = function() return alya.movement.getDirection() end,
    getSpeed = function() return alya.movement.getSpeed() end,
    getAllowedHDistNCP = function() return alya.movement.getAllowedHDistNCP() end,
    setSpeed = function(speed) alya.movement.setSpeed(speed) end,
    setSpeedStrafe = function(speed, strafe) alya.movement.setSpeedStrafe(speed, strafe) end,
    SPRINT_SPEED = alya.movement.SPRINT_SPEED,
    WALK_SPEED = alya.movement.WALK_SPEED,
}

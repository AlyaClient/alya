---@meta

---@class AlyaModuleTable
---@field getName fun(): string
---@field getDescription fun(): string
---@field getCategory fun(): string
---@field isEnabled fun(): boolean
---@field toggle fun()
---@field enable fun()
---@field disable fun()
---@field getKeyCode fun(): integer
---@field setKeyCode fun(keyCode: integer)
---@field onEnable fun(callback: fun())
---@field onDisable fun(callback: fun())
---@field addBooleanSetting fun(name: string, description: string, default: boolean): AlyaBooleanSetting
---@field addNumberSetting fun(name: string, description: string, default: number, min: number, max: number, increment?: number): AlyaNumberSetting
---@field addModeSetting fun(name: string, description: string, default: string, ...: string): AlyaModeSetting
---@field getSetting fun(name: string): AlyaBooleanSetting|AlyaNumberSetting|AlyaModeSetting|nil

---@class AlyaBooleanSetting
---@field getName fun(): string
---@field getValue fun(): boolean
---@field isEnabled fun(): boolean
---@field setValue fun(value: boolean)
---@field toggle fun()
---@field setVisibility fun(fn: fun(): boolean)
---@field isVisible fun(): boolean

---@class AlyaNumberSetting
---@field getName fun(): string
---@field getValue fun(): number
---@field getValueAsInt fun(): integer
---@field getValueAsFloat fun(): number
---@field setValue fun(value: number)
---@field getSecondValue fun(): number
---@field getSecondValueAsInt fun(): integer
---@field setSecondValue fun(value: number)
---@field getMin fun(): number
---@field getMax fun(): number
---@field getIncrement fun(): number
---@field setVisibility fun(fn: fun(): boolean)
---@field isVisible fun(): boolean

---@class AlyaModeSetting
---@field getName fun(): string
---@field getValue fun(): string
---@field is fun(mode: string): boolean
---@field setValue fun(mode: string)
---@field cycle fun()
---@field getModes fun(): string[]
---@field setOnChange fun(fn: fun())
---@field setVisibility fun(fn: fun(): boolean)
---@field isVisible fun(): boolean

---@class AlyaMotionEvent
---@field getType fun(): string
---@field getX fun(): number
---@field getY fun(): number
---@field getZ fun(): number
---@field getYaw fun(): number
---@field getPitch fun(): number
---@field isOnGround fun(): boolean
---@field isPre fun(): boolean
---@field isPost fun(): boolean
---@field setYaw fun(yaw: number)
---@field setPitch fun(pitch: number)
---@field setX fun(x: number)
---@field setY fun(y: number)
---@field setZ fun(z: number)
---@field cancel fun()
---@field isCanceled fun(): boolean

---@class AlyaRender2DEvent
---@field getType fun(): string
---@field getWidth fun(): integer
---@field getHeight fun(): integer

---@class AlyaCancelableEvent
---@field getType fun(): string
---@field cancel fun()
---@field isCanceled fun(): boolean

---@class AlyaSlowDownEvent : AlyaCancelableEvent
---@field getReason fun(): "eat"|"drink"|"block"|"bow"|"unknown"

---@class AlyaPacketSendEvent : AlyaCancelableEvent
---@field getPacketClass fun(): string
---@field getEntityAction fun(): string|nil action name for C0BPacketEntityAction (e.g. "STOP_SPRINTING"), nil otherwise

---@class AlyaPacketReceiveEvent : AlyaCancelableEvent
---@field getPacketClass fun(): string
---@field getEntityId fun(): integer packet entity id (S12 only, -1 otherwise)
---@field getMotionX fun(): number raw motion x (int for S12, float for S27)
---@field getMotionY fun(): number raw motion y
---@field getMotionZ fun(): number raw motion z
---@field setMotionX fun(v: number)
---@field setMotionY fun(v: number)
---@field setMotionZ fun(v: number)

---@class AlyaModules
local AlyaModules = {}

---registers a new Lua module and returns its control table
---@param name string
---@param description string
---@param category "COMBAT"|"MOVEMENT"|"VISUAL"|"PLAYER"|"EXPLOIT"|"OTHER"
---@return AlyaModuleTable
function AlyaModules.register(name, description, category) end

---returns the control table for existing module by name
---@param name string
---@return AlyaModuleTable|nil
function AlyaModules.get(name) end

---returns all registered Lua module tables
---@return AlyaModuleTable[]
function AlyaModules.getAll() end

---returns all currently enabled Lua module tables
---@return AlyaModuleTable[]
function AlyaModules.getEnabled() end

---returns all Lua module tables for the given category
---@param category "COMBAT"|"MOVEMENT"|"VISUAL"|"PLAYER"|"EXPLOIT"|"OTHER"
---@return AlyaModuleTable[]
function AlyaModules.getByCategory(category) end

---@class AlyaEvents
local AlyaEvents = {}

---subscribes a callback to named game event
---@param eventName "motion"|"update"|"render2d"|"render3d"|"tick"|"timeupdate"|"playermove"|"playerinput"|"packetsend"|"packetreceive"|"moveentity"|"slowdown"|"blockplaceable"
---@param callback fun(event: AlyaMotionEvent|AlyaRender2DEvent|AlyaCancelableEvent|AlyaPacketSendEvent)
function AlyaEvents.on(eventName, callback) end

---returns a list of registered event names
---@return string[]
function AlyaEvents.getNames() end

---@class AlyaCommandInfo
---@field name string
---@field description string
---@field aliases string[]

---@class AlyaCommands
local AlyaCommands = {}

---registers new chat command
---@param name string
---@param description string
---@param execute fun(args: string[])
---@param ... string aliases
function AlyaCommands.register(name, description, execute, ...) end

---returns command prefix (default ".")
---@return string
function AlyaCommands.getPrefix() end

---returns registered commands
---@return AlyaCommandInfo[]
function AlyaCommands.getAll() end

---@class AlyaConfig
local AlyaConfig = {}

---saves the current configuration under given name (default "default")
---@param name? string
function AlyaConfig.save(name) end

---loads configuration with given name (default "default")
---@param name? string
function AlyaConfig.load(name) end

---returns true if config with name exists
---@param name string
---@return boolean
function AlyaConfig.exists(name) end

---returns saved config names
---@return string[]
function AlyaConfig.getNames() end

---@class AlyaChat
local AlyaChat = {}

---@param message string
function AlyaChat.info(message) end
---@param message string
function AlyaChat.success(message) end
---@param message string
function AlyaChat.error(message) end
---@param message string
function AlyaChat.warning(message) end
---@param message string
function AlyaChat.raw(message) end

---@class AlyaMovement
local AlyaMovement = {}

---@return boolean
function AlyaMovement.isMoving() end
function AlyaMovement.jump() end
---@return number
function AlyaMovement.getMoveYaw() end
---@return number
function AlyaMovement.getMoveSpeed() end
---@return number
function AlyaMovement.getDirection() end
---@return number
function AlyaMovement.getSpeed() end
---@return number
function AlyaMovement.getAllowedHDistNCP() end
---@param speed number
function AlyaMovement.setSpeed(speed) end
---@param speed number
---@param strafePercentage number
function AlyaMovement.setSpeedStrafe(speed, strafePercentage) end
---@type number
AlyaMovement.SPRINT_SPEED = 0.1533
---@type number
AlyaMovement.WALK_SPEED = 0.0888

---@class AlyaRender
local AlyaRender = {}

---@param x number @param y number @param width number @param height number @param color integer
function AlyaRender.drawRect(x, y, width, height, color) end
---@param left number @param top number @param right number @param bottom number @param color integer
function AlyaRender.drawRectAbsolute(left, top, right, bottom, color) end
---@param x number @param y number @param width number @param height number @param color integer @param thickness number
function AlyaRender.drawRectOutline(x, y, width, height, color, thickness) end
---@param x number @param y number @param width number @param height number @param radius number @param color integer
function AlyaRender.drawRoundedRect(x, y, width, height, radius, color) end
---@param centerX number @param centerY number @param radius number @param startAngle integer @param endAngle integer @param color integer
function AlyaRender.drawArc(centerX, centerY, radius, startAngle, endAngle, color) end
---@param centerX number @param centerY number @param radius number @param color integer
function AlyaRender.drawCircle(centerX, centerY, radius, color) end
---@param x number @param y number @param width number @param height number @param leftColor integer @param rightColor integer
function AlyaRender.drawHorizontalGradient(x, y, width, height, leftColor, rightColor) end
---@param x number @param y number @param width number @param height number @param topColor integer @param bottomColor integer
function AlyaRender.drawVerticalGradient(x, y, width, height, topColor, bottomColor) end
---@param x1 number @param y1 number @param x2 number @param y2 number @param thickness number @param color integer
function AlyaRender.drawLine(x1, y1, x2, y2, thickness, color) end
---@param alpha integer @param red integer @param green integer @param blue integer @return integer
function AlyaRender.toARGB(alpha, red, green, blue) end
---@param red integer @param green integer @param blue integer @return integer
function AlyaRender.toRGB(red, green, blue) end
---@param color integer @param alpha integer @return integer
function AlyaRender.withAlpha(color, alpha) end

---@class AlyaTimerInstance
---@field reset fun()
---@field getTime fun(): integer
---@field hasElapsed fun(milliseconds: integer): boolean
---@field hasElapsedAndReset fun(milliseconds: integer, reset: boolean): boolean

---@class AlyaTimer
local AlyaTimer = {}

---creates and returns new timer instance
---@return AlyaTimerInstance
function AlyaTimer.create() end

---@class AlyaMC
local AlyaMC = {}

---@return integer
function AlyaMC.getDebugFPS() end
---@return boolean
function AlyaMC.isPlayerNull() end
---@return boolean
function AlyaMC.isWorldNull() end
---@return number
function AlyaMC.getGamma() end
---@param gamma number
function AlyaMC.setGamma(gamma) end
---@return integer
function AlyaMC.getRightClickDelay() end
---@param delay integer
function AlyaMC.setRightClickDelay(delay) end
---@return number
function AlyaMC.getTimerSpeed() end
---@param speed number
function AlyaMC.setTimerSpeed(speed) end
---@return number
function AlyaMC.getWorldTime() end
---@param time number
function AlyaMC.setWorldTime(time) end
---@return number
function AlyaMC.getPlayerX() end
---@return number
function AlyaMC.getPlayerY() end
---@return number
function AlyaMC.getPlayerZ() end
---@return number
function AlyaMC.getMotionX() end
---@return number
function AlyaMC.getMotionY() end
---@return number
function AlyaMC.getMotionZ() end
---@param value number
function AlyaMC.setMotionX(value) end
---@param value number
function AlyaMC.setMotionY(value) end
---@param value number
function AlyaMC.setMotionZ(value) end
---@return boolean
function AlyaMC.isOnGround() end
---@return integer
function AlyaMC.getJumpTicks() end
---@param ticks integer
function AlyaMC.setJumpTicks(ticks) end
function AlyaMC.jump() end
---@param sprinting boolean
function AlyaMC.setSprinting(sprinting) end
---@return integer
function AlyaMC.getTicksExisted() end
---@return boolean
function AlyaMC.isForwardPressed() end
---@return boolean
function AlyaMC.isBackPressed() end
---@return boolean
function AlyaMC.isJumpPressed() end
---@return boolean
function AlyaMC.isSneakPressed() end
---@return boolean
function AlyaMC.isForwardDown() end
---@return boolean
function AlyaMC.isJumpDown() end
---@return number
function AlyaMC.getCameraYaw() end
---@param yaw number
function AlyaMC.setCameraYaw(yaw) end
---@return number
function AlyaMC.getCurrentTime() end
---@return number
function AlyaMC.getLastPosX() end
---@return number
function AlyaMC.getLastPosY() end
---@return number
function AlyaMC.getLastPosZ() end
---@return number
function AlyaMC.getFallDistance() end
---@param distance integer
function AlyaMC.setFallDistance(distance) end
---@return number
function AlyaMC.getHurtTime() end
---@return integer
function AlyaMC.getEntityId() end

---sets the player's step height (vanilla default is 0.5)
---@param height number
function AlyaMC.setStepHeight(height) end

---resets the player's step height to the vanilla default (0.5)
function AlyaMC.resetStepHeight() end

---starts or stops buffering outgoing packets instead of sending them
---@param enabled boolean
function AlyaMC.holdPackets(enabled) end

---sends all buffered packets immediately (bypasses event) then clears the buffer
function AlyaMC.flushPackets() end

---discards all buffered packets without sending them
function AlyaMC.clearPackets() end

---returns true if the player is submerged in water
---@return boolean
function AlyaMC.isInWater() end

---returns true if the block directly below the player is a liquid
---@return boolean
function AlyaMC.isOnLiquid() end

---returns true if any gui screen is currently open
---@return boolean
function AlyaMC.isGuiOpen() end

---returns the simple class name of the open gui, or "none"
---@return string
function AlyaMC.getGuiClass() end

---returns the LWJGL key code for a named keybind
---@param name "forward"|"back"|"left"|"right"|"sprint"|"sneak"|"jump"
---@return integer
function AlyaMC.getKeyCode(name) end

---sets the player's reach override distance
---@param reach number
function AlyaMC.setReach(reach) end

---resets the player's reach to the default game value
function AlyaMC.resetReach() end

---sets the entity hitbox expansion (added to all sides)
---@param expansion number
function AlyaMC.setHitboxExpansion(expansion) end

---resets entity hitbox expansion to default
function AlyaMC.resetHitboxExpansion() end

---@class AlyaFontRenderer
---@field drawString fun(text: string, x: number, y: number, color: integer)
---@field drawStringWithShadow fun(text: string, x: number, y: number, color: integer)
---@field getStringWidth fun(text: string): number
---@field getFontHeight fun(): number

---@class AlyaEntityPlayer
---@field id integer
---@field name string
---@field x number
---@field y number
---@field z number
---@field eyeHeight number
---@field health number
---@field hurtTime integer
---@field distance number
---@field isInvisible boolean

---@class AlyaRotation
---@field yaw number
---@field pitch number

---@class AlyaCombat

---returns nearby players as array of AlyaEntityPlayer, sorted by distance
---@param reach? number defaults to 6.0
---@param raycastOnly? boolean only include players in line of sight
---@return AlyaEntityPlayer[]
function AlyaCombat.getPlayers(reach, raycastOnly) end

---returns nearby living entities filtered by type, sorted by distance
---@param reach? number defaults to 6.0
---@param raycastOnly? boolean
---@param players? boolean include players (default true)
---@param hostile? boolean include hostile mobs (default true)
---@param passive? boolean include passive mobs (default true)
---@return AlyaEntityPlayer[]
function AlyaCombat.getEntities(reach, raycastOnly, players, hostile, passive) end

---returns yaw/pitch rotation from player eye to entity position
---@param entity AlyaEntityPlayer
---@return AlyaRotation
function AlyaCombat.getRotationToEntity(entity) end

---returns sensitivity multiplier used for snapping rotations to valid values
---@return number
function AlyaCombat.getSensitivityMultiplier() end

---swings item and sends attack packet for entity with given id
---@param entityId integer
---@return boolean success
function AlyaCombat.attackEntity(entityId) end

---swings the held item
function AlyaCombat.swingItem() end

---plays enchantment critical particle on entity
---@param entityId integer
function AlyaCombat.onEnchantmentCritical(entityId) end

---plays critical hit particle on entity
---@param entityId integer
function AlyaCombat.onCriticalHit(entityId) end

---sends C08PacketPlayerBlockPlacement to begin blocking with held sword
function AlyaCombat.sendBlockPlacement() end

---sends C07PacketPlayerDigging RELEASE_USE_ITEM to stop blocking
function AlyaCombat.sendReleaseUseItem() end

---returns true if player is holding a sword
---@return boolean
function AlyaCombat.isHoldingSword() end

---returns true if player swing animation is in progress
---@return boolean
function AlyaCombat.isSwingInProgress() end

---returns player hurt time
---@return integer
function AlyaCombat.getHurtTime() end

---returns true if the local player can see the entity (raycast)
---@param entityId integer
---@return boolean
function AlyaCombat.canSee(entityId) end

---returns true if name is in the friends list
---@param name string
---@return boolean
function AlyaCombat.isFriend(name) end

---adds name to friends list
---@param name string
function AlyaCombat.addFriend(name) end

---removes name from friends list
---@param name string
function AlyaCombat.removeFriend(name) end

---returns all friend names
---@return string[]
function AlyaCombat.getFriends() end

---sends C04PacketPlayerPosition with a y offset from current position
---@param offset number
---@param onGround boolean
function AlyaCombat.sendPositionPacket(offset, onGround) end

---resets leftClickCounter and calls clickMouse
function AlyaCombat.clickMouse() end

---returns true if the attack key (left mouse) is held
---@return boolean
function AlyaCombat.isAttackKeyDown() end

---returns true if the use item key (right mouse) is held
---@return boolean
function AlyaCombat.isUseKeyDown() end

---returns local player rotation yaw
---@return number
function AlyaCombat.getPlayerYaw() end

---returns local player rotation pitch
---@return number
function AlyaCombat.getPlayerPitch() end

---@class client
---@field modules AlyaModules
---@field events AlyaEvents
---@field commands AlyaCommands
---@field config AlyaConfig
---@field chat AlyaChat
---@field movement AlyaMovement
---@field visual AlyaRender
---@field timer AlyaTimer
---@field mc AlyaMC
---@field combat AlyaCombat
---@field getName fun(): string
---@field getVersion fun(): string
---@field reload fun()
---@field getFontRenderer fun(): AlyaFontRenderer
---@field getFontRendererSmall fun(): AlyaFontRenderer
---@field getFontRendererMedium fun(): AlyaFontRenderer
---@field getFontRendererBold fun(): AlyaFontRenderer
---@field getFontRendererTitle fun(): AlyaFontRenderer

---global client API table
---@type client
alya = {}

---loads and executes Lua script from classpath resource path, returns script's return value
---@param resourcePath string e.g. "/lua/modules/movement/flight/motion.lua"
---@return any
function loadScript(resourcePath) end

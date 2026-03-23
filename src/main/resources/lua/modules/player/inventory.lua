local moduleTable = alya.modules.register("Inventory", "Allows movement while GUIs are open", "PLAYER")
local inventoryToggle  = moduleTable.addBooleanSetting("Inventory", "", true)
local chestToggle      = moduleTable.addBooleanSetting("Chest", "", true)
local otherToggle      = moduleTable.addBooleanSetting("Other Containers", "", true)
local containerClasses = {
    GuiFurnace       = true,
    GuiBrewingStand  = true,
    GuiCrafting      = true,
    GuiDispenser     = true,
    GuiHopper        = true,
    GuiEnchantment   = true,
    GuiRepair        = true,
    GuiBeacon        = true,
    GuiMerchant      = true,
    GuiScreenHorseInventory = true,
}

local function isAllowed()
    if not alya.mc.isGuiOpen() then return false end
    local gui = alya.mc.getGuiClass()
    if gui == "GuiInventory"   and inventoryToggle.isEnabled() then return true end
    if gui == "GuiChest"       and chestToggle.isEnabled()     then return true end
    if containerClasses[gui]   and otherToggle.isEnabled()     then return true end
    return false
end

alya.events.on("playerinput", function(event)
    if not moduleTable.isEnabled() then return end
    if not isAllowed() then return end

    local forwardKey = alya.mc.getKeyCode("forward")
    local backKey    = alya.mc.getKeyCode("back")
    local leftKey    = alya.mc.getKeyCode("left")
    local rightKey   = alya.mc.getKeyCode("right")

    local fwd  = alya.mc.isKeyDown(forwardKey)  and  1 or 0
    local bwd  = alya.mc.isKeyDown(backKey)     and -1 or 0
    local lft  = alya.mc.isKeyDown(leftKey)     and  1 or 0
    local rgt  = alya.mc.isKeyDown(rightKey)    and -1 or 0

    if fwd ~= 0 or bwd ~= 0 or lft ~= 0 or rgt ~= 0 then
        event.setMoveForward(fwd + bwd)
        event.setMoveStrafe(lft + rgt)
    end
end)

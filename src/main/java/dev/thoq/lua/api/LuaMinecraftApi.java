package dev.thoq.lua.api;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.lwjgl.input.Keyboard;

public final class LuaMinecraftApi extends LuaTable {

    private static final Minecraft minecraft = Minecraft.getMinecraft();

    public LuaMinecraftApi() {
        set("getDebugFPS", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(Minecraft.getDebugFPS());
            }
        });

        set("isPlayerNull", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(minecraft.thePlayer == null);
            }
        });

        set("isWorldNull", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(minecraft.theWorld == null);
            }
        });

        set("getGamma", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf((double) minecraft.gameSettings.gammaSetting);
            }
        });

        set("setGamma", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue gammaValue) {
                minecraft.gameSettings.gammaSetting = (float) gammaValue.todouble();
                return LuaValue.NIL;
            }
        });

        set("getRightClickDelay", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(minecraft.rightClickDelayTimer);
            }
        });

        set("setRightClickDelay", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue delayValue) {
                minecraft.rightClickDelayTimer = delayValue.toint();
                return LuaValue.NIL;
            }
        });

        set("getTimerSpeed", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf((double) net.minecraft.util.Timer.timerSpeed);
            }
        });

        set("setTimerSpeed", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue speedValue) {
                net.minecraft.util.Timer.timerSpeed = (float) speedValue.todouble();
                return LuaValue.NIL;
            }
        });

        set("getWorldTime", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (minecraft.theWorld == null)
                    return LuaValue.valueOf(0L);
                return LuaValue.valueOf((double) minecraft.theWorld.getWorldTime());
            }
        });

        set("setWorldTime", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue timeValue) {
                if (minecraft.theWorld != null)
                    minecraft.theWorld.setWorldTime((long) timeValue.todouble());
                return LuaValue.NIL;
            }
        });

        set("getPlayerX", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return minecraft.thePlayer != null ? LuaValue.valueOf(minecraft.thePlayer.posX) : LuaValue.valueOf(0d);
            }
        });
        set("getPlayerY", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return minecraft.thePlayer != null ? LuaValue.valueOf(minecraft.thePlayer.posY) : LuaValue.valueOf(0d);
            }
        });
        set("getPlayerZ", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return minecraft.thePlayer != null ? LuaValue.valueOf(minecraft.thePlayer.posZ) : LuaValue.valueOf(0d);
            }
        });

        set("getMotionX", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return minecraft.thePlayer != null ? LuaValue.valueOf(minecraft.thePlayer.motionX)
                        : LuaValue.valueOf(0d);
            }
        });
        set("getMotionY", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return minecraft.thePlayer != null ? LuaValue.valueOf(minecraft.thePlayer.motionY)
                        : LuaValue.valueOf(0d);
            }
        });
        set("getMotionZ", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return minecraft.thePlayer != null ? LuaValue.valueOf(minecraft.thePlayer.motionZ)
                        : LuaValue.valueOf(0d);
            }
        });

        set("setMotionX", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue motionValue) {
                if (minecraft.thePlayer != null)
                    minecraft.thePlayer.motionX = motionValue.todouble();
                return LuaValue.NIL;
            }
        });
        set("setMotionY", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue motionValue) {
                if (minecraft.thePlayer != null)
                    minecraft.thePlayer.motionY = motionValue.todouble();
                return LuaValue.NIL;
            }
        });
        set("setMotionZ", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue motionValue) {
                if (minecraft.thePlayer != null)
                    minecraft.thePlayer.motionZ = motionValue.todouble();
                return LuaValue.NIL;
            }
        });

        set("isOnGround", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(minecraft.thePlayer != null && minecraft.thePlayer.onGround);
            }
        });

        set("getJumpTicks", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return minecraft.thePlayer != null ? LuaValue.valueOf(minecraft.thePlayer.jumpTicks)
                        : LuaValue.valueOf(0);
            }
        });

        set("setJumpTicks", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue ticksValue) {
                if (minecraft.thePlayer != null)
                    minecraft.thePlayer.jumpTicks = ticksValue.toint();
                return LuaValue.NIL;
            }
        });

        set("jump", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (minecraft.thePlayer != null)
                    minecraft.thePlayer.jump();
                return LuaValue.NIL;
            }
        });

        set("setSprinting", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue sprintingValue) {
                if (minecraft.thePlayer != null)
                    minecraft.thePlayer.setSprinting(sprintingValue.toboolean());
                return LuaValue.NIL;
            }
        });

        set("getTicksExisted", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return minecraft.thePlayer != null ? LuaValue.valueOf(minecraft.thePlayer.ticksExisted)
                        : LuaValue.valueOf(0);
            }
        });

        set("isForwardPressed", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(minecraft.gameSettings.keyBindForward.isKeyDown());
            }
        });
        set("isBackPressed", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(minecraft.gameSettings.keyBindBack.isKeyDown());
            }
        });
        set("isJumpPressed", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(minecraft.gameSettings.keyBindJump.isKeyDown());
            }
        });
        set("isSneakPressed", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(minecraft.gameSettings.keyBindSneak.isKeyDown());
            }
        });
        set("isForwardDown", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(minecraft.gameSettings.keyBindForward.pressed);
            }
        });
        set("isJumpDown", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(minecraft.gameSettings.keyBindJump.pressed);
            }
        });

        set("getCameraYaw", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return minecraft.thePlayer != null ? LuaValue.valueOf((double) minecraft.thePlayer.cameraYaw)
                        : LuaValue.valueOf(0d);
            }
        });
        set("setCameraYaw", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue yawValue) {
                if (minecraft.thePlayer != null)
                    minecraft.thePlayer.cameraYaw = (float) yawValue.todouble();
                return LuaValue.NIL;
            }
        });

        set("getPartialTicks", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf((double) minecraft.timer.renderPartialTicks);
            }
        });

        set("getCurrentTime", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf((double) System.currentTimeMillis());
            }
        });

        set("getLastPosX", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return minecraft.thePlayer != null ? LuaValue.valueOf(minecraft.thePlayer.lastTickPosX)
                        : LuaValue.valueOf(0d);
            }
        });
        set("getLastPosY", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return minecraft.thePlayer != null ? LuaValue.valueOf(minecraft.thePlayer.lastTickPosY)
                        : LuaValue.valueOf(0d);
            }
        });
        set("getLastPosZ", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return minecraft.thePlayer != null ? LuaValue.valueOf(minecraft.thePlayer.lastTickPosZ)
                        : LuaValue.valueOf(0d);
            }
        });
        set("getFallDistance", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return minecraft.thePlayer != null ? LuaValue.valueOf(minecraft.thePlayer.fallDistance) : LuaValue.valueOf(0D);
            }
        });
        set("setFallDistance", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue fallDistance) {
                if (minecraft.thePlayer != null)
                    minecraft.thePlayer.fallDistance = fallDistance.tofloat();
                return LuaValue.NIL;
            }
        });
        set("getHurtTime", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return minecraft.thePlayer != null ? LuaValue.valueOf(minecraft.thePlayer.hurtTime) : LuaValue.valueOf(0);
            }
        });

        set("getEntityId", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return minecraft.thePlayer != null ? LuaValue.valueOf(minecraft.thePlayer.getEntityId()) : LuaValue.valueOf(-1);
            }
        });

        set("setSneakPressed", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue v) {
                minecraft.gameSettings.keyBindSneak.pressed = v.toboolean();
                return LuaValue.NIL;
            }
        });

        set("isAboveVoid", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (minecraft.thePlayer == null || minecraft.theWorld == null) return LuaValue.FALSE;
                java.util.List<?> boxes = minecraft.theWorld.getCollidingBoundingBoxes(
                    minecraft.thePlayer,
                    minecraft.thePlayer.getEntityBoundingBox().offset(0, -1.4, 0).contract(0.2, 0, 0.2)
                );
                return LuaValue.valueOf(boxes.isEmpty());
            }
        });

        set("isOnLadder", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(minecraft.thePlayer != null && minecraft.thePlayer.isOnLadder());
            }
        });

        set("isKeyDown", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue keyCode) {
                return LuaValue.valueOf(Keyboard.isKeyDown(keyCode.toint()));
            }
        });

        set("getCameraPitch", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return minecraft.thePlayer != null ? LuaValue.valueOf((double) minecraft.thePlayer.rotationPitch)
                        : LuaValue.valueOf(0d);
            }
        });

        set("isHoldingBlock", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (minecraft.thePlayer == null) return LuaValue.FALSE;
                ItemStack held = minecraft.thePlayer.getHeldItem();
                return LuaValue.valueOf(held != null && held.getItem() instanceof ItemBlock);
            }
        });

        set("isOnEdge", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if (minecraft.thePlayer == null || minecraft.theWorld == null) return LuaValue.FALSE;
                double x = minecraft.thePlayer.posX;
                double z = minecraft.thePlayer.posZ;
                double blockX = Math.floor(x);
                double blockZ = Math.floor(z);
                double fracX = x - blockX;
                double fracZ = z - blockZ;
                double edgeThreshold = 0.3;
                boolean nearEdge = fracX < edgeThreshold || fracX > (1.0 - edgeThreshold)
                        || fracZ < edgeThreshold || fracZ > (1.0 - edgeThreshold);
                return LuaValue.valueOf(nearEdge);
            }
        });
    }


}

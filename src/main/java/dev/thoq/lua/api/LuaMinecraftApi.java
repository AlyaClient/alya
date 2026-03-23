package dev.thoq.lua.api;

import dev.thoq.event.events.HitboxEvent;
import dev.thoq.event.events.PacketSendEvent;
import dev.thoq.event.events.ReachEvent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.lwjgl.input.Keyboard;

public final class LuaMinecraftApi extends LuaTable {
  private static final Minecraft minecraft = Minecraft.getMinecraft();
  private static float reachOverride = -1.0f;
  private static float hitboxExpansion = -1.0f;
  private static boolean eventsRegistered = false;
  private static boolean holdingPackets = false;
  private static final List<Packet<?>> packetQueue = new ArrayList<>();

  public static void registerEvents(dev.thoq.event.EventBus eventBus) {
    if (!eventsRegistered) {
      eventsRegistered = true;
      eventBus.subscribe(
          ReachEvent.class,
          event -> {
            if (reachOverride > 0) event.setReachDistance(reachOverride);
          });
      eventBus.subscribe(
          HitboxEvent.class,
          event -> {
            if (hitboxExpansion >= 0) event.setExpansion(hitboxExpansion);
          });
      eventBus.subscribe(
          PacketSendEvent.class,
          event -> {
            if (holdingPackets) {
              packetQueue.add(event.getPacket());
              event.cancel();
            }
          });
    }
  }

  public LuaMinecraftApi() {
    set(
        "getDebugFPS",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(Minecraft.getDebugFPS());
          }
        });
    set(
        "isPlayerNull",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(minecraft.thePlayer == null);
          }
        });
    set(
        "isWorldNull",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(minecraft.theWorld == null);
          }
        });
    set(
        "getGamma",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf((double) minecraft.gameSettings.gammaSetting);
          }
        });
    set(
        "setGamma",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue gammaValue) {
            minecraft.gameSettings.gammaSetting = (float) gammaValue.todouble();
            return LuaValue.NIL;
          }
        });
    set(
        "getRightClickDelay",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(minecraft.rightClickDelayTimer);
          }
        });
    set(
        "setRightClickDelay",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue delayValue) {
            minecraft.rightClickDelayTimer = delayValue.toint();
            return LuaValue.NIL;
          }
        });
    set(
        "getTimerSpeed",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf((double) net.minecraft.util.Timer.timerSpeed);
          }
        });
    set(
        "setTimerSpeed",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue speedValue) {
            net.minecraft.util.Timer.timerSpeed = (float) speedValue.todouble();
            return LuaValue.NIL;
          }
        });
    set(
        "getWorldTime",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            if (minecraft.theWorld == null) return LuaValue.valueOf(0L);
            return LuaValue.valueOf((double) minecraft.theWorld.getWorldTime());
          }
        });
    set(
        "setWorldTime",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue timeValue) {
            if (minecraft.theWorld != null)
              minecraft.theWorld.setWorldTime((long) timeValue.todouble());
            return LuaValue.NIL;
          }
        });
    set(
        "getPlayerX",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return minecraft.thePlayer != null
                ? LuaValue.valueOf(minecraft.thePlayer.posX)
                : LuaValue.valueOf(0d);
          }
        });
    set(
        "getPlayerY",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return minecraft.thePlayer != null
                ? LuaValue.valueOf(minecraft.thePlayer.posY)
                : LuaValue.valueOf(0d);
          }
        });
    set(
        "getPlayerZ",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return minecraft.thePlayer != null
                ? LuaValue.valueOf(minecraft.thePlayer.posZ)
                : LuaValue.valueOf(0d);
          }
        });
    set(
        "getMotionX",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return minecraft.thePlayer != null
                ? LuaValue.valueOf(minecraft.thePlayer.motionX)
                : LuaValue.valueOf(0d);
          }
        });
    set(
        "getMotionY",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return minecraft.thePlayer != null
                ? LuaValue.valueOf(minecraft.thePlayer.motionY)
                : LuaValue.valueOf(0d);
          }
        });
    set(
        "getMotionZ",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return minecraft.thePlayer != null
                ? LuaValue.valueOf(minecraft.thePlayer.motionZ)
                : LuaValue.valueOf(0d);
          }
        });
    set(
        "setMotionX",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue motionValue) {
            if (minecraft.thePlayer != null) minecraft.thePlayer.motionX = motionValue.todouble();
            return LuaValue.NIL;
          }
        });
    set(
        "setMotionY",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue motionValue) {
            if (minecraft.thePlayer != null) minecraft.thePlayer.motionY = motionValue.todouble();
            return LuaValue.NIL;
          }
        });
    set(
        "setMotionZ",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue motionValue) {
            if (minecraft.thePlayer != null) minecraft.thePlayer.motionZ = motionValue.todouble();
            return LuaValue.NIL;
          }
        });
    set(
        "isOnGround",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(minecraft.thePlayer != null && minecraft.thePlayer.onGround);
          }
        });
    set(
        "getJumpTicks",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return minecraft.thePlayer != null
                ? LuaValue.valueOf(minecraft.thePlayer.jumpTicks)
                : LuaValue.valueOf(0);
          }
        });
    set(
        "setJumpTicks",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue ticksValue) {
            if (minecraft.thePlayer != null) minecraft.thePlayer.jumpTicks = ticksValue.toint();
            return LuaValue.NIL;
          }
        });
    set(
        "jump",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            if (minecraft.thePlayer != null) minecraft.thePlayer.jump();
            return LuaValue.NIL;
          }
        });
    set(
        "setSprinting",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue sprintingValue) {
            if (minecraft.thePlayer != null)
              minecraft.thePlayer.setSprinting(sprintingValue.toboolean());
            return LuaValue.NIL;
          }
        });
    set(
        "getTicksExisted",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return minecraft.thePlayer != null
                ? LuaValue.valueOf(minecraft.thePlayer.ticksExisted)
                : LuaValue.valueOf(0);
          }
        });
    set(
        "isForwardPressed",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(minecraft.gameSettings.keyBindForward.isKeyDown());
          }
        });
    set(
        "isBackPressed",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(minecraft.gameSettings.keyBindBack.isKeyDown());
          }
        });
    set(
        "isJumpPressed",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(minecraft.gameSettings.keyBindJump.isKeyDown());
          }
        });
    set(
        "isSneakPressed",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(minecraft.gameSettings.keyBindSneak.isKeyDown());
          }
        });
    set(
        "isForwardDown",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(minecraft.gameSettings.keyBindForward.pressed);
          }
        });
    set(
        "isJumpDown",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(minecraft.gameSettings.keyBindJump.pressed);
          }
        });
    set(
        "getCameraYaw",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return minecraft.thePlayer != null
                ? LuaValue.valueOf((double) minecraft.thePlayer.cameraYaw)
                : LuaValue.valueOf(0d);
          }
        });
    set(
        "setCameraYaw",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue yawValue) {
            if (minecraft.thePlayer != null)
              minecraft.thePlayer.cameraYaw = (float) yawValue.todouble();
            return LuaValue.NIL;
          }
        });
    set(
        "getPartialTicks",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf((double) minecraft.timer.renderPartialTicks);
          }
        });
    set(
        "getCurrentTime",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf((double) System.currentTimeMillis());
          }
        });
    set(
        "getLastPosX",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return minecraft.thePlayer != null
                ? LuaValue.valueOf(minecraft.thePlayer.lastTickPosX)
                : LuaValue.valueOf(0d);
          }
        });
    set(
        "getLastPosY",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return minecraft.thePlayer != null
                ? LuaValue.valueOf(minecraft.thePlayer.lastTickPosY)
                : LuaValue.valueOf(0d);
          }
        });
    set(
        "getLastPosZ",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return minecraft.thePlayer != null
                ? LuaValue.valueOf(minecraft.thePlayer.lastTickPosZ)
                : LuaValue.valueOf(0d);
          }
        });
    set(
        "getFallDistance",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return minecraft.thePlayer != null
                ? LuaValue.valueOf(minecraft.thePlayer.fallDistance)
                : LuaValue.valueOf(0D);
          }
        });
    set(
        "setFallDistance",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue fallDistance) {
            if (minecraft.thePlayer != null)
              minecraft.thePlayer.fallDistance = fallDistance.tofloat();
            return LuaValue.NIL;
          }
        });
    set(
        "getHurtTime",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return minecraft.thePlayer != null
                ? LuaValue.valueOf(minecraft.thePlayer.hurtTime)
                : LuaValue.valueOf(0);
          }
        });
    set(
        "getEntityId",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return minecraft.thePlayer != null
                ? LuaValue.valueOf(minecraft.thePlayer.getEntityId())
                : LuaValue.valueOf(-1);
          }
        });
    set(
        "setSneakPressed",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue v) {
            minecraft.gameSettings.keyBindSneak.pressed = v.toboolean();
            return LuaValue.NIL;
          }
        });
    set(
        "isAboveVoid",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            if (minecraft.thePlayer == null || minecraft.theWorld == null) return LuaValue.FALSE;
            java.util.List<?> boxes =
                minecraft.theWorld.getCollidingBoundingBoxes(
                    minecraft.thePlayer,
                    minecraft
                        .thePlayer
                        .getEntityBoundingBox()
                        .offset(0, -1.4, 0)
                        .contract(0.2, 0, 0.2));
            return LuaValue.valueOf(boxes.isEmpty());
          }
        });
    set(
        "isOnLadder",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(
                minecraft.thePlayer != null && minecraft.thePlayer.isOnLadder());
          }
        });
    set(
        "isKeyDown",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue keyCode) {
            return LuaValue.valueOf(Keyboard.isKeyDown(keyCode.toint()));
          }
        });
    set(
        "getCameraPitch",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return minecraft.thePlayer != null
                ? LuaValue.valueOf((double) minecraft.thePlayer.rotationPitch)
                : LuaValue.valueOf(0d);
          }
        });
    set(
        "isHoldingBlock",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            if (minecraft.thePlayer == null) return LuaValue.FALSE;
            ItemStack held = minecraft.thePlayer.getHeldItem();
            return LuaValue.valueOf(held != null && held.getItem() instanceof ItemBlock);
          }
        });
    set(
        "setStepHeight",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue v) {
            if (minecraft.thePlayer != null) minecraft.thePlayer.stepHeight = (float) v.todouble();
            return LuaValue.NIL;
          }
        });
    set(
        "resetStepHeight",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            if (minecraft.thePlayer != null) minecraft.thePlayer.stepHeight = 0.5f;
            return LuaValue.NIL;
          }
        });
    set(
        "holdPackets",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue v) {
            holdingPackets = v.toboolean();
            return LuaValue.NIL;
          }
        });
    set(
        "flushPackets",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            if (minecraft.thePlayer != null) {
              for (Packet<?> packet : packetQueue) {
                minecraft.thePlayer.sendQueue.addToSendQueueNoEvent(packet);
              }
            }
            packetQueue.clear();
            return LuaValue.NIL;
          }
        });
    set(
        "clearPackets",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            packetQueue.clear();
            return LuaValue.NIL;
          }
        });
    set(
        "isInWater",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(minecraft.thePlayer != null && minecraft.thePlayer.isInWater());
          }
        });
    set(
        "isOnLiquid",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            if (minecraft.thePlayer == null || minecraft.theWorld == null) return LuaValue.FALSE;
            BlockPos pos =
                new BlockPos(
                    minecraft.thePlayer.posX,
                    minecraft.thePlayer.getEntityBoundingBox().minY - 0.1,
                    minecraft.thePlayer.posZ);
            return LuaValue.valueOf(
                minecraft.theWorld.getBlockState(pos).getBlock() instanceof BlockLiquid);
          }
        });
    set(
        "isGuiOpen",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(minecraft.currentScreen != null);
          }
        });
    set(
        "getGuiClass",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            if (minecraft.currentScreen == null) return LuaValue.valueOf("none");
            return LuaValue.valueOf(minecraft.currentScreen.getClass().getSimpleName());
          }
        });
    set(
        "getKeyCode",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue name) {
            switch (name.tojstring()) {
              case "forward":
                return LuaValue.valueOf(minecraft.gameSettings.keyBindForward.getKeyCode());
              case "back":
                return LuaValue.valueOf(minecraft.gameSettings.keyBindBack.getKeyCode());
              case "left":
                return LuaValue.valueOf(minecraft.gameSettings.keyBindLeft.getKeyCode());
              case "right":
                return LuaValue.valueOf(minecraft.gameSettings.keyBindRight.getKeyCode());
              case "sprint":
                return LuaValue.valueOf(minecraft.gameSettings.keyBindSprint.getKeyCode());
              case "sneak":
                return LuaValue.valueOf(minecraft.gameSettings.keyBindSneak.getKeyCode());
              case "jump":
                return LuaValue.valueOf(minecraft.gameSettings.keyBindJump.getKeyCode());
              default:
                return LuaValue.valueOf(-1);
            }
          }
        });
    set(
        "setReach",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue v) {
            reachOverride = (float) v.todouble();
            return LuaValue.NIL;
          }
        });
    set(
        "resetReach",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            reachOverride = -1.0f;
            return LuaValue.NIL;
          }
        });
    set(
        "setHitboxExpansion",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue v) {
            hitboxExpansion = (float) v.todouble();
            return LuaValue.NIL;
          }
        });
    set(
        "resetHitboxExpansion",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            hitboxExpansion = -1.0f;
            return LuaValue.NIL;
          }
        });
    set(
        "isOnEdge",
        new ZeroArgFunction() {
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
            boolean nearEdge =
                fracX < edgeThreshold
                    || fracX > (1.0 - edgeThreshold)
                    || fracZ < edgeThreshold
                    || fracZ > (1.0 - edgeThreshold);
            return LuaValue.valueOf(nearEdge);
          }
        });
    set(
        "getHotbarBlockSlot",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            if (minecraft.thePlayer == null) return LuaValue.valueOf(-1);
            for (int slotIndex = 0; slotIndex < 9; slotIndex++) {
              ItemStack itemStack = minecraft.thePlayer.inventory.getStackInSlot(slotIndex);
              if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                return LuaValue.valueOf(slotIndex);
              }
            }
            return LuaValue.valueOf(-1);
          }
        });
    set(
        "getHotbarSlot",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            if (minecraft.thePlayer == null) return LuaValue.valueOf(-1);
            return LuaValue.valueOf(minecraft.thePlayer.inventory.currentItem);
          }
        });
    set(
        "sendHeldItemChange",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue slotValue) {
            if (minecraft.thePlayer != null) {
              minecraft
                  .getNetHandler()
                  .addToSendQueueNoEvent(
                      new net.minecraft.network.play.client.C09PacketHeldItemChange(
                          slotValue.toint()));
            }
            return LuaValue.NIL;
          }
        });
    set(
        "sendAnimation",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            if (minecraft.thePlayer != null) {
              minecraft
                  .getNetHandler()
                  .addToSendQueue(new net.minecraft.network.play.client.C0APacketAnimation());
            }
            return LuaValue.NIL;
          }
        });
    set(
        "isBlockAir",
        new org.luaj.vm2.lib.ThreeArgFunction() {
          @Override
          public LuaValue call(LuaValue xValue, LuaValue yValue, LuaValue zValue) {
            if (minecraft.theWorld != null) {
              BlockPos blockPos = new BlockPos(xValue.toint(), yValue.toint(), zValue.toint());
              return LuaValue.valueOf(
                  minecraft.theWorld.getBlockState(blockPos).getBlock()
                      instanceof net.minecraft.block.BlockAir);
            }
            return LuaValue.FALSE;
          }
        });
    set(
        "isBlockSolid",
        new org.luaj.vm2.lib.ThreeArgFunction() {
          @Override
          public LuaValue call(LuaValue xValue, LuaValue yValue, LuaValue zValue) {
            if (minecraft.theWorld != null) {
              BlockPos blockPos = new BlockPos(xValue.toint(), yValue.toint(), zValue.toint());
              net.minecraft.block.material.Material material =
                  minecraft.theWorld.getBlockState(blockPos).getBlock().getMaterial();
              return LuaValue.valueOf(material.isSolid() && !material.isLiquid());
            }
            return LuaValue.FALSE;
          }
        });
    set(
        "isBlockLiquid",
        new org.luaj.vm2.lib.ThreeArgFunction() {
          @Override
          public LuaValue call(LuaValue xValue, LuaValue yValue, LuaValue zValue) {
            if (minecraft.theWorld != null) {
              BlockPos blockPos = new BlockPos(xValue.toint(), yValue.toint(), zValue.toint());
              return LuaValue.valueOf(
                  minecraft.theWorld.getBlockState(blockPos).getBlock().getMaterial().isLiquid());
            }
            return LuaValue.FALSE;
          }
        });
    set(
        "rightClickBlock",
        new org.luaj.vm2.lib.VarArgFunction() {
          @Override
          public org.luaj.vm2.Varargs invoke(org.luaj.vm2.Varargs arguments) {
            if (minecraft.thePlayer != null && minecraft.theWorld != null) {
              int slotIndex = arguments.checkint(1);
              int blockX = arguments.checkint(2);
              int blockY = arguments.checkint(3);
              int blockZ = arguments.checkint(4);
              int facingX = arguments.checkint(5);
              int facingY = arguments.checkint(6);
              int facingZ = arguments.checkint(7);
              double hitX = arguments.checkdouble(8);
              double hitY = arguments.checkdouble(9);
              double hitZ = arguments.checkdouble(10);
              ItemStack itemStack = minecraft.thePlayer.inventory.getStackInSlot(slotIndex);
              BlockPos blockPos = new BlockPos(blockX, blockY, blockZ);
              net.minecraft.util.EnumFacing enumFacing =
                  net.minecraft.util.EnumFacing.getFacingFromVector(
                      (float) facingX, (float) facingY, (float) facingZ);
              if (enumFacing == null) {
                enumFacing = net.minecraft.util.EnumFacing.UP;
              }
              net.minecraft.util.Vec3 hitVec = new net.minecraft.util.Vec3(hitX, hitY, hitZ);
              boolean success =
                  minecraft.playerController.onPlayerRightClick(
                      minecraft.thePlayer,
                      minecraft.theWorld,
                      itemStack,
                      blockPos,
                      enumFacing,
                      hitVec);
              return LuaValue.valueOf(success);
            }
            return LuaValue.FALSE;
          }
        });
    set(
        "raycastBlock",
        new org.luaj.vm2.lib.ThreeArgFunction() {
          @Override
          public LuaValue call(LuaValue yawValue, LuaValue pitchValue, LuaValue rangeValue) {
            if (minecraft.theWorld != null && minecraft.thePlayer != null) {
              float yawFloat = (float) yawValue.todouble();
              float pitchFloat = (float) pitchValue.todouble();
              double rangeDouble = rangeValue.todouble();
              net.minecraft.util.Vec3 eyePosition = minecraft.thePlayer.getPositionEyes(1.0f);
              float f1 =
                  net.minecraft.util.MathHelper.cos(-yawFloat * 0.017453292F - (float) Math.PI);
              float f2 =
                  net.minecraft.util.MathHelper.sin(-yawFloat * 0.017453292F - (float) Math.PI);
              float f3 = -net.minecraft.util.MathHelper.cos(-pitchFloat * 0.017453292F);
              float f4 = net.minecraft.util.MathHelper.sin(-pitchFloat * 0.017453292F);
              net.minecraft.util.Vec3 lookPosition =
                  new net.minecraft.util.Vec3(f2 * f3, f4, f1 * f3);
              net.minecraft.util.Vec3 endPosition =
                  eyePosition.addVector(
                      lookPosition.xCoord * rangeDouble,
                      lookPosition.yCoord * rangeDouble,
                      lookPosition.zCoord * rangeDouble);
              net.minecraft.util.MovingObjectPosition movingObjectPosition =
                  minecraft.theWorld.rayTraceBlocks(eyePosition, endPosition, false, false, true);
              if (movingObjectPosition != null
                  && movingObjectPosition.typeOfHit
                      == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK) {
                LuaTable resultTable = new LuaTable();
                resultTable.set("x", LuaValue.valueOf(movingObjectPosition.getBlockPos().getX()));
                resultTable.set("y", LuaValue.valueOf(movingObjectPosition.getBlockPos().getY()));
                resultTable.set("z", LuaValue.valueOf(movingObjectPosition.getBlockPos().getZ()));
                return resultTable;
              }
            }
            return LuaValue.NIL;
          }
        });
  }
}

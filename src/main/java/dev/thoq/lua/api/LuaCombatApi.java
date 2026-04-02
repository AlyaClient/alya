package dev.thoq.lua.api;

import dev.thoq.util.IUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.*;

public final class LuaCombatApi extends LuaTable implements IUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final Set<String> friends = new HashSet<>();
    public static boolean isForcedBlocking = false;

    public LuaCombatApi() {
        set(
                "setForcedBlocking",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue v) {
                        isForcedBlocking = v.toboolean();
                        return LuaValue.NIL;
                    }
                });

        set(
                "isForcedBlocking",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf(isForcedBlocking);
                    }
                });

        set(
                "getPlayers",
                new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        if(mc.theWorld == null || mc.thePlayer == null) return LuaValue.NIL;
                        double reach = args.narg() >= 1 ? args.checkdouble(1) : 6.0;
                        boolean raycastOnly = args.narg() >= 2 && args.checkboolean(2);
                        List<EntityPlayer> players = new ArrayList<>();
                        for(Object obj : mc.theWorld.getLoadedEntityList()) {
                            if(!(obj instanceof EntityPlayer)) continue;
                            EntityPlayer ep = (EntityPlayer) obj;
                            if(ep == mc.thePlayer) continue;
                            if(ep.isInvisible()) continue;
                            if(mc.thePlayer.getDistanceToEntity(ep) > reach) continue;
                            if(raycastOnly && !mc.thePlayer.canEntityBeSeen(ep)) continue;
                            players.add(ep);
                        }
                        players.sort(Comparator.comparingDouble(e -> mc.thePlayer.getDistanceToEntity(e)));
                        LuaTable result = new LuaTable();
                        for(int i = 0; i < players.size(); i++) {
                            result.set(i + 1, entityToTable(players.get(i)));
                        }
                        return result;
                    }
                });
        set(
                "getAllPlayers",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        if(mc.theWorld == null || mc.thePlayer == null) return new LuaTable();
                        LuaTable result = new LuaTable();
                        int i = 1;
                        for(Object obj : mc.theWorld.getLoadedEntityList()) {
                            if(!(obj instanceof EntityPlayer)) continue;
                            EntityPlayer ep = (EntityPlayer) obj;
                            if(ep == mc.thePlayer) continue;
                            LuaTable t = new LuaTable();
                            t.set("id", LuaValue.valueOf(ep.getEntityId()));
                            t.set("name", LuaValue.valueOf(ep.getName()));
                            t.set("x", LuaValue.valueOf(ep.posX));
                            t.set("y", LuaValue.valueOf(ep.posY));
                            t.set("z", LuaValue.valueOf(ep.posZ));
                            t.set("lastX", LuaValue.valueOf(ep.lastTickPosX));
                            t.set("lastY", LuaValue.valueOf(ep.lastTickPosY));
                            t.set("lastZ", LuaValue.valueOf(ep.lastTickPosZ));
                            t.set("width", LuaValue.valueOf((double) ep.width));
                            t.set("height", LuaValue.valueOf((double) ep.height));
                            t.set("health", LuaValue.valueOf((double) ep.getHealth()));
                            t.set("maxHealth", LuaValue.valueOf((double) ep.getMaxHealth()));
                            t.set("hurtTime", LuaValue.valueOf(ep.hurtTime));
                            t.set("isInvisible", LuaValue.valueOf(ep.isInvisible()));
                            result.set(i++, t);
                        }
                        return result;
                    }
                });
        set(
                "getEntities",
                new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        if(mc.theWorld == null || mc.thePlayer == null) return new LuaTable();
                        double reach = args.narg() >= 1 ? args.checkdouble(1) : 6.0;
                        boolean raycastOnly = args.narg() >= 2 && args.checkboolean(2);
                        boolean players = args.narg() < 3 || args.checkboolean(3);
                        boolean hostile = args.narg() < 4 || args.checkboolean(4);
                        boolean passive = args.narg() < 5 || args.checkboolean(5);
                        List<EntityLivingBase> targets = new ArrayList<>();
                        for(Object obj : mc.theWorld.getLoadedEntityList()) {
                            if(!(obj instanceof EntityLivingBase)) continue;
                            EntityLivingBase e = (EntityLivingBase) obj;
                            if(e == mc.thePlayer) continue;
                            if(e.isInvisible()) continue;
                            if(mc.thePlayer.getDistanceToEntity(e) > reach) continue;
                            if(raycastOnly && !mc.thePlayer.canEntityBeSeen(e)) continue;
                            boolean isPlayer = e instanceof EntityPlayer;
                            boolean isHostile = e instanceof IMob;
                            boolean isPassive = e instanceof IAnimals && !isHostile;
                            if(isPlayer && !players) continue;
                            if(isHostile && !hostile) continue;
                            if(isPassive && !passive) continue;
                            if(!isPlayer && !isHostile && !isPassive) continue;
                            targets.add(e);
                        }
                        targets.sort(Comparator.comparingDouble(e -> mc.thePlayer.getDistanceToEntity(e)));
                        LuaTable result = new LuaTable();
                        for(int i = 0; i < targets.size(); i++) {
                            result.set(i + 1, livingToTable(targets.get(i)));
                        }
                        return result;
                    }
                });
        set(
                "getRotationToEntity",
                new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        if(mc.thePlayer == null) return LuaValue.NIL;
                        LuaTable entityTable = args.checktable(1);
                        double ex = entityTable.get("x").todouble();
                        double ey = entityTable.get("y").todouble();
                        double ez = entityTable.get("z").todouble();
                        double eyeOffset = entityTable.get("eyeHeight").todouble();
                        double dx = ex - mc.thePlayer.posX;
                        double dy = (ey + eyeOffset) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
                        double dz = ez - mc.thePlayer.posZ;
                        double dist = Math.sqrt(dx * dx + dz * dz);
                        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
                        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
                        LuaTable rot = new LuaTable();
                        rot.set("yaw", LuaValue.valueOf((double) yaw));
                        rot.set("pitch", LuaValue.valueOf((double) pitch));
                        return rot;
                    }
                });
        set(
                "getSensitivityMultiplier",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        float sens = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
                        return LuaValue.valueOf((double) (sens * sens * sens * 8.0f));
                    }
                });
        set(
                "attackEntity",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue entityIdValue) {
                        if(mc.theWorld == null || mc.thePlayer == null) return LuaValue.NIL;
                        int id = entityIdValue.toint();
                        Entity entity = mc.theWorld.getEntityByID(id);
                        if(entity == null) return LuaValue.FALSE;
                        mc.thePlayer.swingItem();
                        mc.getNetHandler()
                                .addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                        return LuaValue.TRUE;
                    }
                });
        set(
                "swingItem",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        if(mc.thePlayer != null) mc.thePlayer.swingItem();
                        return LuaValue.NIL;
                    }
                });
        set(
                "onEnchantmentCritical",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue entityIdValue) {
                        if(mc.theWorld == null || mc.thePlayer == null) return LuaValue.NIL;
                        Entity entity = mc.theWorld.getEntityByID(entityIdValue.toint());
                        if(entity instanceof EntityLivingBase) mc.thePlayer.onEnchantmentCritical(entity);
                        return LuaValue.NIL;
                    }
                });
        set(
                "onCriticalHit",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue entityIdValue) {
                        if(mc.theWorld == null || mc.thePlayer == null) return LuaValue.NIL;
                        Entity entity = mc.theWorld.getEntityByID(entityIdValue.toint());
                        if(entity instanceof EntityLivingBase) mc.thePlayer.onCriticalHit(entity);
                        return LuaValue.NIL;
                    }
                });
        set(
                "sendBlockPlacement",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        if(mc.thePlayer == null) return LuaValue.NIL;
                        mc.getNetHandler()
                                .addToSendQueue(
                                        new C08PacketPlayerBlockPlacement(
                                                new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
                        return LuaValue.NIL;
                    }
                });
        set(
                "sendReleaseUseItem",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        if(mc.thePlayer == null) return LuaValue.NIL;
                        mc.getNetHandler()
                                .addToSendQueue(
                                        new C07PacketPlayerDigging(
                                                C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                                                new BlockPos(0, 0, 0),
                                                EnumFacing.DOWN));
                        return LuaValue.NIL;
                    }
                });
        set(
                "isHoldingSword",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        if(mc.thePlayer == null) return LuaValue.FALSE;
                        return LuaValue.valueOf(
                                mc.thePlayer.getHeldItem() != null
                                        && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword);
                    }
                });
        set(
                "isSwingInProgress",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf(mc.thePlayer != null && mc.thePlayer.isSwingInProgress);
                    }
                });
        set(
                "getHurtTime",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return mc.thePlayer != null
                                ? LuaValue.valueOf(mc.thePlayer.hurtTime)
                                : LuaValue.valueOf(0);
                    }
                });
        set(
                "canSee",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue entityIdValue) {
                        if(mc.theWorld == null || mc.thePlayer == null) return LuaValue.FALSE;
                        Entity entity = mc.theWorld.getEntityByID(entityIdValue.toint());
                        if(entity == null) return LuaValue.FALSE;
                        return LuaValue.valueOf(mc.thePlayer.canEntityBeSeen(entity));
                    }
                });
        set(
                "isFriend",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue nameValue) {
                        return LuaValue.valueOf(friends.contains(nameValue.tojstring().toLowerCase()));
                    }
                });
        set(
                "addFriend",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue nameValue) {
                        friends.add(nameValue.tojstring().toLowerCase());
                        return LuaValue.NIL;
                    }
                });
        set(
                "removeFriend",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue nameValue) {
                        friends.remove(nameValue.tojstring().toLowerCase());
                        return LuaValue.NIL;
                    }
                });
        set(
                "getFriends",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        LuaTable t = new LuaTable();
                        int i = 1;
                        for(String f : friends) {
                            t.set(i++, LuaValue.valueOf(f));
                        }
                        return t;
                    }
                });
        set(
                "sendPositionPacket",
                new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        if(mc.thePlayer == null) return LuaValue.NIL;
                        double x = mc.thePlayer.posX;
                        double y = mc.thePlayer.posY;
                        double z = mc.thePlayer.posZ;
                        double offset = args.checkdouble(1);
                        boolean onGround = args.narg() >= 2 && args.checkboolean(2);
                        mc.getNetHandler()
                                .addToSendQueue(
                                        new C03PacketPlayer.C04PacketPlayerPosition(x, y + offset, z, onGround));
                        return LuaValue.NIL;
                    }
                });
        set(
                "clickMouse",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        if(mc.thePlayer == null) return LuaValue.NIL;
                        mc.leftClickCounter = 0;
                        mc.clickMouse();
                        return LuaValue.NIL;
                    }
                });
        set(
                "rightClickMouse",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        if(mc.thePlayer == null) return LuaValue.NIL;
                        try {
                            java.lang.reflect.Method m =
                                    net.minecraft.client.Minecraft.class.getDeclaredMethod("rightClickMouse");
                            m.setAccessible(true);
                            m.invoke(mc);
                        } catch(Exception ignored) {
                        }
                        return LuaValue.NIL;
                    }
                });
        set(
                "isAttackKeyDown",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf(mc.gameSettings.keyBindAttack.isKeyDown());
                    }
                });
        set(
                "isUseKeyDown",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf(mc.gameSettings.keyBindUseItem.isKeyDown());
                    }
                });
        set(
                "getPlayerYaw",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return mc.thePlayer != null
                                ? LuaValue.valueOf((double) mc.thePlayer.rotationYaw)
                                : LuaValue.valueOf(0d);
                    }
                });
        set(
                "getPlayerPitch",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return mc.thePlayer != null
                                ? LuaValue.valueOf((double) mc.thePlayer.rotationPitch)
                                : LuaValue.valueOf(0d);
                    }
                });
        set(
                "setPlayerPitch",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue v) {
                        if(mc.thePlayer != null) mc.thePlayer.rotationPitch = (float) v.todouble();
                        return LuaValue.NIL;
                    }
                });
        set(
                "setPlayerYaw",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue v) {
                        if(mc.thePlayer != null) mc.thePlayer.rotationYaw = (float) v.todouble();
                        return LuaValue.NIL;
                    }
                });
        set(
                "setCameraPitch",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue v) {
                        if(mc.thePlayer != null) mc.thePlayer.cameraPitch = (float) v.todouble();
                        return LuaValue.NIL;
                    }
                });
        set(
                "setPlayerHurtTime",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue v) {
                        if(mc.thePlayer != null) mc.thePlayer.hurtTime = v.toint();
                        return LuaValue.NIL;
                    }
                });
        set(
                "setClientRotation",
                new VarArgFunction() {
                    @Override
                    public Varargs invoke(Varargs args) {
                        if(mc.thePlayer == null) return LuaValue.NIL;
                        float yaw = (float) args.checkdouble(1);
                        float pitch = (float) args.checkdouble(2);
                        mc.thePlayer.renderYawOffset = yaw;
                        mc.thePlayer.rotationYawHead = yaw;
                        mc.thePlayer.rotationPitch = pitch;
                        return LuaValue.NIL;
                    }
                });
        set(
                "getEntityById",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue idValue) {
                        if(mc.theWorld == null || mc.thePlayer == null) return LuaValue.NIL;
                        Entity entity = mc.theWorld.getEntityByID(idValue.toint());
                        if(!(entity instanceof EntityLivingBase)) return LuaValue.NIL;
                        return livingToTable((EntityLivingBase) entity);
                    }
                });
        set(
                "setMoveForward",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue v) {
                        if(mc.thePlayer != null) mc.thePlayer.moveForward = (float) v.todouble();
                        return LuaValue.NIL;
                    }
                });
        set(
                "setMoveStrafing",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue v) {
                        if(mc.thePlayer != null) mc.thePlayer.moveStrafing = (float) v.todouble();
                        return LuaValue.NIL;
                    }
                });
        set(
                "getHotbarItemName",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue slotValue) {
                        if(mc.thePlayer == null) return LuaValue.valueOf("");
                        net.minecraft.item.ItemStack stack =
                                mc.thePlayer.inventory.getStackInSlot(slotValue.toint());
                        if(stack == null) return LuaValue.valueOf("");
                        return LuaValue.valueOf(stack.getItem().getUnlocalizedName());
                    }
                });
        set(
                "setHotbarSlot",
                new OneArgFunction() {
                    @Override
                    public LuaValue call(LuaValue v) {
                        if(mc.thePlayer != null) mc.thePlayer.inventory.currentItem = v.toint();
                        return LuaValue.NIL;
                    }
                });
        set(
                "useHeldItem",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        if(mc.thePlayer != null && mc.theWorld != null)
                            mc.playerController.sendUseItem(
                                    mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
                        return LuaValue.NIL;
                    }
                });
    }

    private LuaTable livingToTable(EntityLivingBase e) {
        LuaTable t = new LuaTable();
        t.set("id", LuaValue.valueOf(e.getEntityId()));
        String name =
                e instanceof EntityPlayer
                        ? ((EntityPlayer) e).getName()
                        : e.hasCustomName() ? e.getCustomNameTag() : e.getClass().getSimpleName();
        t.set("name", LuaValue.valueOf(name));
        t.set("x", LuaValue.valueOf(e.posX));
        t.set("y", LuaValue.valueOf(e.posY));
        t.set("z", LuaValue.valueOf(e.posZ));
        t.set("eyeHeight", LuaValue.valueOf((double) e.getEyeHeight()));
        t.set("health", LuaValue.valueOf((double) e.getHealth()));
        t.set("hurtTime", LuaValue.valueOf(e.hurtTime));
        t.set("distance", LuaValue.valueOf((double) mc.thePlayer.getDistanceToEntity(e)));
        t.set("isInvisible", LuaValue.valueOf(e.isInvisible()));
        t.set("isPlayer", LuaValue.valueOf(e instanceof EntityPlayer));
        t.set("isHostile", LuaValue.valueOf(e instanceof IMob));
        t.set("isPassive", LuaValue.valueOf(e instanceof IAnimals && !(e instanceof IMob)));
        return t;
    }

    private LuaTable entityToTable(EntityPlayer ep) {
        LuaTable t = new LuaTable();
        t.set("id", LuaValue.valueOf(ep.getEntityId()));
        t.set("name", LuaValue.valueOf(ep.getName()));
        t.set("x", LuaValue.valueOf(ep.posX));
        t.set("y", LuaValue.valueOf(ep.posY));
        t.set("z", LuaValue.valueOf(ep.posZ));
        t.set("eyeHeight", LuaValue.valueOf((double) ep.getEyeHeight()));
        t.set("health", LuaValue.valueOf((double) ep.getHealth()));
        t.set("hurtTime", LuaValue.valueOf(ep.hurtTime));
        t.set("distance", LuaValue.valueOf((double) mc.thePlayer.getDistanceToEntity(ep)));
        t.set("isInvisible", LuaValue.valueOf(ep.isInvisible()));
        return t;
    }
}

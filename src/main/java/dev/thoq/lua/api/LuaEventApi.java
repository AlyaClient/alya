package dev.thoq.lua.api;

import dev.thoq.Alya;
import dev.thoq.event.IEvent;
import dev.thoq.event.IEventListener;
import dev.thoq.event.events.*;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.*;

public final class LuaEventApi extends LuaTable {
    private static final net.minecraft.client.Minecraft mc =
            net.minecraft.client.Minecraft.getMinecraft();
    private static final Map<String, Class<? extends IEvent>> EVENT_CLASS_MAP = new HashMap<>();
    private final List<Map.Entry<Class<IEvent>, IEventListener<IEvent>>> subscriptions =
            new ArrayList<>();

    static {
        EVENT_CLASS_MAP.put("motion", MotionEvent.class);
        EVENT_CLASS_MAP.put("update", UpdateEvent.class);
        EVENT_CLASS_MAP.put("render2d", Render2DEvent.class);
        EVENT_CLASS_MAP.put("tick", TickEvent.class);
        EVENT_CLASS_MAP.put("timeupdate", TimeUpdateEvent.class);
        EVENT_CLASS_MAP.put("playermove", PlayerMoveEvent.class);
        EVENT_CLASS_MAP.put("playerinput", PlayerInputEvent.class);
        EVENT_CLASS_MAP.put("packetsend", PacketSendEvent.class);
        EVENT_CLASS_MAP.put("packetreceive", PacketReceiveEvent.class);
        EVENT_CLASS_MAP.put("moveentity", MoveEntityEvent.class);
        EVENT_CLASS_MAP.put("slowdown", SlowDownEvent.class);
        EVENT_CLASS_MAP.put("blockplaceable", BlockPlaceableEvent.class);
        EVENT_CLASS_MAP.put("render3d", Render3DEvent.class);
    }

    public LuaEventApi() {
        set(
                "on",
                new TwoArgFunction() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public LuaValue call(LuaValue eventNameValue, LuaValue callbackFunction) {
                        if(!(callbackFunction instanceof LuaFunction)) return LuaValue.NIL;
                        final LuaFunction luaFunction = (LuaFunction) callbackFunction;
                        final String eventKey = eventNameValue.tojstring().toLowerCase();
                        final Class<? extends IEvent> eventClass = EVENT_CLASS_MAP.get(eventKey);
                        if(eventClass == null) {
                            Alya.getInstance().getLogger().warn("Unknown Lua event: {}", eventKey);
                            return LuaValue.NIL;
                        }
                        final Class<IEvent> typedEventClass = (Class<IEvent>) eventClass;
                        final IEventListener<IEvent> listener =
                                event -> {
                                    LuaTable eventTable = buildEventTable(event);
                                    try {
                                        luaFunction.call(eventTable);
                                    } catch(LuaError luaError) {
                                        Alya.getInstance()
                                                .getLogger()
                                                .error("Lua event handler error: {}", luaError.getMessage());
                                    }
                                };
                        subscriptions.add(new AbstractMap.SimpleEntry<>(typedEventClass, listener));
                        Alya.getInstance().getEventBus().subscribe(typedEventClass, listener);
                        return LuaValue.NIL;
                    }
                });
        set(
                "getNames",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        LuaTable namesTable = new LuaTable();
                        int tableIndex = 1;
                        for(String eventKey : EVENT_CLASS_MAP.keySet()) {
                            namesTable.set(tableIndex++, LuaValue.valueOf(eventKey));
                        }
                        return namesTable;
                    }
                });
    }

    public void clearSubscriptions() {
        for(Map.Entry<Class<IEvent>, IEventListener<IEvent>> entry : subscriptions) {
            Alya.getInstance().getEventBus().unsubscribe(entry.getKey(), entry.getValue());
        }
        subscriptions.clear();
    }

    private LuaTable buildEventTable(final IEvent event) {
        LuaTable eventTable = new LuaTable();
        eventTable.set(
                "getType",
                new ZeroArgFunction() {
                    @Override
                    public LuaValue call() {
                        return LuaValue.valueOf(event.getClass().getSimpleName());
                    }
                });
        if(event instanceof MotionEvent) {
            final MotionEvent motionEvent = (MotionEvent) event;
            eventTable.set(
                    "getX",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(motionEvent.getX());
                        }
                    });
            eventTable.set(
                    "getY",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(motionEvent.getY());
                        }
                    });
            eventTable.set(
                    "getZ",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(motionEvent.getZ());
                        }
                    });
            eventTable.set(
                    "getYaw",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf((double) motionEvent.getYaw());
                        }
                    });
            eventTable.set(
                    "getPitch",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf((double) motionEvent.getPitch());
                        }
                    });
            eventTable.set(
                    "isOnGround",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(motionEvent.isOnGround());
                        }
                    });
            eventTable.set(
                    "setOnGround",
                    new OneArgFunction() {
                        @Override
                        public LuaValue call(LuaValue onGroundValue) {
                            motionEvent.setOnGround(onGroundValue.toboolean());
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "isPre",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(motionEvent.isPre());
                        }
                    });
            eventTable.set(
                    "isPost",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(motionEvent.isPost());
                        }
                    });
            eventTable.set(
                    "setYaw",
                    new OneArgFunction() {
                        @Override
                        public LuaValue call(LuaValue v) {
                            motionEvent.setYaw((float) v.todouble());
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "setPitch",
                    new OneArgFunction() {
                        @Override
                        public LuaValue call(LuaValue v) {
                            motionEvent.setPitch((float) v.todouble());
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "setX",
                    new OneArgFunction() {
                        @Override
                        public LuaValue call(LuaValue xValue) {
                            motionEvent.setX(xValue.todouble());
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "setY",
                    new OneArgFunction() {
                        @Override
                        public LuaValue call(LuaValue yValue) {
                            motionEvent.setY(yValue.todouble());
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "setZ",
                    new OneArgFunction() {
                        @Override
                        public LuaValue call(LuaValue zValue) {
                            motionEvent.setZ(zValue.todouble());
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "cancel",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            motionEvent.cancel();
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "isCanceled",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(motionEvent.isCanceled());
                        }
                    });
        } else if(event instanceof Render2DEvent) {
            final Render2DEvent render2DEvent = (Render2DEvent) event;
            eventTable.set(
                    "getWidth",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(render2DEvent.scaledResolution().getScaledWidth());
                        }
                    });
            eventTable.set(
                    "getHeight",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(render2DEvent.scaledResolution().getScaledHeight());
                        }
                    });
        } else if(event instanceof TimeUpdateEvent) {
            final TimeUpdateEvent timeUpdateEvent = (TimeUpdateEvent) event;
            eventTable.set(
                    "cancel",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            timeUpdateEvent.cancel();
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "isCanceled",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(timeUpdateEvent.isCanceled());
                        }
                    });
        } else if(event instanceof PlayerMoveEvent) {
            final PlayerMoveEvent playerMoveEvent = (PlayerMoveEvent) event;
            eventTable.set(
                    "cancel",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            playerMoveEvent.cancel();
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "isCanceled",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(playerMoveEvent.isCanceled());
                        }
                    });
        } else if(event instanceof PlayerInputEvent) {
            final PlayerInputEvent playerInputEvent = (PlayerInputEvent) event;
            eventTable.set(
                    "cancel",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            playerInputEvent.cancel();
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "isCanceled",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(playerInputEvent.isCanceled());
                        }
                    });
            eventTable.set(
                    "setMoveForward",
                    new OneArgFunction() {
                        @Override
                        public LuaValue call(LuaValue v) {
                            if(mc.thePlayer != null && mc.thePlayer.movementInput != null)
                                mc.thePlayer.movementInput.moveForward = (float) v.todouble();
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "setMoveStrafe",
                    new OneArgFunction() {
                        @Override
                        public LuaValue call(LuaValue v) {
                            if(mc.thePlayer != null && mc.thePlayer.movementInput != null)
                                mc.thePlayer.movementInput.moveStrafe = (float) v.todouble();
                            return LuaValue.NIL;
                        }
                    });
        } else if(event instanceof PacketSendEvent) {
            final PacketSendEvent packetSendEvent = (PacketSendEvent) event;
            eventTable.set(
                    "cancel",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            packetSendEvent.cancel();
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "isCanceled",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(packetSendEvent.isCanceled());
                        }
                    });
            eventTable.set(
                    "getPacketClass",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(packetSendEvent.getPacket().getClass().getSimpleName());
                        }
                    });
            eventTable.set(
                    "getEntityAction",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            if(packetSendEvent.getPacket() instanceof C0BPacketEntityAction)
                                return LuaValue.valueOf(
                                        ((C0BPacketEntityAction) packetSendEvent.getPacket()).getAction().name());
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "getUseAction",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            if(packetSendEvent.getPacket() instanceof net.minecraft.network.play.client.C02PacketUseEntity)
                                return LuaValue.valueOf(
                                        ((net.minecraft.network.play.client.C02PacketUseEntity) packetSendEvent.getPacket()).getAction().name());
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "getAttackedEntityId",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            if(packetSendEvent.getPacket() instanceof net.minecraft.network.play.client.C02PacketUseEntity) {
                                net.minecraft.network.play.client.C02PacketUseEntity usePacket =
                                        (net.minecraft.network.play.client.C02PacketUseEntity) packetSendEvent.getPacket();
                                if(mc.theWorld != null) {
                                    net.minecraft.entity.Entity attackedEntity = usePacket.getEntityFromWorld(mc.theWorld);
                                    if(attackedEntity != null) return LuaValue.valueOf(attackedEntity.getEntityId());
                                }
                            }
                            return LuaValue.valueOf(-1);
                        }
                    });
        } else if(event instanceof PacketReceiveEvent) {
            final PacketReceiveEvent packetReceiveEvent = (PacketReceiveEvent) event;
            eventTable.set(
                    "cancel",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            packetReceiveEvent.cancel();
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "isCanceled",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(packetReceiveEvent.isCanceled());
                        }
                    });
            eventTable.set(
                    "getPacketClass",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(packetReceiveEvent.getPacket().getClass().getSimpleName());
                        }
                    });
            eventTable.set(
                    "getMotionX",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            if(packetReceiveEvent.getPacket() instanceof S12PacketEntityVelocity)
                                return LuaValue.valueOf(
                                        ((S12PacketEntityVelocity) packetReceiveEvent.getPacket()).getMotionX());
                            if(packetReceiveEvent.getPacket() instanceof S27PacketExplosion)
                                return LuaValue.valueOf(
                                        ((S27PacketExplosion) packetReceiveEvent.getPacket()).getX());
                            return LuaValue.valueOf(0d);
                        }
                    });
            eventTable.set(
                    "getMotionY",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            if(packetReceiveEvent.getPacket() instanceof S12PacketEntityVelocity)
                                return LuaValue.valueOf(
                                        ((S12PacketEntityVelocity) packetReceiveEvent.getPacket()).getMotionY());
                            if(packetReceiveEvent.getPacket() instanceof S27PacketExplosion)
                                return LuaValue.valueOf(
                                        (double) ((S27PacketExplosion) packetReceiveEvent.getPacket()).getY());
                            return LuaValue.valueOf(0d);
                        }
                    });
            eventTable.set(
                    "getMotionZ",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            if(packetReceiveEvent.getPacket() instanceof S12PacketEntityVelocity)
                                return LuaValue.valueOf(
                                        ((S12PacketEntityVelocity) packetReceiveEvent.getPacket()).getMotionZ());
                            if(packetReceiveEvent.getPacket() instanceof S27PacketExplosion)
                                return LuaValue.valueOf(
                                        (double) ((S27PacketExplosion) packetReceiveEvent.getPacket()).getZ());
                            return LuaValue.valueOf(0d);
                        }
                    });
            eventTable.set(
                    "setMotionX",
                    new OneArgFunction() {
                        @Override
                        public LuaValue call(LuaValue val) {
                            if(packetReceiveEvent.getPacket() instanceof S12PacketEntityVelocity)
                                ((S12PacketEntityVelocity) packetReceiveEvent.getPacket()).motionX = val.toint();
                            else if(packetReceiveEvent.getPacket() instanceof S27PacketExplosion)
                                ((S27PacketExplosion) packetReceiveEvent.getPacket()).posX =
                                        (float) val.todouble();
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "setMotionY",
                    new OneArgFunction() {
                        @Override
                        public LuaValue call(LuaValue val) {
                            if(packetReceiveEvent.getPacket() instanceof S12PacketEntityVelocity)
                                ((S12PacketEntityVelocity) packetReceiveEvent.getPacket()).motionY = val.toint();
                            else if(packetReceiveEvent.getPacket() instanceof S27PacketExplosion)
                                ((S27PacketExplosion) packetReceiveEvent.getPacket()).posY =
                                        (float) val.todouble();
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "setMotionZ",
                    new OneArgFunction() {
                        @Override
                        public LuaValue call(LuaValue val) {
                            if(packetReceiveEvent.getPacket() instanceof S12PacketEntityVelocity)
                                ((S12PacketEntityVelocity) packetReceiveEvent.getPacket()).motionZ = val.toint();
                            else if(packetReceiveEvent.getPacket() instanceof S27PacketExplosion)
                                ((S27PacketExplosion) packetReceiveEvent.getPacket()).posZ =
                                        (float) val.todouble();
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "getEntityId",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            if(packetReceiveEvent.getPacket() instanceof S12PacketEntityVelocity)
                                return LuaValue.valueOf(
                                        ((S12PacketEntityVelocity) packetReceiveEvent.getPacket()).getEntityID());
                            return LuaValue.valueOf(-1);
                        }
                    });
        } else if(event instanceof TickEvent) {
            final TickEvent tickEvent = (TickEvent) event;
            eventTable.set(
                    "cancel",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            tickEvent.cancel();
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "isCanceled",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(tickEvent.isCanceled());
                        }
                    });
        } else if(event instanceof Render3DEvent) {
            final Render3DEvent render3DEvent = (Render3DEvent) event;
            eventTable.set(
                    "getPartialTicks",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf((double) render3DEvent.partialTicks());
                        }
                    });
        } else if(event instanceof SlowDownEvent) {
            final SlowDownEvent slowDownEvent = (SlowDownEvent) event;
            eventTable.set(
                    "getReason",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(slowDownEvent.getReason());
                        }
                    });
            eventTable.set(
                    "cancel",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            slowDownEvent.cancel();
                            return LuaValue.NIL;
                        }
                    });
            eventTable.set(
                    "isCanceled",
                    new ZeroArgFunction() {
                        @Override
                        public LuaValue call() {
                            return LuaValue.valueOf(slowDownEvent.isCanceled());
                        }
                    });
        }
        return eventTable;
    }
}

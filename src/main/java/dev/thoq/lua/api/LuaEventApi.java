package dev.thoq.lua.api;

import dev.thoq.Alya;
import dev.thoq.event.IEvent;
import dev.thoq.event.IEventListener;
import dev.thoq.event.events.BlockPlaceableEvent;
import dev.thoq.event.events.MotionEvent;
import dev.thoq.event.events.MoveEntityEvent;
import dev.thoq.event.events.PacketSendEvent;
import dev.thoq.event.events.PlayerInputEvent;
import dev.thoq.event.events.PlayerMoveEvent;
import dev.thoq.event.events.Render2DEvent;
import dev.thoq.event.events.SlowDownEvent;
import dev.thoq.event.events.TickEvent;
import dev.thoq.event.events.TimeUpdateEvent;
import dev.thoq.event.events.UpdateEvent;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LuaEventApi extends LuaTable {

    private static final Map<String, Class<? extends IEvent>> EVENT_CLASS_MAP = new HashMap<>();

    private final List<Map.Entry<Class<IEvent>, IEventListener<IEvent>>> subscriptions = new ArrayList<>();

    static {
        EVENT_CLASS_MAP.put("motion", MotionEvent.class);
        EVENT_CLASS_MAP.put("update", UpdateEvent.class);
        EVENT_CLASS_MAP.put("render2d", Render2DEvent.class);
        EVENT_CLASS_MAP.put("tick", TickEvent.class);
        EVENT_CLASS_MAP.put("timeupdate", TimeUpdateEvent.class);
        EVENT_CLASS_MAP.put("playermove", PlayerMoveEvent.class);
        EVENT_CLASS_MAP.put("playerinput", PlayerInputEvent.class);
        EVENT_CLASS_MAP.put("packetsend", PacketSendEvent.class);
        EVENT_CLASS_MAP.put("moveentity", MoveEntityEvent.class);
        EVENT_CLASS_MAP.put("slowdown", SlowDownEvent.class);
        EVENT_CLASS_MAP.put("blockplaceable", BlockPlaceableEvent.class);
    }

    public LuaEventApi() {
        set("on", new TwoArgFunction() {
            @Override
            @SuppressWarnings("unchecked")
            public LuaValue call(LuaValue eventNameValue, LuaValue callbackFunction) {
                if (!(callbackFunction instanceof LuaFunction))
                    return LuaValue.NIL;
                final LuaFunction luaFunction = (LuaFunction) callbackFunction;
                final String eventKey = eventNameValue.tojstring().toLowerCase();
                final Class<? extends IEvent> eventClass = EVENT_CLASS_MAP.get(eventKey);
                if (eventClass == null) {
                    Alya.getInstance().getLogger().warn("Unknown Lua event: {}", eventKey);
                    return LuaValue.NIL;
                }
                final Class<IEvent> typedEventClass = (Class<IEvent>) eventClass;
                final IEventListener<IEvent> listener = event -> {
                    LuaTable eventTable = buildEventTable(event);
                    try {
                        luaFunction.call(eventTable);
                    } catch (LuaError luaError) {
                        Alya.getInstance().getLogger().error("Lua event handler error: {}",
                                luaError.getMessage());
                    }
                };
                subscriptions.add(new AbstractMap.SimpleEntry<>(typedEventClass, listener));
                Alya.getInstance().getEventBus().subscribe(typedEventClass, listener);
                return LuaValue.NIL;
            }
        });

        set("getNames", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                LuaTable namesTable = new LuaTable();
                int tableIndex = 1;
                for (String eventKey : EVENT_CLASS_MAP.keySet()) {
                    namesTable.set(tableIndex++, LuaValue.valueOf(eventKey));
                }
                return namesTable;
            }
        });
    }

    public void clearSubscriptions() {
        for (Map.Entry<Class<IEvent>, IEventListener<IEvent>> entry : subscriptions) {
            Alya.getInstance().getEventBus().unsubscribe(entry.getKey(), entry.getValue());
        }
        subscriptions.clear();
    }

    private LuaTable buildEventTable(final IEvent event) {
        LuaTable eventTable = new LuaTable();
        eventTable.set("getType", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(event.getClass().getSimpleName());
            }
        });

        if (event instanceof MotionEvent) {
            final MotionEvent motionEvent = (MotionEvent) event;
            eventTable.set("getX", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(motionEvent.getX());
                }
            });
            eventTable.set("getY", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(motionEvent.getY());
                }
            });
            eventTable.set("getZ", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(motionEvent.getZ());
                }
            });
            eventTable.set("getYaw", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf((double) motionEvent.getYaw());
                }
            });
            eventTable.set("getPitch", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf((double) motionEvent.getPitch());
                }
            });
            eventTable.set("isOnGround", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(motionEvent.isOnGround());
                }
            });
            eventTable.set("setOnGround", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue onGroundValue) {
                    motionEvent.setOnGround(onGroundValue.toboolean());
                    return LuaValue.NIL;
                }
            });
            eventTable.set("isPre", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(motionEvent.isPre());
                }
            });
            eventTable.set("isPost", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(motionEvent.isPost());
                }
            });
            eventTable.set("setX", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue xValue) {
                    motionEvent.setX(xValue.todouble());
                    return LuaValue.NIL;
                }
            });
            eventTable.set("setY", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue yValue) {
                    motionEvent.setY(yValue.todouble());
                    return LuaValue.NIL;
                }
            });
            eventTable.set("setZ", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue zValue) {
                    motionEvent.setZ(zValue.todouble());
                    return LuaValue.NIL;
                }
            });
            eventTable.set("cancel", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    motionEvent.cancel();
                    return LuaValue.NIL;
                }
            });
            eventTable.set("isCanceled", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(motionEvent.isCanceled());
                }
            });
        } else if (event instanceof Render2DEvent) {
            final Render2DEvent render2DEvent = (Render2DEvent) event;
            eventTable.set("getWidth", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(render2DEvent.scaledResolution().getScaledWidth());
                }
            });
            eventTable.set("getHeight", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(render2DEvent.scaledResolution().getScaledHeight());
                }
            });
        } else if (event instanceof TimeUpdateEvent) {
            final TimeUpdateEvent timeUpdateEvent = (TimeUpdateEvent) event;
            eventTable.set("cancel", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    timeUpdateEvent.cancel();
                    return LuaValue.NIL;
                }
            });
            eventTable.set("isCanceled", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(timeUpdateEvent.isCanceled());
                }
            });
        } else if (event instanceof PlayerMoveEvent) {
            final PlayerMoveEvent playerMoveEvent = (PlayerMoveEvent) event;
            eventTable.set("cancel", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    playerMoveEvent.cancel();
                    return LuaValue.NIL;
                }
            });
            eventTable.set("isCanceled", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(playerMoveEvent.isCanceled());
                }
            });
        } else if (event instanceof PlayerInputEvent) {
            final PlayerInputEvent playerInputEvent = (PlayerInputEvent) event;
            eventTable.set("cancel", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    playerInputEvent.cancel();
                    return LuaValue.NIL;
                }
            });
            eventTable.set("isCanceled", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(playerInputEvent.isCanceled());
                }
            });
        } else if (event instanceof PacketSendEvent) {
            final PacketSendEvent packetSendEvent = (PacketSendEvent) event;
            eventTable.set("cancel", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    packetSendEvent.cancel();
                    return LuaValue.NIL;
                }
            });
            eventTable.set("isCanceled", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(packetSendEvent.isCanceled());
                }
            });
            eventTable.set("getPacketClass", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(packetSendEvent.getPacket().getClass().getSimpleName());
                }
            });
        } else if (event instanceof TickEvent) {
            final TickEvent tickEvent = (TickEvent) event;
            eventTable.set("cancel", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    tickEvent.cancel();
                    return LuaValue.NIL;
                }
            });
            eventTable.set("isCanceled", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(tickEvent.isCanceled());
                }
            });
        } else if (event instanceof SlowDownEvent) {
            final SlowDownEvent slowDownEvent = (SlowDownEvent) event;
            eventTable.set("cancel", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    slowDownEvent.cancel();
                    return LuaValue.NIL;
                }
            });
            eventTable.set("isCanceled", new ZeroArgFunction() {
                @Override
                public LuaValue call() {
                    return LuaValue.valueOf(slowDownEvent.isCanceled());
                }
            });
        }

        return eventTable;
    }
}

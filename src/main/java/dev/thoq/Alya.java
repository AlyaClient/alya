package dev.thoq;

import dev.thoq.command.CommandManager;
import dev.thoq.command.commands.BindCommand;
import dev.thoq.command.commands.ConfigCommand;
import dev.thoq.command.commands.HelpCommand;
import dev.thoq.command.commands.ReloadCommand;
import dev.thoq.config.ConfigManager;
import dev.thoq.event.EventBus;
import dev.thoq.lua.LuaEngine;
import dev.thoq.module.ModuleManager;
import dev.thoq.module.modules.clickgui.ClickGUI;
import dev.thoq.module.modules.render.HUDModule;
import dev.thoq.module.modules.render.KeystrokesModule;
import dev.thoq.util.font.AlyaFontRenderer;
import dev.thoq.util.misc.Title;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public final class Alya {

    private static final Alya INSTANCE = new Alya();
    private static final String NAME = "Alya", VERSION = "1.0";
    private static final ResourceLocation OPENING_SOUND = new ResourceLocation("Alya/Sounds/Opening.wav");

    private final Logger LOGGER = LogManager.getLogger(Alya.class);
    private final EventBus eventBus = new EventBus();
    private final ModuleManager moduleManager = new ModuleManager();
    private final CommandManager commandManager = new CommandManager();
    private final ConfigManager configManager = new ConfigManager();
    private final LuaEngine luaEngine = new LuaEngine();

    private AlyaFontRenderer fontRenderer;
    private AlyaFontRenderer fontRendererSmall;
    private AlyaFontRenderer fontRendererMedium;
    private AlyaFontRenderer fontRendererBold;
    private AlyaFontRenderer fontRendererTitle;
    private AlyaFontRenderer fontRendererTiny;
    private boolean audioStarted;

    private Alya() {
    }


    public String[] getScripts() {
        final String[] scripts = {"util/movement.lua",
            "util/chat.lua",
            "util/render.lua",

            "core/module.lua",
            "core/submodule.lua",
            "core/command.lua",

            "modules/render/fullbright.lua",
            "modules/render/arraylist.lua",
            "modules/render/ambience.lua",
            "modules/render/esp.lua",

            "modules/movement/flight.lua",
            "modules/movement/speed.lua",

            "modules/player/sprint.lua",
            "modules/player/nojumpdelay.lua",
            "modules/player/norightclickdelay.lua",
            "modules/player/nofall.lua",

            "modules/combat/killaura.lua",
            "modules/combat/velocity.lua",
            "modules/combat/criticals.lua",
            "modules/combat/autoclicker.lua",
            "modules/combat/clickassist.lua",
            "modules/combat/targetstrafe.lua",

            "modules/movement/longjump.lua",

            "modules/player/legitscaffold.lua",

            "modules/misc/disabler.lua",

            "modules/world/timer.lua",

            "modules/movement/highjump.lua",
            "modules/movement/wee.lua"
        };
        return scripts;
    }

    public void initialize() {
        Title.update(this.getClass());
        moduleManager.putAll(new ClickGUI(), new HUDModule(), new KeystrokesModule());
        commandManager.putAll(new HelpCommand(), new BindCommand(), new ConfigCommand(), new ReloadCommand());
        bindFontRenderersToLua();
        luaEngine.loadAll();
        LOGGER.info("Initialized {} modules", moduleManager.getModules().size());
        LOGGER.info("Initialized {} commands", commandManager.getCommands().size());
        configManager.load();
        playStartupSound();
    }

    private void bindFontRenderersToLua() {
        luaEngine.getGlobals().set("getFontRenderer", new org.luaj.vm2.lib.ZeroArgFunction() {
            @Override public org.luaj.vm2.LuaValue call() {
                return buildFontRendererTable(getFontRenderer());
            }
        });
        luaEngine.getGlobals().set("getFontRendererSmall", new org.luaj.vm2.lib.ZeroArgFunction() {
            @Override public org.luaj.vm2.LuaValue call() {
                return buildFontRendererTable(getFontRendererSmall());
            }
        });
        luaEngine.getGlobals().set("getFontRendererMedium", new org.luaj.vm2.lib.ZeroArgFunction() {
            @Override public org.luaj.vm2.LuaValue call() {
                return buildFontRendererTable(getFontRendererMedium());
            }
        });
        luaEngine.getGlobals().set("getFontRendererBold", new org.luaj.vm2.lib.ZeroArgFunction() {
            @Override public org.luaj.vm2.LuaValue call() {
                return buildFontRendererTable(getFontRendererBold());
            }
        });
        luaEngine.getGlobals().set("getFontRendererTitle", new org.luaj.vm2.lib.ZeroArgFunction() {
            @Override public org.luaj.vm2.LuaValue call() {
                return buildFontRendererTable(getFontRendererTitle());
            }
        });

        org.luaj.vm2.LuaTable alyaTable = (org.luaj.vm2.LuaTable) luaEngine.getGlobals().get("alya");
        alyaTable.set("getFontRenderer", luaEngine.getGlobals().get("getFontRenderer"));
        alyaTable.set("getFontRendererSmall", luaEngine.getGlobals().get("getFontRendererSmall"));
        alyaTable.set("getFontRendererMedium", luaEngine.getGlobals().get("getFontRendererMedium"));
        alyaTable.set("getFontRendererBold", luaEngine.getGlobals().get("getFontRendererBold"));
        alyaTable.set("getFontRendererTitle", luaEngine.getGlobals().get("getFontRendererTitle"));
    }

    private org.luaj.vm2.LuaTable buildFontRendererTable(final AlyaFontRenderer renderer) {
        org.luaj.vm2.LuaTable table = new org.luaj.vm2.LuaTable();
        table.set("drawString", new org.luaj.vm2.lib.VarArgFunction() {
            @Override public org.luaj.vm2.Varargs invoke(org.luaj.vm2.Varargs args) {
                renderer.drawString(args.arg(1).tojstring(), args.arg(2).tofloat(), args.arg(3).tofloat(), args.arg(4).toint());
                return org.luaj.vm2.LuaValue.NIL;
            }
        });
        table.set("drawStringWithShadow", new org.luaj.vm2.lib.VarArgFunction() {
            @Override public org.luaj.vm2.Varargs invoke(org.luaj.vm2.Varargs args) {
                renderer.drawStringWithShadow(args.arg(1).tojstring(), args.arg(2).tofloat(), args.arg(3).tofloat(), args.arg(4).toint());
                return org.luaj.vm2.LuaValue.NIL;
            }
        });
        table.set("getStringWidth", new org.luaj.vm2.lib.OneArgFunction() {
            @Override public org.luaj.vm2.LuaValue call(org.luaj.vm2.LuaValue text) {
                return org.luaj.vm2.LuaValue.valueOf((double) renderer.getStringWidth(text.tojstring()));
            }
        });
        table.set("getFontHeight", new org.luaj.vm2.lib.ZeroArgFunction() {
            @Override public org.luaj.vm2.LuaValue call() {
                return org.luaj.vm2.LuaValue.valueOf((double) renderer.getFontHeight());
            }
        });
        return table;
    }

    private void playStartupSound() {
        CompletableFuture.runAsync(() -> {
            if(!audioStarted) {
                try {
                    final Minecraft mc = Minecraft.getMinecraft();
                    final InputStream inputStream = mc.getResourceManager().getResource(OPENING_SOUND).getInputStream();

                    try(final AudioInputStream audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream))) {
                        final Clip clip = AudioSystem.getClip();
                        clip.open(audioStream);
                        clip.setMicrosecondPosition(250_000);
                        clip.start();
                    }
                } catch(final UnsupportedAudioFileException | IOException | LineUnavailableException exception) {
                    LOGGER.error("Failed to play startup chime", exception);
                }

                audioStarted = true;
            }
        });
    }

    public void terminate() {
        LOGGER.info("Shutting down {}...", NAME);
        configManager.save();
        moduleManager.disableAll();
    }

    public static String getName() {
        return NAME;
    }

    public static String getVersion() {
        return VERSION;
    }

    public static Alya getInstance() {
        return INSTANCE;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LuaEngine getLuaEngine() {
        return luaEngine;
    }

    public AlyaFontRenderer getFontRenderer() {
        if(fontRenderer == null) {
            fontRenderer = new AlyaFontRenderer(15f);
        }
        return fontRenderer;
    }

    public AlyaFontRenderer getFontRendererSmall() {
        if(fontRendererSmall == null) {
            fontRendererSmall = new AlyaFontRenderer(8f);
        }
        return fontRendererSmall;
    }

    public AlyaFontRenderer getFontRendererMedium() {
        if(fontRendererMedium == null) {
            fontRendererMedium = new AlyaFontRenderer(10f);
        }
        return fontRendererMedium;
    }

    public AlyaFontRenderer getFontRendererBold() {
        if(fontRendererBold == null) {
            fontRendererBold = new AlyaFontRenderer("Alya/Fonts/OpenSans-Bold.ttf", 12f);
        }
        return fontRendererBold;
    }

    public AlyaFontRenderer getFontRendererTitle() {
        if(fontRendererTitle == null) {
            fontRendererTitle = new AlyaFontRenderer("Alya/Fonts/OpenSans-Bold.ttf", 32f);
        }
        return fontRendererTitle;
    }

    public AlyaFontRenderer getFontRendererTiny() {
        if(fontRendererTiny == null) {
            fontRendererTiny = new AlyaFontRenderer(7f);
        }
        return fontRendererTiny;
    }

    public Logger getLogger() {
        return LOGGER;
    }


}

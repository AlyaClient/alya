package dev.thoq;

import dev.thoq.command.CommandManager;
import dev.thoq.command.commands.*;
import dev.thoq.config.ConfigManager;
import dev.thoq.event.EventBus;
import dev.thoq.lua.LuaEngine;
import dev.thoq.lua.api.LuaFontApi;
import dev.thoq.lua.api.LuaMinecraftApi;
import dev.thoq.module.ModuleManager;
import dev.thoq.module.modules.clickgui.ClickGUI;
import dev.thoq.module.modules.render.HUDModule;
import dev.thoq.module.modules.render.KeystrokesModule;
import dev.thoq.util.font.AlyaFontRenderer;
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
    private static String CLIENT_NAME = "Alya";
    private static final ResourceLocation OPENING_SOUND =
            new ResourceLocation("Alya/Sounds/Opening.wav");
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
        final String[] scripts = {
                "util/movement.lua",
                "util/chat.lua",
                "util/render.lua",
                "util/timer.lua",

                "core/module.lua",
                "core/submodule.lua",
                "core/command.lua",

                "modules/render/fullbright.lua",
                "modules/render/arraylist.lua",
                "modules/render/ambience.lua",
                "modules/render/esp.lua",
                "modules/movement/flight.lua",
                "modules/movement/flight/static.lua",
                "modules/movement/flight/motion.lua",
                "modules/movement/speed.lua",
                "modules/movement/speed/vanilla.lua",
                "modules/movement/speed/verus.lua",
                "modules/movement/speed/bhop.lua",
                "modules/movement/step.lua",
                "modules/movement/jesus.lua",
                "modules/movement/keepsprint.lua",
                "modules/movement/longjump.lua",
                "modules/movement/longjump/verus.lua",
                "modules/movement/longjump/mineland.lua",
                "modules/movement/longjump/grim.lua",
                "modules/movement/longjump/ncp.lua",
                "modules/movement/longjump/fireball.lua",
                "modules/movement/longjump/vulcan.lua",
                "modules/movement/highjump.lua",
                "modules/movement/wee.lua",
                "modules/player/sprint.lua",
                "modules/player/noslow.lua",
                "modules/player/inventory.lua",
                "modules/player/blink.lua",
                "modules/player/nojumpdelay.lua",
                "modules/player/norightclickdelay.lua",
                "modules/player/nofall.lua",
                "modules/player/nofall/vanilla.lua",
                "modules/player/nofall/verus.lua",
                "modules/player/legitscaffold.lua",
                "modules/combat/killaura.lua",
                "modules/combat/velocity.lua",
                "modules/combat/velocity/motion.lua",
                "modules/combat/criticals.lua",
                "modules/combat/criticals/watchdog.lua",
                "modules/combat/criticals/packet.lua",
                "modules/combat/autoclicker.lua",
                "modules/combat/autoclicker/drag.lua",
                "modules/combat/autoclicker/normal.lua",
                "modules/combat/clickassist.lua",
                "modules/combat/hitboxes.lua",
                "modules/combat/reach.lua",
                "modules/combat/targetstrafe.lua",
                "modules/world/scaffold.lua",
                "modules/world/timer.lua",
                "modules/misc/disabler.lua",
                "modules/misc/disabler/omnisprint.lua",
                "modules/misc/hackerdetector.lua"
        };
        return scripts;
    }

    public void initCommands() {
        commandManager.putAll(
                new HelpCommand(),
                new BindCommand(),
                new ConfigCommand(),
                new ReloadCommand(),
                new NameCommand()
        );
    }

    public void initialize() {
        LuaMinecraftApi.registerEvents(eventBus);
        moduleManager.putAll(new ClickGUI(), new HUDModule(), new KeystrokesModule());
        initCommands();
        LuaFontApi.bind(luaEngine.getGlobals());
        luaEngine.loadAll();
        LOGGER.info("Initialized {} modules", moduleManager.getModules().size());
        LOGGER.info("Initialized {} commands", commandManager.getCommands().size());
        configManager.load();
        playStartupSound();
    }


    private void playStartupSound() {
        CompletableFuture.runAsync(
                () -> {
                    if(!audioStarted) {
                        try {
                            final Minecraft mc = Minecraft.getMinecraft();
                            final InputStream inputStream =
                                    mc.getResourceManager().getResource(OPENING_SOUND).getInputStream();
                            try(final AudioInputStream audioStream =
                                        AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream))) {
                                final Clip clip = AudioSystem.getClip();
                                clip.open(audioStream);
                                clip.setMicrosecondPosition(250_000);
                                clip.start();
                            }
                        } catch(final UnsupportedAudioFileException
                                      | IOException
                                      | LineUnavailableException exception) {
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

    public String getClientName() {
        return Alya.CLIENT_NAME;
    }

    public void setClientName(final String name) {
        Alya.CLIENT_NAME = name;
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

package dev.thoq;

import dev.thoq.command.CommandManager;
import dev.thoq.command.commands.*;
import dev.thoq.config.ConfigManager;
import dev.thoq.event.EventBus;
import dev.thoq.lua.LuaEngine;
import dev.thoq.lua.Script;
import dev.thoq.lua.ScriptsUtil;
import dev.thoq.lua.api.LuaFontApi;
import dev.thoq.lua.api.LuaMinecraftApi;
import dev.thoq.module.Category;
import dev.thoq.module.ModuleManager;
import dev.thoq.module.modules.clickgui.ClickGUI;
import dev.thoq.module.modules.render.HUDModule;
import dev.thoq.module.modules.render.KeystrokesModule;
import dev.thoq.util.font.AlyaFontRenderer;
import dev.thoq.viamcp.impl.ViaMCP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Alya {

    private static final Alya INSTANCE = new Alya();
    private static final String NAME = "Alya", VERSION = "1.0";
    private static String CLIENT_NAME = "Alya";
    private final Logger LOGGER = LogManager.getLogger(Alya.class);
    private final EventBus eventBus = new EventBus();
    private final ModuleManager moduleManager = new ModuleManager();
    private final CommandManager commandManager = new CommandManager();
    private final ConfigManager configManager = new ConfigManager();
    private final LuaEngine luaEngine = new LuaEngine();
    private final ScriptsUtil scriptUtil = new ScriptsUtil();
    private AlyaFontRenderer fontRenderer;
    private AlyaFontRenderer fontRendererSmall;
    private AlyaFontRenderer fontRendererMedium;
    private AlyaFontRenderer fontRendererBold;
    private AlyaFontRenderer fontRendererTitle;

    private Alya() {
    }

    @SuppressWarnings("SpellCheckingInspection")
    public String[] getScripts() {
        return scriptUtil.putAll(
                new Script(Category.VISUAL, "fullbright"),
                new Script(Category.VISUAL, "arraylist"),
                new Script(Category.VISUAL, "esp"),
                new Script(Category.VISUAL, "chams"),
                new Script(Category.VISUAL, "outline"),
                new Script(Category.VISUAL, "scoreboard"),
                new Script(Category.VISUAL, "nametags"),
                new Script(Category.MOVEMENT, "flight"),
                new Script(Category.MOVEMENT, "terrain"),
                new Script(Category.MOVEMENT, "safewalk"),
                new Script(Category.MOVEMENT, "doublejump"),
                new Script(Category.MOVEMENT, "speed"),
                new Script(Category.MOVEMENT, "step"),
                new Script(Category.MOVEMENT, "jesus"),
                new Script(Category.MOVEMENT, "keepsprint"),
                new Script(Category.MOVEMENT, "longjump"),
                new Script(Category.MOVEMENT, "highjump"),
                new Script(Category.MOVEMENT, "wee"),
                new Script(Category.PLAYER, "sprint"),
                new Script(Category.PLAYER, "noslowdown"),
                new Script(Category.PLAYER, "inventory"),
                //todo: fix blink
                //new Script(Category.PLAYER, "blink"),
                new Script(Category.PLAYER, "nojumpdelay"),
                new Script(Category.PLAYER, "norightclickdelay"),
                new Script(Category.PLAYER, "nofall"),
                new Script(Category.PLAYER, "legitscaffold"),
                new Script(Category.COMBAT, "killaura"),
                new Script(Category.COMBAT, "velocity"),
                new Script(Category.COMBAT, "knockback"),
                new Script(Category.COMBAT, "criticals"),
                new Script(Category.COMBAT, "autoclicker"),
                new Script(Category.COMBAT, "clickassist"),
                new Script(Category.COMBAT, "hitboxes"),
                new Script(Category.COMBAT, "reach"),
                new Script(Category.COMBAT, "targetstrafe"),
                new Script(Category.PLAYER, "scaffold"),
                new Script(Category.PLAYER, "timer"),
                new Script(Category.EXPLOIT, "disabler"),
                new Script(Category.OTHER, "hackerdetector"),
                new Script(Category.OTHER, "worldtime")
        );
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
        ViaMCP.create();
        ViaMCP.INSTANCE.initAsyncSlider();
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
            fontRendererBold = new AlyaFontRenderer("client/fonts/Lato-Bold.ttf", 12f);
        }
        return fontRendererBold;
    }

    public AlyaFontRenderer getFontRendererTitle() {
        if(fontRendererTitle == null) {
            fontRendererTitle = new AlyaFontRenderer("client/fonts/OpenSans-Bold.ttf", 32f);
        }
        return fontRendererTitle;
    }

    public Logger getLogger() {
        return LOGGER;
    }


}

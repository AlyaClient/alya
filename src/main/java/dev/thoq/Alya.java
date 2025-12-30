package dev.thoq;

import dev.thoq.command.CommandManager;
import dev.thoq.command.commands.BindCommand;
import dev.thoq.command.commands.ConfigCommand;
import dev.thoq.command.commands.HelpCommand;
import dev.thoq.config.ConfigManager;
import dev.thoq.event.EventBus;
import dev.thoq.module.ModuleManager;
import dev.thoq.module.modules.clickgui.ClickGUI;
import dev.thoq.module.modules.movement.FlightModule;
import dev.thoq.module.modules.movement.SpeedModule;
import dev.thoq.module.modules.player.NoJumpDelayModule;
import dev.thoq.module.modules.player.NoRightClickDelayModule;
import dev.thoq.module.modules.render.*;
import dev.thoq.util.font.AlyaFontRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@SuppressWarnings("unused")
public final class Alya {

    private static final Alya INSTANCE = new Alya();
    private static final String NAME = "Alya", VERSION = "1.0";

    private final Logger LOGGER = LogManager.getLogger();
    private final EventBus eventBus = new EventBus();
    private final ModuleManager moduleManager = new ModuleManager();
    private final CommandManager commandManager = new CommandManager();
    private final ConfigManager configManager = new ConfigManager();

    private AlyaFontRenderer fontRenderer;
    private AlyaFontRenderer fontRendererSmall;
    private AlyaFontRenderer fontRendererBold;
    private AlyaFontRenderer fontRendererTitle;

    private Alya() {
    }

    public void initialize() {
        Display.setTitle(String.format("%s %s %s", NAME, VERSION, "Development"));
        initializeModules();
        initializeCommands();
        LOGGER.info("Initialized {} modules", moduleManager.getModules().size());
        LOGGER.info("Initialized {} commands", commandManager.getCommands().size());

        configManager.load();
    }

    private void initializeModules() {
        moduleManager.register(new ClickGUI());
        moduleManager.register(new ArrayListModule());
        moduleManager.register(new HUDModule());
        moduleManager.register(new KeystrokesModule());
        moduleManager.register(new FlightModule());
        moduleManager.register(new SpeedModule());
        moduleManager.register(new FullBrightModule());
        moduleManager.register(new AmbienceModule());
        moduleManager.register(new NoRightClickDelayModule());
        moduleManager.register(new NoJumpDelayModule());
    }

    private void initializeCommands() {
        commandManager.register(new HelpCommand());
        commandManager.register(new BindCommand());
        commandManager.register(new ConfigCommand());
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
        if(fontRendererSmall == null) {
            fontRendererSmall = new AlyaFontRenderer(10f);
        }
        return fontRendererSmall;
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

    public Logger getLogger() {
        return LOGGER;
    }


}

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
import dev.thoq.module.modules.player.SprintModule;
import dev.thoq.module.modules.render.*;
import dev.thoq.module.modules.world.TimerModule;
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

@SuppressWarnings("unused")
public final class Alya {

    private static final Alya INSTANCE = new Alya();
    private static final String NAME = "Alya", VERSION = "1.0";
    private static final ResourceLocation OPENING_SOUND = new ResourceLocation("Alya/Sounds/Opening.wav");

    private final Logger LOGGER = LogManager.getLogger();
    private final EventBus eventBus = new EventBus();
    private final ModuleManager moduleManager = new ModuleManager();
    private final CommandManager commandManager = new CommandManager();
    private final ConfigManager configManager = new ConfigManager();

    private AlyaFontRenderer fontRenderer;
    private AlyaFontRenderer fontRendererSmall;
    private AlyaFontRenderer fontRendererMedium;
    private AlyaFontRenderer fontRendererBold;
    private AlyaFontRenderer fontRendererTitle;
    private boolean audioStarted;

    private Alya() {
    }

    public void initialize() {
        Title.update(this.getClass());
        initializeModules();
        initializeCommands();
        LOGGER.info("Initialized {} modules", moduleManager.getModules().size());
        LOGGER.info("Initialized {} commands", commandManager.getCommands().size());

        configManager.load();
        playStartupSound();
    }

    private void initializeModules() {
        moduleManager.putAll(
                new ClickGUI(),
                new ArrayListModule(),
                new HUDModule(),
                new KeystrokesModule(),
                new FlightModule(),
                new SpeedModule(),
                new FullBrightModule(),
                new AmbienceModule(),
                new NoRightClickDelayModule(),
                new NoJumpDelayModule(),
                new SprintModule(),
                new TimerModule()
        );
    }

    private void initializeCommands() {
        commandManager.putAll(
                new HelpCommand(),
                new BindCommand(),
                new ConfigCommand()
        );
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

    public Logger getLogger() {
        return LOGGER;
    }


}

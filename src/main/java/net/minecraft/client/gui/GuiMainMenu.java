package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import dev.thoq.gui.auth.AltManagerGui;
import dev.thoq.gui.AlyaButton;
import dev.thoq.util.render.ShaderUtil;
import dev.thoq.util.misc.Title;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.reflect.Reflector;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SuppressWarnings({"SameParameterValue", "unchecked", "rawtypes", "DataFlowIssue", "unused", "deprecation"})
public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {

    private static final Logger logger = LogManager.getLogger(GuiMainMenu.class);
    private static final Random RANDOM = new Random();
    private static ShaderUtil menuShader = null;

    private String splashText;

    private final boolean field_175375_v = true;

    private final Object threadLock = new Object();

    private String openGLWarning1;

    private String openGLWarning2;

    private String openGLWarningLink;
    private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
    public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
    private int field_92024_r;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;

    private boolean field_183502_L;
    private GuiScreen modUpdateNotification;

    private ResourceLocation randomImage;

    public GuiMainMenu() {
        this.openGLWarning2 = field_96138_a;
        this.field_183502_L = false;
        this.splashText = "";
        BufferedReader bufferedreader = null;

        try {
            List<String> list = Lists.newArrayList();
            bufferedreader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(), Charsets.UTF_8));
            String s;

            while((s = bufferedreader.readLine()) != null) {
                s = s.trim();

                if(!s.isEmpty()) {
                    list.add(s);
                }
            }

            if(!list.isEmpty()) {
                do {
                    this.splashText = list.get(RANDOM.nextInt(list.size()));
                } while(this.splashText.hashCode() == 125780783);
            }
        } catch(IOException ignored) {
        } finally {
            if(bufferedreader != null) {
                try {
                    bufferedreader.close();
                } catch(IOException ignored) {
                }
            }
        }

        this.openGLWarning1 = "";

        if(!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
            this.openGLWarning1 = I18n.format("title.oldgl1");
            this.openGLWarning2 = I18n.format("title.oldgl2");
            this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }

    public void updateScreen() {
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    public void initGui() {
        int n = RANDOM.nextInt(8) + 1;
        this.randomImage = new ResourceLocation("Alya/Assets/Femboys/" + n + ".png");

        Title.update(this.getClass());
        final DynamicTexture viewportTexture = new DynamicTexture(256, 256);
        ResourceLocation backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", viewportTexture);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        if(calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) == 24) {
            this.splashText = "Merry X-mas!";
        } else if(calendar.get(Calendar.MONTH) + 1 == 1 && calendar.get(Calendar.DATE) == 1) {
            this.splashText = "Happy new year!";
        } else if(calendar.get(Calendar.MONTH) + 1 == 10 && calendar.get(Calendar.DATE) == 31) {
            this.splashText = "OOoooOOOoooo! Spooky!";
        }

        final int i = 24;
        final int j = this.height / 4 + 48;

        this.addSingleplayerMultiplayerButtons(j, 24);

        this.buttonList.add(new AlyaButton(0, this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options")));
        this.buttonList.add(new AlyaButton(4, this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("menu.quit")));

        synchronized(this.threadLock) {
            int field_92023_s = (int) font.getStringWidth(this.openGLWarning1);
            this.field_92024_r = (int) font.getStringWidth(this.openGLWarning2);
            int k = Math.max(field_92023_s, this.field_92024_r);
            this.field_92022_t = (this.width - k) / 2;
            this.field_92021_u = this.buttonList.get(0).yPosition - 24;
            this.field_92020_v = this.field_92022_t + k;
            this.field_92019_w = this.field_92021_u + 24;
        }

        this.mc.func_181537_a(false);
    }

    private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
        this.buttonList.add(new AlyaButton(1, this.width / 2 - 100, p_73969_1_, I18n.format("menu.singleplayer")));
        this.buttonList.add(new AlyaButton(2, this.width / 2 - 100, p_73969_1_ + p_73969_2_, I18n.format("menu.multiplayer")));

        if(Reflector.GuiModList_Constructor.exists()) {
            this.buttonList.add(new AlyaButton(14, this.width / 2 + 2, p_73969_1_ + p_73969_2_ * 2, 98, 20, I18n.format("menu.online").replace("Minecraft", "").trim()));
            this.buttonList.add(new AlyaButton(6, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, 98, 20, I18n.format("fml.menu.mods")));
        } else {
            this.buttonList.add(new AlyaButton(14, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, I18n.format("menu.online")));
        }
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }

        if(button.id == 5) {
            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }

        if(button.id == 1) {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }

        if(button.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }

        if(button.id == 14) {
            this.mc.displayGuiScreen(new AltManagerGui());
        }

        if(button.id == 4) {
            this.mc.shutdown();
        }

        if(button.id == 6 && Reflector.GuiModList_Constructor.exists()) {
            this.mc.displayGuiScreen((GuiScreen) Reflector.newInstance(Reflector.GuiModList_Constructor, new Object[]{this}));
        }

        if(button.id == 12) {
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

            if(worldinfo != null) {
                GuiYesNo guiyesno = GuiSelectWorld.func_152129_a(this, worldinfo.getWorldName(), 12);
                this.mc.displayGuiScreen(guiyesno);
            }
        }
    }

    public void confirmClicked(boolean result, int id) {
        if(result && id == 12) {
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            isaveformat.flushCache();
            isaveformat.deleteWorldDirectory("Demo_World");
            this.mc.displayGuiScreen(this);
        } else if(id == 13) {
            if(result) {
                try {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
                    oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, new URI(this.openGLWarningLink));
                } catch(Throwable throwable) {
                    logger.error("Couldn't open link", throwable);
                }
            }

            this.mc.displayGuiScreen(this);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (menuShader == null) menuShader = new ShaderUtil("Alya/Shaders/MainMenuBg.glsl");
        menuShader.render();

        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        int firstButtonY = this.height / 4 + 48;
        int availableHeight = firstButtonY - 10;
        int scaledSize = Math.min(140, availableHeight - 20);
        int logoX = (this.width - scaledSize) / 2;
        int logoY = Math.max(10, (availableHeight - scaledSize) / 2);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation("Alya/Assets/GUI/logo.png"));

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        drawModalRectWithCustomSizedTexture(logoX, logoY, 0, 0, scaledSize, scaledSize, scaledSize, scaledSize);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        if(Reflector.FMLCommonHandler_getBrandings.exists()) {
            Object object = Reflector.call(Reflector.FMLCommonHandler_instance);
            List<String> list = Lists.<String>reverse((List) Reflector.call(object, Reflector.FMLCommonHandler_getBrandings, new Object[]{Boolean.TRUE}));

            for(int l1 = 0; l1 < list.size(); ++l1) {
                String s1 = list.get(l1);

                if(!Strings.isNullOrEmpty(s1)) {
                    this.drawString(s1, 2, this.height - (10 + l1 * ((int) font.getFontHeight() + 1)), 16777215);
                }
            }

            if(Reflector.ForgeHooksClient_renderMainMenu.exists()) {
                Reflector.call(Reflector.ForgeHooksClient_renderMainMenu, this, this.fontRendererObj, this.width, this.height);
            }
        }
        if(this.openGLWarning1 != null && !this.openGLWarning1.isEmpty()) {
            drawRect(this.field_92022_t - 2, this.field_92021_u - 2, this.field_92020_v + 2, this.field_92019_w - 1, 1428160512);
            this.drawString(this.openGLWarning1, this.field_92022_t, this.field_92021_u, -1);
            this.drawString(this.openGLWarning2, (this.width - this.field_92024_r) / 2, this.buttonList.get(0).yPosition - 12, -1);
        }

        if(this.randomImage != null) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(this.randomImage);
            final int width = 50;
            final int height = 70;
            drawModalRectWithCustomSizedTexture(this.width - width, this.height - height, 0, 0, width, height, width, height);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        if(this.modUpdateNotification != null) {
            this.modUpdateNotification.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        synchronized(this.threadLock) {
            if(!this.openGLWarning1.isEmpty() && mouseX >= this.field_92022_t && mouseX <= this.field_92020_v && mouseY >= this.field_92021_u && mouseY <= this.field_92019_w) {
                GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
                guiconfirmopenlink.disableSecurityWarning();
                this.mc.displayGuiScreen(guiconfirmopenlink);
            }
        }
    }

    public void onGuiClosed() {
    }


}

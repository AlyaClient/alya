package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import dev.thoq.Alya;
import dev.thoq.alya.BuildConfig;
import dev.thoq.gui.auth.AltManagerGui;
import dev.thoq.util.font.AlyaFontRenderer;
import dev.thoq.util.misc.BrowserUtil;
import dev.thoq.util.render.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.optifine.reflect.Reflector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SuppressWarnings({
        "SameParameterValue",
        "unchecked",
        "rawtypes",
        "DataFlowIssue",
        "unused"
})
public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {

    private static final Logger logger = LogManager.getLogger(GuiMainMenu.class);
    private static final Random RANDOM = new Random();
    private static ShaderUtil menuShader = null;

    private final boolean field_175375_v = true;

    private final Object threadLock = new Object();

    private String openGLWarning1;

    private String openGLWarning2;

    private String openGLWarningLink;
    public static final String field_96138_a =
            "Please click "
                    + EnumChatFormatting.UNDERLINE
                    + "here"
                    + EnumChatFormatting.RESET
                    + " for more information.";
    private int field_92024_r;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;

    private GuiScreen modUpdateNotification;

    private static ResourceLocation randomImage = null;
    private int imageX, imageY, imageW, imageH;

    public GuiMainMenu() {
        this.openGLWarning2 = field_96138_a;
        this.openGLWarning1 = "";

        if(!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
            this.openGLWarning1 = I18n.format("title.oldgl1");
            this.openGLWarning2 = I18n.format("title.oldgl2");
            this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    @Override
    public void initGui() {
        if(randomImage == null) {
            int n = RANDOM.nextInt(12) + 1;
            randomImage = new ResourceLocation("client/assets/femboys/" + n + ".png");
        }

        final DynamicTexture viewportTexture = new DynamicTexture(256, 256);
        ResourceLocation backgroundTexture =
                this.mc.getTextureManager().getDynamicTextureLocation("background", viewportTexture);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        final int i = 24;
        final int j = this.height / 4 + 48;

        this.addSingleplayerMultiplayerButtons(j, 24);

        int optionsWidth = 24 + (int) Alya.getInstance().getFontRendererSmall().getStringWidth(I18n.format("menu.options").replace("...", "")) + 4;
        int langWidth = 24 + (int) Alya.getInstance().getFontRendererSmall().getStringWidth(I18n.format("options.language").replace("...", "")) + 4;
        this.buttonList.add(
                new GuiButton(5, this.width - 24 - 4 - optionsWidth - 2 - langWidth - 2, 4, langWidth, 24, "") {
                    @Override
                    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                        if(this.visible) {
                            this.hovered =
                                    mouseX >= this.xPosition
                                            && mouseY >= this.yPosition
                                            && mouseX < this.xPosition + this.width
                                            && mouseY < this.yPosition + this.height;
                            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                            mc.getTextureManager().bindTexture(new ResourceLocation("client/icons/language.png"));
                            drawModalRectWithCustomSizedTexture(this.xPosition + 4, this.yPosition + 4, 0, 0, 16, 16, 16, 16);

                            String text = I18n.format("options.language").replace("...", "");
                            AlyaFontRenderer font = Alya.getInstance().getFontRendererSmall();
                            float textY = this.yPosition + (this.height - font.getHeight()) / 2.0F + 1.0F;
                            font.drawString(text, this.xPosition + 22, textY, -1);
                            this.mouseDragged(mc, mouseX, mouseY);
                        }
                    }
                });
        this.buttonList.add(
                new GuiButton(0, this.width - 24 - 4 - optionsWidth - 2, 4, optionsWidth, 24, "") {
                    @Override
                    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                        if(this.visible) {
                            this.hovered =
                                    mouseX >= this.xPosition
                                            && mouseY >= this.yPosition
                                            && mouseX < this.xPosition + this.width
                                            && mouseY < this.yPosition + this.height;
                            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                            mc.getTextureManager().bindTexture(new ResourceLocation("client/icons/options.png"));
                            drawModalRectWithCustomSizedTexture(this.xPosition + 4, this.yPosition + 4, 0, 0, 16, 16, 16, 16);

                            String text = I18n.format("menu.options").replace("...", "");
                            AlyaFontRenderer font = Alya.getInstance().getFontRendererSmall();
                            float textY = this.yPosition + (this.height - font.getHeight()) / 2.0F + 1.0F;
                            font.drawString(text, this.xPosition + 22, textY, -1);
                            this.mouseDragged(mc, mouseX, mouseY);
                        }
                    }
                });
        this.buttonList.add(
                new GuiButton(4, this.width - 24 - 2, 4, 24, 24, "") {
                    @Override
                    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                        if(this.visible) {
                            this.hovered =
                                    mouseX >= this.xPosition
                                            && mouseY >= this.yPosition
                                            && mouseX < this.xPosition + this.width
                                            && mouseY < this.yPosition + this.height;
                            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                            mc.getTextureManager().bindTexture(new ResourceLocation("client/icons/close.png"));
                            drawModalRectWithCustomSizedTexture(this.xPosition + 4, this.yPosition + 4, 0, 0, 16, 16, 16, 16);
                            this.mouseDragged(mc, mouseX, mouseY);
                        }
                    }
                });

        synchronized(this.threadLock) {
            int field_92023_s = font.getStringWidth(this.openGLWarning1);
            this.field_92024_r = font.getStringWidth(this.openGLWarning2);
            int k = Math.max(field_92023_s, this.field_92024_r);
            this.field_92022_t = (this.width - k) / 2;
            this.field_92021_u = this.buttonList.getFirst().yPosition - 24;
            this.field_92020_v = this.field_92022_t + k;
            this.field_92019_w = this.field_92021_u + 24;
        }

        this.mc.func_181537_a(false);
    }

    private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
        final GuiButton singleplayer = new GuiButton(1, this.width / 2 - 75, p_73969_1_, 150, 20, I18n.format("menu.singleplayer"));
        singleplayer.customFont = Alya.getInstance().getFontRendererMedium();
        this.buttonList.add(singleplayer);

        final GuiButton multiplayer = new GuiButton(2, this.width / 2 - 75, p_73969_1_ + p_73969_2_, 150, 20, I18n.format("menu.multiplayer"));
        multiplayer.customFont = Alya.getInstance().getFontRendererMedium();
        this.buttonList.add(multiplayer);
        final GuiButton alts = new GuiButton(
                14, this.width / 2 - 75, p_73969_1_ + p_73969_2_ * 2, 150, 20, I18n.format("menu.alts"));
        alts.customFont = Alya.getInstance().getFontRendererMedium();
        this.buttonList.add(alts);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }

        if(button.id == 5) {
            this.mc.displayGuiScreen(
                    new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
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
            this.mc.displayGuiScreen(
                    (GuiScreen) Reflector.newInstance(Reflector.GuiModList_Constructor, new Object[]{this}));
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if(result) {
            BrowserUtil.open(this.openGLWarningLink);
        }

        this.mc.displayGuiScreen(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(menuShader == null) menuShader = new ShaderUtil("client/shaders/main_menu_bg.glsl");
        menuShader.render();

        int firstButtonY = this.height / 4 + 48;
        int availableHeight = firstButtonY - 10;
        int scaledSize = Math.min(140, availableHeight - 20);
        int logoX = (this.width - scaledSize) / 2;
        int logoY = Math.max(10, (availableHeight - scaledSize) / 2);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation("client/assets/gui/logo.png"));
        drawModalRectWithCustomSizedTexture(logoX, logoY, 0, 0, scaledSize, scaledSize, scaledSize, scaledSize);

        if(Reflector.FMLCommonHandler_getBrandings.exists()) {
            Object object = Reflector.call(Reflector.FMLCommonHandler_instance);
            List<String> list =
                    Lists.<String>reverse(
                            (List)
                                    Reflector.call(
                                            object,
                                            Reflector.FMLCommonHandler_getBrandings,
                                            new Object[]{Boolean.TRUE}));

            for(int l1 = 0; l1 < list.size(); ++l1) {
                String s1 = list.get(l1);

                if(!Strings.isNullOrEmpty(s1)) {
                    this.drawString(
                            s1, 2, this.height - (10 + l1 * ((int) font.getFontHeight() + 1)), 16777215);
                }
            }

            if(Reflector.ForgeHooksClient_renderMainMenu.exists()) {
                Reflector.call(
                        Reflector.ForgeHooksClient_renderMainMenu,
                        this,
                        this.fontRendererObj,
                        this.width,
                        this.height);
            }
        }
        if(this.openGLWarning1 != null && !this.openGLWarning1.isEmpty()) {
            drawRect(
                    this.field_92022_t - 2,
                    this.field_92021_u - 2,
                    this.field_92020_v + 2,
                    this.field_92019_w - 1,
                    1428160512);
            this.drawString(this.openGLWarning1, this.field_92022_t, this.field_92021_u, -1);
            this.drawString(
                    this.openGLWarning2,
                    (this.width - this.field_92024_r) / 2,
                    this.buttonList.getFirst().yPosition - 12,
                    -1);
        }

        if(randomImage != null) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(randomImage);
            final int texW = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
            final int texH = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
            final int maxSize = 150;
            final float scale = Math.min((float) maxSize / texW, (float) maxSize / texH);
            final int drawW = (int) (texW * scale);
            final int drawH = (int) (texH * scale);
            this.imageX = this.width - drawW;
            this.imageY = this.height - drawH;
            this.imageW = drawW;
            this.imageH = drawH;
            drawModalRectWithCustomSizedTexture(
                    this.imageX, this.imageY, 0, 0, drawW, drawH, drawW, drawH);
        }

        Alya.getInstance().getFontRendererSmall().drawStringWithShadow("Alya Client (#" + BuildConfig.GIT_HASH + ")", 2, this.height - 10, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);

        if(this.modUpdateNotification != null) {
            this.modUpdateNotification.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(randomImage != null
                && mouseX >= this.imageX
                && mouseX <= this.imageX + this.imageW
                && mouseY >= this.imageY
                && mouseY <= this.imageY + this.imageH) {
            BrowserUtil.open("https://alya.thoq.dev");
        }

        synchronized(this.threadLock) {
            if(!this.openGLWarning1.isEmpty()
                    && mouseX >= this.field_92022_t
                    && mouseX <= this.field_92020_v
                    && mouseY >= this.field_92021_u
                    && mouseY <= this.field_92019_w) {
                GuiConfirmOpenLink guiconfirmopenlink =
                        new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
                guiconfirmopenlink.disableSecurityWarning();
                this.mc.displayGuiScreen(guiconfirmopenlink);
            }
        }
    }

    public static ResourceLocation getRandomImage() {
        return randomImage;
    }


}

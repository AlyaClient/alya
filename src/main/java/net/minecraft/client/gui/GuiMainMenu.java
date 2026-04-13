package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import bypass.Alya;
import bypass.BuildConfig;
import bypass.gui.auth.AltManagerGui;
import bypass.util.font.AlyaFontRenderer;
import bypass.util.misc.BrowserUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomPanorama;
import net.optifine.CustomPanoramaProperties;
import net.optifine.reflect.Reflector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

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
    private float updateCounter;
    private int panoramaTimer;
    private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[]{new ResourceLocation("client/panorama/panorama_0.png"), new ResourceLocation("client/panorama/panorama_1.png"), new ResourceLocation("client/panorama/panorama_2.png"), new ResourceLocation("client/panorama/panorama_3.png"), new ResourceLocation("client/panorama/panorama_4.png"), new ResourceLocation("client/panorama/panorama_5.png")};
    private final boolean field_175375_v = true;
    private ResourceLocation backgroundTexture;
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
    private static final int PHOTOS = 13;

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
    public void updateScreen() {
        ++this.panoramaTimer;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    @Override
    public void initGui() {
        if(randomImage == null) {
            int n = RANDOM.nextInt(PHOTOS) + 1;
            randomImage = new ResourceLocation("client/assets/femboys/" + n + ".png");
        }

        final DynamicTexture viewportTexture1 = new DynamicTexture(256, 256);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", viewportTexture1);

        final DynamicTexture viewportTexture = new DynamicTexture(256, 256);
        ResourceLocation backgroundTexture =
                this.mc.getTextureManager().getDynamicTextureLocation("background", viewportTexture);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        final int i = 24;
        final int j = this.height / 2 + 10;

        this.addSingleplayerMultiplayerButtons(j, 24);

        int optionsWidth = 24 + (int) Alya.getInstance().getFontRendererSmall().getStringWidth(I18n.format("menu.options").replace("...", "")) + 4;
        int langWidth = 24 + (int) Alya.getInstance().getFontRendererSmall().getStringWidth(I18n.format("options.language").replace("...", "")) + 4;
        int senseWidth = 24 + (int) Alya.getInstance().getFontRendererSmall().getStringWidth("Sense") + 4;
        int discordWidth = 24 + (int) Alya.getInstance().getFontRendererSmall().getStringWidth("Discord") + 4;
        int langX = this.width - 24 - 4 - optionsWidth - 2 - langWidth - 2;
        int senseX = langX - senseWidth - 2;
        int discordX = senseX - discordWidth - 2;
        this.buttonList.add(
                new GuiButton(10, senseX, 4, senseWidth, 24, "") {
                    @Override
                    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                        if(this.visible) {
                            this.hovered =
                                    mouseX >= this.xPosition
                                            && mouseY >= this.yPosition
                                            && mouseX < this.xPosition + this.width
                                            && mouseY < this.yPosition + this.height;
                            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                            mc.getTextureManager().bindTexture(new ResourceLocation(mc.gameSettings.sense ? "client/icons/sense.png" : "client/icons/no_sense.png"));
                            drawModalRectWithCustomSizedTexture(this.xPosition + 4, this.yPosition + 4, 0, 0, 16, 16, 16, 16);

                            String text = "Sense";
                            AlyaFontRenderer font = Alya.getInstance().getFontRendererSmall();
                            float textY = this.yPosition + (this.height - font.getHeight()) / 2.0F + 1.0F;
                            font.drawString(text, this.xPosition + 22, textY, -1);
                            this.mouseDragged(mc, mouseX, mouseY);
                        }
                    }
                });
        this.buttonList.add(
                new GuiButton(11, discordX, 4, discordWidth, 24, "") {
                    @Override
                    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                        if(this.visible) {
                            this.hovered =
                                    mouseX >= this.xPosition
                                            && mouseY >= this.yPosition
                                            && mouseX < this.xPosition + this.width
                                            && mouseY < this.yPosition + this.height;
                            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                            mc.getTextureManager().bindTexture(new ResourceLocation("client/icons/discord.png"));
                            drawModalRectWithCustomSizedTexture(this.xPosition + 4, this.yPosition + 4, 0, 0, 16, 16, 16, 16);

                            String text = "Discord";
                            AlyaFontRenderer font = Alya.getInstance().getFontRendererSmall();
                            float textY = this.yPosition + (this.height - font.getHeight()) / 2.0F + 1.0F;
                            font.drawString(text, this.xPosition + 22, textY, -1);
                            this.mouseDragged(mc, mouseX, mouseY);
                        }
                    }
                });
        this.buttonList.add(
                new GuiButton(5, langX, 4, langWidth, 24, "") {
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
    public void confirmClicked(boolean result, int id) {
        if(result) {
            BrowserUtil.open(this.openGLWarningLink);
        }

        this.mc.displayGuiScreen(this);
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

        if(button.id == 10) {
            this.mc.gameSettings.sense = !this.mc.gameSettings.sense;
            this.mc.gameSettings.saveOptions();
        }

        if(button.id == 11) {
            BrowserUtil.open("https://discord.gg/J3XUnGaZjQ");
        }

        if(button.id == 4) {
            this.mc.shutdown();
        }

        if(button.id == 6 && Reflector.GuiModList_Constructor.exists()) {
            this.mc.displayGuiScreen(
                    (GuiScreen) Reflector.newInstance(Reflector.GuiModList_Constructor, new Object[]{this}));
        }
    }

    private void drawPanorama(int p_73970_1_, int p_73970_2_, float p_73970_3_) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        int i = 8;
        int j = 64;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

        if(custompanoramaproperties != null) {
            j = custompanoramaproperties.getBlur1();
        }

        for(int k = 0; k < j; ++k) {
            GlStateManager.pushMatrix();
            float f = ((float) (k % i) / (float) i - 0.5F) / 64.0F;
            float f1 = ((float) (k / i) / (float) i - 0.5F) / 64.0F;
            float f2 = 0.0F;
            GlStateManager.translate(f, f1, f2);
            GlStateManager.rotate(MathHelper.sin(((float) this.panoramaTimer + p_73970_3_) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-((float) this.panoramaTimer + p_73970_3_) * 0.1F, 0.0F, 1.0F, 0.0F);

            for(int l = 0; l < 6; ++l) {
                GlStateManager.pushMatrix();

                if(l == 1) {
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if(l == 2) {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if(l == 3) {
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if(l == 4) {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if(l == 5) {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                ResourceLocation[] aresourcelocation = titlePanoramaPaths;

                if(custompanoramaproperties != null) {
                    aresourcelocation = custompanoramaproperties.getPanoramaLocations();
                }

                this.mc.getTextureManager().bindTexture(aresourcelocation[l]);
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int i1 = 255 / (k + 1);
                float f3 = 0.0F;
                worldrenderer.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, i1).endVertex();
                worldrenderer.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, i1).endVertex();
                worldrenderer.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, i1).endVertex();
                worldrenderer.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, i1).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }

        worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }

    private void rotateAndBlurSkybox(float p_73968_1_) {
        this.mc.getTextureManager().bindTexture(this.backgroundTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.colorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        GlStateManager.disableAlpha();
        int i = 3;
        int j = 3;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

        if(custompanoramaproperties != null) {
            j = custompanoramaproperties.getBlur2();
        }

        for(int k = 0; k < j; ++k) {
            float f = 1.0F / (float) (k + 1);
            int l = this.width;
            int i1 = this.height;
            float f1 = (float) (k - i / 2) / 256.0F;
            worldrenderer.pos(l, i1, this.zLevel).tex(0.0F + f1, 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos(l, 0.0D, this.zLevel).tex(1.0F + f1, 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos(0.0D, 0.0D, this.zLevel).tex(1.0F + f1, 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos(0.0D, i1, this.zLevel).tex(0.0F + f1, 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);
    }

    private void renderSkybox(int p_73971_1_, int p_73971_2_, float p_73971_3_) {
        this.mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        this.drawPanorama(p_73971_1_, p_73971_2_, p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        int i = 3;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

        if(custompanoramaproperties != null) {
            i = custompanoramaproperties.getBlur3();
        }

        for(int j = 0; j < i; ++j) {
            this.rotateAndBlurSkybox(p_73971_3_);
            this.rotateAndBlurSkybox(p_73971_3_);
        }

        this.mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        float f2 = this.width > this.height ? 120.0F / (float) this.width : 120.0F / (float) this.height;
        float f = (float) this.height * f2 / 256.0F;
        float f1 = (float) this.width * f2 / 256.0F;
        int k = this.width;
        int l = this.height;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0D, l, this.zLevel).tex(0.5F - f, 0.5F + f1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(k, l, this.zLevel).tex(0.5F - f, 0.5F - f1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(k, 0.0D, this.zLevel).tex(0.5F + f, 0.5F - f1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(0.0D, 0.0D, this.zLevel).tex(0.5F + f, 0.5F + f1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableAlpha();
        this.renderSkybox(mouseX, mouseY, partialTicks);
        GlStateManager.enableAlpha();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        int i = 274;
        int j = this.width / 2 - i / 2;
        int k = 30;
        int l = -2130706433;
        int i1 = 16777215;
        int j1 = 0;
        int k1 = Integer.MIN_VALUE;
        int scaledSize = 140;
        int buttonY = this.height / 2 + 10;
        int maxLogoSize = buttonY - 20;
        scaledSize = Math.min(scaledSize, maxLogoSize);
        int logoX = (this.width - scaledSize) / 2;
        int logoY = Math.min(80, buttonY - scaledSize - 10);

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

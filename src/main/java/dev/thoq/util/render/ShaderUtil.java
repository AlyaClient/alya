package dev.thoq.util.render;

import dev.thoq.Alya;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class ShaderUtil {
    private int programId = -1;
    private int timeUniform = -1;
    private int resolutionUniform = -1;
    private int mouseUniform = -1;
    private int mouseVelocityUniform = -1;
    private int deltaTimeUniform = -1;
    private int logoRectUniform = -1;
    private final long startTime;
    private boolean initialized = false;
    private final String shaderPath;
    private float mouseX = 0.5f;
    private float mouseY = 0.5f;
    private long lastFrameTime = 0;
    private float logoRectX = -9999f;
    private float logoRectY = -9999f;
    private float logoRectW = 1f;
    private float logoRectH = 1f;

    public ShaderUtil(final String shaderPath) {
        this.shaderPath = shaderPath;
        this.startTime = System.currentTimeMillis();
    }

    public boolean init() {
        if(initialized) {
            return true;
        }
        try {
            final ResourceLocation location = new ResourceLocation("minecraft", shaderPath);
            final InputStream inputStream =
                    Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
            final String shaderSource = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            inputStream.close();
            final String vertexSource =
                    "#version 120\n"
                            + "void main() {\n"
                            + "    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n"
                            + "    gl_TexCoord[0] = gl_MultiTexCoord0;\n"
                            + "}\n";
            final int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
            GL20.glShaderSource(vertexShader, vertexSource);
            GL20.glCompileShader(vertexShader);
            if(GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                System.err.println(
                        "[Alya] Vertex shader compilation failed: "
                                + GL20.glGetShaderInfoLog(vertexShader, 1024));
                return false;
            }
            final int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
            GL20.glShaderSource(fragmentShader, shaderSource);
            GL20.glCompileShader(fragmentShader);
            if(GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                System.err.println(
                        "[Alya] Fragment shader compilation failed: "
                                + GL20.glGetShaderInfoLog(fragmentShader, 1024));
                return false;
            }
            programId = GL20.glCreateProgram();
            GL20.glAttachShader(programId, vertexShader);
            GL20.glAttachShader(programId, fragmentShader);
            GL20.glLinkProgram(programId);
            if(GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
                System.err.println(
                        "[Alya] Shader program linking failed: " + GL20.glGetProgramInfoLog(programId, 1024));
                return false;
            }
            timeUniform = GL20.glGetUniformLocation(programId, "time");
            resolutionUniform = GL20.glGetUniformLocation(programId, "resolution");
            mouseUniform = GL20.glGetUniformLocation(programId, "mouse");
            mouseVelocityUniform = GL20.glGetUniformLocation(programId, "mouseVelocity");
            deltaTimeUniform = GL20.glGetUniformLocation(programId, "deltaTime");
            logoRectUniform = GL20.glGetUniformLocation(programId, "logoRect");
            GL20.glDeleteShader(vertexShader);
            GL20.glDeleteShader(fragmentShader);
            initialized = true;
            return true;
        } catch(final Exception exception) {
            Alya.getInstance().getLogger().error("Failed to load shader: {}", shaderPath, exception);
            return false;
        }
    }

    public void render() {
        if(!initialized && !init()) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        final float time = (System.currentTimeMillis() - startTime) / 1000.0f;
        long currentTime = System.currentTimeMillis();
        float deltaTime = lastFrameTime == 0 ? 0.016f : (currentTime - lastFrameTime) / 1000.0f;
        deltaTime = Math.min(deltaTime, 0.1f);
        lastFrameTime = currentTime;
        float prevMouseX = mouseX;
        float prevMouseY = mouseY;
        mouseX = (float) Mouse.getX() / mc.displayWidth;
        mouseY = (float) Mouse.getY() / mc.displayHeight;
        float velX = mouseX - prevMouseX;
        float velY = mouseY - prevMouseY;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 770, 771);
        GlStateManager.enableAlpha();
        GL20.glUseProgram(programId);
        if(timeUniform != -1) {
            GL20.glUniform1f(timeUniform, time);
        }
        if(resolutionUniform != -1) {
            GL20.glUniform2f(resolutionUniform, mc.displayWidth, mc.displayHeight);
        }
        if(mouseUniform != -1) {
            GL20.glUniform2f(mouseUniform, mouseX, mouseY);
        }
        if(mouseVelocityUniform != -1) {
            GL20.glUniform2f(mouseVelocityUniform, velX, velY);
        }
        if(deltaTimeUniform != -1) {
            GL20.glUniform1f(deltaTimeUniform, deltaTime);
        }
        if(logoRectUniform != -1) {
            GL20.glUniform4f(logoRectUniform, logoRectX, logoRectY, logoRectW, logoRectH);
        }
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(0, 0);
        GL11.glVertex2f(0, scaledResolution.getScaledHeight());
        GL11.glVertex2f(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
        GL11.glVertex2f(scaledResolution.getScaledWidth(), 0);
        GL11.glEnd();
        GL20.glUseProgram(0);
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public void setLogoRect(float x, float y, float w, float h) {
        this.logoRectX = x;
        this.logoRectY = y;
        this.logoRectW = w;
        this.logoRectH = h;
    }

    public boolean isInitialized() {
        return initialized;
    }
}

package dev.thoq.gui.auth;

import com.google.gson.*;
import dev.thoq.Alya;
import net.minecraft.client.Minecraft;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public final class AltStorage {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final AltStorage INSTANCE = new AltStorage();
    private final List<AltEntry> alts = new ArrayList<>();
    private String encryptionPassword = null;
    private boolean locked = false;
    private boolean unlocked = false;

    private AltStorage() {
    }

    public static AltStorage getInstance() {
        return INSTANCE;
    }

    public List<AltEntry> getAlts() {
        return alts;
    }

    public void addAlt(final AltEntry alt) {
        alts.add(alt);
        save();
    }

    public void removeAlt(final AltEntry alt) {
        alts.remove(alt);
        save();
    }

    public boolean hasPassword() {
        return getStorageFile().exists() && isFileEncrypted();
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setPassword(final String password) {
        this.encryptionPassword = password;
        save();
    }

    public void clearPassword() {
        this.encryptionPassword = null;
        save();
    }

    public boolean unlock(final String password) {
        this.encryptionPassword = password;
        try {
            load();
            locked = false;
            unlocked = true;
            return true;
        } catch(final Exception exception) {
            this.encryptionPassword = null;
            locked = true;
            unlocked = false;
            Alya.getInstance().getLogger().error("[AltStorage] Wrong password or corrupt file", exception);
            return false;
        }
    }

    private File getStorageFile() {
        final File dir = new File(Minecraft.getMinecraft().mcDataDir, Alya.getName());
        if(!dir.exists()) { //noinspection ResultOfMethodCallIgnored, todo
            dir.mkdirs();
        }
        return new File(dir, "alts.dat");
    }

    private boolean isFileEncrypted() {
        try {
            final String raw = Files.readString(getStorageFile().toPath());
            return raw.startsWith("ENC:");
        } catch(final Exception exception) {
            return false;
        }
    }

    public void save() {
        try {
            final JsonArray array = new JsonArray();
            for(final AltEntry alt : alts) {
                final JsonObject obj = new JsonObject();
                obj.addProperty("name", alt.getName());
                obj.addProperty("uuid", alt.getUuid());
                obj.addProperty("accessToken", alt.getAccessToken());
                obj.addProperty("type", alt.getType().name());
                obj.addProperty("favorite", alt.isFavorite());
                array.add(obj);
            }
            final String json = GSON.toJson(array);

            if(encryptionPassword != null && !encryptionPassword.isEmpty()) {
                final String encrypted = encrypt(json, encryptionPassword);
                Files.writeString(getStorageFile().toPath(), "ENC:" + encrypted);
            } else {
                Files.writeString(getStorageFile().toPath(), json);
            }
        } catch(final Exception exception) {
            Alya.getInstance().getLogger().error("[AltStorage] Failed to save alts", exception);
        }
    }

    public void load() {
        final File file = getStorageFile();
        if(!file.exists()) {
            return;
        }
        try {
            final String raw = Files.readString(file.toPath());
            final String json;
            if(raw.startsWith("ENC:")) {
                if(encryptionPassword == null || encryptionPassword.isEmpty()) {
                    locked = true;
                    return;
                }
                json = decrypt(raw.substring(4), encryptionPassword);
            } else {
                json = raw;
            }
            alts.clear();
            final JsonArray array = JsonParser.parseString(json).getAsJsonArray();
            for(final JsonElement jsonElement : array) {
                final JsonObject jsonObject = jsonElement.getAsJsonObject();
                final AltEntry entry = new AltEntry(
                        jsonObject.get("name").getAsString(),
                        jsonObject.get("uuid").getAsString(),
                        jsonObject.get("accessToken").getAsString(),
                        AltEntry.Type.valueOf(jsonObject.get("type").getAsString()),
                        jsonObject.has("favorite") && jsonObject.get("favorite").getAsBoolean()
                );
                alts.add(entry);
            }
            locked = false;
        } catch(final Exception exception) {
            Alya.getInstance().getLogger().error("[AltStorage] Failed to load alts", exception);
            throw new RuntimeException(exception);
        }
    }

    private static String encrypt(final String plaintext, final String password) throws Exception {
        final SecureRandom secureRandom = new SecureRandom();
        final byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        final byte[] iv = new byte[12];
        secureRandom.nextBytes(iv);

        final SecretKeySpec key = deriveKey(password, salt);
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
        final byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        final byte[] out = new byte[salt.length + iv.length + ciphertext.length];
        System.arraycopy(salt, 0, out, 0, salt.length);
        System.arraycopy(iv, 0, out, salt.length, iv.length);
        System.arraycopy(ciphertext, 0, out, salt.length + iv.length, ciphertext.length);
        return Base64.getEncoder().encodeToString(out);
    }

    private static String decrypt(final String encoded, final String password) throws Exception {
        final byte[] data = Base64.getDecoder().decode(encoded);
        final byte[] salt = new byte[16];
        final byte[] iv = new byte[12];
        final byte[] ciphertext = new byte[data.length - 28];
        System.arraycopy(data, 0, salt, 0, 16);
        System.arraycopy(data, 16, iv, 0, 12);
        System.arraycopy(data, 28, ciphertext, 0, ciphertext.length);

        final SecretKeySpec key = deriveKey(password, salt);
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
        final byte[] plaintext = cipher.doFinal(ciphertext);
        return new String(plaintext, StandardCharsets.UTF_8);
    }

    private static SecretKeySpec deriveKey(final String password, final byte[] salt) throws Exception {
        final KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        final byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public void resetAll() {
        alts.clear();
        encryptionPassword = null;
        locked = false;
        unlocked = false;
        final File file = getStorageFile();
        if(file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }
}

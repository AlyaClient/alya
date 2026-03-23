package dev.thoq.util.auth;
import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import dev.thoq.Alya;
import net.minecraft.util.Session;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.awt.*;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
@SuppressWarnings("HttpUrlsUsage")
public final class MicrosoftAuth {
    private static final AtomicReference<HttpServer> activeServer = new AtomicReference<>(null);
    public static final RequestConfig REQUEST_CONFIG = RequestConfig
            .custom()
            .setConnectionRequestTimeout(30_000)
            .setConnectTimeout(30_000)
            .setSocketTimeout(30_000)
            .build();
    public static final String CLIENT_ID = "42a60a84-599d-44b2-a7c6-b00cdef1d6a2";
    public static final int PORT = 25575;
    public static CompletableFuture<String> acquireMSAuthCode(final Executor executor) {
        return acquireMSAuthCode(MicrosoftAuth::openWebLink, executor);
    }
    public static CompletableFuture<String> acquireMSAuthCode(final Consumer<URI> browserAction, final Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                StringBuilder sb = new StringBuilder();
                SecureRandom random = new SecureRandom();
                random.ints(8, 0, 62).mapToObj("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"::charAt)
                        .forEach(sb::append);
                String state = sb.toString();
                HttpServer existing = activeServer.getAndSet(null);
                if (existing != null) {
                    existing.stop(0);
                }
                HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
                activeServer.set(server);
                CountDownLatch latch = new CountDownLatch(1);
                AtomicReference<String> authCode = new AtomicReference<>(null);
                AtomicReference<String> errorMsg = new AtomicReference<>(null);
                server.createContext("/callback", exchange -> {
                    Map<String, String> queryParams = URLEncodedUtils
                            .parse(exchange.getRequestURI().toString().replaceAll("/callback\\?", ""), StandardCharsets.UTF_8)
                            .stream().collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
                    if (!state.equals(queryParams.get("state"))) {
                        String em = String.format("State mismatch expected %s got %s", state, queryParams.get("state"));
                        errorMsg.set(em);
                        Alya.getInstance().getLogger().error("state mismatch {}", em);
                    } else if (queryParams.containsKey("code")) {
                        authCode.set(queryParams.get("code"));
                    } else if (queryParams.containsKey("error")) {
                        String em = String.format("%s %s", queryParams.get("error"), queryParams.get("error_description"));
                        errorMsg.set(em);
                        Alya.getInstance().getLogger().error("error from callback {}", em);
                    }
                    try {
                        InputStream stream = MicrosoftAuth.class.getResourceAsStream("/assets/minecraft/Alya/Assets/Web/auth_login_sucess.html");
                        byte[] respBytes = stream != null ? IOUtils.toByteArray(stream) : new byte[0];
                        exchange.getResponseHeaders().add("Content-Type", "text/html");
                        exchange.sendResponseHeaders(200, respBytes.length);
                        exchange.getResponseBody().write(respBytes);
                        exchange.getResponseBody().close();
                    } catch (Exception e) {
                        Alya.getInstance().getLogger().error("error writing response", e);
                    }
                    latch.countDown();
                });
                URI uri = new URIBuilder("https://login.live.com/oauth20_authorize.srf")
                        .addParameter("client_id", CLIENT_ID)
                        .addParameter("response_type", "code")
                        .addParameter("redirect_uri", String.format("http://localhost:%d/callback", server.getAddress().getPort()))
                        .addParameter("scope", "XboxLive.signin XboxLive.offline_access")
                        .addParameter("state", state)
                        .addParameter("prompt", "select_account")
                        .build();
                browserAction.accept(uri);
                server.start();
                latch.await();
                server.stop(0);
                activeServer.compareAndSet(server, null);
                String code = authCode.get();
                if (StringUtils.isBlank(code)) {
                    String err = Optional.ofNullable(errorMsg.get()).orElse("no auth or error");
                    Alya.getInstance().getLogger().error("no code or error present {}", err);
                    throw new Exception(err);
                }
                return code;
            } catch (InterruptedException ie) {
                Alya.getInstance().getLogger().warn("acquire interrupted");
                throw new CancellationException("acquire MS auth code cancelled");
            } catch (Exception e) {
                Alya.getInstance().getLogger().error("exception in acquireMSAuthCode", e);
                throw new CompletionException("unable to acquire MS auth code", e);
            }
        }, executor);
    }
    public static CompletableFuture<String> acquireMSAccessToken(final String authCode, final Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try (CloseableHttpClient client = HttpClients.createMinimal()) {
                HttpPost request = new HttpPost("https://login.live.com/oauth20_token.srf");
                request.setConfig(REQUEST_CONFIG);
                request.setHeader("Content-Type", "application/x-www-form-urlencoded");
                request.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                        new BasicNameValuePair("client_id", CLIENT_ID),
                        new BasicNameValuePair("grant_type", "authorization_code"),
                        new BasicNameValuePair("code", authCode),
                        new BasicNameValuePair("redirect_uri", String.format("http://localhost:%d/callback", PORT))
                ), StandardCharsets.UTF_8));
                org.apache.http.HttpResponse response = client.execute(request);
                JsonObject json = JsonParser.parseString(EntityUtils.toString(response.getEntity())).getAsJsonObject();
                String token = Optional.ofNullable(json.get("access_token"))
                        .map(JsonElement::getAsString)
                        .filter(t -> !StringUtils.isBlank(t))
                        .orElseThrow(() -> new Exception("no access token"));
                return token;
            } catch (Exception e) {
                Alya.getInstance().getLogger().error("error in acquireMSAccessToken", e);
                throw new CompletionException("unable to get MS access token", e);
            }
        }, executor);
    }
    public static CompletableFuture<String> acquireXboxAccessToken(final String accessToken, final Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try (CloseableHttpClient client = HttpClients.createMinimal()) {
                HttpPost request = new HttpPost("https://user.auth.xboxlive.com/user/authenticate");
                request.setConfig(REQUEST_CONFIG);
                request.setHeader("Content-Type", "application/json");
                JsonObject obj = new JsonObject();
                JsonObject props = new JsonObject();
                props.addProperty("AuthMethod", "RPS");
                props.addProperty("SiteName", "user.auth.xboxlive.com");
                props.addProperty("RpsTicket", String.format("d=%s", accessToken));
                obj.add("Properties", props);
                obj.addProperty("RelyingParty", "http://auth.xboxlive.com");
                obj.addProperty("TokenType", "JWT");
                request.setEntity(new StringEntity(obj.toString()));
                org.apache.http.HttpResponse res = client.execute(request);
                JsonObject json = JsonParser.parseString(EntityUtils.toString(res.getEntity())).getAsJsonObject();
                String token = Optional.ofNullable(json.get("Token")).map(JsonElement::getAsString)
                        .filter(t -> !StringUtils.isBlank(t))
                        .orElseThrow(() -> new Exception("no xbox token"));
                return token;
            } catch (Exception e) {
                Alya.getInstance().getLogger().error("error in acquireXboxAccessToken", e);
                throw new CompletionException("unable to get xbox access token", e);
            }
        }, executor);
    }
    public static CompletableFuture<Map<String, String>> acquireXboxXstsToken(final String accessToken, final Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try (CloseableHttpClient client = HttpClients.createMinimal()) {
                HttpPost request = new HttpPost("https://xsts.auth.xboxlive.com/xsts/authorize");
                request.setConfig(REQUEST_CONFIG);
                request.setHeader("Content-Type", "application/json");
                JsonObject entity = new JsonObject();
                JsonObject props = new JsonObject();
                JsonArray userTokens = new JsonArray();
                userTokens.add(new JsonPrimitive(accessToken));
                props.add("UserTokens", userTokens);
                props.addProperty("SandboxId", "RETAIL");
                entity.add("Properties", props);
                entity.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
                entity.addProperty("TokenType", "JWT");
                request.setEntity(new StringEntity(entity.toString()));
                org.apache.http.HttpResponse res = client.execute(request);
                JsonObject json = JsonParser.parseString(EntityUtils.toString(res.getEntity())).getAsJsonObject();
                String token = Optional.ofNullable(json.get("Token")).map(JsonElement::getAsString)
                        .filter(t -> !StringUtils.isBlank(t))
                        .orElseThrow(() -> new Exception("no xsts token"));
                String uhs = json.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray()
                        .get(0).getAsJsonObject().get("uhs").getAsString();
                Map<String, String> map = new HashMap<>();
                map.put("Token", token);
                map.put("uhs", uhs);
                return map;
            } catch (Exception e) {
                Alya.getInstance().getLogger().error("error in acquireXboxXstsToken", e);
                throw new CompletionException("unable to get xsts token", e);
            }
        }, executor);
    }
    public static CompletableFuture<String> acquireMCAccessToken(final String xstsToken, final String userHash, final Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try (CloseableHttpClient client = HttpClients.createMinimal()) {
                HttpPost request = new HttpPost("https://api.minecraftservices.com/authentication/login_with_xbox");
                request.setConfig(REQUEST_CONFIG);
                request.setHeader("Content-Type", "application/json");
                request.setEntity(new StringEntity(String.format("{\"identityToken\":\"XBL3.0 x=%s;%s\"}", userHash, xstsToken)));
                org.apache.http.HttpResponse res = client.execute(request);
                JsonObject json = JsonParser.parseString(EntityUtils.toString(res.getEntity())).getAsJsonObject();
                String token = Optional.ofNullable(json.get("access_token"))
                        .map(JsonElement::getAsString)
                        .filter(t -> !StringUtils.isBlank(t))
                        .orElseThrow(() -> new Exception("no mc access token"));
                return token;
            } catch (Exception e) {
                Alya.getInstance().getLogger().error("error in acquireMCAccessToken", e);
                throw new CompletionException("unable to get mc access token", e);
            }
        }, executor);
    }
    public static CompletableFuture<Session> login(final String mcToken, final Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try (CloseableHttpClient client = HttpClients.createMinimal()) {
                HttpGet request = new HttpGet("https://api.minecraftservices.com/minecraft/profile");
                request.setConfig(REQUEST_CONFIG);
                request.setHeader("Authorization", "Bearer " + mcToken);
                org.apache.http.HttpResponse res = client.execute(request);
                JsonObject json = JsonParser.parseString(EntityUtils.toString(res.getEntity())).getAsJsonObject();
                String uuid = Optional.ofNullable(json.get("id")).map(JsonElement::getAsString)
                        .filter(u -> !StringUtils.isBlank(u))
                        .orElseThrow(() -> new Exception("no profile present"));
                Session session = new Session(json.get("name").getAsString(), uuid, mcToken, Session.Type.MOJANG.toString());
                Alya.getInstance().getLogger().debug("login successful for {}", json.get("name").getAsString());
                return session;
            } catch (Exception e) {
                Alya.getInstance().getLogger().error("error in login", e);
                throw new CompletionException("unable to fetch profile", e);
            }
        }, executor);
    }
    public static void openWebLink(URI uri) {
        boolean opened = false;
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(uri);
                opened = true;
            } catch (Exception e) {
                Alya.getInstance().getLogger().error("desktop browse failed", e);
            }
        }
        if (!opened) {
            String os = System.getProperty("os.name").toLowerCase();
            try {
                ProcessBuilder pb = os.contains("linux")
                        ? new ProcessBuilder("xdg-open", uri.toString())
                        : os.contains("mac")
                        ? new ProcessBuilder("open", uri.toString())
                        : new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", uri.toString());
                pb.start();
            } catch (Exception e) {
                Alya.getInstance().getLogger().error("fallback open failed", e);
            }
        }
    }
}

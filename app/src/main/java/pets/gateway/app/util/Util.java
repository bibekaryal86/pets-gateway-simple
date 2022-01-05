package pets.gateway.app.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.Map;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {
    // provided at runtime
    public static final String SERVER_PORT = "PORT";
    public static final String PROFILE = "SPRING_PROFILES_ACTIVE";
    public static final String BASIC_AUTH_USR_PETSDATABASE = "BASIC_AUTH_USR_PETSDATABASE";
    public static final String BASIC_AUTH_PWD_PETSDATABASE = "BASIC_AUTH_PWD_PETSDATABASE";
    public static final String BASIC_AUTH_USR_PETSSERVICE = "BASIC_AUTH_USR_PETSSERVICE";
    public static final String BASIC_AUTH_PWD_PETSSERVICE = "BASIC_AUTH_PWD_PETSSERVICE";
    public static final String SECRET_KEY = "SECRET_KEY";

    // others
    public static final int SERVER_MAX_THREADS = 100;
    public static final int SERVER_MIN_THREADS = 20;
    public static final int SERVER_IDLE_TIMEOUT = 120;

    public static String getSystemEnvProperty(String keyName) {
        return (System.getProperty(keyName) != null) ? System.getProperty(keyName) : System.getenv(keyName);
    }

    public static boolean hasText(String s) {
        return (s != null && !s.trim().isEmpty());
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    public boolean shouldSkipField(FieldAttributes f) {
                        return (f == null);
                    }
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                }).create();
    }

    public static String getSecretKey() {
        return getSystemEnvProperty(SECRET_KEY);
    }

    public static Map<String, String> getPetsServiceAuthHeader() {
        String username = getSystemEnvProperty(BASIC_AUTH_USR_PETSSERVICE);
        String password = getSystemEnvProperty(BASIC_AUTH_PWD_PETSSERVICE);
        String authorization = Base64.getEncoder().encodeToString(String.format("%s:%s", username, password).getBytes());
        return Map.of("Authorization", String.format("Basic %s", authorization));
    }

    public static Map<String, String> getPetsDatabaseAuthHeader() {
        String username = getSystemEnvProperty(BASIC_AUTH_USR_PETSDATABASE);
        String password = getSystemEnvProperty(BASIC_AUTH_PWD_PETSDATABASE);
        String authorization = Base64.getEncoder().encodeToString(String.format("%s:%s", username, password).getBytes());
        return Map.of("Authorization", String.format("Basic %s", authorization));
    }

    public static Object getRequestBody(HttpServletRequest request, Class<?> clazz) {
        try {
            return getGson().fromJson(request.getReader(), clazz);
        } catch (Exception ex) {
            return null;
        }
    }
}

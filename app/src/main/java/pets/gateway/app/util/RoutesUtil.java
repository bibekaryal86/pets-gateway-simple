package pets.gateway.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pets.gateway.app.exception.CustomRuntimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoutesUtil {

    // the services
    private static final String PETS_DATABASE = "pets-database";
    private static final String PETS_SERVICE = "pets-service";
    private static final String PETS_AUTHENTICATE = "pets-authenticate";
    // development
    private static final String PETS_DB_BASE_DEV = "http://localhost:8002";
    private static final String PETS_SVC_BASE_DEV = "http://localhost:8003";
    private static final String PETS_AUTH_BASE_DEV = "http://localhost:8004";
    // docker
    private static final String PETS_DB_BASE_DOCKER = "http://pets-database:8002";
    private static final String PETS_SVC_BASE_DOCKER = "http://pets-service:8003";
    private static final String PETS_AUTH_BASE_DOCKER = "http://pets-authenticate:8004";
    // production
    private static final String PETS_DB_BASE_PROD = "https://pets-database.appspot.com";
    private static final String PETS_SVC_BASE_PROD = "https://pets-service.appspot.com";
    private static final String PETS_AUTH_BASE_PROD = "https://pets-authenticate.appspot.com";
    // the map
    private static Map<String, String> theRoutesMap = null;

    private static Map<String, String> setRoutesMap() {
        Map<String, String> routesMap = new HashMap<>();
        String profile = Util.getSystemEnvProperty(Util.PROFILE);

        String endpointBasePetsDatabase;
        String endpointBasePetsService;
        String endpointBasePetsAuthenticate;

        if (!Util.hasText(profile)) {
            throw new CustomRuntimeException("PROFILE NOT SET AT RUNTIME");
        }

        if ("development".equals(profile)) {
            endpointBasePetsDatabase = PETS_DB_BASE_DEV;
            endpointBasePetsService = PETS_SVC_BASE_DEV;
            endpointBasePetsAuthenticate = PETS_AUTH_BASE_DEV;
        } else if ("docker".equals(profile)) {
            endpointBasePetsDatabase = PETS_DB_BASE_DOCKER;
            endpointBasePetsService = PETS_SVC_BASE_DOCKER;
            endpointBasePetsAuthenticate = PETS_AUTH_BASE_DOCKER;
        } else {
            endpointBasePetsDatabase = PETS_DB_BASE_PROD;
            endpointBasePetsService = PETS_SVC_BASE_PROD;
            endpointBasePetsAuthenticate = PETS_AUTH_BASE_PROD;
        }

        routesMap.put(PETS_DATABASE, endpointBasePetsDatabase);
        routesMap.put(PETS_SERVICE, endpointBasePetsService);
        routesMap.put(PETS_AUTHENTICATE, endpointBasePetsAuthenticate);

        theRoutesMap = new HashMap<>();
        theRoutesMap.putAll(routesMap);

        return routesMap;
    }

    public static Map<String, String> routesMap() {
        return Objects.requireNonNullElseGet(theRoutesMap, RoutesUtil::setRoutesMap);
    }

    public static String getRoute(String requestUri) {
        try {
            String routeBase = requestUri.split("/")[1];
            return routesMap().get(routeBase);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Map<String, String> getRouteAuthHeader(String requestUri) {
        if (Util.AUTHORIZATION_NOT_NEEDED.contains(requestUri)) {
            return new HashMap<>();
        } else if (requestUri.contains(PETS_DATABASE)) {
            return Util.getPetsDatabaseAuthHeader();
        } else if (requestUri.contains(PETS_SERVICE)) {
            return Util.getPetsServiceAuthHeader();
        } else {
            return new HashMap<>();
        }
    }
}

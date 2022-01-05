package pets.gateway.app.gateway;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import pets.gateway.app.model.GatewayModel;
import pets.gateway.app.model.GatewayResponse;
import pets.gateway.app.util.ConnectorUtil;
import pets.gateway.app.util.RoutesUtil;
import pets.gateway.app.util.Util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class GatewayService {

    private String trace(HttpServletRequest request) {
        return Objects.requireNonNullElse(request.getAttribute("TRACE"), "TERROR").toString();
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();

            if (!Util.RESTRICTED_HEADERS.contains(key)) {
                String value = request.getHeader(key);
                headers.put(key, value);
            }
        }
        headers.putAll(RoutesUtil.getRouteAuthHeader(request.getRequestURI()));
        return headers;
    }

    public GatewayResponse gatewayService(HttpServletRequest request) {
        String trace = trace(request);
        String requestUri = request.getRequestURI();
        String routeBase = RoutesUtil.getRoute(requestUri);

        if (!Util.hasText(routeBase)) {
            log.info("[ {} ] | Route Not Found for requestURI: [ {} ]", trace, requestUri);
            return GatewayResponse.builder()
                    .statusCode(422)
                    .object(GatewayModel.builder()
                            .errMsg("Error! Route Not Found!! Please Try Again!!!")
                            .build())
                    .build();
        } else {
            String outgoingUrl = routeBase.concat(requestUri);
            String httpMethod = request.getMethod();
            Map<String, String> headers = getHeaders(request);
            Object bodyObject = Util.getRequestBody(request);
            return ConnectorUtil.sendHttpRequest(outgoingUrl, httpMethod, bodyObject, headers, trace);
        }
    }
}

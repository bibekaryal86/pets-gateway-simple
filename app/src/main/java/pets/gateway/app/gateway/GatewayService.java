package pets.gateway.app.gateway;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import pets.gateway.app.model.GatewayModel;
import pets.gateway.app.model.GatewayResponse;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static pets.gateway.app.util.ConnectorUtil.sendHttpRequest;
import static pets.gateway.app.util.RoutesUtil.getRouteAuthHeader;
import static pets.gateway.app.util.RoutesUtil.getRouteBase;
import static pets.gateway.app.util.Util.RESTRICTED_HEADERS;
import static pets.gateway.app.util.Util.TRACE;
import static pets.gateway.app.util.Util.getRequestBody;
import static pets.gateway.app.util.Util.hasText;

@Slf4j
public class GatewayService {

    private String trace(HttpServletRequest request) {
        return Objects.requireNonNullElse(request.getAttribute(TRACE), "TERROR").toString();
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();

            if (!RESTRICTED_HEADERS.contains(key)) {
                String value = request.getHeader(key);
                headers.put(key, value);
            }
        }
        headers.putAll(getRouteAuthHeader(request.getRequestURI()));
        return headers;
    }

    private String getParameters(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder("?");
        Map<String, String[]> paramMap = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            for (String s : entry.getValue()) {
                stringBuilder.append(entry.getKey());
                stringBuilder.append("=");
                stringBuilder.append(s);
                stringBuilder.append("&");
            }
        }

        int length = stringBuilder.length();
        stringBuilder.deleteCharAt(length-1);
        return stringBuilder.toString();
    }

    public GatewayResponse gatewayService(HttpServletRequest request) {
        String trace = trace(request);
        String requestUri = request.getRequestURI();
        String routeBase = getRouteBase(requestUri);

        if (!hasText(routeBase)) {
            log.info("[ {} ] | Route Not Found for requestURI: [ {} ]", trace, requestUri);
            return GatewayResponse.builder()
                    .statusCode(422)
                    .object(GatewayModel.builder()
                            .errMsg("Error! Route Not Found!! Please Try Again!!!")
                            .build())
                    .build();
        } else {
            String outgoingUrl = routeBase.concat(requestUri).concat(getParameters(request));
            String httpMethod = request.getMethod();
            Map<String, String> headers = getHeaders(request);
            Object bodyObject = getRequestBody(request);
            return sendHttpRequest(outgoingUrl, httpMethod, bodyObject, headers, trace);
        }
    }
}

package pets.gateway.app.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import pets.gateway.app.util.RoutesUtil;
import pets.gateway.app.util.Util;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class GatewayFilterHeaderAuth implements Filter {

    // move adding header from gatewayservice to here

    private static final long TIME_TO_EXPIRY_FOR_RENEWAL_REQUEST = 300000;     // FIVE MINUTES
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private String trace(HttpServletRequest request) {
        return Objects.requireNonNullElse(request.getAttribute(Util.TRACE), "TERROR").toString();
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        // just in case, because there are multiple filters
        if (request.getAttribute(Util.TRACE) == null) {
            httpServletRequest.setAttribute(Util.TRACE, ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
        }
        String trace = trace(httpServletRequest);

        if (isIgnoreRequests(httpServletRequest)) {
            chain.doFilter(request, response);
        } else {
            Date expirationDate = claimsExpirationDate(httpServletRequest, trace);

            if (expirationDate == null) {
                logRequestDetails(httpServletRequest, trace);
                httpServletResponse.setStatus(401);
                response.setContentType("application/json");
                httpServletResponse.getWriter().print("{\"errMsg\": \"Invalid/Expired Token!!!\"}");
            } else {
                boolean isCloseToExpiry = isCloseToExpiry(expirationDate, trace);

                if (isCloseToExpiry) {
                    httpServletResponse.setHeader("Access-Control-Expose-Headers", "refresh-token");
                    httpServletResponse.setHeader("refresh-token", "true");
                }

                chain.doFilter(request, response);
            }
        }
    }

    public Date claimsExpirationDate(HttpServletRequest request, String trace) {
        Date expirationDate = null;

        try {
            String oldToken = request.getHeader(AUTHORIZATION_HEADER);

            if (Util.hasText(oldToken)) {
                oldToken = oldToken.replace("Bearer ", "");

                Claims claims = Jwts.parser()
                        .setSigningKey(Util.getSecretKey())
                        .parseClaimsJws(oldToken)
                        .getBody();

                expirationDate = claims.getExpiration();
            }
        } catch (Exception ex) {
            log.error("[ {} ] Error parsing request token: [ {} ], [ {} ]",
                    trace, ex.getClass().getName(), ex.getMessage());
        }

        return expirationDate;
    }

    public boolean isCloseToExpiry(Date expirationDate, String trace) {
        Date currentDate = new Date(System.currentTimeMillis());
        long difference = expirationDate.getTime() - currentDate.getTime();
        log.info("[ {} ] Token Expiration Check: [ {} ] | [ {} ] | [ {} ]",
                trace, expirationDate, currentDate, difference);
        return difference < TIME_TO_EXPIRY_FOR_RENEWAL_REQUEST;
    }

    private boolean isIgnoreRequests(HttpServletRequest request) {
        return Util.AUTHORIZATION_NOT_NEEDED.contains(request.getRequestURI());
    }

    private void logRequestDetails(HttpServletRequest request, String trace) {
        log.info("[ {} ] Invalid / Expired Auth Token:: Incoming: [ {} ] | Method: [ {} ]",
                trace, request.getRequestURI(), request.getMethod());
    }
}

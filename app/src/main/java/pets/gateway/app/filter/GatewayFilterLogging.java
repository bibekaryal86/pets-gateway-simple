package pets.gateway.app.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import pets.gateway.app.util.Util;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class GatewayFilterLogging implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        if ("/favicon.ico".equals(httpServletRequest.getRequestURI())) {
            httpServletResponse.setStatus(200);
        } else {
            // just in case, because there are multiple filters
            if (request.getAttribute(Util.TRACE) == null) {
                httpServletRequest.setAttribute(Util.TRACE, ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
            }

            logRequest(httpServletRequest);
            chain.doFilter(request, response);
            logResponse(httpServletRequest, httpServletResponse);
        }
    }

    private void logRequest(HttpServletRequest httpServletRequest) {
        log.info("[ {} ] | REQUEST::: Incoming: [ {} ] | Method: [ {} ]",
                httpServletRequest.getAttribute(Util.TRACE), httpServletRequest.getRequestURI(), httpServletRequest.getMethod());
    }

    private void logResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        log.info("[ {} ] | RESPONSE::: Status [ {} ]",
                httpServletRequest.getAttribute(Util.TRACE), httpServletResponse.getStatus());
    }
}

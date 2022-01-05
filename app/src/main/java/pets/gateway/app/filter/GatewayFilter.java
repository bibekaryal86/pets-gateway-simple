package pets.gateway.app.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class GatewayFilter implements Filter {

    private static final String TRACE = "TRACE";

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        if ("/favicon.ico".equals(httpServletRequest.getRequestURI())) {
            httpServletResponse.setStatus(200);
        } else {
            httpServletRequest.setAttribute(TRACE, ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));

            logRequest(httpServletRequest);
            chain.doFilter(request, response);
            logResponse(httpServletRequest, httpServletResponse);
        }
    }

    private void logRequest(HttpServletRequest httpServletRequest) {
        log.info("[ {} ] | REQUEST::: Incoming: [ {} ] | Method: [ {} ]",
                httpServletRequest.getAttribute(TRACE), httpServletRequest.getRequestURI(), httpServletRequest.getMethod());
    }

    private void logResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        log.info("[ {} ] | RESPONSE::: Status [ {} ]", httpServletRequest.getAttribute(TRACE), httpServletResponse.getStatus());
    }
}

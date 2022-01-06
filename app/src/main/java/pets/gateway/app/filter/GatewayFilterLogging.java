package pets.gateway.app.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static pets.gateway.app.util.Util.TRACE;

@Slf4j
public class GatewayFilterLogging implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        if ("/favicon.ico".equals(httpServletRequest.getRequestURI())) {
            httpServletResponse.setStatus(200);
        } else {
            // just in case, because there are multiple filters
            if (request.getAttribute(TRACE) == null) {
                httpServletRequest.setAttribute(TRACE, ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
            }

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
        log.info("[ {} ] | RESPONSE::: Status [ {} ]",
                httpServletRequest.getAttribute(TRACE), httpServletResponse.getStatus());
    }
}

package pets.gateway.app.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class GatewayFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        logRequest(httpServletRequest);
        chain.doFilter(request, response);
        logResponse(httpServletRequest, httpServletResponse);
    }

    private void logRequest(HttpServletRequest httpServletRequest) {
        log.info("REQUEST BEGIN: [ {} ] | [ {} ]", httpServletRequest.getRequestURI(), httpServletRequest.getRemoteAddr());
    }

    private void logResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        log.info("REQUEST END: [ {} ] | [ {} ]", httpServletRequest.getRequestURI(), httpServletResponse.getStatus());
    }
}

package pets.gateway.app.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;

public class GatewayServlet extends HttpServlet {
    // find a way to set response from actual response from downstream service
    private static final String CHARACTER_ENCODING = "utf-8";
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String CONTENT_TYPE = "application/json";
    private static final String PING_SUCCESSFUL = "{\"ping\": \"successful\"}";
    private static final String PING_UNSUCCESSFUL = "{\"ping\": \"method not allowed\"}";


    private boolean isTestPing(String requestUri) {
        return requestUri.contains("/tests/ping");
    }

    private String trace(HttpServletRequest request) {
        return Objects.requireNonNullElse(request.getAttribute("TRACE"), "TRACE_ERROR").toString();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setContentType(CONTENT_TYPE);

        System.out.println("in doGet: " + trace(request));

        if (isTestPing(request.getRequestURI())) {
            response.setStatus(200);
            response.getWriter().print(PING_SUCCESSFUL);
        } else {
            response.setStatus(200);
            response.getWriter().print(PING_UNSUCCESSFUL);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setContentType(CONTENT_TYPE);

        System.out.println("in doPost: " + request.getAttribute("TRACE"));

        if (isTestPing(request.getRequestURI())) {
            response.setStatus(405);
            response.getWriter().print(PING_UNSUCCESSFUL);
        } else {
            response.setStatus(200);
            response.getWriter().print(PING_UNSUCCESSFUL);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setContentType(CONTENT_TYPE);

        System.out.println("in doPut: " + trace(request));

        if (isTestPing(request.getRequestURI())) {
            response.setStatus(405);
            response.getWriter().print(PING_UNSUCCESSFUL);
        } else {
            response.setStatus(200);
            response.getWriter().print(PING_UNSUCCESSFUL);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setContentType(CONTENT_TYPE);

        System.out.println("in doDelete: " + trace(request));

        if (isTestPing(request.getRequestURI())) {
            response.setStatus(405);
            response.getWriter().print(PING_UNSUCCESSFUL);
        } else {
            response.setStatus(200);
            response.getWriter().print(PING_UNSUCCESSFUL);
        }
    }
}

package pets.gateway.app.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;

public class GatewayServlet extends HttpServlet {
    // find a way to set response from actual response from downstream service

    private boolean isTestPing(String requestUri) {
        return requestUri.contains("/tests/ping");
    }

    private String trace(HttpServletRequest request) {
        return Objects.requireNonNullElse(request.getAttribute("TRACE"), "TRACE_ERROR").toString();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");

        System.out.println("in doGet: " + trace(request));

        if (isTestPing(request.getRequestURI())) {
            response.setStatus(200);
            response.getWriter().print("{\"ping\": \"successful\"}");
        } else {
            response.setStatus(200);
            response.getWriter().print("{\"ping\": \"unsuccessful\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");

        System.out.println("in doPost: " + request.getAttribute("TRACE"));

        if (isTestPing(request.getRequestURI())) {
            response.setStatus(405);
            response.getWriter().print("{\"ping\": \"method not allowed\"}");
        } else {
            response.setStatus(200);
            response.getWriter().print("{\"ping\": \"something else\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");

        System.out.println("in doPut: " + trace(request));

        if (isTestPing(request.getRequestURI())) {
            response.setStatus(405);
            response.getWriter().print("{\"ping\": \"method not allowed\"}");
        } else {
            response.setStatus(200);
            response.getWriter().print("{\"ping\": \"something else\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");

        System.out.println("in doDelete: " + trace(request));

        if (isTestPing(request.getRequestURI())) {
            response.setStatus(405);
            response.getWriter().print("{\"ping\": \"method not allowed\"}");
        } else {
            response.setStatus(200);
            response.getWriter().print("{\"ping\": \"something else\"}");
        }
    }
}

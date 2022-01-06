package pets.gateway.app.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pets.gateway.app.gateway.GatewayService;
import pets.gateway.app.model.GatewayResponse;

import java.io.IOException;

import static pets.gateway.app.util.Util.getGson;

public class GatewayServlet extends HttpServlet {
    private static final String CHARACTER_ENCODING = "utf-8";
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String CONTENT_TYPE = "application/json";
    private static final String PING_SUCCESSFUL = "{\"ping\": \"successful\"}";

    private boolean isTestPing(String requestUri) {
        String[] array = requestUri.split("/");
        return array.length > 2 && array[1].equals("tests") && array[2].equals("ping");
    }

    // this could be added in a filter (GatewayFilterLogging because it applies to /*
    // but since this is the only servlet, it has been put here
    private void setAccessControlHeaders(HttpServletResponse response) {
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.setContentType(CONTENT_TYPE);
        response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, "content-type,authorization");
        response.addHeader(ACCESS_CONTROL_ALLOW_METHODS, "GET,PUT,POST,DELETE,OPTIONS");
    }

    private void doEverything(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setAccessControlHeaders(response);

        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(200);
        }else if (isTestPing(request.getRequestURI())) {
            response.setStatus(200);
            response.getWriter().print(PING_SUCCESSFUL);
        } else {
            GatewayResponse gatewayResponse = new GatewayService().gatewayService(request);
            response.setStatus(gatewayResponse.getStatusCode());
            response.getWriter().print(getGson().toJson(gatewayResponse.getObject()));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doEverything(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doEverything(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doEverything(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doEverything(request, response);
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doEverything(request, response);
    }
}

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
    private static final String CONTENT_TYPE = "application/json";
    private static final String PING_SUCCESSFUL = "{\"ping\": \"successful\"}";

    private boolean isTestPing(String requestUri) {
        String[] array = requestUri.split("/");
        return array.length > 2 && array[1].equals("tests") && array[2].equals("ping");
    }

    private void doEverything(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setContentType(CONTENT_TYPE);

        if (isTestPing(request.getRequestURI())) {
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
}

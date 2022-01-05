package pets.gateway.app.server;

import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import pets.gateway.app.filter.GatewayFilterLogging;
import pets.gateway.app.filter.GatewayFilterHeaderAuth;
import pets.gateway.app.servlet.GatewayServlet;
import pets.gateway.app.util.Util;

import java.util.EnumSet;

public class ServerJetty {

    public void start() throws Exception {
        QueuedThreadPool threadPool = new QueuedThreadPool(Util.SERVER_MAX_THREADS, Util.SERVER_MIN_THREADS, Util.SERVER_IDLE_TIMEOUT);
        Server server = new Server(threadPool);

        try (ServerConnector connector = new ServerConnector(server)) {
            String port = Util.getSystemEnvProperty(Util.SERVER_PORT);
            connector.setPort(port == null ? 8080 : Integer.parseInt(port));
            server.setConnectors(new Connector[]{connector});
        }

        server.setHandler(getServletHandler());
        server.start();
    }

    private ServletHandler getServletHandler() {
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addFilterWithMapping(GatewayFilterLogging.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        servletHandler.addFilterWithMapping(GatewayFilterHeaderAuth.class, "/pets-service/*", EnumSet.of(DispatcherType.REQUEST));
        servletHandler.addFilterWithMapping(GatewayFilterHeaderAuth.class, "/pets-database/*", EnumSet.of(DispatcherType.REQUEST));
        servletHandler.addServletWithMapping(GatewayServlet.class, "/*");
        return servletHandler;
    }
}
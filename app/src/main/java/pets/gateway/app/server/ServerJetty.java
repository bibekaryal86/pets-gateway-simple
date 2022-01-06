package pets.gateway.app.server;

import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import pets.gateway.app.filter.GatewayFilterHeaderAuth;
import pets.gateway.app.filter.GatewayFilterLogging;
import pets.gateway.app.servlet.GatewayServlet;

import java.util.EnumSet;

import static pets.gateway.app.util.Util.SERVER_IDLE_TIMEOUT;
import static pets.gateway.app.util.Util.SERVER_MAX_THREADS;
import static pets.gateway.app.util.Util.SERVER_MIN_THREADS;
import static pets.gateway.app.util.Util.SERVER_PORT;
import static pets.gateway.app.util.Util.getSystemEnvProperty;

public class ServerJetty {

    public void start() throws Exception {
        QueuedThreadPool threadPool = new QueuedThreadPool(SERVER_MAX_THREADS, SERVER_MIN_THREADS, SERVER_IDLE_TIMEOUT);
        Server server = new Server(threadPool);

        try (ServerConnector connector = new ServerConnector(server)) {
            String port = getSystemEnvProperty(SERVER_PORT);
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
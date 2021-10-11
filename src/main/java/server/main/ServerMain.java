package server.main;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Server Class
 *
 * Handles the creation of a server.
 */

public class ServerMain {

    // Server port
    public static final int    PORT = 5000;
    // Server URI
    public static final String URI = "http://0.0.0.0/";

    // The package(s) containing the JAX-RS resource classes
    public static final String[] RESOURCE_PACKAGES = {"server.resources"};

    /**
     * Starts the Grizzly web server.
     *
     * @return the HttpServer object
     */
    public static HttpServer startServer() {
        URI baseUri = UriBuilder.fromUri(URI).port(PORT).build();
        final ResourceConfig config = new ResourceConfig().packages(RESOURCE_PACKAGES);
        return GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
    }

    /**
     * Server main method.  Starts the web server by calling startServer()
     * @param args command line arguments (ignored)
     */
    public static void main(String[] args) {
        startServer();
    }
}

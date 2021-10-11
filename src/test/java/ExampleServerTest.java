import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class ExampleServerTest {

    private static HttpServer httpServer;
    private static Client client;

    // This starts the server and creates the Client once before all tests in this class
    @BeforeClass
    public static void startServer() {
        httpServer = server.main.ServerMain.startServer();
        client = ClientBuilder.newClient();
    }

    // This shuts the server down after all tests in this class are complete
    @AfterClass
    public static void stopServer() {
        if( client != null ) client.close();
        if( httpServer != null ) httpServer.shutdown();
    }
//    /**
//     * Tests that a GET request to URL "/example/hello" returns "Hello from your server!"
//     */
//    @Test
//    public void testExample01() {
//        String response = client.target("http://localhost:9998/")
//                .path("example/hello")
//                .request("text/plain")
//                .get(String.class);
//        client.close();
//
//        Assert.assertEquals("Hello from your server!", response);
//    }

}

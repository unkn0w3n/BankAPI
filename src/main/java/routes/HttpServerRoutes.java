package routes;

import com.sun.net.httpserver.HttpServer;
import utilits.Properties;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpServerRoutes {
    private HttpServer server;

    public HttpServerRoutes(HttpServer server){
        this.server = server;
    }

    //Start Server and Parse Routes
    public void start() throws IOException {
        //server config
        //server = HttpServer.create();
        server.bind(new InetSocketAddress(Properties.HTTP_SERVER_PORT), 0);

        //Post && GET: /api/transactions
        ApiTransactions apiTransactions = new ApiTransactions();
        apiTransactions.process(server);

        //POST && GET: /api/approve
        ApiApprove apiApprove = new ApiApprove();
        apiApprove.process(server);

        //Post && GET: /api/cards
        ApiCards apiCards = new ApiCards();
        apiCards.process(server);

        //Post && GET: /api/cards/balance
        ApiCardsBalance apiCardsBalance = new ApiCardsBalance();
        apiCardsBalance.process(server);

        //Post && GET: /api/accounts
        ApiAccounts apiAccounts = new ApiAccounts();
        apiAccounts.process(server);

        //Post && GET: /api/users
        ApiUsers apiUsers = new ApiUsers();
        apiUsers.process(server);

        //Server start
        server.setExecutor(null);
        server.start();
        System.out.println("server started on port:"+Properties.HTTP_SERVER_PORT);
    }

    public void stop(){
        server.stop(1);
        System.out.println("Server shutted down");
    }
}

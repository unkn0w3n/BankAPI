package routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HttpServerRoutes {
    //Start Server and Parse Routes
    public void start() throws IOException {
        //server config
        int serverPort = 8000;
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(serverPort), 0);

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

        //Post && GET: /api/transactions
        ApiTransactions apiTransactions = new ApiTransactions();
        apiTransactions.process(server);

        //POST && GET: /api/approve
        ApiApprove apiApprove = new ApiApprove();
        apiApprove.process(server);


        //Server start
        server.setExecutor(null);
        server.start();
        System.out.println("server started on port:"+serverPort);
    }


}

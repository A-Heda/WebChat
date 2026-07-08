package server;

import com.sun.net.httpserver.HttpServer;
import controllers.UserController;
import controllers.GroupController;
import controllers.ChatController;
import controllers.ContactController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;


public class MessengerServer {
    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/users",new UserController());

            
            server.createContext("/groups",new GroupController());

            
            server.createContext("/chats",new ChatController());

            server.createContext("/contacts", new ContactController());
            
            server.setExecutor(Executors.newFixedThreadPool(10));
            server.start();
            System.out.println( "Messenger Server Started On Port 8080");
        } catch (IOException e) {
            System.out.println("Server Failed To Start");
            e.printStackTrace();
        }
    }
}

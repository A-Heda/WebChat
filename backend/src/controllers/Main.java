package controllers;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/users", new UserController());

            // بعداً اضافه می‌کنیم:
            // server.createContext("/groups", new GroupController());
            // server.createContext("/messages", new MessageController());

            server.setExecutor(Executors.newFixedThreadPool(10)); // 10 کاربر هم‌زمان می‌تونن request بفرستن
                                                                            // هر request توی یک Thread جدا پردازش میشه

            server.start();
            System.out.println("Server is running on port 8080: ..");
        } catch (IOException e) {
            System.out.println("Server failed to start");
            e.printStackTrace();
        }
    }
}

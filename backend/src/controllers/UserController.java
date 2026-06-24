package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import model.User;
import services.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserController implements HttpHandler {

    private UserService userService;
    private Gson gson;


    public UserController() {
        userService = new UserService();
        gson = new Gson();
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin","*");      //اتصال فرانت به بک با فعال کردن CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers","Content-Type");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");

        if(exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
        exchange.sendResponseHeaders(204, -1);
        return;
    }

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();


        switch (path) {

            case "/users/register":

                if(method.equals("POST")) {
                    register(exchange);
                }
                else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;


            case "/users/login":

                if(method.equals("POST")) {
                    login(exchange);
                }
                else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;


            case "/users/password":

                if(method.equals("PUT")) {
                    changePassword(exchange);
                }
                else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;


            case "/users/profile-image":

                if(method.equals("PUT")) {
                    updateProfileImage(exchange);
                }
                else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;


            case "/users/id":

                if(method.equals("GET")) {
                    findUserById(exchange);
                }
                else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;


            case "/users/username":

                if(method.equals("GET")) {
                    findUserByUsername(exchange);
                }
                else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;


            default:
                sendResponse(exchange, "Route not found", 404);
        }
    }


    // POST /users/register
    private void register(HttpExchange exchange) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

    JsonObject json = gson.fromJson(reader, JsonObject.class);

    String id = json.get("id").getAsString();
    String username = json.get("username").getAsString();
    String password = json.get("password").getAsString();
    String repeatPassword = json.get("repeatPassword").getAsString();

    String result =
            userService.register(id, username, password, repeatPassword);

    if(result.equals("SUCCESS")) {
        sendResponse(exchange, result, 200);
    }
    else {
        sendResponse(exchange, result, 400);
    }
}


    // POST /users/login
    private void login(HttpExchange exchange) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

    JsonObject json = gson.fromJson(reader, JsonObject.class);

    String username = json.get("username").getAsString();
    String password = json.get("password").getAsString();

    String result = userService.login(username, password);

    if("SUCCESS".equals(result)) {
    User user = userService.findUserByUsername(username);
    JsonObject response = new JsonObject();
    response.addProperty("id", user.getId());
    response.addProperty("username", user.getUsername());          //also sends userId&username via its response to frontend
    sendResponse(exchange, response, 200);
}
    else {
        sendResponse(exchange, result, 401);
    }
}


    // PUT /users/password
    private void changePassword(HttpExchange exchange) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

    JsonObject json = gson.fromJson(reader, JsonObject.class);

    String userId = json.get("userId").getAsString();
    String oldPassword = json.get("oldPassword").getAsString();
    String newPassword = json.get("newPassword").getAsString();

    String result = userService.changePassword(userId, oldPassword, newPassword);

    if(result.equals("SUCCESS")) {
        sendResponse(exchange, result, 200);
    }
    else {
        sendResponse(exchange, result, 400);
    }
}


    // PUT /users/profile-image
    private void updateProfileImage(HttpExchange exchange) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

    JsonObject json = gson.fromJson(reader, JsonObject.class);

    String userId = json.get("userId").getAsString();
    String imagePath = json.get("imagePath").getAsString();

    String result = userService.updateProfileImage(userId, imagePath);

    if(result.equals("SUCCESS")) {
        sendResponse(exchange, result, 200);
    }
    else {
        sendResponse(exchange, result, 400);
    }
}


    // GET /users/id?id=...
    private void findUserById(HttpExchange exchange) throws IOException {

    String query = exchange.getRequestURI().getQuery();

    if(query == null || !query.startsWith("id=")) {
        sendResponse(exchange, "Missing id parameter.", 400);
        return;
    }

    String id = query.substring(3);

    User user = userService.findUserById(id);

    if(user == null) {
        sendResponse(exchange, "User not found.", 404);
    }
    else {
        sendResponse(exchange, user , 200);
    }
}


    // GET /users/username?username=...
    private void findUserByUsername(HttpExchange exchange) throws IOException {

    String query = exchange.getRequestURI().getQuery();

    if (query == null || !query.startsWith("username=")) {
        sendResponse(exchange, "Missing username parameter.", 400);
        return;
    }

    String username = query.substring("username=".length());

    User user = userService.findUserByUsername(username);

    if (user == null) {
        sendResponse(exchange, "User not found.", 404);
    } else {
        sendResponse(exchange, user, 200);
    }
}


    private void sendResponse(HttpExchange exchange,
                          Object obj,
                          int statusCode) throws IOException {

    String jsonResponse = gson.toJson(obj);

    exchange.getResponseHeaders().set("Content-Type", "application/json");

    byte[] responseBytes = jsonResponse.getBytes("UTF-8");

    exchange.sendResponseHeaders(statusCode, responseBytes.length);

    exchange.getResponseBody().write(responseBytes);

    exchange.getResponseBody().close();
}
}
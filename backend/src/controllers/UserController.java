package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import model.User;
import services.ContactService;
import services.GroupService;
import services.UserService;
import services.BlockService;
import services.ChatStatusService;
import services.BlockService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserController implements HttpHandler {
    private ContactService contactService;
    private UserService userService;
    private Gson gson;

    private ChatStatusService chatStatusService;
    private GroupService groupService;
    private BlockService blockService;

    public UserController() {
        userService = new UserService();
        gson = new Gson();
        contactService = new ContactService();
        chatStatusService = new ChatStatusService();
        groupService = new GroupService();
        blockService = new BlockService();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (path) {

            case "/users/register":

                if (method.equals("POST")) {
                    register(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/users/login":

                if (method.equals("POST")) {
                    login(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/users/change-id":

                if (method.equals("PUT")) {

                    changeUserId(exchange);

                } else {

                    sendResponse(exchange, "Method not allowed", 405);

                }

                break;

            case "/users/delete":

                if (method.equals("DELETE")) {

                    deleteAccount(exchange);

                } else {

                    sendResponse(exchange, "Method not allowed", 405);

                }

                break;

            case "/users/password":

                if (method.equals("PUT")) {
                    changePassword(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/users/profile-image":

                if (method.equals("PUT")) {
                    updateProfileImage(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/users/remove-contact":

                if (method.equals("DELETE")) {
                    removeContact(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/users/id":

                if (method.equals("GET")) {
                    findUserById(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/users/change-username":

                if (method.equals("PUT")) {
                    changeUsername(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/users/contact":

                if (method.equals("POST")) {
                    addContact(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/users/block":

                if (method.equals("PUT")) {

                    blockUser(exchange);

                } else {

                    sendResponse(exchange, "Method not allowed", 405);

                }

                break;

            case "/users/unblock":

                if (method.equals("PUT")) {

                    unblockUser(exchange);

                } else {

                    sendResponse(exchange, "Method not allowed", 405);

                }

                break;

            case "/users/archive":

                if (method.equals("PUT")) {
                    archiveChat(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/users/mutual-groups":

                if (method.equals("GET")) {
                    getMutualGroups(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/users/status":

                if (method.equals("GET")) {
                    getUserStatus(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/users/username":

                if (method.equals("GET")) {
                    findUserByUsername(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            default:
                sendResponse(exchange, "Route not found", 404);
        }
    }


    private void register(HttpExchange exchange) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String id = json.get("id").getAsString();
        String username = json.get("username").getAsString();
        String password = json.get("password").getAsString();
        String repeatPassword = json.get("repeatPassword").getAsString();

        String result = userService.register(id, username, password, repeatPassword);

        if (result.equals("SUCCESS")) {
            sendResponse(exchange, result, 200);
        } else {
            sendResponse(exchange, result, 400);
        }
    }


    private void login(HttpExchange exchange) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String username = json.get("username").getAsString();
        String password = json.get("password").getAsString();

        String result = userService.login(username, password);

        if ("SUCCESS".equals(result)) {
            User user = userService.findUserByUsername(username);
            JsonObject response = new JsonObject();
            response.addProperty("id", user.getId());
            response.addProperty("username", user.getUsername());

            sendResponse(exchange, response, 200);
        } else {
            sendResponse(exchange, result, 401);
        }
    }

    private void changeUserId(HttpExchange exchange)
            throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String oldId = json.get("oldId").getAsString();

        String newId = json.get("newId").getAsString();

        String result = userService.changeUserId(
                oldId,
                newId);

        if (result.equals("SUCCESS")) {

            sendResponse(exchange, result, 200);

        } else {

            sendResponse(exchange, result, 400);

        }
    }

    private void changeUsername(HttpExchange exchange)
            throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String userId = json.get("userId").getAsString();

        String newUsername = json.get("newUsername").getAsString();

        String result = userService.changeUsername(
                userId,
                newUsername);

        if (result.equals("SUCCESS")) {

            sendResponse(exchange, result, 200);

        } else {

            sendResponse(exchange, result, 400);
        }
    }

    private void addContact(HttpExchange exchange)
            throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String ownerId = json.get("ownerId").getAsString();

        String contactId = json.get("contactId").getAsString();

        String result = contactService.addContact(ownerId, contactId);

        if (result.equals("SUCCESS"))
            sendResponse(exchange, result, 200);
        else
            sendResponse(exchange, result, 400);
    }

    private void blockUser(HttpExchange exchange)
            throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String ownerId = json.get("ownerId").getAsString();

        String contactId = json.get("contactId").getAsString();

        blockService.blockUser(ownerId, contactId);

        sendResponse(exchange, "SUCCESS", 200);
    }

    private void unblockUser(HttpExchange exchange)
            throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String ownerId = json.get("ownerId").getAsString();

        String contactId = json.get("contactId").getAsString();

        blockService.unblockUser(ownerId, contactId);

        sendResponse(exchange, "SUCCESS", 200);
    }

    private void archiveChat(HttpExchange exchange) throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String userId = json.get("userId").getAsString();

        String chatId = json.get("chatId").getAsString();

        boolean archived = json.get("archived").getAsBoolean();

        chatStatusService.setArchived(userId, chatId, archived);

        sendResponse(exchange, "SUCCESS", 200);
    }

    private void getMutualGroups(HttpExchange exchange) throws IOException {

        String query = exchange.getRequestURI().getQuery();

        String[] params = query.split("&");

        String user1 = "";
        String user2 = "";

        for (String p : params) {

            if (p.startsWith("user1="))
                user1 = p.substring(6);

            if (p.startsWith("user2="))
                user2 = p.substring(6);
        }

        sendResponse(
                exchange,
                groupService.getMutualGroups(user1, user2),
                200);
    }

    private void getUserStatus(HttpExchange exchange) throws IOException {

        String query = exchange.getRequestURI().getQuery();

        String[] params = query.split("&");

        String ownerId = "";
        String contactId = "";
        String chatId = "";

        for (String p : params) {

            if (p.startsWith("ownerId="))
                ownerId = p.substring(8);

            else if (p.startsWith("contactId="))
                contactId = p.substring(10);

            else if (p.startsWith("chatId="))
                chatId = p.substring(7);
        }

        JsonObject response = new JsonObject();

        response.addProperty(
                "blocked",
                blockService.isBlocked(ownerId, contactId));

        response.addProperty(
                "contact",
                contactService.isContact(ownerId, contactId));

        response.addProperty(
                "archived",
                chatStatusService.isArchived(ownerId, chatId));

        sendResponse(exchange, response, 200);
    }

    private void removeContact(HttpExchange exchange)
            throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String ownerId = json.get("ownerId").getAsString();

        String contactId = json.get("contactId").getAsString();

        String result = contactService.removeContact(ownerId, contactId);

        if (result.equals("SUCCESS"))
            sendResponse(exchange, result, 200);
        else
            sendResponse(exchange, result, 400);
    }

    private void setBlocked(HttpExchange exchange)
            throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String ownerId = json.get("ownerId").getAsString();

        String contactId = json.get("contactId").getAsString();

        boolean blocked = json.get("blocked").getAsBoolean();

        String result;

        if (blocked)
            result = contactService.blockContact(ownerId, contactId);
        else
            result = contactService.unblockContact(ownerId, contactId);

        if (result.equals("SUCCESS"))
            sendResponse(exchange, result, 200);
        else
            sendResponse(exchange, result, 400);
    }

    private void getContactStatus(HttpExchange exchange)
            throws IOException {

        String query = exchange.getRequestURI().getQuery();

        if (query == null) {
            sendResponse(exchange, "Missing parameters", 400);
            return;
        }

        String[] params = query.split("&");

        String ownerId = params[0].split("=")[1];

        String contactId = params[1].split("=")[1];

        JsonObject response = new JsonObject();

        response.addProperty(
                "isContact",
                contactService.isContact(ownerId, contactId));

        response.addProperty(
                "blocked",
                contactService.isBlocked(ownerId, contactId));

        sendResponse(exchange, response, 200);
    }


    private void changePassword(HttpExchange exchange) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String userId = json.get("userId").getAsString();
        String oldPassword = json.get("oldPassword").getAsString();
        String newPassword = json.get("newPassword").getAsString();

        String result = userService.changePassword(userId, oldPassword, newPassword);

        if (result.equals("SUCCESS")) {
            sendResponse(exchange, result, 200);
        } else {
            sendResponse(exchange, result, 400);
        }
    }


    private void updateProfileImage(HttpExchange exchange) throws IOException {
        BufferedReader requestReader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

        JsonObject requestBody = gson.fromJson(requestReader, JsonObject.class);

        String userId = requestBody.get("userId").getAsString();
        String profileImageBase64 = requestBody.get("image").getAsString();

        String updateResult = userService.updateProfileImage(
            userId,
            profileImageBase64
    );

        if (updateResult.equals("SUCCESS")) {
            sendResponse(exchange, updateResult, 200);
        } else {
            sendResponse(exchange, updateResult, 400);
        }
    }


    private void findUserById(HttpExchange exchange) throws IOException {

        String query = exchange.getRequestURI().getQuery();

        if (query == null || !query.startsWith("id=")) {
            sendResponse(exchange, "Missing id parameter.", 400);
            return;
        }

        String id = query.substring(3);

        User user = userService.findUserById(id);

        if (user == null) {
            sendResponse(exchange, "User not found.", 404);
        } else {
            sendResponse(exchange, user, 200);
        }
    }


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

    private void deleteAccount(HttpExchange exchange)
            throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String userId = json.get("userId").getAsString();

        String password = json.get("password").getAsString();

        String result = userService.deleteAccount(
                userId,
                password);

        if (result.equals("SUCCESS")) {

            sendResponse(exchange, result, 200);

        } else {

            sendResponse(exchange, result, 400);

        }
    }
}

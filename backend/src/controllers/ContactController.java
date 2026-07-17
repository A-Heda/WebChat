package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Contact;
import model.User;
import repositories.UserRepository;
import services.ContactService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ContactController implements HttpHandler {
    private final Gson gson = new Gson();
    private final ContactService contactService = new ContactService();
    private final UserRepository userRepository = new UserRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin","*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers","Content-Type");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        switch (path) {
            case "/contacts":
                if (method.equals("GET")) {
                    getContacts(exchange);
                } else {
                    sendResponse(exchange, "Method Not Allowed", 405);
                }
                break;

            case "/contacts/add":
                if (method.equals("POST")) {
                    addContact(exchange);
                } else {
                    sendResponse(exchange, "Method Not Allowed", 405);
                }
                break;

            case "/contacts/remove":
                if (method.equals("POST")) {
                    removeContact(exchange);
                } else {
                    sendResponse(exchange, "Method Not Allowed", 405);
                }
                break;

            case "/contacts/block":
                if (method.equals("POST")) {
                    blockContact(exchange);
                } else {
                    sendResponse(exchange, "Method Not Allowed", 405);
                }
                break;

            case "/contacts/unblock":
                if (method.equals("POST")) {
                    unblockContact(exchange);
                } else {
                    sendResponse(exchange, "Method Not Allowed", 405);
                }
                break;

            default:
                sendResponse(exchange, "Not Found", 404);
        }
    }

    private void getContacts(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (query == null || !query.startsWith("ownerId=")) {
            sendResponse(exchange, "Owner ID is required.", 400);
            return;
        }

        String ownerId = query.substring("ownerId=".length());
        List<Contact> contacts = contactService.getContacts(ownerId);

        JsonArray result = new JsonArray();

        for (Contact contact : contacts) {
            User user = userRepository.findById(contact.getContactId());

            if (user == null)
                continue;

            JsonObject json = new JsonObject();
            json.addProperty("id", user.getId());
            json.addProperty("username", user.getUsername());
            json.addProperty("imagePath", user.getProfileImagePath());
            json.addProperty("blocked", contact.isBlocked());

            result.add(json);
        }

        sendResponse(exchange, result, 200);
    }

    private void addContact(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String ownerId = json.get("ownerId").getAsString();
        String contactId = json.get("contactId").getAsString();

        String result = contactService.addContact(ownerId, contactId);

        if (result.equals("SUCCESS"))
            sendResponse(exchange, result, 200);
        else
            sendResponse(exchange, result, 400);
    }

    private void removeContact(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String ownerId = json.get("ownerId").getAsString();
        String contactId = json.get("contactId").getAsString();

        String result = contactService.removeContact(ownerId, contactId);

        if (result.equals("SUCCESS"))
            sendResponse(exchange, result, 200);
        else
            sendResponse(exchange, result, 400);
    }

    private void blockContact(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String ownerId = json.get("ownerId").getAsString();
        String contactId = json.get("contactId").getAsString();

        String result = contactService.blockContact(ownerId, contactId);

        if (result.equals("SUCCESS"))
            sendResponse(exchange, result, 200);
        else
            sendResponse(exchange, result, 400);
    }

    private void unblockContact(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String ownerId = json.get("ownerId").getAsString();
        String contactId = json.get("contactId").getAsString();

        String result = contactService.unblockContact(ownerId, contactId);

        if (result.equals("SUCCESS"))
            sendResponse(exchange, result, 200);
        else
            sendResponse(exchange, result, 400);
    }

    private void sendResponse(HttpExchange exchange,
            Object data,
            int statusCode)
            throws IOException {

        String jsonResponse = gson.toJson(data);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        byte[] responseBytes = jsonResponse.getBytes("UTF-8");

        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        exchange.getResponseBody().write(responseBytes);

        exchange.getResponseBody().close();
    }
}

package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import model.Message;
import services.ChatService;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;


public class ChatController implements HttpHandler {

    private ChatService chatService;
    private Gson gson;


    public ChatController() {
        chatService = new ChatService();
        gson = new Gson();
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();


        switch (path) {


            case "/chats/create-private":

                if(method.equals("POST")) {
                    createPrivateChat(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;


            case "/chats/send":

                if(method.equals("POST")) {
                    sendMessage(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;


            case "/chats/edit":

                if(method.equals("PUT")) {
                    editMessage(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;


            case "/chats/delete":

                if(method.equals("DELETE")) {
                    deleteMessage(exchange);
                } else {
                    sendResponse(exchange,"Method not allowed", 405);
                }

                break;


            case "/chats/messages":

                if(method.equals("GET")) {
                    getMessages(exchange);
                } else {
                    sendResponse(exchange,"Method not allowed",405);
                }

                break;


            default:
                sendResponse(exchange,"Route not found", 404);
        }
    }


    // POST /chats/create-private
    private void createPrivateChat(HttpExchange exchange) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

    JsonObject json = gson.fromJson(reader, JsonObject.class);

    String user1Id = json.get("user1Id").getAsString();

    String user2Id = json.get("user2Id").getAsString();

    String result = chatService.createPrivateChat(user1Id, user2Id);


    if ("SUCCESS".equals(result)) {
        sendResponse(exchange, result, 200);
    } else {
        sendResponse(exchange, result, 400);
    }
}


    // POST /chats/send
    private void sendMessage(HttpExchange exchange) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

    JsonObject json = gson.fromJson(reader, JsonObject.class);

    //String messageId = json.get("messageId").getAsString();

    String senderId = json.get("senderId").getAsString();

    String chatId = json.get("chatId").getAsString();

    String text = json.get("text").getAsString();


    String result = chatService.sendMessage(chatId , senderId , text);


    if ("SUCCESS".equals(result)) {
        sendResponse(exchange, result, 200);
    } else {
        sendResponse(exchange, result, 400);
    }
}


    // PUT /chats/edit
    private void editMessage(HttpExchange exchange) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

    JsonObject json = gson.fromJson(reader, JsonObject.class);


    String chatId = json.get("chatId").getAsString();

    String messageId = json.get("messageId").getAsString();

    String newText = json.get("newText").getAsString();

    String editorId = json.get("editorId").getAsString();

    String result = chatService.editMessage(chatId, messageId, newText, editorId);


    if ("SUCCESS".equals(result)) {
        sendResponse(exchange, result, 200);
    } else {
        sendResponse(exchange, result, 400);
    }
}


    // DELETE /chats/delete
    private void deleteMessage(HttpExchange exchange) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

    JsonObject json = gson.fromJson(reader, JsonObject.class);


    String chatId = json.get("chatId").getAsString();

    String messageId = json.get("messageId").getAsString();
    
    String requesterId = json.get("requesterId").getAsString();


    String result = chatService.deleteMessage(chatId, messageId , requesterId);


    if ("SUCCESS".equals(result)) {
        sendResponse(exchange, result, 200);
    } else {
        sendResponse(exchange, result, 400);
    }
}


    // GET /chats/messages?chatId=...
    private void getMessages(HttpExchange exchange) throws IOException {

    String query = exchange.getRequestURI().getQuery();

    if(query == null || !query.startsWith("chatId=")) {

        sendResponse(exchange, "Missing chatId parameter.", 400);

        return;
    }

    String chatId = query.substring("chatId=".length());

    List<Message> messages = chatService.getAllMessages(chatId);

    if(messages == null)
    sendResponse(exchange, "Chat not found.", 404);

    sendResponse(exchange, messages,200);
}


    private void sendResponse(HttpExchange exchange,
                          Object data,
                          int statusCode)
                          throws IOException {

    String jsonResponse = gson.toJson(data);

    exchange.getResponseHeaders().set("Content-Type","application/json; charset=UTF-8");

    byte[] responseBytes = jsonResponse.getBytes("UTF-8");

    exchange.sendResponseHeaders(statusCode, responseBytes.length);

    exchange.getResponseBody().write(responseBytes);

    exchange.getResponseBody().close();
}
}
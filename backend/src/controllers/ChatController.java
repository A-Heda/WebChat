package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import model.Message;
import services.ChatPreview;
import services.ChatService;
import services.UserService;
import services.ChatStatusService;
import model.User;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ChatController implements HttpHandler {

    private ChatService chatService;
    private UserService userService;
    private ChatStatusService chatStatusService;
    private Gson gson;

    public ChatController() {
        chatService = new ChatService();
        userService = new UserService();
        chatStatusService = new ChatStatusService();
        gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // اتصال فرانت به بک با فعال کردن CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (path) {

            case "/chats/report":

                if (method.equals("POST")) {
                    reportMessage(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/chats/create-private":

                if (method.equals("POST")) {
                    createPrivateChat(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/chats/send":

                if (method.equals("POST")) {
                    sendMessage(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/chats/edit":

                if (method.equals("PUT")) {
                    editMessage(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/chats/delete":

                if (method.equals("DELETE")) {
                    deleteMessage(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/chats/messages":

                if (method.equals("GET")) {
                    getMessages(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/chats/user":

                if (method.equals("GET")) {
                    getUserChats(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/chats/info":

                if (method.equals("GET")) {
                    getChatInfo(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }

                break;

            case "/chats/pin":

                if(method.equals("PUT")){
                    pinChat(exchange);
                } else {
                    sendResponse(exchange,"Method not allowed",405);
                }

                break;

            case "/chats/archive":

                if(method.equals("PUT")){
                    archiveChat(exchange);
                } else {
                    sendResponse(exchange,"Method not allowed",405);
                }

                break;

            case "/chats/save":

                if(method.equals("POST")){
                    saveMessage(exchange);
                } else {
                    sendResponse(exchange,"Method not allowed",405);
                }

                break;

            case "/chats/saved":

                if(method.equals("GET")){
                    getSavedMessages(exchange);
                } else {
                    sendResponse(exchange,"Method not allowed",405);
                }

                break;

            case "/chats/saved/send":

                if(method.equals("POST")){
                    sendSavedMessage(exchange);
                } else {
                    sendResponse(exchange,"Method not allowed",405);
                }

                break;

            case "/chats/media":

                if(method.equals("POST")){
                    sendMedia(exchange);
                } else {
                    sendResponse(exchange,"Method not allowed",405);
                }

                break;

            // case "/chsts/cerate-group" : added the group creation feature to group
            // controller

            // if(method.equals("POST")) {
            // createGroup(exchange);
            // } else {
            // sendResponse(exchange , "Method not allowed" , 405);
            // }

            // break;

            default:
                sendResponse(exchange, "Route not found", 404);
        }
    }

    // POST /chats/create-private
    private void createPrivateChat(HttpExchange exchange) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String user1Id = json.get("user1Id").getAsString();

        String user2Id = json.get("user2Id").getAsString();

        String result = chatService.createPrivateChat(user1Id, user2Id);

        if (result.startsWith("private_")) { // means created succesfully
            sendResponse(exchange, result, 200);
        } else {
            sendResponse(exchange, result, 400);
        }
    }

    // POST /chats/send
    private void sendMessage(HttpExchange exchange) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        // String messageId = json.get("messageId").getAsString();

        String senderId = json.get("senderId").getAsString();

        String chatId = json.get("chatId").getAsString();

        String text = json.get("text").getAsString();

        String result = chatService.sendMessage(chatId, senderId, text);

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

        String result = chatService.deleteMessage(chatId, messageId, requesterId);

        if ("SUCCESS".equals(result)) {
            sendResponse(exchange, result, 200);
        } else {
            sendResponse(exchange, result, 400);
        }
    }

    private void reportMessage(HttpExchange exchange) throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String chatId = json.get("chatId").getAsString();

        String messageId = json.get("messageId").getAsString();

        String reporterId = json.get("reporterId").getAsString();

        String result = chatService.reportMessage(
                chatId,
                messageId,
                reporterId);

        if (result.equals("SUCCESS"))
            sendResponse(exchange, result, 200);
        else
            sendResponse(exchange, result, 400);
    }

    // GET /chats/messages?chatId=...
    private void getMessages(HttpExchange exchange) throws IOException {

    String query = exchange.getRequestURI().getQuery();

    if (query == null || !query.startsWith("chatId=")) {

        sendResponse(exchange, "Missing chatId parameter.", 400);

        return;
    }

    String chatId = query.substring("chatId=".length());

    List<Message> messages = chatService.getAllMessages(chatId);

    if (messages == null) {

        sendResponse(exchange, "Chat not found.", 404);

        return;
    }

    JsonArray result = new JsonArray();

    for (Message message : messages) {

        JsonObject json = gson.toJsonTree(message).getAsJsonObject();

        User sender = userService.findUserById(message.getSenderId());

        if (sender != null) {

            json.addProperty("senderUsername", sender.getUsername());
            json.addProperty("senderImagePath", sender.getProfileImagePath());

        } else {

            json.addProperty("senderUsername", "Unknown");
            json.addProperty("senderImagePath", "../assets/default-avatar.png");

        }

        result.add(json);
    }

    sendResponse(exchange, result, 200);
}

    private void getUserChats(HttpExchange exchange) throws IOException {

        String query = exchange.getRequestURI().getQuery();

        if (query == null || !query.startsWith("userId=")) {
            sendResponse(exchange, "Missing userId", 400);
            return;
        }

        String userId = query.substring("userId=".length());

        List<ChatPreview> chats = chatService.getUserChats(userId);

        sendResponse(exchange, chats, 200);
    }

    // GET /chats/info?chatId=private_1_2&userId=1
    private void getChatInfo(HttpExchange exchange) throws IOException {

        String query = exchange.getRequestURI().getQuery();

        String chatId = null;
        String userId = null;

        String[] params = query.split("&");

        for (String param : params) {

            if (param.startsWith("chatId="))
                chatId = param.substring(7);

            if (param.startsWith("userId="))
                userId = param.substring(7);
        }

        ChatPreview info = chatService.getChatInfo(chatId, userId);

        if (info == null) {

            sendResponse(exchange, "Chat not found.", 404);

            return;
        }

        sendResponse(exchange, info, 200);
    }

    // private void createGroup(HttpExchange exchange) throws IOException {

    // BufferedReader reader = new BufferedReader(new
    // InputStreamReader(exchange.getRequestBody()));

    // JsonObject json = gson.fromJson(reader, JsonObject.class);

    // String groupId = json.get("groupId").getAsString();

    // String groupName = json.get("groupName").getAsString();

    // String adminId = json.get("adminId").getAsString();

    // List<String> memberIds = new ArrayList<>();

    // JsonArray members = json.getAsJsonArray("memberIds");

    // for(int i = 0 ; i < members.size() ; i++) {
    // memberIds.add(members.get(i).getAsString());
    // }

    // String result = chatService.createGroupChat(groupId, groupName, adminId,
    // memberIds);

    // if(result.startsWith("group_")) {
    // sendResponse(exchange,result,200);
    // } else {
    // sendResponse(exchange,result,400);
    // }

    // }

    private void archiveChat(HttpExchange exchange) throws IOException{
        BufferedReader reader = 
            new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

        JsonObject json =
            gson.fromJson(reader,JsonObject.class);

        String userId =
            json.get("userId").getAsString();

        String chatId =
            json.get("chatId").getAsString();

        boolean archived =
            json.get("archived").getAsBoolean();

        chatStatusService.setArchived(userId, chatId, archived);

    sendResponse(exchange,"SUCCESS",200);
}

private void pinChat(HttpExchange exchange) throws IOException {

    BufferedReader reader =
            new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

    JsonObject json =
            gson.fromJson(reader,JsonObject.class);

    String userId =
            json.get("userId").getAsString();

    String chatId =
            json.get("chatId").getAsString();

    boolean pinned =
            json.get("pinned").getAsBoolean();

    chatStatusService.pinChat(userId, chatId, pinned);

    sendResponse(exchange,"SUCCESS",200);

}

    private void saveMessage(HttpExchange exchange) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        JsonObject json = gson.fromJson(reader,JsonObject.class);

        String userId = json.get("userId").getAsString();
        String chatId = json.get("chatId").getAsString();
        String messageId = json.get("messageId").getAsString();

        String result = chatService.saveSavedMessage(userId, chatId, messageId);

        if(result.equals("SUCCESS")) {
            sendResponse(exchange, result, 200);
        } else {
            sendResponse(exchange, result, 400);
        }
    }

    private void getSavedMessages(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (query == null || !query.startsWith("userId=")) {
            sendResponse(exchange, "Missing userId", 400);
            return;
        }

        String userId = query.substring("userId=".length());
        List<Message> messages = chatService.getSavedMessages(userId);
        JsonArray result = new JsonArray();

        for (Message message : messages) {
            JsonObject json = gson.toJsonTree(message).getAsJsonObject();
            User sender = userService.findUserById(message.getSenderId());

            if (sender != null) {
                json.addProperty("senderUsername", sender.getUsername());
                json.addProperty("senderImagePath", sender.getProfileImagePath());
            } else {
                json.addProperty("senderUsername", "Unknown");
                json.addProperty("senderImagePath", "../assets/default-avatar.png");
            }

        result.add(json);
    }

    sendResponse(exchange, result, 200);
}


    // POST /chats/saved/send
    private void sendSavedMessage(HttpExchange exchange) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String userId = json.get("userId").getAsString();

        String text = "";
        if(json.has("text"))
            text = json.get("text").getAsString();

        String mediaUrl = null;
        if(json.has("mediaUrl"))
            mediaUrl = json.get("mediaUrl").getAsString();

        String result = chatService.sendSavedMessage(userId, text, mediaUrl);

        if ("SUCCESS".equals(result)) {
            sendResponse(exchange, result, 200);
        } else {
            sendResponse(exchange, result, 400);
        }
    }

    private void sendMedia(HttpExchange exchange) throws IOException {
    BufferedReader requestReader =
        new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

    JsonObject requestBody = gson.fromJson(requestReader, JsonObject.class);
    
    if(!requestBody.has("mediaUrl")){
        sendResponse(exchange,"Missing mediaUrl",400);
        return;
    }

    String senderUserId = requestBody.get("senderId").getAsString();
    String targetChatId = requestBody.get("chatId").getAsString();
    String mediaFileUrl = requestBody.get("mediaUrl").getAsString();

    String sendResult = chatService.sendMedia(
        targetChatId,
        senderUserId,
        mediaFileUrl
    );

    if (sendResult.equals("SUCCESS")) {
        sendResponse(exchange, sendResult, 200);
    } else {
        sendResponse(exchange, sendResult, 400);
    }
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
package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import services.GroupService;
import model.Group;
import services.ChatPreview;
import services.ChatService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GroupController implements HttpHandler {

    private GroupService groupService;
    private Gson gson;
    private ChatService chatService;

    public GroupController() {
        chatService = new ChatService();
        groupService = new GroupService();
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

        String method = exchange.getRequestMethod();          //POST , GET , PUT
        String path = exchange.getRequestURI().getPath();

        switch (path) {

            case "/groups/create":

                if (method.equals("POST")) {
                    createGroup(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }
                break;

            case "/groups/info":

                if(method.equals("GET")) {
                    getGroupChatInfo(exchange);
                }
                else {
                    sendResponse(exchange, "Method not allowed", 405);
                }
                break;


            case "/groups/add-member":

                if (method.equals("POST")) {
                    addMember(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }
                break;


            case "/groups/remove-member":

                if (method.equals("POST")) {
                    removeMember(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }
                break;
                

            case "/groups/change-name":

                if(method.equals("PUT")) {
                    changeGroupName(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }
                break;


            case "/groups/change-image":

                if(method.equals("PUT")) {
                    changeGroupImage(exchange);
                } else {
                sendResponse(exchange, "Method not allowed", 405);
                }
                break;


            case "/groups/delete":

                if (method.equals("DELETE")) {
                    deleteGroup(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }
                break;

            case "/groups/leave":

                if(method.equals("POST")) {
                    leaveGroup(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
             }
            break;


            case "/groups/id":

                if (method.equals("GET")) {
                    findGroupById(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }
                break;

            default:
                sendResponse(exchange, "Route not found", 404);
        }
    }

    private void createGroup(HttpExchange exchange) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String groupId = json.get("groupId").getAsString();

        String groupName = json.get("groupName").getAsString();
               
        String adminId = json.get("adminId").getAsString();

        List<String> memberIds = new ArrayList<>();

        JsonArray members = json.getAsJsonArray("memberIds");

        for(int i = 0 ; i < members.size() ; i++) {
            memberIds.add(members.get(i).getAsString());
        }

        String result = chatService.createGroupChat(groupId, groupName, adminId, memberIds);

        if(result.startsWith("group_")) {
            sendResponse(exchange,result,200);
        } else {
            sendResponse(exchange,result,400);
        }
        
    }

    private void addMember(HttpExchange exchange) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

    JsonObject json = gson.fromJson(reader, JsonObject.class);

    String groupId = json.get("groupId").getAsString();

    String userId = json.get("userId").getAsString();

    String result = groupService.addMember(groupId,userId);

    if("SUCCESS".equals(result))
        sendResponse(exchange,result,200);
    else
        sendResponse(exchange,result,400);

}

    private void removeMember(HttpExchange exchange) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

    JsonObject json = gson.fromJson(reader, JsonObject.class);

    String groupId = json.get("groupId").getAsString();

    String userId = json.get("userId").getAsString();

    String result = groupService.removeMember(groupId,userId);

    if("SUCCESS".equals(result))
        sendResponse(exchange,result,200);
    else
        sendResponse(exchange,result,400);

}

    private void changeGroupName(HttpExchange exchange) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

    JsonObject json = gson.fromJson(reader, JsonObject.class);

    String groupId = json.get("groupId").getAsString();

    String newName = json.get("newGroupName").getAsString();

    String result = groupService.changeGroupName(groupId,newName);

    if("SUCCESS".equals(result))
        sendResponse(exchange,result,200);
    else
        sendResponse(exchange,result,400);

}

    private void changeGroupImage(HttpExchange exchange) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

    JsonObject json = gson.fromJson(reader, JsonObject.class);

    String groupId =  json.get("groupId").getAsString();

    String imagePath = json.get("imagePath").getAsString();

    String result = groupService.changeGroupImage(groupId,imagePath);

    if("SUCCESS".equals(result))
        sendResponse(exchange,result,200);
    else
        sendResponse(exchange,result,400);

}


    private void deleteGroup(HttpExchange exchange) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

    JsonObject json = gson.fromJson(reader, JsonObject.class);

    String groupId = json.get("groupId").getAsString();
    String requesterId = json.get("requesterId").getAsString();

    String result = groupService.deleteGroup(groupId, requesterId);

    if("SUCCESS".equals(result))
        sendResponse(exchange, result, 200);
    else
        sendResponse(exchange, result, 400);
}

    private void leaveGroup(HttpExchange exchange) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

    JsonObject json = gson.fromJson(reader, JsonObject.class);

    String groupId = json.get("groupId").getAsString();
    String userId = json.get("userId").getAsString();

    String result = groupService.leaveGroup(groupId, userId);

    if("SUCCESS".equals(result)) {
        sendResponse(exchange, result, 200);
    } else {
        sendResponse(exchange, result, 400);
    }
}

    private void findGroupById(HttpExchange exchange) throws IOException {

    String query = exchange.getRequestURI().getQuery();

    if(query == null || !query.startsWith("groupId=")) {

        sendResponse(exchange, "Missing groupId parameter.",400);

        return;
    }

    String groupId = query.substring("groupId=".length());

    Group group = groupService.findGroupById(groupId);

    if(group == null)
        sendResponse(exchange,"Group not found.",404);
    else
        sendResponse(exchange, group,200);

}

    private void sendResponse(HttpExchange exchange,
                              Object data,
                              int statusCode) throws IOException {

        String jsonResponse = gson.toJson(data);

        exchange.getResponseHeaders().set("Content-Type", "application/json");

        byte[] responseBytes = jsonResponse.getBytes("UTF-8");

        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        exchange.getResponseBody().write(responseBytes);

        exchange.getResponseBody().close();
    }

    private void getGroupChatInfo (HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if(query == null || !query.startsWith("groupId=")) {
            sendResponse(exchange, "Missing groupId parameter.", 400);
            return;
        }

        String groupId = query.substring("groupId=".length());

        String chatId = "group_" + groupId;

        ChatPreview result = chatService.getGroupChatInfo(chatId);

        if (result == null) {
            sendResponse(exchange, "Group not found.", 404);
        } else {
            sendResponse(exchange, result, 200);
        }
    }
}
package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;


public class MediaController implements HttpHandler {

    private static final String MEDIA_ROOT_DIRECTORY = "backend/database/media/";
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,OPTIONS");

        if(exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")){
            exchange.sendResponseHeaders(204,-1);
            return;
        }

        if(exchange.getRequestMethod().equalsIgnoreCase("GET")){
            loadMedia(exchange);
        }
        else if(exchange.getRequestMethod().equalsIgnoreCase("POST")){
            uploadMedia(exchange);
        }
        else{
            exchange.sendResponseHeaders(405,-1);
        }
    }

    public void loadMedia (HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();

        // e.g. : backend/media/images/test.png -> images/test.png
        String requestedFilePath = requestPath.substring("/media/".length());

        File requestedFile = new File(MEDIA_ROOT_DIRECTORY + requestedFilePath);

        if (!requestedFile.exists()) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        String mimeType = Files.probeContentType(requestedFile.toPath());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        exchange.getResponseHeaders().set("Content-Type", mimeType);
        exchange.sendResponseHeaders(200, requestedFile.length());

        try (
            OutputStream responseStream = exchange.getResponseBody();
            FileInputStream fileInputStream = new FileInputStream(requestedFile)
        ) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                responseStream.write(buffer, 0, bytesRead);
            }
        }
    }

    private void uploadMedia(HttpExchange exchange) throws IOException {
        JsonObject requestBody = gson.fromJson(new InputStreamReader(exchange.getRequestBody()),JsonObject.class);

        String originalFileName = requestBody.get("fileName").getAsString();
        String mimeType = requestBody.get("contentType").getAsString();
        String base64Data = requestBody.get("data").getAsString();

        byte[] fileBytes = Base64.getDecoder().decode(base64Data);

        String fileExtension = "";
        int index = originalFileName.lastIndexOf(".");
        if(index != -1) {                                               //درصورت نبود پسوند کرش نکند
            fileExtension = originalFileName.substring(index);         
        }

        String targetFolder;

        if (mimeType.startsWith("image")) {
            targetFolder = "images";
        } else if (mimeType.startsWith("video")) {
            targetFolder = "videos";
        } else if (mimeType.startsWith("audio")) {
            targetFolder = "audios";
        } else {
            targetFolder = "files";
        }

        String generatedFileName = UUID.randomUUID() + fileExtension;

        File mediaDirectory = new File(MEDIA_ROOT_DIRECTORY + targetFolder);
        mediaDirectory.mkdirs();

        File savedFile = new File(mediaDirectory, generatedFileName);
        Files.write(savedFile.toPath(), fileBytes);

        JsonObject responseBody = new JsonObject();
        responseBody.addProperty("url", "/media/" + targetFolder + "/" + generatedFileName);

        byte[] responseBytes = gson.toJson(responseBody).getBytes();

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.close();
}

}

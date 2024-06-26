package cat.iesesteveterradas;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class MongoTools {

    public static List<Map<String, Object>> extractFromPosts(String data) {
        // Create an empty Jsoup document (not used for storing extracted data)
        Document parsedDoc = Jsoup.parse(data);

        Elements posts = parsedDoc.select("post"); // Select all <post> elements

        // List to hold extracted post data
        List<Map<String, Object>> extractedPosts = new ArrayList<>();

        for (Element post : posts) {
            Map<String, Object> postData = new HashMap<>();

            String id = post.select("row[Id]").attr("Id");
            String postTypeId = post.select("row[PostTypeId]").attr("PostTypeId");
            String acceptedAnswerId = post.select("row[AcceptedAnswerId]").attr("AcceptedAnswerId");
            String creationDate = post.select("row[CreationDate]").attr("CreationDate");
            String score = post.select("row[Score]").attr("Score");
            String viewCount = post.select("row[ViewCount]").attr("ViewCount");
            String body = post.select("row[Body]").text(); // Extract text content of Body
            String ownerUserId = post.select("row[OwnerUserId]").attr("OwnerUserId");
            String lastActivityDate = post.select("row[LastActivityDate]").attr("LastActivityDate");
            String title = post.select("row[Title]").attr("Title");
            String tags = post.select("row[Tags]").text();
            String answerCount = post.select("row[AnswerCount]").attr("AnswerCount");
            String commentCount = post.select("row[CommentCount]").attr("CommentCount");
            String contentLicense = post.select("row[ContentLicense]").attr("ContentLicense");
    
            // Add extracted attributes to the map
            postData.put("id", id);
            postData.put("postTypeId", postTypeId);
            postData.put("acceptedAnswerId", acceptedAnswerId);
            postData.put("creationDate", creationDate);
            postData.put("score", score);
            postData.put("viewCount", convertirAFloat(viewCount));
            postData.put("body", body);
            postData.put("ownerUserId", ownerUserId);
            postData.put("lastActivityDate", lastActivityDate);
            postData.put("title", title);
            postData.put("tags", tags);
            postData.put("answerCount", answerCount);
            postData.put("commentCount", commentCount);
            postData.put("contentLicense", contentLicense);

            if(viewCount != null && !viewCount.equals("")) {
                extractedPosts.add(postData); 
                System.out.println(postData);
            }
            else{
                System.out.println(postData); // 4512

            }
        }   

        return extractedPosts;
    }

    public static void MongoInsert(List<Map<String, Object>> extractedPosts) {
        // Connect to MongoDB (replace with your connection URI)
        try (var mongoClient = MongoClients.create("mongodb://root:example@localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("PR32");
            MongoCollection<org.bson.Document> collection = database.getCollection("preguntas_populares");

            // Insert each extracted post data as a BSON document
            for (Map<String, Object> postData : extractedPosts) {
                org.bson.Document mongoDoc = new org.bson.Document();
                for (Map.Entry<String, Object> entry : postData.entrySet()) {
                    mongoDoc.append(entry.getKey(), entry.getValue());
                }
                collection.insertOne(mongoDoc);
            }

            System.out.println("Documents inserted successfully!");
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    
    public static Float convertirAFloat(String cadena) {
        Float resultado = null;
        try {
            resultado = Float.parseFloat(cadena);
        } catch (NumberFormatException e) {
            // En caso de que la cadena no pueda ser convertida a float
            // Aquí puedes manejar la excepción según tu lógica, por ejemplo:
            // System.err.println("La cadena no es un número válido.");
            // O bien, puedes relanzar la excepción si quieres manejarla fuera de este método.
        }
        return resultado;
    }
}

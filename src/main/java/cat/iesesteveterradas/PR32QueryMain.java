package cat.iesesteveterradas;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PR32QueryMain {
    public static void main(String[] args) {
        // Establecer conexión con MongoDB
        try (MongoClient mongoClient = MongoClients.create("mongodb://root:example@localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("PR32");
            MongoCollection<Document> collection = database.getCollection("preguntas_populares");

            // Consulta 1: Obtener las preguntas con ViewCount mayor que la media
            double averageViewCount = calculateAverageViewCount(collection);
            System.out.println("Consulta 1: Preguntas con ViewCount mayor que la media (" + averageViewCount + "):");
            List<Document> highViewCountQuestions = getHighViewCountQuestions(collection, averageViewCount);
            printQuestions(highViewCountQuestions);

            // Consulta 2: Obtener las preguntas que contienen alguna de las letras especificadas en el título
            List<String> lettersToSearch = Arrays.asList("pug", "wig", "yak", "nap", "jig", "mug", "zap", "gag", "oaf", "elf");
            System.out.println("\nConsulta 2: Preguntas con títulos que contienen alguna de estas letras: " + lettersToSearch);
            List<Document> questionsWithLettersInTitle = getQuestionsWithLettersInTitle(collection, lettersToSearch);
            printQuestions(questionsWithLettersInTitle);
        }
    }

    // Método para calcular el promedio de ViewCount en la colección de posts
    private static double calculateAverageViewCount(MongoCollection<Document> collection) {
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
                new Document("$group", new Document("_id", null)
                        .append("average", new Document("$avg", "$viewCount")))
        ));
        Document firstResult = result.first();
        if (firstResult != null) {
            System.out.println(firstResult);
            return firstResult.getDouble("average");
        } else {
            // Handle the case when there are no documents in the collection
            return 0.0;
        }
    }

    // Método para obtener las preguntas con ViewCount mayor que la media
    private static List<Document> getHighViewCountQuestions(MongoCollection<Document> collection, double averageViewCount) {
        FindIterable<Document> result = collection.find(new Document("postTypeId", 1)
                .append("viewCount", new Document("$gt", averageViewCount)));
        return result.into(new ArrayList<>());
    }

    // Método para obtener las preguntas que contienen alguna de las letras especificadas en el título
    private static List<Document> getQuestionsWithLettersInTitle(MongoCollection<Document> collection, List<String> lettersToSearch) {
        FindIterable<Document> result = collection.find(new Document("postTypeId", 1)
                .append("title", new Document("$regex", ".*[" + String.join("", lettersToSearch) + "].*")));
        return result.into(new ArrayList<>());
    }

    // Método para imprimir las preguntas
    private static void printQuestions(List<Document> questions) {
        for (Document question : questions) {
            System.out.println(question.get("title") + " - viewCount: " + question.get("viewCount"));
        }
    }
}

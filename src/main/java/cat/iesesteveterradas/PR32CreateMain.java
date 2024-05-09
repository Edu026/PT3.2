package cat.iesesteveterradas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import org.basex.api.client.ClientSession;
import org.basex.core.*;
import org.basex.core.cmd.*;
import org.bson.Document;
import org.apache.commons.text.StringEscapeUtils;

import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import opennlp.tools.stemmer.snowball.spanishStemmer;

import java.util.Map;
import java.util.List;
import java.util.logging.*;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;



public class PR32CreateMain {
    private static final Logger logger  = Logger.getLogger("MyLogger");
     
    public static void main(String[] args) throws IOException {
        // Initialize connection details
       String host = "127.0.0.1";
       int port = 1984;
       String username = "admin"; // Default username
       String password = "admin"; // Default password

       // Generate Logger
       generateLogger(logger);

       // Establish a connection to the BaseX server
       try (ClientSession session = new ClientSession(host, port, username, password)) {
           logger.info("Connected to BaseX server.");

           session.execute(new Open("coffee.stackexchange"));
           String currentWorkingDirectory = System.getProperty("user.dir");
           System.out.println("Directorio de trabajo actual: " + currentWorkingDirectory);
           
           // Example query - adjust as needed
           String filePath = "data/input/consulta1.xquery";
           String myQuery = readXPathQueryFromFile(filePath);

           // Execute the query
           String result = session.execute(new XQuery(myQuery));
           // Print the result
           logger.info(myQuery);
           logger.info("Query Result:");
           logger.info(result);
           // Save the result 
           saveResultAsXML(result, "./data/output/resultConsulta1.xml");

           // Read the result
           // String data = readXPathQueryFromFile("./data/output/resultConsulta1.xml"); 
           String data = readXPathQueryFromFile("./data/output/resultConsulta1.xml"); 


           // Convertir entitats HTML en caràcters corresponents
           String unescapedString = StringEscapeUtils.unescapeHtml4(data);

           // logger.info("Converted result:");
           // logger.info(unescapedString);

            // Insert in Mongo
            List<Map<String,Object>> extractFromPosts  = MongoTools.extractFromPosts(data);
            MongoTools.MongoInsert(extractFromPosts);



        } catch (BaseXException e) {
            logger.warning("Error connecting or executing the query: " + e.getMessage());
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }        
    }

    public static void saveResultAsXML(String result, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath)))) {
            logger.info("File Saved");
            writer.println(result);
        }
    }

    public static String readXPathQueryFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    public static void generateLogger(Logger logger){
        FileHandler fh;
        String currentWorkingDirectory = System.getProperty("user.dir");

        try {
            // Configura el manejador de archivos para guardar los logs en "application.log"
            fh = new FileHandler(currentWorkingDirectory + "/data/logs/PR32CreateMain.java.log");
            logger.addHandler(fh);

            // Formateador simple para el archivo de logs
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (Exception e) {
            logger.warning("Ocurrió un error al configurar el archivo de logs: " + e.getMessage());
        }
    }
}

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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PR32CreateMain {
    private static final Logger logger = LoggerFactory.getLogger(PR32CreateMain.class);    

    
    public static void main(String[] args) throws IOException {
        // Initialize connection details
       String host = "127.0.0.1";
       int port = 1984;
       String username = "admin"; // Default username
       String password = "admin"; // Default password

       // Establish a connection to the BaseX server
       try (ClientSession session = new ClientSession(host, port, username, password)) {
           logger.info("Connected to BaseX server.");

           //session.execute(new Open("factbook")); 
           
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
           //Save the result 
           saveResultAsXML(result, "./data/output/resultConsulta1.xml");


        } catch (BaseXException e) {
            logger.error("Error connecting or executing the query: " + e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }        
    }
        // Método para guardar el resultado como un archivo XML
    public static void saveResultAsXML(String result, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath)))) {
            logger.info("File Saved");
            writer.println(result);
        }
    }
        // Método para leer el contenido del archivo en una cadena
    public static String readXPathQueryFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}

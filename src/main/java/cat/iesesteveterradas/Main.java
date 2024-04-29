
package cat.iesesteveterradas;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("MyLogger");
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

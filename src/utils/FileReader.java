package utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class FileReader {
    public static DatosJson leerArchivo(String rutaArchivo) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(rutaArchivo), DatosJson.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

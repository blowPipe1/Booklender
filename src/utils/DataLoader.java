package utils;

import com.google.gson.Gson;
import models.LibraryData;

import java.io.*;


public class DataLoader {
    private static final Gson gson = new Gson();
    private static LibraryData data;

    public static LibraryData loadData() throws IOException {
        if (data == null) {
            InputStream is = DataLoader.class.getResourceAsStream("/data/books.json");
            if (is == null) {
                throw new FileNotFoundException("books.json not found");
            }
            try (InputStreamReader isr = new InputStreamReader(is, "UTF-8")) {
                data = gson.fromJson(isr, LibraryData.class);
            }
        }
        return data;
    }
}
package store.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public final class MarkdownReader {
    private static final String DELIMITER = ",";

    private MarkdownReader() {
    }

    public static List<String[]> readFile(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            return br.lines()
                    .skip(1)
                    .map(line -> line.split(DELIMITER))
                    .toList();
        }
    }
}

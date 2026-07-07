package it.map.graphicadventure.progettoesame.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {
    
    public static Set<String> loadFileListInSet(File file) throws IOException {
        Set<String> set = new HashSet<>();

        // 1. Il 'try (...)' garantisce che il file venga CHIUSO al 100% in ogni scenario
        // 2. StandardCharsets.UTF_8 risolve per sempre il problema delle lettere accentate ()
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String line;

            // 3. Questo è il modo corretto di ciclare un file fino alla fine (EOF)
            while ((line = reader.readLine()) != null) {
                // Saltiamo le righe vuote se presenti nel file
                if (!line.trim().isEmpty()) {
                    set.add(line.trim().toLowerCase());
                }
            }
        }

        return set;
    }

    public static List<String> parseString(String string, Set<String> stopwords) {
        List<String> tokens = new ArrayList<>();
        String[] split = string.toLowerCase().split("\\s+");
        for (String t : split) {
            if (!stopwords.contains(t)) {
                tokens.add(t);
            }
        }
        return tokens;
    }

}

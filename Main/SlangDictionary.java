package Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class SlangDictionary implements Serializable {
    private HashMap<String, List<String>> slangMap;
    private HashMap<String, Set<String>> definitionMap;
    private List<String> searchHistory;

    public SlangDictionary() {
        this.slangMap = new HashMap<>();
        this.definitionMap = new HashMap<>();
        this.searchHistory = new ArrayList<>();
    }

    public void loadFromFile(String filePath) {
        try {
            List<String[]> allData = new ArrayList<>();
            FileReader fileReader = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fileReader);

            br.readLine();

            for (String line; (line = br.readLine()) != null;) {
                String[] parts = line.split("`");
                if (parts.length < 2) {
                    continue;
                }

                String slang = parts[0];
                List<String> definition = Arrays.asList(parts[1].trim().split("\\s*\\|\\s*"));

                this.slangMap.put(slang, definition);
                for (String def : definition) {
                    String[] keywords = def.split("\\s+");

                    for (String keyword : keywords) {
                        String cleanKeyword = keyword.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

                        if (cleanKeyword.isEmpty()) {
                            continue;
                        }
                        this.definitionMap.computeIfAbsent(cleanKeyword, k -> new HashSet<>()).add(slang);
                    }
                }
                allData.add(parts);
            }

            br.close();

            // // print Data
            // for (String[] row : allData) {
            // for (String cell : row) {
            // System.out.print(cell + "\t");
            // }
            // System.out.println();
            // }

            // print HashMaps
            System.out.println("slangMap:");
            slangMap.entrySet().stream().limit(5).forEach(entry -> {
                System.out.println("Slang: " + entry.getKey());
                System.out.println("  Definitions: " + entry.getValue());
                System.out.println("---");
            });
            System.out.println("definitionMap:");
            definitionMap.entrySet().stream().limit(5).forEach(entry -> {
                System.out.println("Keyword: " + entry.getKey());
                System.out.println("  Slangs (Set): " + entry.getValue());
                System.out.println("---");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        SlangDictionary sd = new SlangDictionary();
        sd.loadFromFile("slang.txt");
    }
}
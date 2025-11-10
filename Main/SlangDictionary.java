package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class SlangDictionary implements Serializable {
    public static final long serialVersionUID = 1L;

    private HashMap<String, List<String>> slangMap; /* ">": ["Frustration"] */
    private HashMap<String, Set<String>> definitionMap; /* "Frustration": Set {">"} */
    private List<String> searchHistory;

    public SlangDictionary() {
        this.slangMap = new LinkedHashMap<>();
        this.definitionMap = new LinkedHashMap<>();
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

    public void saveToFile(String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this);
        } catch (IOException ioe) {
            System.err.println("An error when save file: " + ioe.getMessage());
            ioe.printStackTrace();
        }
    }

    public static SlangDictionary loadFromSerializedFile(String filePath) {
        SlangDictionary dictionary = null;

        try (FileInputStream fis = new FileInputStream(filePath);
                ObjectInputStream ois = new ObjectInputStream(fis)) {
            dictionary = (SlangDictionary) ois.readObject();
        } catch (IOException | ClassNotFoundException exc) {
            System.out.println("File not found at " + filePath + ". Try to load from txt.");
        }

        return dictionary;
    }

    public void testFiveLines() {
        System.out.println("slangMap (5):");
        this.slangMap.entrySet().stream().limit(5).forEach(entry -> {
            System.out.println("  Slang: " + entry.getKey() + " -> " + entry.getValue());
        });
        System.out.println("definitionMap (5):");
        this.definitionMap.entrySet().stream().limit(5).forEach(entry -> {
            System.out.println("  Keyword: " + entry.getKey() + " -> " + entry.getValue());
        });
    }
}
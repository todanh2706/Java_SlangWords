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
import java.util.Collections;

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

    /**
     * @param filePath
     * @throws IOException
     */
    public void loadFromFile(String filePath) throws IOException {
        try (FileReader fileReader = new FileReader(filePath);
                BufferedReader br = new BufferedReader(fileReader)) {
            br.readLine();

            String line;
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                try {
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
                } catch (ArrayIndexOutOfBoundsException err) {
                    System.err.println("Error: Skip line " + lineNumber + " cause the issue when analysis: " + line);
                }
                System.out.println("Loaded data successfully from text file: " + filePath);
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

    /**
     * @param filePath
     * @throws IOException
     */
    public void saveToFile(String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this);
            System.out.println("Loaded the dictionary to file :" + filePath);
        }
    }

    /**
     * @param filePath
     * @return SlangDictionary
     */
    public static SlangDictionary loadFromSerializedFile(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("File not found " + filePath + ". Try to load data from text file.");
            return null;
        }

        try (FileInputStream fis = new FileInputStream(filePath);
                ObjectInputStream ois = new ObjectInputStream(fis)) {
            SlangDictionary dictionary = (SlangDictionary) ois.readObject();
            System.out.println("Loaded dictionary successfully from file: " + filePath);
            return dictionary;
        } catch (ClassNotFoundException exc) {
            System.out.println("File not found at " + filePath + ". Try to load from txt.");
            return null;
        } catch (IOException ioe) {
            System.out.println("File " + filePath + "can not be readed!");
            return null;
        }
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

    /**
     * @param filePath
     * @return true or false
     */
    public static boolean resetDictionaryFile(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("File " + filePath + " not found.");
            return true;
        }

        try {
            if (file.delete()) {
                System.out.println("Deleted file " + filePath + " successfully.");
                return true;
            } else {
                System.err.println("Can not delete file " + filePath + "!");
                return false;
            }
        } catch (SecurityException se) {
            System.err.println("Security error: Do not have permission to delete file " + filePath + "!");
            se.printStackTrace();
            return false;
        }
    }

    /**
     * @param slang
     * @return definitionList
     */
    public List<String> searchSlang(String slang) {
        if (slang == null) {
            return Collections.emptyList();
        }
        this.searchHistory.add(slang);

        List<String> definitions = this.slangMap.get(slang);

        if (definitions == null) {
            return Collections.emptyList();
        }

        return definitions;
    }
}
/**
 * @author Huu Danh, To
 */

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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Map;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Random;

public class SlangDictionary implements Serializable {
    public static final long serialVersionUID = 1L;

    private String textFilePath = "";
    private String serializedFilePath = "dictionary.ser";

    private HashMap<String, List<String>> slangMap; /* ">": ["Frustration"] */
    private HashMap<String, Set<String>> definitionMap; /* "Frustration": Set {">"} */
    private List<String> searchHistory;

    private String randomSlangOfDay;
    private LocalDate dateOfRandomSlang;

    public SlangDictionary() {
        this.slangMap = new LinkedHashMap<>();
        this.definitionMap = new LinkedHashMap<>();
        this.searchHistory = new ArrayList<>();
    }

    /**
     * @param filePath
     */
    public void setTextFilePath(String textFilePath) {
        this.textFilePath = textFilePath;
    }

    /**
     * @param filePath
     */
    public void setSerializedFilePath(String serializedFilePath) {
        this.serializedFilePath = serializedFilePath;
    }

    /**
     * @param filePath
     * @throws IOException
     */
    public void loadFromFile() throws IOException {
        if (this.slangMap != null) {
            this.slangMap.clear();
        } else {
            this.slangMap = new LinkedHashMap<>();
        }

        if (this.definitionMap != null) {
            this.definitionMap.clear();
        } else {
            this.definitionMap = new LinkedHashMap<>();
        }

        if (this.searchHistory != null) {
            this.searchHistory.clear();
        } else {
            this.searchHistory = new ArrayList<>();
        }

        this.randomSlangOfDay = null;
        this.dateOfRandomSlang = null;

        try (FileReader fileReader = new FileReader(this.textFilePath);
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
            }
            br.close();

            System.out.println("Loaded data successfully from text file: " + this.textFilePath);

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
        }
    }

    /**
     * @param filePath
     */
    public void saveToFile() {
        try {
            FileOutputStream fos = new FileOutputStream(this.serializedFilePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            System.out.println("Saved the dictionary to file: " + this.serializedFilePath);
            oos.close();
        } catch (IOException ioe) {
            System.out.println("An error occures: " + ioe.getMessage());
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
     * @return true or false
     */
    public boolean resetDictionaryFile() {
        File file = new File(this.serializedFilePath);

        if (!file.exists()) {
            System.out.println("File " + this.serializedFilePath + " not found.");
            return true;
        }

        try {
            if (file.delete()) {
                System.out.println("Deleted file " + this.serializedFilePath + " successfully.");
                return true;
            } else {
                System.err.println("Can not delete file " + this.serializedFilePath + "!");
                return false;
            }
        } catch (SecurityException se) {
            System.err
                    .println("Security error: Do not have permission to delete file " + this.serializedFilePath + "!");
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

    /**
     * @param definition
     * @return slangSet
     */
    public Set<String> searchDefinition(String definition) {
        if (definition == null) {
            return Collections.emptySet();
        }
        this.searchHistory.add(definition);

        Set<String> finalSlangSet = new HashSet<>();
        String[] keywords = definition.split("\\s+");

        for (String keyword : keywords) {
            String cleanKeyword = keyword.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

            if (cleanKeyword.isEmpty()) {
                continue;
            }

            Set<String> slangs = this.definitionMap.get(cleanKeyword);

            if (slangs != null) {
                finalSlangSet.addAll(slangs);
            }
        }

        return finalSlangSet;
    }

    public void showHistory() {
        System.out.println("======> Search history:");
        for (String searched : searchHistory) {
            System.out.println(searched);
        }
        System.out.println("==========================================");
    }

    /**
     * Helpers
     */
    private void addDefinitionsToIndex(String slang, List<String> definitions) {
        for (String def : definitions) {
            String[] keywords = def.split("\\s+");
            for (String keyword : keywords) {
                String cleanKeyword = keyword.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                if (!cleanKeyword.isEmpty()) {
                    this.definitionMap.computeIfAbsent(cleanKeyword, k -> new HashSet<>()).add(slang);
                }
            }
        }
    }

    private void removeDefinitionsFromIndex(String slang, List<String> oldDefinitions) {
        for (String def : oldDefinitions) {
            String[] keywords = def.split("\\s+");
            for (String keyword : keywords) {
                String cleanKeyword = keyword.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                if (cleanKeyword.isEmpty()) {
                    continue;
                }

                Set<String> slangs = this.definitionMap.get(cleanKeyword);
                if (slangs != null) {
                    slangs.remove(slang);
                    if (slangs.isEmpty()) {
                        this.definitionMap.remove(cleanKeyword);
                    }
                }
            }
        }
    }

    /**
     * @param slang
     * @return true/false
     */
    public boolean checkSlangExists(String slang) {
        return this.slangMap.containsKey(slang);
    }

    /**
     * @param slang
     * @param definitions
     */
    public void addNewSlang(String slang, List<String> definitions) {
        this.slangMap.put(slang, definitions);
        this.addDefinitionsToIndex(slang, definitions);
        System.out.println("Aded a new slang " + slang + " and saved.");
    }

    /**
     * @param slang
     * @param newDefinitions
     */
    public void overwriteSlang(String slang, List<String> newDefinitions) {
        List<String> oldDefinitions = this.slangMap.get(slang);
        if (oldDefinitions != null) {
            this.removeDefinitionsFromIndex(slang, oldDefinitions);
        }
        this.slangMap.put(slang, newDefinitions);
        this.addDefinitionsToIndex(slang, newDefinitions);
        System.out.println("Overwrited slang " + slang + " and saved.");
    }

    /**
     * @param slang
     * @param additionalDefinitions
     */
    public void duplicateSlang(String slang, List<String> additionalDefinitions) {
        List<String> oldDefinitions = this.slangMap.get(slang);
        List<String> combineDefinitions = new ArrayList<>(oldDefinitions);
        combineDefinitions.addAll(additionalDefinitions);
        this.addDefinitionsToIndex(slang, additionalDefinitions);
        this.slangMap.put(slang, combineDefinitions);
        System.out.println("Added the new definitions for slang " + slang + " and saved.");
    }

    /**
     * @return List<String>
     */
    public List<String> getSearchHistory() {
        return this.searchHistory;
    }

    /**
     * @param slang
     */
    public void deleteSlang(String slang) {
        if (slang == null)
            return;
        if (this.slangMap.containsKey(slang) == false) {
            System.out.println("Slang not found in slang map!");
            return;
        }
        if (this.definitionMap.containsValue(slang) == false) {
            System.out.println("Slang not found in definition map!");
            return;
        }

        this.slangMap.remove(slang);

        Iterator<Map.Entry<String, Set<String>>> iterator = this.definitionMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Set<String>> entry = iterator.next();
            Set<String> def = entry.getValue();

            if (def.contains(slang)) {
                if (def.size() == 1) {
                    iterator.remove();
                } else {
                    def.remove(slang);
                }
            }
        }
    }

    /**
     * @param originalKey
     * @param newKey
     * @param newDefinitions
     */
    public void editSlang(String originalKey, String newKey, List<String> newDefinitions) {
        List<String> oldDefinitions = this.slangMap.get(originalKey);

        if (oldDefinitions != null) {
            this.removeDefinitionsFromIndex(originalKey, oldDefinitions);
        }

        this.slangMap.remove(originalKey);
        this.slangMap.put(newKey, newDefinitions);

        this.addDefinitionsToIndex(newKey, newDefinitions);

        System.out.println("Slang was edited. The original slang: " + originalKey + ", The new slang: " + newKey);
    }

    public Map.Entry<String, List<String>> getSlangOfTheDay() {
        LocalDate today = LocalDate.now();

        if (today.equals(dateOfRandomSlang) && randomSlangOfDay != null
                && this.slangMap.containsKey(randomSlangOfDay)) {
            List<String> definitions = this.slangMap.get(randomSlangOfDay);
            return new AbstractMap.SimpleEntry<>(randomSlangOfDay, definitions);
        }

        if (this.slangMap == null || this.slangMap.isEmpty()) {
            return new AbstractMap.SimpleEntry<>("Error", List.of("Empty dictionary."));
        }

        List<String> keys = new ArrayList<>(this.slangMap.keySet());
        Random rand = new Random();
        this.randomSlangOfDay = keys.get(rand.nextInt(keys.size()));
        this.dateOfRandomSlang = today;

        System.out.println("Selected slang for a new day: " + this.randomSlangOfDay);

        List<String> definitions = this.slangMap.get(this.randomSlangOfDay);
        return new AbstractMap.SimpleEntry<>(this.randomSlangOfDay, definitions);
    }

    /**
     * Pick random slang for question
     * 
     * @return
     */
    public Map.Entry<String, List<String>> randomPickSlang() {
        if (this.slangMap == null || this.slangMap.isEmpty()) {
            return new AbstractMap.SimpleEntry<>("Error", List.of("Empty dictionary!"));
        }

        List<String> slangKeys = new ArrayList<>(this.slangMap.keySet());

        Random rand = new Random();
        String randomSlang = slangKeys.get(rand.nextInt(slangKeys.size()));

        List<String> definitions = this.slangMap.get(randomSlang);

        return new AbstractMap.SimpleEntry<>(randomSlang, definitions);
    }

    /**
     * Pick other answer for question
     * 
     * @param definitionsToAvoid
     * @return
     */
    public String randomPickDefinition(List<String> definitionsToAvoid) {
        if (this.slangMap == null || this.slangMap.isEmpty()) {
            return "Slang map is not initialized!";
        }

        Set<String> allDefinitionsPool = new HashSet<>();
        for (List<String> defsList : this.slangMap.values()) {
            allDefinitionsPool.addAll(defsList);
        }

        Set<String> avoidSet = new HashSet<>(definitionsToAvoid);

        List<String> wrongAnswersPool = new ArrayList<>();
        for (String def : allDefinitionsPool) {
            if (!avoidSet.contains(def)) {
                wrongAnswersPool.add(def);
            }
        }

        if (wrongAnswersPool.isEmpty()) {
            return "Cannot find any other definition.";
        }

        Random rand = new Random();

        Collections.shuffle(wrongAnswersPool);
        return wrongAnswersPool.get(rand.nextInt(wrongAnswersPool.size()));
    }
}
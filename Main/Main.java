package Main;

public class Main {
    public static void main(String args[]) {
        String textFilePath = "slang.txt";
        String serializedFilePath = "dictionary.ser";

        SlangDictionary sd = SlangDictionary.loadFromSerializedFile(serializedFilePath);

        if (sd == null) {
            sd = new SlangDictionary();
            sd.loadFromFile(textFilePath);
            sd.saveToFile(serializedFilePath);
        }
        System.out.println("\n The dictionary is ready.");
        sd.testFiveLines();
    }
}

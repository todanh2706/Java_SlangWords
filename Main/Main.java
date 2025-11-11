package Main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String args[]) {
        String textFilePath = "slang.txt";
        String serializedFilePath = "dictionary.ser";

        try {
            SlangDictionary sd = SlangDictionary.loadFromSerializedFile(serializedFilePath);

            if (sd == null) {
                sd = new SlangDictionary();
                sd.loadFromFile(textFilePath);
                sd.saveToFile(serializedFilePath);
            }

            System.out.println("\n The Dictionary is ready.");
            System.out.println(
                    "---------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("Test 5 first line:");
            sd.testFiveLines();
            System.out.println(
                    "---------------------------------------------------------------------------------------------------------------------------------------------");

            System.out.print("Enter the slang (and enter to search): ");
            Scanner scanner = new Scanner(System.in);
            String slang = scanner.nextLine();

            List<String> defs = sd.searchSlang(slang);
            if (defs.isEmpty()) {
                System.out.println("Not found!");
            } else {
                System.out.println("Meaning: " + defs.get(0));
            }

            scanner.close();
        } catch (FileNotFoundException err) {
            System.err.println("The source text file not foun: " + err.getMessage());
        } catch (IOException ioe) {
            System.err.println("An IO error occures: " + ioe.getMessage());
            ioe.printStackTrace();
        } catch (Exception err) {
            System.err.println("An error occures:");
            err.printStackTrace();
        }

    }
}

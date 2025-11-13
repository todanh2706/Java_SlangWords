package Main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {

    void handleChoose(Integer choice, SlangDictionary sd, Scanner scanner) {
        try {
            switch (choice) {
                case 0:
                    System.out.println("The program was stopped!");
                    break;

                case 1:
                    System.out.println("Deleting the .ser file...");
                    sd.resetDictionaryFile();
                    System.out.println("Deleted the .ser file successfully.");
                    break;

                case 2:
                    System.out.print("Enter the new text file path: ");
                    String textFilePath = scanner.nextLine();
                    sd.setTextFilePath(textFilePath);
                    System.out.println("Updated the text file path successfully.");
                    break;

                case 3:
                    // Slang searching
                    System.out.print("Enter the slang (and press \"Enter\" to search): ");
                    String slang = scanner.nextLine();

                    List<String> defs = sd.searchSlang(slang);
                    if (defs.isEmpty()) {
                        System.out.println("Not found!");
                    } else {
                        System.out.println("====== Meanings: ");
                        for (int i = 0; i < defs.size(); i++) {
                            System.out.println(defs.get(i));
                        }
                        System.out.println("=================");
                    }
                    sd.saveToFile();
                    break;

                case 4:
                    // Definition searching
                    System.out.print("Enter the definition (and press \"Enter\" to searching): ");
                    String definition = scanner.nextLine();

                    Set<String> slangs = sd.searchDefinition(definition);
                    if (slangs.isEmpty()) {
                        System.out.println("Not found!");
                    } else {
                        System.out.print("Slangs: ");
                        for (String s : slangs) {
                            System.out.print(s + " ");
                        }
                        System.out.print("\n");
                    }
                    sd.saveToFile();
                    break;

                case 5:
                    System.out.print("Enter the slang which you want to add: ");
                    String newSlang = scanner.nextLine();

                    System.out
                            .print("Enter the number of definitions you want to define for your slang " + newSlang
                                    + ": ");
                    int n = scanner.nextInt();
                    scanner.nextLine();

                    System.out
                            .println("Enter " + n + " definitions that you want to define for your slang " + newSlang
                                    + ": ");
                    List<String> newDefinitions = new ArrayList<>();
                    for (int i = 0; i < n; i++) {
                        System.out.print((i + 1) + ". ");
                        String def = scanner.nextLine();
                        newDefinitions.add(def);
                    }

                    if (sd.checkSlangExists(newSlang)) {
                        int c;
                        do {
                            System.out.println(
                                    "Sorry, this slang is already exists, but you can re-define for this slang with your new definitions list or adding definitions for this slang.");
                            System.out.println("You must choose one:");
                            System.out.println("0. Quit.");
                            System.out.println("1. Overwrite slang's definitions.");
                            System.out.println("2. Append the definitions list for this slang.");
                            System.out.print("Your choice: ");
                            c = scanner.nextInt();
                            scanner.nextLine();

                            if (c == 0)
                                break;
                            else if (c == 1) {
                                sd.overwriteSlang(newSlang, newDefinitions);
                            } else if (c == 2) {
                                sd.duplicateSlang(newSlang, newDefinitions);
                            } else {
                                System.out.println("Invalid input! Please try again!");
                            }
                        } while (c < 0 || c > 2);
                        System.out.println("Closed the process.");
                    }
                    sd.saveToFile();
                    break;

                default:
                    break;
            }
        } catch (Exception err) {
            System.err.println("An error occures:");
            err.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Main myApp = new Main();
        Scanner scanner = new Scanner(System.in);
        String textFilePath = "slang.txt";
        final String serializedFilePath = "dictionary.ser";
        int choice;
        try {

            SlangDictionary sd = SlangDictionary.loadFromSerializedFile(serializedFilePath);

            if (sd == null) {
                sd = new SlangDictionary();
                sd.setSerializedFilePath(serializedFilePath);
                System.out.print("Enter the text file path: ");
                textFilePath = scanner.nextLine();
                sd.setTextFilePath(textFilePath);
                sd.loadFromFile();
                sd.saveToFile();
            } else {
                sd.setSerializedFilePath(serializedFilePath);
                sd.setTextFilePath(textFilePath);
            }

            System.out.println("\n The Dictionary is ready.");
            System.out.println(
                    "---------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("Test 5 first line:");
            sd.testFiveLines();
            System.out.println(
                    "---------------------------------------------------------------------------------------------------------------------------------------------");

            do {
                // Searching history
                sd.showHistory();
                System.out.println("- Choose one option:");
                System.out.println("0. Quit.");
                System.out.println("1. Reset the .ser file.");
                System.out.println("2. Change text file.");
                System.out.println("3. Slang searching.");
                System.out.println("4. Definiton searching.");
                System.out.println("5. Add new slang.");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine();

                if (choice < 0 || choice > 5)
                    System.out.println("Invalid input! Please try again!");
                else
                    myApp.handleChoose(choice, sd, scanner);
            } while (choice != 0);

            System.out.println("****** The program was stopped.");

        } catch (FileNotFoundException err) {
            System.err.println("The source text file not found: " + err.getMessage());
        } catch (IOException ioe) {
            System.err.println("An IO error occures: " + ioe.getMessage());
            ioe.printStackTrace();
        } catch (Exception err) {
            System.err.println("An error occures:");
            err.printStackTrace();
        }
        scanner.close();

    }
}

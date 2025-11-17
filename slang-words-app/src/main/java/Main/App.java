package Main;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static final String serializedFilePath = "dictionary.ser";
    private static final String textFilePath = "slang.txt";

    private SlangDictionary dictionary;
    private Stage primaryStage;

    /**
     * Model loading
     */
    @Override
    public void init() throws Exception {
        dictionary = SlangDictionary.loadFromSerializedFile(serializedFilePath);

        if (dictionary == null) {
            System.out.println("Can not find file .ser. Will load from " + textFilePath);
            dictionary = new SlangDictionary();
            dictionary.setTextFilePath(textFilePath);
            dictionary.setSerializedFilePath(serializedFilePath);
            try {
                dictionary.loadFromFile();
                dictionary.saveToFile();
            } catch (IOException ioe) {
                System.err.println("Can not load file " + textFilePath);
                ioe.printStackTrace();
            }
        } else {
            dictionary.setTextFilePath(textFilePath);
            dictionary.setSerializedFilePath(serializedFilePath);
        }
        System.out.println("Dictionary is ready.");
    }

    /**
     * Show Welcome screen (GEMINI)
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Slang dictionary");

        dictionary.getSlangOfTheDay();
        dictionary.saveToFile();

        Parent welcomeRoot = WelcomeScreen.createView(dictionary, this);
        Scene scene = new Scene(welcomeRoot, 500, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Go to main
     */
    public void showMainScreen() {
        MainController controller = new MainController(dictionary);
        MainView view = new MainView(controller);

        Parent root = view.getView();

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Slang Dictionary - Main");
    }

    /**
     * Quit
     */
    public void quitApplication() {
        Platform.exit();
    }

    /**
     * Save model
     */
    @Override
    public void stop() throws Exception {
        System.out.println("Saving dictionary to " + serializedFilePath + "...");
        dictionary.saveToFile();
        System.out.println("Done. Goodbye!");
    }
}
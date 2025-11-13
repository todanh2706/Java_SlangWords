package Main;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application {
    private static final String serializedFilePath = "dictionary.ser";
    private static final String textFilePath = "slang.txt";

    private SlangDictionary dictionary;

    private ListView<String> resultsView = new ListView<>();
    private ListView<String> historyView = new ListView<>();

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

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Slang dictionary");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        TabPane tabPane = new TabPane();
        Tab searchTab = new Tab("Search", createSearchTab());
        Tab addTab = new Tab("Add Slang", createAddTab());
        Tab adminTab = new Tab("Admin", createAdminTab());

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(searchTab, addTab, adminTab);

        root.setCenter(tabPane);

        VBox historyBox = createHistoryView();
        root.setRight(historyBox);
        BorderPane.setMargin(historyBox, new Insets(0, 0, 0, 10));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Search Tab
     */
    private VBox createSearchTab() {
        TextField searchField = new TextField();
        searchField.setPromptText("Enter slang or definition...");

        Button searchSlangBtn = new Button("Search by Slang");
        Button searchDefBtn = new Button("Search by Definition");

        searchSlangBtn.setOnAction(e -> handleSearchSlang(searchField.getText()));
        searchDefBtn.setOnAction(e -> handleSearchDefinition(searchField.getText()));

        HBox searchBox = new HBox(10, searchSlangBtn, searchDefBtn);

        VBox layout = new VBox(10, new Label("Enter search term"), searchField, searchBox, new Label("Result: "),
                resultsView);
        VBox.setVgrow(resultsView, Priority.ALWAYS);

        return layout;
    }

    /**
     * Add Tab
     */
    private VBox createAddTab() {
        TextField slangField = new TextField();
        slangField.setPromptText("Enter new slang");

        TextArea defArea = new TextArea();
        defArea.setPromptText("Enter definitions, one per line...");

        Button addBtn = new Button("Add New Slang");

        addBtn.setOnAction(e -> handleAddSlang(slangField.getText(), defArea.getText()));

        VBox layout = new VBox(10, new Label("Slang:"), slangField, new Label("Definitions:"), defArea, addBtn);
        VBox.setVgrow(defArea, Priority.ALWAYS);
        return layout;
    }

    /**
     * Admin Tab
     */
    private VBox createAdminTab() {
        Button resetBtn = new Button("Reset Dictionary from slang.txt");

        resetBtn.setOnAction(e -> handleResetDictionary());

        VBox layout = new VBox(10, resetBtn);
        layout.setPadding(new Insets(10));
        return layout;
    }

    /**
     * History area
     */
    private VBox createHistoryView() {
        updateHistoryView();

        VBox layout = new VBox(10, new Label("Search History"), historyView);
        VBox.setVgrow(historyView, Priority.ALWAYS);
        layout.setMinWidth(200);
        return layout;
    }

    /**
     * Slang searching
     */
    private void handleSearchSlang(String slang) {
        if (slang == null || slang.trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Input Error", "Please enter a slang to search.");
            return;
        }

        List<String> defs = dictionary.searchSlang(slang);
        resultsView.getItems().clear();

        if (defs.isEmpty()) {
            resultsView.getItems().add("No definitions found for '" + slang + "'");
        } else {
            resultsView.getItems().addAll(defs);
        }
        updateHistoryView();
    }

    /**
     * Defintion searching
     */
    private void handleSearchDefinition(String definition) {
        if (definition == null || definition.trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Input Error", "Please enter a definition keyword to search.");
            return;
        }

        Set<String> slangs = dictionary.searchDefinition(definition);
        resultsView.getItems().clear();

        if (slangs.isEmpty()) {
            resultsView.getItems().add("No slangs found for keyword '" + definition + "'");
        } else {
            resultsView.getItems().addAll(slangs);
        }
        updateHistoryView();
    }

    /**
     * History updating
     */
    private void updateHistoryView() {
        historyView.setItems(FXCollections.observableArrayList(dictionary.getSearchHistory()));
    }

    /**
     * Add slang
     */
    private void handleAddSlang(String slang, String defsText) {
        if (slang == null || slang.trim().isEmpty() || defsText == null || defsText.trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Input Error", "Slang and Definitions cannot be empty.");
            return;
        }

        List<String> newDefinitions = Arrays.asList(defsText.trim().split("\\r?\\n"));

        if (dictionary.checkSlangExists(slang)) {
            Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
            confirmAlert.setTitle("Slang Exists");
            confirmAlert.setHeaderText("Slang '" + slang + "' already exists.");
            confirmAlert.setContentText("What do you want to do?");

            ButtonType btnOverwrite = new ButtonType("Overwrite");
            ButtonType btnDuplicate = new ButtonType("Duplicate (Append)");
            ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            confirmAlert.getButtonTypes().setAll(btnOverwrite, btnDuplicate, btnCancel);

            Optional<ButtonType> result = confirmAlert.showAndWait();

            if (result.isPresent()) {
                if (result.get() == btnOverwrite) {
                    dictionary.overwriteSlang(slang, newDefinitions);
                    showAlert(AlertType.INFORMATION, "Success", "Slang overwritten successfully.");
                } else if (result.get() == btnDuplicate) {
                    dictionary.duplicateSlang(slang, newDefinitions);
                    showAlert(AlertType.INFORMATION, "Success", "Definitions appended successfully.");
                }
            }

        } else {
            dictionary.addNewSlang(slang, newDefinitions);
            showAlert(AlertType.INFORMATION, "Success", "New slang added successfully.");
        }
    }

    /**
     * Reset dictionary
     */
    private void handleResetDictionary() {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Reset");
        confirmAlert.setHeaderText("Are you sure you want to reset the dictionary?");
        confirmAlert.setContentText(
                "This will delete the " + serializedFilePath + " and reload all data from " + textFilePath + ".");

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = dictionary.resetDictionaryFile();
            if (deleted) {
                try {
                    dictionary.loadFromFile();
                    showAlert(AlertType.INFORMATION, "Success", "Dictionary reset successfully from " + textFilePath);
                    resultsView.getItems().clear();
                    historyView.getItems().clear();
                } catch (IOException e) {
                    showAlert(AlertType.ERROR, "Error",
                            "Failed to reload from " + textFilePath + ": " + e.getMessage());
                }
            } else {
                showAlert(AlertType.ERROR, "Error", "Could not delete " + serializedFilePath + ".");
            }
        }
    }

    /**
     * Helper
     */
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Saving dictionary to " + serializedFilePath + "...");
        dictionary.saveToFile();
        System.out.println("Done. Goodbye!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

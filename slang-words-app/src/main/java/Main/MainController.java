package Main;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.beans.property.*;

public class MainController {
    private SlangDictionary dictionary;

    // Some JavaFX Property that can listen
    private ObservableList<String> resultsList = FXCollections.observableArrayList();
    private ObservableList<String> historyList = FXCollections.observableArrayList();

    // Property for editiing tab
    private StringProperty editSlangKey = new SimpleStringProperty();
    private StringProperty editDefinitions = new SimpleStringProperty();
    private StringProperty editStatus = new SimpleStringProperty();
    private BooleanProperty editFormVisible = new SimpleBooleanProperty(false);

    // Original key keeper (editing feature)
    private AtomicReference<String> originalSlangKey = new AtomicReference<>();

    // ------------------ Getters for Properties ------------------
    public MainController(SlangDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public ObservableList<String> getResultsList() {
        return resultsList;
    }

    public ObservableList<String> getHistoryList() {
        return historyList;
    }

    public StringProperty editSlangKeyProperty() {
        return editSlangKey;
    }

    public StringProperty editDefinitionsProperty() {
        return editDefinitions;
    }

    public StringProperty editStatusProperty() {
        return editStatus;
    }

    public BooleanProperty editFormVisibleProperty() {
        return editFormVisible;
    }

    // ------------------ Event handler ------------------

    /**
     * Update the history list
     */
    public void updateHistoryView() {
        historyList.setAll(dictionary.getSearchHistory());
    }

    /**
     * Handle slang searching
     * 
     * @param slang
     */
    public void handleSearchSlang(String slang) {
        if (slang == null || slang.trim().isEmpty()) {
            AlertUtil.showAlert(AlertType.WARNING, "Input Error", "Please enter a slang to search.");
            return;
        }

        List<String> defs = dictionary.searchSlang(slang);
        resultsList.clear();

        if (defs.isEmpty()) {
            resultsList.add("No definitions found for '" + slang + "'");
        } else {
            resultsList.addAll(defs);
        }
        updateHistoryView();
    }

    /**
     * Handle definition searching
     * 
     * @param definition
     */
    public void handleSearchDefinition(String definition) {
        if (definition == null || definition.trim().isEmpty()) {
            AlertUtil.showAlert(AlertType.WARNING, "Input Error", "Please enter a definition keyword to search.");
            return;
        }

        Set<String> slangs = dictionary.searchDefinition(definition);
        resultsList.clear();

        if (slangs.isEmpty()) {
            resultsList.add("No slangs found for keyword '" + definition + "'");
        } else {
            resultsList.addAll(slangs);
        }
        updateHistoryView();
    }

    /**
     * Handle add new slang
     * 
     * @param slang
     * @param defsText
     */
    public void handleAddSlang(String slang, String defsText) {
        if (slang == null || slang.trim().isEmpty() || defsText == null || defsText.trim().isEmpty()) {
            AlertUtil.showAlert(AlertType.WARNING, "Input Error", "Slang an Definitions cannot be empty.");
            return;
        }

        List<String> newDefinitions = Arrays.asList(defsText.trim().split("\\r?\\n"));
        slang = slang.trim();

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
                    AlertUtil.showAlert(AlertType.INFORMATION, "Success", "Slang overwritten successfully.");
                } else if (result.get() == btnDuplicate) {
                    dictionary.duplicateSlang(slang, newDefinitions);
                    AlertUtil.showAlert(AlertType.INFORMATION, "Success", "Definitions appended successfully.");
                }
            }
        } else {
            dictionary.addNewSlang(slang, newDefinitions);
            AlertUtil.showAlert(AlertType.INFORMATION, "Success", "New slang added successfully.");
        }
    }

    /**
     * Reset the dictionary
     * 
     */
    public void handleResetDictionary() {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Reset");
        confirmAlert.setHeaderText("Are you sure you want to reset the dictionary?");
        confirmAlert.setContentText(
                "This will delete the .ser file and reload all data from the .txt file.");

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = dictionary.resetDictionaryFile();
            if (deleted) {
                try {
                    dictionary.loadFromFile();
                    AlertUtil.showAlert(AlertType.INFORMATION, "Success", "Dictionary reset successfully.");
                    resultsList.clear();
                    historyList.clear();
                } catch (IOException e) {
                    AlertUtil.showAlert(AlertType.ERROR, "Error",
                            "Failed to reload from .txt file: " + e.getMessage());
                }
            } else {
                AlertUtil.showAlert(AlertType.ERROR, "Error", "Could not delete .ser file.");
            }
        }
    }

    /**
     * Handle find slang to edit
     * 
     * @param slangToFind
     */
    public void handleFindSlangForEdit(String slangToFind) {
        if (slangToFind == null || slangToFind.isEmpty()) {
            editStatus.set("Please enter a slang to find.");
            editFormVisible.set(false);
            return;
        }

        if (this.dictionary.checkSlangExists(slangToFind)) {
            originalSlangKey.set(slangToFind);

            List<String> defs = this.dictionary.searchSlang(slangToFind);

            editSlangKey.set(slangToFind);
            editDefinitions.set(String.join("\n", defs));
            editStatus.set("Slang found. You can edit it now.");
            editFormVisible.set(true);
        } else {
            editStatus.set("Slang '" + slangToFind + "' not found!");
            editFormVisible.set(false);
            originalSlangKey.set(null);
        }
    }

    /**
     * Handle save edited slang
     * 
     */
    public void handleSaveSlangEdit() {
        String originalKey = originalSlangKey.get();
        String newKey = editSlangKey.get().trim();
        String newDefsText = editDefinitions.get().trim();

        if (originalKey == null || newKey.isEmpty() || newDefsText.isEmpty()) {
            editStatus.set("Error: Can not save. Slang or definitions are empty!");
            return;
        }

        List<String> newDefinitions = Arrays.asList(newDefsText.split("\\r?\\n"));
        this.dictionary.editSlang(originalKey, newKey, newDefinitions);

        editStatus.set("Slang '" + originalKey + "' updated successfully!");
        editFormVisible.set(false);
        originalSlangKey.set(null);
    }
}

package Main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MainView {

    private MainController controller;

    // UI component that the Controller need to update
    private ListView<String> resultsView = new ListView<>();
    private ListView<String> historyView = new ListView<>();

    public MainView(MainController controller) {
        this.controller = controller;
    }

    /**
     * Create root node form UI
     */
    public Parent getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        TabPane tabPane = new TabPane();
        Tab searchTab = new Tab("Search", createSearchTab());
        Tab addTab = new Tab("Add Slang", createAddTab());
        Tab adminTab = new Tab("Admin", createAdminTab());
        Tab editTab = new Tab("Edit Slang", createEditTab());

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(searchTab, addTab, adminTab, editTab);

        root.setCenter(tabPane);

        VBox historyBox = createHistoryView();
        root.setRight(historyBox);
        BorderPane.setMargin(historyBox, new Insets(0, 0, 0, 10));

        // Binding ListView with ObservableList of Controller
        resultsView.setItems(controller.getResultsList());
        historyView.setItems(controller.getHistoryList());

        // Load the initial history
        controller.updateHistoryView();

        return root;
    }

    /**
     * Create searching tab
     */
    private VBox createSearchTab() {
        TextField searchField = new TextField();
        searchField.setPromptText("Enter slang or definition...");

        Button searchSlangBtn = new Button("Search by Slang");
        Button searchDefBtn = new Button("Search by Definition");

        // Bind the evnt with the Controller
        searchSlangBtn.setOnAction(e -> controller.handleSearchSlang(searchField.getText()));
        searchDefBtn.setOnAction(e -> controller.handleSearchDefinition(searchField.getText()));

        HBox searchBox = new HBox(10, searchSlangBtn, searchDefBtn);

        VBox layout = new VBox(10, new Label("Enter search term"), searchField, searchBox, new Label("Result: "),
                resultsView);
        VBox.setVgrow(resultsView, Priority.ALWAYS);

        return layout;
    }

    /**
     * Create adding slang tab
     */
    private VBox createAddTab() {
        TextField slangField = new TextField();
        slangField.setPromptText("Enter new slang");

        TextArea defArea = new TextArea();
        defArea.setPromptText("Enter definitions, one per line...");

        Button addBtn = new Button("Add New Slang");

        // Bind the event with controller
        addBtn.setOnAction(e -> {
            controller.handleAddSlang(slangField.getText(), defArea.getText());
            // Delete text
            slangField.clear();
            defArea.clear();
        });

        VBox layout = new VBox(10, new Label("Slang:"), slangField, new Label("Definitions:"), defArea, addBtn);
        VBox.setVgrow(defArea, Priority.ALWAYS);
        return layout;
    }

    /**
     * Create admin tab
     */
    private VBox createAdminTab() {
        Button resetBtn = new Button("Reset Dictionary from slang.txt");

        // Bind event with Controller
        resetBtn.setOnAction(e -> controller.handleResetDictionary());

        VBox layout = new VBox(10, resetBtn);
        layout.setPadding(new Insets(10));
        return layout;
    }

    /**
     * Create editing slang tab
     */
    private VBox createEditTab() {
        TextField searchField = new TextField();
        searchField.setPromptText("Enter the slang that you want to change...");
        searchField.setPrefWidth(300);

        Button findBtn = new Button("Find");
        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        HBox searchBox = new HBox(10, searchField, findBtn);
        searchBox.setAlignment(Pos.CENTER);

        Label statusLabel = new Label();
        Label slangLabel = new Label("Slang Word: ");
        TextField slangField = new TextField();
        Label defLabel = new Label("Definitions: ");
        TextArea definitionsArea = new TextArea();
        definitionsArea.setWrapText(true);

        VBox editForm = new VBox(10, slangLabel, slangField, defLabel, definitionsArea, saveBtn, statusLabel);

        // Binding UI attributes with Properties of Controller
        statusLabel.textProperty().bind(controller.editStatusProperty());
        slangField.textProperty().bindBidirectional(controller.editSlangKeyProperty());
        definitionsArea.textProperty().bindBidirectional(controller.editDefinitionsProperty());
        editForm.visibleProperty().bind(controller.editFormVisibleProperty());

        // Bind the event with Controller
        findBtn.setOnAction(e -> controller.handleFindSlangForEdit(searchField.getText().trim()));

        saveBtn.setOnAction(e -> {
            controller.handleSaveSlangEdit();
            searchField.clear();
        });

        VBox layout = new VBox(20, searchBox, editForm);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        return layout;
    }

    /**
     * Craete History Area
     */
    private VBox createHistoryView() {
        VBox layout = new VBox(10, new Label("Search History"), historyView);
        VBox.setVgrow(historyView, Priority.ALWAYS);
        layout.setMinWidth(200);
        return layout;
    }
}
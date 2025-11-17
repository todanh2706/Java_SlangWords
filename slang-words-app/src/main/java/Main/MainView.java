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
        Parent quizTabContent = createQuizTab();
        Tab quizTab = new Tab("Fun Quiz", quizTabContent);

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(searchTab, addTab, adminTab, editTab, quizTab);

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
     * Create quiz tab
     */
    private Parent createQuizTab() {
        Label welcomeLabel = new Label("Welcome to our mini quiz, are you ready?");
        welcomeLabel.setStyle("-fx-font-size: 16px;");

        Label promptLabel = new Label("Choose your challenge:");
        promptLabel.setStyle("-fx-font-size: 14px;");

        Button slangQuizButton = new Button("Quiz: Slang -> Definition");
        slangQuizButton.setPrefWidth(250);
        slangQuizButton.setStyle("-fx-font-size: 14px;");

        Button defQuizButton = new Button("Quiz: Definition -> Slang");
        defQuizButton.setPrefWidth(250);
        defQuizButton.setStyle("-fx-font-size: 14px;");

        slangQuizButton.setOnAction(e -> controller.handleStartSlangQuiz());
        defQuizButton.setOnAction(e -> controller.handleStartDefinitionQuiz());

        VBox welcomeLayout = new VBox(20, welcomeLabel, promptLabel, slangQuizButton, defQuizButton);
        welcomeLayout.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_LEFT);

        Label questionLabel = new Label();
        questionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold");
        questionLabel.textProperty().bind(controller.quizQuestionProperty());

        ToggleGroup optionsGroup = controller.getQuizToggleGroup();

        RadioButton option1 = new RadioButton();
        option1.setToggleGroup(optionsGroup);
        option1.textProperty().bind(controller.quizOption1Property());

        RadioButton option2 = new RadioButton();
        option2.setToggleGroup(optionsGroup);
        option2.textProperty().bind(controller.quizOption2Property());

        RadioButton option3 = new RadioButton();
        option3.setToggleGroup(optionsGroup);
        option3.textProperty().bind(controller.quizOption3Property());

        RadioButton option4 = new RadioButton();
        option4.setToggleGroup(optionsGroup);
        option4.textProperty().bind(controller.quizOption4Property());

        Button submitBtn = new Button("Submit");
        Button nextBtn = new Button("New Question");
        Button quitButton = new Button("End");

        quitButton.setOnAction(e -> controller.handleQuitQuiz());
        submitBtn.setOnAction(e -> controller.handleSubmitQuiz());
        nextBtn.setOnAction(e -> controller.handleNextQuestion());

        HBox buttonBox = new HBox(10, submitBtn, nextBtn, quitButton);

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-weight: bold;");
        statusLabel.textProperty().bind(controller.quizStatusProperty());

        layout.getChildren().addAll(questionLabel, option1, option2, option3, option4, buttonBox, statusLabel);

        StackPane root = new StackPane();
        root.getChildren().addAll(welcomeLayout, layout);

        welcomeLayout.visibleProperty().bind(controller.quizStateProperty().isEqualTo(0));
        layout.visibleProperty().bind(controller.quizStateProperty().isNotEqualTo(0));

        return root;
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
        Label resetLabel = new Label("Reset dictionary using the slang.txt file:");
        Button resetBtn = new Button("Reset Dictionary");
        resetBtn.setOnAction(e -> controller.handleResetDictionary());
        VBox resetBox = new VBox(5, resetLabel, resetBtn);

        VBox layout = new VBox(10, resetBox);
        layout.setPadding(new Insets(20));

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
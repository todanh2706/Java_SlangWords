package Main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Map;

public class WelcomeScreen {

    /**
     * UI for welcome page
     * 
     * @param dictionary
     * @param app
     */
    public static Parent createView(SlangDictionary dictionary, App app) {

        // Main layout
        VBox rootLayout = new VBox(25);
        rootLayout.setAlignment(Pos.CENTER);
        rootLayout.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Welcome to Slang Dictionary");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Slang of the day box
        VBox sodLayout = new VBox(8);
        sodLayout.setAlignment(Pos.CENTER);
        sodLayout.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 15;");

        // Get slang of the day
        Map.Entry<String, List<String>> sod = dictionary.getSlangOfTheDay();
        String slang = sod.getKey();
        String definitions = String.join(" | ", sod.getValue());

        Label sodTitle = new Label("Slang of the Day");
        sodTitle.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");

        Label slangLabel = new Label(slang);
        slangLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #007bff;");

        Label defLabel = new Label(definitions);
        defLabel.setWrapText(true); // Auto break line
        defLabel.setTextAlignment(TextAlignment.CENTER);

        sodLayout.getChildren().addAll(sodTitle, slangLabel, defLabel);

        // Buttons
        HBox buttonLayout = new HBox(15);
        buttonLayout.setAlignment(Pos.CENTER);

        Button continueButton = new Button("Continue");
        continueButton.setPrefWidth(100);
        continueButton.setOnAction(e -> app.showMainScreen());

        Button quitButton = new Button("Quit");
        quitButton.setPrefWidth(100);
        quitButton.setOnAction(e -> app.quitApplication());

        buttonLayout.getChildren().addAll(continueButton, quitButton);

        rootLayout.getChildren().addAll(titleLabel, sodLayout, buttonLayout);

        return rootLayout;
    }
}
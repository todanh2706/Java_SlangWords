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
     * Tạo giao diện (View) cho màn hình chào.
     * 
     * @param dictionary Đối tượng từ điển để lấy Slang
     * @param app        Đối tượng App để gọi hành động (Continue/Quit)
     * @return Một Parent node (layout) để hiển thị trong Scene
     */
    public static Parent createView(SlangDictionary dictionary, App app) {

        // 1. Layout chính (dọc)
        VBox rootLayout = new VBox(25); // 25 là khoảng cách (spacing)
        rootLayout.setAlignment(Pos.CENTER);
        rootLayout.setPadding(new Insets(20));

        // 2. Tiêu đề
        Label titleLabel = new Label("Welcome to Slang Dictionary");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // 3. Khung "Slang của Ngày"
        VBox sodLayout = new VBox(8); // Khoảng cách 8
        sodLayout.setAlignment(Pos.CENTER);
        sodLayout.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 15;");

        // Lấy slang của ngày
        Map.Entry<String, List<String>> sod = dictionary.getSlangOfTheDay();
        String slang = sod.getKey();
        String definitions = String.join(" | ", sod.getValue());

        Label sodTitle = new Label("Slang of the Day");
        sodTitle.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");

        Label slangLabel = new Label(slang);
        slangLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #007bff;");

        Label defLabel = new Label(definitions);
        defLabel.setWrapText(true); // Tự động xuống hàng nếu nghĩa quá dài
        defLabel.setTextAlignment(TextAlignment.CENTER);

        sodLayout.getChildren().addAll(sodTitle, slangLabel, defLabel);

        // 4. Hàng chứa 2 nút bấm
        HBox buttonLayout = new HBox(15); // Khoảng cách 15 giữa 2 nút
        buttonLayout.setAlignment(Pos.CENTER);

        Button continueButton = new Button("Continue");
        continueButton.setPrefWidth(100);
        // Gán hành động: Gọi hàm trong App.java
        continueButton.setOnAction(e -> app.showMainScreen());

        Button quitButton = new Button("Quit");
        quitButton.setPrefWidth(100);
        // Gán hành động: Gọi hàm trong App.java
        quitButton.setOnAction(e -> app.quitApplication());

        buttonLayout.getChildren().addAll(continueButton, quitButton);

        // 5. Thêm tất cả vào layout chính
        rootLayout.getChildren().addAll(titleLabel, sodLayout, buttonLayout);

        return rootLayout;
    }
}
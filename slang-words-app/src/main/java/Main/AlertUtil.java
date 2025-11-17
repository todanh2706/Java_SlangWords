/**
 * GEMINI GENERATED FILE
 */

package Main;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertUtil {
    /**
     * Show a window to alert
     */
    public static void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

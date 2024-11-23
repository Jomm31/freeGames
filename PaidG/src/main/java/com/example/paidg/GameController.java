package com.example.paidg;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameController {

    @FXML
    private Button downloadGTAV, downloadSummertimeSAGA, downloadStickman, downloadMinecraft;
    @FXML
    private Button downloadRiseOfKingdoms, downloadSleepingGirl, GamesBought, Logout, EditAccount;

    private Map<Button, Game> gameMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Initialize games
        Game gtav = new PaidGame("GTA V", 1);
        Game summertimeSaga = new PaidGame("Summertime Saga", 2);
        Game stickman = new FreeGame("Stickman", 3);
        Game minecraft = new PaidGame("Minecraft", 4);
        Game riseOfKingdoms = new FreeGame("Rise of Kingdoms", 5);
        Game sleepingGirl = new FreeGame("Sleeping Girl", 6);

        // Map buttons to games
        gameMap.put(downloadGTAV, gtav);
        gameMap.put(downloadSummertimeSAGA, summertimeSaga);
        gameMap.put(downloadStickman, stickman);
        gameMap.put(downloadMinecraft, minecraft);
        gameMap.put(downloadRiseOfKingdoms, riseOfKingdoms);
        gameMap.put(downloadSleepingGirl, sleepingGirl);

        // Set button actions
        gameMap.forEach((button, game) -> button.setOnAction(event -> game.download()));

        // Handle navigation buttons
        GamesBought.setOnAction(event -> openGamesBought());
        EditAccount.setOnAction(event -> openEditAccount());
        Logout.setOnAction(event -> handleLogout());
    }

    private void openGamesBought() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GamesDownloaded.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) GamesBought.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to open Games Bought page.");
        }
    }

    private void openEditAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditProfile.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) EditAccount.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to open Edit Account page.");
        }
    }

    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AccPage.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) Logout.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Account Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to log out.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

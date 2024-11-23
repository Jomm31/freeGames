package com.example.paidg;

import javafx.scene.control.Alert;

public class FreeGame extends Game {

    public FreeGame(String name, int gameId) {
        super(name, gameId);
    }

    @Override
    public void download() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Free game \"" + getName() + "\" has been downloaded successfully!");
        alert.showAndWait();
    }
}

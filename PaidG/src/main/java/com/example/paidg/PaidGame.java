package com.example.paidg;
import javafx.scene.control.Alert;

public class PaidGame extends Game {
    private boolean isPurchased;

    public PaidGame(String name, int gameId) {
        super(name, gameId);
        this.isPurchased = false;
    }

    @Override
    public void download() {
        if (!isPurchased) {
            purchaseGame();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setTitle("Already Purchased");
            alert.setContentText("You already own \"" + getName() + "\".");
            alert.showAndWait();
        }
    }

    private void purchaseGame() {
        executeTransaction(getGameId());
        isPurchased = true;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Paid game \"" + getName() + "\" has been purchased and downloaded successfully!");
        alert.showAndWait();
    }

    private void executeTransaction(int gameId) {
        // Database transaction logic here, similar to your original executeTransaction method.
        // Use the shared transaction logic, connecting to the database and inserting records.
    }
}
package com.example.paidg;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Controller for the Games Downloaded page.
 * Displays transaction history for downloaded games.
 */
public class GamesDownloadedController {

    @FXML
    private Button gamesDownloadedButton;

    @FXML
    private TableColumn<Transaction, Integer> tableTransactionId;

    @FXML
    private TableColumn<Transaction, Date> tableTransactionDate;

    @FXML
    private TableColumn<Transaction, String> tableGameTitle;

    @FXML
    private TableColumn<Transaction, Integer> tableGameID;

    @FXML
    private TableColumn<Transaction, Integer> tableUserId;

    @FXML
    private TableView<Transaction> transactionTable;

    /**
     * Initializes the controller by setting up the table columns and populating data.
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        loadTransactionData();
        gamesDownloadedButton.setOnAction(event -> navigateToPage("games.fxml", "Games"));
    }

    /**
     * Configures the table columns with corresponding data fields.
     */
    private void setupTableColumns() {
        tableTransactionId.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        tableTransactionDate.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        tableGameTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        tableGameID.setCellValueFactory(new PropertyValueFactory<>("gameId"));
        tableUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
    }

    /**
     * Loads transaction data into the table.
     */
    private void loadTransactionData() {
        ObservableList<Transaction> transactions = fetchTransactionData();
        transactionTable.setItems(transactions);
    }

    /**
     * Fetches transaction data from the database for the logged-in user.
     *
     * @return An ObservableList of transactions.
     */
    private ObservableList<Transaction> fetchTransactionData() {
        ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM transaction WHERE UserID = ?";

        try (Connection con = Database.connectDb();
             PreparedStatement statement = con.prepareStatement(sql)) {

            if (con == null) {
                throw new SQLException("Database connection failed.");
            }

            statement.setInt(1, AccPageController.userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int transactionId = resultSet.getInt("TransactionID");
                Date transactionDate = resultSet.getDate("Date");
                int gameId = resultSet.getInt("GameId");
                int userId = resultSet.getInt("UserID");
                String gameTitle = fetchGameTitle(gameId);

                Transaction transaction = new Transaction(transactionId, transactionDate, gameTitle, gameId, userId);
                transactionList.add(transaction);
            }
        } catch (SQLException e) {
            showErrorDialog("Error fetching transaction data: " + e.getMessage());
        }
        return transactionList;
    }

    /**
     * Fetches the title of a game based on its ID.
     *
     * @param gameId The ID of the game.
     * @return The title of the game.
     */
    private String fetchGameTitle(int gameId) {
        String gameTitle = "";
        String sql = "SELECT Title FROM games WHERE GameID = ?";

        try (Connection con = Database.connectDb();
             PreparedStatement statement = con.prepareStatement(sql)) {

            if (con == null) {
                throw new SQLException("Database connection failed.");
            }

            statement.setInt(1, gameId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                gameTitle = resultSet.getString("Title");
            }
        } catch (SQLException e) {
            showErrorDialog("Error fetching game title: " + e.getMessage());
        }
        return gameTitle;
    }

    /**
     * Navigates to a specified page.
     *
     * @param fxmlFile The FXML file for the target page.
     * @param title    The title of the target page.
     */
    private void navigateToPage(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) gamesDownloadedButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            showErrorDialog("Error navigating to " + title + " page: " + e.getMessage());
        }
    }

    /**
     * Displays an error dialog with a given message.
     *
     * @param message The error message to display.
     */
    private void showErrorDialog(String message) {
        // Replace with proper JavaFX alert if needed
        System.err.println(message);
    }
}

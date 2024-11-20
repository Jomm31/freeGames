package com.example.paidg;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller for the Sign-In Page.
 * Handles user authentication and navigation to other pages.
 */
public class AccPageController {

    @FXML
    private TextField signInEmailField;
    @FXML
    private PasswordField signInPassword;
    @FXML
    private Button signInButton;

    public static int userId;

    /**
     * Navigates to the Registration page when the "Sign Up" button is clicked.
     */
    @FXML
    protected void onSignUpButtonClick() {
        navigateToPage("Register.fxml", "Register");
    }

    /**
     * Handles the Login button click event.
     * Validates input fields, authenticates the user, and navigates to the Games page if successful.
     */
    @FXML
    protected void onLoginButtonClick() {
        String email = signInEmailField.getText();
        String password = signInPassword.getText();

        // Validate input fields
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Email and password must be filled out.");
            return;
        }

        // Authenticate user
        if (authenticateUser(email, password)) {
            navigateToPage("Games.fxml", "Games");
        } else {
            showAlert("Error", "Invalid email or password.");
        }
    }

    /**
     * Authenticates the user by verifying their email and password against the database.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @return true if authentication is successful, false otherwise.
     */
    private boolean authenticateUser(String email, String password) {
        try (Connection connection = Database.connectDb()) {
            if (connection == null) {
                showAlert("Error", "Failed to connect to the database.");
                return false;
            }

            String sql = "SELECT UserID, Password FROM user WHERE Email = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String storedPassword = rs.getString("Password");
                    userId = rs.getInt("UserID");
                    return verifyPassword(password, storedPassword);
                }
                return false; // No user found
            }
        } catch (SQLException e) {
            showAlert("Error", "Database error occurred: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifies the input password against the stored hashed password.
     * Replace this method's implementation with a proper hashing check (e.g., BCrypt).
     *
     * @param inputPassword       The password entered by the user.
     * @param storedHashedPassword The hashed password stored in the database.
     * @return true if the password matches, false otherwise.
     */
    private boolean verifyPassword(String inputPassword, String storedHashedPassword) {
        // Implement proper hashing logic (e.g., BCrypt).
        return inputPassword.equals(storedHashedPassword);
    }

    /**
     * Navigates to the specified page.
     *
     * @param fxmlFile The FXML file to load.
     * @param title    The title of the new scene.
     */
    private void navigateToPage(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) signInButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load " + title + " page.");
            e.printStackTrace();
        }
    }

    /**
     * Displays an alert dialog with the given title and message.
     *
     * @param title   The title of the alert.
     * @param message The message to display in the alert.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

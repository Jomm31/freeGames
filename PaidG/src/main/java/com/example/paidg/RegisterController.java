package com.example.paidg;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

public class RegisterController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private RadioButton femaleRadio;
    @FXML
    private RadioButton maleRadio;
    @FXML
    private RadioButton othersRadio;
    @FXML
    private DatePicker birthdayPicker;
    @FXML
    private Button signUpButton;
    @FXML
    private Button signInButton;
    @FXML
    private ToggleGroup genderToggleGroup;

    @FXML
    protected void onSignUpButtonClick() {
        maleRadio.setToggleGroup(genderToggleGroup);
        othersRadio.setToggleGroup(genderToggleGroup);
        femaleRadio.setToggleGroup(genderToggleGroup);
        LocalDate birthday1 = birthdayPicker.getValue();

        // Check if all required fields are filled out
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                emailField.getText().isEmpty() || passwordField.getText().isEmpty() || birthday1 == null) {
            showAlert("Error", "All fields must be filled out, including your birthday.");
            return;
        }

        // Get the selected gender
        RadioButton selectedRadioButton = (RadioButton) genderToggleGroup.getSelectedToggle();
        String gender = selectedRadioButton != null ? selectedRadioButton.getText() : "Not Specified"; // Handle case where no gender is selected

        // Validate age
        int age = Period.between(birthday1, LocalDate.now()).getYears();
        if (age < 8) {
            showAlert("Error", "You must be at least 8 years old to sign up");
            return;
        }

        // Save user to database
        boolean isSaved = saveUserToDatabase(firstNameField.getText(), lastNameField.getText(),
                emailField.getText(), passwordField.getText(), gender, birthday1);

        if (isSaved) {
            showAlert("Success", "Account created successfully!");
            goBackToSignIn();
        } else {
            showAlert("Error", "Account creation failed.");
        }
    }

    @FXML
    private void onSignInButtonClick() {
        try {
            // Load the new scene (games.fxml)
            AnchorPane pane = FXMLLoader.load(getClass().getResource("games.fxml"));
            Scene scene = new Scene(pane);

            // Get the current stage and set the new scene
            Stage stage = (Stage) signInButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load games page.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void goBackToSignIn() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AccPage.fxml"));
            Pane signInPane = fxmlLoader.load();
            Stage stage = (Stage) signUpButton.getScene().getWindow();
            Scene scene = new Scene(signInPane);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load sign-in page.");
        }
    }

    private boolean saveUserToDatabase(String firstName, String lastName, String email, String password, String gender, LocalDate birthday1) {
        Connection connection = Database.connectDb();
        if (connection != null) {
            String sql = "INSERT INTO user (FirstName, LastName, Email, Password, Gender, Birthday) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, email);
                pstmt.setString(4, password); // Consider hashing this
                pstmt.setString(5, gender);
                pstmt.setDate(6, Date.valueOf(birthday1));

                pstmt.executeUpdate();
                return true; // User saved successfully
            } catch (SQLException e) {
                e.printStackTrace();
                return false; // Failed to save user
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false; // No connection
    }
}

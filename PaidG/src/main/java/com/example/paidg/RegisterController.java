package com.example.paidg;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
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

/**
 * Controller for the Registration Page.
 * Handles user input, validation, and saving user data to the database.
 */
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
    private ToggleGroup genderToggleGroup;

    /**
     * Handles the Sign-Up button click event.
     * Validates input, saves user data, and navigates to the Sign-In page if successful.
     */
    @FXML
    protected void onSignUpButtonClick() {
        setupToggleGroup();

        // Validate inputs
        if (!areFieldsValid()) return;

        LocalDate birthday = birthdayPicker.getValue();
        String gender = getSelectedGender();

        // Validate age
        if (!isAgeValid(birthday)) return;

        // Save user to database
        if (saveUserToDatabase(
                firstNameField.getText(),
                lastNameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                gender,
                birthday
        )) {
            showAlert("Success", "Account created successfully!");
            navigateToSignInPage();
        } else {
            showAlert("Error", "Account creation failed.");
        }
    }

    /**
     * Sets up the toggle group for gender selection.
     */
    private void setupToggleGroup() {
        maleRadio.setToggleGroup(genderToggleGroup);
        othersRadio.setToggleGroup(genderToggleGroup);
        femaleRadio.setToggleGroup(genderToggleGroup);
    }

    /**
     * Validates that all required fields are filled.
     *
     * @return true if valid, false otherwise
     */
    private boolean areFieldsValid() {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()
                || emailField.getText().isEmpty() || passwordField.getText().isEmpty()
                || birthdayPicker.getValue() == null) {
            showAlert("Error", "All fields must be filled out, including your birthday.");
            return false;
        }
        return true;
    }

    /**
     * Gets the selected gender from the radio buttons.
     *
     * @return the selected gender or "Not Specified" if none selected
     */
    private String getSelectedGender() {
        RadioButton selectedRadioButton = (RadioButton) genderToggleGroup.getSelectedToggle();
        return selectedRadioButton != null ? selectedRadioButton.getText() : "Not Specified";
    }

    /**
     * Validates that the user's age is at least 8 years.
     *
     * @param birthday the user's birthday
     * @return true if valid, false otherwise
     */
    private boolean isAgeValid(LocalDate birthday) {
        int age = Period.between(birthday, LocalDate.now()).getYears();
        if (age < 8) {
            showAlert("Error", "You must be at least 8 years old to sign up.");
            return false;
        }
        return true;
    }

    /**
     * Saves the user data to the database.
     *
     * @return true if saved successfully, false otherwise
     */
    private boolean saveUserToDatabase(String firstName, String lastName, String email, String password, String gender, LocalDate birthday) {
        Connection connection = Database.connectDb();
        if (connection != null) {
            String sql = "INSERT INTO user (FirstName, LastName, Email, Password, Gender, Birthday) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, email);
                pstmt.setString(4, password); // Consider hashing the password for security
                pstmt.setString(5, gender);
                pstmt.setDate(6, Date.valueOf(birthday));

                pstmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Displays an alert dialog.
     *
     * @param title   the title of the alert
     * @param message the message to display
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Navigates to the Sign-In page.
     */
    private void navigateToSignInPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AccPage.fxml"));
            Pane signInPane = fxmlLoader.load();
            Stage stage = (Stage) signUpButton.getScene().getWindow();
            Scene scene = new Scene(signInPane);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the sign-in page.");
        }
    }
}

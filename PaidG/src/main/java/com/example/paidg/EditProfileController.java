package com.example.paidg;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EditProfileController implements Initializable {


    @FXML
    private TextField firstNameEditField;
    @FXML
    private TextField lastNameEditField;
    @FXML
    private TextField emailEditField;
    @FXML
    private PasswordField passwordEditField;
    @FXML
    private PasswordField confirmPasswordEditField;
    @FXML
    private RadioButton femaleRadioEdit;
    @FXML
    private RadioButton maleEditRadio;
    @FXML
    private RadioButton othersRadioEdit;
    @FXML
    private DatePicker birthdayPickerEdit;
    @FXML
    private Button SubmitEditButton;
    @FXML
    private ToggleGroup Edittoogle;

    @FXML
    protected void onSubmitEditButtonClick() {
        // Check if all fields are filled out
        if (firstNameEditField.getText().isEmpty() || lastNameEditField.getText().isEmpty() ||
                emailEditField.getText().isEmpty() || passwordEditField.getText().isEmpty() ||
                confirmPasswordEditField.getText().isEmpty() || birthdayPickerEdit.getValue() == null) {
            showAlert("Error", "All fields must be filled out.");
            return;
        }

        // Validate passwords
        if (!passwordEditField.getText().equals(confirmPasswordEditField.getText())) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        // Get the selected gender
        RadioButton selectedRadioButton = (RadioButton) Edittoogle.getSelectedToggle();
        String gender = selectedRadioButton != null ? selectedRadioButton.getText() : "Not Specified";

        // Get birthday
        LocalDate birthday = birthdayPickerEdit.getValue();

        // Update user in the database
        boolean isUpdated = updateUserInDatabase(firstNameEditField.getText(), lastNameEditField.getText(),
                emailEditField.getText(), passwordEditField.getText(), gender, birthday);

        if (isUpdated) {
            showAlert("Success", "Profile updated successfully!");
            // Optionally navigate back or reset fields
        } else {
            showAlert("Error", "Profile update failed.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean updateUserInDatabase(String firstName, String lastName, String email, String password, String gender, LocalDate birthday) {
        Connection connection = Database.connectDb();
        if (connection != null) {
            String sql = "UPDATE user SET FirstName = ?, LastName = ?, Email = ?, Password = ?, Gender = ?, Birthday = ?  WHERE UserID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, email);
                pstmt.setString(4, password); // Consider hashing this
                pstmt.setString(5, gender);
                pstmt.setDate(6, java.sql.Date.valueOf(birthday));
                pstmt.setString(7, email); // Assuming the email is unique and used to identify the user
                pstmt.setInt(8, AccPageController.user_id);
                pstmt.executeUpdate();
                return true; // User updated successfully
            } catch (SQLException e) {
                e.printStackTrace();
                return false; // Failed to update user
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

    public void initialize(URL url, ResourceBundle resourceBundle) {
        SubmitEditButton.setOnAction(event -> onSubmitEditButtonClick());
    }
}

package com.example.hospital;

import com.example.hospital.models.Report;
import com.example.hospital.models.User;
import com.example.hospital.utils.DBUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.Optional;

public class MedicalApp extends Application {

    DBUtils db = new DBUtils();
    User loggedInUser = null;



    private final TableView<Report> reportTableView = new TableView<>();
    private final TextArea detailsTextArea = new TextArea();

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();


        // Initialize columns for report table
        TableColumn<Report, String> reportColumn = new TableColumn<>("Report");
        reportColumn.setCellValueFactory(cellData -> cellData.getValue().reportTextProperty());
        reportTableView.getColumns().add(reportColumn);

        // Fetch reports from the database
        ObservableList<Report> reports = db.fetchReportsFromDatabase();

        reportTableView.setItems(reports);

        // Listen for selection changes and update details text
        reportTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showReportDetails(newValue));

        // Initialize the root layout
        VBox tableContainer = new VBox();
        tableContainer.getChildren().addAll(new Label("Reports"), reportTableView);

        Button newButton = new Button("New Report");
        newButton.setOnAction(e -> {
            if (loggedInUser != null) {
                showNewReportDialog();
            } else {
                showLoginDialog();
            }
        });



        Button loginButton = new Button("Authenticate");

        loginButton.setOnAction(e ->
                {
                    if (loggedInUser == null) {
                        showLoginDialog();
                    } else {
                        showLogoutDialog();
                    }
                });


        HBox buttonContainer = new HBox();
        buttonContainer.getChildren().addAll(newButton,loginButton);
        buttonContainer.setSpacing(10);
        buttonContainer.setPadding(new Insets(10));

        root.setLeft(tableContainer);
        root.setRight(detailsTextArea);
        root.setTop(buttonContainer);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Medical App");
        primaryStage.show();
    }




    // Method to display report details in the TextArea
    private void showReportDetails(Report report) {
        if (report != null) {
            StringBuilder details = new StringBuilder();
            details.append("Report Date: ").append(report.getReportDate()).append("\n");
            details.append("Report Text: ").append(report.getReportText()).append("\n");

            if (report.getPatient() != null) {
                User user = report.getPatient();
                details.append("\nPatient's Details:\n")
                        .append("Name: ").append(user.getName()).append("\n")
                        .append("Email: ").append(user.getEmail()).append("\n")
                        .append("Date of Birth: ").append(user.getDob()).append("\n")
                        .append("Phone: ").append(user.getPhone()).append("\n")
                        .append("Blood Group: ").append(user.getBloodGroup()).append("\n")
                        .append("Address: ").append(user.getAddress()).append("\n");
            }
            if (report.getDoctor() != null) {
                User user = report.getDoctor();
                details.append("\nDoctor's Details:\n")
                        .append("Name: ").append(user.getName()).append("\n")
                        .append("Email: ").append(user.getEmail()).append("\n")
                        .append("Date of Birth: ").append(user.getDob()).append("\n")
                        .append("Phone: ").append(user.getPhone()).append("\n")
                        .append("Blood Group: ").append(user.getBloodGroup()).append("\n")
                        .append("Address: ").append(user.getAddress()).append("\n");
            }
            detailsTextArea.setText(details.toString());
        } else {
            detailsTextArea.setText("");
        }
    }


    // Method to show a new report dialog
    private void showNewReportDialog() {
        Dialog<Report> dialog = new Dialog<>();
        dialog.setTitle("New Report");
        dialog.setHeaderText("Enter Report Details");

        // Set the button types
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        // Create the report text input field

        TextField patientEmailField = new TextField();
        patientEmailField.setPromptText("Patient Email");

        TextField patientPasswordField = new TextField();
        patientPasswordField.setPromptText("Patient Password");


        TextArea reportDescriptionField = new TextArea();
        reportDescriptionField.setPromptText("Report Description");



        // Enable/Disable submit button depending on whether a report text was entered
        Button submitButton = (Button) dialog.getDialogPane().lookupButton(submitButtonType);
        submitButton.setDisable(true);

        // Do some validation (using lambda for simplicity here)

        patientEmailField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(newValue.trim().isEmpty());
        });

        patientPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(newValue.trim().isEmpty());
        });

        reportDescriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(newValue.trim().isEmpty());
        });

        // Layout the dialog content
        VBox content = new VBox();
        content.getChildren().addAll(new Label("Patient Email:"),patientEmailField,new Label("Patient Password:"),patientPasswordField, new Label("Report Description:"),reportDescriptionField);
        content.setSpacing(10);
        content.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(content);

        // Request focus on the report text field by default
        Platform.runLater(patientEmailField::requestFocus);


        // Convert the result to a report object when the submit button is clicked
        dialog.setResultConverter(dialogButton -> {
            if(loggedInUser == null){return  null;}
            if (dialogButton == submitButtonType) {

                User patient = db.fetchUserFromDatabase(patientEmailField.getText(),patientPasswordField.getText());
                if(patient != null){
                    // Allow creating the report
                    return new Report(reportDescriptionField.getText(), new Date(System.currentTimeMillis()), patient, loggedInUser);
                }
                else{
                    // Deny creating the report
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login Error");
                    alert.setHeaderText("Invalid Credentials");
                    alert.setContentText("No valid patient is available against your provided email and password, Try again or try to create a patient first.");
                    alert.showAndWait();
                    return null;
                }

            }
            return null;
        });

        Optional<Report> result = dialog.showAndWait();
        if (result.isPresent()) {
            Report newReport = result.get();
            // Save the new report to the database
            db.saveReportToDatabase(newReport);
            // Add the new report to the table view
            reportTableView.getItems().add(newReport);
            }
        // If user cancelled the dialog

    }


    // Method to show a login dialog
    private void showLoginDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Login");
        dialog.setHeaderText("Doctor Login");

        // Set the button types
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the username and password input fields
        TextField eamilTextField = new TextField();
        eamilTextField.setPromptText("Username (Email)");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Enable/Disable login button depending on whether username and password were entered
        Button loginButton = (Button) dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using lambda for simplicity)
        eamilTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty() || passwordField.getText().trim().isEmpty());
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty() || eamilTextField.getText().trim().isEmpty());
        });

        // Layout the dialog content
        VBox content = new VBox();
        content.getChildren().addAll(new Label("Username:"), eamilTextField,
                new Label("Password:"), passwordField);
        content.setSpacing(10);
        content.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(content);

        // Request focus on the username field by default
        Platform.runLater(eamilTextField::requestFocus);

        // Validate the credentials when the login button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                // You can implement your own logic to validate the credentials against the database
                String email = eamilTextField.getText();
                String password = passwordField.getText();
                // Check if the username and password belong to a doctor
                boolean isValidDoctor = validateDoctorCredentials(email, password);
                if (isValidDoctor) {
                    // Allow login
                    return true;
                } else {
                    // Deny login
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login Error");
                    alert.setHeaderText("Invalid Credentials");
                    alert.setContentText("Only doctors are allowed to log in.");
                    alert.showAndWait();
                    return false;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showLogoutDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Logout");
        dialog.setHeaderText("Are you sure you want to logout?");
        ButtonType logoutButtonType = new ButtonType("Logout", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(logoutButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == logoutButtonType) {
                // Logout the user
                loggedInUser = null;
                return true;
            }
            return false;
        });

        dialog.showAndWait();
    }
    // Method to validate doctor credentials against the database
    private boolean validateDoctorCredentials(String email, String password) {
        // You need to implement the logic to validate the credentials against the database
        // For simplicity, let's assume there is a table named 'doctors' with columns 'email' and 'password'
        // where the doctor's email and password are stored.
        // You would execute a SQL query to check if the provided email and password match a record in the 'doctors' table.
        // If a match is found, return true; otherwise, return false.
        User user = db.fetchDoctorUserFromDatabase(email, password);
        if(user != null){
            loggedInUser = user;
            return true;
        }else {
            return false;
        }
         // Dummy implementation; replace this with your actual logic
    }


    public static void main(String[] args) {
        launch(args);
    }
}
package com.example.hospital;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.Optional;

public class MedicalApp extends Application {

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
        ObservableList<Report> reports = fetchReportsFromDatabase();

        reportTableView.setItems(reports);

        // Listen for selection changes and update details text
        reportTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showReportDetails(newValue));

        // Initialize the root layout
        VBox tableContainer = new VBox();
        tableContainer.getChildren().addAll(new Label("Reports"), reportTableView);

        Button newButton = new Button("New");
        newButton.setOnAction(e -> showNewReportDialog());

        HBox buttonContainer = new HBox();
        buttonContainer.getChildren().add(newButton);
        buttonContainer.setPadding(new Insets(10));

        root.setLeft(tableContainer);
        root.setRight(detailsTextArea);
        root.setTop(buttonContainer);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Medical App");
        primaryStage.show();
    }


    // Method to fetch reports from the database
    private ObservableList<Report> fetchReportsFromDatabase() {
        ObservableList<Report> reports = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/medifire",
                "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT report_text, report_date, users.* FROM report INNER JOIN users ON report.patient_id = users.id")) {

            while (rs.next()) {
                // Assuming Report and User classes have appropriate constructors
                Report report = new Report(rs.getString("report_text"), rs.getDate("report_date"));
                User user = new User(rs.getInt("id"), rs.getString("password"), rs.getString("type"),
                        rs.getString("name"), rs.getString("email"), rs.getDate("dob"),
                        rs.getString("phone"), rs.getString("blood_group"), rs.getString("address"));
//                System.out.println(rs.getString("name"));
                report.setUser(user);
                reports.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    private void saveReportToDatabase(Report report){
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/medifire",
                    "root", "");
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO report (report_text, report_date, patient_id, doctor_id) VALUES (?, ?, ?, ?)");
            stmt.setString(1, report.getReportText());
            stmt.setDate(2, new Date(report.getReportDate().getTime()));
            stmt.setInt(3, report.getPatientId());
            stmt.setInt(4, report.getDoctorId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Method to display report details in the TextArea
    private void showReportDetails(Report report) {
        if (report != null) {
            StringBuilder details = new StringBuilder();
            details.append("Report Text: ").append(report.getReportText()).append("\n");
            details.append("Report Date: ").append(report.getReportDate()).append("\n");
            if (report.getUser() != null) {
                User user = report.getUser();
                details.append("\nDetails:\n")
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

        TextField patientIdField = new TextField();
        patientIdField.setPromptText("Patient ID");
        TextField doctorIdField = new TextField();
        doctorIdField.setPromptText("Doctor ID");
        TextArea reportDescriptionField = new TextArea();
        reportDescriptionField.setPromptText("Report Description");



        // Enable/Disable submit button depending on whether a report text was entered
        Button submitButton = (Button) dialog.getDialogPane().lookupButton(submitButtonType);
        submitButton.setDisable(true);

        // Do some validation (using lambda for simplicity here)

        patientIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(newValue.trim().isEmpty());
        });

        doctorIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(newValue.trim().isEmpty());
        });

        reportDescriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(newValue.trim().isEmpty());
        });

        // Layout the dialog content
        VBox content = new VBox();
        content.getChildren().addAll(new Label("Patient ID:"),patientIdField,new Label("Doctor ID:"),doctorIdField,new Label("Report Description:"),reportDescriptionField);
        content.setSpacing(10);
        content.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(content);

        // Request focus on the report text field by default
        Platform.runLater(patientIdField::requestFocus);


        // Convert the result to a report object when the submit button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                Report report = new Report(reportDescriptionField.getText(), new Date(System.currentTimeMillis()));
                report.setDoctorId(Integer.parseInt(doctorIdField.getText()));
                report.setPatientId(Integer.parseInt(patientIdField.getText()));
                return report;}
            return null;
        });

        Optional<Report> result = dialog.showAndWait();
        if (result.isPresent()) {
            Report newReport = result.get();
            // Save the new report to the database
            saveReportToDatabase(newReport);
            // Add the new report to the table view
            reportTableView.getItems().add(newReport);
            }
        // If user cancelled the dialog
    }

    // Sample Report class
    public static class Report {
        private int doctorId;
        private int patientId;

        public int getDoctorId() {
            return doctorId;
        }

        public void setDoctorId(int doctorId) {
            this.doctorId = doctorId;
        }

        public int getPatientId() {
            return patientId;
        }

        public void setPatientId(int patientId) {
            this.patientId = patientId;
        }

        private final String reportText;
        private final Date reportDate;
        private User user;

        public Report(String reportText, Date reportDate) {
            this.reportText = reportText;
            this.reportDate = reportDate;
        }

        public String getReportText() {
            return reportText;
        }

        public Date getReportDate() {
            return reportDate;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }


        public ObservableValue<String> reportTextProperty() {
            return new SimpleStringProperty(reportText);
        }
    }

    // Sample User class
    public static class User {
        private  int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Date getDob() {
            return dob;
        }

        public void setDob(Date dob) {
            this.dob = dob;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getBloodGroup() {
            return bloodGroup;
        }

        public void setBloodGroup(String bloodGroup) {
            this.bloodGroup = bloodGroup;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        private  String password;

        private  String type;
        private  String name;
        private  String email;
        private Date dob;
        private  String phone;
        private String bloodGroup;
        private String address;

        public User(int id, String password, String type, String name, String email, Date dob, String phone, String bloodGroup, String address) {
            this.id = id;
            this.password = password;
            this.type = type;
            this.name = name;
            this.email = email;
            this.dob = dob;
            this.phone = phone;
            this.bloodGroup = bloodGroup;
            this.address = address;
        }
        // Define user properties and constructor here
    }

    public static void main(String[] args) {
        launch(args);
    }
}
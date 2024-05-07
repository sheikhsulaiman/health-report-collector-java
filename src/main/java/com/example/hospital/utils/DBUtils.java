package com.example.hospital.utils;

import com.example.hospital.models.Report;
import com.example.hospital.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DBUtils {
    public void saveReportToDatabase(Report report){
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

    // Method to fetch reports from the database
    public ObservableList<Report> fetchReportsFromDatabase() {
        ObservableList<Report> reports = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/medifire",
                "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT r.report_id,r.report_text,r.report_date,p.id AS patient_id,p.name AS patient_name,p.email AS patient_email,p.phone AS patient_phone,p.address AS patient_address,p.dob AS patient_dob,p.blood_group AS patient_blood_group,d.id AS doctor_id,d.name AS doctor_name,d.email AS doctor_email,d.phone AS doctor_phone,d.address AS doctor_address,d.dob AS doctor_dob,d.blood_group AS doctor_blood_group FROM report r JOIN users p ON r.patient_id = p.id JOIN users d ON r.doctor_id = d.id")) {

            while (rs.next()) {
                // Assuming Report and User classes have appropriate constructors
                Report report = new Report(rs.getString("report_text"), rs.getDate("report_date"));
                report.setReportId(rs.getInt("report_id"));
                User doctor = new User(
                        rs.getInt("doctor_id"),
                        rs.getString("doctor_name"),
                        rs.getString("doctor_email"),
                        rs.getString("doctor_phone"),
                        rs.getString("doctor_address"),
                        rs.getDate("doctor_dob"),
                        rs.getString("doctor_blood_group")

                );
                User patient = new User(
                        rs.getInt("patient_id"),
                        rs.getString("patient_name"),
                        rs.getString("patient_email"),
                        rs.getString("patient_phone"),
                        rs.getString("patient_address"),
                        rs.getDate("patient_dob"),
                        rs.getString("patient_blood_group")
                );
                report.setPatient(patient);
                report.setDoctor(doctor);
                reports.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public void saveUserToDatabase(User user) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/medifire",
                    "root", "");
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (name, email, password, dob, phone, blood_group, address, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setDate(4, new Date(user.getDob().getTime()));
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getBloodGroup());
            stmt.setString(7, user.getAddress());
            stmt.setString(8, user.getType());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User fetchUserFromDatabase(String email, String password) {
        User user = null;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/medifire",
                "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?")) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getDate("dob"),
                        rs.getString("blood_group"),
                        rs.getString("type")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User fetchDoctorUserFromDatabase(String email, String password) {
        User user = null;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/medifire",
                "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ? AND type = 'doctor'")) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getDate("dob"),
                        rs.getString("blood_group"),
                        rs.getString("type")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}

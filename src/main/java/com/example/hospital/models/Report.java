package com.example.hospital.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

import java.sql.Date;

public class Report {
    private int reportId;

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getReportId() {
        return reportId;
    }

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
    private User patient;
    private User doctor;

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public Report(int reportId,String reportText, Date reportDate) {
        this.reportId = reportId;
        this.reportText = reportText;
        this.reportDate = reportDate;
    }

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

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }


    public ObservableValue<String> reportTextProperty() {
        return new SimpleStringProperty(reportText);
    }


}


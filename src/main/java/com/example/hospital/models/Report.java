package com.example.hospital.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

import java.sql.Date;

public class Report {
    private int reportId;
    private User patient;
    private User doctor;
    private final String reportText;
    private final Date reportDate;

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getReportId() {
        return reportId;
    }

    public int getDoctorId() {
        return doctor.getId();
    }

      public int getPatientId() {
            return patient.getId();
        }



    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public Report(int reportId,String reportText, Date reportDate,User patient, User doctor) {
        this.reportId = reportId;
        this.reportText = reportText;
        this.reportDate = reportDate;
        this.patient = patient;
        this.doctor = doctor;
    }

    public Report(String reportText, Date reportDate , User patient, User doctor) {

        this.reportText = reportText;
        this.reportDate = reportDate;
        this.patient = patient;
        this.doctor = doctor;
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


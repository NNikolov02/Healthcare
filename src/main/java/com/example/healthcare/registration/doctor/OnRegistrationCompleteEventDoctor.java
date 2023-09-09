package com.example.healthcare.registration.doctor;

import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import org.springframework.context.ApplicationEvent;

import javax.print.Doc;
import java.util.Locale;

public class OnRegistrationCompleteEventDoctor extends ApplicationEvent {

    private String appUrl;
    private Locale locale;
    private Doctor doctor;


    public OnRegistrationCompleteEventDoctor(Doctor doctor, Locale locale, String appUrl) {
        super(doctor);
        this.doctor = doctor;
        this.locale = locale;
        this.appUrl = appUrl;

    }


    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}
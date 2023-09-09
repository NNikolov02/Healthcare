package com.example.healthcare.registration.appointment;

import com.example.healthcare.model.Appointment;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

public class OnRegistrationCompleteEventAppDoc extends ApplicationEvent {

    private String appUrl;
    private Locale locale;
    private Appointment appointment;


    public OnRegistrationCompleteEventAppDoc(Appointment appointment, Locale locale, String appUrl) {
        super(appointment);
        this.appointment = appointment;
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

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }
}
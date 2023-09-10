package com.example.healthcare.registration.customer;

import com.example.healthcare.model.Customer;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

public class OnDoctorCompleteEventCustomerAccept extends ApplicationEvent {

    private String appUrl;
    private Locale locale;
    private Customer customer;


    public OnDoctorCompleteEventCustomerAccept(Customer customer, Locale locale, String appUrl) {
        super(customer);
        this.customer = customer;
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
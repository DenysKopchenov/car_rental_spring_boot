package com.dkop.car.rental.service;

public interface MailService {

    void sendEmail(String userEmail, String subject, String text);
}

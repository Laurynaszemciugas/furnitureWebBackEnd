package com.example.jwt_demo.controller;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderContoller {


    @Autowired
    private JavaMailSender javaMailSender;


   public void welcomeMessage(String setTo){

       SimpleMailMessage message = new SimpleMailMessage();
       message.setFrom("laurynaszemciugas@gmail.com");

       message.setTo(setTo);
       message.setText("Welcome to Furniture management system");
       message.setSubject("You are all set you can go and use the system");


//       javaMailSender.send(message);

   }



    public void stockWarning(String setTo, String messageToClient, Long count) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true,
                    "UTF-8"
            );

            helper.setFrom("laurynaszemciugas@gmail.com");
            helper.setTo(setTo);
            helper.setSubject(
                    "⚠ " + count + " orders need attention"
            );

            helper.setText(messageToClient, true); // true = HTML

            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send warning email", e);
        }
    }



    public void verificationGmail(String setTo , String code){

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("laurynaszemciugas@gmail.com");

        message.setTo(setTo);
        message.setText("Please verify your account visit " + "http://10.195.17.135:9999/GmailVerification/" + code);
        message.setSubject("Gmail verification");


        javaMailSender.send(message);

    }




}

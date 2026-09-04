package com.example.jwt_demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderContoller {


    @Autowired
    private JavaMailSender javaMailSender;


   public void welcomeMessage(){

       SimpleMailMessage message = new SimpleMailMessage();
       message.setFrom("laurynaszemciugas@gmail.com");

       message.setTo("laurynas.zem426@go.kauko.lt");
       message.setText("YO SUP");
       message.setSubject("SUP");


       javaMailSender.send(message);

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

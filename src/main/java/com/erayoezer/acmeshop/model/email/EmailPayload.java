package com.erayoezer.acmeshop.model.email;

public class EmailPayload {
    private From from;
    private Recipient[] to;
    private String subject;
    private String text;

    public EmailPayload(From from, Recipient[] to, String subject, String text) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.text = text;
    }

    public From getFrom() {
        return from;
    }

    public void setFrom(From from) {
        this.from = from;
    }

    public Recipient[] getTo() {
        return to;
    }

    public void setTo(Recipient[] to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}


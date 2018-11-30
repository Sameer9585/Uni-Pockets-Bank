package com.ibrickedlabs.mpokket.Data;

public class Mpokket {
    private String studentName;
    private String studentRegesitrationNumber;
    private String stundentImageUrl;
    private String amountBorrowed;
    private String amountRequested;
    private String repayAfter;
    private String dateAndtime;
    private boolean isAccepted;
    private  String userId;

    public Mpokket(String studentName, String studentRegesitrationNumber, String stundentImageUrl, String amountBorrowed, String amountRequested, String repayAfter, String dateAndtime,boolean isAccepted,String userId) {
        this.studentName = studentName;
        this.studentRegesitrationNumber = studentRegesitrationNumber;
        this.stundentImageUrl = stundentImageUrl;
        this.amountBorrowed = amountBorrowed;
        this.amountRequested = amountRequested;
        this.repayAfter = repayAfter;
        this.dateAndtime = dateAndtime;
        this.isAccepted=isAccepted;
        this.userId=userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public String getStudentRegesitrationNumber() {
        return studentRegesitrationNumber;
    }

    public void setStudentRegesitrationNumber(String studentRegesitrationNumber) {
        this.studentRegesitrationNumber = studentRegesitrationNumber;
    }

    public Mpokket() {
    }

    public Mpokket(String amountBorrowed, String amountRequested, String repayAfter, String dateAndtime) {
        this.amountBorrowed = amountBorrowed;
        this.amountRequested = amountRequested;
        this.repayAfter = repayAfter;
        this.dateAndtime = dateAndtime;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStundentImageUrl() {
        return stundentImageUrl;
    }

    public void setStundentImageUrl(String stundentImageUrl) {
        this.stundentImageUrl = stundentImageUrl;
    }


    public String getAmountBorrowed() {
        return amountBorrowed;
    }

    public void setAmountBorrowed(String amountBorrowed) {
        this.amountBorrowed = amountBorrowed;
    }

    public String getAmountRequested() {
        return amountRequested;
    }

    public void setAmountRequested(String amountRequested) {
        this.amountRequested = amountRequested;
    }

    public String getRepayAfter() {
        return repayAfter;
    }

    public void setRepayAfter(String repayAfter) {
        this.repayAfter = repayAfter;
    }

    public String getDateAndtime() {
        return dateAndtime;
    }

    public void setDateAndtime(String dateAndtime) {
        this.dateAndtime = dateAndtime;
    }
}

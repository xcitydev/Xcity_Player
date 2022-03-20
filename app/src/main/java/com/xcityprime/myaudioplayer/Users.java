package com.xcityprime.myaudioplayer;

public class Users {
    private String email;
    private String password;
    private String uniqueNumber;
    private String bttEarned;
    private String referralId;

    public Users(){}

    public Users(String email, String password, String uniqueNumber, String bttEarned, String referralId) {
        this.email = email;
        this.password = password;
        this.uniqueNumber = uniqueNumber;
        this.bttEarned = bttEarned;
        this.referralId = referralId;
    }

    public String getReferralId() {
        return referralId;
    }

    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUniqueNumber() {
        return uniqueNumber;
    }

    public void setUniqueNumber(String uniqueNumber) {
        this.uniqueNumber = uniqueNumber;
    }

    public String getBttEarned() {
        return bttEarned;
    }

    public void setBttEarned(String bttEarned) {
        this.bttEarned = bttEarned;
    }
}

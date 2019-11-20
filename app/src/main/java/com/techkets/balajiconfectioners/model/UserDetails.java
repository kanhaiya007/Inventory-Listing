package com.techkets.balajiconfectioners.model;

public class UserDetails {
    private String userEmail;
    private String userName;
    private String userPassword;
    private long userMobileNo;
    private String userAddress;
    private String userId;
    private String userType;

    public UserDetails(String userEmail, String userName, String userPassword, long userMobileNo, String userAddress,String userId,String userType) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userMobileNo = userMobileNo;
        this.userAddress = userAddress;
        this.userId=userId;
        this.userType=userType;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserDetails() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public long getUserMobileNo() {
        return userMobileNo;
    }

    public void setUserMobileNo(long userMobileNo) {
        this.userMobileNo = userMobileNo;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "userEmail='" + userEmail + '\'' +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userMobileNo=" + userMobileNo +
                ", userAddress='" + userAddress + '\'' +
                ", userId='" + userId + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}

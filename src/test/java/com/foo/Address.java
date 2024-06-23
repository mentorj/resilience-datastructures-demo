package com.foo;

/**
 * <p>
 * simple class storing address details
 * used in tests to show some Pattern Matching features (destructuring)
 * </p>
 */
public class Address {
    private String emailAddress;
    private String phoneNumber;
    private String zipCode;
    private String town;
    private String country;

    public Address() {
    }

    public Address( String town, String country) {
        this.town = town;
        this.country = country;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}

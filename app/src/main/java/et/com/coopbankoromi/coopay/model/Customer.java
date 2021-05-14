package et.com.coopbankoromi.coopay.model;

import java.util.Date;

public class Customer {

    private int id;
    private Date dob;
    private String firstname, middlename, phone, lastname, password, status, email, username, gender;

    public Customer(int id, Date dob, String firstname, String middlename, String phone, String lastname, String password, String status, String email, String username, String gender) {
        this.id = id;
        this.dob = dob;
        this.firstname = firstname;
        this.middlename = middlename;
        this.phone = phone;
        this.lastname = lastname;
        this.password = password;
        this.status = status;
        this.email = email;
        this.username = username;
        this.gender = gender;
    }

    public Customer(int id, String username, String email) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.gender = gender;
    }

    public Customer() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


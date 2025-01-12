package com.hack.user.interfaces;

public class UserDto {

    private String email;
    private String password;
    
    // Getters e Setters
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

    // Construtor
    public UserDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserDto() {
    }    
}

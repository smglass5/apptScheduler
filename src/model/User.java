/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Shannon
 */
public class User {

    private int userId;
    private String userName;
    private String password;
    private int active;

    public User(int userId, String userName, String password, int active) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.active = active;
    }

    public User() {        
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public int getActive() {
        return active;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActive(int active) {
        this.active = active;
    }    
}

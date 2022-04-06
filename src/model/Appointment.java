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
public class Appointment {
    
    private int appointmentId;
    private int customerId; 
    private String customerName;
    private int userId;
    private String contact;
    private String type;    
    private String start;
    private String end;
    
    public Appointment(int appointmentId,  String customerName, String contact, String type, String start, String end) {
        this.customerName = customerName;
        this.appointmentId = appointmentId;
        this.contact = contact;
        this.type = type;
        this.start = start;
        this.end = end;       
    }
    
    public Appointment() {        
    }
    
    public int getAppointmentId() {
        return appointmentId;
    }

    public int getCustomerId() {
        return customerId;
    }
   
    public int getUserId() {
        return userId;
    }

    public String getContact() {
        return contact;
    }
          
    public String getType() {
        return type;
    }
    
    public String getStart() {
        return start;
    }
    
    public String getEnd() {
        return end;
    }
    
    public String getCustomerName() {
        return customerName;
    }     
    
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
   
    public void setType(String type) {
        this.type = type;
    }
    
    public void setStart(String start) {
        this.start =start;
    }
    
    public void setEnd(String end) {
        this.end = end;
    }    
    
}



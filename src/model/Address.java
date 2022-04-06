/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Shannon Glass
 */
public class Address {
    
    public int addressId;
    private String address;
    private int cityId;
    private String postalCode;
    private String phone;

    
    public Address (int addressId, String address, int cityId, String postalCode, String phone) {
        this.addressId = addressId;
        this.address = address;
        this.cityId = cityId;
        this.postalCode = postalCode;
        this.phone = phone;
    }

    public Address() {      
    }

    public int getAddressId() {
        return addressId;
    }

    public String getAddress() {
        return address;
    }

    public int getCityId() {
        return cityId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
            
}

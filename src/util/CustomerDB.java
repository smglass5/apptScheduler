/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.City;
import model.Customer;

/**
 *
 * @author Shannon Glass
 */
public class CustomerDB {
    
     private static final Connection conn = DBConnection.getConnection();
    private static PreparedStatement ps;
    private static ResultSet rs;

    private static final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();    
    private static final ObservableList<City> cityList = FXCollections.observableArrayList();
    

    // Retrieves all customers and their associated data from database
    public static ObservableList<Customer> getAllCustomers() {
        allCustomers.clear();
      
        try {
            String selectCustomer = "SELECT customer.*, address.*, city.*, country.* FROM customer JOIN "
                    + "address ON customer.addressId = address.addressId"
                    + " JOIN city ON address.cityId = city.cityId "
                    + " JOIN country ON city.countryId = country.countryId";
            ps = conn.prepareStatement(selectCustomer);
            rs = ps.executeQuery();

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customerId"));
                customer.setCustomerName(rs.getString("customerName"));
                customer.setAddress(rs.getString("address"));
                customer.setCity(rs.getString("city"));
                customer.setCountry(rs.getString("country"));
                customer.setPostalCode(rs.getString("postalCode"));
                customer.setPhone(rs.getString("phone"));
                allCustomers.add(customer);
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return allCustomers;
    }

    // Retrieves customer names from database. Used to populate combo box
    public static ObservableList<Customer> getCustomers() {
        ObservableList<Customer> customers = FXCollections.observableArrayList();      

        try {
            String selectCustomer = "SELECT customerName FROM customer";
            ps = conn.prepareStatement(selectCustomer);
            rs = ps.executeQuery();

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerName(rs.getString("customerName"));
                customers.add(customer);
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return customers;
    }

    // Retrieves cities from database. Used to populate combo boxs and associated country fields
    public static ObservableList<City> getAllCities() {

        try {
            String selectCity = "SELECT cityId, city FROM city";
            ps = conn.prepareStatement(selectCity);
            rs = ps.executeQuery();

            while (rs.next()) {
                City city = new City();
                city.setCityId(rs.getInt("cityId"));
                city.setCity(rs.getString("city"));
                cityList.add(city);
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return cityList;
    }

    // Updates customer and associated address in database
    public static void updateCustomer(int id, String customerName, String address, int cityId, String postalCode, String phone) {       

        try {
            String updateCustomer = "UPDATE customer SET customerName = ? WHERE  customerId = ?";
            ps = conn.prepareStatement(updateCustomer);

            ps.setString(1, customerName);
            ps.setInt(2, id);
            ps.execute();

            String updateAddress = "UPDATE address SET address = ?, cityId = ?,  postalCode = ?, phone = ? WHERE addressId = ?";
            ps = conn.prepareStatement(updateAddress);

            ps.setString(1, address);
            ps.setInt(2, cityId);
            ps.setString(3, postalCode);
            ps.setString(4, phone);
            ps.setInt(5, id);
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Saves new customer and address to database
    public static void saveCustomer(String customerName, String address, int cityId, String postalCode, String phone) {
        int addressId = getAddressMaxId();
        int customerId = getCustomerMaxId();
       
        try {
            String insertAddress = "INSERT INTO address(addressId, address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy)"
                    + "VALUES(?, ?, ' ', ?, ?, ?, NOW(), 'test', NOW(), 'test') ";
            ps = conn.prepareStatement(insertAddress);
         
            ps.setInt(1, addressId);
            ps.setString(2, address);
            ps.setInt(3, cityId);
            ps.setString(4, postalCode);
            ps.setString(5, phone);
            ps.execute();

            String insertCustomer = "INSERT INTO customer (customerId, customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy)" //customerId, 
                    + " VALUES (?, ?, ?, ?, NOW(), 'test', NOW(), 'test')";
            ps = conn.prepareStatement(insertCustomer);

           ps.setInt(1, customerId);
            ps.setString(2, customerName);
            ps.setInt(3, addressId);
            ps.setInt(4, 1);
            ps.execute();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    // Deletes customer data from appointment and customer tables, also deletes associated address from address table in database
    public static void deleteCustomer(Customer customer) { 

        try {
            String deleteAppointment = "DELETE FROM appointment WHERE customerId = ?";
            ps = conn.prepareStatement(deleteAppointment);
            ps.setInt(1, customer.getCustomerId());
            ps.execute();  
            
            String deleteCustomer = "DELETE FROM customer WHERE customerId = ?";
            ps = conn.prepareStatement(deleteCustomer);
            ps.setInt(1, customer.getCustomerId());
            ps.execute();
            
            String deleteAddress = "DELETE FROM address WHERE address = ?";
            ps = conn.prepareStatement(deleteAddress);
            ps.setString(1, customer.getAddress());
            ps.execute();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
 
    // Finds the maximum existing customerId for adding new customer with new id
    private static int getCustomerMaxId() {        
        int customerMaxId = 0;
        
        try {
            String selectMax = "SELECT MAX(customerId) FROM customer";
            ps = conn.prepareStatement(selectMax);
            rs = ps.executeQuery();
            
            if(rs.next()) {
                customerMaxId = rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
      return customerMaxId + 1;  
    }
 
    // Finds the maximum existing addressId for adding new address with new id
    private static int getAddressMaxId() {       
        int addressMaxId = 0;
        
        try {
            String selectMax = "SELECT MAX(addressId) FROM address";
            ps = conn.prepareStatement(selectMax);
            rs = ps.executeQuery();
            
            if(rs.next()) {
                addressMaxId = rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
      return addressMaxId + 1;  
    }
}

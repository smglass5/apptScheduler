/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.CustomerDB;
import util.FormException;

/**
 *
 * @author Shannon Glass
 */
public class AddCustomerController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private ComboBox<String> cityCombo;
    @FXML
    private TextField countryField;
    @FXML
    private TextField postalCodeField;
    @FXML
    private TextField phoneField;

    Stage stage;
    Parent scene;
    Alert alert;

    private final ObservableList<String> cityList = FXCollections.observableArrayList("New York", "Los Angeles", "Toronto", "Vancouver", "Oslo");
    
    // Initializes AddCustomer screen and sets the city combo box values
     public void initialize() throws SQLException {
        cityCombo.setItems(cityList);
    }

     // Returns user to CustomerScreen with confirmation alert
    @FXML
    public void handleCancel(ActionEvent event) throws IOException {
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel");
        alert.setHeaderText("Confirmation Needed!");
        alert.setContentText("Are you sure you want to cancel adding a new customer?");
        alert.showAndWait();

        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/Customer.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    // Saves new customer and address to database
    @FXML
    public void handleSave(ActionEvent event) {
        String customerName = nameField.getText();
        String address = addressField.getText();
        int cityId = (cityCombo.getSelectionModel().getSelectedIndex() + 1);
        String postalCode = postalCodeField.getText();
        String phone = phoneField.getText();

        try {
            if (validInput()) {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Save");
                alert.setContentText("Are you ready to save this customer?");
                alert.showAndWait().ifPresent((response -> {
                    if (response == ButtonType.OK) {

                        try {
                            CustomerDB.saveCustomer(customerName, address, cityId, postalCode, phone);
                            CustomerDB.getAllCustomers();
                            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                            scene = FXMLLoader.load(getClass().getResource("/view/Customer.fxml"));
                            stage.setScene(new Scene(scene));
                            stage.show();
                        } catch (IOException ex) {
                            Logger.getLogger(AddCustomerController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }));
            }
        } catch (FormException e) {
            Alert exAlert = new Alert(Alert.AlertType.ERROR);
            exAlert.setTitle("Exception");
            exAlert.setHeaderText("There was an exception!");
            exAlert.setContentText(e.getMessage());
            exAlert.showAndWait().filter(response -> response == ButtonType.OK);
        }
    }

    // Sets the country based on the selected city 
    @FXML
    public void setCountry() {
        String selectedCity = cityCombo.getSelectionModel().getSelectedItem();
        switch (selectedCity) {
            case "New York":
            case "Los Angeles":
                countryField.setText("US");
                break;
            case "Toronto":
            case "Vancouver":
                countryField.setText("Canada");
                break;
            default:
                countryField.setText("Norway");
                break;
        }
    }

    // Validate user input with custom exception
    public boolean validInput() throws FormException {
        if (nameField.getText().isEmpty()) {
            throw new FormException("A customer must be entered!");
        }
        if (addressField.getText().isEmpty()) {
            throw new FormException("An address must be entered!");
        }
        if (cityCombo.getValue() == null) {
            throw new FormException("A city must be chosen!");
        }
        if (postalCodeField.getText().isEmpty()) {
            throw new FormException("A postal code must be entered!");
            } else if (postalCodeField.getText().length() > 5 || postalCodeField.getText().length() < 5) {
                throw new FormException("The postal code must be 5 digits!");
        }
        if (phoneField.getText().isEmpty()) {
            throw new FormException("A phone number must be entered!");
             } else if (phoneField.getText().length() > 8 || phoneField.getText().length() < 8) {
                throw new FormException("The phone number pattern must be XXX-XXXX !");
        }
        return true;
    }
}

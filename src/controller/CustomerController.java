/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.sql.SQLException;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Address;
import model.Customer;
import util.CustomerDB;
import util.FormException;

/**
 *
 * @author Shannon Glass
 */
public class CustomerController {

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> idColumn;
    @FXML
    private TableColumn<Customer, String> nameColumn;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private ComboBox cityCombo;
    @FXML
    private TextField countryField;
    @FXML
    private TextField postalCodeField;
    @FXML
    private TextField phoneField;
    @FXML
    private TableColumn<Customer, String> addressColumn;
    @FXML
    private TableColumn<Customer, String> cityColumn;
    @FXML
    private TableColumn<Customer, String> countryColumn;
    @FXML
    private TableColumn<Customer, String> postalCodeColumn;
    @FXML
    private TableColumn<Customer, String> phoneColumn;
    @FXML

    Stage stage;
    Parent scene;
    Alert alert;

    Customer selectedCustomer;
    Address selectedAddress;
    int customerId;
    int addressId;

    private final ObservableList<String> cityList = FXCollections.observableArrayList("New York", "Los Angeles", "Toronto", "Vancouver", "Oslo");

    // Initializes Customer scene and sets the table and combo box values
    public void initialize() throws SQLException {
        setTableValues();
        cityCombo.setItems(cityList);
        customerTable.setItems(CustomerDB.getAllCustomers());
    }

    // Sets customer table values
    public void setTableValues() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    // Sets field values of selected customer for updating
    public void setCustomer() {
        selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        customerId = selectedCustomer.getCustomerId();
        addressId = selectedCustomer.getAddressId();
        nameField.setText(selectedCustomer.getCustomerName());
        addressField.setText(selectedCustomer.getAddress());
        cityCombo.getSelectionModel().select(selectedCustomer.getCity());
        setCountry();
        countryField.setText(selectedCustomer.getCountry());
        postalCodeField.setText(selectedCustomer.getPostalCode());
        phoneField.setText(selectedCustomer.getPhone());
    }

    // Sets the country based on the selected city 
    @FXML
    public void setCountry() {
        String selectedCity = cityCombo.getSelectionModel().getSelectedItem().toString();
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

    // Returns user to MainMenu scene
    @FXML
    public void handleBack(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    // Deletes a selected customer and associated address from database
    @FXML
    public void handleDelete(ActionEvent event) {
        if (customerTable.getSelectionModel().getSelectedItem() != null) {
            selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Alert");
            alert.setHeaderText("Delete");
            alert.setContentText("Are you sure you want to delete this customer?");
            alert.showAndWait().ifPresent((response -> {
                if (response == ButtonType.OK) {
                    CustomerDB.deleteCustomer(selectedCustomer);
                    customerTable.setItems(CustomerDB.getAllCustomers());
                }
            }));
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No selection!");
            alert.setContentText("A customer must be selected!");
            alert.showAndWait();
        }
    }

    // Opens AddCustomer scene
    @FXML
    public void handleNew(ActionEvent event) throws IOException {
        Stage addStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/AddCustomer.fxml"));
        addStage.setScene(new Scene(scene));
        addStage.show();
    }

    // Sends selected customer data to form for updating
    @FXML
    public void handleUpdate(ActionEvent event) {
        if (customerTable.getSelectionModel().getSelectedItem() != null) {
            setCustomer();
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No selection!");
            alert.setContentText("A customer must be selected!");
            alert.showAndWait();
        }
    }

    // Saves updated customer data to database
    @FXML
    public void handleSave(ActionEvent event) {
        customerId = selectedCustomer.getCustomerId();
        addressId = selectedCustomer.getAddressId();
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
                        CustomerDB.updateCustomer(customerId, customerName, address, cityId, postalCode, phone);
                        customerTable.setItems(CustomerDB.getAllCustomers());
                    }
                }));
            }
        } catch (FormException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception");
            alert.setHeaderText("There was an exception!");
            alert.setContentText(e.getMessage());
            alert.showAndWait().filter(response -> response == ButtonType.OK);
        }
    }

    // Validates user input with custom exception
    public boolean validInput() throws FormException {
        String selectedCity = cityCombo.getSelectionModel().getSelectedItem().toString();
        if (nameField.getText().isEmpty()) {
            throw new FormException("Customer field cannot be empty!");
        }
        if (addressField.getText().isEmpty()) {
            throw new FormException("An address must be entered!");
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 *
 * @author Shannon Glass
 */
public class MainMenuController {

    Stage stage;
    Parent scene;
    Alert alert;
    

    // Opens Appointment scene
    @FXML
    void handleAppointment(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/Appointment.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    // Opens Customer scene
    @FXML
    public void handleCustomer(ActionEvent event) throws IOException {        
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/Customer.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    // Opens Report scene
    @FXML
    public void handleReport(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/Report.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }
    
    // Closes the program with confirmation
     @FXML
    public void handleExit(ActionEvent event) {        
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alert");
        alert.setHeaderText("Confirm");
        alert.setContentText("Are you sure you want to exit the program?");         
        alert.showAndWait().ifPresent((response -> { //Lambda expression to handle alert for cancelling logging in and exiting program   
            if (response == ButtonType.OK) {
                System.exit(0);
            }
        }));
    }
    
    public void initialize(URL url, ResourceBundle rb) {       
    }

}
    
    

 

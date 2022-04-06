/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.User;
import util.AppointmentDB;
import util.DBConnection;
import util.Logger;

/**
 * FXML Controller class
 *
 * @author Shannon Glass
 */
public class LoginController implements Initializable {

    @FXML
    private TextField userNameField;
    @FXML
    private Label userNameLabel;
    @FXML
    private TextField passwordField;
    @FXML
    private Label passwordLabel;
    @FXML
    private Button cancelButton;
    @FXML
    private Button loginButton;

    public static User loggedIn = new User();
    ResourceBundle rb;
    Stage stage;
    Parent scene;
    Connection conn;
    PreparedStatement ps;
    ResultSet rs;
    Alert alert;

    public LoginController() {
        conn = DBConnection.getConnection();
    }

    // Initializes Log-in screen with language based on user's location in French or English
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
        System.out.println(Locale.getDefault());

        userNameLabel.setText(rb.getString("Username"));
        passwordLabel.setText(rb.getString("Password"));
        loginButton.setText(rb.getString("Login"));
        cancelButton.setText(rb.getString("Cancel"));
    }

    // Cancels log-in and exits program
    @FXML
    void handleCancel(ActionEvent event) {
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(rb.getString("confirmTitle"));
        alert.setHeaderText(rb.getString("confirmHeader"));
        alert.setContentText(rb.getString("confirmText"));
        alert.showAndWait().ifPresent((response -> { //Lambda expression to handle alert when cancelling log-in, exits program efficiently 
            if (response == ButtonType.OK) {
                System.exit(0);
            }
        }));
    }

    // Checks if the entered username and password exists and is valid
    @FXML
    public void handleLogin(ActionEvent event) throws SQLException, IOException {
        String userName = userNameField.getText();
        String password = passwordField.getText();
        String selectStatement = "SELECT * FROM user WHERE userName = ? AND password = ?";
        ps = conn.prepareStatement(selectStatement);
        ps.setString(1, userName);
        ps.setString(2, password);
        rs = ps.executeQuery();
        if (rs.next()) {
            appointmentAlert();
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
            Logger.logger(userName, true);
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("errorTitle"));
            alert.setContentText(rb.getString("errorText"));
            alert.showAndWait();
            Logger.logger(userName, false);
        }
    }

    // Alerts user when an upcoming appointment is within 15 minutes of log-in
    public void appointmentAlert() {
        try {
            ObservableList<Appointment> alerts = AppointmentDB.getAlertAppointment();
            if (alerts != null) {

                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("informationTitle"));
                alert.setHeaderText(rb.getString("informationHeader"));
                alert.setContentText(rb.getString("informationText"));
                alert.showAndWait();
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
    }

}

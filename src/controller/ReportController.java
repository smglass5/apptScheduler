/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 *
 * @author Shannon Glass
 */
import java.io.IOException;
import java.util.HashMap;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Appointment;
import util.AppointmentDB;

public class ReportController {

    @FXML
    private TextArea reportText;

    Stage stage;
    Parent scene;
    Appointment contactAppointment;

    public void initialize() {
    }

    // Shows report for the amount of appointment types by month
    @FXML
    public void handleType(ActionEvent event) {
        reportText.clear();

        try {
            ObservableList<Appointment> type = AppointmentDB.getAppointmentsByMonth();
            Integer value = 1;

            HashMap<String, Integer> map = new HashMap<>();
            type.forEach((appointment) -> {
                if (map.containsKey(appointment.getType())) {
                    map.put(appointment.getType(), map.get(appointment.getType()) + 1);
                } else {
                    map.put(appointment.getType(), value);
                }
            });
            map.keySet().forEach((i) -> {
                reportText.appendText(map.get(i) + " " + i + " appointment(s) this month. \n");
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Shows report for Consultant appointments
    @FXML
    public void handleConsultant(ActionEvent event) {
        reportText.clear();

        try {
            ObservableList<Appointment> schedule = AppointmentDB.getAllAppointments();
            
            schedule.forEach((Appointment appointment) -> { //Lambda used for internal iteration of list to add efficiency
                int appointmentId = appointment.getAppointmentId();
                String contact = appointment.getContact();
                String customerName = appointment.getCustomerName();
                String type = appointment.getType();
                String start = appointment.getStart();

                reportText.appendText(contact + " has a " + type + " appointment with " + customerName
                        + " on " + start + ".\n");
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Shows report for amount of appointments per customer
    @FXML
    public void handleCustomer(ActionEvent event) {
        reportText.clear();

        try {
            ObservableList<Appointment> customer = AppointmentDB.getAllAppointments();
            Integer value = 1;

            HashMap<String, Integer> map = new HashMap<>();
            customer.forEach((appointment) -> {
                if (map.containsKey(appointment.getCustomerName())) {
                    map.put(appointment.getCustomerName(), map.get(appointment.getCustomerName()) + 1);
                } else {
                    map.put(appointment.getCustomerName(), value);
                }
            });
            map.keySet().forEach((i) -> {
                reportText.appendText(map.get(i) + " appointment(s) scheduled for " + i + ". \n");
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

}

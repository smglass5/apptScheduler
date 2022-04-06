/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import util.AppointmentDB;
import util.CustomerDB;
import util.FormException;

/**
 *
 * @author Shannon Glass
 */
public class AddAppointmentController {

    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private ComboBox<String> startTimeCombo;
    @FXML
    private ComboBox<String> endTimeCombo;
    @FXML
    private ComboBox<Customer> customerCombo;
    @FXML
    private ComboBox<String> contactCombo;
    @FXML
    private DatePicker datePicker;

    Stage stage;
    Parent scene;
    Alert alert;

    public static Appointment newAppointment = new Appointment();

    private final ObservableList<String> startTimes = FXCollections.observableArrayList();
    private final ObservableList<String> endTimes = FXCollections.observableArrayList();
    private final ObservableList<String> contactList = FXCollections.observableArrayList("Steven", "James", "Beth", "Mary");
    private final ObservableList<String> typeList = FXCollections.observableArrayList("Presentation", "Scrum", "Consultation", "Introduction");

    private final ZoneId zId = ZoneId.systemDefault();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter tf = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

    // Intializes AddAppointment scene and sets the combo box values
    public void initialize() {
        contactCombo.setItems(contactList);
        setCustomerCombo();
        typeCombo.setItems(typeList);
        setTimeCombos();
    }

    // Populates the time combo box values based on office hours
    public void setTimeCombos() {
        LocalTime time = LocalTime.of(8, 0);
        do {
            startTimes.add(time.format(tf));
            endTimes.add(time.format(tf));
            time = time.plusMinutes(15);
        } while (!time.equals(LocalTime.of(17, 15)));
        startTimes.remove(startTimes.size() - 1);
        endTimes.remove(0);

        startTimeCombo.setItems(startTimes);
        endTimeCombo.setItems(endTimes);
    }

    // Populates the customer combo box with current customers
    public void setCustomerCombo() {
        ObservableList<Customer> customers = CustomerDB.getCustomers();
        customerCombo.setItems(customers);
    }

    // Returns user to Appointment scene and cancels adding new appointment with confirmation
    @FXML
    public void handleCancel(ActionEvent event) throws IOException {

        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel");
        alert.setHeaderText("Confirmation Needed!");
        alert.setContentText("Are you sure you want to cancel adding a new appointment?");
        alert.showAndWait().ifPresent((response -> {
            if (response == ButtonType.OK) {
                try {
                    stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                    scene = FXMLLoader.load(getClass().getResource("/view/Appointment.fxml"));
                    stage.setScene(new Scene(scene));
                    stage.show();
                } catch (IOException ex) {
                    Logger.getLogger(AddAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }));
    }

    // Saves new appointment to database
    @FXML
    public void handleSave(ActionEvent event) {
        String contact = contactCombo.getValue();
        int customerId = customerCombo.getSelectionModel().getSelectedIndex() + 1;
        String type = typeCombo.getValue();
        LocalDate date = datePicker.getValue();
        LocalTime startTime = LocalTime.parse(startTimeCombo.getSelectionModel().getSelectedItem(), tf);
        LocalTime endTime = LocalTime.parse(endTimeCombo.getSelectionModel().getSelectedItem(), tf);

        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

        ZonedDateTime zdtStart = startDateTime.atZone(zId);
        ZonedDateTime zdtEnd = endDateTime.atZone(zId);
        ZonedDateTime utcStart = zdtStart.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime utcEnd = zdtEnd.withZoneSameInstant(ZoneId.of("UTC"));

        LocalDateTime locStart = utcStart.toLocalDateTime();
        LocalDateTime locEnd = utcEnd.toLocalDateTime();

        Timestamp start = Timestamp.valueOf(dtf.format(locStart));
        Timestamp end = Timestamp.valueOf(dtf.format(locEnd));

        try {
            if (validInput()) {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Save");
                alert.setContentText("Are you ready to save this appointment?");
                alert.showAndWait().ifPresent(((ButtonType response) -> {
                    if (response == ButtonType.OK) {
                        try {
                            AppointmentDB.saveAppointment(customerId, contact, type, start, end);
                            AppointmentDB.getAllAppointments();
                            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                            scene = FXMLLoader.load(getClass().getResource("/view/Appointment.fxml"));
                            stage.setScene(new Scene(scene));
                            stage.show();
                        } catch (IOException ex) {
                            Logger.getLogger(AddAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
                        }
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
        LocalDate date = datePicker.getValue();
        int dayOfWeek = date.getDayOfWeek().getValue();
        LocalTime startTime = LocalTime.parse(startTimeCombo.getSelectionModel().getSelectedItem(), tf);
        LocalTime endTime = LocalTime.parse(endTimeCombo.getSelectionModel().getSelectedItem(), tf);
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);
        ZonedDateTime zdtStart = startDateTime.atZone(zId);
        ZonedDateTime zdtEnd = endDateTime.atZone(zId);
        ZonedDateTime utcStart = zdtStart.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime utcEnd = zdtEnd.withZoneSameInstant(ZoneId.of("UTC"));
        LocalDateTime locStart = utcStart.toLocalDateTime();
        LocalDateTime locEnd = utcEnd.toLocalDateTime();

        if (contactCombo.getValue() == null) {
            throw new FormException("A contact must be selected!");
        }
        if (customerCombo.getSelectionModel() == null) {
            throw new FormException("A customer must be selected!");
        }
        if (typeCombo.getValue() == null) {
            throw new FormException("A type must be selected!");
        }
        if (date == null) {
            throw new FormException("A date must be selected!");
        }
        if (dayOfWeek == 6 || dayOfWeek == 7) {
            throw new FormException("The chosen date is outside of business hours!");
        }
        if (startTime == null) {
            throw new FormException("A start time must be selected!");
        }
        if (startTime.equals(endTime) || endTime.equals(startTime)) {
            throw new FormException("The start time and endtime, cannot be the same!");
        }
        if (endTime == null) {
            throw new FormException("An end time must be selected!");
        }
        if (startTime.isAfter(endTime)) {
            throw new FormException("The start time cannot be after the end time!");
        }
        if (endTime.isBefore(startTime)) {
            throw new FormException("The end time cannot be before the start time!");
        }
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new FormException("An appointment cannot be scheduled in the past!");
        }
        if (AppointmentDB.getOverlaps(utcStart, utcEnd) != null) {
            throw new FormException("The time and/or date cannot overlap an existing appointment!");
        }
        return true;
    }

}

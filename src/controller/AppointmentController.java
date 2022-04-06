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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import util.AppointmentDB;
import util.FormException;

/**
 *
 * @author Shannon Glass
 */
public class AppointmentController {

    @FXML
    private TableView<Appointment> appointmentTable;
    @FXML
    private TableColumn<String, Customer> customerColumn;
    @FXML
    private TableColumn<Integer, Appointment> appointmentIdColumn;
    @FXML
    private TableColumn<String, Appointment> typeColumn;
    @FXML
    private TableColumn<String, Appointment> startColumn;
    @FXML
    private TableColumn<String, Appointment> endColumn;
    @FXML
    private TableColumn<String, Appointment> contactColumn;
    @FXML
    private RadioButton allRadio;
    @FXML
    private TextField customerField;
    @FXML
    private ComboBox<String> contactCombo;
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private ComboBox<String> startTimeCombo;
    @FXML
    private ComboBox<String> endTimeCombo;
    @FXML
    private DatePicker datePicker;

    Stage stage;
    Parent scene;
    Alert alert;

    private Appointment selectedAppointment;

    int customerId;
    int appointmentId;
    int userId;

    private final ObservableList<String> startTimes = FXCollections.observableArrayList();
    private final ObservableList<String> endTimes = FXCollections.observableArrayList();
    private final ObservableList<String> contactList = FXCollections.observableArrayList("Steven", "James", "Beth", "Mary");
    private final ObservableList<String> typeList = FXCollections.observableArrayList("Presentation", "Scrum", "Consultation", "Introduction");

    private final ZoneId zId = ZoneId.systemDefault();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter tf = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private final DateTimeFormatter df = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

    // Intializes Appointment scene and sets the table and combo box values
    public void initialize() {
        setTableValues();
        appointmentTable.setItems(AppointmentDB.getAllAppointments());
        contactCombo.setItems(contactList);
        typeCombo.setItems(typeList);
        setTimeCombos();
    }

    // Sets appointment table values
    public void setTableValues() {
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
    }

    // Sets the time combo box values based on office hours
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

    // Sets field values of selected appointment to be updated
    public void setAppointment() {
        selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        customerId = selectedAppointment.getCustomerId();
        appointmentId = selectedAppointment.getAppointmentId();
        userId = selectedAppointment.getUserId();
        contactCombo.getSelectionModel().select(selectedAppointment.getContact());
        customerField.setText(selectedAppointment.getCustomerName());
        typeCombo.getSelectionModel().select(selectedAppointment.getType());
        datePicker.setValue(LocalDate.parse(selectedAppointment.getStart(), df));
        String start = selectedAppointment.getStart();
        String end = selectedAppointment.getEnd();
        LocalDateTime startLdt = LocalDateTime.parse(start, df);
        LocalDateTime endLdt = LocalDateTime.parse(end, df);
        startTimeCombo.getSelectionModel().select(startLdt.toLocalTime().format(tf));
        endTimeCombo.getSelectionModel().select(endLdt.toLocalTime().format(tf));
    }

    // Shows table with all appointments by default and whenever allRadio button is selected
    @FXML
    public void handleAll(ActionEvent event) {
        appointmentTable.setItems(AppointmentDB.getAllAppointments());
    }

    // When monthRadio button is selected, shows table with appointments for the next 30 days
    @FXML
    public void handleMonth(ActionEvent event) {
        setTableValues();
        appointmentTable.setItems(AppointmentDB.getAppointmentsByMonth());
    }

    // When weekRadio button is selected, shows table with appointments for the next 7 days
    @FXML
    public void handleWeek(ActionEvent event) {
        setTableValues();
        appointmentTable.setItems(AppointmentDB.getAppointmentsByWeek());
    }

    // Returns user to MainMenu scene
    @FXML
    public void handleBack(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    // Deletes selected appointment from database with confirmation
    @FXML
    public void handleDelete(ActionEvent event) {
        if (appointmentTable.getSelectionModel().getSelectedItem() != null) {
            selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();

            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Alert");
            alert.setHeaderText("Delete");
            alert.setContentText("Are you sure you want to delete this appointment?");
            alert.showAndWait().ifPresent((response -> {
                if (response == ButtonType.OK) {
                    AppointmentDB.deleteAppointment(selectedAppointment);
                    appointmentTable.setItems(AppointmentDB.getAllAppointments());
                }
            }));
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No selection!");
            alert.setContentText("An appointment must be selected!");
            alert.showAndWait();
        }
    }

    // Opens addAppointment scene
    @FXML
    public void handleNew(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/AddAppointment.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    // Sends selected appointment data to form for updating
    @FXML
    public void handleUpdate(ActionEvent event) {
        if (appointmentTable.getSelectionModel().getSelectedItem() != null) {
            setAppointment();
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No selection!");
            alert.setContentText("An appointment must be selected!");
            alert.showAndWait();
        }
    }

    // Saves updated appointment data to database
    @FXML
    public void handleSave(ActionEvent event) {
        appointmentId = selectedAppointment.getAppointmentId();
        String contact = contactCombo.getValue();
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
                alert.showAndWait().ifPresent((response -> {
                    if (response == ButtonType.OK) {
                        AppointmentDB.updateAppointment(appointmentId, contact, type, start, end);
                        appointmentTable.setItems(AppointmentDB.getAppointmentsByMonth());
                        appointmentTable.setItems(AppointmentDB.getAppointmentsByWeek());
                        appointmentTable.setItems(AppointmentDB.getAllAppointments());
                        allRadio.setSelected(true);
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

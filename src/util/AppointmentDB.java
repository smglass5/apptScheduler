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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;

/**
 *
 * @author Shannon Glass
 */
public class AppointmentDB {

    private static final Connection conn = DBConnection.getConnection();
    private static PreparedStatement ps;
    private static ResultSet rs;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    private static final ZoneId zId = ZoneId.systemDefault();

    private static final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static final ObservableList<Appointment> appointmentsByMonth = FXCollections.observableArrayList();
    private static final ObservableList<Appointment> appointmentsByWeek = FXCollections.observableArrayList();
    private static final ObservableList<Appointment> appointmentAlerts = FXCollections.observableArrayList();
    private static final ObservableList<Appointment> appointmentOverlaps = FXCollections.observableArrayList();

    // Retrieves all appointments from database
    public static ObservableList<Appointment> getAllAppointments() {
        allAppointments.clear();

        try {
            String selectStatement = "SELECT appointment.*, customer.customerName FROM appointment JOIN "
                    + "customer ON appointment.customerId = customer.customerId "
                    + "ORDER BY appointment.start";
            ps = conn.prepareStatement(selectStatement);
            rs = ps.executeQuery();

            while (rs.next()) {
                Appointment all = new Appointment();
                all.setAppointmentId(rs.getInt("appointmentId"));
                all.setCustomerName(rs.getString("customerName"));
                all.setContact(rs.getString("contact"));
                all.setType(rs.getString("type"));
                Timestamp start = rs.getTimestamp("start");
                Timestamp end = rs.getTimestamp("end");

                ZonedDateTime zdtStart = start.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime localStart = zdtStart.withZoneSameInstant(zId);
                ZonedDateTime zdtEnd = end.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime localEnd = zdtEnd.withZoneSameInstant(zId);

                all.setStart(localStart.format(dtf));
                all.setEnd(localEnd.format(dtf));
                allAppointments.add(all);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return allAppointments;
    }
 
    // Retrieves appointments for the next 30 days from database
    public static ObservableList<Appointment> getAppointmentsByMonth() {
        appointmentsByMonth.clear();
        LocalDate now = LocalDate.now();
        LocalDate month = LocalDate.now().plusMonths(1);

        try {
            String selectStatement = "SELECT appointment.*, customer.customerName FROM appointment JOIN "
                    + "customer ON appointment.customerId = customer.customerId "
                    + "WHERE "
                    + "start >= '" + now + "' AND start <= '" + month + "'"
                    + "ORDER BY appointment.start";
            ps = conn.prepareStatement(selectStatement);
            rs = ps.executeQuery();

            while (rs.next()) {
                Appointment monthly = new Appointment();
                monthly.setAppointmentId(rs.getInt("appointmentId"));
                monthly.setCustomerName(rs.getString("customerName"));
                monthly.setContact(rs.getString("contact"));
                monthly.setType(rs.getString("type"));
                Timestamp start = rs.getTimestamp("start");
                Timestamp end = rs.getTimestamp("end");

                ZonedDateTime zdtStart = start.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime localStart = zdtStart.withZoneSameInstant(zId);
                ZonedDateTime zdtEnd = end.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime localEnd = zdtEnd.withZoneSameInstant(zId);

                monthly.setStart(localStart.format(dtf));
                monthly.setEnd(localEnd.format(dtf));
                appointmentsByMonth.add(monthly);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return appointmentsByMonth;
    }

    // Retrieves appointments for the next 7 days from database
    public static ObservableList<Appointment> getAppointmentsByWeek() {
        appointmentsByWeek.clear();
        LocalDate now = LocalDate.now();
        LocalDate week = LocalDate.now().plusWeeks(1);

        try {
            String selectStatement = "SELECT appointment.*, customer.customerName"
                    + " FROM appointment "
                    + " JOIN customer "
                    + " ON appointment.customerId = customer.customerId "
                    + " WHERE "
                    + "start >= '" + now + "' AND start <= '" + week + "'"
                    + "ORDER BY appointment.start";
            ps = conn.prepareStatement(selectStatement);
            rs = ps.executeQuery();

            while (rs.next()) {
                Appointment weekly = new Appointment();
                weekly.setAppointmentId(rs.getInt("appointmentId"));
                weekly.setCustomerName(rs.getString("customerName"));
                weekly.setContact(rs.getString("contact"));
                weekly.setType(rs.getString("type"));
                Timestamp start = rs.getTimestamp("start");
                Timestamp end = rs.getTimestamp("end");

                ZonedDateTime zdtStart = start.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime localStart = zdtStart.withZoneSameInstant(zId);
                ZonedDateTime zdtEnd = end.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime localEnd = zdtEnd.withZoneSameInstant(zId);

                weekly.setStart(localStart.format(dtf));
                weekly.setEnd(localEnd.format(dtf));
                appointmentsByWeek.add(weekly);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return appointmentsByWeek;
    }

    // Used to find whether an appointment time overlaps with existing appointment
    public static ObservableList<Appointment> getOverlaps(ZonedDateTime newStart, ZonedDateTime newEnd) {
        appointmentOverlaps.clear();
        int appointmentId = 0;

        try {
            String selectStatement = "SELECT * FROM appointment "
                    + "WHERE (start BETWEEN ? AND ? OR end BETWEEN ? AND ?)"
                    + "OR (start >= ? AND end <= ?)"
                    + "OR (start < ? AND end > ?)"
                    + "AND (appointmentId = ?)";
            ps = conn.prepareStatement(selectStatement);
            ps.setTimestamp(1, Timestamp.valueOf(newStart.toLocalDateTime()));
            ps.setTimestamp(2, Timestamp.valueOf(newEnd.toLocalDateTime()));
            ps.setTimestamp(3, Timestamp.valueOf(newStart.toLocalDateTime()));
            ps.setTimestamp(4, Timestamp.valueOf(newEnd.toLocalDateTime()));
            ps.setTimestamp(5, Timestamp.valueOf(newStart.toLocalDateTime()));
            ps.setTimestamp(6, Timestamp.valueOf(newEnd.toLocalDateTime()));
            ps.setTimestamp(7, Timestamp.valueOf(newStart.toLocalDateTime()));
            ps.setTimestamp(8, Timestamp.valueOf(newEnd.toLocalDateTime()));
            ps.setInt(9, appointmentId);
            rs = ps.executeQuery();

            if (rs.next()) {
                Appointment overlaps = new Appointment();
                overlaps.setAppointmentId(rs.getInt("appointmentId"));
                appointmentOverlaps.add(overlaps);
                return appointmentOverlaps;
            }

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
        return null;
    }

    // Used to create an alert when a user logs in if there is an upcoming appointment within 15 minutes
    public static ObservableList<Appointment> getAlertAppointment() {
        appointmentAlerts.clear();
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime zdt = now.atZone(zId);
        LocalDateTime localStart = zdt.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime plus15 = localStart.plusMinutes(15);

        try {
            String selectStatement = "SELECT * FROM appointment WHERE start BETWEEN '" + localStart + "'  AND '" + plus15 + "'";
            ps = conn.prepareStatement(selectStatement);
            rs = ps.executeQuery();

            if (rs.next()) {
                Appointment alerts = new Appointment();
                alerts.setAppointmentId(rs.getInt("appointmentId"));
                alerts.setType(rs.getString("type"));
                appointmentAlerts.add(alerts);
                return appointmentAlerts;
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
        return null;
    }

    // Updates appointment data in database
    public static void updateAppointment(int appointmentId, String contact, String type, Timestamp start, Timestamp end) {

        try {
            String updateAppointment = "UPDATE appointment SET contact = ?, type = ?, start = ?, end = ? WHERE appointmentId = ?";
            ps = conn.prepareStatement(updateAppointment);
            ps.setString(1, contact);
            ps.setString(2, type);
            ps.setTimestamp(3, start);
            ps.setTimestamp(4, end);
            ps.setInt(5, appointmentId);
            ps.execute();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Saves new appointment to database
    public static void saveAppointment(int customerId, String contact, String type, Timestamp start, Timestamp end) {
        int appointmentId = getAppointmentMaxId();

        try {
            String insertAppointment = "INSERT INTO appointment (appointmentId, customerId, userId, "
                    + "title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy)"
                    + "VALUES(?, ?, 1, ' ', ' ', ' ', ?, ?, ' ', ?, ?, NOW(), 'test', NOW(), 'test')";
            ps = conn.prepareStatement(insertAppointment);
            ps.setInt(1, appointmentId);
            ps.setInt(2, customerId);
            ps.setString(3, contact);
            ps.setString(4, type);
            ps.setTimestamp(5, start);
            ps.setTimestamp(6, end);
            ps.execute();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Deletes appointment from database
    public static void deleteAppointment(Appointment appointment) {

        try {
            String deleteAppointment = "DELETE FROM appointment WHERE appointmentId = ?";
            ps = conn.prepareStatement(deleteAppointment);
            ps.setInt(1, appointment.getAppointmentId());
            ps.execute();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Finds the maximum existing appointmentId for adding new appointment with new id
    private static int getAppointmentMaxId() {
        int appointmentMaxId = 0;

        try {
            String selectMax = "SELECT MAX(appointmentId) FROM appointment";
            ps = conn.prepareStatement(selectMax);
            rs = ps.executeQuery();

            if (rs.next()) {
                appointmentMaxId = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return appointmentMaxId + 1;
    }
}

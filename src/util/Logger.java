/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;

/**
 *
 * @author Shannon Glass
 */
public class Logger {

    // Writes user activity to a readable file
    public static void logger(String userName, boolean success) {

        String FILENAME = "log.text";
        try (FileWriter fw = new FileWriter(FILENAME, true);
            BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter pw = new PrintWriter(bw)) {
                pw.println(userName + " " + (success ? "successfully logged in at: " : "failed to log in at: ") + ZonedDateTime.now()); 

        } catch (IOException e) {
            System.out.println("Logger Error" + e.getMessage());
        }
    }
}

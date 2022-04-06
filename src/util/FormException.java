/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Shannon Glass
 */

// Custom exception for input validation
public class FormException extends Exception {
    public FormException(String message) {
        super(message);
    }    
}

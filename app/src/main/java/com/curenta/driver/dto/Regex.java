package com.curenta.driver.dto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    public static String nameRegex="^[a-z\\sA-Z]*$";
    public static String phoneNumber="^([(]\\d{3}[)])\\s\\d{3}[-]\\d{4}$";
    public static String password=".{6,}$";
    public static String otp="^\\d{6}$";
    public static String driverLicense="[A-Z]{1}\\d{7}";
    public static String bankRoutingNumber="^\\d{9}$";
    public static String accountNumber="^\\d{10,17}$";
    public static String zipCode="^\\d{3,6}$";
    public static String socialSecurityNumber="^(\\d{3}\\s[-]\\s\\d{2}\\s[-])\\s\\d{4}$";


    public static boolean isValid(String text,String regex){

            // Compile the ReGex
            Pattern p = Pattern.compile(regex);

            // If the password is empty
            // return false
            if (text == null) {
                return false;
            }

            // Pattern class contains matcher() method
            // to find matching between given password
            // and regular expression.
            Matcher m = p.matcher(text);

            // Return if the password
            // matched the ReGex
            return m.matches();


    }
}

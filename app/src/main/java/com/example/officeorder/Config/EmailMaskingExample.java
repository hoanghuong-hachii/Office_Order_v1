package com.example.officeorder.Config;

public class EmailMaskingExample {

    public static void main(String[] args) {
        String email = "huong@example.com";
        //String maskedEmail = maskEmail(email);

        System.out.println("Original Email: " + email);
       // System.out.println("Masked Email: " + maskedEmail);
    }

    public String maskEmail(String email) {
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        int maskLength = Math.min(username.length() / 2, 3);
        String maskedUsername = maskString(username, maskLength);

        String maskedEmail = maskedUsername + "@" + domain;

        return maskedEmail;
    }

    public static String maskString(String str, int maskLength) {

        StringBuilder maskedString = new StringBuilder();
        for (int i = 0; i < maskLength; i++) {
            maskedString.append('*');
        }
        maskedString.append(str.substring(maskLength));
        return maskedString.toString();
    }
}

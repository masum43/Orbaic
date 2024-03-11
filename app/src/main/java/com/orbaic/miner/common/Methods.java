package com.orbaic.miner.common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

public class Methods {
    public static void showErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static String roundToFourDecimalPlaces(double value) {
        Locale locale = Locale.US;
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        DecimalFormat decimalFormat = new DecimalFormat("#.####", symbols);
        return decimalFormat.format(value);
    }


    public static String generateReferralCode(String userId) {
        String prefix = userId.substring(0, 5);
        String hashedString = generateRandomString(5);
        return prefix + hashedString.substring(0, 5);
    }

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static String generateRandomString(int length) {
        Random random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

//    public static String generateUniqueCode() {
//        Random random = new Random();
//        long currentTimeMillis = System.currentTimeMillis();
//        int randomPart = random.nextInt(100000); // Generate a random 5-digit number
//        String uniqueCode = String.format("%d%05d", currentTimeMillis, randomPart);
//        return uniqueCode.substring(0, 8); // Take the first 8 digits
//    }

    public static void dialogWarningShowing(Activity activity, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finishAffinity();
            }
        });
        builder.create().show();
    }
}

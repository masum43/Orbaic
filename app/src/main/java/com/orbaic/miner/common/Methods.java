package com.orbaic.miner.common;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

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

    public static String generateUniqueCode() {
        Random random = new Random();
        long currentTimeMillis = System.currentTimeMillis();
        int randomPart = random.nextInt(100000); // Generate a random 5-digit number
        String uniqueCode = String.format("%d%05d", currentTimeMillis, randomPart);
        return uniqueCode.substring(0, 8); // Take the first 8 digits
    }
}

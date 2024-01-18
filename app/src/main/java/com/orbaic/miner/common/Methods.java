package com.orbaic.miner.common;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import java.text.DecimalFormat;

public class Methods {
    public static void showErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static double roundToFourDecimalPlaces(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.####");
        String formattedValue = decimalFormat.format(value);
        return Double.parseDouble(formattedValue);
    }
}

package org.martellina.artpricingapp;

import android.util.Log;

public class StringShare {
    public static String makeStringShare () {

        String result = "";

        Log.d("MyLog", result);

        if (MainActivity.name.getText().toString().length() > 0) {
            result = MainActivity.name.getText().toString() + "\n";
            Log.d("MyLog", result);
        }

        if (MainActivity.totalPriceOnePiece.getText().toString().length() > 0 && !MainActivity.currencyCurrent.isEmpty()) {
            result = result.concat(MainActivity.totalPriceOnePiece.getText().toString() + " " + MainActivity.currencyCurrent + "\n");
            Log.d("MyLog", result);
        }

        if (MainActivity.exchanged.getText().toString().length() > 0 && !MainActivity.currencyExchangeTo.isEmpty()) {
            result = result.concat(MainActivity.exchanged.getText().toString() + " " + MainActivity.currencyExchangeTo + "\n");
            Log.d("MyLog", result);
        }

        return result;


    }
}

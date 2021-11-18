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

        if (MainActivity.totalPriceOnePiece.getText().toString().length() > 0) {
            result = result.concat(MainActivity.totalPriceOnePiece.getText().toString() + " руб." + "\n");
            Log.d("MyLog", result);
        }

        if (!MainActivity.arrayList.isEmpty()) {
            for (int i = 0; i<MainActivity.arrayList.size(); i++) {
                result = result.concat(MainActivity.arrayList.get(i).getData1() + " " + MainActivity.arrayList.get(i).getData5() + "\n");
                Log.d("MyLog", result);
            }
        }

        return result;


    }
}

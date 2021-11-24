package org.martellina.artpricingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static List<ListItemClass> arrayList = new ArrayList<>();

    public static TextView totalPriceOnePiece;
    public static TextView name;

    public static String currencyCurrent;
    public static String currencyExchangeTo;

    public static TextView exchanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spinnerCurrent = (Spinner) findViewById(R.id.spinner_current);
        String[] currency = getResources().getStringArray(R.array.currency);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currency);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrent.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currencyCurrent = (String)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                currencyCurrent = "";
            }
        };

        spinnerCurrent.setOnItemSelectedListener(itemSelectedListener);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        name = (EditText) findViewById(R.id.edit_text_name);

        EditText materialsQuantity = (EditText) findViewById(R.id.edit_text_quantity);
        EditText materialPrice = (EditText) findViewById(R.id.edit_text_material_price);
        TextView materialsTotal = (TextView) findViewById(R.id.text_view_materials_total);

        TextWatcher textWatcherMaterials = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (materialsQuantity.getText().toString().length() > 0
                        & materialPrice.getText().toString().length() > 0) {
                    double q = Double.parseDouble(materialsQuantity.getText().toString());
                    double p = Double.parseDouble(materialPrice.getText().toString());
                    double total = p * q;
                    String result = String.format("%.2f", total);
                    materialsTotal.setText(result);
                } else {
                    materialsTotal.setText("0");
                }
            }
        };

        materialsQuantity.addTextChangedListener(textWatcherMaterials);
        materialPrice.addTextChangedListener(textWatcherMaterials);

        EditText hours = (EditText) findViewById(R.id.edit_text_hours);
        EditText timePrice = (EditText) findViewById(R.id.edit_text__time_price);
        TextView timeTotal = (TextView) findViewById(R.id.text_view_time_total);

        TextWatcher textWatcherTime = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (hours.getText().toString().length() > 0
                        & timePrice.getText().toString().length() > 0) {
                    double h = Double.parseDouble(hours.getText().toString());
                    double tp = Double.parseDouble(timePrice.getText().toString());

                    double total = h * tp;

                    String result = String.format("%.2f", total);

                    timeTotal.setText(result);
                } else {
                    timeTotal.setText("0");
                }
            }
        };

        hours.addTextChangedListener(textWatcherTime);
        timePrice.addTextChangedListener(textWatcherTime);


        EditText numberOfPieces = (EditText) findViewById(R.id.edit_text_number_of_pieces);
        EditText extraCharge = (EditText) findViewById(R.id.edit_text_extra_charge);
        EditText delivery = (EditText) findViewById(R.id.edit_text_delivery);

        totalPriceOnePiece = (TextView) findViewById(R.id.text_view_total_price_of_one);
        Button pricing = (Button) findViewById(R.id.button_pricing);
        pricing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfPieces.getText().toString().length() == 0
                | extraCharge.getText().toString().length() == 0
                | delivery.getText().toString().length() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Заполните все поля", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0,0);
                    toast.show();
                } else {
                    double mt = Double.parseDouble(materialsTotal.getText().toString().replaceAll(",", "."));
                    double tt = Double.parseDouble(timeTotal.getText().toString().replaceAll(",", "."));

                    double onePiece = (mt + tt) / Double.parseDouble(numberOfPieces.getText().toString());
                    double priceOnePiece = onePiece + onePiece / 100 * Double.parseDouble(extraCharge.getText().toString());
                    double totalOnePiece = priceOnePiece + Double.parseDouble(delivery.getText().toString());

                    String result = String.format("%.2f", totalOnePiece);

                    totalPriceOnePiece.setText(result);
                }
            }
        });

        Button clear = (Button) findViewById(R.id.button_clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear((ViewGroup) findViewById(R.id.layout_pricing));
            }
        });

        Spinner spinnerExchange = (Spinner) findViewById(R.id.spinner_exchange);
        ArrayAdapter adapterExchange = new ArrayAdapter(this, android.R.layout.simple_list_item_1, currency);
        adapterExchange.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExchange.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener2 = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currencyExchangeTo = (String)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                currencyExchangeTo = "";
            }
        };

        spinnerExchange.setOnItemSelectedListener(itemSelectedListener2);
    }


    public void clear (ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; i++) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }
            if (view instanceof ViewGroup && ((ViewGroup)view).getChildCount() > 0) {
                clear((ViewGroup)view);
            }
        }
        totalPriceOnePiece.setText("0");
        exchanged.setText("0");
    }

    public void onExchange (View view) {
        if (totalPriceOnePiece.getText().toString().length() > 0) {
            if (isConnected()) {
                String url = "https://api.exchangerate.host/convert?from=" + currencyCurrent + "&to=" + currencyExchangeTo;

                new GetURLData().execute(url);

            } else {
                DialogFragmentWiFi dialog = new DialogFragmentWiFi();
                dialog.show(getSupportFragmentManager(), "custom");
            }
        }
        else{
            Toast toast = Toast.makeText(this, "Рассчитайте стоимость в рублях", Toast.LENGTH_LONG);
            toast.show();
        }


    }

    public class GetURLData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");

                    return buffer.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }
            return null;
            }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                double exchangeRate = jsonObject.getJSONObject("info").getDouble("rate");
                exchanged = (TextView) findViewById(R.id.text_view_exchanged);
                double result = Double.parseDouble(totalPriceOnePiece.getText().toString().replaceAll(",", ".")) * exchangeRate;

                exchanged.setText(String.format("%.2f", result));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    public void onSendData(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, StringShare.makeStringShare());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public void onBackPressed() {
        DialogFragmentExit dialog = new DialogFragmentExit();
        dialog.show(getSupportFragmentManager(), "custom");
    }
    
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected() && ni.getType() == ConnectivityManager.TYPE_WIFI;
    }

}


package org.martellina.artpricingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Document doc;
    private Thread secThread;
    private Runnable runnable;
    private ListView listView;
    private CustomArrayAdapter adapter;
    private List<ListItemClass> arrayList;

    private TextView totalPriceOnePiece;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
    }

    public void onExchangeOnePiece (View view) {
        init();
    }

    private void init () {
        listView = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        adapter = new CustomArrayAdapter(this, R.layout.list_item_1, arrayList,getLayoutInflater());
        listView.setAdapter(adapter);

        runnable = new Runnable() {
            @Override
            public void run() {
                getWeb();
            }
        };
        secThread = new Thread (runnable);
        secThread.start();
    }

    private void getWeb() {
        try {
            doc = Jsoup.connect("https://www.cbr.ru/currency_base/daily/").get();

            Elements tables = doc.getElementsByTag("tbody");
            Element ourTable = tables.get(0);
            Elements elementsFromTable = ourTable.children();
            Element first = elementsFromTable.get(1);
            Elements first_elements = first.children();

            //Log.d("MyLog", "It`s /" + ourTable.children().get(1).child(1).text() + "/.");

            for (int i = 0; i < ourTable.childrenSize(); i++) {

                ListItemClass items = new ListItemClass();

                if (ourTable.children().get(i).child(1).text().equals("EUR")
                        | ourTable.children().get(i).child(1).text().equals("USD")) {

                    items.setData1(ourTable.children().get(i).child(3).text());
                    Log.d("MyLog", ourTable.children().get(i).child(3).text());
                    items.setData2(ourTable.children().get(i).child(4).text());

                    totalPriceOnePiece = (TextView) findViewById(R.id.text_view_total_price_of_one);

                    double oneRub = Double.parseDouble(totalPriceOnePiece.getText().toString().replaceAll("," , "."));
                    double n = Double.parseDouble(ourTable.children().get(i).child(4).text().replaceAll(",", "."));
                    double result = oneRub/n;

                    String res = String.format("%.2f", result);

                    items.setData5(res);
                    arrayList.add(items);
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
        ab.setTitle("Закрыть приложение");
        ab.setMessage("Данные при выходе не сохраняются");
        ab.setPositiveButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        ab.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        ab.show();
    }
}
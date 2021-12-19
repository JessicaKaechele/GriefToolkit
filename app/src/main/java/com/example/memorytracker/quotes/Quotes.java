package com.example.memorytracker.quotes;

import static com.example.memorytracker.Utils.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.memorytracker.R;
import com.example.memorytracker.Utils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

public class Quotes extends AppCompatActivity {

    private ArrayList<String> quoteList;
    private final String filename = "quote_data.json";
    private ListView listView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);

        quoteList = getQuoteList();

        listView = findViewById(R.id.list);
        ImageButton addQuoteButton = findViewById(R.id.addQuote);

        addQuoteButton.setOnClickListener(v -> {
            AlertDialog.Builder quoteItemBuilder = new AlertDialog.Builder(Quotes.this);
            quoteItemBuilder.setTitle("Füge ein Zitat hinzu:");
            LinearLayout linearLayout = new LinearLayout(Quotes.this);
            final EditText quoteEditText = new EditText(Quotes.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.leftMargin= convertDpToPx(Quotes.this, 20);
            params.rightMargin= convertDpToPx(Quotes.this, 20);

            quoteEditText.setLayoutParams(params);
            linearLayout.addView(quoteEditText);
            quoteItemBuilder.setView(linearLayout);
            quoteItemBuilder.setPositiveButton(R.string.save, (dialogInterface, i) -> {
                String quoteInput = quoteEditText.getText().toString();
                ContentValues values = new ContentValues();
                values.clear();
                saveQuote(quoteInput);
                updateQuoteList();
            });

            quoteItemBuilder.setNegativeButton(R.string.back, null);

            quoteItemBuilder.create().show();
        });


        updateQuoteList();
    }

    private void updateQuoteList() {
        ArrayAdapter<String> quoteListAdapter = new ArrayAdapter<>(
                this,
                R.layout.quote,
                R.id.textView,
                quoteList
        );
        listView.setAdapter(quoteListAdapter);
    }

    private void saveQuote(String quote) {
        if (!quoteList.contains(quote)) {
            quoteList.add(quote);
            writeToFile(this, quoteList, filename);
        }
    }

    public void onDeleteButtonClick(View view) {
        View v = (View) view.getParent();
        TextView itemTextView = v.findViewById(R.id.textView);
        String quoteItem = itemTextView.getText().toString();

        deleteQuote(quoteItem);

        updateQuoteList();
    }


    private void deleteQuote(String quote) {
        quoteList.remove(quote);
        writeToFile(this, quoteList, filename);
    }

    @NonNull
    private ArrayList<String> getQuoteList() {
        String quoteData = null;
        try {
            quoteData = readFromFile(Quotes.this, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Utils.fromJson(quoteData);
    }
}
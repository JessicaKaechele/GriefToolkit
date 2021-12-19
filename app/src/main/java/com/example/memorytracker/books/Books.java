package com.example.memorytracker.books;

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

public class Books extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> bookList;
    private final String filename = "book_data.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        bookList = getBookList();

        listView = findViewById(R.id.list);
        ImageButton addBookButton = findViewById(R.id.addBook);

        addBookButton.setOnClickListener(v -> {
            AlertDialog.Builder bookItemBuilder = new AlertDialog.Builder(Books.this);
            bookItemBuilder.setTitle("Füge ein Medium hinzu:");
            LinearLayout linearLayout = new LinearLayout(Books.this);
            final EditText bookEditText = new EditText(Books.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.leftMargin= convertDpToPx(Books.this, 20);
            params.rightMargin= convertDpToPx(Books.this, 20);
            bookEditText.setLayoutParams(params);
            linearLayout.addView(bookEditText);
            bookItemBuilder.setView(linearLayout);
            bookItemBuilder.setPositiveButton(R.string.save, (dialogInterface, i) -> {
                String bookInput = bookEditText.getText().toString();
                ContentValues values = new ContentValues();
                values.clear();
                saveBook(bookInput);
                updateBookList();
            });

            bookItemBuilder.setNegativeButton(R.string.back, null);

            bookItemBuilder.create().show();
        });


        updateBookList();
    }

    private void updateBookList() {
        ArrayAdapter<String> bookListAdapter = new ArrayAdapter<>(
                this,
                R.layout.book,
                R.id.textView,
                bookList
        );
        listView.setAdapter(bookListAdapter);
    }

    private void saveBook(String book) {
        if (!bookList.contains(book)) {
            bookList.add(book);
            writeToFile(this, bookList, filename);
        }

    }

    public void onDeleteButtonClick(View view) {
        View v = (View) view.getParent();
        TextView itemTextView = v.findViewById(R.id.textView);
        String bookItem = itemTextView.getText().toString();

        deleteBook(bookItem);

        updateBookList();
    }


    private void deleteBook(String book) {
        bookList.remove(book);
        writeToFile(this, bookList, filename);
    }



    @NonNull
    private ArrayList<String> getBookList() {
        String bookData = null;
        try {
            bookData = readFromFile(Books.this, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Utils.fromJson(bookData);
    }
}

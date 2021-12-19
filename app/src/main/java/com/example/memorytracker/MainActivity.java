package com.example.memorytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.memorytracker.books.Books;
import com.example.memorytracker.dayCounter.DayCounter;
import com.example.memorytracker.memorabilia.Memorabilia;
import com.example.memorytracker.memories.Memories;
import com.example.memorytracker.mood.MoodTracker;
import com.example.memorytracker.quotes.Quotes;
import com.example.memorytracker.source.Sources;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button moodTracker = findViewById(R.id.moodTracker);
        moodTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMoodTracker(view);
            }
        });

        Button memories = findViewById(R.id.memories);
        memories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMemories(view);
            }
        });

        Button sources = findViewById(R.id.sources);
        sources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSources(view);
            }
        });

        Button books = findViewById(R.id.books);
        books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBooks(view);
            }
        });

        Button dayCounter = findViewById(R.id.dayCounter);
        dayCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDayCounter(view);
            }
        });

        Button quotes = findViewById(R.id.quotes);
        quotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openQuotes(view);
            }
        });

        Button memorabilia = findViewById(R.id.memorabilia);
        memorabilia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMemorabilia(view);
            }
        });
    }

    public void openMoodTracker(View view) {
        Intent intent = new Intent(this, MoodTracker.class);
        startActivity(intent);
    }

    public void openMemories(View view) {
        Intent intent = new Intent(this, Memories.class);
        startActivity(intent);
    }

    public void openBooks(View view) {
        Intent intent = new Intent(this, Books.class);
        startActivity(intent);
    }

    public void openSources(View view) {
        Intent intent = new Intent(this, Sources.class);
        startActivity(intent);
    }

    public void openDayCounter(View view) {
        Intent intent = new Intent(this, DayCounter.class);
        startActivity(intent);
    }

    public void openQuotes(View view) {
        Intent intent = new Intent(this, Quotes.class);
        startActivity(intent);
    }

    public void openMemorabilia(View view) {
        Intent intent = new Intent(this, Memorabilia.class);
        startActivity(intent);
    }

}
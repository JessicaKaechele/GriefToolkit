package com.example.memorytracker.dayCounter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.example.memorytracker.R;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class DayCounter extends AppCompatActivity {
    private Period period;
    private long daysBetween;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_counter);

        TextView dayTextView = findViewById(R.id.dayTextView);

        TextView daysNumber = findViewById(R.id.daysNumber);
        TextView monthNumber = findViewById(R.id.monthNumber);
        TextView weeksNumber = findViewById(R.id.weeksNumber);
        TextView yearNumber = findViewById(R.id.yearNumber);


        calcDuration();

        dayTextView.setText(String.valueOf(daysBetween));
        daysNumber.setText(String.valueOf(period.getDays()%7));
        monthNumber.setText(String.valueOf(period.getMonths()));
        weeksNumber.setText(String.valueOf(period.getDays()/7));
        yearNumber.setText(String.valueOf(period.getYears()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calcDuration() {
        LocalDate currentDate = LocalDate.now();
        LocalDate dayOfDeath = LocalDate.of(2021, 7, 27);

        period = Period.between(dayOfDeath, currentDate);
        daysBetween = ChronoUnit.DAYS.between(dayOfDeath, currentDate);
    }
}
package com.example.photogallery;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private static DateFormat displayFormat = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
    private EditText startDateDisplay;
    private EditText endDateDisplay;

    private Calendar dateStringToCalendarDate(String date) {
        Date selectedDate = new Date();
        try {
            selectedDate = displayFormat.parse(date);
        } catch (ParseException e) {}
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        return calendar;

    }

    private DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            setDate(startDateDisplay, year, month, day);
        }
    };

    private DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            setDate(endDateDisplay, year, month, day);
        }
    };

    private View.OnClickListener startCalendarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            showCalendar(startDateDisplay, startDateListener);

        }
    };

    private View.OnClickListener endCalendarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            showCalendar(endDateDisplay, endDateListener);

        }
    };

    private View.OnFocusChangeListener startCalendarChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                showCalendar(startDateDisplay, startDateListener);

            }
        }
    };

    private void showCalender(EditText view, DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar cal = dateStringToCalendarDate(view.getText().toString());

    }

    private View.OnFocusChangeListener endCalendarChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {

                showCalendar(endDateDisplay,endDateListener);
            }
        }
    };

    private void showCalendar(EditText view, DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar cal = dateStringToCalendarDate(view.getText().toString());

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                SearchActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateSetListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(Color.TRANSPARENT)));
        dialog.show();
    }

    private void setDate(final EditText editText, int year, int month, int day) {
        month++;
        final String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month, day);
        Calendar cal = dateStringToCalendarDate(editText.getText().toString());
        TimePickerDialog subDialog = new TimePickerDialog(
                editText.getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int min) {
                        String datetime = String.format(Locale.getDefault(), "%s %02d:%02d:00", date, hour, min);
                        editText.setText(datetime);
                    }
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
        );
        subDialog.show();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        startDateDisplay = (EditText) findViewById(R.id.etFromDateTime);
        endDateDisplay = (EditText) findViewById(R.id.etToDateTime);

        startDateDisplay.setOnClickListener(startCalendarClickListener);
        startDateDisplay.setOnFocusChangeListener(startCalendarChangeListener);

        endDateDisplay.setOnClickListener(endCalendarClickListener);
        endDateDisplay.setOnFocusChangeListener(endCalendarChangeListener);

        try {
            Calendar calendar = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd");
            Date now = calendar.getTime();
            String todayStr = new SimpleDateFormat("yyyy‐MM‐dd", Locale.getDefault()).format(now);
            Date today = format.parse((String) todayStr);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            String tomorrowStr = new SimpleDateFormat("yyyy‐MM‐dd", Locale.getDefault()).format( calendar.getTime());
            Date tomorrow = format.parse((String) tomorrowStr);
            ((EditText) findViewById(R.id.etFromDateTime)).setText(new SimpleDateFormat(
                    "yyyy‐MM‐dd HH:mm:ss", Locale.getDefault()).format(today));
            ((EditText) findViewById(R.id.etToDateTime)).setText(new SimpleDateFormat(
                    "yyyy‐MM‐dd HH:mm:ss", Locale.getDefault()).format(tomorrow));
        } catch (Exception ex) { }
    }
    public void cancel(final View v) {
        finish();
    }
    public void go(final View v) {
        Intent i = new Intent();
        Double lon;
        Double lat;
        EditText from = (EditText) findViewById(R.id.etFromDateTime);
        EditText to = (EditText) findViewById(R.id.etToDateTime);
        EditText keywords = (EditText) findViewById(R.id.etKeywords);
        EditText longitude = (EditText) findViewById(R.id.etLongitude);
        EditText latitude = (EditText) findViewById(R.id.etLatitude);
        i.putExtra("STARTTIMESTAMP", from.getText() != null ? from.getText().toString() : "");
        i.putExtra("ENDTIMESTAMP", to.getText() != null ? to.getText().toString() : "");
        i.putExtra("KEYWORDS", keywords.getText() != null ? keywords.getText().toString() : "");
        lon = longitude.getText() != null ? Double.valueOf(longitude.getText().toString()) : 0;
        lat = latitude.getText() != null ? Double.valueOf(latitude.getText().toString()) : 0;
        i.putExtra("LONGITUDE", lon);
        i.putExtra("LATITUDE", lat);
        setResult(RESULT_OK, i);
        finish();
    }
}
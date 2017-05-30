package io.phoenyx.sail;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class AddTimelineEventActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private EditText timelineEventTitleEditText;
    private EditText timelineEventDescriptionEditText;
    private TextView timelineEventDateTextView;
    private String[] months;
    private SharedPreferences sharedPreferences;

    private String originalDate;
    private int year, month, day, finalSelectedYear, finalSelectedMonth, finalSelectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timeline_event);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("New Event");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHandler = new DBHandler(this);
        sharedPreferences = getSharedPreferences("io.phoenyx.sail", MODE_PRIVATE);

        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        finalSelectedYear = year;
        finalSelectedMonth = month;
        finalSelectedDay = day;
        months = new String[]{"Jan.","Feb.","Mar.","Apr.","May","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};

        timelineEventTitleEditText = (EditText) findViewById(R.id.timelineEventTitleEditText);
        timelineEventDescriptionEditText = (EditText) findViewById(R.id.timelineEventDescriptionEditText);
        timelineEventDateTextView = (TextView) findViewById(R.id.timelineEventDateTextView);

        originalDate = months[month] + " " + day + " " + year;
        timelineEventDateTextView.setText(originalDate);
        timelineEventDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long timeSince1970 = System.currentTimeMillis();

                DatePickerDialog dialog = new DatePickerDialog(AddTimelineEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        timelineEventDateTextView.setText(months[selectedMonth] + " " + selectedDay + " " + selectedYear);
                        finalSelectedDay = selectedDay;
                        finalSelectedMonth = selectedMonth;
                        finalSelectedYear = selectedYear;
                    }
                }, year, month, day);

                dialog.getDatePicker().setMaxDate(timeSince1970);
                dialog.setTitle("");
                dialog.show();
            }
        });
    }

    private boolean detectChanges() {
        return !(timelineEventTitleEditText.getText().toString().equals("") &&
                originalDate.equals(timelineEventDateTextView.getText().toString()) &&
                timelineEventDescriptionEditText.getText().toString().equals(""));
    }

    private void discard() {
        if (sharedPreferences.getBoolean("notifyBeforeDiscard", true) && detectChanges()) {
            AlertDialog.Builder notifyBeforeDiscardDB = new AlertDialog.Builder(this);
            LayoutInflater layoutInflater = this.getLayoutInflater();

            @SuppressWarnings("InflateParams")
            View discardDialogView = layoutInflater.inflate(R.layout.discard_dialog, null);
            notifyBeforeDiscardDB.setTitle("Discard Changes?").setView(discardDialogView);

            final CheckBox dontRemindCheckBox = (CheckBox) discardDialogView.findViewById(R.id.dontRemindCheckBox);

            notifyBeforeDiscardDB.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dontRemindCheckBox.isChecked()) {
                        sharedPreferences.edit().putBoolean("notifyBeforeDiscard", false).apply();
                    }

                    dialog.dismiss();
                    finish();
                }
            });

            notifyBeforeDiscardDB.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            notifyBeforeDiscardDB.show();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        discard();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.done:
                if (timelineEventTitleEditText.getText().toString().isEmpty() || timelineEventTitleEditText.getText().toString().equals("") || timelineEventTitleEditText.getText().toString().replace(" ", "").equals("")) {
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(timelineEventTitleEditText.getWindowToken(), 0);

                    Snackbar.make(findViewById(android.R.id.content), "Event must have a title", Snackbar.LENGTH_SHORT).show();
                    break;
                }

                TimelineEvent timelineEvent = new TimelineEvent(timelineEventTitleEditText.getText().toString(), finalSelectedMonth + 1, finalSelectedDay, finalSelectedYear, timelineEventDescriptionEditText.getText().toString());
                dbHandler.createTimelineEvent(timelineEvent);
                finish();
                break;
            default:
                discard();
        }


        return super.onOptionsItemSelected(item);
    }
}

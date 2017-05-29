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

import java.util.Arrays;

public class EditTimelineEventActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private SharedPreferences sharedPreferences;
    private EditText timelineEventTitleEditText, timelineEventDescriptionEditText;
    private TextView timelineEventDateTextView;
    private AlertDialog.Builder notifyBeforeDiscardDB, notifyBeforeDeleteDB;
    private String[] months;
    private TimelineEvent timelineEvent;
    private int timelineEventID, year, month, day, finalSelectedYear, finalSelectedMonth, finalSelectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timeline_event);

        Bundle extras = getIntent().getExtras();
        timelineEventID = extras.getInt("timeline_event_id");

        getSupportActionBar().setTitle("Edit Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = new DBHandler(this);
        sharedPreferences = getSharedPreferences("io.phoenyx.sail", MODE_PRIVATE);
        timelineEvent = dbHandler.getTimelineEvent(timelineEventID);

        months = new String[]{"Jan.","Feb.","Mar.","Apr.","May","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};

        timelineEventTitleEditText = (EditText) findViewById(R.id.timelineEventTitleEditText);
        timelineEventDescriptionEditText = (EditText) findViewById(R.id.timelineEventDescriptionEditText);
        timelineEventDateTextView = (TextView) findViewById(R.id.timelineEventDateTextView);

        timelineEventTitleEditText.setText(timelineEvent.getTitle());
        timelineEventDescriptionEditText.setText(timelineEvent.getDescription());
        timelineEventDateTextView.setText(timelineEvent.getDate());
        finalSelectedYear = timelineEvent.getYear();
        finalSelectedMonth = timelineEvent.getMonth();
        finalSelectedDay = timelineEvent.getDay();

        timelineEventDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long timeSince1970 = System.currentTimeMillis();

                DatePickerDialog dialog = new DatePickerDialog(EditTimelineEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        timelineEventDateTextView.setText(months[selectedMonth] + " " + selectedDay + " " + selectedYear);
                        finalSelectedYear = selectedYear;
                        finalSelectedDay = selectedDay;
                        finalSelectedMonth = selectedMonth + 1;
                    }
                }, year, month, day);

                dialog.getDatePicker().setMaxDate(timeSince1970);
                String[] dateParams = timelineEvent.getDate().split(" ");
                dialog.updateDate(Integer.parseInt(dateParams[2]), Arrays.asList(months).indexOf(dateParams[0]), Integer.parseInt(dateParams[1]));
                dialog.setTitle("");
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete:
                delete();
                break;
            case R.id.save:
                save();
                break;
            default:
                discard();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        discard();

    }

    private void save(){
        if (timelineEventTitleEditText.getText().toString().isEmpty() || timelineEventTitleEditText.getText().toString().equals("") || timelineEventTitleEditText.getText().toString().replace(" ", "").equals("")) {
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(timelineEventTitleEditText.getWindowToken(), 0);

            Snackbar.make(findViewById(android.R.id.content), "Event must have a title", Snackbar.LENGTH_SHORT).show();
            return;
        }

        TimelineEvent newTimelineEvent = new TimelineEvent(timelineEventID, timelineEventTitleEditText.getText().toString(), finalSelectedMonth, finalSelectedDay, finalSelectedYear, timelineEventDescriptionEditText.getText().toString());
        dbHandler.updateTimelineEvent(newTimelineEvent);
        finish();
    }

    private boolean detectChanges() {
        return !(timelineEvent.getTitle().equals(timelineEventTitleEditText.getText().toString()) && timelineEvent.getDate().equals(timelineEventDateTextView.getText().toString()) && timelineEvent.getDescription().equals(timelineEventDescriptionEditText.getText().toString()));
    }

    private void discard(){
        if (sharedPreferences.getBoolean("notifyBeforeDiscard", true) && detectChanges()) {
            notifyBeforeDiscardDB = new AlertDialog.Builder(this);
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

    private void delete() {
        if (sharedPreferences.getBoolean("notifyBeforeDelete", true)) {
            notifyBeforeDeleteDB = new AlertDialog.Builder(this);
            LayoutInflater layoutInflater = this.getLayoutInflater();

            @SuppressWarnings("InflateParams")
            View deleteDialogView = layoutInflater.inflate(R.layout.discard_dialog, null);

            notifyBeforeDeleteDB.setTitle("Delete Goal?").setView(deleteDialogView);

            final CheckBox dontRemindCheckBox = (CheckBox) deleteDialogView.findViewById(R.id.dontRemindCheckBox);

            notifyBeforeDeleteDB.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dontRemindCheckBox.isChecked()) {
                        sharedPreferences.edit().putBoolean("notifyBeforeDelete", false).apply();

                    }
                    dbHandler.deleteTimelineEvent(timelineEventID);
                    finish();
                }
            });

            notifyBeforeDeleteDB.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            notifyBeforeDeleteDB.show();
        } else {
            dbHandler.deleteTimelineEvent(timelineEventID);
            finish();
        }

    }
}

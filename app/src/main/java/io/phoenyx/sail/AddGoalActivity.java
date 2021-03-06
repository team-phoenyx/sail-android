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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class AddGoalActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private SharedPreferences sharedPreferences;

    private EditText goalTitleEditText, goalDescriptionEditText;
    private CheckBox goalLongTermCheckBox, goalNotificationCheckBox;
    private TextView goalDateTextView, goalNotifDateTextView;

    private String[] months;
    private String originalDate;
    private int year, month, day, notifYear, notifMonth, notifDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("New Goal");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHandler = new DBHandler(this);
        sharedPreferences = getSharedPreferences("io.phoenyx.sail", MODE_PRIVATE);

        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        months = new String[]{"Jan.","Feb.","Mar.","Apr.","May","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};

        goalTitleEditText = (EditText) findViewById(R.id.goalTitleEditText);
        goalDescriptionEditText = (EditText) findViewById(R.id.goalDescriptionEditText);
        goalDateTextView = (TextView) findViewById(R.id.goalDateTextView);
        goalNotifDateTextView = (TextView) findViewById(R.id.goalNotificationDateTextView);
        goalLongTermCheckBox = (CheckBox) findViewById(R.id.goalLongTermCheckBox);
        goalNotificationCheckBox = (CheckBox) findViewById(R.id.goalNotifyCheckBox);

        goalNotifDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goalNotificationCheckBox.isChecked()) {
                    long timeSince1970 = System.currentTimeMillis();

                    DatePickerDialog dialog = new DatePickerDialog(AddGoalActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                            goalNotifDateTextView.setText(months[selectedMonth] + " " + selectedDay + " " + selectedYear);
                            notifYear = selectedYear;
                            notifMonth = selectedMonth + 1;
                            notifDay = selectedDay;
                        }
                    }, year, month, day);

                    dialog.getDatePicker().setMinDate(timeSince1970);
                    dialog.setTitle("");
                    dialog.show();
                }
            }
        });

        goalNotificationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    long timeSince1970 = System.currentTimeMillis();

                    DatePickerDialog dialog = new DatePickerDialog(AddGoalActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                            goalNotifDateTextView.setText(months[selectedMonth] + " " + selectedDay + " " + selectedYear);
                            notifYear = selectedYear;
                            notifMonth = selectedMonth + 1;
                            notifDay = selectedDay;
                        }
                    }, year, month, day);

                    dialog.getDatePicker().setMinDate(timeSince1970);
                    dialog.setTitle("");
                    dialog.show();
                } else {
                    goalNotifDateTextView.setText(getResources().getString(R.string.no_notif_label));
                }
            }
        });

        goalLongTermCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    goalDateTextView.setText(getResources().getString(R.string.long_term_due_label));
                } else {
                    goalDateTextView.setText(months[month] + " " + day + " " + year);
                }
            }
        });

        originalDate = months[month] + " " + day + " " + year;
        goalDateTextView.setText(originalDate);
        goalDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!goalLongTermCheckBox.isChecked()) {
                    long timeSince1970 = System.currentTimeMillis();

                    DatePickerDialog dialog = new DatePickerDialog(AddGoalActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                            goalDateTextView.setText(months[selectedMonth] + " " + selectedDay + " " + selectedYear);
                        }
                    }, year, month, day);

                    dialog.getDatePicker().setMinDate(timeSince1970);
                    dialog.setTitle("");
                    dialog.show();
                }
            }
        });
    }

    private boolean detectChanges() {
        return !(goalTitleEditText.getText().toString().equals("") &&
                originalDate.equals(goalDateTextView.getText().toString()) &&
                goalNotifDateTextView.getText().toString().equals("No notification") &&
                goalDescriptionEditText.getText().toString().equals(""));
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
                if (goalTitleEditText.getText().toString().isEmpty() || goalTitleEditText.getText().toString().equals("") || goalTitleEditText.getText().toString().replace(" ", "").equals("")) {
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(goalDescriptionEditText.getWindowToken(), 0);
                    mgr.hideSoftInputFromWindow(goalTitleEditText.getWindowToken(), 0);

                    Snackbar.make(findViewById(android.R.id.content), "Goal must have a title", Snackbar.LENGTH_SHORT).show();
                    break;
                }

                Goal goal = new Goal(goalTitleEditText.getText().toString(), goalDescriptionEditText.getText().toString(), goalDateTextView.getText().toString(), false, false, "");
                int goalID = dbHandler.createGoal(goal);
                goal.setId(goalID);

                if (notifDay != 0 && notifMonth != 0 && notifYear != 0 && goalNotificationCheckBox.isChecked()) {
                    NotificationBuilder builder = new NotificationBuilder(this, notifMonth, notifDay, notifYear, "Upcoming Goal", goalTitleEditText.getText().toString(), goalID, "goal");
                    builder.buildNotification();
                    goal.setNotify(months[notifMonth - 1] + " " + notifDay + " " + notifYear);
                    dbHandler.updateGoal(goal);
                }

                finish();
                break;

            default:
                discard();
        }


        return super.onOptionsItemSelected(item);
    }
}

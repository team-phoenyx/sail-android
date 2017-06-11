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

import java.util.Arrays;
import java.util.Calendar;

public class EditGoalActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private SharedPreferences sharedPreferences;
    private EditText goalTitleEditText, goalDescriptionEditText;
    private CheckBox goalLongTermCheckBox, goalNotificationCheckBox;
    private TextView goalDateTextView, goalNotifDateTextView;
    private String[] months;
    private int goalID, year, month, day, notifDay, notifMonth, notifYear;
    private Goal goal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        Bundle extras = getIntent().getExtras();
        goalID = extras.getInt("goal_id");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Goal");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHandler = new DBHandler(this);
        sharedPreferences = getSharedPreferences("io.phoenyx.sail", MODE_PRIVATE);
        goal = dbHandler.getGoal(goalID);

        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        months = new String[]{"Jan.","Feb.","Mar.","Apr.","May","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};

        goalTitleEditText = (EditText) findViewById(R.id.goalTitleEditText);
        goalDescriptionEditText = (EditText) findViewById(R.id.goalDescriptionEditText);
        goalDateTextView = (TextView) findViewById(R.id.goalDateTextView);
        goalLongTermCheckBox = (CheckBox) findViewById(R.id.goalLongTermCheckBox);
        goalNotifDateTextView = (TextView) findViewById(R.id.goalNotificationDateTextView);
        goalNotificationCheckBox = (CheckBox) findViewById(R.id.goalNotifyCheckBox);

        goalNotifDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goalNotificationCheckBox.isChecked()) {
                    long timeSince1970 = System.currentTimeMillis();

                    DatePickerDialog dialog = new DatePickerDialog(EditGoalActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        goalNotificationCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goalNotificationCheckBox.isChecked()) {
                    long timeSince1970 = System.currentTimeMillis();

                    DatePickerDialog dialog = new DatePickerDialog(EditGoalActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                            goalNotifDateTextView.setText(months[selectedMonth] + " " + selectedDay + " " + selectedYear);
                            notifYear = selectedYear;
                            notifMonth = selectedMonth + 1;
                            notifDay = selectedDay;
                        }
                    }, year, month, day);

                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            goalNotificationCheckBox.setChecked(false);
                            goalNotifDateTextView.setText(getResources().getString(R.string.no_notif_label));

                        }
                    });

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

        goalTitleEditText.setText(goal.getTitle());
        goalDescriptionEditText.setText(goal.getDescription());
        goalDateTextView.setText(goal.getDate());

        goalDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!goalLongTermCheckBox.isChecked()) {
                    long timeSince1970 = System.currentTimeMillis();

                    DatePickerDialog dialog = new DatePickerDialog(EditGoalActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                            goalDateTextView.setText(months[selectedMonth] + " " + selectedDay + " " + selectedYear);
                        }
                    }, year, month, day);

                    dialog.getDatePicker().setMinDate(timeSince1970);


                    String[] dateParams = goalDateTextView.getText().toString().split(" ");
                    dialog.updateDate(Integer.parseInt(dateParams[2]), Arrays.asList(months).indexOf(dateParams[0]), Integer.parseInt(dateParams[1]));
                    dialog.setTitle("");
                    dialog.show();
                }
            }
        });

        if (goal.getDate().equals("Long term")) {
            goalLongTermCheckBox.setChecked(true);
        }

        if (!goal.getNotify().equals("")) {
            String notifyString = goal.getNotify();
            goalNotificationCheckBox.setChecked(true);
            goalNotifDateTextView.setText(notifyString);
            String[] params = notifyString.split(" ");
            notifDay = Integer.parseInt(params[1]);
            notifYear = Integer.parseInt(params[2]);
            notifMonth = getArrayIndex(months, params[0]) + 1;
        }
    }

    public int getArrayIndex(String[] arr, String value) {
        for(int i = 0; i < arr.length; i++) {
            if (arr[i].equals(value)) return i;
        }
        return -1;
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
        if (goalTitleEditText.getText().toString().isEmpty() || goalTitleEditText.getText().toString().equals("") || goalTitleEditText.getText().toString().replace(" ", "").equals("")) {
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(goalDescriptionEditText.getWindowToken(), 0);
            mgr.hideSoftInputFromWindow(goalTitleEditText.getWindowToken(), 0);

            Snackbar.make(findViewById(android.R.id.content), "Goal must have a title", Snackbar.LENGTH_SHORT).show();
            return;
        }

        Goal newGoal = new Goal(goal.getId(), goalTitleEditText.getText().toString(), goalDescriptionEditText.getText().toString(), goalDateTextView.getText().toString(), goal.isStarred(), goal.isCompleted(), "");
        dbHandler.updateGoal(newGoal);

        NotificationBuilder notificationBuilder = new NotificationBuilder(EditGoalActivity.this, newGoal.getId());
        notificationBuilder.deleteNotification();

        if (notifDay != 0 && notifMonth != 0 && notifYear != 0 && goalNotificationCheckBox.isChecked()) {
            NotificationBuilder builder = new NotificationBuilder(this, notifMonth, notifDay, notifYear, "Upcoming Goal", goalTitleEditText.getText().toString(), goalID, "goal");
            builder.buildNotification();
            newGoal.setNotify(months[notifMonth - 1] + " " + notifDay + " " + notifYear);
            dbHandler.updateGoal(newGoal);
        }

        finish();
    }

    private boolean detectChanges() {
        return !(goal.getTitle().equals(goalTitleEditText.getText().toString()) &&
                goal.getDate().equals(goalDateTextView.getText().toString()) &&
                (goal.getNotify().equals(goalNotifDateTextView.getText().toString()) || (goal.getNotify().equals("") && goalNotifDateTextView.getText().toString().equals("No notification"))) &&
                goal.getDescription().equals(goalDescriptionEditText.getText().toString()));
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

    private void delete() {
        if (sharedPreferences.getBoolean("notifyBeforeDelete", true)) {
            AlertDialog.Builder notifyBeforeDeleteDB = new AlertDialog.Builder(this);
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
                    NotificationBuilder notificationBuilder = new NotificationBuilder(EditGoalActivity.this, goalID);
                    notificationBuilder.deleteNotification();

                    dbHandler.deleteGoal(goalID);
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
            NotificationBuilder notificationBuilder = new NotificationBuilder(EditGoalActivity.this, goalID);
            notificationBuilder.deleteNotification();

            dbHandler.deleteGoal(goalID);
            finish();
        }

    }

}

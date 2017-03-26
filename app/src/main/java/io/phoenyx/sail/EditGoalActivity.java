package io.phoenyx.sail;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;

public class EditGoalActivity extends AppCompatActivity {

    DBHandler dbHandler;
    EditText goalTitleEditText, goalDescriptionEditText;
    CheckBox goalLongTermCheckBox, goalNotificationCheckBox;
    TextView goalDateTextView, goalNotifDateTextView;
    String[] months;
    int goalID, year, month, day, notifDay, notifMonth, notifYear;
    Goal goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        Bundle extras = getIntent().getExtras();
        goalID = extras.getInt("goal_id");

        getSupportActionBar().setTitle("Edit Goal");

        dbHandler = new DBHandler(this);
        goal = dbHandler.getGoal(goalID);

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
        });

        goalNotificationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
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
                } else {
                    goalNotifDateTextView.setText("No notification");
                }
            }
        });

        goalLongTermCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    goalDateTextView.setText("Long term");
                } else {
                    goalDateTextView.setText(goal.getDate());
                }
            }
        });

        goalTitleEditText.setText(goal.getTitle());
        goalDescriptionEditText.setText(goal.getDescription());
        goalDateTextView.setText(goal.getDate());

        goalDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long timeSince1970 = System.currentTimeMillis();

                DatePickerDialog dialog = new DatePickerDialog(EditGoalActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        goalDateTextView.setText(months[selectedMonth] + " " + selectedDay + " " + selectedYear);
                    }
                }, year, month, day);

                dialog.getDatePicker().setMinDate(timeSince1970);

                String[] dateParams = goal.getDate().split(" ");
                dialog.updateDate(Integer.parseInt(dateParams[2]), Arrays.asList(months).indexOf(dateParams[0]), Integer.parseInt(dateParams[1]));
                dialog.setTitle("");
                dialog.show();
            }
        });

        if (goal.getDate().equals("Long term")) {
            goalLongTermCheckBox.setChecked(true);
        }
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
                dbHandler.deleteGoal(goalID);
                finish();
                break;
            default:
                Snackbar.make(findViewById(android.R.id.content), "Please try again", BaseTransientBottomBar.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Goal newGoal = new Goal(goal.getId(), goalTitleEditText.getText().toString(), goalDescriptionEditText.getText().toString(), goalDateTextView.getText().toString(), goal.isStarred(), goal.isCompleted(), "");
        dbHandler.updateGoal(newGoal);

        NotificationBuilder notificationBuilder = new NotificationBuilder(EditGoalActivity.this, newGoal.getId());
        notificationBuilder.deleteNotification();

        if (notifDay != 0 && notifMonth != 0 && notifYear != 0 && goalNotificationCheckBox.isChecked()) {
            NotificationBuilder builder = new NotificationBuilder(this, notifMonth, notifDay, notifYear, "Upcoming Goal", goalTitleEditText.getText().toString(), goalID);
            builder.buildNotification();
            newGoal.setNotify(notifMonth + "-" + notifDay + "-" + notifYear);
            dbHandler.updateGoal(newGoal);
        }

        finish();
    }
}

package io.phoenyx.sail;

import android.app.DatePickerDialog;
import android.os.Bundle;
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

import java.util.Calendar;

public class AddPromiseActivity extends AppCompatActivity {

    DBHandler dbHandler;
    EditText promiseTitleEditText, promiseDescriptionEditText, promisePersonEditText;
    CheckBox promiseLongTermCheckBox, promiseNotificationCheckBox;
    TextView promiseDateTextView, promiseNotifDateTextView;
    String[] months;

    int year, month, day, notifYear, notifMonth, notifDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promise);

        getSupportActionBar().setTitle("New Promise");

        dbHandler = new DBHandler(this);

        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        months = new String[]{"Jan.","Feb.","Mar.","Apr.","May","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};

        promiseTitleEditText = (EditText) findViewById(R.id.promiseTitleEditText);
        promiseDescriptionEditText = (EditText) findViewById(R.id.promiseDescriptionEditText);
        promisePersonEditText = (EditText) findViewById(R.id.promisePersonEditText);
        promiseDateTextView = (TextView) findViewById(R.id.promiseDateTextView);
        promiseLongTermCheckBox = (CheckBox) findViewById(R.id.promiseLongTermCheckBox);
        promiseNotifDateTextView = (TextView) findViewById(R.id.promiseNotificationDateTextView);
        promiseNotificationCheckBox = (CheckBox) findViewById(R.id.promiseNotifyCheckBox);

        promiseNotifDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long timeSince1970 = System.currentTimeMillis();

                DatePickerDialog dialog = new DatePickerDialog(AddPromiseActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        promiseNotifDateTextView.setText(months[selectedMonth] + " " + selectedDay + " " + selectedYear);
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

        promiseNotificationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    long timeSince1970 = System.currentTimeMillis();

                    DatePickerDialog dialog = new DatePickerDialog(AddPromiseActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                            promiseNotifDateTextView.setText(months[selectedMonth] + " " + selectedDay + " " + selectedYear);
                            notifYear = selectedYear;
                            notifMonth = selectedMonth + 1;
                            notifDay = selectedDay;
                        }
                    }, year, month, day);

                    dialog.getDatePicker().setMinDate(timeSince1970);
                    dialog.setTitle("");
                    dialog.show();
                } else {
                    promiseNotifDateTextView.setText("No notification");
                }
            }
        });

        promiseLongTermCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    promiseDateTextView.setText("Long term");
                } else {
                    promiseDateTextView.setText(months[month] + " " + day + " " + year);
                }
            }
        });

        promiseDateTextView.setText(months[month] + " " + day + " " + year);
        promiseDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long timeSince1970 = System.currentTimeMillis();

                DatePickerDialog dialog = new DatePickerDialog(AddPromiseActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        promiseDateTextView.setText(months[selectedMonth] + " " + selectedDay + " " + selectedYear);
                    }
                }, year, month, day);

                dialog.getDatePicker().setMinDate(timeSince1970);
                dialog.setTitle("");
                dialog.show();
            }
        });
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
                Promise promise = new Promise(promiseTitleEditText.getText().toString(), promiseDescriptionEditText.getText().toString(), promiseDateTextView.getText().toString(), promisePersonEditText.getText().toString(), false, false, "");
                int promiseID = dbHandler.createPromise(promise);

                if (notifDay != 0 && notifMonth != 0 && notifYear != 0 && promiseNotificationCheckBox.isChecked()) {
                    NotificationBuilder builder = new NotificationBuilder(this, notifMonth, notifDay, notifYear, "Upcoming Promise", promiseTitleEditText.getText().toString(), promiseID);
                    builder.buildNotification();
                    promise.setNotify(months[notifMonth - 1] + " " + notifDay + " " + notifYear);
                    dbHandler.updatePromise(promise);
                }

                finish();
                break;
            default:
                Snackbar.make(findViewById(android.R.id.content), "Please try again", Snackbar.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }
}

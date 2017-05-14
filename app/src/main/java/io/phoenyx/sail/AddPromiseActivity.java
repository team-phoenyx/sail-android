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

public class AddPromiseActivity extends AppCompatActivity {

    DBHandler dbHandler;
    EditText promiseTitleEditText, promiseDescriptionEditText, promisePersonEditText;
    CheckBox promiseLongTermCheckBox, promiseNotificationCheckBox;
    TextView promiseDateTextView, promiseNotifDateTextView;
    String[] months;
    AlertDialog.Builder notifyBeforeDiscardDB;
    SharedPreferences sharedPreferences;

    String originalDate;
    int year, month, day, notifYear, notifMonth, notifDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promise);

        getSupportActionBar().setTitle("New Promise");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = new DBHandler(this);
        sharedPreferences = getSharedPreferences("io.phoenyx.sail", MODE_PRIVATE);

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
                if (promiseNotificationCheckBox.isChecked()) {
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

        originalDate = months[month] + " " + day + " " + year;
        promiseDateTextView.setText(originalDate);
        promiseDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!promiseLongTermCheckBox.isChecked()) {
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
            }
        });
    }

    private boolean detectChanges() {
        return !(promiseTitleEditText.getText().toString().equals("") &&
                promisePersonEditText.getText().toString().equals("") &&
                originalDate.equals(promiseDateTextView.getText().toString()) &&
                promiseDescriptionEditText.getText().toString().equals(""));
    }

    private void discard() {
        if (sharedPreferences.getBoolean("notifyBeforeDiscard", true) && detectChanges()) {
            notifyBeforeDiscardDB = new AlertDialog.Builder(this);
            LayoutInflater layoutInflater = this.getLayoutInflater();
            View discardDialogView = layoutInflater.inflate(R.layout.discard_dialog, null);
            notifyBeforeDiscardDB.setTitle("Discard Changes?").setView(discardDialogView);

            final CheckBox dontRemindCheckBox = (CheckBox) discardDialogView.findViewById(R.id.dontRemindCheckBox);

            notifyBeforeDiscardDB.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dontRemindCheckBox.isChecked()) {
                        sharedPreferences.edit().putBoolean("notifyBeforeDiscard", false).commit();
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
                if (promiseTitleEditText.getText().toString().isEmpty() || promiseTitleEditText.getText().toString().equals("") || promiseTitleEditText.getText().toString().replace(" ", "").equals("")) {
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(promiseTitleEditText.getWindowToken(), 0);
                    mgr.hideSoftInputFromWindow(promiseDescriptionEditText.getWindowToken(), 0);
                    mgr.hideSoftInputFromWindow(promisePersonEditText.getWindowToken(), 0);

                    Snackbar.make(findViewById(android.R.id.content), "Promise must have a title", Snackbar.LENGTH_SHORT).show();
                    break;
                }

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
                discard();
        }


        return super.onOptionsItemSelected(item);
    }
}

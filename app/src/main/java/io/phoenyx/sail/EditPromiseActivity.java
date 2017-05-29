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

public class EditPromiseActivity extends AppCompatActivity {

    DBHandler dbHandler;
    SharedPreferences sharedPreferences;
    EditText promiseTitleEditText, promiseDescriptionEditText, promisePersonEditText;
    CheckBox promiseLongTermCheckBox, promiseNotificationCheckBox;
    TextView promiseDateTextView, promiseNotifDateTextView;
    AlertDialog.Builder notifyBeforeDiscardDB, notifyBeforeDeleteDB;
    int promiseID, notifYear, notifMonth, notifDay;
    Promise promise;
    String[] months;

    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promise);

        Bundle extras = getIntent().getExtras();
        promiseID = extras.getInt("promise_id");

        getSupportActionBar().setTitle("Edit Promise");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = new DBHandler(this);
        sharedPreferences = getSharedPreferences("io.phoenyx.sail", MODE_PRIVATE);
        promise = dbHandler.getPromise(promiseID);

        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        months = new String[]{"Jan.","Feb.","Mar.","Apr.","May","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};

        promiseTitleEditText = (EditText) findViewById(R.id.promiseTitleEditText);
        promiseDescriptionEditText = (EditText) findViewById(R.id.promiseDescriptionEditText);
        promisePersonEditText = (EditText) findViewById(R.id.promisePersonEditText);
        promiseDateTextView = (TextView) findViewById(R.id.promiseDateTextView);
        promiseLongTermCheckBox = (CheckBox) findViewById(R.id.promiseLongTermCheckBox);
        promiseNotificationCheckBox = (CheckBox) findViewById(R.id.promiseNotifyCheckBox);
        promiseNotifDateTextView = (TextView) findViewById(R.id.promiseNotificationDateTextView);

        promiseTitleEditText.setText(promise.getTitle());
        promiseDescriptionEditText.setText(promise.getDescription());
        promiseDateTextView.setText(promise.getDate());
        promisePersonEditText.setText(promise.getPerson());

        if (!promise.getNotify().equals("")) {
            String notifyString = promise.getNotify();
            promiseNotificationCheckBox.setChecked(true);
            promiseNotifDateTextView.setText(notifyString);
            String[] params = notifyString.split(" ");
            notifDay = Integer.parseInt(params[1]);
            notifYear = Integer.parseInt(params[2]);
            notifMonth = getArrayIndex(months, params[0]) + 1;
        }

        if (promise.getDate().equals("Long term")) {
            promiseLongTermCheckBox.setChecked(true);
        }

        promiseNotifDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (promiseNotificationCheckBox.isChecked()) {
                    long timeSince1970 = System.currentTimeMillis();

                    DatePickerDialog dialog = new DatePickerDialog(EditPromiseActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        promiseNotificationCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (promiseNotificationCheckBox.isChecked()) {
                    long timeSince1970 = System.currentTimeMillis();

                    DatePickerDialog dialog = new DatePickerDialog(EditPromiseActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        promiseDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!promiseLongTermCheckBox.isChecked()) {
                    long timeSince1970 = System.currentTimeMillis();

                    DatePickerDialog dialog = new DatePickerDialog(EditPromiseActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                            promiseDateTextView.setText(months[selectedMonth] + " " + selectedDay + " " + selectedYear);
                        }
                    }, year, month, day);

                    dialog.getDatePicker().setMinDate(timeSince1970);
                    String[] dateParams = promiseDateTextView.getText().toString().split(" ");
                    dialog.updateDate(Integer.parseInt(dateParams[2]), Arrays.asList(months).indexOf(dateParams[0]), Integer.parseInt(dateParams[1]));
                    dialog.setTitle("");
                    dialog.show();
                }
            }
        });
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
        if (promiseTitleEditText.getText().toString().isEmpty() || promiseTitleEditText.getText().toString().equals("") || promiseTitleEditText.getText().toString().replace(" ", "").equals("")) {
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(promiseTitleEditText.getWindowToken(), 0);
            mgr.hideSoftInputFromWindow(promiseDescriptionEditText.getWindowToken(), 0);
            mgr.hideSoftInputFromWindow(promisePersonEditText.getWindowToken(), 0);

            Snackbar.make(findViewById(android.R.id.content), "Promise must have a title", Snackbar.LENGTH_SHORT).show();
            return;
        }

        Promise newPromise = new Promise(promiseID, promiseTitleEditText.getText().toString(), promiseDescriptionEditText.getText().toString(), promiseDateTextView.getText().toString(), promisePersonEditText.getText().toString(), false, false, "");
        dbHandler.updatePromise(newPromise);

        NotificationBuilder notificationBuilder = new NotificationBuilder(EditPromiseActivity.this, newPromise.getId());
        notificationBuilder.deleteNotification();

        if (notifDay != 0 && notifMonth != 0 && notifYear != 0 && promiseNotificationCheckBox.isChecked()) {
            NotificationBuilder builder = new NotificationBuilder(this, notifMonth, notifDay, notifYear, "Upcoming promise", promiseTitleEditText.getText().toString(), promiseID);
            builder.buildNotification();
            newPromise.setNotify(months[notifMonth - 1] + " " + notifDay + " " + notifYear);
            dbHandler.updatePromise(newPromise);
        }

        finish();
    }

    private boolean detectChanges() {
        return !(promise.getTitle().equals(promiseTitleEditText.getText().toString()) && promise.getPerson().equals(promisePersonEditText.getText().toString()) && promise.getDate().equals(promiseDateTextView.getText().toString()) && (promise.getNotify().equals(promiseNotifDateTextView.getText().toString()) || (promise.getNotify().equals("") && promiseNotifDateTextView.getText().toString().equals("No notification"))) && promise.getDescription().equals(promiseDescriptionEditText.getText().toString()));
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
                    dbHandler.deletePromise(promiseID);
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
            dbHandler.deletePromise(promiseID);
            finish();
        }

    }
}

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

public class AddAchievementActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private EditText achievementTitleEditText;
    private EditText achievementDescriptionEditText;
    private TextView achievementDateTextView;
    private String[] months;
    private SharedPreferences sharedPreferences;

    private String originalDate;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_achievement);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("New Achievement");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHandler = new DBHandler(this);
        sharedPreferences = getSharedPreferences("io.phoenyx.sail", MODE_PRIVATE);

        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        months = new String[]{"Jan.","Feb.","Mar.","Apr.","May","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};

        achievementTitleEditText = (EditText) findViewById(R.id.achievementTitleEditText);
        achievementDescriptionEditText = (EditText) findViewById(R.id.achievementDescriptionEditText);
        achievementDateTextView = (TextView) findViewById(R.id.achievementDateTextView);

        originalDate = months[month] + " " + day + " " + year;
        achievementDateTextView.setText(originalDate);
        achievementDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long timeSince1970 = System.currentTimeMillis();

                DatePickerDialog dialog = new DatePickerDialog(AddAchievementActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        achievementDateTextView.setText(months[selectedMonth] + " " + selectedDay + " " + selectedYear);
                    }
                }, year, month, day);

                dialog.getDatePicker().setMaxDate(timeSince1970);
                dialog.setTitle("");
                dialog.show();
            }
        });
    }

    private boolean detectChanges() {
        return !(achievementTitleEditText.getText().toString().equals("") &&
                originalDate.equals(achievementDateTextView.getText().toString()) &&
                achievementDescriptionEditText.getText().toString().equals(""));
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
                if (achievementTitleEditText.getText().toString().isEmpty() || achievementTitleEditText.getText().toString().equals("") || achievementTitleEditText.getText().toString().replace(" ", "").equals("")) {
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(achievementDescriptionEditText.getWindowToken(), 0);
                    mgr.hideSoftInputFromWindow(achievementTitleEditText.getWindowToken(), 0);

                    Snackbar.make(findViewById(android.R.id.content), "Achievement must have a title", Snackbar.LENGTH_SHORT).show();
                    break;
                }

                Achievement achievement = new Achievement(achievementTitleEditText.getText().toString(), achievementDescriptionEditText.getText().toString(), achievementDateTextView.getText().toString(), false);
                dbHandler.createAchievement(achievement);
                finish();
                break;
            default:
                discard();
        }


        return super.onOptionsItemSelected(item);
    }
}

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

public class EditAchievementActivity extends AppCompatActivity {

    DBHandler dbHandler;
    SharedPreferences sharedPreferences;
    EditText achievementTitleEditText, achievementDescriptionEditText;
    TextView achievementDateTextView;
    AlertDialog.Builder notifyBeforeDiscardDB, notifyBeforeDeleteDB;
    String[] months;
    int achievementID;
    Achievement achievement;

    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_achievement);

        Bundle extras = getIntent().getExtras();
        achievementID = extras.getInt("achievement_id");

        getSupportActionBar().setTitle("Edit Achievement");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = new DBHandler(this);
        sharedPreferences = getSharedPreferences("io.phoenyx.sail", MODE_PRIVATE);
        achievement = dbHandler.getAchievement(achievementID);

        months = new String[]{"Jan.","Feb.","Mar.","Apr.","May","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};

        achievementTitleEditText = (EditText) findViewById(R.id.achievementTitleEditText);
        achievementDescriptionEditText = (EditText) findViewById(R.id.achievementDescriptionEditText);
        achievementDateTextView = (TextView) findViewById(R.id.achievementDateTextView);

        achievementTitleEditText.setText(achievement.getTitle());
        achievementDescriptionEditText.setText(achievement.getDescription());
        achievementDateTextView.setText(achievement.getDate());

        achievementDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long timeSince1970 = System.currentTimeMillis();

                DatePickerDialog dialog = new DatePickerDialog(EditAchievementActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        achievementDateTextView.setText(months[selectedMonth] + " " + selectedDay + " " + selectedYear);
                    }
                }, year, month, day);

                dialog.getDatePicker().setMaxDate(timeSince1970);
                String[] dateParams = achievement.getDate().split(" ");
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
        if (achievementTitleEditText.getText().toString().isEmpty() || achievementTitleEditText.getText().toString().equals("") || achievementTitleEditText.getText().toString().replace(" ", "").equals("")) {
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(achievementDescriptionEditText.getWindowToken(), 0);
            mgr.hideSoftInputFromWindow(achievementTitleEditText.getWindowToken(), 0);

            Snackbar.make(findViewById(android.R.id.content), "Achievement must have a title", Snackbar.LENGTH_SHORT).show();
            return;
        }


        Achievement newAchievement = new Achievement(achievementID, achievementTitleEditText.getText().toString(), achievementDescriptionEditText.getText().toString(), achievementDateTextView.getText().toString(), false);
        dbHandler.updateAchievement(newAchievement);
        finish();
    }

    private boolean detectChanges() {
        return !(achievement.getTitle().equals(achievementTitleEditText.getText().toString()) && achievement.getDate().equals(achievementDateTextView.getText().toString()) && achievement.getDescription().equals(achievementDescriptionEditText.getText().toString()));
    }

    private void discard(){
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

    private void delete() {
        if (sharedPreferences.getBoolean("notifyBeforeDelete", true)) {
            notifyBeforeDeleteDB = new AlertDialog.Builder(this);
            LayoutInflater layoutInflater = this.getLayoutInflater();
            View deleteDialogView = layoutInflater.inflate(R.layout.discard_dialog, null);

            notifyBeforeDeleteDB.setTitle("Delete Achievement?").setView(deleteDialogView);

            final CheckBox dontRemindCheckBox = (CheckBox) deleteDialogView.findViewById(R.id.dontRemindCheckBox);

            notifyBeforeDeleteDB.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dontRemindCheckBox.isChecked()) {
                        sharedPreferences.edit().putBoolean("notifyBeforeDelete", false).commit();
                    }
                    dbHandler.deleteAchievement(achievementID);
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
            dbHandler.deleteAchievement(achievementID);
            finish();
        }

    }
}

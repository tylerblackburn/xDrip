package com.eveningoutpost.dexdrip;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.Layout;
import android.text.format.DateFormat;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eveningoutpost.dexdrip.Models.AlertType;
import com.eveningoutpost.dexdrip.UtilityModels.AlertPlayer;
import com.eveningoutpost.dexdrip.UtilityModels.BgGraphBuilder;
import com.eveningoutpost.dexdrip.UtilityModels.Constants;
import com.eveningoutpost.dexdrip.R;
import com.eveningoutpost.dexdrip.utils.ActivityWithMenu;

public class EditAlertActivity extends ActivityWithMenu {
    public static String menu_name = "Edit Alert";

    TextView viewHeader;

    EditText alertText;
    EditText alertThreshold;
    EditText alertMp3File;
    EditText editSnooze;

    Button buttonalertMp3;

    Button buttonSave;
    Button buttonRemove;
    Button buttonTest;
    Button buttonPreSnooze;
    CheckBox checkboxAllDay;

    LinearLayout layoutTimeBetween;
    LinearLayout timeInstructions;
    TextView viewTimeStart;
    TextView viewTimeEnd;

    int startHour = 0;
    int startMinute = 0;
    int endHour = 23;
    int endMinute = 59;

    int defaultSnooze;

    String audioPath;

    TextView viewAlertOverrideText;
    CheckBox checkboxAlertOverride;
    Boolean doMgdl;

    String uuid;
    Context mContext;
    boolean above;
    final int CHOOSE_FILE = 1;
    final int MIN_ALERT = 40;
    final int MAX_ALERT = 400;

    private final static String TAG = AlertPlayer.class.getSimpleName();

    String getExtra(Bundle savedInstanceState, String paramName) {
        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString(paramName);
            }
        } else {
            newString= (String) savedInstanceState.getSerializable(paramName);
        }
        return newString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_edit_alert);

        viewHeader = (TextView) findViewById(R.id.view_alert_header);

        buttonSave = (Button)findViewById(R.id.edit_alert_save);
        buttonRemove = (Button)findViewById(R.id.edit_alert_remove);
        buttonTest = (Button)findViewById(R.id.edit_alert_test);
        buttonalertMp3 = (Button)findViewById(R.id.Button_alert_mp3_file);
        buttonPreSnooze = (Button)findViewById(R.id.edit_alert_pre_snooze);


        alertText = (EditText) findViewById(R.id.edit_alert_text);
        alertThreshold = (EditText) findViewById(R.id.edit_alert_threshold);
        alertMp3File = (EditText) findViewById(R.id.edit_alert_mp3_file);

        checkboxAllDay = (CheckBox) findViewById(R.id.check_alert_time);

        layoutTimeBetween = (LinearLayout) findViewById(R.id.time_between);
        timeInstructions = (LinearLayout) findViewById(R.id.time_instructions);
        viewTimeStart = (TextView) findViewById(R.id.view_alert_time_start);
        viewTimeEnd = (TextView) findViewById(R.id.view_alert_time_end);
        editSnooze = (EditText) findViewById(R.id.edit_snooze);

        viewAlertOverrideText = (TextView) findViewById(R.id.view_alert_override_silent);
        checkboxAlertOverride = (CheckBox) findViewById(R.id.check_override_silent);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        addListenerOnButtons();

        if(BgGraphBuilder.isXLargeTablet(getApplicationContext())) {
            viewHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            buttonSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            buttonRemove.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            buttonTest.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            buttonalertMp3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            buttonSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            buttonPreSnooze.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            alertText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            alertThreshold.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            alertMp3File.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

            checkboxAllDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

            viewTimeStart.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            viewTimeEnd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            editSnooze.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            viewAlertOverrideText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

            ((TextView) findViewById(R.id.view_alert_text)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            ((TextView) findViewById(R.id.view_alert_threshold)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            ((TextView) findViewById(R.id.view_alert_default_snooze)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            ((TextView) findViewById(R.id.view_alert_mp3_file)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            ((TextView) findViewById(R.id.view_alert_time)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

        }
        SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        doMgdl = (prefs.getString("units", "mgdl").compareTo("mgdl") == 0);

        if(!doMgdl) {
            alertThreshold.setInputType(InputType.TYPE_CLASS_NUMBER);
            alertThreshold.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            alertThreshold.setKeyListener(DigitsKeyListener.getInstance(false,true));
        }

        uuid = getExtra(savedInstanceState, "uuid");
        String status;
        if (uuid == null) {
            // This is a new alert
            above = Boolean.parseBoolean(getExtra(savedInstanceState, "above"));
            checkboxAllDay.setChecked(true);
            checkboxAlertOverride.setChecked(true);

            audioPath = "";
            alertMp3File.setText(shortPath(audioPath));
            alertMp3File.setKeyListener(null);
            defaultSnooze = SnoozeActivity.getDefaultSnooze(above);
            buttonRemove.setVisibility(View.GONE);
            // One can not snooze an alert that is still not in the database...
            buttonPreSnooze.setVisibility(View.GONE);
            status = "Adding " + (above ? "high" : "low") + " alert";
            startHour = 0;
            startMinute = 0;
            endHour = 23;
            endMinute = 59;

        } else {
            // We are editing an alert
            AlertType at = AlertType.get_alert(uuid);
            if(at==null) {
                Log.wtf(TAG, "Error editing alert, when that alert does not exist...");
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
                return;
            }

            above =at.above;
            alertText.setText(at.name);
            alertThreshold.setText(UnitsConvert2Disp(doMgdl, at.threshold));
            checkboxAllDay.setChecked(at.all_day);
            checkboxAlertOverride.setChecked(at.override_silent_mode);
            defaultSnooze = at.default_snooze;
            if(defaultSnooze == 0) {
                SnoozeActivity.getDefaultSnooze(above);
            }

            audioPath = at.mp3_file;
            alertMp3File.setText(shortPath(audioPath));

            status = "editing " + (above ? "high" : "low") + " alert";
            startHour = AlertType.time2Hours(at.start_time_minutes);
            startMinute = AlertType.time2Minutes(at.start_time_minutes);
            endHour = AlertType.time2Hours(at.end_time_minutes);
            endMinute = AlertType.time2Minutes(at.end_time_minutes);

            if(uuid.equals(AlertType.LOW_ALERT_55)) {
                // This is the 55 alert, can not be edited
                alertText.setKeyListener(null);
                alertThreshold.setKeyListener(null);
                buttonalertMp3.setEnabled(false);
                checkboxAllDay.setEnabled(false);
                checkboxAlertOverride.setEnabled(false);
            }
        }
        alertMp3File.setKeyListener(null);
        viewHeader.setText(status);
        setDefaultSnoozeSpinner();
        setPreSnoozeSpinner();
        enableAllDayControls();
        enableVibrateControls();


    }

    @Override
    public String getMenuName() {
        return menu_name;
    }
    public static String UnitsConvert2Disp(boolean doMgdl, double threshold) {
        DecimalFormat df = new DecimalFormat("#");
        if(doMgdl ) {
            df.setMaximumFractionDigits(0);
            df.setMinimumFractionDigits(0);
            return df.format(threshold);
        } else {
            df.setMaximumFractionDigits(1);
            df.setMinimumFractionDigits(1);
            return df.format(threshold / Constants.MMOLL_TO_MGDL);
        }
    }

    double UnitsConvertFromDisp(double threshold ) {
        if(doMgdl ) {
            return threshold;
        } else {
            return threshold * Constants.MMOLL_TO_MGDL;
        }
    }

    void enableAllDayControls() {
        boolean allDay = checkboxAllDay.isChecked();
        if(allDay) {
            layoutTimeBetween.setVisibility(View.GONE);
            timeInstructions.setVisibility(View.GONE);
        } else {
            setTimeRanges();
        }
    }

    void enableVibrateControls() {
        boolean overrideSilence = checkboxAlertOverride.isChecked();
        if(overrideSilence) {
            checkboxAlertOverride.setText("");
        } else {
            checkboxAlertOverride.setText("Warning, no alert will be played at phone silent/vibrate mode!!!");
        }
    }

    private boolean verifyThreshold(double threshold) {
        List<AlertType> lowAlerts = AlertType.getAll(false);
        List<AlertType> highAlerts = AlertType.getAll(true);

        if(threshold < MIN_ALERT || threshold > MAX_ALERT) {
            Toast.makeText(getApplicationContext(), "threshold has to be between " + MIN_ALERT + " and " + MAX_ALERT,Toast.LENGTH_LONG).show();
            return false;
        }
        if (uuid == null) {
            // We want to make sure that for each threashold there is only one alert. Otherwise, which file should we play.
            for (AlertType lowAlert : lowAlerts) {
                if(lowAlert.threshold == threshold) {
                    Toast.makeText(getApplicationContext(),
                            "Each alert should have it's own threshold. Please choose another threshold.",Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            for (AlertType highAlert : highAlerts) {
                if(highAlert.threshold == threshold) {
                    Toast.makeText(getApplicationContext(),
                            "Each alert should have it's own threshold. Please choose another threshold.",Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }
        // high alerts have to be higher than all low alerts...
        if(above) {
            for (AlertType lowAlert : lowAlerts) {
                if(threshold < lowAlert.threshold  ) {
                    Toast.makeText(getApplicationContext(),
                            "High alert threshold has to be higher than all low alerts. Please choose another threshold.",Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        } else {
            // low alert has to be lower than all high alerts
            for (AlertType highAlert : highAlerts) {
                if(threshold > highAlert.threshold  ) {
                    Toast.makeText(getApplicationContext(),
                            "Low alert threshold has to be lower than all high alerts. Please choose another threshold.",Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        return true;
    }

    public void addListenerOnButtons() {

        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Check that values are ok.
                double threshold = 0;
                try {
                    threshold = Double.parseDouble((alertThreshold.getText().toString()));
                }
                catch (NumberFormatException nfe) {
                    Log.e(TAG, "Invalid number", nfe);
                }
                threshold = UnitsConvertFromDisp(threshold);
                if(!verifyThreshold(threshold)) {
                    return;
                }

                int timeStart = AlertType.toTime(startHour, startMinute);
                int timeEnd = AlertType.toTime(endHour, endMinute);

                boolean allDay = checkboxAllDay.isChecked();
                // if 23:59 was set, we increase it to 24:00
                if(timeStart == AlertType.toTime(23, 59)) {
                    timeStart++;
                }
                if(timeEnd == AlertType.toTime(23, 59)) {
                    timeEnd++;
                }
                if(timeStart == AlertType.toTime(0, 0) &&
                   timeEnd == AlertType.toTime(24, 0)) {
                    allDay = true;
                }
                if (timeStart == timeEnd && (allDay==false)) {
                    Toast.makeText(getApplicationContext(), "start time and end time of alert can not be equal",Toast.LENGTH_LONG).show();
                    return;
                }
                boolean overrideSilentMode = checkboxAlertOverride.isChecked();
;
                String mp3_file = audioPath;
                if (uuid != null) {
                    AlertType.update_alert(uuid, alertText.getText().toString(), above, threshold, allDay, 1, mp3_file, timeStart, timeEnd, overrideSilentMode, defaultSnooze);
                }  else {
                    AlertType.add_alert(null, alertText.getText().toString(), above, threshold, allDay, 1, mp3_file, timeStart, timeEnd, overrideSilentMode, defaultSnooze);
                }
                Intent returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent);
                finish();
            }

        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                if (uuid == null) {
                    Log.wtf(TAG, "Error remove pressed, while we were removing an alert");
                }  else {
                    AlertType.remove_alert(uuid);
                }
                Intent returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent);
                finish();
            }

        });

        buttonTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                testAlert();
            }

        });

        buttonalertMp3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("What type of Alert?")
                        .setItems(R.array.alertType, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select tone for Alerts:");
                                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
                                    startActivityForResult(intent, 999);
                                } else if (which == 1) {
                                    Intent fileIntent = new Intent();
                                    fileIntent.setType("audio/mpeg3");
                                    fileIntent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(fileIntent, "Select File for Alert"), CHOOSE_FILE);
                                } else {
                                    // Xdrip default was chossen, we live the file name as empty.
                                    audioPath = "";
                                    alertMp3File.setText(shortPath(audioPath));
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
       }); //- See more at: http://blog.kerul.net/2011/12/pick-file-using-intentactiongetcontent.html#sthash.c8xtIr1Y.dpuf

        checkboxAllDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //          @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableAllDayControls();
            }
        });

        checkboxAlertOverride.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //          @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableVibrateControls();
            }
        });

        viewTimeStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker = new TimePickerDialog(mContext, AlertDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startHour = selectedHour;
                        startMinute = selectedMinute;
                        setTimeRanges();
                    }
                }, 0, 0, DateFormat.is24HourFormat(mContext));
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        viewTimeEnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker = new TimePickerDialog(mContext, AlertDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endHour = selectedHour;
                        endMinute = selectedMinute;
                        setTimeRanges();
                    }
                }, 23, 59, DateFormat.is24HourFormat(mContext));
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                audioPath = uri.toString();
                alertMp3File.setText(shortPath(audioPath));
            } else {
                if (requestCode == CHOOSE_FILE) {
                    Uri selectedImageUri = data.getData();

                    // Todo this code is very flacky. Probably need a much better understanding of how the different programs
                    // select the file names. We might also have to
                    // - See more at: http://blog.kerul.net/2011/12/pick-file-using-intentactiongetcontent.html#sthash.c8xtIr1Y.cx7s9nxH.dpuf

                    //MEDIA GALLERY
                    String selectedAudioPath = getPath(selectedImageUri);
                    if (selectedAudioPath == null) {
                        //OI FILE Manager
                        selectedAudioPath = selectedImageUri.getPath();
                    }
                    audioPath = selectedAudioPath;
                    alertMp3File.setText(shortPath(audioPath));
                }
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index;
            try {
                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            } catch ( IllegalArgumentException e) {
                Log.e(TAG, "cursor.getColumnIndexOrThrow failed", e);
                return null;
            }
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }   else {
            return null;
        }
    }

    public String timeFormatString(int Hour, int Minute) {
        SimpleDateFormat timeFormat24 = new SimpleDateFormat("HH:mm");
        String selected = Hour+":"+Minute;
        if (!DateFormat.is24HourFormat(mContext)) {
            try {
                Date date = timeFormat24.parse(selected);
                SimpleDateFormat timeFormat12 = new SimpleDateFormat("hh:mm aa");
                return timeFormat12.format(date);
            } catch (final ParseException e) {
                e.printStackTrace();
            }
        }
        return selected;
    }

    public void setTimeRanges() {
        timeInstructions.setVisibility(View.VISIBLE);
        layoutTimeBetween.setVisibility(View.VISIBLE);
        viewTimeStart.setText(timeFormatString(startHour, startMinute));
        viewTimeEnd.setText(timeFormatString(endHour, endMinute));
    }

    public String shortPath(String path) {
        if(path != null) {
            if(path.length() == 0) {
                return "xDrip Default";
            }
            Ringtone ringtone = RingtoneManager.getRingtone(mContext, Uri.parse(path));
            if (ringtone != null) {
                return ringtone.getTitle(mContext);
            } else {
                String[] segments = path.split("/");
                if (segments.length > 1) {
                    return segments[segments.length - 1];
                }
            }
        }
        return "";
    }
    public void setDefaultSnoozeSpinner() {
        editSnooze.setText(String.valueOf(defaultSnooze));
        editSnooze.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mView, MotionEvent mMotionEvent) {
                if (mMotionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    final Dialog d = new Dialog(mContext);
                    d.setTitle("Default Snooze");
                    d.setContentView(R.layout.snooze_picker);
                    Button b1 = (Button) d.findViewById(R.id.button1);
                    Button b2 = (Button) d.findViewById(R.id.button2);

                    final NumberPicker snoozeValue = (NumberPicker) d.findViewById(R.id.numberPicker1);


                    SnoozeActivity.SetSnoozePickerValues(snoozeValue, above, defaultSnooze);
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            defaultSnooze = SnoozeActivity.getTimeFromSnoozeValue(snoozeValue.getValue());
                            editSnooze.setText(String.valueOf(defaultSnooze));

                            d.dismiss();
                        }
                    });
                    b2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.dismiss();
                        }
                    });
                    d.show();
                }
                    return false;

            }});

    }

    public void setPreSnoozeSpinner() {


        buttonPreSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            //public boolean onTouch(View mView, MotionEvent mMotionEvent) {
            public void onClick(View v) {
                final Dialog d = new Dialog(mContext);
                d.setTitle("Snooze this alert...");
                d.setContentView(R.layout.snooze_picker);
                Button b1 = (Button) d.findViewById(R.id.button1);
                Button b2 = (Button) d.findViewById(R.id.button2);
                b1.setText("pre-Snooze");

                final NumberPicker snoozeValue = (NumberPicker) d.findViewById(R.id.numberPicker1);

                SnoozeActivity.SetSnoozePickerValues(snoozeValue, above, defaultSnooze);
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int intValue = SnoozeActivity.getTimeFromSnoozeValue(snoozeValue.getValue());
                        AlertPlayer.getPlayer().PreSnooze(getApplicationContext(), uuid, intValue);
                        d.dismiss();
                    }
                });
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                d.show();
            }});

    }

    public void testAlert() {
        // Check that values are ok.
        double threshold = 0;
        try {
            threshold = Double.parseDouble((alertThreshold.getText().toString()));
        }
        catch (NumberFormatException nfe) {
            Log.e(TAG, "Invalid number", nfe);
        }
        threshold = UnitsConvertFromDisp(threshold);
        if(!verifyThreshold(threshold)) {
            return;
        }

        int timeStart = AlertType.toTime(startHour, startMinute);
        int timeEnd = AlertType.toTime(endHour, endMinute);

        boolean allDay = checkboxAllDay.isChecked();
        // if 23:59 was set, we increase it to 24:00
        if(timeStart == AlertType.toTime(23, 59)) {
            timeStart++;
        }
        if(timeEnd == AlertType.toTime(23, 59)) {
            timeEnd++;
        }
        if(timeStart == AlertType.toTime(0, 0) &&
                timeEnd == AlertType.toTime(24, 0)) {
            allDay = true;
        }
        if (timeStart == timeEnd && (allDay==false)) {
            Toast.makeText(getApplicationContext(), "start time and end time of alert can not be equal",Toast.LENGTH_LONG).show();
            return;
        }
        boolean overrideSilentMode = checkboxAlertOverride.isChecked();
        String mp3_file = audioPath;
        AlertType.testAlert(alertText.getText().toString(), above, threshold, allDay, 1, mp3_file, timeStart, timeEnd, overrideSilentMode, defaultSnooze, mContext);

    }
}
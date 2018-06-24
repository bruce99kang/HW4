package com.example.android.hw4;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Dialog custDialog;
    private Spinner classDaySpinner;
    public SQLiteDatabase mDb;
    private MainAdapter mAdapter;
    private String classDay;
    private String className;
    private String classTime;
    private EditText classNameEdit;
    private final String[] classDaylist = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView;
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MainDbHelper classDbHelper = new MainDbHelper(this);
        mDb = classDbHelper.getWritableDatabase();
        FloatingActionButton mFab = (FloatingActionButton) this.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildViews();
            }
        });
        Cursor cursor = getAllClass();
        mAdapter = new MainAdapter(this, cursor);
        recyclerView.setAdapter(mAdapter);
        scheduleJob(MainActivity.this);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                removeClass(id);
                mAdapter.swapCursor(getAllClass());
            }
        }).attachToRecyclerView(recyclerView);

    }

    public void buildViews() {

        custDialog = new Dialog(MainActivity.this);
        custDialog.setCancelable(false);
        custDialog.setContentView(R.layout.custom_alert);

        classNameEdit = (EditText) custDialog.findViewById(R.id.classNameEditText);
        className = classNameEdit.getText().toString();

        classDaySpinner = (Spinner) custDialog.findViewById(R.id.spinner);
        final ArrayAdapter<String> list = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, classDaylist);
        classDaySpinner.setAdapter(list);
        classDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classDay = classDaylist[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                classDay = classDaylist[0];
            }
        });

        TimePicker timePicker = (TimePicker) custDialog.findViewById(R.id.timePicker);
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        timePicker.setHour(hour + 8);
        timePicker.setMinute(minute);
        timePicker.setIs24HourView(true);

        classTime = timePicker.getHour() + ":" + timePicker.getMinute();

        Button btCancel = (Button) custDialog.findViewById(R.id.cancel_action);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custDialog.cancel();
            }
        });
        Button btOk = (Button) custDialog.findViewById(R.id.ok_action);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (classNameEdit.getText().length() == 0) {
                    return;
                }
                addNewClass(className, classDay, classTime);
                custDialog.cancel();
                classNameEdit.getText().clear();
            }
        });
        custDialog.show();

    }

    private Cursor getAllClass() {
        return mDb.query(MainContract.MainEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MainContract.MainEntry.COLUMN_TIMESTAMP);
    }

    private long addNewClass(String className, String classDay, String classTime) {
        ContentValues cv = new ContentValues();
        cv.put(MainContract.MainEntry.COLUMN_CLASS_NAME, className);
        cv.put(MainContract.MainEntry.COLUMN_CLASS_DAY, classDay);
        cv.put(MainContract.MainEntry.COLUMN_CLASS_TIME, classTime);
        return mDb.insert(MainContract.MainEntry.TABLE_NAME, null, cv);
    }

    private boolean removeClass(long id) {
        return mDb.delete(MainContract.MainEntry.TABLE_NAME, MainContract.MainEntry._ID + "=" + id, null) > 0;
    }

    public void notificationJob() {
        NotificationManager ntfMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int icon = R.drawable.baseline_access_time_black_18dp;
        if (classTime == String.valueOf(hour) + ":" + String.valueOf(minute - 5)) {
            Context context = getApplicationContext();
            Notification.Builder builder = new Notification.Builder(context);
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            builder
                    .setSmallIcon(icon)
                    .setContentTitle(className)
                    .setContentText("快要上課啦")
                    .setLights(0xFFFFFFFF, 1000, 1000)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false);

            Notification notification = builder.build();
            ntfMgr.notify(icon, notification);

        }

    }

    public static void scheduleJob(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class) // the JobService that will be called
                .setTag("my-unique-tag")        // uniquely identifies the job
                .build();
        dispatcher.mustSchedule(myJob);
    }

    public class MyJobService extends JobService {
        @Override
        public boolean onStartJob(JobParameters job) {
            notificationJob();
            return false;
        }

        @Override
        public boolean onStopJob(JobParameters job) {
            return false;
        }

    }


}

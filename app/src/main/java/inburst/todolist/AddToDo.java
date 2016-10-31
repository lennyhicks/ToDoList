package inburst.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lennyhicks on 10/26/16.
 */
public class AddToDo extends Activity {

    private Button toDoSave;
    private EditText toDoTitle;
    private EditText toDoText;
    private Spinner priority;
    private Spinner category;
    private CheckBox isDone;
    private static EditText toDoDue;
    private static EditText setTime;
    private TextView lastModified;
    private int index;
    private static int RESULT_LOAD_IMAGE = 1;
    private static String picturePath;
    private ImageView imageView;
    private EditText addNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo);

        category = (Spinner)findViewById(R.id.category);
        priority = (Spinner)findViewById(R.id.priority);
        toDoText = (EditText)findViewById(R.id.toDo_Text);
        toDoTitle = (EditText)findViewById(R.id.toDo_Title);
        toDoSave = (Button)findViewById(R.id.saveButton);
        toDoDue = (EditText)findViewById(R.id.datePicker);
        isDone = (CheckBox) findViewById(R.id.isDone);
        lastModified = (TextView)findViewById(R.id.dateModified);
        setTime = (EditText)findViewById(R.id.setTime);
        addNew = (EditText)findViewById(R.id.newCat);

        imageView = (ImageView) findViewById(R.id.imageView);

        ArrayList<String> priorities = new ArrayList<>();
        priorities.add(0, "High");
        priorities.add(1, "Normal");
        priorities.add(2, "Low");



        Intent intent = this.getIntent();

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, MainActivity.cats);
        category.setAdapter(spinnerArrayAdapter);


        ArrayAdapter adapt = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, priorities);
        priority.setAdapter(adapt);

        toDoTitle.setText(intent.getStringExtra("Title"));
        toDoText.setText(intent.getStringExtra("Notes"));
        toDoDue.setText(intent.getStringExtra("dueDate"));
        setTime.setText(intent.getStringExtra("dueTime"));
        isDone.setChecked(intent.getBooleanExtra("Done", false));
        lastModified.setText(intent.getStringExtra("lastModified"));
        picturePath = intent.getStringExtra("photoDir");
        imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        String compareValue = intent.getStringExtra("Category");
        if (compareValue != null) {
            int spinnerPosition = spinnerArrayAdapter.getPosition(compareValue);
            category.setSelection(spinnerPosition);
        }

        String pri = intent.getStringExtra("Priority");
        if (pri != null) {
            int spinnerPosition = spinnerArrayAdapter.getPosition(pri);
            category.setSelection(spinnerPosition);
        }

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (category.getSelectedItem().toString().equals("Add New")){
                    addNew.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });        index = intent.getIntExtra("Index", -1);



        setTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                DialogFragment newFragment = new TimePick();
                newFragment.show(getFragmentManager(), "TimePick");

            }
        });

        toDoDue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "DatePicker");

            }
        });

        Button buttonLoadImage = (Button) findViewById(R.id.addPhoto);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }});


        toDoSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar c = Calendar.getInstance();
                System.out.println("Current time => "+c.getTime());

                String dt;
                Date cal = Calendar.getInstance().getTime();
                dt = cal.toLocaleString();


                Intent intent = getIntent();
                intent.putExtra("Title", toDoTitle.getText().toString());
                intent.putExtra("Notes", toDoText.getText().toString());
                intent.putExtra("Date", toDoDue.getText().toString());
                intent.putExtra("Done", isDone.isChecked());
                intent.putExtra("lastModified", dt);
                Log.i("Checked",  ""+isDone.isChecked());
                intent.putExtra("Priority", priority.getSelectedItem().toString());
                if (category.getSelectedItem().toString().equals("Add New")){
                    intent.putExtra("Category", addNew.getText().toString());
                    addNew.setVisibility(View.INVISIBLE);
                } else {
                    intent.putExtra("Category", category.getSelectedItem().toString());
                }
                intent.putExtra("photoDir", picturePath);
                intent.putExtra("dueTime", setTime.getText());
                intent.putExtra("Index", index);
                setResult(RESULT_OK, intent);
                Log.i("TESTTTTTT", toDoDue.getText().toString());
                if (!toDoDue.getText().toString().equals("")) {
                    Log.i("TESTTTTTT", toDoDue.getText().toString());
                    finish();
                } else {
                    Toast.makeText(AddToDo.this, "Please Enter a Due Date", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            toDoDue.setText(pad(month+1) + "/" + pad(day) + "/" + year);
        }
        private static String pad(int c) {
            if (c >= 10)
                return String.valueOf(c);
            else
                return "0" + String.valueOf(c);
        }
    }

    public static class TimePick extends android.app.DialogFragment implements TimePickerDialog.OnTimeSetListener {



        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, minute,false);


        }

        @Override
        public void onTimeSet(TimePicker view, int hourofDay, int minute) {



            setTime.setText(new StringBuilder().append(pad(hourofDay))
                    .append(":").append(pad(minute)));
        }

        private static String pad(int c) {
            if (c >= 10)
                return String.valueOf(c);
            else
                return "0" + String.valueOf(c);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
    }





}

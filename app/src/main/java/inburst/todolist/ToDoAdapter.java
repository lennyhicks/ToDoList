package inburst.todolist;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lennyhicks on 10/25/16.
 */
public class ToDoAdapter extends  ArrayAdapter<ToDo> implements Filterable {

        private int resource;
        private ArrayList<ToDo> ToDo;
        private LayoutInflater inflater;
        private SimpleDateFormat formatter;


        public ToDoAdapter(Context context, int resource, ArrayList<ToDo> toDo) {

            super(context, resource, toDo);

            for (ToDo blah : toDo){
                blah.setDone(blah.getDone());
                Log.i("blah",  ""+ blah.toJSON());
            }
            this.resource = resource;
            this.ToDo = toDo;

            inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            formatter = new SimpleDateFormat("MM/dd/yyyy");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View toDoRow = inflater.inflate(resource, parent, false);

            TextView toDoTitle = (TextView)toDoRow.findViewById(R.id.mainTitle);
            TextView dueDate = (TextView)toDoRow.findViewById(R.id.dueDate);
            CheckBox isDone = (CheckBox) toDoRow.findViewById(R.id.isDone);


            final ToDo toDo = ToDo.get(position);
            if(toDo.getPriority() != null) {
                if (toDo.getPriority().equals("Low")) {
                    toDoRow.setBackgroundColor(Color.argb(10, 255, 0, 0));
                } else if (toDo.getPriority().equals("Normal")) {
                    toDoRow.setBackgroundColor(Color.argb(10, 0, 255, 0));
                } else if (toDo.getPriority().equals("High")) {
                    toDoRow.setBackgroundColor(Color.argb(10, 0, 0, 255));
                }
            }
            if (toDo.getDone() == null){
                isDone.setVisibility(View.INVISIBLE);
                toDoRow.setClickable(false);
                toDoRow.setEnabled(false);
            } else {
                isDone.setChecked(toDo.getDone());
            }
                toDoTitle.setText(toDo.getTitle());
                dueDate.setText(toDo.getDueDate());



            isDone.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if ( ((CheckBox)v).isChecked() ) {
                        toDo.setDone(true);
                    } else{
                        toDo.setDone(false);
                    }
                }
            });
            return toDoRow;
        }

        public void updateAdapter(ArrayList<ToDo> toDo){
            this.ToDo = toDo;

            super.notifyDataSetChanged();
        }


    }




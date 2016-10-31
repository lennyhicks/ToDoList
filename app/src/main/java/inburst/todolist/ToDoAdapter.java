package inburst.todolist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lennyhicks on 10/25/16.
 */
public class ToDoAdapter extends  ArrayAdapter<ToDo> implements Filterable {

        private int resource;
        private ArrayList<ToDo> ToDo;
        private LayoutInflater inflater;



        public ToDoAdapter(Context context, int resource, ArrayList<ToDo> toDo) {

            super(context, resource, toDo);

            for (ToDo blah : toDo){
                blah.setDone(blah.getDone());
            }
            this.resource = resource;
            this.ToDo = toDo;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            final View toDoRow;
            final ToDo cell = getItem(position);

            if(cell.isSectionHeader())
            {
                toDoRow = inflater.inflate(R.layout.section_header, null);

                toDoRow.setClickable(true);

                final TextView header = (TextView) toDoRow.findViewById(R.id.section_header);
                header.setText(cell.getCategory());

                toDoRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                        builder.setTitle("Categories");
                        builder.setMessage("Edit or Cancel");

                        final EditText input = new EditText(parent.getContext());
                        input.setText(cell.getCategory());
                        builder.setView(input);

                        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                MainActivity.cats.add(MainActivity.cats.size(), input.getText().toString());

                                for (ToDo todo: MainActivity.toDoArray){
                                    if(todo.getCategory().equals(cell.getCategory())){
                                        todo.setCategory(input.getText().toString());
                                    }
                                }
                                notifyDataSetChanged();


                            }
                        });

                        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (ToDo todo: MainActivity.toDoArray){
                                    if(todo.getCategory().equals(cell.getCategory())){
                                        todo.setCategory("Uncategorized");
                                    }
                                }
                                notifyDataSetChanged();

                            }
                        });

                        builder.create();
                        builder.show();
                    }

                });

            } else {
                toDoRow = inflater.inflate(resource, parent, false);

                TextView toDoTitle = (TextView) toDoRow.findViewById(R.id.mainTitle);
                TextView dueDate = (TextView) toDoRow.findViewById(R.id.dueDate);
                CheckBox isDone = (CheckBox) toDoRow.findViewById(R.id.isDone);


                final ToDo toDo = ToDo.get(position);
                if (toDo.getPriority() != null) {
                    switch (toDo.getPriority()) {
                        case "Low":
                            toDoRow.setBackgroundColor(Color.argb(10, 255, 0, 0));
                            break;
                        case "Normal":
                            toDoRow.setBackgroundColor(Color.argb(10, 0, 255, 0));
                            break;
                        case "High":
                            toDoRow.setBackgroundColor(Color.argb(10, 0, 0, 255));
                            break;
                    }
                }
                if (toDo.getDone() == null) {
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

                        if (((CheckBox) v).isChecked()) {
                            toDo.setDone(true);
                        } else {
                            toDo.setDone(false);
                        }
                    }
                });

            }
            return toDoRow;

        }

        public void updateAdapter(ArrayList<ToDo> toDo){

            this.ToDo = toDo;

            super.notifyDataSetChanged();

        }


    }




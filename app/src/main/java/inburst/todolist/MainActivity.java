package inburst.todolist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private ToDoAdapter toDoArrayAdapter;
    public ListView toDoList;
    public static ArrayList<ToDo> toDoArray = new ArrayList<>();
    private SharedPreferences toDoPrefs;
    private DatabaseHandler db = new DatabaseHandler(MainActivity.this);
    private SearchView search;
    private ArrayList<ToDo> results = new ArrayList<>();
    private Boolean isSearch;
    public static ArrayList<String> cats;
    public static boolean showCompleted = true;
    private Switch toggleCompleted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isSearch = false;
        toDoPrefs = getPreferences(Context.MODE_PRIVATE);
        setupNotes();

        Collections.sort(toDoArray);
        order();
        toDoList = (ListView) findViewById(R.id.listView);
        search = (SearchView) findViewById(R.id.searchBar);
        toDoArray = sortAndAddSections(toDoArray);

        toDoArrayAdapter = new ToDoAdapter(this, R.layout.list_item, toDoArray);
        toDoList.setAdapter(toDoArrayAdapter);
        toDoArrayAdapter.updateAdapter(toDoArray);


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(search.getQuery())) {
                    toDoList.clearTextFilter();
                    toDoArrayAdapter = new ToDoAdapter(MainActivity.this, R.layout.list_item, toDoArray);
                    toDoList.setAdapter(toDoArrayAdapter);
                    toDoArrayAdapter.updateAdapter(toDoArray);
                    toDoArrayAdapter.notifyDataSetChanged();
                    results = new ArrayList<>();
                    isSearch = false;

                } else {
                    results = new ArrayList<>();
                    isSearch = true;
                    for (ToDo toDo : toDoArray) {
                        if(!toDo.isSectionHeader()) {
                            if (toDo.getTitle().toLowerCase().contains(search.getQuery().toString().toLowerCase())) {

                                results.add(results.size(), toDo);
                                toDoArrayAdapter = new ToDoAdapter(MainActivity.this, R.layout.list_item, results);
                                toDoList.setAdapter(toDoArrayAdapter);
                                toDoArrayAdapter.updateAdapter(results);
                                toDoArrayAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    if(results.size() == 0){

                        toDoArrayAdapter = new ToDoAdapter(MainActivity.this, R.layout.list_item, results);
                        toDoList.setAdapter(toDoArrayAdapter);
                        results.add(0, new ToDo(null, "No Results", null, "No Results", null, null, null, null, null, null));
                        toDoArrayAdapter.updateAdapter(results);
                        toDoArrayAdapter.notifyDataSetChanged();
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(search.getQuery())) {
                    toDoList.clearTextFilter();

                    toDoArrayAdapter = new ToDoAdapter(MainActivity.this, R.layout.list_item, toDoArray);
                    toDoList.setAdapter(toDoArrayAdapter);
                    toDoArrayAdapter.updateAdapter(toDoArray);
                    toDoArrayAdapter.notifyDataSetChanged();
                    isSearch = false;
                    results = new ArrayList<>();

                } else {
                    results = new ArrayList<>();
                    toDoList = (ListView) findViewById(R.id.listView);
                    isSearch = true;

                    for (ToDo toDo : toDoArray) {
                        if (!toDo.isSectionHeader()) {
                            if (toDo.getTitle().toLowerCase().contains(search.getQuery().toString().toLowerCase())) {
                                results.add(results.size(), toDo);
                                toDoArrayAdapter = new ToDoAdapter(MainActivity.this, R.layout.list_item, results);
                                toDoList.setAdapter(toDoArrayAdapter);
                                toDoArrayAdapter.updateAdapter(results);
                                toDoArrayAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    if(results.size() == 0){

                        results.add(0, new ToDo(null, "No Results", null, null, "No Results", null, null, null, null, null));
                        toDoArrayAdapter = new ToDoAdapter(MainActivity.this, R.layout.list_item, results);
                        toDoList.setAdapter(toDoArrayAdapter);
                        toDoArrayAdapter.updateAdapter(results);
                        toDoArrayAdapter.notifyDataSetChanged();


                    }
                }

                return true;
            }
        });

        toDoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                String resultss;
                if (isSearch) {
                    resultss = results.get(position).getKey();
                } else {
                    resultss = toDoArray.get(position).getKey();
                }
                if (resultss != null) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertBuilder.setTitle("Delete");
                    alertBuilder.setMessage("You sure?");
                    alertBuilder.setNegativeButton("Cancel", null);
                    alertBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!isSearch) {
                                ToDo todo = toDoArray.get(position);
                                db.deleteToDo(todo);
                                toDoArray.remove(position);
                                toDoArrayAdapter.updateAdapter(toDoArray);
                            } else {
                                ToDo search = results.get(position);

                                for (ToDo toDo : toDoArray) {

                                    if (search.getKey().equals(toDo.getKey())) {
                                        toDoArray.remove(toDo);
                                        db.deleteToDo(toDo);
                                    }
                                }

                                toDoArrayAdapter.updateAdapter(toDoArray);
                            }
                            for (ToDo toDo : toDoArray) {

                                db.updateToDo(toDo);
                            }
                        }

                    });
                    alertBuilder.create().show();
                    return true;
                }
                else {
                    return false;
                }
            }


        });

        toDoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToDo toDo;

                if (!isSearch) {
                    toDo = toDoArray.get(position);
                } else {
                    toDo = results.get(position);


                }


                if (toDo.getKey() != null) {


                    Intent intent = new Intent(MainActivity.this, AddToDo.class);
                    intent.putExtra("Title", toDo.getTitle());
                    intent.putExtra("Notes", toDo.getNotes());
                    intent.putExtra("Key", toDo.getKey());
                    intent.putExtra("dueDate", toDo.getDueDate());
                    intent.putExtra("lastModified", toDo.getLastModified());
                    intent.putExtra("Done", toDo.getDone());
                    intent.putExtra("Priority", toDo.getPriority());
                    intent.putExtra("Category", toDo.getCategory());
                    intent.putExtra("photoDir", toDo.getPhoto());
                    intent.putExtra("dueTime", toDo.getTime());

                    intent.putExtra("Index", position);
                    startActivityForResult(intent, 1);
                }

            }
        });
    }


    private void setupNotes() {
        setContentView(R.layout.activity_main);
        if (toDoPrefs.getBoolean("firstRun", true)) {

            toDoArray.add(new ToDo("Try to Sleep", "10/31/2016", false, "a", "a", "a", "Personal", null, null));
            toDoArray.add(new ToDo("Get Eggs", "10/31/2016", true, "a", "a", "a", "Groceries", null, null));
            toDoArray.add(new ToDo("Check the car", "10/31/2016", false, "a", "a", "a", "Misc", null, null));
            writeTodos();

            SharedPreferences.Editor editor = toDoPrefs.edit();
            editor.putBoolean("firstRun", false);
            editor.apply();

        } else {
            readTodos();
        }
    }


    private void readTodos() {
        toDoArray = db.getAllToDos();


    }

    private void writeTodos() {
        for (ToDo todo : toDoArray) {
            db.addToDo(todo);
        }

    }

    private void writeTodo(ToDo toDo) {
        db.addToDo(toDo);
    }


    @Override
    public void onSaveInstanceState(Bundle savedState) {

        for (ToDo toDo : toDoArray) {
            db.updateToDo(toDo);
        }
        sortAndAddSections(toDoArray);


        toDoArrayAdapter = new ToDoAdapter(MainActivity.this, R.layout.list_item, toDoArray);
        toDoList.setAdapter(toDoArrayAdapter);
        toDoArrayAdapter.updateAdapter(toDoArray);
        toDoArrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sortAndAddSections(toDoArray);
        if (resultCode == RESULT_OK) {
            int index = data.getIntExtra("Index", -1);

            ToDo toDo = new ToDo(data.getStringExtra("Title"), data.getStringExtra("Date"), data.getBooleanExtra("Done", false), data.getStringExtra("Priority"), data.getStringExtra("lastModified"), data.getStringExtra("Notes"), data.getStringExtra("Category"), data.getStringExtra("photoDir"), data.getStringExtra("dueTime"));

            if (index == -1) {
                toDoArray.add(toDoArray.size(), toDo);
                writeTodo(toDo);
            } else {
                if(isSearch) {
                    for (ToDo todo : toDoArray) {

                        if (toDo.getKey() == todo.getKey()) {
                            results.remove(index);
                            toDoArray.remove(todo);
                            db.updateToDo(todo);
                        }
                    }
                }else {
                    ToDo oldToDo = toDoArray.get(index);
                    toDo.setKey(oldToDo.getKey());
                    db.updateToDo(toDo);
                    toDoArray.set(index, toDo);
                }
            }

            toDoArray = sortAndAddSections(toDoArray);


        }

        toDoArrayAdapter = new ToDoAdapter(MainActivity.this, R.layout.list_item, toDoArray);
        toDoList.setAdapter(toDoArrayAdapter);
        toDoArrayAdapter.updateAdapter(toDoArray);
        toDoArrayAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, AddToDo.class);


            intent.putExtra("Title", "");
            intent.putExtra("Notes", "");
            intent.putExtra("Key", "");
            intent.putExtra("dueDate", "");
            intent.putExtra("lastModified", "");
            intent.putExtra("Done", true);
            intent.putExtra("Priority", "");
            intent.putExtra("Category", "");
            intent.putExtra("photoDir", "");
            intent.putExtra("dueTime", "");

            startActivityForResult(intent, 1);
            return true;
        }

        if (id == R.id.menuToggle) {
            if (showCompleted) {
                showCompleted = false;
                ArrayList<ToDo> temp = showComplete(toDoArray);


                toDoArrayAdapter = new ToDoAdapter(MainActivity.this, R.layout.list_item, temp);
                toDoList.setAdapter(toDoArrayAdapter);
                toDoArrayAdapter.updateAdapter(temp);
                toDoArrayAdapter.notifyDataSetChanged();


            } else {
                showCompleted = true;
                toDoArray = sortAndAddSections(toDoArray);
                toDoArrayAdapter = new ToDoAdapter(MainActivity.this, R.layout.list_item, toDoArray);
                toDoList.setAdapter(toDoArrayAdapter);
                toDoArrayAdapter.updateAdapter(toDoArray);
                toDoArrayAdapter.notifyDataSetChanged();


            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_todo, menu);


            return true;
    }


    private ArrayList sortAndAddSections(ArrayList<ToDo> toDoArray)
    {
        order();
        Log.i("List", toDoArray.toString());
        cats = new ArrayList<>();

        ArrayList temps = new ArrayList();
        ArrayList tempList = new ArrayList();
        for (ToDo todo: toDoArray){
            if (!todo.isSectionHeader()){
                temps.add(todo);
            }
        }
        toDoArray = temps;
        //First we sort the array
        Collections.sort(toDoArray);
        String header = "";


        for(int i = 0; i < toDoArray.size(); i++)
        {

            if(!header.equals(toDoArray.get(i).getCategory())){
                     cats.add(toDoArray.get(i).getCategory());
                     ToDo sectionCell = new ToDo(toDoArray.get(i).getCategory());
                     sectionCell.setToSectionHeader();
                     tempList.add(sectionCell);
                     header = toDoArray.get(i).getCategory();


            }
            tempList.add(toDoArray.get(i));

        }
        cats.add("Add New");

        return tempList;
    }




    private static void order() {

        Collections.sort(toDoArray, new Comparator() {

            public int compare(Object o1, Object o2) {

                if (((ToDo) o2).getDueDate() != null) {
                    String x1 = ((ToDo) o1).getCategory();

                    String x2 = ((ToDo) o2).getCategory();


                    int sComp = x1.compareTo(x2);

                    if (sComp != 0) {
                        return sComp;
                    } else {
                        x1 = ((ToDo) o1).getDueDate();

                        x2 = ((ToDo) o2).getDueDate();

                        return x1.compareTo(x2);
                    }
                }
                else {return 0; }
            }});
    }

public ArrayList showComplete(ArrayList<ToDo> toDos){
    ArrayList<ToDo> temp = new ArrayList<>();


    for(int i = 0; i < toDos.size(); i++) {
        if (toDos.get(i).isSectionHeader()) {
            toDos.remove(i);
        }
    }




for(ToDo todo: toDos){
    if (!todo.getDone()){
        temp.add(todo);
    }
}
    temp = sortAndAddSections(temp);

    return temp;
}

}
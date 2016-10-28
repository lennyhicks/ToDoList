package inburst.todolist;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;


import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ToDoAdapter toDoArrayAdapter;
    public ListView toDoList;
    ArrayList<ToDo> toDoArray = new ArrayList<>();
    private SharedPreferences toDoPrefs;
    private DatabaseHandler db = new DatabaseHandler(MainActivity.this);
    private SearchView search;
    private ArrayList<ToDo> results = new ArrayList<>();
    private Boolean isSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isSearch = false;
        toDoPrefs = getPreferences(Context.MODE_PRIVATE);
        setupNotes();

        Collections.sort(toDoArray);

        toDoList = (ListView) findViewById(R.id.listView);
        search = (SearchView) findViewById(R.id.searchBar);

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

                        if (toDo.getTitle().toLowerCase().contains(search.getQuery().toString().toLowerCase())){

                            results.add(results.size(), toDo);
                            toDoArrayAdapter = new ToDoAdapter(MainActivity.this, R.layout.list_item, results);
                            toDoList.setAdapter(toDoArrayAdapter);
                            toDoArrayAdapter.updateAdapter(results);
                            toDoArrayAdapter.notifyDataSetChanged();
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

                        if (toDo.getTitle().toLowerCase().contains(search.getQuery().toString().toLowerCase())){
                            results.add(results.size(), toDo);
                            toDoArrayAdapter = new ToDoAdapter(MainActivity.this, R.layout.list_item, results);
                            toDoList.setAdapter(toDoArrayAdapter);
                            toDoArrayAdapter.updateAdapter(results);
                            toDoArrayAdapter.notifyDataSetChanged();
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

                                    if (search.getKey() == toDo.getKey()) {
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

            toDoArray.add(new ToDo("Note 1", "10/31/2016", false, "a", "a", "a", "a", null, null));
            toDoArray.add(new ToDo("Note 2", "10/31/2016", true, "a", "a", "a", "a", null, null));
            toDoArray.add(new ToDo("Note 3", "10/31/2016", false, "a", "a", "a", "a", null, null));
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

            toDoArrayAdapter.updateAdapter(toDoArray);
        }
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
        if (id == R.id.menuSort) {
            return true;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_todo, menu);


            return true;


    }

}
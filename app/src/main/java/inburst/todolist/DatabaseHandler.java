package inburst.todolist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "todoList";

    // Contacts table name
    private static final String TABLE_TODO = "todo";

    // Contacts Table Columns names



    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_DONE = "done";
    private static final String KEY_DUEDATE = "duedate";
    private static final String KEY_PRIOROITY = "priority";
    private static final String KEY_LASTMODIFIED = "lastmodified";
    private static final String KEY_NOTES = "notes";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_TIME = "time";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override

    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO + "("
                + KEY_ID + " TEXT PRIMARY KEY," + KEY_CATEGORY + " TEXT," + KEY_DONE + " TEXT," + KEY_DUEDATE + " TEXT," + KEY_TITLE + " TEXT," + KEY_PRIOROITY + " TEXT," + KEY_LASTMODIFIED + " TEXT," + KEY_NOTES + " TEXT," + KEY_PHOTO + " TEXT,"
                + KEY_TIME + " TEXT"     /**/+ ")";
        db.execSQL(CREATE_TODO_TABLE);
    }


    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);

        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    void addToDo(ToDo toDo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, toDo.getKey());
        values.put(KEY_CATEGORY, toDo.getCategory());

        if (toDo.getDone()) {
            values.put(KEY_DONE, true);
        }else {
            values.put(KEY_DONE, "false");
        }
        values.put(KEY_DUEDATE, toDo.getDueDate());
        values.put(KEY_TITLE, toDo.getTitle());
        values.put(KEY_PRIOROITY, toDo.getPriority());
        values.put(KEY_LASTMODIFIED, toDo.getLastModified());
        values.put(KEY_NOTES, toDo.getNotes());
        values.put(KEY_PHOTO, toDo.getPhoto());
        values.put(KEY_TIME, toDo.getTime());

        // Inserting Row
        db.insert(TABLE_TODO, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    ToDo getToDo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TODO, new String[] { KEY_ID,
                        KEY_CATEGORY, KEY_DONE, KEY_DUEDATE, KEY_TITLE, KEY_PRIOROITY, KEY_LASTMODIFIED, KEY_NOTES, KEY_PHOTO, KEY_TIME }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ToDo toDo = new ToDo(cursor.getString(0), cursor.getString(1), Boolean.parseBoolean(cursor.getString(2)), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));
        // return contact
        return toDo;
    }
//    public Cursor searchCustomer(String inputText) throws SQLException {
//
//        String query = "SELECT docid as _id," +
//                KEY_CUSTOMER + "," +
//                KEY_NAME + "," +
//                "(" + KEY_ADDRESS1 + "||" +
//                "(case when " + KEY_ADDRESS2 +  "> '' then '\n' || " + KEY_ADDRESS2 + " else '' end)) as " +  KEY_ADDRESS +"," +
//                KEY_ADDRESS1 + "," +
//                KEY_ADDRESS2 + "," +
//                KEY_CITY + "," +
//                KEY_STATE + "," +
//                KEY_ZIP +
//                " from " + FTS_VIRTUAL_TABLE +
//                " where " +  KEY_SEARCH + " MATCH '" + inputText + "';";
//        Log.w(TAG, query);
//        Cursor mCursor = mDb.rawQuery(query,null);
//
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
//
//    }
    // Getting All Contacts
    public ArrayList<ToDo> getAllToDos() {
        ArrayList<ToDo> toDoList = new ArrayList<ToDo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TODO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ToDo toDo = new ToDo();
                toDo.setKey((cursor.getString(0)));
                toDo.setCategory(cursor.getString(1));
                if(cursor.getString(2).equals("true")) {
                    toDo.setDone(true);
                }else{
                    Log.i("Failing", toDo.getDone()+"");
                    toDo.setDone(false);
                }
                toDo.setDueDate(cursor.getString(3));
                toDo.setTitle(cursor.getString(4));
                toDo.setPriority(cursor.getString(5));
                toDo.setLastModified(cursor.getString(6));
                toDo.setNotes(cursor.getString(7));
                toDo.setPhoto(cursor.getString(8));
                toDo.setTime(cursor.getString(9));

                // Adding to Do to list
                toDoList.add(toDo);
            } while (cursor.moveToNext());
        }

        // return to do list
        return toDoList;
    }

    // Updating single to Do
    public int updateToDo(ToDo toDo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, toDo.getKey());
        values.put(KEY_CATEGORY, toDo.getCategory());
        values.put(KEY_DONE, toDo.getDone().toString());
        values.put(KEY_DUEDATE, toDo.getDueDate());
        values.put(KEY_TITLE, toDo.getTitle());
        values.put(KEY_PRIOROITY, toDo.getPriority());
        values.put(KEY_LASTMODIFIED, toDo.getLastModified());
        values.put(KEY_NOTES, toDo.getNotes());
        values.put(KEY_PHOTO, toDo.getPhoto());
        values.put(KEY_TIME, toDo.getTime());
        Log.i("UPDATED", values.toString());
        // updating row
        return db.update(TABLE_TODO, values, KEY_ID + " = ?",
                new String[] { toDo.getKey() });
    }

    // Deleting single to do
    public void deleteToDo(ToDo toDo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, KEY_ID + " = ?",
                new String[] { toDo.getKey() });
        db.close();
    }


    // Getting to do Count
    public int getToDoCount() {

        String countQuery = "SELECT  * FROM " + TABLE_TODO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

}
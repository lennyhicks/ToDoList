package inburst.todolist;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by lennyhicks on 10/25/16.
 */
public class ToDo implements Comparable<ToDo> {
    @SerializedName("title")
    String Title;
    @SerializedName("dueDate")
    String dueDate;
    @SerializedName("done")
    Boolean done;
    @SerializedName("priority")
    String priority;
    @SerializedName("lastModified")
    String lastModified;
    @SerializedName("notes")
    String notes;
    @SerializedName("category")
    String category;
    @SerializedName("photo")
    String photo;
    @SerializedName("time")
    String time;
    private String key;
    private boolean isSectionHeader;



    public ToDo(String title, String date, Boolean done, String priority, String lastModified, String notes, String category, String photo, String time) {


        this.Title = title;
        this.dueDate = date;
        this.done = done;
        this.key = UUID.randomUUID().toString();
        this.priority = priority;
        this.lastModified = lastModified;
        this.notes = notes;
        this.category = category;
        this.photo = photo;
        this.time = time;

    }

    public ToDo(String category)
    {
        this.category = category;
        isSectionHeader = true;
    }
    public ToDo(String key, String category, Boolean done, String dueDate, String title, String priority, String lastModified, String notes, String photo, String time) {


        this.Title = title;
        this.dueDate = dueDate;
        this.done = done;
        this.key = key;
        this.priority = priority;
        this.lastModified = lastModified;
        this.notes = notes;
        this.category = category;
        this.photo = photo;
        this.time = time;

    }

    public ToDo(){};

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDueDate() {
        if (!isSectionHeader()) {
            String[] dates;
            dates = dueDate.split("/");
            dates[0] = pad(Integer.parseInt(dates[0]));
            dates[1] = pad(Integer.parseInt(dates[1]));
            dueDate = dates[0]+"/"+dates[1]+"/"+dates[2];

            return dueDate;
        }
        else {return "0";}
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setToSectionHeader()
    {
        isSectionHeader = true;
    }

    public boolean isSectionHeader()
    {
        return isSectionHeader;
    }

    public String toJSON(){

        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("id", getKey());
            jsonObject.put("title", getTitle());
            jsonObject.put("duedate", getDueDate());
            jsonObject.put("done", getDone());
            jsonObject.put("priority", getPriority());
            jsonObject.put("lastMod", getLastModified());
            jsonObject.put("notes", getNotes());
            jsonObject.put("category", getCategory());

            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }

    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @Override
    public int compareTo(ToDo another) {

        //return another.getPriority().compareTo(getPriority());
        return (getCategory().compareTo(another.getCategory()));
        //return getDueDate().compareTo(another.getDueDate());
    }


}

package com.example.myappdevproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class DatabaseOperations {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertPerson(Person person) {
        ContentValues values = new ContentValues();
        values.put("name", person.getName());
        values.put("age", person.getAge());
        values.put("email", person.getEmail());

        database.insert("mytable", null, values);
    }

    public List<Person> getAllPersons() {
        List<Person> personList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM mytable", null);

        if (cursor.moveToFirst()) {
            do {
                Person person = new Person();
                person.setId(cursor.getInt(0));
                person.setName(cursor.getString(1));
                person.setAge(cursor.getInt(2));
                person.setEmail(cursor.getString(3));

                personList.add(person);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return personList;
    }
    public Person getPerson(int id) {
        Person person = null;
        String[] columns = {"_id", "name", "age", "email"};
        String selection = "_id=?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = database.query("mytable", columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndex("_id");
            int nameColumnIndex = cursor.getColumnIndex("name");
            int ageColumnIndex = cursor.getColumnIndex("age");
            int emailColumnIndex = cursor.getColumnIndex("email");

            if (idColumnIndex != -1 && nameColumnIndex != -1 && ageColumnIndex != -1 && emailColumnIndex != -1) {
                person = new Person();
                person.setId(cursor.getInt(idColumnIndex));
                person.setName(cursor.getString(nameColumnIndex));
                person.setAge(cursor.getInt(ageColumnIndex));
                person.setEmail(cursor.getString(emailColumnIndex));
            }
        }

        cursor.close();
        return person;
    }



    public void updatePerson(Person person) {
        ContentValues values = new ContentValues();
        values.put("name", person.getName());
        values.put("age", person.getAge());
        values.put("email", person.getEmail());

        database.update("mytable", values, "_id=?", new String[]{String.valueOf(person.getId())});
    }

    public void deletePerson(int id) {
        database.delete("mytable", "_id=?", new String[]{String.valueOf(id)});
    }
    public void deleteAllPersons() {
        database.delete("mytable", null, null);
    }
}
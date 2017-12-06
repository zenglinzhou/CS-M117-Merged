package com.example.android.bluetoothchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by alexmw on 12/4/17.
 */

public class VictimDatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "victimDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_PERSONS = "persons";

    // Person Table Columns
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_AGE = "age";
    private static final String KEY_USER_CONDITION = "condition";
    private static final String KEY_USER_HELP = "help";
    private static final String KEY_USER_LATITUDE = "latitude";
    private static final String KEY_USER_LONGITUDE = "longitude";

    private static VictimDatabaseHelper sInstance;

    public static synchronized VictimDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new VictimDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private VictimDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
//        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PERSONS_TABLE = "CREATE TABLE " + TABLE_PERSONS +
                "(" +
                KEY_USER_NAME + " TEXT PRIMARY KEY," +
                KEY_USER_AGE + " TEXT," +
                KEY_USER_CONDITION + " TEXT," +
                KEY_USER_HELP + " TEXT," +
                KEY_USER_LATITUDE + " REAL," +
                KEY_USER_LONGITUDE + " REAL" +
                ")";

        db.execSQL(CREATE_PERSONS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSONS);
            onCreate(db);
        }
    }

    // Insert or update a user in the database
    // Since SQLite doesn't support "upsert" we need to fall back on an attempt to UPDATE (in case the
    // user already exists) optionally followed by an INSERT (in case the user does not already exist).
    // Unfortunately, there is a bug with the insertOnConflict method
    // (https://code.google.com/p/android/issues/detail?id=13045) so we need to fall back to the more
    // verbose option of querying for the user's primary key if we did an update.
    public Boolean addOrUpdatePerson(Person person) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        Boolean out = false;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_NAME, person.getName());
            values.put(KEY_USER_AGE, person.getAge());
            values.put(KEY_USER_CONDITION, person.getCondition());
            values.put(KEY_USER_HELP, person.getHelp());
            values.put(KEY_USER_LATITUDE, person.getLat());
            values.put(KEY_USER_LONGITUDE, person.getLong());

            db.insertOrThrow(TABLE_PERSONS, null, values);
            db.setTransactionSuccessful();
            out = true;
        } catch (Exception e) {
            Log.i(TAG, "Error while trying to add or update user");
        } finally {
            db.endTransaction();

            Log.d("CHECK", Integer.toString(getAllPersons().size()));
        }

        return out;
    }

    // Get all persons in the database
    public List<Person> getAllPersons() {
        List<Person> persons = new ArrayList<>();

        // SELECT * FROM PERSONS
        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_PERSONS);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    final Person person = new Person();
                    final String name = cursor.getString(cursor.getColumnIndex(KEY_USER_NAME));
                    final String age = cursor.getString(cursor.getColumnIndex(KEY_USER_AGE));
                    final String condition = cursor.getString(cursor.getColumnIndex(KEY_USER_CONDITION));
                    final String help = cursor.getString(cursor.getColumnIndex(KEY_USER_HELP));
                    final Double latitude = cursor.getDouble(cursor.getColumnIndex(KEY_USER_LATITUDE));
                    final Double longitude = cursor.getDouble(cursor.getColumnIndex(KEY_USER_LONGITUDE));
                    person.setName(name);
                    person.setAge(age);
                    person.setCondition(condition);
                    person.setHelp(help);
                    person.setLocation(latitude, longitude);
                    persons.add(person);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return persons;
    }

    // Get all persons in the database
    public List<Person> insertNewPersons(List<Person> newList) {
        List<Person> persons = new ArrayList<>();

        // SELECT * FROM PERSONS
        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_PERSONS);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);

        for (final Person p : newList) {
            if (cursor.moveToFirst()) {
                do {
                    final String name = cursor.getString(cursor.getColumnIndex(KEY_USER_NAME));
                    if (name == p.getName()) {
                        break;
                    }
                    if (cursor.isLast()) {
                        if (addOrUpdatePerson(p)) {
                            persons.add(p);
                        }
                    }
                } while(cursor.moveToNext());
            }
        }
        return persons;
    }

    // Update the user's profile picture url
    public int updatePersonLocation(Person person) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_LATITUDE, person.getLat());
        values.put(KEY_USER_LONGITUDE, person.getLong());

        // Updating profile picture url for user with that userName
        return db.update(TABLE_PERSONS, values, KEY_USER_NAME + " = ?",
                new String[] { String.valueOf(person.getName()) });
    }

    // Delete all posts and users in the database
//    public void deleteAllPostsAndUsers() {
//        SQLiteDatabase db = getWritableDatabase();
//        db.beginTransaction();
//        try {
//            // Order of deletions is important when foreign key relationships exist.
//            db.delete(TABLE_POSTS, null, null);
//            db.delete(TABLE_USERS, null, null);
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            Log.d(TAG, "Error while trying to delete all posts and users");
//        } finally {
//            db.endTransaction();
//        }
//    }
}
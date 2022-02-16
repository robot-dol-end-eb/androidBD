package ru.ex.app25;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class NotesHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "NotesDB";
    SQLiteDatabase db;

    public static final class Notes {
        public static final String TABLE = "TestTable";
        public static final String COLUMN_SECOND_NAME = "second_name";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_AGE = "age";

        public static String getCreateStatement() {
            String query = String.format(
                    "CREATE TABLE `%s` (" +
                            "`%s` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "`%s` TEXT, " +
                            "`%s` TEXT, " +
                            "`%s` INTEGER" +
                            ")",
                    TABLE,
                    BaseColumns._ID,
                    COLUMN_SECOND_NAME,
                    COLUMN_FIRST_NAME,
                    COLUMN_AGE
            );
            Log.wtf("test", "getCreateStatement: " + query);
            return query;
        }

        public static String getNote(Cursor cursor) {
            int second_name_id = cursor.getColumnIndex(COLUMN_SECOND_NAME);
            int first_name_id = cursor.getColumnIndex(COLUMN_FIRST_NAME);
            int age_id = cursor.getColumnIndex(COLUMN_AGE);

            String second_name = cursor.getString(second_name_id);
            String first_name = cursor.getString(first_name_id);
            String age = cursor.getString(age_id);

            return second_name + " " + first_name + " " + age;
        }

        public static long insertNote(SQLiteDatabase db, String second_name, String first_name, int age) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SECOND_NAME, second_name);
            values.put(COLUMN_FIRST_NAME, first_name);
            values.put(COLUMN_AGE, age);
            return db.insert(TABLE, null, values);
        }

        public static long clearNotes(SQLiteDatabase db) {
            return db.delete(TABLE, null, null);
        }
    }

    public NotesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Notes.getCreateStatement());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
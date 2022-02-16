package ru.ex.app25;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Main extends AppCompatActivity implements TextWatcher, AdapterView.OnItemClickListener {
    private NotesHelper dbHelper;
    private Cursor cursor;

    private Button btnAdd, btnView, btnClear, btnSearch, btnEdit, btnDelete;

    private EditText etSecondName, etFirstName, etAge;
    private ListView lv;
    private ArrayAdapter<String> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnAdd = findViewById(R.id.btn_add);
        btnView = findViewById(R.id.btn_view);
        btnClear = findViewById(R.id.btn_clear);
        btnSearch = findViewById(R.id.btn_search);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);

        etSecondName = findViewById(R.id.et_second_name);
        etSecondName.addTextChangedListener(this);
        etFirstName = findViewById(R.id.et_first_name);
        etFirstName.addTextChangedListener(this);
        etAge = findViewById(R.id.et_age);
        etAge.addTextChangedListener(this);

        lv = findViewById(R.id.lv);
        list = new ArrayAdapter<String>(this, R.layout.item);
        lv.setAdapter(list);
        lv.setOnItemClickListener(this);

        dbHelper = new NotesHelper(this);
        view();

        btnsEnabled();
    }

    public int getNotesCount() {
        return cursor.getCount();
    }

    public void loadNotes() {
        cursor = dbHelper.getReadableDatabase().rawQuery(String.format(
                "SELECT * FROM `%s`",
                NotesHelper.Notes.TABLE),
                null
        );
    }

    public String getNote(int index) {
        cursor.moveToPosition(index);
        return NotesHelper.Notes.getNote(cursor);
    }

    public void addNote(String second_name, String first_name, int age) {
        NotesHelper.Notes.insertNote(
                dbHelper.getWritableDatabase(),
                second_name,
                first_name,
                age
        );
        dbHelper.close();
    }

    public void add(View view) {
        addNote(etSecondName.getText().toString(), etFirstName.getText().toString(), Integer.parseInt(etAge.getText().toString()));
    }

    public void view(View view) {
        view();
    }

    private void view() {
        list.clear();
        loadNotes();
        if (cursor.moveToFirst()) {
            do {
                list.add(NotesHelper.Notes.getNote(cursor));
            } while (cursor.moveToNext());
        } else {
            list.add("Database is empty");
        }
        dbHelper.close();
    }

    public void clear(View view) {
        NotesHelper.Notes.clearNotes(dbHelper.getWritableDatabase());
        dbHelper.close();
        view();
    }

    private String getWhereString() {
        String where_str = "";
        where_str += etSecondName.getText().toString().equals("") ? "" : ("`" + NotesHelper.Notes.COLUMN_SECOND_NAME + "` LIKE '%" + etSecondName.getText().toString() + "%' AND ");
        where_str += etFirstName.getText().toString().equals("") ? "" : ("`" + NotesHelper.Notes.COLUMN_FIRST_NAME + "` LIKE '%" + etFirstName.getText().toString() + "%' AND ");
        where_str += etAge.getText().toString().equals("") ? "" : ("`" + NotesHelper.Notes.COLUMN_AGE + "` = '" + etAge.getText().toString() + "' AND ");
        where_str += "1 = 1";
        return where_str;
    }

    private void search() {
        list.clear();

        cursor = dbHelper.getReadableDatabase().rawQuery(String.format(
                "SELECT * FROM `%s` WHERE %s",
                NotesHelper.Notes.TABLE,
                getWhereString()
        ), null);

        if (cursor.moveToFirst()) {
            do {
                list.add(NotesHelper.Notes.getNote(cursor));
            } while (cursor.moveToNext());
        } else {
            list.add("Search failed");
        }
        dbHelper.close();
    }

    public void search(View view) {
        search();
    }

    private boolean is_edit = false;
    private String where_edit;

    public void edit(View view) {
        is_edit = !is_edit;
        btnsEnabled();
        if (is_edit) {
            btnView.setEnabled(false);
            btnClear.setEnabled(false);

            where_edit = getWhereString();
        } else {
            btnView.setEnabled(true);
            btnClear.setEnabled(true);

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();
            boolean empty = true;
            if (!etSecondName.getText().toString().equals("")) {
                empty = false;
                cv.put(NotesHelper.Notes.COLUMN_SECOND_NAME, etSecondName.getText().toString());
            }
            if (!etFirstName.getText().toString().equals("")) {
                empty = false;
                cv.put(NotesHelper.Notes.COLUMN_FIRST_NAME, etFirstName.getText().toString());
            }
            if (!etAge.getText().toString().equals("")) {
                empty = false;
                cv.put(NotesHelper.Notes.COLUMN_AGE, Integer.parseInt(etAge.getText().toString()));
            }

            if (!empty) {
                db.update(NotesHelper.Notes.TABLE, cv, where_edit, null);
            }

            db.close();
        }
    }

    public void delete(View view) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(NotesHelper.Notes.TABLE, getWhereString(), null);
        db.close();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    private void btnsEnabled() {
        if (is_edit) {
            btnAdd.setEnabled(false);
            btnSearch.setEnabled(false);
            btnDelete.setEnabled(false);
            return;
        }
        if (!etSecondName.getText().toString().equals("") && !etFirstName.getText().toString().equals("") && !etAge.getText().toString().equals("")) {
            btnAdd.setEnabled(true);
        } else {
            btnAdd.setEnabled(false);
        }
        if (!etSecondName.getText().toString().equals("") || !etFirstName.getText().toString().equals("") || !etAge.getText().toString().equals("")) {
            btnSearch.setEnabled(true);
            btnDelete.setEnabled(true);
        } else {
            btnSearch.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        btnsEnabled();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String text = ((TextView)view).getText().toString();
        String[] arr = text.split(" ");
        etSecondName.setText(arr[0]);
        etFirstName.setText(arr[1]);
        etAge.setText(arr[2]);
    }
}


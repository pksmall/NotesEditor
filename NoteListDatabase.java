package gb.pavelkorzhenko.a2l1menuapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gb.pavelkorzhenko.a2l1menuapp.databases.DatabaseHelper;

/**
 * Created by small on 12/11/2017.
 */

public class NoteListDatabase {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public static final String TABLE_NOTES = "notelists"; // название таблицы в бд
    // названия столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_HASHID = "hashid";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_BODY = "body";
    public static final String COLUMN_GEOBODY = "geobody";
    public static final String COLUMN_PUBDATE = "pubdate";
    public static final String COLUMN_UPDDATE = "upddate";

    private String[] notesAllColumn = {
            COLUMN_ID,
            COLUMN_HASHID,
            COLUMN_TITLE,
            COLUMN_BODY,
            COLUMN_GEOBODY,
            COLUMN_PUBDATE,
            COLUMN_UPDDATE
    };

    public NoteListDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        // dbHelper.getReadableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public NoteLists addNote(String title, String body, String geobody) {
        ContentValues values = new ContentValues();
        long hashID = body.hashCode() + title.hashCode();
        Date currentDate = new Date();

        values.put(COLUMN_HASHID, hashID);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_BODY, body);
        values.put(COLUMN_GEOBODY, geobody);

        // create note
        NoteLists newNote = new NoteLists();
        newNote.setTxtTitle(title);
        newNote.setTxtBody(body);
        newNote.setTxtGeoBody(geobody);
        newNote.setTxtPubDate(currentDate);
        newNote.setTxtUpdDate(currentDate);
        newNote.setHashId();

        // save date
        values.put(COLUMN_PUBDATE, newNote.getTxtPubDate());
        values.put(COLUMN_UPDDATE, newNote.getTxtUpdDate());

        long insertId = database.insert(TABLE_NOTES, null,
                values);

        // continue create note
        newNote.setId(insertId);

        return newNote;
    }

    public void editNote(long id, String title,String body, String geobody) {
        ContentValues editedNote = new ContentValues();
        long hashID = body.hashCode() + title.hashCode();
        String currentDate = NoteLists.sdf.format((new Date()));
        editedNote.put(COLUMN_ID, id);
        editedNote.put(COLUMN_HASHID, hashID);
        editedNote.put(COLUMN_TITLE, title);
        editedNote.put(COLUMN_BODY, body);
        editedNote.put(COLUMN_GEOBODY, geobody);
        editedNote.put(COLUMN_UPDDATE, currentDate);

        int rows = database.update(TABLE_NOTES, editedNote, COLUMN_ID + "= " + id, null);
        Log.d("DETEALACTIVITY", "ID: " + id + " TitleE: " + title + " Rows:" + rows);

        //Делает тоже самое, но через аргументы
        /*database.update(TABLE_NOTES, editedNote, TABLE_NOTES + "= ?", new String[] {String.valueOf(id)});*/
    }

    public void deleteNote(long id) {
        int rowCount = database.delete(TABLE_NOTES, COLUMN_ID + " = " + id, null);
        Log.d("NOTELISTDATABASE", "deleted rows count = " + rowCount);
    }

    public void deleteAll() {
        database.delete(TABLE_NOTES, null, null);
    }

    public NoteLists getNoteById(int id) {
        NoteLists note = new NoteLists();

        Cursor cursor = database.query(TABLE_NOTES,
                notesAllColumn, COLUMN_ID + " = " + id, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            note = cursorToNote(cursor);
        }

        cursor.close();
        return note;
    }

    public List<NoteLists> getAllNotes() {
        List<NoteLists> notes = new ArrayList<>();

        Cursor cursor = database.query(TABLE_NOTES,
                notesAllColumn, null, null, null, null, null);
        /*Cursor rawCursor = database.rawQuery("SELECT * FROM " + TABLE_NOTES, null);
        database.execSQL("UPDATE " + TABLE_NOTES + " SET " + COLUMN_NOTE + " = note WHERE "
                + COLUMN_ID + " = 1");*/

        if (cursor != null && cursor.moveToFirst()) {
            do {
                NoteLists note = cursorToNote(cursor);
                notes.add(note);
            } while (cursor.moveToNext());
        }

        // обязательно закройте cursor
        cursor.close();
        return notes;
    }

    private NoteLists cursorToNote(Cursor cursor) {
        NoteLists note = new NoteLists();

        //В базах данных SQLLite есть только три типа: FLOAT, INTEGER, TEXT
        //но Android позволяет вам при извечении привести их к Java типам

        int columnIdIdx = cursor.getColumnIndex(COLUMN_ID);
        int columnHashIdIdx = cursor.getColumnIndex(COLUMN_HASHID);
        int columnTitleIdx = cursor.getColumnIndex(COLUMN_TITLE);
        int columnBodyIdx = cursor.getColumnIndex(COLUMN_BODY);
        int columnGeoBodyIdx = cursor.getColumnIndex(COLUMN_GEOBODY);
        int columnPubDateIdx = cursor.getColumnIndex(COLUMN_PUBDATE);
        int columnUpdDateIdx = cursor.getColumnIndex(COLUMN_UPDDATE);

        note.setId(cursor.getLong(columnIdIdx));
        note.setTxtTitle(cursor.getString(columnTitleIdx));
        note.setTxtBody(cursor.getString(columnBodyIdx));
        note.setTxtGeoBody(cursor.getString(columnGeoBodyIdx));
        note.setTxtPubDate(cursor.getString(columnPubDateIdx));
        note.setTxtUpdDate(cursor.getString(columnUpdDateIdx));
        note.setHashId(cursor.getLong(columnHashIdIdx));

        return note;
    }


    public List<NoteLists> getNotesByString(String query) {
        List<NoteLists> notes = new ArrayList<>();

        Cursor cursor = database.query(TABLE_NOTES,
                notesAllColumn,
                COLUMN_TITLE + " LIKE ?" + " OR " +
                COLUMN_BODY +  " LIKE ?", new String[] { "%"+ query + "%", "%"+ query + "%" },
                null, null, COLUMN_ID, null);

/*        String sqlquery = "SELECT * FROM " + TABLE_NOTES + " WHERE " + COLUMN_TITLE + " LIKE '%" + query + "%'" +
                " OR " + COLUMN_BODY + " LIKE '%" + query + "%'";
        Log.d("LISTNOTEDB", "sqlquery: " + sqlquery);
        Cursor cursor = database.rawQuery(sqlquery, null);
        database.execSQL(sqlquery); */

        if (cursor != null && cursor.moveToFirst()) {
            do {
                NoteLists note = cursorToNote(cursor);
                notes.add(note);
            } while (cursor.moveToNext());
        }

        // обязательно закройте cursor
        cursor.close();
        return notes;
    }
}

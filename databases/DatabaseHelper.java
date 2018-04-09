package gb.pavelkorzhenko.a2l1menuapp.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import gb.pavelkorzhenko.a2l1menuapp.NoteListDatabase;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notelists.db"; // название бд
    private static final int DATABASE_VERSION = 2; // версия базы данных

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + NoteListDatabase.TABLE_NOTES +
                " ("
                    + NoteListDatabase.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + NoteListDatabase.COLUMN_HASHID + " INTEGER,"
                    + NoteListDatabase.COLUMN_TITLE + " TEXT,"
                    + NoteListDatabase.COLUMN_BODY + " TEXT,"
                    + NoteListDatabase.COLUMN_GEOBODY + " TEXT,"
                    + NoteListDatabase.COLUMN_PUBDATE + " TEXT,"
                    + NoteListDatabase.COLUMN_UPDDATE + " TEXT" +
                " );"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NoteListDatabase.TABLE_NOTES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}

package gb.pavelkorzhenko.a2l1menuapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import gb.pavelkorzhenko.a2l1menuapp.adapters.ListViewAdapterSimple;

public class NoteListEdit extends AppCompatActivity implements IConstant {

    protected ImageButton btnGeoShow;
    protected EditText editTitleView;
    protected EditText editBodyView;
    protected EditText editGeoBodyView;
    protected int indexID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSaveBeheivor(view);
            }
        });

        Intent getMainData = getIntent();
        indexID = getMainData.getIntExtra(TXTINDEX, -1);
        Log.d("DETEALACTIVITY", "ID: " + indexID);
        if (getMainData != null && indexID != -1) {
            String txtTitle = getMainData.getStringExtra(TXTTITLE);
            String txtBody = getMainData.getStringExtra(TXTBODY);
            String geoBody = getMainData.getStringExtra(TXTGEOBODY);
            editTitleView =  findViewById(R.id.editTitle);
            editBodyView = findViewById(R.id.editBody);
            editGeoBodyView = findViewById(R.id.editGeoBody);
            editTitleView.setText(txtTitle);
            editBodyView.setText(txtBody);
            editGeoBodyView.setText(geoBody);
        }

        btnGeoShow = findViewById(R.id.btnIdGeoShow);
        btnGeoShow.setOnClickListener(btnGeoShowListener);
    }

    private void saveDatabaseData(Context context, int idx, String title, String body, String geobody) {
        NoteListDatabase noteListDatabase = new NoteListDatabase(context);
        noteListDatabase.open();
        if (idx == -1) {
            if (title.length() > 0 || body.length() > 0 || geobody.length() > 0) {
                noteListDatabase.addNote(title, body, geobody);
            }
        } else {
            noteListDatabase.editNote(idx, title, body, geobody);
        }
        noteListDatabase.close();
    }

    private void btnBackHandler() {
        Intent backIntent=new Intent(getApplicationContext(), MenuActivity.class);
        editTitleView = findViewById(R.id.editTitle);
        editBodyView = findViewById(R.id.editBody);
        editGeoBodyView = findViewById(R.id.editGeoBody);

        //save data in database
        if (indexID == -1) {
            saveDatabaseData(getApplicationContext(), -1,
                    editTitleView.getText().toString(),
                    editBodyView.getText().toString(),
                    editGeoBodyView.getText().toString());
        } else {
            saveDatabaseData(getApplicationContext(), indexID,
                    editTitleView.getText().toString(),
                    editBodyView.getText().toString(),
                    editGeoBodyView.getText().toString());
        }

        backIntent.putExtra(TXTINDEX, indexID);
        backIntent.putExtra(TXTTITLE, editTitleView.getText().toString());
        backIntent.putExtra(TXTBODY, editBodyView.getText().toString());
        backIntent.putExtra(TXTGEOBODY, editGeoBodyView.getText().toString());
        Log.d("DETEALACTIVITY", "Title: " + editTitleView.getText() + " Body:" + editBodyView.getText() );
        setResult(RESULT_OK,backIntent);
        finish();
        //startActivity(backIntent);
    }

    private void btnSaveBeheivor(View view) {
        Snackbar.make(view, "Save Note", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        btnBackHandler();
    }

    View.OnClickListener btnGeoShowListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            editGeoBodyView = findViewById(R.id.editGeoBody);
            String geo = "geo:0, 0?z=20&q=" + editGeoBodyView.getText().toString();
            // Create intent object
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geo));
            // You could specify package for use the GoogleMaps app, only
            //intent.setPackage("com.google.android.apps.maps");
            // Start maps activity
            startActivity(intent);
        }
    };

}

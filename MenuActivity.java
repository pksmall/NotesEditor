package gb.pavelkorzhenko.a2l1menuapp;

import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import gb.pavelkorzhenko.a2l1menuapp.adapters.ListViewAdapterSimple;
import gb.pavelkorzhenko.a2l1menuapp.adapters.RecyclerViewAdapter;
import gb.pavelkorzhenko.a2l1menuapp.widgets.NoteWidget;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MenuActivity extends AppCompatActivity
        implements IConstant {

    View globalView;
    ListView listView;
    //RecyclerViewAdapter recyclerViewAdapter;
    ListViewAdapterSimple listViewAdapterSimple;
    int currentPosition = -1;
    TextView textNoTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listViewAdapterSimple = new ListViewAdapterSimple(getApplicationContext());
        listViewAdapterSimple.readFromDatabase();
        //readNotesFromInternalStorage();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNoteEditActivity(-1);
            }
        });

        initListView();
        /* RecyclerView implements
         * recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext());
         * recyclerViewAdapter.readFromDatabase(); // database init/open
         * initRecylerView();
         */
        UpdateWidgets();
        handleIntent(getIntent());
        //registerForContextMenu(listView);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            Log.d("MAINACTIVITY", "Search: " + query);
            listViewAdapterSimple.searchByString(query);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

//    private void initRecylerView() {
//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//        linearLayoutManager.setOrientation(1);  //vertical orient, 0 - horizontal
//
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setAdapter(recyclerViewAdapter);
//        recyclerView.setLongClickable(true);
//    }

    private void setNoTitle() {
        Log.d("MAINMYACTIVITY","set title #" + listViewAdapterSimple.getCount());
        textNoTitle = findViewById(R.id.txtNoTitle);
        if (listViewAdapterSimple.getCount() == 0) {
            textNoTitle.setVisibility(VISIBLE);
            textNoTitle.setText(getResources().getString(R.string.noNotes));
        } else {
            textNoTitle.setVisibility(GONE);
        }
        listViewAdapterSimple.updateList();
    }

    private void getNoteEditActivity(int idx) {
        Intent intent = new Intent(getApplicationContext(), NoteListEdit.class);
        if (idx != -1) {
            NoteLists noteItem = (NoteLists) listViewAdapterSimple.getItem(idx);
            intent.putExtra(TXTTITLE, noteItem.getTxtTitle());
            intent.putExtra(TXTBODY, noteItem.getTxtBody());
            intent.putExtra(TXTGEOBODY, noteItem.getTxtGeoBody());
            intent.putExtra(TXTINDEX, (int) noteItem.getId());
        }
        startActivityForResult(intent, guardCode);
    }

    // create list
    private void initListView() {
        listView = findViewById(R.id.listView);
        listView.setAdapter(listViewAdapterSimple);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getNoteEditActivity(position);
                currentPosition = position;
            }
        });

        listView.setMultiChoiceModeListener(new ListView.MultiChoiceModeListener() {
            int checkedCount;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                getSupportActionBar().hide();
                // Capture total checked items
                checkedCount = listView.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                listViewAdapterSimple.toggleSelection(position);
                mode.invalidate();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.modal_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                if (checkedCount == 1){
                    MenuItem item = menu.findItem(R.id.menu_edit_modal);
                    item.setVisible(true);
                } else {
                    MenuItem item = menu.findItem(R.id.menu_edit_modal);
                    item.setVisible(false);
                }
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                SparseBooleanArray selected;

                int id = item.getItemId();
                switch (id) {
                    case R.id.menu_edit_modal:
                        selected = listViewAdapterSimple.getSelectedIds();
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                getNoteEditActivity(selected.keyAt(i));
                                break;
                            }
                        }
                        UpdateWidgets();
                        return true;
                    case R.id.menu_delete:
                        selected = listViewAdapterSimple.getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                NoteLists selectedItem = (NoteLists) listViewAdapterSimple.getItem(selected.keyAt(i));
                                // Remove selected items following the ids
                                listViewAdapterSimple.remove(selectedItem);
                            }
                        }
                        mode.finish(); // Action picked, so close the CAB
                        UpdateWidgets();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                getSupportActionBar().show();
                listViewAdapterSimple.removeSelection();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if(requestCode == guardCode){
            if(resultCode == RESULT_OK){
                Log.d("MAINMYACTIVITY","Result OK");
                UpdateWidgets();
                saveNotesToInternalStorage();
            }
        //}
    }

    private void UpdateWidgets() {
        Intent intent = new Intent(this, NoteWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), NoteWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
        setNoTitle();
    }

    public void readNotesFromInternalStorage() {
        String path = getFilesDir() + "/" + internalFileName;
        listViewAdapterSimple.readFromFile(path);
    }

    public void saveNotesToInternalStorage() {
        String path = getFilesDir() + "/" + internalFileName;
        listViewAdapterSimple.saveToFile(path);
    }

    public void saveNotesToExtStorage() {
        String path;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/" + externalFileName;
        } else {
            File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
            if(!docsFolder.exists()) {
                docsFolder.mkdir();
            }
            path = docsFolder.getAbsolutePath() + "/" + externalFileName;
        }
        listViewAdapterSimple.saveToFile(path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) MenuActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MenuActivity.this.getComponentName()));
        }
        //searchItem.expandActionView();
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // open search
                //Toast.makeText(MenuActivity.this, "onMenuItemActionExpand called", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // close search
                //Toast.makeText(MenuActivity.this, "onMenutItemActionCollapse called", Toast.LENGTH_SHORT).show();
                listViewAdapterSimple.searchByString("");
                listViewAdapterSimple.updateList();
                return true;
            }
        });
        // this solution isn't worked
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                Toast.makeText(MenuActivity.this, "onMenuItemActionExpand called", Toast.LENGTH_SHORT).show();
//                listViewAdapterSimple.searchByString("");
//                listViewAdapterSimple.updateList();
//                return true;
//            }
//        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return handleMenuItemClick(globalView, item.getItemId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return handleMenuItemClick(globalView, item.getItemId());
    }

    private boolean handleMenuItemClick(View view, int id) {
        switch (id) {
            case R.id.menu_add:
                getNoteEditActivity(-1);
                UpdateWidgets();
                return true;
            case R.id.menu_edit:
                getNoteEditActivity(currentPosition);
                UpdateWidgets();
                return true;
            case R.id.menu_delete:
                if (currentPosition >= 0) {
                    listViewAdapterSimple.deleteElement(currentPosition);
                }
                saveNotesToInternalStorage();
                UpdateWidgets();
                return true;
            case R.id.menu_clear:
                listViewAdapterSimple.clearList();
                saveNotesToInternalStorage();
                UpdateWidgets();
                return true;
            default:
                return false;
        }
    }

    /**
     * Override states
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("MAINMYACTIVITY", "Main onRestart()");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MAINMYACTIVITY", "Main onStart()");
    }
    @Override
    protected void onResume() {
        super.onResume();
        UpdateWidgets();
        listViewAdapterSimple.updateList();
        setNoTitle();
        Log.d("MAINMYACTIVITY", "Main onResume()");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MAINMYACTIVITY", "Main onPause()");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MAINMYACTIVITY", "Main onStop()");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MAINMYACTIVITY", "Main onDestroy()");
    }
}

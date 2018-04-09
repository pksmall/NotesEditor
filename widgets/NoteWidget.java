package gb.pavelkorzhenko.a2l1menuapp.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import gb.pavelkorzhenko.a2l1menuapp.IConstant;
import gb.pavelkorzhenko.a2l1menuapp.NoteListDatabase;
import gb.pavelkorzhenko.a2l1menuapp.NoteListEdit;
import gb.pavelkorzhenko.a2l1menuapp.NoteLists;
import gb.pavelkorzhenko.a2l1menuapp.R;

/**
 * Implementation of App Widget functionality.
 */
public class NoteWidget extends AppWidgetProvider implements IConstant {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews rViews = new RemoteViews(context.getPackageName(), R.layout.note_widget);
        //rViews.setTextViewText(R.id.txtWidgetNoNotes, "No Notes");
        setList(rViews, context, appWidgetId);
        setAddButtonClick(rViews, context);
        setListClick(rViews, context);
        appWidgetManager.updateAppWidget(appWidgetId, rViews);
    }

    private static void setListClick(RemoteViews rViews, Context context) {
        Intent addButtonIntent = new Intent(context, NoteWidget.class);
        addButtonIntent.setAction(ITEM_ON_CLICK_ACTION);
        PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0,
                addButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rViews.setPendingIntentTemplate(R.id.listWidgetNotesView, listClickPIntent);
    }

    private static void setAddButtonClick(RemoteViews rViews, Context context) {
        Intent addButtonIntent = new Intent(context, NoteWidget.class);
        addButtonIntent.setAction(ADD_BUTTON_ON_CLICK_ACTION);
        PendingIntent addButtonPIntent = PendingIntent.getBroadcast(context, 0,
                addButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rViews.setOnClickPendingIntent(R.id.btnNoteWidgetAddButton, addButtonPIntent);
    }

    private static void setList(RemoteViews rViews, Context context, int appWidgetId) {
        Intent adapter = new Intent(context, NoteWidgetService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        rViews.setRemoteAdapter(R.id.listWidgetNotesView, adapter);
        rViews.setTextViewText(R.id.txtWidgetNoNotes, context.getResources().getString(R.string.noNotes));
        rViews.setEmptyView(R.id.listWidgetNotesView, R.id.txtWidgetNoNotes);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(context);
        if (intent.getAction().equalsIgnoreCase(UPDATE_MEETING_ACTION)) {
            int appWidgetIds[] = mAppWidgetManager.getAppWidgetIds(new ComponentName(context, NoteWidget.class));
            mAppWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listWidgetNotesView);
        }
        if (intent.getAction().equalsIgnoreCase(ITEM_ON_CLICK_ACTION)) {
            int viewIndex = intent.getIntExtra(NOTE_WIDGET_EXTRA_ITEM, 0);
            Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
            getNoteEditActivity(context, viewIndex);
        }
        if (intent.getAction().equalsIgnoreCase(ADD_BUTTON_ON_CLICK_ACTION)) {
            //int viewIndex = intent.getIntExtra(NOTE_WIDGET_EXTRA_ITEM, 0);
            Toast.makeText(context, "Add Button Click. Run Edit Note Activity", Toast.LENGTH_SHORT).show();
            getNoteEditActivity(context, -1);
        }
        super.onReceive(context, intent);
    }

    private void  getNoteEditActivity(Context context, int idx) {
        Intent intent = new Intent(context, NoteListEdit.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (idx != -1) {
            NoteListDatabase noteListDatabase = new NoteListDatabase(context);
            noteListDatabase.open();
            NoteLists noteItem = noteListDatabase.getNoteById(idx);
            noteListDatabase.close();

            intent.putExtra(TXTTITLE, noteItem.getTxtTitle());
            intent.putExtra(TXTBODY, noteItem.getTxtBody());
            intent.putExtra(TXTGEOBODY, noteItem.getTxtGeoBody());
            intent.putExtra(TXTINDEX, idx);
        }
        context.startActivity(intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


package gb.pavelkorzhenko.a2l1menuapp.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import gb.pavelkorzhenko.a2l1menuapp.IConstant;
import gb.pavelkorzhenko.a2l1menuapp.NoteListDatabase;
import gb.pavelkorzhenko.a2l1menuapp.NoteLists;
import gb.pavelkorzhenko.a2l1menuapp.R;

/**
 * Created by small on 12/14/2017.
 */

public class NoteWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    Context mContext;
    Intent mIntent;
    String geoText;

    private List<NoteLists> elements;

    public NoteWidgetFactory(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
    }

    @Override
    public void onCreate() {
        elements = new ArrayList<NoteLists>();
        onDataSetChanged();
    }

    @Override
    public void onDataSetChanged() {
        elements.clear();
        NoteListDatabase noteListDatabase = new NoteListDatabase(mContext);
        noteListDatabase.open();
        elements = noteListDatabase.getAllNotes();
        noteListDatabase.close();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return elements.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rView = new RemoteViews(mContext.getPackageName(), R.layout.notewidget_item);

        // sent OnClick
        // Next, set a fill-intent, which will be used to fill in the pending intent template
        // that is set on the collection view in StackWidgetProvider.
        Bundle extras = new Bundle();
        extras.putInt(IConstant.NOTE_WIDGET_EXTRA_ITEM, (int) elements.get(position).getId());
        mIntent.putExtras(extras);
        rView.setOnClickFillInIntent(R.id.layoutNoteWidgetItem, mIntent);

        //set text
        rView.setTextViewText(R.id.txtWidgetNoteTitle, elements.get(position).getTxtTitle());
        rView.setTextViewText(R.id.txtWidgetPubDateHint, elements.get(position).getTxtPubDate());
        if (elements.get(position).getTxtGeoBody().length() > 0) {
            geoText = mContext.getResources().getString(R.string.getHint) + mContext.getResources().getString(R.string.strYes);
        } else {
            geoText = mContext.getResources().getString(R.string.getHint) + mContext.getResources().getString(R.string.strNo);
        }
        rView.setTextViewText(R.id.txtWidgetGeoHint, geoText);

        return rView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return elements.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

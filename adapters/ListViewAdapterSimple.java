package gb.pavelkorzhenko.a2l1menuapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import gb.pavelkorzhenko.a2l1menuapp.NoteListDatabase;
import gb.pavelkorzhenko.a2l1menuapp.NoteLists;
import gb.pavelkorzhenko.a2l1menuapp.R;

/**
 * Created by small on 11/30/2017.
 */

public class ListViewAdapterSimple extends BaseAdapter {
    private List<NoteLists> elements = new ArrayList<NoteLists>();
    private Context context;
    private LayoutInflater layoutInflater;
    private SparseBooleanArray mSelectedItemsIds;
    NoteListDatabase noteListDatabase;
    private  String query;

    public ListViewAdapterSimple(Context context) {
        this.context = context;
        this.query = "";
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return elements.size();
    }

    @Override
    public Object getItem(int position) { return elements.get(position);  }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // do with dataset
    public void addNewElement(String title, String body, String geoBody) {
        NoteLists element = noteListDatabase.addNote(title,body,geoBody);
        elements.add(element);
        notifyDataSetChanged();
    }

    public void deleteElement(int id) {
        int idx = id % elements.size();
        Log.d("LISTAPAPTER","delElement - id:" + id + " idx:" + idx + " size:" + elements.size());
        if (elements.size() > 0) {
            noteListDatabase.deleteNote(elements.get(idx).getId());
            elements.remove(idx);
            notifyDataSetChanged();
        }
    }

    public void searchByString(String query) {
        this.query = query;
    }

    public void updateList() {
        noteListDatabase.close();
        readFromDatabase();
        notifyDataSetChanged();
    }

    public void remove(NoteLists object) {
        Log.d("LISTAPAPTER","remove - id:" + object.getId() + " title:" + object.getTxtTitle());
        noteListDatabase.deleteNote(object.getId());
        elements.remove(object);
        notifyDataSetChanged();
    }

    public void editElement(int id, String title, String body, String geoBody) {
        if (elements.size() > 0 ) {
            elements.get(id).setTxtTitle(title);
            elements.get(id).setTxtBody(body);
            elements.get(id).setTxtGeoBody(geoBody);
            elements.get(id).setHashId();
            noteListDatabase.editNote(elements.get(id).getId(), title, body, geoBody);
            notifyDataSetChanged();
        }
    }
    public void clearList() {
        elements.clear();
        noteListDatabase.deleteAll();
        notifyDataSetChanged();
    }

    class NoteHolder {
        TextView txtTitle;
        TextView txtGeoHint;
        TextView txtPubDate;
        TextView txtUpdDate;
        TextView txtBody;
        RelativeLayout relativeLayout;
    }

    @SuppressLint("CutPasteId")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NoteHolder holder;
        if (convertView == null) {
            holder = new NoteHolder();
            convertView = layoutInflater.inflate(R.layout.list_view_item, parent, false);
            holder.relativeLayout = convertView.findViewById(R.id.relativeLayout);
            holder.txtTitle = convertView.findViewById(R.id.element_text);
            holder.txtGeoHint = convertView.findViewById(R.id.txtGeoHint);
            holder.txtPubDate = convertView.findViewById(R.id.txtPubDateHint);
            holder.txtUpdDate = convertView.findViewById(R.id.txtUpdateDateHint);
            holder.txtBody = convertView.findViewById(R.id.txtBody);
            convertView.setTag(holder);
        } else {
            holder = (NoteHolder) convertView.getTag();
        }

        if (mSelectedItemsIds.get(position)) {
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#FF9912"));
        } else {
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        String geoText;
        if (elements.get(position).getTxtGeoBody().length() > 0) {
            geoText = context.getResources().getString(R.string.getHint) + context.getResources().getString(R.string.strYes);
            holder.txtGeoHint.setText(geoText);
        } else {
            geoText = context.getResources().getString(R.string.getHint) + context.getResources().getString(R.string.strNo);
            holder.txtGeoHint.setText(geoText);
        }
        holder.txtTitle.setText(elements.get(position).getTxtTitle());
        holder.txtPubDate.setText(elements.get(position).getTxtPubDate());
        holder.txtUpdDate.setText(elements.get(position).getTxtUpdDate());
        holder.txtBody.setText(elements.get(position).getTxtBody());

        return convertView;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    // file operations
    public void readFromDatabase() {
        noteListDatabase = new NoteListDatabase(context);
        noteListDatabase.open();

        if (this.query.length() > 0) {
            Log.d("LISTAPAPTER","Query: " + this.query);
            this.elements = noteListDatabase.getNotesByString(this.query);
        } else {
            Log.d("LISTAPAPTER","qUery: " + this.query);
            this.elements = noteListDatabase.getAllNotes();
        }
    }

    public void saveToFile(String fileName) {
        File file;
        try {
            file = new File(fileName);

            FileOutputStream fileOutputStream;
            ObjectOutputStream objectOutputStream;

            if(!file.exists()) {
                file.createNewFile();
            }

            fileOutputStream = new FileOutputStream(file, false);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(elements);

            objectOutputStream.flush();
            fileOutputStream.flush();
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public void readFromFile(String fileName) {
        FileInputStream fileInputStream;
        ObjectInputStream objectInputStream;

        try {
            fileInputStream = new FileInputStream(fileName);
            objectInputStream = new ObjectInputStream(fileInputStream);

            this.elements = (List<NoteLists>) objectInputStream.readObject();

            objectInputStream.close();
            fileInputStream.close();

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}

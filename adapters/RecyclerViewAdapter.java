package gb.pavelkorzhenko.a2l1menuapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by small on 12/17/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.NoteViewHolder> {
    private Context context;
    private Context pContext;
    private LayoutInflater inflater;
    private List<NoteLists> elements = new ArrayList<NoteLists>();
    private SparseBooleanArray mSelectedItemsIds;
    NoteListDatabase noteListDatabase;
    TextView txtTitle;
    TextView txtGeoHint;
    TextView txtPubDate;

    public RecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mSelectedItemsIds = new SparseBooleanArray();
        this.pContext = parent.getContext();
        inflater = LayoutInflater.from(pContext);
        return new NoteViewHolder(inflater, parent);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    @Override
    public void onViewRecycled(NoteViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public void updateList() {
        noteListDatabase.close();
        //readFromDatabase();
        notifyDataSetChanged();
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

    public Object getItem(int position) { return elements.get(position);  }

    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public NoteViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recycler_view_item,  parent, false));
            txtTitle = itemView.findViewById(R.id.element_text);
            txtGeoHint = itemView.findViewById(R.id.txtGeoHint);
            txtPubDate = itemView.findViewById(R.id.txtPubDateHint);
        }

        void bind(int position) {
            String geoText;
            if (elements.get(position).getTxtGeoBody().length() > 0) {
                geoText = pContext.getResources().getString(R.string.getHint) + pContext.getResources().getString(R.string.strYes);
                txtGeoHint.setText(geoText);
            } else {
                geoText = pContext.getResources().getString(R.string.getHint) + pContext.getResources().getString(R.string.strNo);
                txtGeoHint.setText(geoText);
            }
            txtTitle.setText(elements.get(position).getTxtTitle());
            txtPubDate.setText(elements.get(position).getTxtPubDate());

        }

        @Override
        public void onClick(View v) {

        }
    }

//    // datebase operations
//    public void readFromDatabase() {
//        noteListDatabase = new NoteListDatabase(context);
//        noteListDatabase.open();
//
//        this.elements = noteListDatabase.getAllNotes();
//    }

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

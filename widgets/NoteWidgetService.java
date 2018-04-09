package gb.pavelkorzhenko.a2l1menuapp.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by small on 12/14/2017.
 */

public class NoteWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new NoteWidgetFactory(this, intent);
    }
}

package yu.kutsuna.todoapp

import android.content.Intent
import android.widget.RemoteViewsService

class TodoAppWidgetService: RemoteViewsService() {
    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory {
        return TodoAppWidgetFactory()
    }
}
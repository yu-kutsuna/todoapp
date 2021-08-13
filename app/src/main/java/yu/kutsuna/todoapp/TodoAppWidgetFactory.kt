package yu.kutsuna.todoapp

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import kotlinx.coroutines.*
import yu.kutsuna.todoapp.data.Todo
import yu.kutsuna.todoapp.main.MainActivity
import java.lang.Exception

class TodoAppWidgetFactory : RemoteViewsService.RemoteViewsFactory {
    private lateinit var todoList: List<Todo>

    fun getTodoList(): Deferred<List<Todo>> {
        return GlobalScope.async {
            return@async db.findActiveItem().reversed()
        }
    }

    override fun onCreate() {
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            todoList = getTodoList().await()
        }
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun onDataSetChanged() {
        val identityToken = Binder.clearCallingIdentity()
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            todoList = getTodoList().await()
        }
        Binder.restoreCallingIdentity(identityToken)
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    @SuppressLint("RemoteViewLayout")
    override fun getViewAt(p0: Int): RemoteViews? {
        if(p0 == AdapterView.INVALID_POSITION) return null

        val todo = todoList[p0]
        val remoteView = RemoteViews(TodoApplication.pkgName, R.layout.todo_widget_row_item)
        with(remoteView) {
            setTextViewText(R.id.todo_value, todo.value)
            setTextViewText(R.id.date, todo.updateDate)

            setOnClickFillInIntent(R.id.widget_item_root, Intent(TodoApplication.context, MainActivity::class.java).apply { action = MainActivity.ACTION_FROM_WIDGET })
        }
        return remoteView
    }

    override fun getCount(): Int {
        return try {
            Thread.sleep(1000)
            todoList.size
        } catch (e: Exception) {
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                todoList = getTodoList().await()
            }
            Thread.sleep(1000)
            todoList.size
        }
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
    }

    companion object {
        private val db = TodoApplication.database.todoDao()
    }
}
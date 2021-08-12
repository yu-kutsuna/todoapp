package yu.kutsuna.todoapp

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import yu.kutsuna.todoapp.main.MainActivity

/**
 * Implementation of App Widget functionality.
 */
class TodoAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        when(intent?.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                context?.let { ct ->
                    val manager = AppWidgetManager.getInstance(ct)
                    val myWidget = ComponentName(ct, TodoAppWidgetProvider::class.java)
                    val appWidgetIds = manager.getAppWidgetIds(myWidget)
                    manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_todo_list)
                }
            }
        }
    }

    fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Construct the RemoteViews object
        val clickIntentTemplate = Intent(context, MainActivity::class.java).apply { action = MainActivity.ACTION_FROM_WIDGET }
        val views = RemoteViews(context.packageName, R.layout.todo_app_widget)
        val widgetServiceIntent = Intent(context, TodoAppWidgetService::class.java)
        views.setOnClickPendingIntent(R.id.title, PendingIntent.getActivity(context, 0, clickIntentTemplate, 0))
        views.setRemoteAdapter(R.id.widget_todo_list, widgetServiceIntent)

        // リストアイテムクリックリスナーの設定
        val clickPendingIntentTemplate = TaskStackBuilder.create(context).addNextIntentWithParentStack(clickIntentTemplate).getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setPendingIntentTemplate(R.id.widget_todo_list, clickPendingIntentTemplate)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
        const val EXTRA_KEY = "EXTRA_KEY"
    }
}


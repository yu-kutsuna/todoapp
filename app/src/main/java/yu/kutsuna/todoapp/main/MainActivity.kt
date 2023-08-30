package yu.kutsuna.todoapp.main

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import yu.kutsuna.todoapp.R
import yu.kutsuna.todoapp.TodoAppWidgetProvider
import yu.kutsuna.todoapp.data.TodoModel
import yu.kutsuna.todoapp.databinding.ActivityMainBinding
import yu.kutsuna.todoapp.extensions.hideKeyboard
import yu.kutsuna.todoapp.row.TodoViewAdapter


class MainActivity : AppCompatActivity(), LifecycleOwner {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // ViewModel取得
        mainViewModel =
            ViewModelProviders.of(this, MainViewModel.Factory(object : MainViewModel.Callback {
                override fun finishAllClear() {
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                }
            }, this)).get(MainViewModel::class.java)
        mainViewModel.selectedType.value =
            if (intent.action != null && (intent.action == ACTION_FROM_WIDGET || intent.getBooleanExtra(
                    EXTRA_KEY_FROM_WIDGET, false
                )) && mainViewModel.selectedType.value != MainViewModel.SelectedType.ACTIVE
            ) {
                MainViewModel.SelectedType.ACTIVE
            } else {
                MainViewModel.SelectedType.ALL
            }

        // LiveData監視開始
        mainViewModel.todoList.observe(this, Observer { todoList ->
            (binding.recyclerView.adapter as TodoViewAdapter).update(todoList)
            val widgetIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                component = ComponentName(applicationContext, TodoAppWidgetProvider::class.java)
            }
            sendBroadcast(widgetIntent)

            binding.todoText.setText("")
            hideKeyboard()
        })

        lifecycle.addObserver(mainViewModel)

        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this
        binding.todoText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mainViewModel.addText(s)
            }

        })

        // RecyclerViewのAdapterセット
        binding.recyclerView.adapter =
            TodoViewAdapter(this,
                object : TodoViewAdapter.RowEventListener {
                    /**
                     * アイテムのチェックボックス押下時に通知される
                     * チェック済みアイテムの存在可否をチェックしてViewを更新する
                     */
                    override fun clickCheckBox(checkedList: List<TodoModel>) {
                        mainViewModel.checkedItemList = checkedList
                    }
                }
            )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        mainViewModel.updateList()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action != null && intent.action == ACTION_FROM_WIDGET) {
            mainViewModel.selectedType.postValue(MainViewModel.SelectedType.ACTIVE)
        }
    }

    companion object {
        const val ACTION_FROM_WIDGET = "ACTION_FROM_WIDGET"
        const val EXTRA_KEY_FROM_WIDGET = "EXTRA_KEY_FROM_WIDGET"
        const val TAG = "MainActivity"
    }
}

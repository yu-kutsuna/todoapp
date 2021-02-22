package yu.kutsuna.todoapp.main

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
import yu.kutsuna.todoapp.data.TodoModel
import yu.kutsuna.todoapp.databinding.ActivityMainBinding
import yu.kutsuna.todoapp.extensions.existCheckedItem
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

        // LiveData監視開始
        mainViewModel.todoList.observe(this, Observer { todoList ->
            (binding.recyclerView.adapter as TodoViewAdapter).update(todoList)
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
                     * 削除ボタン押下時の処理
                     */
                    override fun clickDeleteIcon(id: String) {
                        mainViewModel.clickDeleteIcon(id)
                    }

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
    }
}

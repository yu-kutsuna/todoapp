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
import yu.kutsuna.todoapp.hideKeyboard
import yu.kutsuna.todoapp.row.TodoViewAdapter


class MainActivity : AppCompatActivity(), LifecycleOwner {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // ViewModel取得
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        // LiveData監視開始
        observeViewModel()

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
    }

    /**
     * LiveData監視開始
     */
    private fun observeViewModel() {
        /**
         * リストに変更が加わった時の処理
         * RecyclerViewのアダプターがnullの場合は初期処理をし、
         * 存在している場合はアップデートのみ行う
         * また、リスト更新時にはキーボードを隠す
         */
        mainViewModel.todoList.observe(this, Observer { todoList ->
            updateList(todoList)
            binding.todoText.setText("")
            hideKeyboard()
        })

        /**
         * 全選択ボタン押下時の処理
         * 未完了の全アイテムとチェック済みアイテムを比較し、
         * 全てが選択済みの場合はtodoListのisCheckedを全てfalseにし、
         * そうでない場合はisCheckedを全てtrueにして
         * チェック済みアイテムリストを更新し、
         * 最後にListを更新する
         */
        mainViewModel.isAllSelectClicked.observe(this, Observer {
            mainViewModel.todoList.value?.let { todoList ->
                if (todoList.filter { it.isChecked }.size < todoList.size - todoList.filter { it.todo.isCompleted }.size) {
                    todoList.forEach {
                        if (!it.todo.isCompleted) {
                            it.isChecked = true
                        }
                    }
                } else {
                    checkBoxReset()
                }
                mainViewModel.checkedTodoList = todoList.filter { it.isChecked }.toMutableList()
            }

            binding.recyclerView.adapter?.notifyDataSetChanged()
        })
    }

    /**
     * リスト更新処理
     */
    private fun updateList(todoList: List<TodoModel>) {
        checkBoxReset()
        if (binding.recyclerView.adapter == null) {
            binding.lifecycleOwner?.let { lifecycleOwner ->
                binding.recyclerView.adapter =
                        TodoViewAdapter(
                                todoList,
                                lifecycleOwner,
                                object : TodoViewAdapter.RowEventListener {
                                    /**
                                     * 削除ボタン押下時の処理
                                     */
                                    override fun clickDeleteIcon(id: String) {
                                        mainViewModel.clickDeleteIcon(id)
                                    }

                                    /**
                                     * チェックボックス押下時に通知される
                                     * フィールドのTodoList内の対応するアイテムのisCheckedを反転し、
                                     * チェック済みアイテムリストを更新する
                                     */
                                    override fun clickCheckBox(checkedId: Long) {
                                        run loop@{
                                            todoList.forEach { todo ->
                                                if (todo.todo.id == checkedId) {
                                                    todo.isChecked = !todo.isChecked
                                                    return@loop
                                                }
                                            }
                                        }
                                        mainViewModel.checkedTodoList = todoList.filter { it.isChecked }.toMutableList()
                                    }
                                }
                        )
            }
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
        } else {
            (binding.recyclerView.adapter as TodoViewAdapter).update(todoList)
        }
    }

    private fun checkBoxReset() {
        mainViewModel.todoList.value?.forEach {
            if (!it.todo.isCompleted) {
                it.isChecked = false
            }
        }
    }
}

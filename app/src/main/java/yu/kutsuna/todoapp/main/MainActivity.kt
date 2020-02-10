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

        // Adapterセット
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
                     * チェックボックス押下時に通知される
                     * フィールドのTodoList内の対応するアイテムのisCheckedを反転し、
                     * チェック済みアイテムリストを更新する
                     */
                    override fun clickCheckBox(isChecked: Boolean) {
                        mainViewModel.isItemChecking.value = isChecked
                    }
                }
            )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        mainViewModel.adapter = binding.recyclerView.adapter as TodoViewAdapter
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
            (binding.recyclerView.adapter as TodoViewAdapter).update(todoList)
            binding.todoText.setText("")
            hideKeyboard()
        })
    }
}

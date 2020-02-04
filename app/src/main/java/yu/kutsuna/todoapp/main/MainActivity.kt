package yu.kutsuna.todoapp.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import yu.kutsuna.todoapp.R
import yu.kutsuna.todoapp.row.TodoViewAdapter
import yu.kutsuna.todoapp.databinding.ActivityMainBinding
import yu.kutsuna.todoapp.hideKeyboard

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = (DataBindingUtil.setContentView(this,
            R.layout.activity_main
        ) as ActivityMainBinding).apply {
            lifecycleOwner = this@MainActivity
            viewModel = mainViewModel
            /**
             * EditTextの入力イベント
             * Addボタンの出しわけに使用
             */
            todoText.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    mainViewModel.addText(s)
                }

            })
        }
        observeViewModel()
        mainViewModel.init()
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
            if(binding.recyclerView.adapter == null) {
                binding.lifecycleOwner?.let {
                    binding.recyclerView.adapter =
                        TodoViewAdapter(
                            todoList,
                            it,
                            mainViewModel,
                            object: TodoViewAdapter.RowEventListener{
                                override fun clickDeleteIcon(id: String) {
                                    mainViewModel.clickDeleteIcon(id)
                                }
                            }
                        )
                }
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
            } else {
                (binding.recyclerView.adapter as TodoViewAdapter).update(todoList)
            }
            binding.todoText.setText("")
            hideKeyboard()
        })

        /**
         * 全選択ボタン押下時の処理
         * RecyclerView.Adapterから全選択を行う
         */
        mainViewModel.isAllSelectClicked.observe(this, Observer {
            binding.recyclerView.adapter?.let {
                (it as TodoViewAdapter).allSelect()
            }
        })
    }
}

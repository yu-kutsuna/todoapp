package yu.kutsuna.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import yu.kutsuna.todoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val myViewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = (DataBindingUtil.setContentView(this, R.layout.activity_main) as ActivityMainBinding).apply {
            lifecycleOwner = this@MainActivity
            viewModel = myViewModel
            todoText.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    myViewModel.addText(s)
                }

            })
        }
        observeViewModel()
        myViewModel.init()
    }

    private fun observeViewModel() {
        myViewModel.todoList.observe(this, Observer { todoList ->
            if(binding.recyclerView.adapter == null) {
                binding.recyclerView.adapter = TodoViewAdapter(this, todoList)
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
            } else {
                (binding.recyclerView.adapter as TodoViewAdapter).update(todoList)
            }
        })
        myViewModel.isAllSelectClicked.observe(this, Observer { isClicked ->
            if(isClicked) {
                myViewModel.todoList.value?.forEachIndexed{ index , todo ->
                    (binding.recyclerView.findViewHolderForItemId(index.toLong()) as TodoViewAdapter.TodoViewHolder).checkBox.isChecked = true
                }
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
        })
    }
}

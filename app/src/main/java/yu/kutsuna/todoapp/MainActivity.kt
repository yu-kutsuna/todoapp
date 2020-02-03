package yu.kutsuna.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import yu.kutsuna.todoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = (DataBindingUtil.setContentView(this, R.layout.activity_main) as ActivityMainBinding).apply {
            lifecycleOwner = this@MainActivity
            viewModel = mainViewModel
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

    private fun observeViewModel() {
        mainViewModel.todoList.observe(this, Observer { todoList ->
            if(binding.recyclerView.adapter == null) {
                binding.lifecycleOwner?.let {
                    binding.recyclerView.adapter = TodoViewAdapter(todoList, it, mainViewModel)
                }
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
            } else {
                (binding.recyclerView.adapter as TodoViewAdapter).update(todoList)
            }
            binding.todoText.setText("")
            hideKeyboard()
        })

        mainViewModel.isAllSelectClicked.observe(this, Observer {
            binding.recyclerView.adapter?.let {
                (it as TodoViewAdapter).allSelect()
            }
        })
    }
}

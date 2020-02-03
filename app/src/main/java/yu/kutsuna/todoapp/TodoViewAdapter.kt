package yu.kutsuna.todoapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import yu.kutsuna.todoapp.data.Todo
import yu.kutsuna.todoapp.databinding.TodoRowItemBinding

class TodoViewAdapter(private var todoList: List<Todo>,
                      private val parentLifecycleOwner: LifecycleOwner,
                      private val parentViewModel: MainViewModel
): RecyclerView.Adapter<TodoViewAdapter.TodoViewHolder>() {

    class TodoViewHolder(val binding: TodoRowItemBinding): RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoViewHolder{
        val binding = DataBindingUtil.inflate<TodoRowItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.todo_row_item,
            parent,
            false
        )
        return TodoViewHolder(binding)
    }

    override fun getItemCount(): Int  = todoList.size

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.binding.todo = todoList[position]
        holder.binding.viewModel = TodoRowViewModel(todoList[position].id.toString(), parentViewModel)
        holder.binding.lifecycleOwner = parentLifecycleOwner
        holder.binding.checkBox.isChecked = isAllSelected
        holder.binding.viewModel?.let{ todoRowViewModel ->
            todoRowViewModel.isDeleted.observe(parentLifecycleOwner, Observer { isDeleted ->
                if(isDeleted) {
                    update(todoRowViewModel.todoList)
                    todoRowViewModel.isDeleted.value = false
                }
            })
        }
    }

    fun update(todoList: List<Todo>) {
        Log.d("test", "TodoViewAdapter updateList!")
        this.todoList = todoList
        notifyDataSetChanged()
    }

    fun allSelect() {
        isAllSelected = !isAllSelected
        notifyDataSetChanged()
    }

    companion object {
        var isAllSelected = false
    }
}
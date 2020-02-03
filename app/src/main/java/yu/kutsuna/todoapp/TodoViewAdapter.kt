package yu.kutsuna.todoapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import yu.kutsuna.todoapp.data.Todo

class TodoViewAdapter(private val context: Context, private var todoList: List<Todo>): RecyclerView.Adapter<TodoViewAdapter.TodoViewHolder>() {

    class TodoViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkbox)
        val todoValue: TextView = view.findViewById(R.id.todo_value)
        val deleteIcon: TextView = view.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoViewHolder = TodoViewHolder(LayoutInflater.from(context).inflate(R.layout.todo_row_item, parent, false))

    override fun getItemCount(): Int  = todoList.size

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.todoValue.text = todoList[position].value
        holder.checkBox.isChecked = isAllSelected
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
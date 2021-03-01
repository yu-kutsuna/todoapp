package yu.kutsuna.todoapp.row

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import yu.kutsuna.todoapp.*
import yu.kutsuna.todoapp.data.TodoModel
import yu.kutsuna.todoapp.databinding.TodoRowItemBinding
import yu.kutsuna.todoapp.extensions.clearPaint
import yu.kutsuna.todoapp.extensions.existCheckedItem
import yu.kutsuna.todoapp.extensions.inversionChecked
import yu.kutsuna.todoapp.extensions.setStrikeThrough

class TodoViewAdapter(
    private val parentLifecycleOwner: LifecycleOwner,
    private val rowEventListener: RowEventListener
) : RecyclerView.Adapter<TodoViewAdapter.TodoViewHolder>() {

    private lateinit var todoList: List<TodoModel>
    private lateinit var todoRowViewModel: TodoRowViewModel

    class TodoViewHolder(val binding: TodoRowItemBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * リストの削除ボタンが押下されたら
     * MainActivityに通知する
     */
    interface RowEventListener {
        fun clickCheckBox(checkedList: List<TodoModel>)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoViewHolder {
        val binding = DataBindingUtil.inflate<TodoRowItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.todo_row_item,
            parent,
            false
        )
        return TodoViewHolder(binding)
    }

    override fun getItemCount(): Int = todoList.size

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder")

        todoRowViewModel = TodoRowViewModel(todoList[position], position).apply { init() }

        holder.binding.viewModel = todoRowViewModel
        holder.binding.lifecycleOwner = parentLifecycleOwner
        holder.binding.executePendingBindings()

        /**
         * 完了済みのアイテムに取り消し線をつける
         */
        if (todoList[position].todo.isCompleted) {
            holder.binding.todoValue.setStrikeThrough()
        } else {
            holder.binding.todoValue.clearPaint()
        }


        /**
         * チェックボックス押下時に通知される
         * EventListenerからMainActivityに通知する
         */
        todoRowViewModel.checkedPosition.observe(parentLifecycleOwner, Observer { checkedPosition ->
            todoList[checkedPosition].inversionChecked()
            rowEventListener.clickCheckBox(getCheckedList())
        })
    }

    /**
     * リスト更新処理
     */
    fun update(todoList: List<TodoModel>) {
        this.todoList = todoList
        notifyDataSetChanged()
    }

    /**
     * チェック済みリストの取得
     */
    fun getCheckedList(): List<TodoModel> {
        return todoList.filter { it.isChecked }
    }

    companion object {
        private const val TAG = "TodoViewAdapter"
    }
}
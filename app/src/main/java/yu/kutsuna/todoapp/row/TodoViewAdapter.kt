package yu.kutsuna.todoapp.row

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import yu.kutsuna.todoapp.R
import yu.kutsuna.todoapp.data.TodoModel
import yu.kutsuna.todoapp.databinding.TodoRowItemBinding

class TodoViewAdapter(
        private var todoList: List<TodoModel>,
        private val parentLifecycleOwner: LifecycleOwner,
        private val rowEventListener: RowEventListener
) : RecyclerView.Adapter<TodoViewAdapter.TodoViewHolder>() {
    private lateinit var todoRowViewModel: TodoRowViewModel

    class TodoViewHolder(val binding: TodoRowItemBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * リストの削除ボタンが押下されたら
     * MainActivityに通知する
     */
    interface RowEventListener {
        fun clickDeleteIcon(id: String)
        fun clickCheckBox(checkedId: Long)
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

        todoRowViewModel = TodoRowViewModel(todoList[position]).apply { init() }

        holder.binding.viewModel = todoRowViewModel
        holder.binding.lifecycleOwner = parentLifecycleOwner

        /**
         * 完了済みのアイテムに取り消し線をつける
         */
        if (todoList[position].todo.isCompleted) {
            val paint = holder.binding.todoValue.paint
            paint.flags = holder.binding.todoValue.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            paint.isAntiAlias = true
        } else {
            val paint = holder.binding.todoValue.paint
            paint.flags = 0
        }

        /**
         * 削除ボタン押下時の処理
         * EventListenerからMainActivityに通知する
         */
        todoRowViewModel.deleteId.observe(parentLifecycleOwner, Observer {
            rowEventListener.clickDeleteIcon(it)
        })


        /**
         * チェックボックス押下時に通知される
         * EventListenerからMainActivityに通知する
         */
        todoRowViewModel?.checkedId?.observe(parentLifecycleOwner, Observer { checkedId ->
            rowEventListener.clickCheckBox(checkedId)
        })
    }

    /**
     * リスト更新処理
     */
    fun update(todoList: List<TodoModel>) {
        Log.d(TAG, "update")
        this.todoList = todoList
        notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "TodoViewAdapter"
    }
}
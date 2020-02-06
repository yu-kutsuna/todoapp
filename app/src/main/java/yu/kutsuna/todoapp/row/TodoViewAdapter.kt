package yu.kutsuna.todoapp.row

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import yu.kutsuna.todoapp.R
import yu.kutsuna.todoapp.data.Todo
import yu.kutsuna.todoapp.data.TodoModel
import yu.kutsuna.todoapp.databinding.TodoRowItemBinding
import yu.kutsuna.todoapp.main.MainViewModel


class TodoViewAdapter(
    private var todoList: List<TodoModel>,
    private val parentLifecycleOwner: LifecycleOwner,
    private val parentViewModel: MainViewModel,
    private val rowEventListener: RowEventListener
) : RecyclerView.Adapter<TodoViewAdapter.TodoViewHolder>() {
    private var todoRowViewModel: TodoRowViewModel? = null

    class TodoViewHolder(val binding: TodoRowItemBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * リストの削除ボタンが押下されたら
     * MainActivityに通知する
     */
    interface RowEventListener {
        fun clickDeleteIcon(id: String)
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
        todoRowViewModel = TodoRowViewModel(todoList[position]).apply { init() }
        holder.binding.viewModel = todoRowViewModel
        holder.binding.lifecycleOwner = parentLifecycleOwner
        holder.binding.executePendingBindings()

        Log.d(TAG, "onBindViewHolder position $position isChecked ${todoList[position].isChecked}")

        /**
         * チェックボックスのイベントリスナー
         * イベントを検知したらチェックされているアイテムのみをリスト化して
         * MainViewModelに渡す
         */
        holder.binding.checkBox.setOnCheckedChangeListener { _, _ ->
            val checkedTodoList = mutableListOf<Todo>()
            todoList.forEach {
                if (it.isChecked) {
                    checkedTodoList.add(it.todo)
                }
            }
            parentViewModel.checkedTodoList = checkedTodoList
        }


        /**
         * 完了済みのアイテムに取り消し線をつける
         */
        if (todoList[position].todo.isCompleted) {
            val paint = holder.binding.todoValue.paint
            paint.flags = holder.binding.todoValue.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            paint.isAntiAlias = true
            todoRowViewModel?.isCompleted?.value = true
        } else {
            val paint = holder.binding.todoValue.paint
            paint.flags = 0
            todoRowViewModel?.isCompleted?.value = false
        }

        todoRowViewModel?.deleteId?.observe(parentLifecycleOwner, Observer {
            rowEventListener.clickDeleteIcon(it)
        })
    }

    /**
     * リスト更新処理
     */
    fun update(todoList: List<TodoModel>) {
        this.todoList = todoList
        checkBoxReset()
        notifyDataSetChanged()
    }

    /**
     * 全選択ボタン押下時の処理
     * 未完了の全アイテムとチェック済みアイテムを比較し、
     * 全てが選択済みの場合はtodoListのisCheckedを全てfalseにし、
     * そうでない場合はisCheckedを全てtrueにして
     * 更新する
     */
    fun allSelect() {
        if (todoList.filter { it.isChecked }.size < todoList.size - todoList.filter { it.todo.isCompleted }.size) {
            todoList.forEach {
                it.isChecked = true
            }
        } else {
            checkBoxReset()
        }
        notifyDataSetChanged()
    }

    private fun checkBoxReset() {
        todoList.forEach {
            it.isChecked = false
        }
    }

    companion object {
        private const val TAG = "TodoViewAdapter"
    }
}
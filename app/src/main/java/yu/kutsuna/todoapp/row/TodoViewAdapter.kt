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

    enum class AllSelectType {
        NONE,
        ALL_SELECT,
        ALL_CLEAR
    }

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
        todoRowViewModel = TodoRowViewModel(todoList[position].todo.id.toString())
        holder.binding.todo = todoList[position].todo
        holder.binding.viewModel = todoRowViewModel
        holder.binding.lifecycleOwner = parentLifecycleOwner

        // チェックボックスの初期化
        Log.d(TAG, "onBindViewHolder position $position checked ${todoList[position].isChecked}")
        holder.binding.viewModel?.isChecked?.value = todoList[position].isChecked

        /**
         * 全選択処理
         * 一つでも選択されている場合は選択されていないチェックボックスを選択済みにする
         * 全て選択されている場合は全てのチェックボックスの選択を解除する
         */
        when (allSelectType) {
            AllSelectType.ALL_SELECT -> {
                Log.d(TAG, "allSelect type ALL_SELECT todoListSize ${todoList.size}")
                holder.binding.viewModel?.isChecked?.value = true
                todoList.forEach {
                    it.isChecked = true
                }
            }
            AllSelectType.ALL_CLEAR,
            AllSelectType.NONE -> {
                Log.d(TAG, "allSelect type ALL_CLEAR or NONE")
                holder.binding.viewModel?.isChecked?.value = false
                todoList.forEach {
                    it.isChecked = false
                }
            }
        }

        parentViewModel.isItemChecking.value = todoList.any { it.isChecked }

        if (position + 1 == todoList.size) {
            allSelectType =
                AllSelectType.NONE
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
        }

        todoRowViewModel?.deleteId?.observe(parentLifecycleOwner, Observer {
            rowEventListener.clickDeleteIcon(it)
        })

        todoRowViewModel?.checkedId?.observe(parentLifecycleOwner, Observer { id ->

            run loop@{
                todoList.forEachIndexed { index, todo ->
                    if (todo.todo.id.toString() == id) {
                        todoRowViewModel?.isChecked?.value?.let { checked ->
                            todoList[index].isChecked = checked
                        }
                        return@loop
                    }
                }
            }
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
     * 全選択ボタン押下時の処理
     */
    fun allSelect() {
        Log.d(TAG, "allSelect checkedListSize ${ todoList.filter { it.isChecked }.size} , todoListSize ${todoList.size}")
        allSelectType = if (todoList.filter { it.isChecked }.size < todoList.size) {
            AllSelectType.ALL_SELECT
        } else {
            AllSelectType.ALL_CLEAR
        }
        notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "TodoViewAdapter"
        var allSelectType =
            AllSelectType.NONE
    }
}
package yu.kutsuna.todoapp

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import yu.kutsuna.todoapp.data.Todo
import yu.kutsuna.todoapp.databinding.TodoRowItemBinding


class TodoViewAdapter(
    private var todoList: List<Todo>,
    private val parentLifecycleOwner: LifecycleOwner,
    private val parentViewModel: MainViewModel
) : RecyclerView.Adapter<TodoViewAdapter.TodoViewHolder>() {
    private var todoRowViewModel: TodoRowViewModel? = null

    class TodoViewHolder(val binding: TodoRowItemBinding) : RecyclerView.ViewHolder(binding.root)

    enum class AllSelectType {
        NONE,
        ALL_SELECT,
        ALL_CLEAR
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
        todoRowViewModel = TodoRowViewModel(todoList[position].id.toString(), parentViewModel)
        holder.binding.todo = todoList[position]
        holder.binding.viewModel = todoRowViewModel
        holder.binding.lifecycleOwner = parentLifecycleOwner

        /**
         * チェックボックスの状態変更時の処理
         * チェックされたアイテムのIDのリストをMainViewModelで保持する
         * また、全てのチェックが外れた時にクリアボタンを非表示にし、
         * アイテムが一つでもチェックされた時にクリアボタンを表示する
         */
        holder.binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                parentViewModel.checkedIdList.add(todoList[position].id.toString())
            } else {
                run loop@{
                    parentViewModel.checkedIdList.forEachIndexed { index, id ->
                        if (id == todoList[position].id.toString()) {
                            parentViewModel.checkedIdList.removeAt(index)
                            return@loop
                        }
                    }
                }
            }

            parentViewModel.isItemChecking.value = parentViewModel.checkedIdList.isNotEmpty()
        }

        /**
         * 全選択処理
         * 一つでも選択されている場合は選択されていないチェックボックスを選択済みにする
         * 全て選択されている場合は全てのチェックボックスの選択を解除する
         */
        when (allSelectType) {
            AllSelectType.ALL_SELECT -> todoRowViewModel?.isChecked?.value = true
            AllSelectType.ALL_CLEAR -> todoRowViewModel?.isChecked?.value = false
            AllSelectType.NONE -> todoRowViewModel?.isChecked?.value = false
        }

        if (position + 1 == todoList.size) {
            allSelectType = AllSelectType.NONE
        }

        /**
         * 完了済みのアイテムに取り消し線をつける
         */
        Log.d(TAG, "position $position , isCompleted ${todoList[position].isCompleted}")
        if (todoList[position].isCompleted) {
            val paint = holder.binding.todoValue.paint
            paint.flags = holder.binding.todoValue.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            paint.isAntiAlias = true
            todoRowViewModel?.isCompleted?.value = true
        } else {
            val paint = holder.binding.todoValue.paint
            paint.flags = 0
        }
    }

    fun update(todoList: List<Todo>) {
        this.todoList = todoList
        notifyDataSetChanged()
    }

    fun allSelect() {
        allSelectType = if (parentViewModel.checkedIdList.size < todoList.size) {
            AllSelectType.ALL_SELECT
        } else {
            AllSelectType.ALL_CLEAR
        }
        notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "TodoViewAdapter"
        var allSelectType = AllSelectType.NONE
    }
}
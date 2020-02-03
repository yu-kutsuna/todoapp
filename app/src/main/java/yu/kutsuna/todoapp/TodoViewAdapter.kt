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


class TodoViewAdapter(private var todoList: List<Todo>,
                      private val parentLifecycleOwner: LifecycleOwner,
                      private val parentViewModel: MainViewModel
): RecyclerView.Adapter<TodoViewAdapter.TodoViewHolder>() {

    class TodoViewHolder(val binding: TodoRowItemBinding): RecyclerView.ViewHolder(binding.root)

    enum class AllSelectType {
        NONE,
        ALL_SELECT,
        ALL_CLEAR
    }

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

        /**
         * チェックボックスの状態変更時の処理
         * チェックされたアイテムのIDのリストをMainViewModelで保持する
         * また、全てのチェックが外れた時にクリアボタンを非表示にし、
         * アイテムが一つでもチェックされた時にクリアボタンを表示する
         */
        holder.binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                parentViewModel.checkedIdList.add(todoList[position].id.toString())
            } else {
                run loop@ {
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
        when(allSelectType) {
            AllSelectType.ALL_SELECT -> holder.binding.checkBox.isChecked = true
            AllSelectType.ALL_CLEAR -> holder.binding.checkBox.isChecked = false
            AllSelectType.NONE -> Unit
        }
        if(position + 1 == todoList.size) {
            allSelectType = AllSelectType.NONE
        }

        Log.d(TAG, "position $position , isCompleted ${todoList[position].isCompleted}")
        if(todoList[position].isCompleted) {
            val paint = holder.binding.todoValue.paint
            paint.flags = holder.binding.todoValue.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            paint.isAntiAlias = true
        } else {
            val paint = holder.binding.todoValue.paint
            paint.flags = 0
        }
    }

    fun update(todoList: List<Todo>) {
        Log.d("test", "TodoViewAdapter updateList!")
        this.todoList = todoList
        notifyDataSetChanged()
    }

    fun allSelect() {
        allSelectType = if(parentViewModel.checkedIdList.size < todoList.size) {
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
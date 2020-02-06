package yu.kutsuna.todoapp.row

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import yu.kutsuna.todoapp.data.TodoModel

class TodoRowViewModel(val todo: TodoModel): ViewModel() {
    var isCompleted: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var isChecked: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply{value = false}
    var deleteId: MutableLiveData<String> = MutableLiveData()
    var checkedId: MutableLiveData<String> = MutableLiveData()
    var value: MutableLiveData<String> = MutableLiveData()
    var date: MutableLiveData<String> = MutableLiveData()
    var id: Long = 0

    fun init() {
        value.value = todo.todo.value
        date.value = todo.todo.updateDate
        isChecked.value = todo.isChecked
        id = todo.todo.id
    }

    /**
     * 削除アイコン押下時の処理
     * MainViewModelに削除を通知する
     */
    fun clickDeleteIcon(view: View) {
        deleteId.value = id.toString()
    }

    /**
     * Todoのテキスト押下時の処理
     * 完了済：何もしない
     * 未完了：チェックボックスのチェック状態を反転する
     */
    fun clickCheck(view: View) {
        isChecked.value?.let {
            isChecked.value = !it
            todo.isChecked = !it
        }
        Log.d("TodoRowViewModel", "checkedIdObserve clickCheck isChecked ${isChecked.value}")
        checkedId.value = id.toString()
    }

}
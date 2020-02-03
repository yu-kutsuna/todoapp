package yu.kutsuna.todoapp

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yu.kutsuna.todoapp.data.Todo

class TodoRowViewModel(val id: String, private val parentViewModel: MainViewModel): ViewModel() {
    val isDeleted: MutableLiveData<Boolean> = MutableLiveData()
    var todoList: List<Todo> = emptyList()
    fun clickDeleteIcon(view: View) {
        parentViewModel.clickDeleteIcon(id)
    }
}
package yu.kutsuna.todoapp.row

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import yu.kutsuna.todoapp.main.MainViewModel

class TodoRowViewModel(val id: String, private val parentViewModel: MainViewModel): ViewModel() {
    var isCompleted: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    var isChecked: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }


    fun clickDeleteIcon(view: View) {
        parentViewModel.clickDeleteIcon(id)
    }

    fun clickTodoValue(view: View) {
        isCompleted.value?.let {
            if(it) return
        }

        isChecked.value?.let {
            isChecked.value = !it
        }
    }
}
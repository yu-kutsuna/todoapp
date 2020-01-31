package yu.kutsuna.todoapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TodoRowViewModel: ViewModel() {
    var todoValue: MutableLiveData<String> = MutableLiveData()
}
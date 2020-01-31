package yu.kutsuna.todoapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    var isListExist: MutableLiveData<Boolean> = MutableLiveData()
    var isEmptyAddText: MutableLiveData<Boolean> = MutableLiveData()
}
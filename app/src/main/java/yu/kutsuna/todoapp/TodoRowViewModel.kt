package yu.kutsuna.todoapp

import android.view.View
import androidx.lifecycle.ViewModel

class TodoRowViewModel(val id: String, private val parentViewModel: MainViewModel): ViewModel() {
    fun clickDeleteIcon(view: View) {
        parentViewModel.clickDeleteIcon(id)
    }
}
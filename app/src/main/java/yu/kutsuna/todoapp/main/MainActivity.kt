package yu.kutsuna.todoapp.main

import android.app.ActionBar
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import yu.kutsuna.todoapp.R
import yu.kutsuna.todoapp.TodoAppWidgetProvider
import yu.kutsuna.todoapp.data.TodoModel
import yu.kutsuna.todoapp.databinding.ActivityMainBinding
import yu.kutsuna.todoapp.extensions.hideKeyboard
import yu.kutsuna.todoapp.row.TodoViewAdapter
import java.lang.Exception


class MainActivity : AppCompatActivity(), LifecycleOwner {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
        binding.adView.adListener = object : AdListener() {

            override fun onAdClicked() {
                super.onAdClicked()
                Log.d(TAG, "AdMob onAdClicked")
            }

            override fun onAdClosed() {
                super.onAdClosed()
                Log.d(TAG, "AdMob onAdClosed")
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.d(TAG, "AdMob onAdImpression")
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(TAG, "AdMob onAdLoaded")
                binding.adProgress.visibility = View.GONE
            }

            override fun onAdOpened() {
                super.onAdOpened()
                Log.d(TAG, "AdMob onAdOpened")
            }
        }


        // ViewModel取得
        mainViewModel =
            ViewModelProviders.of(this, MainViewModel.Factory(object : MainViewModel.Callback {
                override fun finishAllClear() {
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                }
            }, this)).get(MainViewModel::class.java)

        // LiveData監視開始
        mainViewModel.todoList.observe(this, Observer { todoList ->
            (binding.recyclerView.adapter as TodoViewAdapter).update(todoList)
            val widgetIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                component = ComponentName(applicationContext, TodoAppWidgetProvider::class.java)
            }
            sendBroadcast(widgetIntent)

            binding.todoText.setText("")
            hideKeyboard()
        })

        lifecycle.addObserver(mainViewModel)

        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this
        binding.todoText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mainViewModel.addText(s)
            }

        })

        // RecyclerViewのAdapterセット
        binding.recyclerView.adapter =
            TodoViewAdapter(this,
                object : TodoViewAdapter.RowEventListener {
                    /**
                     * アイテムのチェックボックス押下時に通知される
                     * チェック済みアイテムの存在可否をチェックしてViewを更新する
                     */
                    override fun clickCheckBox(checkedList: List<TodoModel>) {
                        mainViewModel.checkedItemList = checkedList
                    }
                }
            )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        if (intent.action != null && intent.action == ACTION_FROM_WIDGET) {
            mainViewModel.selectActive()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action != null && intent.action == ACTION_FROM_WIDGET) {
            mainViewModel.selectActive()
        }
    }

    companion object {
        const val ACTION_FROM_WIDGET = "ACTION_FROM_WIDGET"
        const val TAG = "MainActivity"
    }
}

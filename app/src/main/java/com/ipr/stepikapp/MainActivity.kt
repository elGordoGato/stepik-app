package com.ipr.stepikapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.gson.Gson
import com.ipr.stepikapp.ui.theme.StepikAppTheme
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainActivity : ComponentActivity() {
    val url =
        "https://api.rss2json.com/v1/api.json?rss_url=http%3A%2F%2Ffeeds.bbci.co.uk%2Fnews%2Frss.xml"
    lateinit var vList: ListView
    var request: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_view)

        vList = findViewById<ListView>(R.id.main_listView)

        val o = createRequest(url)
            .map { Gson().fromJson(it, Feed::class.java) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        request = o.subscribe({
            showListView(it.items)
        }, {
            Log.e("news", "", it)
        })

    }

    private fun showListView(feedList: ArrayList<FeedItem>) {
        vList.adapter = Adapter(feedList)

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        request?.dispose()
        super.onDestroy()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StepikAppTheme {
        Greeting("Android")
    }
}

data class Feed(
    val items: ArrayList<FeedItem>
)

data class FeedItem(
    val title: String,
    val link: String,
    val thumbnail: String,
    val description: String
)

class Adapter(val items: ArrayList<FeedItem>) : BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(parent!!.context)
        val view = convertView ?: inflater.inflate(R.layout.list_item, parent, false)
        val vTitle = view.findViewById<TextView>(R.id.item_title)

        val item = getItem(position) as FeedItem
        vTitle.text = item.title

        return view
    }

}
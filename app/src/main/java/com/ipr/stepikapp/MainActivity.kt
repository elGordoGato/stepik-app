package com.ipr.stepikapp

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.gson.Gson
import com.ipr.stepikapp.ui.theme.StepikAppTheme
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject

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
            .map { Gson().fromJson(it, FeedAPI::class.java) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        request = o.subscribe({
            val feed = Feed(it.items.mapTo(RealmList<FeedItem>()) { feed ->
                FeedItem(feed.title, feed.link, feed.thumbnail, feed.description)
            })

            Realm.getDefaultInstance().executeTransaction { realm ->
                val oldList = realm.where(Feed::class.java).findAll()
                if (!oldList.isEmpty()) {
                    for (item in oldList) {
                        item.deleteFromRealm()
                    }
                }

                realm.copyToRealm(feed)

            }

            showListView()
        }, {
            Log.e("news", "", it)
            showListView()
        })

    }

    private fun showListView() {
        Realm.getDefaultInstance().executeTransaction { realm ->
            val feeds = realm.where(Feed::class.java).findAll()
            if (!feeds.isEmpty()) {
                vList.adapter = Adapter(feeds[0]!!.items)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
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

data class FeedAPI(
    val items: ArrayList<FeedItem>
)

data class FeedItemAPI(
    val title: String,
    val link: String,
    val thumbnail: String,
    val description: String
)

open class Feed(
    var items: RealmList<FeedItem> = RealmList()
) : RealmObject()

open class FeedItem(
    var title: String = "",
    var link: String = "",
    var thumbnail: String = "",
    var description: String = ""
) : RealmObject()

class Adapter(val items: RealmList<FeedItem>) : BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(parent!!.context)
        val view = convertView ?: inflater.inflate(R.layout.list_item, parent, false)
        val vTitle = view.findViewById<TextView>(R.id.item_title)
        val vDescr = view.findViewById<TextView>(R.id.item_descr)
        val vThumb = view.findViewById<ImageView>(R.id.item_thumb)

        val item = getItem(position) as FeedItem
        vTitle.text = item.title
        vDescr.text = item.description

        Picasso.with(vThumb.context).load(item.thumbnail).into(vThumb)

        view.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(item.link)
            vThumb.context.startActivity(i)
        }

        return view
    }

}

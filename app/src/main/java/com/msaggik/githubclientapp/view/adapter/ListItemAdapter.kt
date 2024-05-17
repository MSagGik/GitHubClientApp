package com.msaggik.githubclientapp.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.msaggik.githubclientapp.R
import com.msaggik.githubclientapp.model.entities.itemsearch.Item

private const val ITEM_PREFERENCES = "item_preferences"
private const val ITEM_KEY = "item_key"
class ListItemAdapter (private val fragment: Fragment, private val itemList: List<Item>) : RecyclerView.Adapter<ListItemAdapter.ItemViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(fragment, view)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itemList[position])
    }
    override fun getItemCount(): Int {
        return itemList.size
    }

    class ItemViewHolder(private val fragment: Fragment, itemView: View): RecyclerView.ViewHolder(itemView) {

        private val avatar: ImageView = itemView.findViewById(R.id.avatar)
        private val nickname: TextView = itemView.findViewById(R.id.nickname)
        private val subscribers: TextView = itemView.findViewById(R.id.subscribers)
        private val location: TextView = itemView.findViewById(R.id.location)
        private val listItemSearch: FrameLayout = itemView.findViewById(R.id.list_item_search)

        private lateinit var sharedPreferences: SharedPreferences

        @SuppressLint("UseRequireInsteadOfGet")
        fun bind(model: Item) {
            Glide.with(itemView)
                .load(model.avatarUrl)
                .placeholder(R.drawable.avatar_placeholder)
                .centerCrop()
                .transform(RoundedCorners(doToPx(7f, itemView.context.applicationContext)))
                .into(avatar)
            nickname.text = model.login
            subscribers.text = model.numberFollowers
            location.text = model.location
            listItemSearch.setOnClickListener(View.OnClickListener {
                if(fragment.context != null) {
                    sharedPreferences = fragment.context!!.applicationContext.getSharedPreferences(
                        ITEM_PREFERENCES,
                        AppCompatActivity.MODE_PRIVATE)
                    val itemJson = Gson().toJson(model)
                    sharedPreferences.edit().putString(ITEM_KEY, itemJson).apply()
                }
                fragment.findNavController().navigate(R.id.action_searchFragment_to_userFragment)
            })
        }

        private fun doToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            ).toInt()
        }
    }

}

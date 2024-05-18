package com.msaggik.githubclientapp.view.fragments.nooauth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msaggik.githubclientapp.R
import com.msaggik.githubclientapp.view.adapter.ListItemAdapter
import com.msaggik.githubclientapp.model.entities.item.follower.Follower
import com.msaggik.githubclientapp.model.entities.itemsearch.Item
import com.msaggik.githubclientapp.model.entities.itemsearch.ResponseServerUsers
import com.msaggik.githubclientapp.model.entities.item.User
import com.msaggik.githubclientapp.model.network.RestGitHub
import com.msaggik.githubclientapp.model.network.RestGitHubModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private lateinit var context: Context

    private lateinit var searchItem: EditText
    private lateinit var clearButton: ImageView
    private lateinit var listViewItem: RecyclerView
    private lateinit var textPlaceholder: TextView

    private var textSearchRequest = ""
    private var listItem: MutableList<Item> = mutableListOf()
    private lateinit var listItemAdapter: ListItemAdapter

    private val gitHubBaseURL = "https://api.github.com"
    private val retrofit = RestGitHubModule.createRetrofitObject(gitHubBaseURL)
    private val gitHubRestService = retrofit.create(RestGitHub::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        context = view.context

        searchItem = view.findViewById(R.id.search_item)
        clearButton = view.findViewById(R.id.clear_button)
        listViewItem = view.findViewById(R.id.list_item)
        textPlaceholder = view.findViewById(R.id.text_placeholder)

        listViewItem.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listItemAdapter = ListItemAdapter(this, listItem)
        listViewItem.adapter = listItemAdapter

        searchItem.setOnEditorActionListener(editorListener)
        searchItem.addTextChangedListener(changedListener)
        clearButton.setOnClickListener(listener)

        return view
    }

    private val changedListener = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (p0.isNullOrEmpty()) {
                clearButton.visibility = View.GONE
            } else {
                clearButton.visibility = View.VISIBLE
            }
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    }

    private val listener: View.OnClickListener = object : View.OnClickListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun onClick(p0: View?) {
            when (p0?.id) {
                R.id.clear_button -> {
                    searchItem.setText("")
                    keyGone()
                    listItem.clear()
                    listItemAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private val editorListener = object : TextView.OnEditorActionListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
            textSearchRequest = searchItem.text.toString()
            listItem.clear()
            CoroutineScope(Dispatchers.Main).launch {
                searchItems(textSearchRequest)
                while (listItem.isEmpty()) {
                    delay(10L)
                }
                for (i in 0..<listItem.size) {
                    showFollowers(listItem[i])
                    showLocation(listItem[i])
                }
            }
            keyGone()
            return true
        }
    }

    private fun keyGone() {
        val keyboardOnOff =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        keyboardOnOff?.hideSoftInputFromWindow(searchItem.windowToken, 0)
    }

    private fun searchItems(textSearchRequest: String) {
        listItem.clear()
        if (textSearchRequest.isNotEmpty()) {
            gitHubRestService.searchItem(textSearchRequest)
                .enqueue(object : Callback<ResponseServerUsers> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<ResponseServerUsers>,
                        response: Response<ResponseServerUsers>
                    ) {
                        if (response.code() == 200) {
                            listItem.clear()
                            if (response.body()?.items?.isNotEmpty() == true) {
                                listViewItem.visibility = View.VISIBLE
                                textPlaceholder.visibility = View.GONE
                                listItem.addAll(response.body()?.items!!)
                            listItemAdapter.notifyDataSetChanged()
                            }
                            if (listItem.isEmpty()) {
                                listViewItem.visibility = View.GONE
                                textPlaceholder.text = getString(R.string.text_placeholder_one)
                                textPlaceholder.visibility = View.VISIBLE
                            }
                        } else if(response.code() == 403){
                            listViewItem.visibility = View.GONE
                            textPlaceholder.text = getString(R.string.request_limit_exceeded)
                            textPlaceholder.visibility = View.VISIBLE
                        } else {
                            listViewItem.visibility = View.GONE
                            textPlaceholder.text = getString(R.string.text_placeholder_two)
                            textPlaceholder.visibility = View.VISIBLE
                            Toast.makeText(context, response.code().toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseServerUsers>, t: Throwable) {
                        listViewItem.visibility = View.GONE
                        textPlaceholder.text = getString(R.string.text_placeholder_two)
                        textPlaceholder.visibility = View.VISIBLE
                        Toast.makeText(context, t.message.toString(), Toast.LENGTH_LONG).show()
                    }
                })
        }
    }

    private fun showFollowers(item: Item) {
        var listFollowers: MutableList<Follower> = mutableListOf()
        if (item.login?.isNotEmpty() == true) {
            gitHubRestService.showFollowers(item.login!!).enqueue(object : Callback<List<Follower>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<List<Follower>>,
                    response: Response<List<Follower>>
                ) {
                    if (response.code() == 200) {
                        if (response.body()?.isNotEmpty() == true) {
                            listFollowers.addAll(response.body()!!)
                            item.numberFollowers =
                                if(listFollowers.size == 1) {"${listFollowers.size} ${getString(R.string.follower)}"}
                                else if(listFollowers.size in 2..99){"${listFollowers.size} ${getString(R.string.followers)}"}
                                else if(listFollowers.size > 99){getString(R.string.more_than_99_followers)}
                                else {""}
                            listItemAdapter.notifyDataSetChanged()
                        }
                    } else if(response.code() == 403){
                        Log.i("response followers", getString(R.string.request_limit_exceeded))
                    }
                }

                override fun onFailure(call: Call<List<Follower>>, t: Throwable) {
                    Log.e("showFollowers", t.message.toString())
                }
            })
        }
    }

    private fun showLocation(item: Item) {
        if (item.login?.isNotEmpty() == true) {
            gitHubRestService.showUser(item.login!!).enqueue(object :
                Callback<User> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<User>, response: Response<User>
                ) {
                    if (response.code() == 200) {
                        if (response.body()?.location?.isNotEmpty() == true) {
                            item.location = response.body()?.location!!
                            listItemAdapter.notifyDataSetChanged()
                        }
                    }else if(response.code() == 403){
                        Log.i("response location", getString(R.string.request_limit_exceeded))
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("showLocate", t.message.toString())
                }
            })
        }
    }
}
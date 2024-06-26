package com.msaggik.githubclientapp.view.fragments.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.msaggik.githubclientapp.R
import com.msaggik.githubclientapp.view.adapter.ListRepositoryAdapter
import com.msaggik.githubclientapp.model.entities.itemsearch.Item
import com.msaggik.githubclientapp.model.entities.item.repositories.Repos
import com.msaggik.githubclientapp.model.network.RestGitHub
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ITEM_PREFERENCES = "item_preferences"
private const val OAUTH_PREFERENCES = "oauth_preferences"
private const val ITEM_KEY = "item_key"
private const val TOKEN_KEY = "token_key"
class ItemFragment : Fragment() {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String
    private var item = Item()
    private var countPage = 1

    private lateinit var nickname: TextView
    private lateinit var listViewRepository: RecyclerView
    private lateinit var textPlaceholder: TextView
    private lateinit var buttonForward: ImageView
    private lateinit var buttonBack: ImageView

    private var listRepos: MutableList<Repos> = mutableListOf()
    private lateinit var listRepositoryAdapter: ListRepositoryAdapter

    private val gitHubBaseURL = "https://api.github.com"
    private val retrofit = RestGitHub.createRetrofitObject(gitHubBaseURL)
    private val gitHubRestService = retrofit.create(RestGitHub::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item, container, false)

        context = view.context
        sharedPreferences = context.applicationContext.getSharedPreferences(OAUTH_PREFERENCES, AppCompatActivity.MODE_PRIVATE)
        token = sharedPreferences.getString(TOKEN_KEY, null).toString()
        sharedPreferences = context.applicationContext.getSharedPreferences(ITEM_PREFERENCES, Context.MODE_PRIVATE)
        val jsonItem = sharedPreferences.getString(ITEM_KEY, null)

        nickname = view.findViewById(R.id.nickname)
        listViewRepository = view.findViewById(R.id.list_repository)
        textPlaceholder = view.findViewById(R.id.text_placeholder)
        buttonForward = view.findViewById(R.id.buttonForward)
        buttonBack = view.findViewById(R.id.buttonBack)

        listViewRepository.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listRepositoryAdapter = ListRepositoryAdapter(this, listRepos)
        listViewRepository.adapter = listRepositoryAdapter

        if(jsonItem != null) {
            item = Gson().fromJson(jsonItem, Item::class.java)
            nickname.text = item.login
            repositories(item, countPage)
        }

        buttonForward.setOnClickListener(listener)
        buttonBack.setOnClickListener(listener)

        return view
    }

    private val listener: View.OnClickListener = object : View.OnClickListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun onClick(p0: View?) {
            when (p0?.id) {
                R.id.buttonForward -> {
                    countPage++
                    Log.i("countPage", "" + countPage)
                    repositories(item, countPage)
                }
                R.id.buttonBack -> {
                    if (countPage > 1) {
                        countPage--
                        Log.i("countPage", "" + countPage)
                        repositories(item, countPage)
                    }
                }
            }
        }
    }

    private fun repositories(item: Item, page: Int) {
        listRepos.clear()
        if (item.login?.isNotEmpty() == true) {
            val serviceShowRepositories = if(token.length > 14) {
                gitHubRestService.showRepositoriesOauth(token, item.login!!, page, 7)
            } else {
                gitHubRestService.showRepositories(item.login!!, page, 7)
            }
            serviceShowRepositories.enqueue(object : Callback<List<Repos>> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<List<Repos>>,
                        response: Response<List<Repos>>
                    ) {
                        if (response.code() == 200) {
                            if (response.body()?.isNotEmpty() == true) {
                                placeholderOffMessage()
                                listRepos.addAll(response.body()!!)
                                listRepositoryAdapter.notifyDataSetChanged()
                            }
                            if (listRepos.isEmpty()) {
                                if(countPage > 1) countPage--
                                placeholderOnMessage(context.getString(R.string.text_placeholder_one))
                            }
                        } else if(response.code() == 403){
                            placeholderOnMessage(context.getString(R.string.request_limit_exceeded))
                        } else {
                            placeholderOnMessage(context.getString(R.string.text_placeholder_two))
                        }
                    }

                    override fun onFailure(call: Call<List<Repos>>, t: Throwable) {
                        placeholderOnMessage(context.getString(R.string.text_placeholder_two))
                        Toast.makeText(context, t.message.toString(), Toast.LENGTH_LONG).show()
                    }
                })
        }
    }

    private fun placeholderOnMessage(message: String) {
        listViewRepository.visibility = View.GONE
        textPlaceholder.text = message
        textPlaceholder.visibility = View.VISIBLE
    }

    private fun placeholderOffMessage() {
        listViewRepository.visibility = View.VISIBLE
        textPlaceholder.visibility = View.GONE
    }
}
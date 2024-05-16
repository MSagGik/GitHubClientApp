package com.msaggik.githubclientapp.view.fragments

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.msaggik.githubclientapp.R
import com.msaggik.githubclientapp.util.adapter.ListRepositoryAdapter
import com.msaggik.githubclientapp.util.entities.Item
import com.msaggik.githubclientapp.util.entities.Repos
import com.msaggik.githubclientapp.util.network.RestGitHub
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ITEM_PREFERENCES = "item_preferences"
private const val ITEM_KEY = "item_key"
class ItemFragment : Fragment() {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
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
    private val retrofit = Retrofit.Builder()
        .baseUrl(gitHubBaseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val gitHubRestService = retrofit.create(RestGitHub::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item, container, false)

        context = view.context

        nickname = view.findViewById(R.id.nickname)
        listViewRepository = view.findViewById(R.id.list_repository)
        textPlaceholder = view.findViewById(R.id.text_placeholder)
        buttonForward = view.findViewById(R.id.buttonForward)
        buttonBack = view.findViewById(R.id.buttonBack)

        listViewRepository.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listRepositoryAdapter = ListRepositoryAdapter(listRepos)
        listViewRepository.adapter = listRepositoryAdapter

        sharedPreferences = context.applicationContext.getSharedPreferences(ITEM_PREFERENCES, Context.MODE_PRIVATE)
        val jsonItem = sharedPreferences.getString(ITEM_KEY, null)

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
        Log.i("login", "" + item.login)
        if (item.login?.isNotEmpty() == true) {
            gitHubRestService.showRepositories(item.login!!, page).enqueue(object : Callback<List<Repos>> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<List<Repos>>,
                        response: Response<List<Repos>>
                    ) {
                        Log.i("response repositories", "" + response.code())
                        if (response.code() == 200) {
                            if (response.body()?.isNotEmpty() == true) {
                                listViewRepository.visibility = View.VISIBLE
                                textPlaceholder.visibility = View.GONE
                                listRepos.addAll(response.body()!!)
                                listRepositoryAdapter.notifyDataSetChanged()
                            }
                            if (listRepos.isEmpty()) {
                                listViewRepository.visibility = View.GONE
                                textPlaceholder.text = getString(R.string.text_placeholder_one)
                                textPlaceholder.visibility = View.VISIBLE
                            }
                        } else if(response.code() == 403){
                            listViewRepository.visibility = View.GONE
                            textPlaceholder.text = getString(R.string.request_limit_exceeded)
                            textPlaceholder.visibility = View.VISIBLE
                        } else {
                            listViewRepository.visibility = View.GONE
                            textPlaceholder.text = getString(R.string.text_placeholder_two)
                            textPlaceholder.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(call: Call<List<Repos>>, t: Throwable) {
                        listViewRepository.visibility = View.GONE
                        textPlaceholder.text = getString(R.string.text_placeholder_two)
                        textPlaceholder.visibility = View.VISIBLE
                        Toast.makeText(context, t.message.toString(), Toast.LENGTH_LONG).show()
                        Log.e("showRepositories", t.message.toString())
                    }
                })
        }
    }
}
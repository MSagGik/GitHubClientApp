package com.msaggik.githubclientapp.view.fragments.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.msaggik.githubclientapp.R
import com.msaggik.githubclientapp.model.entities.item.User
import com.msaggik.githubclientapp.model.entities.item.repositories.Repos
import com.msaggik.githubclientapp.model.entities.itemsearch.Item
import com.msaggik.githubclientapp.model.network.RestGitHub
import com.msaggik.githubclientapp.view.adapter.ListRepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val OAUTH_PREFERENCES = "oauth_preferences"
private const val TOKEN_KEY = "token_key"
class ProfileFragment : Fragment() {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String

    private lateinit var avatar: ImageView
    private lateinit var nickname: TextView
    private lateinit var location: TextView
    private lateinit var company: TextView
    private lateinit var followers: TextView
    private lateinit var following: TextView
    private lateinit var numberPublicRepositories: TextView
    private lateinit var textPlaceholder: TextView
    private lateinit var listRepository: RecyclerView

    private var listRepos: MutableList<Repos> = mutableListOf()
    private lateinit var listRepositoryAdapter: ListRepositoryAdapter

    private val gitHubBaseURL = "https://api.github.com"
    private val retrofit = RestGitHub.createRetrofitObject(gitHubBaseURL)
    private val gitHubRestService = retrofit.create(RestGitHub::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        context = view.context
        sharedPreferences = context.applicationContext.getSharedPreferences(OAUTH_PREFERENCES, AppCompatActivity.MODE_PRIVATE)
        token = sharedPreferences.getString(TOKEN_KEY, null).toString()

        avatar = view.findViewById(R.id.avatar)
        nickname = view.findViewById(R.id.nickname)
        location = view.findViewById(R.id.location)
        company = view.findViewById(R.id.company)
        followers = view.findViewById(R.id.followers)
        following = view.findViewById(R.id.following)
        numberPublicRepositories = view.findViewById(R.id.number_public_repositories)
        textPlaceholder = view.findViewById(R.id.text_placeholder)
        listRepository = view.findViewById(R.id.list_repository)

        listRepository.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listRepositoryAdapter = ListRepositoryAdapter(this, listRepos)
        listRepository.adapter = listRepositoryAdapter

        getProfile(token)

        return view
    }

    private fun getProfile(accessToken: String) {
        if (token.length > 14) {
            gitHubRestService.getUserInfo(accessToken).enqueue(object : Callback<User> {
                @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    if (response.code() == 200) {
                        if (response.body()?.login?.isNotEmpty() == true) {
                            placeholderOffMessage()
                            val user: User = response.body()!!
                            Glide.with(avatar)
                                .load(user.avatarUrl)
                                .placeholder(R.drawable.avatar_placeholder)
                                .centerCrop()
                                .transform(RoundedCorners(doToPx(7f, context.applicationContext)))
                                .into(avatar)
                            nickname.text = user.login
                            location.text = user.location
                            company.text = user.company
                            followers.text = "${user.followers} ${getString(R.string.followers_profile)}"
                            following.text = "${user.following} ${getString(R.string.following_profile)}"
                            numberPublicRepositories.text = "${user.publicRepos} ${getString(R.string.public_repositories)}"
                            repositories(accessToken, user.login!!)
                        }
                    } else if(response.code() == 403){
                        placeholderOnMessage(getString(R.string.request_limit_exceeded))
                    } else {
                        placeholderOnMessage(getString(R.string.text_placeholder_two))
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    placeholderOnMessage(getString(R.string.text_placeholder_two))
                    Toast.makeText(context, t.message.toString(), Toast.LENGTH_LONG).show()
                }
            })
        } else {
            placeholderOnMessage(getString(R.string.text_placeholder_two))
        }
    }

    private fun repositories(token: String, nickname: String) {
        listRepos.clear()
        gitHubRestService.showRepositoriesOauth(token, nickname, 1, 100).enqueue(object : Callback<List<Repos>> {
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
                } else if(response.code() == 403){
                    placeholderOnMessage(getString(R.string.request_limit_exceeded))
                } else {
                    placeholderOnMessage(getString(R.string.text_placeholder_two))
                }
            }

            override fun onFailure(call: Call<List<Repos>>, t: Throwable) {
                placeholderOnMessage(getString(R.string.text_placeholder_two))
                Toast.makeText(context, t.message.toString(), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun placeholderOnMessage(message: String) {
        listRepository.visibility = View.GONE
        textPlaceholder.text = message
        textPlaceholder.visibility = View.VISIBLE
    }

    private fun placeholderOffMessage() {
        listRepository.visibility = View.VISIBLE
        textPlaceholder.visibility = View.GONE
    }

    private fun doToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}
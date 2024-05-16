package com.msaggik.githubclientapp.util.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.msaggik.githubclientapp.R
import com.msaggik.githubclientapp.util.entities.Repos
import java.text.SimpleDateFormat
import java.util.Locale

class ListRepositoryAdapter (private val reposListAdd: List<Repos>) : RecyclerView.Adapter<ListRepositoryAdapter.RepositoryViewHolder> () {
    private var reposList = reposListAdd

    fun setReposList(reposListUpdate: List<Repos>) {
        reposList = reposListUpdate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_repository, parent, false)
        return RepositoryViewHolder(view)
    }
    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        holder.bind(reposList[position])
    }
    override fun getItemCount(): Int {
        return reposList.size
    }

    class RepositoryViewHolder(repositoryView: View): RecyclerView.ViewHolder(repositoryView) {

        private val repositoryName: TextView = repositoryView.findViewById(R.id.repository_name)
        private val repositoryLanguage: TextView = repositoryView.findViewById(R.id.repository_language)
        private val repositoryMetadata: TextView = repositoryView.findViewById(R.id.repository_metadata)
        private val repositoryDescription: TextView = repositoryView.findViewById(R.id.repository_description)

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind(model: Repos) {
            repositoryName.text = model.name
            repositoryLanguage.text = model.language
            val date = model.updatedAt?.let { it ->
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(it)
                    ?.let { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(it) }
            }
            repositoryMetadata.text = "${model.forksCount} forks, " +
                    "${model.stargazersCount} stars, default " +
                    "${model.defaultBranch}, last commit ${date}"
            repositoryDescription.text = model.description.toString()
        }
    }
}
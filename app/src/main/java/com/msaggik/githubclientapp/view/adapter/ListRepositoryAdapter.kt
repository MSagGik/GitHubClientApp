package com.msaggik.githubclientapp.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.msaggik.githubclientapp.R
import com.msaggik.githubclientapp.model.entities.item.repositories.Repos
import java.text.SimpleDateFormat
import java.util.Locale

class ListRepositoryAdapter (private val fragment: Fragment, private val reposList: List<Repos>) : RecyclerView.Adapter<ListRepositoryAdapter.RepositoryViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_repository, parent, false)
        return RepositoryViewHolder(fragment, view)
    }
    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        holder.bind(reposList[position])
    }
    override fun getItemCount(): Int {
        return reposList.size
    }

    class RepositoryViewHolder(private val fragment: Fragment, repositoryView: View): RecyclerView.ViewHolder(repositoryView) {

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
            repositoryMetadata.text = "${model.forksCount} ${fragment.getString(R.string.forks)}, " +
                    "${model.stargazersCount} ${fragment.getString(R.string.stars)}, ${fragment.getString(R.string.default_)} " +
                    "${model.defaultBranch}, ${fragment.getString(R.string.last_commit)} ${date}"
            repositoryDescription.text = if (model.description != null) {
                model.description.toString()
            } else {
                ""
            }
        }
    }
}
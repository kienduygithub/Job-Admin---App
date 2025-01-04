package com.example.jobapp_u.home.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.jobapp_u.databinding.TpoCardLayoutBinding
import com.example.jobapp_u.model.Tpo

class TpoAdapter : RecyclerView.Adapter<TpoAdapter.TpoViewHolder>() {

    private val tpoList: MutableList<Tpo> = mutableListOf()

    inner class TpoViewHolder(
        private val binding: TpoCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(tpo: Tpo) {
            binding.ivProfileTpo.load(tpo.imageUri)
            binding.tvTpoName.text = tpo.username
            binding.tvTpoEmail.text = tpo.email
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TpoViewHolder {
        val binding = TpoCardLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TpoViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TpoViewHolder, position: Int) {
        holder.bind(tpoList[position])
    }

    override fun getItemCount(): Int = tpoList.size


    fun setData(newTpoList: List<Tpo>) {
        tpoList.clear()
        tpoList.addAll(newTpoList)
        notifyDataSetChanged()
    }

}
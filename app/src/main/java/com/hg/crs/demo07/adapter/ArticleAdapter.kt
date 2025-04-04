package com.hg.crs.demo07.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hg.crs.databinding.Dm07ItemUserBinding
import com.hg.crs.demo07.entity.Article


class ArticleAdapter(private val context: Context) : RecyclerView.Adapter<BindingViewHolder>() {

    private val data = ArrayList<Article>()

    fun setData(data: List<Article>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val binding = Dm07ItemUserBinding.inflate(LayoutInflater.from(context), parent, false)
        return BindingViewHolder(binding);
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        val item = data[position]
        val binding = holder.binding as Dm07ItemUserBinding
        binding.dm07Text.text = item.text
    }

    override fun getItemCount(): Int = data.size


}
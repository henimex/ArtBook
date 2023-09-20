package com.hendev.artbook

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hendev.artbook.databinding.RcycleRowBinding

class ArtAdaptor(val list: ArrayList<ArtModel>) : RecyclerView.Adapter<ArtAdaptor.ArtHolder>() {
    class ArtHolder(val binding: RcycleRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtHolder {
        val binding = RcycleRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ArtHolder, position: Int) {
        holder.binding.rvTextView.text = list[position].artName
        holder.binding.rvSubTextView.text = list[position].artistName

        val cont: Context = holder.itemView.context

        holder.itemView.setOnClickListener { data ->
            val intent = Intent(cont, DetailArt::class.java)
            intent.putExtra("info", "old")
            intent.putExtra("id", list[position].id)
            cont.startActivity(intent)

        }
    }
}
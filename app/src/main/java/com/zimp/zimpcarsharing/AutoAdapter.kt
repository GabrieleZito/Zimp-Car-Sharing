package com.zimp.zimpcarsharing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zimp.zimpcarsharing.databinding.CardViewAutoBinding

class AutoAdapter(private val mList:List<AutoViewModel>): RecyclerView.Adapter<AutoAdapter.ViewHolder>() {
    class ViewHolder(binding: CardViewAutoBinding):RecyclerView.ViewHolder(binding.root){
        val img = binding.cardImg
        val marca = binding.marca
        val modello = binding.modello
        val tariffa = binding.tariffa
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardViewAutoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val AutoViewModel = mList[position]
        holder.img.setImageResource(AutoViewModel.img)
        holder.marca.text = AutoViewModel.marca
        holder.modello.text = AutoViewModel.modello
        holder.tariffa.text = "${AutoViewModel.tariffa}"

    }
}
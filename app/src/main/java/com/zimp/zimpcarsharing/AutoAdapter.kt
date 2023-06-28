package com.zimp.zimpcarsharing

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zimp.zimpcarsharing.databinding.CardViewAutoBinding
import com.zimp.zimpcarsharing.models.Auto

class AutoAdapter(private val mList:List<Auto>): RecyclerView.Adapter<AutoAdapter.ViewHolder>() {
    class ViewHolder(binding: CardViewAutoBinding):RecyclerView.ViewHolder(binding.root){
        val img = binding.cardImg
        val marca = binding.marca
        val modello = binding.modello
        val tariffa = binding.tariffa
        val bottone = binding.prenotaBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardViewAutoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val auto = mList[position]
        holder.img.setImageResource(R.drawable.ic_launcher_background)
        holder.marca.text = auto.marca
        holder.modello.text = auto.modello
        holder.tariffa.text = "${auto.tariffa}"
        holder.bottone.setOnClickListener { Log.i("PEPPE", "Cliccato ${holder.modello.text}") }

    }

}
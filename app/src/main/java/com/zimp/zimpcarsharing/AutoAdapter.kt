package com.zimp.zimpcarsharing

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zimp.zimpcarsharing.databinding.ActivityPrenotazioneBinding
import com.zimp.zimpcarsharing.databinding.CardViewAutoBinding
import com.zimp.zimpcarsharing.models.Auto
import com.zimp.zimpcarsharing.models.Utente

class AutoAdapter(private val mList:List<Auto>, private val context: Context, private val binding: ActivityPrenotazioneBinding, private val utente: Utente?): RecyclerView.Adapter<AutoAdapter.ViewHolder>() {
    class ViewHolder(binding: CardViewAutoBinding):RecyclerView.ViewHolder(binding.root){
        val img = binding.cardImg
        val marca = binding.marca
        val modello = binding.modello
        val tariffa = binding.tariffa
        val prenotaBtn = binding.prenotaBtn
        val mapBtn = binding.mapBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardViewAutoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pa = PrenotazioneActivity()
        //val binding2 = ActivityPrenotazioneBinding.inflate(LayoutInflater.from(pa.getContext()))
        val auto = mList[position]
        holder.img.setImageResource(R.drawable.ic_launcher_background)
        holder.marca.text = auto.marca+" "
        holder.modello.text = auto.modello
        holder.tariffa.text = "${auto.tariffa}"
        holder.prenotaBtn.setOnClickListener {
            pa.prenotaAuto(auto, context, utente)
        }
        holder.mapBtn.setOnClickListener {
            pa.avviaMappa(auto.latitudine, auto.longitudine, binding)
        }

    }

}
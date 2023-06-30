package com.zimp.zimpcarsharing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zimp.zimpcarsharing.databinding.CardAuto2Binding
import com.zimp.zimpcarsharing.models.Auto
import com.zimp.zimpcarsharing.models.Utente

class AutoAdapter2(private var list: List<Auto>, private val utente: Utente?): RecyclerView.Adapter<AutoAdapter2.ViewHolder>() {

    class ViewHolder(binding: CardAuto2Binding):RecyclerView.ViewHolder(binding.root){
        val img = binding.cardImg
        val marca = binding.marca
        val modello = binding.modello
        val testo = binding.prenotataText
        val ore = binding.orePrenotata
        val elimina = binding.eliminaBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardAuto2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ma = MieAutoActivity()
        val auto = list[position]
        holder.img.setImageResource(R.drawable.ic_launcher_background)
        holder.marca.text = auto.marca
        holder.modello.text = auto.modello
        holder.testo.text = if (utente?.idUtente==auto.idproprietario) "Sei il proprietario" else "Prenotata per: "
        holder.ore.text = if (holder.testo.text=="Sei il proprietario") "" else "${auto.orePrenotata}"
        holder.elimina.setOnClickListener {
            ma.elimina(auto)
        }
    }

}
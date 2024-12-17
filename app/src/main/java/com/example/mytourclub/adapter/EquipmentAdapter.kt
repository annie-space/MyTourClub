package com.example.mytourclub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.model.Equipment

class EquipmentAdapter(
    private val equipmentList: MutableList<Equipment>,
    private val recommendedQuantities: Map<Int, Int>,
    private val onRentClickListener: (Equipment, Int, View) -> Unit
) : RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_equipment, parent, false)
        return EquipmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: EquipmentViewHolder, position: Int) {
        val equipment = equipmentList[position]
        val recommendedQuantity = recommendedQuantities[equipment.id] ?: 1

        holder.equipmentName.text = equipment.name
        holder.equipmentCost.text = equipment.priceUnit.toString()
        holder.quantityInput.setText(recommendedQuantity.toString())

        holder.btnRent.setOnClickListener {
            val quantity = holder.quantityInput.text.toString().toIntOrNull() ?: recommendedQuantity
            holder.btnRent.visibility = View.GONE
            holder.tvRented.visibility = View.VISIBLE
            onRentClickListener(equipment, quantity, holder.btnRent)
        }
    }

    override fun getItemCount(): Int = equipmentList.size

    class EquipmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val equipmentName: TextView = itemView.findViewById(R.id.tvEquipmentName)
        val equipmentCost: TextView = itemView.findViewById(R.id.tvEquipmentPrice)
        val quantityInput: EditText = itemView.findViewById(R.id.etQuantity)
        val btnRent: Button = itemView.findViewById(R.id.btnRent)
        val tvRented: TextView = itemView.findViewById(R.id.tvRented)
    }
}

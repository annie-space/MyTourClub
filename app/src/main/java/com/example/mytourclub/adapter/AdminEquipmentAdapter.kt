package com.example.mytourclub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.model.Equipment

class AdminEquipmentAdapter(
    private var equipmentList: List<Equipment>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<AdminEquipmentAdapter.EquipmentViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(equipment: Equipment)
        fun onDeleteClick(equipment: Equipment)
    }

    private val expandedPositions = mutableSetOf<Int>()

    inner class EquipmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEquipmentName: TextView = itemView.findViewById(R.id.tvEquipmentName)
        val tvEquipmentDetails: TextView = itemView.findViewById(R.id.tvEquipmentDetails)
        val tvAvailableQuantity: TextView = itemView.findViewById(R.id.tvAvailableQuantity)
        val imgEquipmentPhoto: ImageView = itemView.findViewById(R.id.imgEquipmentPhoto)
        val btnDeleteEquipment: Button = itemView.findViewById(R.id.btnDeleteEquipment)
        val btnEditEquipment: Button = itemView.findViewById(R.id.btnEditEquipment)

        init {
            itemView.setOnClickListener {
                if (expandedPositions.contains(adapterPosition)) {
                    expandedPositions.remove(adapterPosition)
                    tvEquipmentDetails.visibility = View.GONE
                    tvAvailableQuantity.visibility = View.GONE
                    imgEquipmentPhoto.visibility = View.GONE
                } else {
                    expandedPositions.add(adapterPosition)
                    tvEquipmentDetails.visibility = View.VISIBLE
                    tvAvailableQuantity.visibility = View.VISIBLE
                    imgEquipmentPhoto.visibility = View.VISIBLE
                }
                notifyItemChanged(adapterPosition)
            }

            btnDeleteEquipment.setOnClickListener {
                listener.onDeleteClick(equipmentList[adapterPosition])
            }

            btnEditEquipment.setOnClickListener {
                listener.onItemClick(equipmentList[adapterPosition])
            }
        }

        fun bind(equipment: Equipment) {
            tvEquipmentName.text = equipment.name
            tvEquipmentDetails.text = "Количество: ${equipment.quantity}\nОписание: ${equipment.description}\nЦена за единицу: ${equipment.priceUnit}"
            imgEquipmentPhoto.setImageResource(equipment.photo)
            tvEquipmentDetails.visibility = if (expandedPositions.contains(adapterPosition)) View.VISIBLE else View.GONE
            tvAvailableQuantity.visibility = if (expandedPositions.contains(adapterPosition)) View.VISIBLE else View.GONE
            imgEquipmentPhoto.visibility = if (expandedPositions.contains(adapterPosition)) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_equipment, parent, false)
        return EquipmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: EquipmentViewHolder, position: Int) {
        holder.bind(equipmentList[position])
    }

    override fun getItemCount() = equipmentList.size

    fun updateEquipment(newEquipmentList: List<Equipment>) {
        equipmentList = newEquipmentList
        notifyDataSetChanged()
    }
}

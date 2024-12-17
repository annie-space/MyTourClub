package com.example.mytourclub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.model.Guide

class GuideAdapter(
    private var guides: List<Guide>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<GuideAdapter.GuideViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(guide: Guide)
        fun onDeleteClick(guide: Guide)
    }

    private val expandedPositions = mutableSetOf<Int>()

    inner class GuideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGuideName: TextView = itemView.findViewById(R.id.tvGuideName)
        val tvGuideDetails: TextView = itemView.findViewById(R.id.tvGuideDetails)
        val btnDeleteGuide: Button = itemView.findViewById(R.id.btnDeleteGuide)
        val btnEditGuide: Button = itemView.findViewById(R.id.btnEditGuide)

        init {
            itemView.setOnClickListener {
                if (expandedPositions.contains(adapterPosition)) {
                    expandedPositions.remove(adapterPosition)
                    tvGuideDetails.visibility = View.GONE
                } else {
                    expandedPositions.add(adapterPosition)
                    tvGuideDetails.visibility = View.VISIBLE
                }
                notifyItemChanged(adapterPosition)
            }

            btnDeleteGuide.setOnClickListener {
                listener.onDeleteClick(guides[adapterPosition])
            }

            btnEditGuide.setOnClickListener {
                listener.onItemClick(guides[adapterPosition])
            }
        }

        fun bind(guide: Guide) {
            tvGuideName.text = guide.name
            tvGuideDetails.text = "Email: ${guide.email}\nТелефон: ${guide.phone}\nОпыт: ${guide.experience}\nСпециализация: ${guide.specialization}\nПол: ${guide.gender}"
            tvGuideDetails.visibility = if (expandedPositions.contains(adapterPosition)) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_guide, parent, false)
        return GuideViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        holder.bind(guides[position])
    }

    override fun getItemCount() = guides.size

    fun updateGuides(newGuides: List<Guide>) {
        guides = newGuides
        notifyDataSetChanged()
    }
}

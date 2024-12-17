package com.example.mytourclub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.model.User

class ParticipantAdapter(
    private val participants: List<User>
) : RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_participant, parent, false)
        return ParticipantViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val participant = participants[position]
        holder.tvParticipantName.text = participant.name
        holder.tvParticipantEmail.text = participant.email
        holder.tvParticipantPhone.text = participant.phone
    }

    override fun getItemCount(): Int {
        return participants.size
    }

    class ParticipantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvParticipantName: TextView = itemView.findViewById(R.id.tvParticipantName)
        val tvParticipantEmail: TextView = itemView.findViewById(R.id.tvParticipantEmail)
        val tvParticipantPhone: TextView = itemView.findViewById(R.id.tvParticipantPhone)
    }
}

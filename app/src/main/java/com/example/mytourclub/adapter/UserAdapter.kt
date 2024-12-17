package com.example.mytourclub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.model.User
import java.text.SimpleDateFormat
import java.util.*

class UserAdapter(
    private var users: List<User>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(user: User)
        fun onDeleteClick(user: User)
    }

    private val expandedPositions = mutableSetOf<Int>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvUserDetails: TextView = itemView.findViewById(R.id.tvUserDetails)
        val btnDeleteUser: Button = itemView.findViewById(R.id.btnDeleteUser)

        init {
            itemView.setOnClickListener {
                if (expandedPositions.contains(adapterPosition)) {
                    expandedPositions.remove(adapterPosition)
                    tvUserDetails.visibility = View.GONE
                } else {
                    expandedPositions.add(adapterPosition)
                    tvUserDetails.visibility = View.VISIBLE
                }
                notifyItemChanged(adapterPosition)
            }

            btnDeleteUser.setOnClickListener {
                listener.onDeleteClick(users[adapterPosition])
            }

        }

        fun bind(user: User) {
            tvUserName.text = user.name
            tvUserDetails.text = "Дата рождения: ${dateFormat.format(user.dob)}\nEmail: ${user.email}\nТелефон: ${user.phone}\nГруппа здоровья: ${user.healthGroup}\nПол: ${user.gender}"
            tvUserDetails.visibility = if (expandedPositions.contains(adapterPosition)) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}

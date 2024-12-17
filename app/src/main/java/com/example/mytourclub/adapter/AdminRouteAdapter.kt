package com.example.mytourclub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.model.Route
import com.example.mytourclub.model.Equipment
import com.example.mytourclub.model.RouteEquipment

class AdminRouteAdapter(
    private var routesWithEquipment: List<Pair<Route, List<Pair<RouteEquipment, Equipment?>>>>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<AdminRouteAdapter.RouteViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(route: Route)
        fun onDeleteClick(route: Route)
    }

    private val expandedPositions = mutableSetOf<Int>()

    inner class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRouteName: TextView = itemView.findViewById(R.id.tvRouteName)
        val tvEquipmentDetails: TextView = itemView.findViewById(R.id.tvEquipmentDetails)
        val btnDeleteRoute: Button = itemView.findViewById(R.id.btnDeleteRoute)
        val btnEditRoute: Button = itemView.findViewById(R.id.btnEditRoute)

        init {
            itemView.setOnClickListener {
                if (expandedPositions.contains(adapterPosition)) {
                    expandedPositions.remove(adapterPosition)
                    tvEquipmentDetails.visibility = View.GONE
                } else {
                    expandedPositions.add(adapterPosition)
                    tvEquipmentDetails.visibility = View.VISIBLE
                }
                notifyItemChanged(adapterPosition)
            }

            btnDeleteRoute.setOnClickListener {
                listener.onDeleteClick(routesWithEquipment[adapterPosition].first)
            }

            btnEditRoute.setOnClickListener {
                listener.onItemClick(routesWithEquipment[adapterPosition].first)
            }
        }

        fun bind(routeWithEquipment: Pair<Route, List<Pair<RouteEquipment, Equipment?>>>) {
            val (route, routeEquipmentList) = routeWithEquipment
            tvRouteName.text = route.name
            tvEquipmentDetails.text = routeEquipmentList.joinToString(separator = "\n") { (routeEquipment, equipment) ->
                "${equipment?.name ?: "Неизвестное снаряжение"} - Рекомендуемое количество: ${routeEquipment.quantityRequired}"
            }
            tvEquipmentDetails.visibility = if (expandedPositions.contains(adapterPosition)) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(routesWithEquipment[position])
    }

    override fun getItemCount() = routesWithEquipment.size

    fun updateRoutes(newRoutesWithEquipment: List<Pair<Route, List<Pair<RouteEquipment, Equipment?>>>>) {
        routesWithEquipment = newRoutesWithEquipment
        notifyDataSetChanged()
    }
}

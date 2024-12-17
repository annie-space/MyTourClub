package com.example.mytourclub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.model.Route
import java.text.SimpleDateFormat
import java.util.Locale

class GuidRouteAdapter(
    private val routes: List<Route>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<GuidRouteAdapter.RouteViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(route: Route)
        fun onParticipantsClick(route: Route)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_guid_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routes[position]
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formattedDate = sdf.format(route.dates)

        holder.tvRouteName.text = route.name
        holder.tvRouteDate.text = formattedDate

        holder.itemView.setOnClickListener {
            itemClickListener.onParticipantsClick(route)
        }
    }

    override fun getItemCount(): Int {
        return routes.size
    }

    class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRouteName: TextView = itemView.findViewById(R.id.tvRouteName)
        val tvRouteDate: TextView = itemView.findViewById(R.id.tvRouteDate)
    }
}

package com.example.mytourclub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.model.Route

class RouteAdapter(
    private var routes: List<Route>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routes[position]
        holder.bind(route)
    }

    override fun getItemCount(): Int = routes.size

    fun updateList(newRoutes: List<Route>) {
        routes = newRoutes
        notifyDataSetChanged()
    }

    inner class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val routeName: TextView = itemView.findViewById(R.id.textViewName)
        private val routePrice: TextView = itemView.findViewById(R.id.textViewPrice)
        private val routeLength: TextView = itemView.findViewById(R.id.textViewLength)
        private val routeDuration: TextView = itemView.findViewById(R.id.textViewDuration)
        private val routeImage: ImageView = itemView.findViewById(R.id.imageViewRoute)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(route: Route) {
            routeName.text = route.name
            routePrice.text = "${route.price} руб."
            routeLength.text = "${route.length} км"
            routeDuration.text = "${route.duration} дней"
            routeImage.setImageResource(route.photoResId ?: R.drawable.icon) // Используем значение по умолчанию
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(routes[position])
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(route: Route)
    }
}

package com.example.mytourclub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.model.Booking
import com.example.mytourclub.model.Route

class BookedRouteAdapter(
    private val bookings: List<Booking>,
    private val routes: List<Route>,
    private val onItemClickListener: OnItemClickListener,
    private val onDeleteClickListener: (Booking) -> Unit
) : RecyclerView.Adapter<BookedRouteAdapter.BookedRouteViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(booking: Booking)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookedRouteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booked_route, parent, false)
        return BookedRouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookedRouteViewHolder, position: Int) {
        val booking = bookings[position]
        val route = routes.find { it.id == booking.routeId }

        // Отображение названия маршрута и статуса бронирования
        holder.routeName.text = route?.name ?: "Неизвестный маршрут"
        holder.bookingStatus.text = if (booking.status) "Подтверждено" else "Ожидает подтверждения"

        // Обработка нажатия на кнопку "Подробнее"
        holder.buttonDetails.setOnClickListener {
            onItemClickListener.onItemClick(booking)
        }

        // Показать кнопку "Удалить", если статус бронирования равен 0
        if (!booking.status) {
            holder.buttonDelete.visibility = View.VISIBLE
            holder.buttonDelete.setOnClickListener {
                onDeleteClickListener(booking)
            }
        } else {
            holder.buttonDelete.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = bookings.size

    class BookedRouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val routeName: TextView = itemView.findViewById(R.id.routeName)
        val bookingStatus: TextView = itemView.findViewById(R.id.bookingStatus)
        val buttonDetails: Button = itemView.findViewById(R.id.buttonDetails)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)
    }
}

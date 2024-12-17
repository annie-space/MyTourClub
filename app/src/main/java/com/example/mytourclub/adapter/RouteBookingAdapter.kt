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

class RouteBookingAdapter(
    private var routeBookings: MutableList<Pair<Route, MutableList<Booking>>>,
    private val userNames: Map<Int, String>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RouteBookingAdapter.RouteViewHolder>() {

    interface OnItemClickListener {
        fun onConfirmClick(booking: Booking)
    }

    private val expandedPositions = mutableSetOf<Int>()

    inner class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRouteName: TextView = itemView.findViewById(R.id.tvRouteName)
        val tvNoBookings: TextView = itemView.findViewById(R.id.tvNoBookings)
        val bookingsContainer: ViewGroup = itemView.findViewById(R.id.bookingsContainer)

        init {
            itemView.setOnClickListener {
                if (expandedPositions.contains(adapterPosition)) {
                    expandedPositions.remove(adapterPosition)
                    bookingsContainer.visibility = View.GONE
                } else {
                    expandedPositions.add(adapterPosition)
                    bookingsContainer.visibility = View.VISIBLE
                }
                notifyItemChanged(adapterPosition)
            }
        }

        fun bind(routeBooking: Pair<Route, List<Booking>>) {
            val (route, bookings) = routeBooking
            tvRouteName.text = route.name

            bookingsContainer.removeAllViews()
            if (bookings.isEmpty()) {
                tvNoBookings.visibility = View.VISIBLE
            } else {
                tvNoBookings.visibility = View.GONE
                bookings.forEach { booking ->
                    val bookingView = LayoutInflater.from(itemView.context)
                        .inflate(R.layout.item_booking, bookingsContainer, false)
                    val tvBookingInfo = bookingView.findViewById<TextView>(R.id.tvBookingInfo)
                    val btnConfirm = bookingView.findViewById<Button>(R.id.btnConfirm)
                    val tvConfirmed = bookingView.findViewById<TextView>(R.id.tvConfirmed)

                    val userName = userNames[booking.userId] ?: "Unknown User"
                    tvBookingInfo.text = "$userName"

                    if (booking.status == true) {
                        btnConfirm.visibility = View.GONE
                        tvConfirmed.visibility = View.VISIBLE
                    } else {
                        btnConfirm.visibility = View.VISIBLE
                        tvConfirmed.visibility = View.GONE
                    }

                    btnConfirm.setOnClickListener {
                        listener.onConfirmClick(booking)
                        btnConfirm.visibility = View.GONE
                        tvConfirmed.visibility = View.VISIBLE
                    }

                    bookingsContainer.addView(bookingView)
                }
            }

            // Сохранение состояния видимости контейнера
            bookingsContainer.visibility = if (expandedPositions.contains(adapterPosition)) View.VISIBLE else View.GONE

            // Установка цвета фона для маршрутов с неподтвержденными бронированиями
            itemView.setBackgroundColor(
                if (bookings.any { it.status == false }) itemView.context.getColor(R.color.orange)
                else itemView.context.getColor(android.R.color.transparent)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_route_bookings, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(routeBookings[position])
    }

    override fun getItemCount() = routeBookings.size

    fun updateRouteBookings(newRouteBookings: List<Pair<Route, List<Booking>>>) {
        routeBookings = newRouteBookings.map { it.first to it.second.toMutableList() }.toMutableList()
        notifyDataSetChanged()
    }

    fun updateBooking(booking: Booking) {
        val routePosition = routeBookings.indexOfFirst { it.first.id == booking.routeId }
        if (routePosition != -1) {
            val routeBooking = routeBookings[routePosition]
            val bookingPosition = routeBooking.second.indexOfFirst { it.id == booking.id }
            if (bookingPosition != -1) {
                routeBooking.second[bookingPosition] = booking
                notifyItemChanged(routePosition)
            }
        }
    }
}

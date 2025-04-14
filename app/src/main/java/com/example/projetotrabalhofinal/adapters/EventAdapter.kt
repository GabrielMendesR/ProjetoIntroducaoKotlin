package com.example.projetotrabalhofinal.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projetotrabalhofinal.R
import com.example.projetotrabalhofinal.db.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventAdapter(
    private val events: List<Event>,
    private val onItemClick: (Event) -> Unit,
    private val onDeleteClick: (Event) -> Unit // Nova callback para exclusão
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        private val textDescription: TextView = itemView.findViewById(R.id.textDescription)
        private val textDateTime: TextView = itemView.findViewById(R.id.textDateTime)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(event: Event) {
            textTitle.text = event.title
            textDescription.text = event.description

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dateString = sdf.format(Date(event.eventTime))
            textDateTime.text = dateString

            itemView.setOnClickListener { onItemClick(event) }
            btnDelete.setOnClickListener { onDeleteClick(event) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount() = events.size
}
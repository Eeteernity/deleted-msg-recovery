package com.aayan.deletedmsgrecovery

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private var items: List<CachedMessage> = emptyList()
    private val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())

    fun submitList(newItems: List<CachedMessage>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.sender.text = item.sender
        holder.content.text = item.content
        holder.timestamp.text = dateFormat.format(Date(item.timestampMillis))

        if (item.wasMarkedDeleted) {
            holder.badge.visibility = View.VISIBLE
            holder.content.setTextColor(Color.parseColor("#D32F2F"))
        } else {
            holder.badge.visibility = View.GONE
            holder.content.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sender: TextView = view.findViewById(R.id.txtSender)
        val content: TextView = view.findViewById(R.id.txtContent)
        val timestamp: TextView = view.findViewById(R.id.txtTimestamp)
        val badge: TextView = view.findViewById(R.id.badgeDeleted)
    }
}

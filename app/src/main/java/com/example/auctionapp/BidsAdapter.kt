package com.example.auctionapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BidsAdapter(private val bidList: List<BidEntry>) :
    RecyclerView.Adapter<BidsAdapter.BidViewHolder>() {

    class BidViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val paddleTextView: TextView = view.findViewById(R.id.paddleTextView)
        val bidTextView: TextView = view.findViewById(R.id.bidTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BidViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bid, parent, false)
        return BidViewHolder(view)
    }

    override fun onBindViewHolder(holder: BidViewHolder, position: Int) {
        val bid = bidList[position]
        holder.nameTextView.text = bid.name
        holder.paddleTextView.text = "Paddle: ${bid.paddleNumber}"
        holder.bidTextView.text = "Bid: ${bid.bidAmount ?: "No Bid"}"
    }

    override fun getItemCount(): Int = bidList.size
}

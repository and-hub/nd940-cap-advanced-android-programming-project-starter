package com.example.android.politicalpreparedness.election.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.utils.toFormattedString
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("electionListData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Election>?) {
    val adapter = recyclerView.adapter as ElectionListAdapter
    adapter.submitList(data)
}

@BindingAdapter("electionDay")
fun bindTextViewToElectionDay(textView: TextView, electionDay: Date){
    textView.text = electionDay.toFormattedString()
}
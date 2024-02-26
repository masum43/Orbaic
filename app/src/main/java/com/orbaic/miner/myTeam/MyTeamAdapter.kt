package com.orbaic.miner.myTeam

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orbaic.miner.R
import com.orbaic.miner.common.Constants
import com.orbaic.miner.databinding.GridSingleViewBinding

class MyTeamAdapter : RecyclerView.Adapter<MyTeamAdapter.ViewHolder>() {

    private var list: List<Team> = listOf()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = GridSingleViewBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val team = list[position]
        Glide.with(holder.itemView.context)
            .load(team.imageUrl)
            .error(R.drawable.demo_avatar2)
            .into(holder.binding.circularImageTeam)
        holder.binding.tvUserName.text = team.userName

        holder.binding.cvMininigStatus.visibility =
            if (team.miningStatus == Constants.STATUS_ON) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateData(newList: List<Team>) {
        val diffCallback = TeamDiffCallback(list, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        list = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun setContext(context: Context) {
        this.context = context
    }

    fun setList(list: List<Team>) {
        this.list = list
    }

    inner class ViewHolder(val binding: GridSingleViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class TeamDiffCallback(
        private val oldList: List<Team>,
        private val newList: List<Team>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}

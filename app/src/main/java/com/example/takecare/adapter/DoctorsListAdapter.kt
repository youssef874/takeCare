package com.example.avionav.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.takecare.databinding.DoctorsListItemBinding
import com.example.takecare.model.Doctor

/**
 * This class will be the adapter of the home screen list
 * @param onClickListener: an lambda function get [Plane] as param to invoke it in this class
 */
class DoctorsListAdapter(private val onClickListener: ONClickListener):
    ListAdapter<Doctor,DoctorsListAdapter.ListItemViewHolder>(DiffCallback) {

    /**
     * This class represent the list item view holder
     * @param binding: the list item layout through data binding
     */
    class ListItemViewHolder(private var binding: DoctorsListItemBinding)
        : RecyclerView.ViewHolder(binding.root){
            fun bind(doctor: Doctor){
                binding.doctor = doctor
                binding.executePendingBindings()
            }
        }

    /**
     * This object help recycleView keep updated if the data changed
     */
    companion object DiffCallback: DiffUtil.ItemCallback<Doctor>() {
        override fun areItemsTheSame(oldItem: Doctor, newItem: Doctor): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Doctor, newItem: Doctor): Boolean {
            return oldItem == newItem
        }
    }

    class ONClickListener(val clickListener: (doctor: Doctor) -> Unit){
        fun onClick(doctor: Doctor) = clickListener(doctor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        return ListItemViewHolder(DoctorsListItemBinding
            .inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val data = getItem(position) as Doctor

        holder.itemView.setOnClickListener {
            onClickListener.onClick(data)
        }
        holder.bind(data)
    }
}
package com.example.takecare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.avionav.adapter.DoctorsListAdapter
import com.example.takecare.databinding.AppointmentListItemBinding
import com.example.takecare.databinding.DoctorsListItemBinding
import com.example.takecare.model.Appointment
import com.example.takecare.model.Doctor

class AppointmentAdapter (private val onClickListener: ONClickListener):
    ListAdapter<Appointment, AppointmentAdapter.ListItemViewHolder>(DiffCallback) {

    /**
     * This class represent the list item view holder
     * @param binding: the list item layout through data binding
     */
    class ListItemViewHolder(var binding: AppointmentListItemBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(appointment: Appointment){
            binding.appointment = appointment
            binding.executePendingBindings()
        }
    }

    /**
     * This object help recycleView keep updated if the data changed
     */
    companion object DiffCallback: DiffUtil.ItemCallback<Appointment>() {
        override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
            return oldItem == newItem
        }
    }

    class ONClickListener(val clickListener: (appointment: Appointment) -> Unit){
        fun onClick(appointment: Appointment) = clickListener(appointment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        return ListItemViewHolder(
            AppointmentListItemBinding
            .inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val data = getItem(position) as Appointment

        holder.binding.editAppointmentButton.setOnClickListener {
            onClickListener.onClick(data)
        }
        holder.bind(data)
    }
}
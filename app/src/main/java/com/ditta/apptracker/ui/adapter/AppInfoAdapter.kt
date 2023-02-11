package com.ditta.apptracker.ui.adapter

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.ditta.apptracker.databinding.ItemAppInfoBinding
import com.ditta.apptracker.model.AppInfoUi


class AppInfoAdapter(
    private val itemListener: ItemListener,
    private val packageManager: PackageManager
) : RecyclerView.Adapter<AppInfoAdapter.AppInfoViewHolder>(), Filterable {

    private lateinit var appInfos: List<AppInfoUi>
    private var appInfosFiltered = mutableListOf<AppInfoUi>()

    fun updateAppInfos(appInfos: List<AppInfoUi>) {
        this.appInfos = appInfos
        this.appInfosFiltered = appInfos as MutableList<AppInfoUi>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppInfoViewHolder {
        val binding = ItemAppInfoBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AppInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppInfoViewHolder, position: Int) {
        holder.bind(appInfosFiltered[position], itemListener)
    }

    override fun getItemCount(): Int = appInfosFiltered.size

    inner class AppInfoViewHolder(
        private val binding: ItemAppInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(appInfo: AppInfoUi, itemListener: ItemListener) {
            try {
                val appIcon: Drawable = packageManager
                    .getApplicationIcon(appInfo.packageName)
                binding.appIcon.setImageDrawable(appIcon)
                binding.appIcon.visibility = View.VISIBLE
            } catch (e: PackageManager.NameNotFoundException) {
            }
            binding.appInfo = appInfo
            binding.itemListener = itemListener
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                appInfosFiltered = if (charSearch.isEmpty()) {
                    appInfos.toMutableList()
                } else {
                    val filteredList = ArrayList<AppInfoUi>()
                    appInfos
                        .filter {
                            it.packageName.contains(charSearch.lowercase())
                        }
                        .forEach { filteredList.add(it) }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = appInfosFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                appInfosFiltered = if (results?.values == null)
                    appInfos as ArrayList<AppInfoUi>
                else
                    results.values as ArrayList<AppInfoUi>

                notifyDataSetChanged()
            }
        }
    }
}

class ItemListener(private val onChecked: (appInfo: AppInfoUi) -> Unit) {

    fun onCheckedChanged(appInfo: AppInfoUi, isChecked: Boolean) {
        appInfo.toTrack = isChecked
        onChecked(appInfo)
    }
}
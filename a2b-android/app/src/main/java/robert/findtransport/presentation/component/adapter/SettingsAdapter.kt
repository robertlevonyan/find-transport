package robert.findtransport.presentation.component.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import robert.findtransport.R
import robert.findtransport.base.BaseRecyclerViewAdapter
import robert.findtransport.base.BaseViewHolder
import robert.findtransport.data.model.SettingData
import robert.findtransport.data.model.SettingData.EndViewType.*
import robert.findtransport.databinding.ItemSettingBinding
import robert.findtransport.databinding.ItemSettingProgressBinding
import robert.findtransport.databinding.ItemSettingSwitchBinding
import robert.findtransport.presentation.component.rv.SettingsDiffCallback
import robert.findtransport.presentation.settings.SettingsViewModel
import robert.findtransport.utils.extensions.set
import robert.findtransport.utils.extensions.setEndView

class SettingsAdapter(private val settingsViewModel: SettingsViewModel) :
    BaseRecyclerViewAdapter<ItemSettingBinding, SettingData, SettingsAdapter.SettingsViewHolder>(
        AsyncDifferConfig.Builder(SettingsDiffCallback())) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
      SettingsViewHolder(ItemSettingBinding.inflate(LayoutInflater.from(parent.context), parent, false))

  inner class SettingsViewHolder(private val binding: ItemSettingBinding) :
      BaseViewHolder<ItemSettingBinding, SettingData>(binding) {

    override fun bind(item: SettingData) {
      binding.run {
        val colorsBg = itemView.context.resources.getIntArray(R.array.colors_bg)

        ivSettingIcon.setImageResource(item.icon)
        ivSettingIcon.backgroundTintList = ColorStateList.valueOf(colorsBg[adapterPosition])
        tvSettingLabel.setText(item.label)
        tvSettingDetails.setText(item.detail)

        val additionalViewBinding = flAdditionalView.setEndView(item.endViewType)
        clRoot.setOnClickListener { settingsViewModel.onItemClick(item) }

        item.additionalInfo?.let { additionalInfo ->
          when (item.endViewType) {
            NONE, IMAGE -> if (additionalInfo is String) {
              tvAdditionalText set additionalInfo
            } else if (additionalInfo is Int) {
              tvAdditionalText set (itemView.context?.getString(additionalInfo) ?: "")
            }
            SWITCH -> if (additionalInfo is Boolean && additionalViewBinding is ItemSettingSwitchBinding) {
              additionalViewBinding.swSettings.isChecked = additionalInfo
            }
            PROGRESS -> if (additionalInfo is Boolean && additionalViewBinding is ItemSettingProgressBinding) {
              additionalViewBinding.pbSettings.visibility = if (additionalInfo) View.VISIBLE else View.GONE
            }
          }
        }
      }
    }
  }
}

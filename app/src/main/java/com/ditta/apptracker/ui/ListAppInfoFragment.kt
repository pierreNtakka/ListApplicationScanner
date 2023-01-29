package com.ditta.apptracker.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.ditta.apptracker.R
import com.ditta.apptracker.databinding.ListAppInfoFragmentBinding
import com.ditta.apptracker.datastore.InfoStatsManager
import com.ditta.apptracker.datastore.SharedPrefRepository
import com.ditta.apptracker.ui.adapter.AppInfoAdapter
import com.ditta.apptracker.ui.adapter.ItemListener
import com.ditta.apptracker.utils.ViewUtils
import com.ditta.apptracker.viewmodel.AppInfoViewModel
import com.ditta.apptracker.viewmodel.AppInfoViewModelFactory
import com.ditta.tracker.RetrieverAndroidAppInfo

class ListAppInfoFragment : Fragment(), MenuProvider {

    private var _binding: ListAppInfoFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppInfoViewModel by viewModels {
        AppInfoViewModelFactory(
            RetrieverAndroidAppInfo(requireContext()),
            SharedPrefRepository(requireContext()),
            InfoStatsManager(requireActivity())
        )
    }

    private val itemListener: ItemListener = ItemListener { appInfoUI ->
        viewModel.onItemAppChecked(appInfoUI)
    }

    private var appInfoAdapter: AppInfoAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ListAppInfoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        appInfoAdapter = AppInfoAdapter(itemListener, requireActivity().packageManager)
        binding.recyclerViewAppInfo.adapter = appInfoAdapter
        binding.fragmentList = this@ListAppInfoFragment

        viewModel.requestPermission.observe(viewLifecycleOwner) {
            if (it) {
                ViewUtils.createDialog(
                    requireContext(),
                    R.string.attention,
                    R.string.explanation_access_to_appusage_is_not_enabled,
                    false,
                    R.string.ok
                ) {
                    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }.show()
            }
        }

        viewModel.appInfo.observe(viewLifecycleOwner) {
            (binding.recyclerViewAppInfo.adapter as AppInfoAdapter).updateAppInfos(it)
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.checkPermission()
        viewModel.updateUI()
    }

    override fun onPause() {
        super.onPause()
        viewModel.storeAppInfo()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_item, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_search -> {
                val searchView: SearchView = menuItem.actionView as SearchView

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        appInfoAdapter?.filter?.filter(query)
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        appInfoAdapter?.filter?.filter(newText)
                        return false
                    }
                })
                true
            }
            R.id.action_sync -> {
                viewModel.updateUI()
                true
            }

            R.id.action_clear_all -> {
                viewModel.clearAllSelection()
                true
            }
            else -> false
        }
    }

}
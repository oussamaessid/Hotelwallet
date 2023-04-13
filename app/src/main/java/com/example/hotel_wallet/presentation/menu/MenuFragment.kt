package com.example.hotelwallet.presentation.menu

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.hotel_wallet.databinding.FragmentMenuBinding
import com.example.hotel_wallet.domain.model.Category
import com.example.hotel_wallet.presentation.misc.BaseFragment
import com.example.hotel_wallet.utility.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : BaseFragment<FragmentMenuBinding>(
    FragmentMenuBinding::inflate
) {

    private lateinit var idcategory: String
    private val favoriteViewModel by activityViewModels<MenuViewModel>()

    private lateinit var menuListAdapter: MenuListAdapter
    private var menuList = mutableListOf<Category>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuListAdapter = MenuListAdapter(menuList) {
            val bundle = Bundle()
            bundle.putString("id_category", it.id.toString())
            Log.println(Log.ASSERT, "if_category", it.id.toString())
            bundle.putString("category_name", it.nom)
            bundle.putString("category_description", it.description)
//            findNavController().navigate(R.id.action_menuFragment_to_detailFragment, bundle)
        }

        binding.customToolbar.imgArrow.isVisible = true
        binding.customToolbar.imgArrow.setOnClickListener {
        }
        getMealInformationFromIntent()
        binding.recyclerViewCategories.setHasFixedSize(true)
        binding.recyclerViewCategories.isNestedScrollingEnabled = false
        binding.recyclerViewCategories.adapter = menuListAdapter

        favoriteViewModel.getCategories(idcategory)
        observeFavorites()

        setBottomNavigation(true)
    }

    private fun observeFavorites() {
        lifecycleScope.launchWhenStarted {
            favoriteViewModel.stateCategories.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Loading -> {
                        setLoading(true)

                    }
                    is Resource.Success -> {
                        it.data.apply {
                            menuList.clear()
                            menuList.addAll(this)
                            menuListAdapter.notifyDataSetChanged()

                        }
                        setLoading(false)
                    }
                    is Resource.Error -> {
                        setLoading(false)
                    }
                }
            }
        }
    }

    private fun getMealInformationFromIntent() {
        val args = this.arguments
        idcategory = args?.get("id_service").toString()
    }
}
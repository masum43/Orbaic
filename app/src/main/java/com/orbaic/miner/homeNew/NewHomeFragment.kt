package com.orbaic.miner.homeNew

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.orbaic.miner.R
import com.orbaic.miner.databinding.FragmentNewHomeBinding
import com.wada811.viewbinding.viewBinding

class NewHomeFragment : Fragment(R.layout.fragment_new_home) {
    private val binding by viewBinding(FragmentNewHomeBinding::bind)
    private val viewModel: NewHomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()
    }

    private fun fetchData() {
        viewModel.fetchData()
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            Log.e("user123", "onDataChange: ${user?.name}")
            binding.aciCoin.text = user?.point
        }
    }
}
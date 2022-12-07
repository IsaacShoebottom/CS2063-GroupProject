package com.example.myapplication.ui.completed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentCompletedBinding

class CompletedFragment : Fragment() {

    private var _binding: FragmentCompletedBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompletedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val completedRecycler = binding.root.findViewById<View>(R.id.compressed_recycler_view) as? RecyclerView
        completedRecycler?.adapter = MainActivity.completedAdapter
        completedRecycler?.layoutManager = LinearLayoutManager(binding.root.context)

        MainActivity.completedAdapter.refreshList(binding.root.context)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
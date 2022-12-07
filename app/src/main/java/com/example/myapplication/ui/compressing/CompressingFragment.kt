package com.example.myapplication.ui.compressing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentCompressingBinding

class CompressingFragment : Fragment() {

    private var _binding: FragmentCompressingBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompressingBinding.inflate(inflater, container, false)


        val compressingRecycler =
            binding.root.findViewById<View>(R.id.compressing_recycler_view) as? RecyclerView
        compressingRecycler?.adapter = MainActivity.compressingAdapter
        compressingRecycler?.layoutManager = LinearLayoutManager(binding.root.context)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
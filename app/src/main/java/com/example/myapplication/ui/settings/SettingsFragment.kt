package com.example.myapplication.ui.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[SettingsViewModel::class.java]

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root




        //sets the textbox visibility on startup
        binding.settingCustomSizeVideoText.visibility = View.INVISIBLE


        //updates size when size selection changes
        binding.settingRadioGroup.setOnCheckedChangeListener{ group,checkedID ->

            if(checkedID == binding.settingDefaultSizeVideo.id){
                homeViewModel.size.value = 8.0
            }else if(checkedID == binding.settingBigSizeVideo.id){
                homeViewModel.size.value = 50.0
            }else if(checkedID == binding.settingHugeSize.id){
                homeViewModel.size.value = 500.0
            }else{
                try{
                    homeViewModel.size.value = binding.settingCustomSizeVideoText.text.toString().toDouble()
                }catch (e: NumberFormatException){
                    homeViewModel.size.value = 0.0
                }
            }

            Toast.makeText(root.context,"" + homeViewModel.size.value,Toast.LENGTH_SHORT).show()

        }

        //updates size when the custom value changes
        binding.settingCustomSizeVideoText.addTextChangedListener ( object: TextWatcher {

                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {
                    try{
                        homeViewModel.size.value = s.toString().toDouble()
                    }catch (e: NumberFormatException){
                        homeViewModel.size.value = 0.0
                    }
                    Toast.makeText(root.context,"" + homeViewModel.size.value,Toast.LENGTH_SHORT).show()
                }
            }
        )

        //listens for changes in the custom radiobutton and hides or shows the textbox
        binding.settingCustomSizeVideo.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked){
                binding.settingCustomSizeVideoText.visibility = View.VISIBLE
            }else{
                binding.settingCustomSizeVideoText.visibility = View.INVISIBLE
            }
        }

//        val textView: TextView = binding.textSettings
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
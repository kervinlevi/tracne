package tech.codevil.tracne.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import tech.codevil.tracne.databinding.FragmentStatisticsBinding

/**
 * Created by kervin.decena on 10/04/2021.
 */
@AndroidEntryPoint
class StatisticsFragment: Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater)
        return binding.root
    }


}
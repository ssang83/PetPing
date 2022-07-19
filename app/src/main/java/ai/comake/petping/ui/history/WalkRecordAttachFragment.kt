package ai.comake.petping.ui.history

import ai.comake.petping.R
import ai.comake.petping.databinding.FragmentWalkRecordAttachBinding
import ai.comake.petping.util.LogUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalkRecordAttachFragment : Fragment() {
    private lateinit var binding: FragmentWalkRecordAttachBinding
    private val viewModel: WalkRecordAttachViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWalkRecordAttachBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupClickEvent()
    }

    private fun setupClickEvent() {
        binding.openHomeScreen.setOnClickListener {
            LogUtil.log("TAG", "openHomeScreen")
            activity?.findNavController(R.id.nav_main)?.navigate(
                R.id.action_walkrecordattach_to_home
            )
        }

//        activity?.onBackPressedDispatcher?.addCallback(this) {
//            LogUtil.log("TAG", "onBackPressedDispatcher")
//        }
    }
}
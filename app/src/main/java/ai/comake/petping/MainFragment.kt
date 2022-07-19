package ai.comake.petping

import ai.comake.petping.data.vo.MenuLink
import ai.comake.petping.databinding.FragmentMainBinding
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.updateDarkStatusBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.let { updateDarkStatusBar(it) }
        setUpObserver()
        viewModel.testLogin()
        checkMenuDirection()
        LogUtil.log("TAG", "onViewCreated")
    }

    private fun setUpObserver() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")
            ?.observe(viewLifecycleOwner) {
                LogUtil.log("TAG", "it $it")
            }

        viewModel.openHomeScreen.observeEvent(viewLifecycleOwner) {
            LogUtil.log("TAG", "it $it")
            findNavController().navigate(R.id.action_main_to_home)
//            setUpDeepLink()
        }
    }

    private fun checkMenuDirection() {
        LogUtil.log("TAG", "checkMenuDirection ")
        activity?.let {
            if (it.intent?.hasExtra("menu") == true) {
                LogUtil.log("TAG", "" + it.intent.getStringExtra("menu"))
                when (it.intent.getStringExtra("menu")) {
                    "walk" -> {
                        val arg = MenuLink.PetPing("walk")
                        findNavController().navigate(NavMainDirections.actionMainToHome().setMenulink(arg))
                    }
                }
            }
        }
    }

//    private fun setUpDeepLink() {
//        val arg = MeneLink.Airbridge("Airbridge")
//        findNavController().navigate(MainFragmentDirections.actionMainToHome().setDeeplink(arg))
//    }
}

package ai.comake.petping.ui.signup

import ai.comake.petping.R
import ai.comake.petping.databinding.FragmentSignupBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadTasks()
        setUpViewEvent()
    }

    private fun setUpViewEvent() {
        binding.finish.setOnClickListener {
            activity?.findNavController(R.id.nav_main)
                ?.navigate(R.id.action_signup_home)
        }
    }

    private fun setUpObserver() {
        viewModel.items.observe(
            this,
            { result ->
//                viewAdapter.replace(result)
            })
    }

}
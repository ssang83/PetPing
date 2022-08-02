package ai.comake.petping.ui.guide

import ai.comake.petping.databinding.FragmentGuideImageBinding
import ai.comake.petping.ui.base.BaseFragment
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide

/**
 * android-petping-2
 * Class: GuideImageFragment
 * Created by cliff on 2022/07/29.
 *
 * Description:
 */
class GuideImageFragment(var imgId: Int) : BaseFragment<FragmentGuideImageBinding>(FragmentGuideImageBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(requireContext())
            .load(imgId)
            .into(binding.guideImg)
    }
}
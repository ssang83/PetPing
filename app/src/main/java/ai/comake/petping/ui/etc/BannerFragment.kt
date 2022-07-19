package ai.comake.petping.ui.etc

import ai.comake.petping.R
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.ui.home.HomeFragmentDirections
import ai.comake.petping.util.setSafeOnClickListener
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide

/**
 * android-petping-2
 * Class: BannerFragment
 * Created by cliff on 2022/02/22.
 *
 * Description:
 */
class BannerFragment(private val imageResource: String, private val link: String, private val colorCode: String): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_banner, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView = view.findViewById<ImageView>(R.id.image_view)
        val cardView = view.findViewById<CardView>(R.id.fragment_root)
        Glide.with(view.context).load(imageResource).into(imageView)
        cardView.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorCode))
        cardView.setSafeOnClickListener {
            val config = WebConfig(
                url = link
            )
            requireActivity().findNavController(R.id.nav_main)
                .navigate(HomeFragmentDirections.actionHomeScreenToContentsWebFragment(config))
        }
    }
}
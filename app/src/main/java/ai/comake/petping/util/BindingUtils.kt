package ai.comake.petping.util

import ai.comake.petping.R
import ai.comake.petping.data.vo.*
import ai.comake.petping.emit
import ai.comake.petping.ui.common.itemdecoration.MyDividerItemDecoration
import ai.comake.petping.ui.common.itemdecoration.VerticalMarginItemDecoration
import ai.comake.petping.ui.common.webview.GodoMallWebView
import ai.comake.petping.ui.common.webview.reward.RewardWebViewModel
import ai.comake.petping.ui.common.widget.EventScrollView
import ai.comake.petping.ui.common.widget.IDEditText
import ai.comake.petping.ui.common.widget.IDFullEditText
import ai.comake.petping.ui.etc.EtcViewModel
import ai.comake.petping.ui.etc.PetItemAdapter
import ai.comake.petping.ui.etc.inquiry.*
import ai.comake.petping.ui.etc.member_info.change.ChangeEmailViewModel
import ai.comake.petping.ui.etc.mission_pet.InsuranceMissionPetAdapter
import ai.comake.petping.ui.etc.mission_pet.MissionPetViewModel
import ai.comake.petping.ui.etc.notice.NoticeAdapter
import ai.comake.petping.ui.etc.notice.NoticeViewModel
import ai.comake.petping.ui.godomall.FileChooserConfig
import ai.comake.petping.ui.godomall.GodoMallViewModel
import ai.comake.petping.ui.godomall.JSAlertConfig
import ai.comake.petping.ui.godomall.JSConfirmConfig
import ai.comake.petping.ui.history.walk.ImagePagerAdapter
import ai.comake.petping.ui.history.walk.WalkHistoryViewModel
import ai.comake.petping.ui.home.dashboard.DashboardViewModel
import ai.comake.petping.ui.home.dashboard.tip.TipAdapter
import ai.comake.petping.ui.home.dashboard.tip.TipAllAdapter
import ai.comake.petping.ui.home.dashboard.tip.TipAllViewModel
import ai.comake.petping.ui.home.shop.ShopAdapter
import ai.comake.petping.ui.home.shop.ShopViewModel
import ai.comake.petping.ui.insurance.InsuranceHistoryAdapter
import ai.comake.petping.ui.insurance.InsuranceHistoryViewModel
import ai.comake.petping.ui.profile.DogProfileViewModel
import ai.comake.petping.ui.profile.FamilyAdapter
import ai.comake.petping.ui.profile.SettingFamilyViewModel
import ai.comake.petping.ui.profile.SettingViewModel
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Message
import android.text.*
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.webkit.*
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.get
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout

object BindingUtils {
    @JvmStatic
    @BindingAdapter("petWalkTitle")
    fun setPetWalkTitle(textView: TextView, count: Int) {
        if (count > 0) {
            textView.text = Html.fromHtml(
                String.format(
                    textView.context.getString(R.string.dashboard_walk_info_title_walk_s),
                    count
                )
            )
        }
    }

    @JvmStatic
    @BindingAdapter("petWalkTitle")
    fun setPetWalkTitle(textView: TextView, name: String?) {
        textView.text = Html.fromHtml(
            String.format(
                textView.context.getString(R.string.dashboard_walk_info_title_not_walk_s),
                name
            )
        )
    }

    @JvmStatic
    @BindingAdapter("petWalkNotPossibleTitle")
    fun setPetWalkNotPossibleTitle(textView: TextView, name: String?) {
        textView.text = Html.fromHtml(
            String.format(
                textView.context.getString(R.string.dashboard_walk_info_title_not_possible_walk_s),
                name
            )
        )
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageUrl(imageView: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            Glide.with(imageView.context).load(url).centerCrop().into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageUrl(imageView: ImageView, url: Int?) {
        Glide.with(imageView.context).load(url).centerCrop().into(imageView)
    }

    @JvmStatic
    @BindingAdapter("topRoundImageUrl")
    fun setTopRoundImageUrl(imageView: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            Glide.with(imageView.context).load(url).into(imageView)
            roundTop(imageView, 50f) // round top 20dp
        }
    }

    @JvmStatic
    @BindingAdapter("onSafeClick")
    fun onSafeClick(view: View, listener: View.OnClickListener) {
        view.setSafeOnClickListener(listener)
    }

    @JvmStatic
    @BindingAdapter("onNormalClick")
    fun onNormalClick(view: View, listener: View.OnClickListener) {
        view.setOnClickListener(listener)
    }

    @JvmStatic
    @BindingAdapter(value = ["viewModel", "shopItem"])
    fun bindShopItem(
        recyclerView: RecyclerView,
        viewModel: ShopViewModel,
        items: List<RecommendGoods>?
    ) {
        items?.let {
            recyclerView.apply {
                adapter = ShopAdapter(viewModel).apply {
                    stateRestorationPolicy =
                        RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                    submitList(it)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("goodsPrice")
    fun setGoodsPrice(view: AppCompatTextView, item: RecommendGoods) {
        view.apply {
            when {
                item.discountRate == 0 -> {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.greyscale_9_111
                        )
                    )

                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)

                    val tf = ResourcesCompat.getFont(context, R.font.nanum_square_round_b)
                    typeface = tf
                    text = item.price.toPriceFormat()
                }
                else -> {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.color_bbbbbb
                        )
                    )
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)

                    val tf = ResourcesCompat.getFont(context, R.font.nanum_square_round)
                    typeface = tf
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    text = item.price.toPriceFormat()
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("goodsPriceUnit")
    fun setGoodsPriceDisable(view: AppCompatTextView, item: RecommendGoods) {
        view.apply {
            when {
                item.discountRate == 0 -> {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.greyscale_9_111
                        )
                    )

                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
                    text = "P"
                }
                else -> {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.color_bbbbbb
                        )
                    )

                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    text = "P"
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("dcPrice")
    fun setDiscountPrice(view: AppCompatTextView, item: RecommendGoods) {
        view.text = item.finalAmount.toPriceFormat()
    }

    @JvmStatic
    @BindingAdapter("scrollTop")
    fun setScrollTop(view: RecyclerView, status: Boolean) {
        if (status) {
            view.scrollToPosition(0)
        }
    }

    @JvmStatic
    @BindingAdapter("scrollTop")
    fun setScrollTop(view: AppBarLayout, status: Boolean) {
        if (status) {
            view.setExpanded(true)
        }
    }

    @JvmStatic
    @BindingAdapter("scrollListener")
    fun setScrollListener(view: RecyclerView, viewModel: ShopViewModel) {
        view.apply {
            layoutManager = LinearLayoutManager(view.context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    (layoutManager as LinearLayoutManager).apply {
                        val visibleCount = findFirstVisibleItemPosition()
                        when {
                            visibleCount >= 3 -> viewModel.setTopBtnStatus(true)
                            else -> viewModel.setTopBtnStatus(false)
                        }
                    }
                }
            })
        }
    }

    @JvmStatic
    @BindingAdapter("scrollListener")
    fun setScrollListener(view: RecyclerView, viewModel: NoticeViewModel) {
        view.apply {
            layoutManager = LinearLayoutManager(view.context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    (layoutManager as LinearLayoutManager).apply {
                        val visibleCount = findFirstVisibleItemPosition()
                        when {
                            visibleCount >= 3 -> viewModel.setTopBtnStatus(true)
                            else -> viewModel.setTopBtnStatus(false)
                        }
                    }
                }
            })
        }
    }

    @JvmStatic
    @BindingAdapter("webViewListener")
    fun setOnGodoMallWebViewListener(
        webView: GodoMallWebView,
        listener: GodoMallWebView.GodoMallWebViewListener
    ) {
        webView.setGodoMallWebViewListener(listener)
    }

    @JvmStatic
    @BindingAdapter("webChromeClient")
    fun setWebChromeClient(view: WebView, viewModel: GodoMallViewModel) {
        view.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                val config = JSAlertConfig(
                    message, result
                )

                viewModel.showJsAlert.emit(config)
                return true
            }

            override fun onJsConfirm(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                val config = JSConfirmConfig(
                    message, result
                )

                viewModel.showJsConfirm.emit(config)
                return true
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                val config = FileChooserConfig(
                    filePathCallback,
                    fileChooserParams
                )

                viewModel.showFileChooser.emit(config)
                return true
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                val webViewPop = WebView(view?.context!!)
                webViewPop.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url.toString()
                        if (url.contains("https://")) {
                            view?.context?.startActivity(Intent(Intent.ACTION_VIEW).apply {
                                setData(Uri.parse(url))
                            })
                            return true
                        } else {
                            return false
                        }
                    }
                }

                (resultMsg?.obj as WebView.WebViewTransport).apply { webView = webViewPop }
                resultMsg.sendToTarget()
                return true
            }
        }
    }

    @JvmStatic
    @BindingAdapter("animation")
    fun setAnimation(view: AppCompatImageView, isFirst: Boolean) {
        if (isFirst.not()) {
            val anim = AnimationUtils.loadAnimation(view.context, R.anim.rotation)
            view.startAnimation(anim)
        }
    }

    @JvmStatic
    @BindingAdapter("autoAnimation")
    fun updateAnimation(view: ImageView, visible: Boolean) {
        if (visible) {
            (view.drawable as AnimationDrawable).start()
        }
    }

    @JvmStatic
    @BindingAdapter("bringToFront")
    fun bringToFront(view: View, visible: Boolean) {
        if (visible) {
            view.z = 999f
        }
    }

    @JvmStatic
    @BindingAdapter("uploadMissionButtonText")
    fun setUploadMissionButtonText(button: Button, viewModel: RewardWebViewModel) {
        button.apply {
            when {
                viewModel.missionState.value == 1 -> text =
                    button.context.getString(R.string.upload_file)
                else -> text = button.context.getString(R.string.re_upload_file)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("htmlText")
    fun setHtmlText(view: TextView, text: String) {
        view.text = Html.fromHtml(text)
    }

    @JvmStatic
    @BindingAdapter("circleImageUrl")
    fun setCircleImageUrl(imageView: ImageView, url: String?) {
        val context = imageView.context
        Glide.with(context)
            .load(url)
            .apply(RequestOptions.circleCropTransform())
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("fitImageUrl")
    fun fitImageUrl(imageView: ImageView, url: String?) {
        val context = imageView.context
        Glide.with(context).load(url).override(
            context.resources.displayMetrics.widthPixels,
            context.resources.displayMetrics.heightPixels
        ).optionalFitCenter().into(imageView)
    }

    @JvmStatic
    @BindingAdapter("dateTextMissionPet")
    fun setInsuranceDateText(view: TextView, item: PetInsuranceMissionPet) {
        view.text = "${item.insuStartDate} ~ ${item.insuEndDate}"
    }

    @JvmStatic
    @BindingAdapter("enabledText")
    fun btnEnabledText(view: AppCompatTextView, enabled: Boolean) {
        view.apply {
            isEnabled = enabled
            when (enabled) {
                true -> view.setTextColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.greyscale_9_111
                    )
                )
                else -> view.setTextColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.color_bbbbbb
                    )
                )
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["viewModel", "insuranceMissionPet"])
    fun bindInsuranceMissionPet(
        recyclerView: RecyclerView,
        viewModel: MissionPetViewModel,
        items: List<PetInsuranceMissionPet>?
    ) {
        items?.let {
            recyclerView.apply {
                adapter = InsuranceMissionPetAdapter(viewModel).apply { setItems(it) }
            }
            recyclerView.addItemDecoration(
                VerticalMarginItemDecoration(
                    marginBottom = recyclerView.context.resources.getDimensionPixelOffset(R.dimen.margin_30dp)
                )
            )
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["viewModel", "myPets"])
    fun bindMyPets(
        recyclerView: RecyclerView,
        viewModel: EtcViewModel,
        items: List<MyPet>?
    ) {
        items?.let {
            LogUtil.log("")
            recyclerView.apply {
                itemAnimator = DefaultItemAnimator()
                adapter = PetItemAdapter(viewModel).apply { setItems(it) }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("eventScrollListener")
    fun setEventScrollListener(view: EventScrollView, viewModel: EtcViewModel) {
        view.apply {
            setOnScrollListener(object : EventScrollView.OnScrollListener {
                override fun onScrollChanged(l: Int, t: Int, oldX: Int, oldY: Int) {
                    if (viewModel.isVisibleScrollView.value == true) {
                        viewModel.isVisibleScrollView.value = false
                    }
                }

                override fun onScrollEnd() {
                    if (viewModel.isVisibleScrollView.value == false) {
                        viewModel.isVisibleScrollView.value = true
                    }
                }
            })
        }
    }

    @JvmStatic
    @BindingAdapter("viewPagerAnimate")
    fun setViewPagerAnimate(view: ViewPager2, viewModel: EtcViewModel) {
        view.apply {
            when {
                viewModel.isVisibleScrollView.value == true -> {
                    animate().alpha(1f).duration = 1000
                }
                else -> {
                    animate().alpha(0f).duration = 0
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["viewModel", "noticeItem"])
    fun bindNoticeItem(
        recyclerView: RecyclerView,
        viewModel: NoticeViewModel,
        items: List<NoticeResponseData>?
    ) {
        items?.let {
            recyclerView.apply {
                itemAnimator = DefaultItemAnimator()
                addItemDecoration(
                    MyDividerItemDecoration(
                        recyclerView.context,
                        R.drawable.notice_divider
                    )
                )
                adapter = NoticeAdapter(viewModel).apply { submitList(it) }
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["viewModel", "faqItem"])
    fun bindfaqItem(
        recyclerView: RecyclerView,
        viewModel: FAQViewModel,
        items: List<NoticeResponseData>?
    ) {
        items?.let {
            recyclerView.apply {
                itemAnimator = DefaultItemAnimator()
                addItemDecoration(
                    MyDividerItemDecoration(
                        recyclerView.context,
                        R.drawable.notice_divider
                    )
                )
                adapter = FAQAdapter(viewModel).apply { submitList(it) }
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["viewModel", "inquiryItem"])
    fun bindInquiryItem(
        recyclerView: RecyclerView,
        viewModel: InquiryDoViewModel,
        items: List<InquiryData>?
    ) {
        items?.let {
            recyclerView.apply {
                itemAnimator = DefaultItemAnimator()
                addItemDecoration(
                    MyDividerItemDecoration(
                        recyclerView.context,
                        R.drawable.notice_divider
                    )
                )
                adapter = InquiryDoAdapter(viewModel).apply { submitList(it) }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("answerText")
    fun setAnswerText(view: TextView, status: Boolean) {
        view.apply {
            when (status) {
                true -> {
                    text = "답변 완료"
                    setTextColor(view.context.getColor(R.color.primary_pink))
                }

                else -> {
                    text = "답변 대기"
                    setTextColor(view.context.getColor(R.color.greyscale_9_111))
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("inquirtyTitleText")
    fun setTitleText(view: TextView, item: InquiryData) {
        view.text = "${item.categoryStr} ${item.title}"
    }

    @JvmStatic
    @BindingAdapter("directChecked")
    fun directChecked(compoundButton: CompoundButton, isChecked: Boolean) {
        compoundButton.isChecked = isChecked
        compoundButton.jumpDrawablesToCurrentState()
    }

    @JvmStatic
    @BindingAdapter("boldText1")
    fun setBoldText1(view: AppCompatTextView, title: String) {
        val ssb = SpannableStringBuilder(title).apply {
            setSpan(StyleSpan(Typeface.BOLD), 14, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        view.setText(ssb)
    }

    @JvmStatic
    @BindingAdapter("boldText2")
    fun setBoldText2(view: AppCompatTextView, title: String) {
        val ssb = SpannableStringBuilder(title).apply {
            setSpan(StyleSpan(Typeface.BOLD), 14, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        view.setText(ssb)
    }

    @JvmStatic
    @BindingAdapter("boldText3")
    fun setBoldText(view: AppCompatTextView, title: String) {
        val ssb = SpannableStringBuilder(title).apply {
            setSpan(StyleSpan(Typeface.BOLD), 12, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val ssb1 = SpannableStringBuilder(ssb).apply {
            setSpan(StyleSpan(Typeface.BOLD), 16, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        view.setText(ssb1)
    }

    @JvmStatic
    @BindingAdapter("tabFont")
    fun setTabLayoutFont(view: TabLayout, context: Context?) {
        val vg = view.getChildAt(0) as ViewGroup
        val tabCnt = vg.childCount

        for (i in 0 until tabCnt) {
            val vgTab = vg.getChildAt(i) as ViewGroup
            val tabChildCnt = vgTab.childCount
            for (j in 0 until tabChildCnt) {
                val tabViewChild = vgTab.getChildAt(j)
                if (tabViewChild is AppCompatTextView) {
                    context?.let {
                        tabViewChild.typeface =
                            Typeface.createFromAsset(it.assets, "nanum_square_round_b.ttf")
                    }
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("lenghText")
    fun setLengthText(view: AppCompatTextView, item: WalkRecord) {
        view.text = "/${item.walkImagesURL.size}"
    }

    @JvmStatic
    @BindingAdapter("layoutParam")
    fun setLayoutParam(layout: ConstraintLayout, viewModel: WalkHistoryViewModel) {
        val layoutParams = layout.layoutParams
        layoutParams.height = layout.resources.displayMetrics.widthPixels
        layout.layoutParams = layoutParams
        layout.requestLayout()
    }

    @JvmStatic
    @BindingAdapter("onPageChangeCallback")
    fun setOnPageChangeCallback(view: ViewPager2, viewModel: WalkHistoryViewModel) {
        view.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.selectPage.value = (position + 1).toString()
            }
        })
    }

    @JvmStatic
    @BindingAdapter(value = ["viewModel", "pagerAdapter"])
    fun setImageAdapter(view: ViewPager2, viewModel: WalkHistoryViewModel, item: WalkRecord) {
        view.apply {
            adapter = ImagePagerAdapter(viewModel, item.walkImagesURL)
        }
    }

    @JvmStatic
    @BindingAdapter("withParent")
    fun setWithParents(view: AppCompatTextView, item: WalkRecord) {
        view.apply {
            when {
                item.isMemberName == false -> {
                    text = item.memberName
                    setTextColor(
                        ContextCompat.getColor(
                            this.context,
                            R.color.greyscale_9_aaa
                        )
                    )
                }
                else -> {
                    val str = "${item.memberName} 님"
                    val ssb = SpannableStringBuilder(str).apply {
                        setSpan(
                            StyleSpan(Typeface.BOLD),
                            0,
                            item.memberName.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    text = ssb
                    setTextColor(
                        ContextCompat.getColor(
                            this.context,
                            R.color.greyscale_9_666
                        )
                    )
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("withPets")
    fun setWithPets(view: AppCompatTextView, item: WalkRecord) {
        view.apply {
            item.withPets.forEachIndexed { index, pet ->
                append(pet)
                if (index != item.withPets.size - 1) {
                    append(", ")
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("tabSelectedListener")
    fun setTabSelectedListener(tabLayout: TabLayout, viewModel: DogProfileViewModel) {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewModel.tabSelected.emit(it.position)
                    val tabViewGroup = (tabLayout.get(0) as ViewGroup)[it.position] as ViewGroup
                    tabViewGroup.let { view ->
                        view.children.forEach { childView ->
                            if (childView is TextView) {
                                val typeface = Typeface.createFromAsset(
                                    tabLayout.context.resources.assets,
                                    "nanum_square_round_b.ttf"
                                )
                                childView.typeface = typeface
                            }
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    val tabViewGroup =
                        (tabLayout.get(0) as ViewGroup)[it.position ?: 0] as ViewGroup
                    tabViewGroup.let { view ->
                        view.children.forEach { childView ->
                            if (childView is TextView) {
                                val typeface = Typeface.createFromAsset(
                                    tabLayout.context.resources.assets,
                                    "nanum_square_round.ttf"
                                )
                                childView.typeface = typeface
                            }
                        }
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    @JvmStatic
    @BindingAdapter("tabSelectedListener")
    fun setTabSelectedListener(tabLayout: TabLayout, viewModel: QuestionViewModel) {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewModel.tabSelected.emit(it.position)
                    val tabViewGroup = (tabLayout.get(0) as ViewGroup)[it.position] as ViewGroup
                    tabViewGroup.let { view ->
                        view.children.forEach { childView ->
                            if (childView is TextView) {
                                val typeface = Typeface.createFromAsset(
                                    tabLayout.context.resources.assets,
                                    "nanum_square_round_b.ttf"
                                )
                                childView.typeface = typeface
                            }
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    val tabViewGroup =
                        (tabLayout.get(0) as ViewGroup)[it.position ?: 0] as ViewGroup
                    tabViewGroup.let { view ->
                        view.children.forEach { childView ->
                            if (childView is TextView) {
                                val typeface = Typeface.createFromAsset(
                                    tabLayout.context.resources.assets,
                                    "nanum_square_round.ttf"
                                )
                                childView.typeface = typeface
                            }
                        }
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    @JvmStatic
    @BindingAdapter("tabSelectedListener")
    fun setTabSelectedListener(tabLayout: TabLayout, viewModel: SettingViewModel) {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewModel.tabPosition.emit(it.position)
                    val tabViewGroup = (tabLayout.get(0) as ViewGroup)[it.position] as ViewGroup
                    tabViewGroup.let { view ->
                        view.children.forEach { childView ->
                            if (childView is TextView) {
                                val typeface = Typeface.createFromAsset(
                                    tabLayout.context.resources.assets,
                                    "nanum_square_round_b.ttf"
                                )
                                childView.typeface = typeface
                            }
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    val tabViewGroup =
                        (tabLayout.get(0) as ViewGroup)[it.position ?: 0] as ViewGroup
                    tabViewGroup.let { view ->
                        view.children.forEach { childView ->
                            if (childView is TextView) {
                                val typeface = Typeface.createFromAsset(
                                    tabLayout.context.resources.assets,
                                    "nanum_square_round.ttf"
                                )
                                childView.typeface = typeface
                            }
                        }
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    @JvmStatic
    @BindingAdapter("tabSelectedListener")
    fun setTabSelectedListener(tabLayout: TabLayout, listener: TabLayout.OnTabSelectedListener) {
        tabLayout.addOnTabSelectedListener(listener)
    }

    @JvmStatic
    @BindingAdapter("focusChangeListener")
    fun setOnFocusChangeListener(editText: EditText, listener: View.OnFocusChangeListener) {
        editText.setOnFocusChangeListener(listener)
    }

    @JvmStatic
    @BindingAdapter("addTextChangedListener")
    fun addTextChangedListener(editText: EditText, watcher: TextWatcher) {
        editText.addTextChangedListener(watcher)
    }

    @JvmStatic
    @BindingAdapter("touchListener")
    fun setOnTouchListener(view: View, listener: View.OnTouchListener) {
        view.setOnTouchListener(listener)
    }

    @JvmStatic
    @BindingAdapter("errorText")
    fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
        view.error = errorMessage
    }

    @JvmStatic
    @BindingAdapter(value = ["textInputLayout", "viewModel"])
    fun setOnFocusChangeListener(
        editText: EditText,
        layout: TextInputLayout,
        viewModel: ChangeEmailViewModel
    ) {
        editText.setOnFocusChangeListener { v, hasFocus ->
            var hintInfo = viewModel.focusHint
            if (hasFocus.not()) {
                layout.endIconMode = TextInputLayout.END_ICON_NONE
                var str = editText.text
                if (str.isEmpty()) {
                    layout.isErrorEnabled = false
                    hintInfo = viewModel.hint.toString()
                    setTextInputLayoutHintColor(layout, editText.context, R.color.greyscale_4_ddd)
                } else {
                    setTextInputLayoutHintColor(layout, editText.context, R.color.greyscale_9_aaa)
                }

                if (layout.isErrorEnabled.not()) layout.helperText = ""
            } else {
                layout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                setTextInputLayoutHintColor(layout, editText.context, R.color.greyscale_9_aaa)
                if (!layout.isErrorEnabled) layout.helperText = viewModel.helperText
            }

            layout.hint = hintInfo
        }
    }

    @JvmStatic
    @BindingAdapter("passwdErrorTextColor")
    fun setErrorTextColor(layout: TextInputLayout, color: String?) {
        color?.let {
            layout.apply {
                when (it) {
                    "grey" -> setErrorTextColor(
                        layout.context.resources.getColorStateList(
                            R.color.greyscale_9_aaa,
                            layout.context.theme
                        )
                    )
                    "pink" -> setErrorTextColor(
                        layout.context.resources.getColorStateList(
                            R.color.primary_pink,
                            layout.context.theme
                        )
                    )
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("defaultHintTextColor")
    fun setDefaultHintTextColor(layout: TextInputLayout, color: String?) {
        color?.let {
            layout.apply {
                when (it) {
                    "grey_aaa" -> {
                        defaultHintTextColor = layout.context.resources.getColorStateList(
                            R.color.greyscale_9_aaa,
                            layout.context.theme
                        )
                    }
                    "grey_ddd" -> {
                        defaultHintTextColor = layout.context.resources.getColorStateList(
                            R.color.greyscale_4_ddd,
                            layout.context.theme
                        )
                    }
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("locationWalkEndTime")
    fun setTime(view: AppCompatTextView, time: String) {
        view.text = "${time} (종료시간)"
    }

    @JvmStatic
    @BindingAdapter("boldText4")
    fun setBoldText4(view: AppCompatTextView, title: String) {
        val str = "${title} 님이 확인했어요"
        val ssb = SpannableStringBuilder(str).apply {
            setSpan(StyleSpan(Typeface.BOLD), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        view.setText(ssb)
    }

    @JvmStatic
    @BindingAdapter("text")
    fun setTextWatcher(idView: IDEditText, id: MutableLiveData<String>) {
        val firstId = idView.findViewById<EditText>(R.id.id_first_tv)
        val secondId = idView.findViewById<EditText>(R.id.id_second_tv)

        firstId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                id.value = idView.getIdInfo()
            }

        })

        secondId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                id.value = idView.getIdInfo()
            }
        })
    }

    @JvmStatic
    @BindingAdapter("imageSelector")
    fun setImageSelector(view: ImageView, status: Boolean) {
        view.apply {
            if (status) {
                isSelected = true
            } else {
                isSelected = false
            }
        }
    }

    @JvmStatic
    @BindingAdapter("dateText")
    fun setInsuranceDateText(view: TextView, item: PetInsurJoinsData) {
        view.text = "${item.insuStartDate} ~ ${item.insuEndDate}"
    }

    @JvmStatic
    @BindingAdapter("insuranceState")
    fun setInsuranceState(view: TextView, state: Int) {
        view.apply {
            when (state) {
                2 -> {
                    visibility = View.VISIBLE
                    text = "가입 진행 중"
                    setTextColor(ContextCompat.getColor(view.context, R.color.greyscale_9_777))
                }
                3 -> {
                    visibility = View.VISIBLE
                    text = "인수 심사 중"
                    setTextColor(ContextCompat.getColor(view.context, R.color.primary_violet))
                }
                4 -> {
                    visibility = View.VISIBLE
                    text = "인수 심사 거절"
                    setTextColor(ContextCompat.getColor(view.context, R.color.colorPrimary))
                }
                else -> visibility = View.GONE
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["viewModel", "insurnaceItem"])
    fun bindInsuranceHistoryItem(
        recyclerView: RecyclerView,
        viewModel: InsuranceHistoryViewModel,
        items: List<PetInsurJoinsData>?
    ) {
        items?.let {
            recyclerView.apply {
                itemAnimator = DefaultItemAnimator()
                adapter = InsuranceHistoryAdapter(viewModel).apply { setItems(it) }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("text")
    fun setTextWatcher(idView: IDFullEditText, id: MutableLiveData<String>) {
        val firstId = idView.findViewById<EditText>(R.id.id_first_tv)
        val secondId = idView.findViewById<EditText>(R.id.id_second_tv)

        firstId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                id.value = idView.getIdInfo()
            }

        })

        secondId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                id.value = idView.getIdInfo()
            }
        })
    }

    @JvmStatic
    @BindingAdapter("idFullBirth")
    fun setIDFullBirth(view: IDFullEditText, birth: String) {
        view.setBirth(birth)
    }

    @JvmStatic
    @BindingAdapter("familyName")
    fun setFamilyName(view: TextView, item: MemberInfoFamilyReg) {
        view.apply {
            text = item.memberName
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
        }
    }

    @JvmStatic
    @BindingAdapter("familyEmail")
    fun setFamilyEmail(view: TextView, item: MemberInfoFamilyReg) {
        view.apply {
            text = item.email

            if (item.memberName.isNullOrEmpty()) {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
                setTextColor(context.getColor(R.color.greyscale_9_111))
            } else {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
                setTextColor(context.getColor(R.color.greyscale_9_777))
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["viewModel", "familyItems"])
    fun bindFamilyItem(
        recyclerView: RecyclerView,
        viewModel: SettingFamilyViewModel,
        items: List<MemberInfoFamilyReg>?
    ) {
        items?.let {
            recyclerView.apply {
                itemAnimator = DefaultItemAnimator()
                addItemDecoration(
                    MyDividerItemDecoration(
                        recyclerView.context,
                        R.drawable.notice_divider
                    )
                )
                adapter = FamilyAdapter(viewModel).apply { setItems(it) }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("scrollChangeListener")
    fun setOnScrollChangeListener(view: View, listener: View.OnScrollChangeListener) {
        view.setOnScrollChangeListener(listener)
    }

    @JvmStatic
    @BindingAdapter(value = ["viewModel", "tipItem"])
    fun bindPingTipItem(
        recyclerView: RecyclerView,
        viewModel: DashboardViewModel,
        items: List<Tip>?
    ) {
        items?.let {
            recyclerView.apply {
                itemAnimator = DefaultItemAnimator()
                layoutManager = LinearLayoutManager(recyclerView.context).apply {
                    orientation = LinearLayoutManager.HORIZONTAL
                }

                adapter = TipAdapter(recyclerView.context, viewModel).apply {
                    stateRestorationPolicy =
                        RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                    submitList(it)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["viewModel", "tipAllItem"])
    fun bindPingTipAllItem(
        recyclerView: RecyclerView,
        viewModel: TipAllViewModel,
        items: List<Tip>?
    ) {
        items?.let {
            recyclerView.apply {
                itemAnimator = DefaultItemAnimator()
                adapter = TipAllAdapter(viewModel).apply { submitList(it) }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("addOnScrollListener")
    fun setOnScrollListener(view: RecyclerView, listener: RecyclerView.OnScrollListener) {
        view.addOnScrollListener(listener)
    }

    @JvmStatic
    @BindingAdapter("tagText")
    fun setTagText(view: TextView, tag: String) {
        view.text = "#${tag}"
    }

    @JvmStatic
    @BindingAdapter("boldDescText")
    fun setBoldDescText(view: TextView, desc: String) {
        val ssb = SpannableStringBuilder(desc).apply {
            setSpan(StyleSpan(Typeface.BOLD), 4, desc.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        view.setText(ssb)
    }

    @JvmStatic
    @BindingAdapter("boldDescText1")
    fun setBoldDescText1(view: TextView, desc: String) {
        val ssb = SpannableStringBuilder(desc).apply {
            setSpan(
                StyleSpan(Typeface.BOLD),
                7,
                desc.length - 3,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        view.setText(ssb)
    }

    @JvmStatic
    @BindingAdapter("boldDescText2")
    fun setBoldDescText2(view: TextView, desc: String) {
        val ssb = SpannableStringBuilder(desc).apply {
            setSpan(StyleSpan(Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val ssb1 = SpannableStringBuilder(ssb).apply {
            setSpan(StyleSpan(Typeface.BOLD), 5, desc.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        view.setText(ssb1)
    }

    @JvmStatic
    @BindingAdapter("boldDescText3")
    fun setBoldDescText3(view: TextView, desc: String) {
        val ssb = SpannableStringBuilder(desc).apply {
            setSpan(StyleSpan(Typeface.BOLD), 9, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        view.setText(ssb)
    }
}

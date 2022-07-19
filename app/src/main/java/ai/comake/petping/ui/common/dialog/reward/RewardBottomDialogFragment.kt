package ai.comake.petping.ui.common.dialog.reward

import ai.comake.petping.R
import ai.comake.petping.databinding.BottomDialogMissionUploadBinding
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.recyclerViewSetup
import ai.comake.petping.util.setSafeOnClickListener
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * android-petping-2
 * Class: RewardBottomDialogFragment
 * Created by cliff on 2022/02/15.
 *
 * Description:
 */
class RewardBottomDialogFragment(
    val text: String,
    val uploadFile: (fileList: List<String>) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding:BottomDialogMissionUploadBinding

    private var imageList: ArrayList<String> = arrayListOf()
    private lateinit var adapter: RewardImageAdapter


    /**
     * 이미지 요청 후 결과 처리
     */
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult() // ◀ StartActivityForResult 처리를 담당
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val data = activityResult.data
            imageList.add(data?.data.toString())
            adapter.mItems = imageList
            adapter.notifyDataSetChanged()

            if (imageList.size > 0) {
                binding.btn.isEnabled = true
                binding.imageRecyclerView.visibility = View.VISIBLE
            }

            updateImage()
        }
    }

    /**
     * 퍼미션 처리
     */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        when {
            isGranted -> imagePick()
            else -> permissionErrorPopup()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomDialogMissionUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            addImage.setSafeOnClickListener { imagePick() }
            btn.setSafeOnClickListener {
                uploadFile.invoke(imageList)
                dismiss()
            }

            adapter = RewardImageAdapter { position ->
                imageList.removeAt(position)
                adapter.mItems = imageList
                adapter.notifyDataSetChanged()
                if (imageList.size == 0) {
                    btn.isEnabled = false
                    imageRecyclerView.visibility = View.GONE
                }

                updateImage()
            }

            recyclerViewSetup(
                requireContext(),
                imageRecyclerView,
                adapter,
                RecyclerView.HORIZONTAL
            )

            imageRecyclerView.visibility = View.GONE
            btn.text = text
            btn.isEnabled = false
        }
    }

    fun updateImage() {
        if (imageList.size < 3) {
            binding.addImage.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.greyscale_9_111
                )
            )
            binding.addImage.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.btn_outline_666)
        } else {
            binding.addImage.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.greyscale_9_aaa
                )
            )
            binding.addImage.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.btn_outline_ddd)
        }
    }

    private fun storagePermissionLaunch() {
        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun permissionErrorPopup() {
        SingleBtnDialog(
            requireContext(),
            getString(R.string.permission_error_title),
            getString(R.string.permission_error_desc))
        {}.show()
    }

    private fun imagePick() {
        if (imageList.size < 3) {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            pickImage.launch(intent)
        }
    }
}
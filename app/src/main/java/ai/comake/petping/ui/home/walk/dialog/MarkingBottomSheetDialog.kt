package ai.comake.petping.ui.home.walk.dialog

import ai.comake.petping.R
import ai.comake.petping.data.vo.WalkablePet
import ai.comake.petping.databinding.BottomSheetDialogMarkingBinding
import ai.comake.petping.ui.home.walk.adapter.MarkingPetAdapter
import ai.comake.petping.util.recyclerViewSetup
import ai.comake.petping.util.setSafeOnClickListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MarkingBottomSheetDialog(
    val walkablePetList: List<WalkablePet.Pets>,
    val onMarkingRegisterClick: (Int, Int) -> Unit
) : BottomSheetDialogFragment(
) {
    private lateinit var binding: BottomSheetDialogMarkingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogMarkingBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var petId: Int = try {
        walkablePetList[0].id
    } catch (e: Exception) {
        0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<FloatingActionButton>(R.id.marking).setSafeOnClickListener { _ ->
            onMarkingRegisterClick(petId, 1)
            dismiss()
        }

        view.findViewById<FloatingActionButton>(R.id.no1).setSafeOnClickListener { _ ->
            onMarkingRegisterClick(petId, 2)
            dismiss()
        }

        view.findViewById<FloatingActionButton>(R.id.no2).setSafeOnClickListener { _ ->
            onMarkingRegisterClick(petId, 3)
            dismiss()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        if(walkablePetList.size > 1) {
            recyclerViewSetup(requireContext(), recyclerView, MarkingPetAdapter(walkablePetList) { index ->
                petId = index
            }, RecyclerView.HORIZONTAL)
        } else {
            recyclerView.visibility = View.GONE
        }
    }
}
package br.com.ceccon.andre.ui.articledetails.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import br.com.ceccon.andre.databinding.BottomSheetOptionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OptionsBottomSheetFragment(
    val onClickSave: () -> Unit,
    val onClickImage: () -> Unit,
    val onClickArticle: () -> Unit,
    fragmentManager: FragmentManager
) : BottomSheetDialogFragment() {

    init {
        show(fragmentManager, tag)
    }

    private val binding get() = _binding!!
    private var _binding: BottomSheetOptionsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetOptionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        binding.tvShareArticle.setOnClickListener {
            dismissAllowingStateLoss()
            onClickArticle.invoke()
        }

        binding.tvSharePhoto.setOnClickListener {
            dismissAllowingStateLoss()
            onClickImage.invoke()
        }

        binding.tvSavePhoto.setOnClickListener {
            dismissAllowingStateLoss()
            onClickSave.invoke()
        }
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }
}
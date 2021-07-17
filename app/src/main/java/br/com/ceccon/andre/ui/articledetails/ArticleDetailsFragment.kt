package br.com.ceccon.andre.ui.articledetails

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.ceccon.andre.R
import br.com.ceccon.andre.databinding.ArticleDetailsFragmentBinding
import br.com.ceccon.andre.ui.articledetails.bottomsheet.OptionsBottomSheetFragment
import br.com.ceccon.andre.utils.*
import br.com.ceccon.andre.utils.NavigationExtension.navigateWithAnimations
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transform.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.util.*

class ArticleDetailsFragment : Fragment() {

    private val binding get() = _binding!!
    private var _binding: ArticleDetailsFragmentBinding? = null

    private val args: ArticleDetailsFragmentArgs by lazy {
        ArticleDetailsFragmentArgs.fromBundle(requireArguments())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ArticleDetailsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.btnOpenArticle.setOnClickListener {
            args.articleItem.url?.let { url ->
                ArticleDetailsFragmentDirections.actionArticleDetailsFragmentToWebViewFragment(
                    url = url
                ).also { action -> findNavController().navigateWithAnimations(action) }
            } ?: Toast.makeText(requireContext(), "Sem Url", Toast.LENGTH_LONG).show()
        }

        binding.btnshare.setOnClickListener {
            OptionsBottomSheetFragment(
                onClickSave = {
                    requireActivity().verifyStoragePermission(
                        onPermissionsGranted = {
                            downloadAndSaveImage(false)
                        }
                    )
                },
                onClickImage = {
                    requireActivity().verifyStoragePermission(
                        onPermissionsGranted = { downloadAndSaveImage(true) }
                    )
                },
                onClickArticle = { shareArticle() },
                fragmentManager = childFragmentManager
            )
        }
    }

    private fun bind() {
        with(binding) {
            with(args.articleItem) {
                tvTitle.text = title.getText()
                tvAuthor.text = author.getText()
                tvContent.text = content.getText()
                tvSource.text = source.name.getText()
                tvDescription.text = description.getText()
                tvPublishedAt.text = publishedAt.getText().toDate(
                    formatoDesejado = "dd/MM/yyyy HH:mm:ss"
                )

                val urlImagem = urlToImage.toString()

                if (urlImagem.isNotEmpty() && urlImagem.isNotBlank()) {
                    ivWine.load(urlImagem) {
                        crossfade(true)
                        error(R.drawable.newspaper)
                        placeholder(R.drawable.newspaper)
                        transformations(RoundedCornersTransformation())
                    }
                }

                previousView()
            }
        }
    }

    private fun shareArticle() {
        args.articleItem.url?.let { url ->
            try {
                nextView()
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, url)
                    type = "text/plain"
                }

                previousView()
                startActivity(
                    Intent.createChooser(
                        sendIntent,
                        resources.getText(R.string.app_name)
                    )
                )
            } catch (e: Exception) {
                errorShare("Erro ao compartilhar!")
            }
        } ?: errorShare("Sem Url para compartilhar!")
    }

    private fun shareImage(
        uriToImage: Uri
    ) {
        args.articleItem.urlToImage?.let { _ ->
            try {
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uriToImage)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    type = "image/jpeg"
                }

                nextView()
                startActivity(
                    Intent.createChooser(
                        shareIntent,
                        resources.getText(R.string.app_name)
                    )
                )
            } catch (e: Exception) {
                errorShare("Erro ao compartilhar!")
            }
        } ?: errorShare("Sem Url para compartilhar!")
    }

    private fun downloadAndSaveImage(share: Boolean) {
        args.articleItem.urlToImage?.let { urlToImage ->
            lifecycleScope.launch {
                previousView()
                val loader = ImageLoader(requireContext())
                val request = ImageRequest.Builder(requireContext())
                    .data(urlToImage)
                    .allowHardware(false)
                    .build()

                val result = (loader.execute(request) as SuccessResult).drawable
                val bitmap = (result as BitmapDrawable).bitmap

                val fileName = Calendar.getInstance().time.formatTo()

                try {
                    val resultSave = requireActivity().savePhotoToExternalStorage(
                        displayName = fileName, bmp = bitmap
                    )

                    withContext(Dispatchers.Main) {
                        if (resultSave.first && share) {
                            resultSave.second?.let { uri -> shareImage(uriToImage = uri) }
                        } else {
                            nextView()
                            Toast.makeText(
                                requireContext(),
                                "Foto salva com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: IOException) {
                    Timber.e("Erro: %s", e.message)
                }
            }
        }
    }

    private fun errorShare(message: String) {
        nextView()
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun previousView() {
        with(binding.viewFlipper) {
            setInAnimation(requireContext(), R.anim.slide_in_left)
            setOutAnimation(requireContext(), R.anim.slide_out_right)
            showPrevious();
        }
    }

    private fun nextView() {
        with(binding.viewFlipper) {
            setInAnimation(requireContext(), R.anim.slide_in_left)
            setOutAnimation(requireContext(), R.anim.slide_out_right)
            showNext()
        }
    }
}
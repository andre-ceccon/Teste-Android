package br.com.ceccon.andre.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.ceccon.andre.R
import br.com.ceccon.andre.data.source.local.NewsLocalDataSourceImpl
import br.com.ceccon.andre.data.source.local.database.NewsDatabase
import br.com.ceccon.andre.data.source.remote.NewsRemoteDataSourceImpl
import br.com.ceccon.andre.data.source.remote.RetrofitInstance
import br.com.ceccon.andre.data.source.repository.NewsRepositoryImpl
import br.com.ceccon.andre.databinding.ListNewsFragmentBinding
import br.com.ceccon.andre.utils.ConnectionLiveData
import br.com.ceccon.andre.utils.NavigationExtension.navigateWithAnimations
import kotlinx.coroutines.Dispatchers

class ListNewsFragment : Fragment() {

    private val binding get() = _binding!!
    private var _binding: ListNewsFragmentBinding? = null

    private var isNetworkAvailable: Boolean = false
    private lateinit var viewModel: ListNewsViewModel

    private val newsAdapter: ListNewsAdapter by lazy {
        ListNewsAdapter(onClickArticle = { article ->
            ListNewsFragmentDirections.actionListNewsFragmentToArticleDetailsFragment(
                article
            ).also { action -> findNavController().navigateWithAnimations(action) }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListNewsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ConnectionLiveData(requireActivity()).observe(viewLifecycleOwner) { available ->
            isNetworkAvailable = available
            logicRetriveData()
        }

        val result = createViewModel()

        if (!result) return

        observeViewModels()
        initReCyclerAndSwipe()
    }

    private fun createViewModel(): Boolean {
        val defaultDispatcher = Dispatchers.IO

        val newsLocal = NewsLocalDataSourceImpl(
            ioDispatcher = defaultDispatcher,
            newsDao = NewsDatabase.getInstance(requireContext()).newsDao(),
        )

        RetrofitInstance.api?.let { api ->
            val newsRemote = NewsRemoteDataSourceImpl(
                ioDispatcher = defaultDispatcher, apiService = api
            )

            val repository = NewsRepositoryImpl(
                localDataSource = newsLocal,
                remoteDataSource = newsRemote,
                ioDispatcher = defaultDispatcher,
            )

            viewModel = ViewModelProvider(
                viewModelStore, ListNewsViewModel.ViewModelFactory(repository)
            ).get(ListNewsViewModel::class.java)
            return true
        } ?: return false
    }

    private fun initReCyclerAndSwipe() {
        binding.rvNews.apply {
            setHasFixedSize(true)
            adapter = newsAdapter
        }

        binding.swipeRefreshId.setOnRefreshListener {
            hideViews()
            logicRetriveData()
            binding.swipeRefreshId.isRefreshing = false
        }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.apply {
                pbNews.visibility = View.VISIBLE
                loadingText.visibility = View.VISIBLE
            }

            if (!isNetworkAvailable) {
                hideViews()
                logicRetriveData()
            } else {
                binding.apply {
                    pbNews.visibility = View.GONE
                    loadingText.visibility = View.GONE
                }
            }
        }, 1000)
    }

    private fun observeViewModels() {
        lifecycleScope.launchWhenCreated {
            with(viewModel) {
                isLoading.observe(viewLifecycleOwner) { state ->
                    when (state) {
                        true -> {
                            hideViews()
                            binding.apply {
                                pbNews.visibility = View.VISIBLE
                                loadingText.visibility = View.VISIBLE
                            }
                        }
                        false -> {
                            binding.apply {
                                pbNews.visibility = View.GONE
                                loadingText.visibility = View.GONE
                            }
                        }
                    }
                }

                dataFetchState.observe(viewLifecycleOwner) { state ->
                    when (state) {
                        true -> unHideViews()
                        false -> {
                            hideViews()
                            binding.apply {
                                pbNews.visibility = View.GONE
                                tvError.visibility = View.VISIBLE
                                loadingText.visibility = View.GONE

                                tvError.text = if (isNetworkAvailable)
                                    getString(R.string.desc_error_refresh)
                                else
                                    getString(R.string.desc_without_connection_and_items)
                            }
                        }
                    }
                }

                news.observe(viewLifecycleOwner) { list -> newsAdapter.submitList(list) }
            }
        }
    }

    private fun logicRetriveData() {
        hideViews()
        if (isNetworkAvailable) viewModel.refreshNews()
        else viewModel.getNews()
    }

    private fun hideViews() {
        binding.apply {
            pbNews.visibility = View.GONE
            rvNews.visibility = View.GONE
            tvError.visibility = View.GONE
            loadingText.visibility = View.GONE
            tvErrorConnetion.visibility = View.GONE
        }
    }

    private fun unHideViews() {
        binding.apply {
            tvError.visibility = View.GONE
            rvNews.visibility = View.VISIBLE
            if (!isNetworkAvailable) tvErrorConnetion.visibility = View.VISIBLE
        }
    }
}
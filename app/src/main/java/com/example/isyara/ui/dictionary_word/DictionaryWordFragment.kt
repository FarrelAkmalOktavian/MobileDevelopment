package com.example.isyara.ui.dictionary_word

import DictionaryWordAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.isyara.R
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.databinding.FragmentDictionarySentenceBinding


class DictionaryWordFragment : Fragment() {

    private val dictionaryWordViewModel: DictionaryWordViewModel by viewModels {
        DictionaryWordViewModelFactory.getInstance(requireContext())
    }

    private var _binding: FragmentDictionarySentenceBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DictionaryWordAdapter

    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionarySentenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupObserver()

        // Get token
        val userPreferences = UserPreferences(requireContext())
        token = userPreferences.getToken() ?: ""

        // Fetch all data initially
        dictionaryWordViewModel.searchSentence(token, "")

        // Perform search when user inputs a query
        binding.tfSearch.editText?.setOnEditorActionListener { _, _, _ ->
            val query = binding.tfSearch.editText?.text.toString()
            dictionaryWordViewModel.searchSentence(token, query)
            true
        }
    }

    private fun setupToolbar() {
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.includeToolbar.toolbar)
        activity.supportActionBar?.title = getString(R.string.kata)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.includeToolbar.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        adapter = DictionaryWordAdapter(emptyList())
        binding.rvSentence.layoutManager = GridLayoutManager(context, 2)
        binding.rvSentence.adapter = adapter
    }

    private fun setupObserver() {
        dictionaryWordViewModel.sentences.observe(viewLifecycleOwner) { sentences ->
            if (sentences.isNotEmpty()) {
                adapter = DictionaryWordAdapter(sentences)
                binding.rvSentence.adapter = adapter
                binding.rvSentence.isVisible = true
                binding.imgEmpty.isVisible = false
            } else {
                binding.rvSentence.isVisible = false
                binding.imgEmpty.isVisible = true
            }
        }

        dictionaryWordViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        dictionaryWordViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            binding.imgEmpty.isVisible = true
            binding.rvSentence.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
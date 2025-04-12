package com.example.projetotrabalhofinal.ui.historico

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetotrabalhofinal.adapters.EventAdapter
import com.example.projetotrabalhofinal.databinding.FragmentHistoricoBinding
import com.example.projetotrabalhofinal.db.EventDbHelper

class HistoricoFragment : Fragment() {
    private var _binding: FragmentHistoricoBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: EventDbHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoricoBinding.inflate(inflater, container, false)
        dbHelper = EventDbHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPastEvents()
    }

    private fun loadPastEvents() {
        val currentTime = System.currentTimeMillis()
        val cursor = dbHelper.getPastEvents(currentTime)
        val events = dbHelper.cursorToEventList(cursor)

        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHistory.adapter = EventAdapter(events) { event ->
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dbHelper.close()
        _binding = null
    }
}
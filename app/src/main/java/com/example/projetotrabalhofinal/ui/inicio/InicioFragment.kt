package com.example.projetotrabalhofinal.ui.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetotrabalhofinal.adapters.EventAdapter
import com.example.projetotrabalhofinal.databinding.FragmentInicioBinding
import com.example.projetotrabalhofinal.db.EventDbHelper

class InicioFragment : Fragment() {
    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: EventDbHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        dbHelper = EventDbHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUpcomingEvents()
    }

    private fun loadUpcomingEvents() {
        val currentTime = System.currentTimeMillis()
        val cursor = dbHelper.getUpcomingEvents(currentTime)
        val events = dbHelper.cursorToEventList(cursor)

        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewEvents.adapter = EventAdapter(events,
            { _ ->

            },
            { event ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirmar exclusão")
                    .setMessage("Deseja realmente excluir este evento?")
                    .setPositiveButton("Excluir") { _, _ ->
                        dbHelper.deleteEvent(requireContext(), event.id)
                        loadUpcomingEvents()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dbHelper.close()
        _binding = null
    }
}
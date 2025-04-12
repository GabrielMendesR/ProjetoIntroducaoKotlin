package com.example.projetotrabalhofinal.ui.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projetotrabalhofinal.databinding.FragmentAddBinding
import com.example.projetotrabalhofinal.db.EventDbHelper
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: EventDbHelper
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        dbHelper = EventDbHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDatePicker()
        setupTimePicker()
        setupSaveButton()
    }

    private fun setupDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        binding.editTextDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupTimePicker() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            updateTimeInView()
        }

        binding.editTextTime.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.editTextDate.setText(sdf.format(calendar.time))
    }

    private fun updateTimeInView() {
        val myFormat = "HH:mm"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.editTextTime.setText(sdf.format(calendar.time))
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            if (validateFields()) {
                saveEvent()
            }
        }
    }

    private fun validateFields(): Boolean {
        return when {
            binding.editTextTitle.text.toString().trim().isEmpty() -> {
                showToast("Informe o título do evento")
                false
            }
            binding.editTextDate.text.toString().trim().isEmpty() -> {
                showToast("Selecione a data do evento")
                false
            }
            binding.editTextTime.text.toString().trim().isEmpty() -> {
                showToast("Selecione a hora do evento")
                false
            }
            else -> true
        }
    }

    private fun saveEvent() {
        val title = binding.editTextTitle.text.toString()
        val description = binding.editTextDescription.text.toString()
        val eventTime = calendar.timeInMillis

        try {
            val id = dbHelper.insertEvent(title, description, eventTime)

            if (id != -1L) {
                showToast("Evento salvo com sucesso!")
                clearFields()
                findNavController().navigateUp()
            } else {
                showToast("Erro ao salvar evento")
            }
        } catch (e: Exception) {
            showToast("Erro: ${e.message}")
        }
    }

    private fun clearFields() {
        binding.editTextTitle.text.clear()
        binding.editTextDescription.text.clear()
        binding.editTextDate.text.clear()
        binding.editTextTime.text.clear()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dbHelper.close()
        _binding = null
    }
}
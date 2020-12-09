package com.warnet.laptopku

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment

class BudgetFragment : Fragment() {
    // Variabel untuk merujuk EditText Min dan Max
    internal lateinit var minEditText: EditText
    internal lateinit var maxEditText: EditText

    // Variabel untuk menyimpan range budget pengguna
    internal var min = 0
    internal var max = 99999999

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Menangkap perubahan angka pada EditText Min dan Max, lalu menyimpannya ke variabel masing-masing
        minEditText = view.findViewById(R.id.minEditText)
        minEditText.addTextChangedListener(object : android.text.TextWatcher{
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int){}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int){
                min = if(s.isNotBlank())
                    s.toString().toInt()
                else
                    0
            }
            override fun afterTextChanged(s: android.text.Editable){}
        })
        maxEditText = view.findViewById(R.id.maxEditText)
        maxEditText.addTextChangedListener(object : android.text.TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int){
                max = if(s.isNotBlank())
                    s.toString().toInt()
                else
                    0
            }
            override fun afterTextChanged(s: android.text.Editable){}
        })
    }
}
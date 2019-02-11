package com.leinaro.grunenthal.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import com.leinaro.grunenthal.GrnenthalApplication
import com.leinaro.grunenthal.R
import com.leinaro.grunenthal.models.SearchParameters
import kotlinx.android.synthetic.main.activity_custom_search.*


class CustomSearchActivity : AppCompatActivity(),
        AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {

    private var channelSelected = 0

    private var franchiseSelected = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_search)
        setupView()
        initListeners()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupView() {
        setupToolbar()
        setupFranchises()
    }

    private fun setupFranchises() {
        val adapter = ArrayAdapter<String>(baseContext, android.R.layout.simple_spinner_dropdown_item, GrnenthalApplication.franquicias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_pharmacy_chain.adapter = adapter
    }

    private fun initListeners() {
        spinner_pharmacy_chain.onItemSelectedListener = this
        checkbox_business_pharmacy.setOnCheckedChangeListener(this)
        checkbox_mix_pharmacy.setOnCheckedChangeListener(this)
        button_search.setOnClickListener {
            val searchParameters = SearchParameters(franchiseSelected, channelSelected)
            val intent = Intent()
            intent.putExtra("SEARCH_PARAMETERS", searchParameters)
            setResult(8888, intent)
            finish()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        franchiseSelected = GrnenthalApplication.franquicias[position]
        Toast.makeText(baseContext, "$franchiseSelected has been the franchise selected", Toast.LENGTH_SHORT).show()
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        channelSelected = when (buttonView.id) {
            R.id.checkbox_mix_pharmacy -> getChannelSelected1(isChecked)
            R.id.checkbox_business_pharmacy -> getChannelSelected2(isChecked)
            else -> 0
        }
        Toast.makeText(baseContext, "Channel selected: $channelSelected", Toast.LENGTH_SHORT).show()
    }

    private fun getChannelSelected1(isChecked: Boolean): Int {
        return if (isChecked) {
            if (checkbox_business_pharmacy.isChecked) 0 else 1
        } else {
            if (checkbox_business_pharmacy.isChecked) 2 else 0
        }
    }

    private fun getChannelSelected2(isChecked: Boolean): Int {
        return if (isChecked) {
            if (checkbox_mix_pharmacy.isChecked) 0 else 2
        } else {
            if (checkbox_mix_pharmacy.isChecked) 1 else 0
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}

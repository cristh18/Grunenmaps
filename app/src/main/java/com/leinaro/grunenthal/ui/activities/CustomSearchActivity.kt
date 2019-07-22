package com.leinaro.grunenthal.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.leinaro.grunenthal.GrnenthalApplication
import com.leinaro.grunenthal.R
import com.leinaro.grunenthal.models.SearchParameters
import kotlinx.android.synthetic.main.activity_custom_search.*

class CustomSearchActivity : AppCompatActivity(),
        AdapterView.OnItemSelectedListener {

    private var channelSelected = 0

    private var franchiseSelected = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_search)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setupView()
        initListeners()
    }

    private fun setupView() {
        setupToolbar()
        setupFranchises()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupFranchises() {
        val adapter = ArrayAdapter<String>(baseContext, android.R.layout.simple_spinner_dropdown_item, GrnenthalApplication.franquicias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_pharmacy_chain.adapter = adapter
    }

    private fun initListeners() {
        spinner_pharmacy_chain.onItemSelectedListener = this
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
        Log.e(this.javaClass.name, "$franchiseSelected has been the franchise selected")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}

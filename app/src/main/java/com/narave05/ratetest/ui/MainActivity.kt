package com.narave05.ratetest.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.narave05.ratetest.R
import com.narave05.ratetest.ui.ratesinfo.RatesFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState != null) return
        //TODO move to navigation component :)
         supportFragmentManager.beginTransaction().apply {
             add(R.id.container, RatesFragment())
             commit()
         }
    }
}

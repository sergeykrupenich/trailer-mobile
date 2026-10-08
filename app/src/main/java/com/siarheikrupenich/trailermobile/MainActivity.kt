package com.siarheikrupenich.trailermobile

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.siarheikrupenich.trailermobile.viewmodel.TrailerApp
import com.siarheikrupenich.trailermobile.viewmodel.TrailerGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setNavGraph()
    }

    private fun setNavGraph() {
        setContent {
            TrailerApp { TrailerGraph() }
        }
    }
}

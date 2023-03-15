package com.labs.flipkart.sceneviewtoggleimpl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.sceneview.model.GLBLoader
import io.github.sceneview.model.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class BlackActivity : AppCompatActivity(R.layout.activity_black) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenResumed {
            val s: Model? = withContext(Dispatchers.Default) {
                GLBLoader.loadModel(
                    this@BlackActivity,
                    "https://static-assets-web.flixcart.com/arm/glb/RFRFNDEEJ28SNQPG.glb"
                )
            }
        }
    }
}
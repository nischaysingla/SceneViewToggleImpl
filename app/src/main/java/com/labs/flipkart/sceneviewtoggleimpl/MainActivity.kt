package com.labs.flipkart.sceneviewtoggleimpl

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.doOnDetach
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.ar.sceneform.ArCameraNode
import com.gorisse.thomas.lifecycle.doOnDestroy
import io.github.sceneview.SceneView
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.model.GLBLoader
import io.github.sceneview.model.Model
import io.github.sceneview.model.createInstance
import io.github.sceneview.node.ModelNode
import io.github.sceneview.utils.Color

class MainActivity : AppCompatActivity() {
    private lateinit var switchFab: FloatingActionButton
    private lateinit var crossFab: FloatingActionButton
    private lateinit var frameLayout: FrameLayout

    private lateinit var rfrModel: Model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        switchFab = findViewById(R.id.floatingActionButton)
        crossFab = findViewById(R.id.removeFragmentFab)
        frameLayout = findViewById(R.id.frameLayout)
        crossFab.setOnClickListener {
            frameLayout.removeAllViews()
        }
        frameLayout.addView(getThreeDView())

        switchFab.setOnClickListener {
            val isArSceneView = frameLayout.children.firstOrNull()?.apply {
                (this as SceneView).let {
                    it.children.filter { it !is ArCameraNode }.forEach {
                        it.destroy()
                    }
                }
            } is ArSceneView
            frameLayout.removeAllViews()
            if (isArSceneView) {
                frameLayout.addView(getThreeDView())
            } else {
                frameLayout.addView(getArView())
            }
        }
    }


    fun getArView(): View {
        val arSceneView = ArSceneView(this).apply {
            doOnDestroy { view: View ->
                Log.i("<RMN>", "getArView: doOnDestroy $view")
            }


            doOnDetach {
                children.getOrNull(1)?.destroy()
            }
            onArSessionCreated = {

                val arModelNode = ArModelNode(PlacementMode.BEST_AVAILABLE).apply {
                    applyPoseRotation = false
                    doOnAttachedToScene {
                        lifecycleScope.launchWhenResumed {
                            if (!::rfrModel.isInitialized) {
                                rfrModel = GLBLoader.loadModel(
                                    context,
                                    "https://static-assets-web.flixcart.com/arm/glb/RFRFNDEEJ28SNQPG.glb"
                                )!!
                            }
                            this@apply.setModelInstance(rfrModel.createInstance())
                            Log.i("<RMN>", "getThreeDView: $rfrModel")
                        }
                    }
                }
                addChild(arModelNode)
                selectedNode = arModelNode
            }
            onFrame = {
                (children.getOrNull(1) as? ArNode)?.let { arNode: ArNode ->
                    if (!arNode.isAnchored) {
                        currentFrame?.hitTest()?.let {
                            if (it.trackable is com.google.ar.core.Plane) {
                                arNode.anchor = it.createAnchor()
                            }
                        }
                    }
                }
            }

        }

        return arSceneView
    }


    fun getThreeDView(): View {
        return SceneView(this).apply {
            backgroundColor = Color(1f, 1f, 1f, 1f)
            doOnDestroy { view: View ->
                Log.i("<RMN>", "getArView: doOnDestroy $view")
            }
            doOnDetach {
//                children.getOrNull(1)?.destroy()
            }
            addChild(
                ModelNode().apply {
                    doOnAttachedToScene {
                        lifecycleScope.launchWhenResumed {
                            if (!::rfrModel.isInitialized) {
                                rfrModel = GLBLoader.loadModel(
                                    context,
                                    "LRCFZ6CEGGHUTA4G.glb"
                                )!!
                            }
                            this@apply.setModelInstance(rfrModel.createInstance())
                            Log.i("<RMN>", "getThreeDView: $rfrModel")
                        }
                    }
                }
            )
        }
    }

}
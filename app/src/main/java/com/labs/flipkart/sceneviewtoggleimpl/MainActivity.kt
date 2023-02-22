package com.labs.flipkart.sceneviewtoggleimpl

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.doOnDetach
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.ar.sceneform.ArCameraNode
import com.gorisse.thomas.lifecycle.doOnDestroy
import io.github.sceneview.SceneView
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.utils.Color

class MainActivity : AppCompatActivity() {
    private lateinit var switchFab: FloatingActionButton
    private lateinit var crossFab: FloatingActionButton
    private lateinit var frameLayout: FrameLayout

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
                    loadModelGlbAsync(
                        context = this@MainActivity,
                        glbFileLocation = "LRCFZ6CEGGHUTA4G.glb",
                        autoAnimate = true,
                        scaleToUnits = 1f,
                        onError = {
                            Log.i("<RMN>", "getArView: onError $it")
                        },
                        onLoaded = {
                            Log.i("<RMN>", "getArView: onLoaded $it")
                        }
                    )
                }
                addChild(arModelNode)
                selectedNode = arModelNode
            }
            onFrame = {
                (children.getOrNull(1) as? ArNode)?.let { arNode: ArNode ->
                    if (!arNode.isAnchored) {
                        currentFrame?.hitTest()?.let {
                            Log.i("<RMN>", "hitTest: ${it.trackable} ${it.hitPose}")
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
                children.getOrNull(1)?.destroy()
            }
            addChild(
                ModelNode(
                    context = this@MainActivity,
                    modelGlbFileLocation = "LRCFZ6CEGGHUTA4G.glb",
                    scaleUnits = 0.8f,
                    centerOrigin = Position(y = -0.3f),
                    onError = {
                        Log.i("<RMN>", "getArView: onError $it")
                    },
                    onLoaded = {
                        Log.i("<RMN>", "getArView: onLoaded $it")
                    }
                )
            )
        }
    }

}
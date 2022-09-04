package com.realityexpander.stateflowvsflowvsshareflowvslivedata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.realityexpander.stateflowvsflowvsshareflowvslivedata.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // LiveData
        binding.btnLiveData.setOnClickListener {
            viewModel.triggerLiveData()
        }

        // StateFlow
        binding.btnStateFlow.setOnClickListener {
            viewModel.triggerStateFlow()
        }

        // Flow
        binding.btnFlow.setOnClickListener {

            // Flow is a one-time shot, there is no state retained. When flow completes, it is done.

            lifecycleScope.launch { // Ok to use launch here because its not a StateFlow
                viewModel.triggerFlow().collectLatest {
                    binding.tvFlow.text = "Flow: $it"
                }
            }
        }

        // SharedFlow
        binding.btnSharedFlow.setOnClickListener {
            viewModel.triggerSharedFlow()
        }

        subscribeToObservables()

    }

    private fun subscribeToObservables() {

        // LiveData - re-emits after config change
        viewModel.liveData.observe(this, Observer {
            binding.tvLiveData.text = "LiveData: $it"
        })

        // StateFlow - similar to liveData but it is a Kotlin Flow
        // Easier than livedata for testing (can use TestObserver, no waiting for `delays`)
        // StateFlow is a HOT flow (keeps emitting even if no observers)
        // StateFlow will re-emit its value after config change.
        lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.collectLatest {
                binding.tvStateFlow.text = "StateFlow: $it"
            }
        }

        // SharedFlow
        // A HOT flow, so it will still emit events even if no observers/collectors.
        // Used for one-time events. (e.g. navigation events, toast or snackbar events)
        // Value will NOT be re-emitted after config change.
        lifecycleScope.launchWhenStarted {
            viewModel.sharedFlow.collectLatest {
                binding.tvSharedFlow.text = "SharedFlow: $it"
                viewModel.sharedFlowValue = it

                // This will only show the snackbar once, even if the value is re-emitted after config change.
                Snackbar.make(binding.root, "SharedFlow: $it", Snackbar.LENGTH_SHORT).show()
            }
        }



    }
}
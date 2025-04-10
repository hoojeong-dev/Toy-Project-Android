package com.example.wearable.datalayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wearable.common.constants.CommonConstants
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Node
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Node ViewModel
 */
class NodeViewModel: ViewModel() {

    /** Connected Nodes */
    private val _nodes: MutableSet<Node> = mutableSetOf()
    val nodes: Set<Node> = _nodes

    /**
     * 연결된 노드 데이터 저장하기
     */
    fun fetchNodes(capabilityClient: CapabilityClient) = viewModelScope.launch {

        _nodes.clear()
        _nodes.addAll(
            getCapabilities(capabilityClient)
                .filterValues { CommonConstants.WEAR_CAPABILITY in it }
                .keys
                .toMutableSet()
        )
    }

    /**
     * 연결된 노드 데이터 가져오기
     *
     * @return Map<Node, Set<String>>
     */
    suspend fun getCapabilities(capabilityClient: CapabilityClient): Map<Node, Set<String>> =
        capabilityClient.getAllCapabilities(CapabilityClient.FILTER_REACHABLE)
            .await()
            .flatMap { (capability, capabilityInfo) ->
                capabilityInfo.nodes.map { it to capability }
            }.groupBy(
                keySelector = { it.first },
                valueTransform = { it.second }
            ).mapValues { it.value.toSet() }
}
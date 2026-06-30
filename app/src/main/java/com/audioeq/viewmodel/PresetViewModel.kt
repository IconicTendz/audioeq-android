package com.audioeq.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.audioeq.data.export.PresetExportImport
import com.audioeq.data.model.Preset
import com.audioeq.data.repository.PresetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PresetViewModel(
    private val presetRepository: PresetRepository? = null,
    private val exportImport: PresetExportImport? = null
) : ViewModel() {

    private val _presets = MutableStateFlow<List<Preset>>(emptyList())
    val presets: StateFlow<List<Preset>> = _presets.asStateFlow()

    private val _activePresetId = MutableStateFlow<String?>(null)
    val activePresetId: StateFlow<String?> = _activePresetId.asStateFlow()

    init {
        viewModelScope.launch {
            presetRepository?.allPresets?.collect { list ->
                _presets.value = list
            }
        }
    }

    fun loadFactoryPresets() {
        viewModelScope.launch { presetRepository?.loadFactoryPresets() }
    }

    fun savePreset(preset: Preset) {
        viewModelScope.launch { presetRepository?.savePreset(preset) }
    }

    fun deletePreset(id: String) {
        viewModelScope.launch { presetRepository?.deletePresetById(id) }
    }

    fun setActivePreset(preset: Preset) {
        _activePresetId.value = preset.id
    }

    fun createPresetFromCurrent(
        name: String,
        description: String,
        currentPreset: Preset
    ) {
        val newPreset = currentPreset.copy(
            id = java.util.UUID.randomUUID().toString(),
            name = name,
            description = description,
            isFactory = false,
            category = com.audioeq.data.model.PresetCategory.CUSTOM,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        savePreset(newPreset)
        setActivePreset(newPreset)
    }

    fun duplicatePreset(preset: Preset) {
        val copy = preset.copy(
            id = java.util.UUID.randomUUID().toString(),
            name = "${preset.name} (Copy)",
            isFactory = false,
            category = com.audioeq.data.model.PresetCategory.CUSTOM,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        savePreset(copy)
    }

    fun exportPresets(uri: Uri) {
        viewModelScope.launch {
            val currentPresets = _presets.value
            exportImport?.exportPresetsToJson(currentPresets, uri)
        }
    }

    fun importPresets(uri: Uri, onResult: (List<Preset>?) -> Unit) {
        viewModelScope.launch {
            val imported = exportImport?.importPresetsFromUri(uri)
            imported?.forEach { preset -> presetRepository?.savePreset(preset) }
            onResult(imported)
        }
    }
}

package com.audioeq.data.export

import android.content.Context
import android.net.Uri
import com.audioeq.data.model.AudioEffectState
import com.audioeq.data.model.EqState
import com.audioeq.data.model.Preset
import com.audioeq.data.model.PresetCategory
import com.audioeq.data.model.SpectrumSettings
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class PresetExportImport(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    data class ExportData(
        val version: Int = 1,
        val appName: String = "AudioEQ",
        val presets: List<Preset>,
        val exportDate: Long = System.currentTimeMillis()
    )

    fun exportPresetsToJson(presets: List<Preset>, uri: Uri): Boolean {
        return try {
            val exportData = ExportData(presets = presets)
            val json = gson.toJson(exportData)
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(json.toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun exportPresetsToZip(presets: List<Preset>, uri: Uri): Boolean {
        return try {
            val exportData = ExportData(presets = presets)
            val json = gson.toJson(exportData)
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                ZipOutputStream(outputStream).use { zipOut ->
                    zipOut.putNextEntry(ZipEntry("audioeq_presets.json"))
                    zipOut.write(json.toByteArray())
                    zipOut.closeEntry()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun importPresetsFromUri(uri: Uri): List<Preset>? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val content = BufferedReader(InputStreamReader(inputStream)).readText()
            inputStream.close()

            // Try plain JSON first
            try {
                val exportData = gson.fromJson(content, ExportData::class.java)
                return exportData.presets.map { it.copy(isFactory = false) }
            } catch (_: Exception) { /* Not plain JSON, try ZIP */ }

            // Try ZIP
            try {
                val zipStream = ZipInputStream(content.byteInputStream())
                var entry: ZipEntry? = zipStream.nextEntry
                while (entry != null) {
                    if (entry.name == "audioeq_presets.json") {
                        val zipContent = zipStream.readBytes().toString(Charsets.UTF_8)
                        val exportData = gson.fromJson(zipContent, ExportData::class.java)
                        zipStream.closeEntry()
                        zipStream.close()
                        return exportData.presets.map { it.copy(isFactory = false) }
                    }
                    zipStream.closeEntry()
                    entry = zipStream.nextEntry
                }
                zipStream.close()
            } catch (_: Exception) { /* Not a valid ZIP */ }

            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

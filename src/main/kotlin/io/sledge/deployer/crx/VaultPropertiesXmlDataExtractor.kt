package io.sledge.deployer.crx

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import java.io.IOException
import java.util.zip.ZipFile


class VaultPropertiesXmlDataExtractor {

    companion object {
        const val VLT_PROPERTIES_PATH = "META-INF/vault/properties.xml"
    }

    val kotlinXmlMapper = XmlMapper(JacksonXmlModule().apply {
        setDefaultUseWrapper(false)
    }).registerKotlinModule()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)

    @Throws(IOException::class)
    fun getEntryValue(zip: String, entryKeyName: String): String {
        val zipFile = ZipFile(File(zip))
        val zipEntry = zipFile.getEntry(VLT_PROPERTIES_PATH)
        val zipFileInputStream = zipFile.getInputStream(zipEntry)

        try {
            val propertiesXml = kotlinXmlMapper.readValue(zipFileInputStream, Properties::class.java)
            return propertiesXml.entries.find { entry -> entry.key.equals(entryKeyName) }?.value ?: ""
        } finally {
            zipFile.close()
        }
    }

    @JacksonXmlRootElement(localName = "properties")
    data class Properties(
            @JacksonXmlElementWrapper(useWrapping = false)
            @set:JacksonXmlProperty(localName = "entry")
            var entries: List<Entry> = ArrayList()
    )

    data class Entry(
            @set:JacksonXmlProperty(localName = "key", isAttribute = true)
            var key: String?) {
        @set:JacksonXmlText
        lateinit var value: String
    }
}

package io.sledge.deployer.zip

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
import java.util.zip.ZipFile
import java.io.IOException


class Unarchiver {

    val kotlinXmlMapper = XmlMapper(JacksonXmlModule().apply {
        setDefaultUseWrapper(false)
    }).registerKotlinModule()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)

    @Throws(IOException::class)
    fun unzipPropertiesFile(zip: String, filePath: String): String {
        val zipFile = ZipFile(File(zip))
        val zipEntry = zipFile.getEntry(filePath)
        val file = zipFile.getInputStream(zipEntry)
        val m = kotlinXmlMapper.readValue(file, Properties::class.java)
        zipFile.close()
        return m.entries.find { entry -> entry.key.equals("name") }?.value ?: ""
    }

    companion object {

        const val META_PATH = "META-INF"

        const val VLT_DIR = "vault"

        const val VLT_PATH = "$META_PATH/$VLT_DIR"

        const val VLT_PROPERTIES = "$VLT_PATH/properties.xml"

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
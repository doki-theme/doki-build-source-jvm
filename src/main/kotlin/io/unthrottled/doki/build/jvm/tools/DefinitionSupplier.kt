package io.unthrottled.doki.build.jvm.tools

import com.google.gson.GsonBuilder
import io.unthrottled.doki.build.jvm.models.HasId
import io.unthrottled.doki.build.jvm.models.MasterThemeDefinition
import io.unthrottled.doki.build.jvm.models.ThemeTemplateDefinition
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import java.util.stream.Stream

enum class DokiProduct(
  val value: String,
  val prettyName: String
) {
  JETBRAINS_THEME("jetbrains", "JetBrains"), ICONS("icons", "Icons");
}

object DefinitionSupplier {

  private val gson = GsonBuilder().setPrettyPrinting().create()

  fun createThemeDefinitions(
    themeDirectory: Path,
    masterThemeDirectory: Path
  ): Map<String, Map<String, ThemeTemplateDefinition>> =
    Stream.concat(
      Files.walk(Paths.get(themeDirectory.toString(), "templates")),
      Files.walk(Paths.get(masterThemeDirectory.toString(), "templates"))
    )
      .filter { !Files.isDirectory(it) }
      .filter { it.fileName.toString().endsWith(".template.json") }
      .map { Files.newInputStream(it) }
      .map {
        gson.fromJson(
          InputStreamReader(it, StandardCharsets.UTF_8),
          ThemeTemplateDefinition::class.java
        )
      }
      .collect(
        Collectors.groupingBy {
          it.type ?: throw IllegalArgumentException("Expected template ${it.name} to have a type")
        }
      )
      .entries.associate {
        it.key to (it.value.associateBy { kv -> kv.name })
      }

  fun <T : HasId> getAllDokiThemeDefinitions(
    dokiProduct: DokiProduct,
    productBuildSourceDirectory: Path,
    masterThemeDirectory: Path,
    clazz: Class<T>
  ): Stream<Triple<Path, MasterThemeDefinition, T>> {
    val allProductDefinitions =
      Files.walk(productBuildSourceDirectory)
        .filter { !Files.isDirectory(it) }
        .filter { it.fileName.toString().endsWith("${dokiProduct.value}.definition.json") }
        .map { Files.newInputStream(it) }
        .map {
          gson.fromJson(
            InputStreamReader(it, StandardCharsets.UTF_8),
            clazz
          )
        }.collect(
          Collectors.toMap(
            { it.id }, { it }
          )
        )

    val masterThemeDefinitionPath = Paths.get(masterThemeDirectory.toString(), "definitions")
    return Files.walk(masterThemeDefinitionPath)
      .filter { !Files.isDirectory(it) }
      .filter { it.fileName.toString().endsWith("master.definition.json") }
      .map { it to Files.newInputStream(it) }
      .map {
        val masterThemePath = it.first.toString()
        val masterFileDefinition = masterThemePath.substringAfter("$masterThemeDefinitionPath")
        val productDefinitionDefinitionPath =
          Paths.get(productBuildSourceDirectory.toString(), masterFileDefinition)
        val masterThemeDefinition = gson.fromJson(
          InputStreamReader(it.second, StandardCharsets.UTF_8),
          MasterThemeDefinition::class.java
        )
        val productDefinition =
          allProductDefinitions[masterThemeDefinition.id] ?: throw IllegalArgumentException(
            """
            Master Theme ${masterThemeDefinition.displayName} is missing the ${dokiProduct.prettyName} definition file!
            """.trimIndent()
          )
        Triple(productDefinitionDefinitionPath, masterThemeDefinition, productDefinition)
      }
  }
}

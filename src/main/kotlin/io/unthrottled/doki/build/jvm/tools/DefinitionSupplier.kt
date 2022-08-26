package io.unthrottled.doki.build.jvm.tools

import com.google.gson.GsonBuilder
import io.unthrottled.doki.build.jvm.models.AssetTemplateDefinition
import io.unthrottled.doki.build.jvm.models.HasId
import io.unthrottled.doki.build.jvm.models.MasterThemeDefinition
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Optional
import java.util.stream.Collectors
import java.util.stream.Stream

enum class DokiProduct(
  val value: String,
  val prettyName: String
) {
  JETBRAINS_THEME("jetbrains", "JetBrains"), ICONS("icons", "Icons");
}

enum class ConstructableTypes(val serializedName: String) {
  LookAndFeel("LAF"), Color("COLOR");
}

class ConstructableAssetSupplier(
  private val assetTypeToAssetTemplate: Map<String, Map<String, AssetTemplateDefinition>>
) {

  fun getConstructableAsset(templateType: ConstructableTypes): Optional<ConstructableAsset> =
    Optional.ofNullable(assetTypeToAssetTemplate[templateType.serializedName])
      .map {
        ConstructableAsset(
          templateType,
          it
        )
      }
}

object BuildTools {
  fun <T, R> resolveTemplateWithCombini(
    childTemplate: T,
    templateNameToTemplate: Map<String, T>,
    attributeResolver: (T) -> R,
    parentResolver: (T) -> String?,
    combiniFunction: (R, R) -> R,
  ): R {
    val parentKey = parentResolver(childTemplate)
    return if (parentKey == null) {
      attributeResolver(childTemplate)
    } else {
      val parent = templateNameToTemplate[parentKey]
        ?: throw IllegalStateException("Expected template to have parent key $parentKey")
      val resolvedParent = resolveTemplateWithCombini(
        parent,
        templateNameToTemplate, attributeResolver, parentResolver, combiniFunction
      )
      combiniFunction(resolvedParent, attributeResolver(childTemplate))
    }
  }

  fun <T> combineMaps(
    parent: Map<String, T>,
    child: Map<String, T>
  ): MutableMap<String, T> {
    val changeableParent = parent.toMutableMap()
    changeableParent.putAll(child)
    return changeableParent
  }
}

class ConstructableAsset(
  val type: ConstructableTypes,
  val definitions: Map<String, AssetTemplateDefinition>
) {

  fun constructAsset() {
  }
}

object DefinitionSupplier {

  private val gson = GsonBuilder().setPrettyPrinting().create()

  fun createCommonAssetsTemplate(
    buildSourceAssetDirectory: Path,
    masterThemesDirectory: Path
  ): ConstructableAssetSupplier =
    ConstructableAssetSupplier(
      Stream.concat(
        Files.walk(Paths.get(buildSourceAssetDirectory.toString(), "templates")),
        Files.walk(Paths.get(masterThemesDirectory.toString(), "templates"))
      )
        .filter { !Files.isDirectory(it) }
        .filter { it.fileName.toString().endsWith(".template.json") }
        .map { Files.newInputStream(it) }
        .map {
          gson.fromJson(
            InputStreamReader(it, StandardCharsets.UTF_8),
            AssetTemplateDefinition::class.java
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
    )

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

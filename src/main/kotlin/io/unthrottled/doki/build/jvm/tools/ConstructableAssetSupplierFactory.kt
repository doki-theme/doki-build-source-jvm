package io.unthrottled.doki.build.jvm.tools

import io.unthrottled.doki.build.jvm.models.AssetTemplateDefinition
import io.unthrottled.doki.build.jvm.tools.PathTools.readJSONFromFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Optional
import java.util.stream.Collectors
import java.util.stream.Stream

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

class ConstructableAsset(
  val type: ConstructableTypes,
  val definitions: Map<String, AssetTemplateDefinition>
)

object ConstructableAssetSupplierFactory {

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
        .map { readJSONFromFile(it, AssetTemplateDefinition::class.java) }
        .collect(
          Collectors.groupingBy {
            it.type ?: throw IllegalArgumentException("Expected template ${it.name} to have a type")
          }
        )
        .entries.associate {
          it.key to (it.value.associateBy { kv -> kv.name })
        }
    )
}

package io.unthrottled.doki.build.jvm.models

data class SchemaDefinition(
  val required: List<String>?
)

data class ThemeDefinitionSchema(
  val properties: Map<String, SchemaDefinition>
)

class Background(
  val name: String,
  val position: String,
  val opacity: Int? = null
)

class Backgrounds(
  val default: Background,
  val secondary: Background?
)

data class AssetTemplateDefinition(
  val type: String? = null,
  val extends: String? = null,
  val name: String,
  val ui: Map<String, Any>? = null,
  val icons: Map<String, Any>? = null,
  val colors: Map<String, String>? = null
)

data class BuildSticker(
  val name: String,
  val anchor: String,
  val opacity: Int,
)

data class JetbrainsStickers(
  val default: String,
  val secondary: String?
)

data class BuildStickers(
  val default: BuildSticker,
  val secondary: BuildSticker?
)

data class EditorSchemeOverrides(
  override val colors: Map<String, String>
) : HasColors

data class Overrides(
  val editorScheme: EditorSchemeOverrides?
)

data class BackgroundDefinition(
  val name: String?,
  val position: String?,
  val opacity: Int?
)
data class BackgroundsDefinition(
  val default: BackgroundDefinition?,
  val secondary: BackgroundDefinition?
)

interface HasId {
  val id: String
}
interface HasColors {
  val colors: Map<String, String>
}

// todo: move these to the apps they support
data class JetbrainsAppDefinition(
  override val id: String,
  val editorScheme: Map<String, Any>,
  val overrides: Overrides?,
  val backgrounds: BackgroundsDefinition?,
  val ui: Map<String, Any>,
  val uiBase: String?,
  val icons: Map<String, Any>
) : HasId

// todo: move these to the apps they support
data class IconsAppDefinition(
  override val id: String,
  val overrides: Map<String, Any>,
  override val colors: Map<String, String>,
) : HasId, HasColors

data class MasterThemeDefinition(
  val id: String,
  val name: String,
  val displayName: String,
  val dark: Boolean,
  val author: String,
  val group: String,
  val product: String?,
  val stickers: BuildStickers,
  val overrides: Overrides?,
  override val colors: Map<String, String>,
  val meta: Map<String, String>?
) : HasColors {
  val usableName: String
    get() = name.replace(' ', '_')
      .replace(":", "")
  val usableGroup: String
    get() = group.replace(' ', '_')
      .replace(":", "")
}

data class JetbrainsThemeDefinition(
  val id: String,
  val name: String,
  val displayName: String?,
  val dark: Boolean,
  val author: String?,
  val editorScheme: String,
  val group: String,
  val stickers: JetbrainsStickers,
  val backgrounds: Backgrounds,
  val colors: Map<String, Any>,
  val ui: Map<String, Any>,
  val icons: Map<String, Any>,
  val meta: Map<String, String>
)

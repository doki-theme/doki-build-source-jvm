package io.unthrottled.doki.build.jvm.tools

enum class DokiProduct(
  val value: String,
  val prettyName: String
) {
  JETBRAINS_THEME("jetbrains", "JetBrains"), ICONS("icons", "Icons");
}

package io.unthrottled.doki.build.jvm.tools

import io.unthrottled.doki.build.jvm.models.StringDictionary

fun resolveColor(
  color: String,
  namedColors: StringDictionary<String>,
): String {
  val startingTemplateIndex = color.indexOf('&')
  return if (startingTemplateIndex > -1) {
    val lastDelimiter = color.lastIndexOf('&')
    val namedColor = color.substring(startingTemplateIndex + 1, lastDelimiter + 1)
    val namedColorValue = namedColors[namedColor] ?: throw IllegalStateException(
      "Named color: '$namedColor' is not present"
    )

    if (color == namedColorValue) {
      throw IllegalArgumentException("Very Cheeky, you set $namedColor to resolve to itself \uD83D\uDE12")
    }

    val resolvedNamedColor = resolveColor(namedColorValue, namedColors)

    resolvedNamedColor + color.substring(lastDelimiter + 1)
  } else {
    color
  }
}

fun applyNamedColors(
  objectWithNamedColors: StringDictionary<String>,
  namedColors: StringDictionary<String>,
): StringDictionary<String> {
  return objectWithNamedColors.entries
    .associate { (key, color) ->
      key to resolveColor(color, namedColors)
    }.toMutableMap()
}

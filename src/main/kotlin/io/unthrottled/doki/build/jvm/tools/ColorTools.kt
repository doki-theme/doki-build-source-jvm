package io.unthrottled.doki.build.jvm.tools

import java.util.regex.Pattern

object ColorTools {
  private val COLOR_HEX_PATTERN_RGB = Pattern.compile("^#([A-Fa-f0-9]{6})$")
  private val COLOR_HEX_PATTERN_RGBA = Pattern.compile("^#([A-Fa-f0-9]{8})$")
  private const val HEX_COLOR_LENGTH_RGB = 7
  private const val HEX_COLOR_LENGTH_RGBA = 9

  fun isColorCode(text: String?): Boolean {
    if (text == null || !text.startsWith('#')) return false
    return if (text.length != HEX_COLOR_LENGTH_RGB &&
      text.length != HEX_COLOR_LENGTH_RGBA
    ) {
      false
    } else {
      COLOR_HEX_PATTERN_RGB.matcher(
        text
      ).matches() || COLOR_HEX_PATTERN_RGBA.matcher(
        text
      ).matches()
    }
  }
}

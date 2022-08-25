package io.unthrottled.doki.build.jvm.tools

import java.nio.file.Files
import java.nio.file.Files.createDirectories
import java.nio.file.Files.exists
import java.nio.file.Path

object PathTools {
  fun ensureExists(path: Path): Path {
    if (!exists(path) && Files.isDirectory(path)) {
      createDirectories(path)
    }
    return path
  }

  fun cleanDirectory(themeDirectory: Path) {
    if (Files.notExists(themeDirectory)) {
      createDirectories(themeDirectory)
    } else {
      Files.walk(themeDirectory)
        .sorted(Comparator.reverseOrder())
        .forEach(Files::delete)
    }
  }
}

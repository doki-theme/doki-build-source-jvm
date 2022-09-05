package io.unthrottled.doki.build.jvm.tools

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Files.createDirectories
import java.nio.file.Files.exists
import java.nio.file.Path

object PathTools {
  fun ensureDirectoryExists(path: Path): Path {
    if (!exists(path)) {
      createDirectories(path)
    }
    return path
  }

  fun cleanDirectory(directoryToClean: Path) {
    if (Files.notExists(directoryToClean)) {
      createDirectories(directoryToClean)
    } else {
      Files.walk(directoryToClean)
        .filter(Files::exists)
        .filter { pathToRemove -> pathToRemove != directoryToClean }
        .sorted(Comparator.reverseOrder())
        .forEach(Files::delete)
    }
  }

  private val gson = GsonBuilder()
    .create()

  fun <T> readJSONFromFile(mappingFile: Path, typeToken: TypeToken<T>): T =
    gson.fromJson(
      InputStreamReader(
        Files.newInputStream(
          mappingFile
        )
      ),
      typeToken.type
    )
  fun <T> readJSONFromFile(mappingFile: Path, clazz: Class<T>): T =
    gson.fromJson(
      InputStreamReader(
        Files.newInputStream(
          mappingFile
        )
      ),
      clazz
    )
}

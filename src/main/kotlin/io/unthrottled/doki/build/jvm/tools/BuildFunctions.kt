package io.unthrottled.doki.build.jvm.tools

import io.unthrottled.doki.build.jvm.models.StringDictionary

// same as functions.ts in doki-build-source
object BuildFunctions {
  fun <T, R> resolveTemplateWithCombini(
    childTemplate: T,
    templateNameToTemplate: StringDictionary<T>,
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
    parent: StringDictionary<T>,
    child: StringDictionary<T>
  ): StringDictionary<T> {
    val changeableParent = parent.toMutableMap()
    changeableParent.putAll(child)
    return changeableParent
  }
}

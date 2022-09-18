package io.unthrottled.doki.build.jvm.tools

import io.unthrottled.doki.build.jvm.models.StringDictionary

// same as functions.ts in doki-build-source
object BuildFunctions {

  fun <T, R> composeTemplateWithCombini(
    childTemplate: T,
    templateNameToTemplate: StringDictionary<T>,
    attributeResolver: (T) -> R,
    parentResolver: (T) -> List<String>?,
    combiniFunction: (R, R) -> R,
  ): R {
    val parentTemplateNames = parentResolver(childTemplate)
    return if (parentTemplateNames == null) {
      attributeResolver(childTemplate)
    } else {
      val fullParentTemplates = parentTemplateNames
        .mapNotNull {
          parentTemplateName ->
          templateNameToTemplate[parentTemplateName]
        }

      // combine parents first, so that
      // we know what will be overidden in the base/grandparent template
      val combinedParents = fullParentTemplates
        .map {
          parentTemplate ->
          attributeResolver(parentTemplate)
        }
        .reduce(combiniFunction)

      val grandParentsToFillOut =
        fullParentTemplates.flatMap {
          fullParentTemplate ->
          parentResolver(fullParentTemplate) ?: emptyList()
        }

      if (grandParentsToFillOut.isEmpty()) {
        // no grandparents, so these parents ar the base
        // of the template, apply the child overrides
        combiniFunction(
          combinedParents,
          attributeResolver(childTemplate)
        )
      } else {
        val resolvedBaseTemplate =
          grandParentsToFillOut
            .distinct()
            .map {
              grandParentToResolve ->
              val grandParentTemplate = templateNameToTemplate[grandParentToResolve]
                ?: throw IllegalStateException("Expected template $grandParentToResolve to be present")
              composeTemplateWithCombini(
                grandParentTemplate,
                templateNameToTemplate,
                attributeResolver,
                parentResolver,
                combiniFunction
              )
            }.reduce(combiniFunction)

        // apply parent overrides to the base template
        val fullParentTemplate = combiniFunction(
          resolvedBaseTemplate,
          combinedParents,
        )

        // apply child overrides to the parent overrides.
        combiniFunction(
          fullParentTemplate,
          attributeResolver(childTemplate),
        )
      }
    }
  }

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

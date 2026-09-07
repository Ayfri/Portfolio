package io.github.ayfri.jsonld

import androidx.compose.runtime.Composable
import io.github.ayfri.components.addJsonLD

/** Appends alongside the page-level graph that `setJsonLD` already installed. */
@Composable
fun JsonLD(jsonLD: JsonLD) = addJsonLD(JSON.stringify(jsonLD))

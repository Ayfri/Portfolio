package io.github.ayfri.components

import androidx.compose.runtime.Composable
import io.github.ayfri.AppStyle
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.dom.*

/** Values paired with how often they appear, most frequent first. */
fun List<String>.countedByFrequency() = groupingBy { it }.eachCount().toList().sortedByDescending { it.second }

@Composable
fun SearchField(query: String, placeholder: String, onInput: (String) -> Unit) {
	Style(FiltersStyle)

	Label(attrs = {
		classes(FiltersStyle.search)
	}) {
		I(FontAwesomeType.SOLID, "search")
		Input(InputType.Search) {
			classes(AppStyle.input)
			attr("aria-label", placeholder)
			placeholder(placeholder)
			value(query)
			onInput { onInput(it.value) }
		}
	}
}

/** Clicking the selected chip again clears the filter, so an empty [selected] means "everything". */
@Composable
fun ChipGroup(label: String, icon: String, values: List<Pair<String, Int>>, selected: String, onSelect: (String) -> Unit) {
	if (values.isEmpty()) return
	Style(FiltersStyle)

	Div({
		classes(FiltersStyle.chipGroup)
	}) {
		Span({ classes(FiltersStyle.label) }) {
			I(FontAwesomeType.SOLID, icon)
			Text(label)
		}

		Div({
			classes(FiltersStyle.chips)
		}) {
			values.forEach { (value, count) ->
				Button({
					classes(AppStyle.chip)
					if (value == selected) classes("active")
					attr("aria-pressed", (value == selected).toString())
					onClick { onSelect(if (value == selected) "" else value) }
				}) {
					Text("$value ($count)")
				}
			}
		}
	}
}

object FiltersStyle : StyleSheet() {
	val search by style {
		flex("1 1 18rem")
		position(Position.Relative)

		"i" {
			color(Color(AppStyle.TEXT_MUTED_COLOR))
			left(.9.cssRem)
			position(Position.Absolute)
			top(50.percent)
			property("translate", "0 -50%")
		}

		"input" {
			paddingLeft(2.5.cssRem)
			width(100.percent)
		}
	}

	val chipGroup by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(.6.cssRem)
	}

	val chips by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(.5.cssRem)
	}

	val label by style {
		alignItems(AlignItems.Center)
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		display(DisplayStyle.Flex)
		fontSize(.9.cssRem)
		gap(.5.cssRem)

		"i" {
			color(Color(AppStyle.VIOLET_COLOR))
			fontSize(.8.cssRem)
		}
	}
}

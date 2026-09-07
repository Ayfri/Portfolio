package io.github.ayfri.data

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.AppStyle
import io.github.ayfri.components.A
import io.github.ayfri.components.FontAwesomeType
import io.github.ayfri.components.I
import io.github.ayfri.localImage
import io.github.ayfri.utils.gradientBorderBackground
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*

/**
 * Hand-picked showcase for the home page, kept separate from the GitHub snapshot ordering:
 * star count alone buries recent work behind old Discord libraries.
 */
data class FeaturedProject(
	val name: String,
	val tagline: String,
	val image: String,
	val repository: String,
	val tags: List<String>,
	val liveUrl: String? = null,
)

val featuredProjects = listOf(
	FeaturedProject(
		name = "Kore",
		tagline = "A type-safe Kotlin DSL that compiles straight to Minecraft datapacks, so complex packs never need hand-written JSON or mcfunction files.",
		image = "projects/kore.avif",
		repository = "Ayfri/Kore",
		tags = listOf("Kotlin", "DSL", "Minecraft", "Gradle plugin"),
		liveUrl = "https://kore.ayfri.com",
	),
	FeaturedProject(
		name = "PokéCards-Collector",
		tagline = "The whole Pokémon TCG catalogue, English and Japanese, with prices, filters and the collection you actually own.",
		image = "projects/pokecards.avif",
		repository = "Ayfri/PokeCards-Collector",
		tags = listOf("SvelteKit", "Supabase", "Cloudflare Workers", "Tailwind 4"),
		liveUrl = "https://pokecards-collector.ayfri.com",
	),
	FeaturedProject(
		name = "Atom Clicker",
		tagline = "An incremental game about splitting atoms: buildings, upgrade trees, prestige runs and an online leaderboard.",
		image = "projects/atom-clicker.avif",
		repository = "Ayfri/Atom-Clicker",
		tags = listOf("Svelte 5", "TypeScript", "PixiJS"),
		liveUrl = "https://atom-clicker.ayfri.com",
	),
	FeaturedProject(
		name = "GPT Images",
		tagline = "A bring-your-own-key front-end for GPT Image and Sora, with reference images, batch generation and live cost tracking.",
		image = "projects/gpt-images.avif",
		repository = "Ayfri/GPT-Images",
		tags = listOf("SvelteKit", "OpenAI API", "TypeScript"),
		liveUrl = "https://gpt-images.ayfri.com",
	),
	FeaturedProject(
		name = "Cat'aclsym: Claw of the Dead",
		tagline = "A 2D tower defense where cats hold the line against a zombie horde: eight levels, traps, per-level challenges and an armory progression tree.",
		image = "projects/cataclysm.avif",
		repository = "Cat-aclsym/Cat-aclsym_Claw_of_the_dead",
		tags = listOf("Godot 4", "GDScript", "Team project"),
	),
	FeaturedProject(
		name = "Realtime Todolist",
		tagline = "Shared todo lists that stay in sync live for everyone holding the link, with import, export and drag ordering.",
		image = "projects/todolist.avif",
		repository = "antaww/todo-list",
		tags = listOf("Svelte", "Realtime", "Cloudflare Pages"),
		liveUrl = "https://realtime-todolist.pages.dev",
	),
)

@Composable
fun FeaturedProjectCard(project: FeaturedProject, stars: Int?, hero: Boolean = false) {
	Article({
		classes(FeaturedProjectStyle.card)
		if (hero) classes(FeaturedProjectStyle.heroCard)
	}) {
		Div({
			classes(FeaturedProjectStyle.thumbnail)
		}) {
			Img(localImage(project.image), "${project.name} screenshot") {
				attr("loading", "lazy")
				attr("decoding", "async")
			}
		}

		Div({
			classes(FeaturedProjectStyle.content)
		}) {
			Div({
				classes(FeaturedProjectStyle.header)
			}) {
				H3 { Text(project.name) }

				stars?.let {
					Span({
						classes(FeaturedProjectStyle.stars)
					}) {
						I(FontAwesomeType.SOLID, "star")
						Text(it.toString())
					}
				}
			}

			P({
				classes(FeaturedProjectStyle.tagline)
			}) {
				Text(project.tagline)
			}

			Div({
				classes(FeaturedProjectStyle.tags)
			}) {
				project.tags.forEach { tag ->
					Span({ classes(FeaturedProjectStyle.tag) }) { Text(tag) }
				}
			}

			Div({
				classes(FeaturedProjectStyle.links)
			}) {
				project.liveUrl?.let { url ->
					A(url, {
						classes(FeaturedProjectStyle.projectLink, FeaturedProjectStyle.primaryLink)
					}) {
						I(FontAwesomeType.SOLID, "arrow-up-right-from-square")
						Text("Live")
					}
				}

				A("https://github.com/${project.repository}", {
					classes(FeaturedProjectStyle.projectLink)
				}) {
					I(FontAwesomeType.BRAND, "github")
					Text("Source")
				}
			}
		}
	}
}

object FeaturedProjectStyle : StyleSheet() {
	const val CARD_BACKGROUND = "#181820"
	const val THUMBNAIL_BACKGROUND = "#0E0E12"

	val grid by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns {
			repeat(GridEntry.Repeat.Auto.Type.AutoFit) {
				minmax(19.cssRem, 1.fr)
			}
		}
		gap(1.5.cssRem)
		width(100.percent)
		textAlign(TextAlign.Start)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val card by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		overflow(Overflow.Hidden)
		borderRadius(0.9.cssRem)
		border(1.px, LineStyle.Solid, Color.transparent)
		gradientBorderBackground(Color(CARD_BACKGROUND))
		boxShadow("0 0 15px rgba(0, 212, 255, 0.08)")

		transitions {
			properties("transform", "box-shadow") {
				duration(0.3.s)
				timingFunction(TransitionTimingFunction.EaseInOut)
			}
		}

		hover(self) style {
			transform { translateY((-6).px) }
			boxShadow("0 0 30px rgba(255, 0, 128, 0.25)")
		}
	}

	/** The first project gets the full row on desktop, screenshot beside the text instead of above it. */
	val heroCard by style {
		gridColumn(1, -1)

		media(mediaMinWidth(AppStyle.mobileFirstBreak)) {
			self {
				flexDirection(FlexDirection.Row)
				alignItems(AlignItems.Stretch)
			}

			// flex-shrink stays on: without it the 960px screenshot keeps its intrinsic width and squeezes the text column.
			desc(self, className("thumbnail")) style {
				flex("1 1 58%")
				minWidth(0.px)
				property("aspect-ratio", "auto")
			}

			desc(self, className("content")) style {
				flex("1 1 42%")
				minWidth(0.px)
				justifyContent(JustifyContent.Center)
				padding(2.cssRem)
			}

			desc(self, className("tagline")) style {
				fontSize(1.05.cssRem)
			}
		}
	}

	val thumbnail by style {
		backgroundColor(Color(THUMBNAIL_BACKGROUND))
		property("aspect-ratio", "16 / 10")
		overflow(Overflow.Hidden)

		"img" {
			width(100.percent)
			height(100.percent)
			objectFit(ObjectFit.Cover)
			property("object-position", "top center")
			display(DisplayStyle.Block)
		}
	}

	val content by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.75.cssRem)
		padding(1.25.cssRem)
		flex(1)
	}

	val header by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.SpaceBetween)
		gap(0.75.cssRem)

		"h3" {
			margin(0.px)
			fontSize(1.25.cssRem)
			color(Color.white)
		}
	}

	val stars by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		gap(0.35.cssRem)
		color(Color("#FFE218"))
		fontSize(0.95.cssRem)
		fontWeight(600)
		whiteSpace(WhiteSpace.NoWrap)
	}

	val tagline by style {
		margin(0.px)
		color(Color("#FFFFFFCC"))
		fontSize(0.95.cssRem)
		lineHeight(1.5.number)
	}

	val tags by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.4.cssRem)
	}

	val tag by style {
		backgroundColor(Color("#FFFFFF12"))
		borderRadius(1.cssRem)
		color(Color("#FFFFFFDD"))
		fontSize(0.75.cssRem)
		padding(0.25.cssRem, 0.7.cssRem)
	}

	val links by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.6.cssRem)
		marginTop(autoLength)
		paddingTop(0.4.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val projectLink by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		gap(0.4.cssRem)
		borderRadius(0.45.cssRem)
		color(Color.white)
		fontSize(0.9.cssRem)
		fontWeight(600)
		padding(0.45.cssRem, 0.9.cssRem)
		border(1.px, LineStyle.Solid, Color("#FFFFFF25"))

		transitions {
			properties("background-color", "border-color") {
				duration(0.25.s)
			}
		}

		hover(self) style {
			backgroundColor(Color("#FFFFFF15"))
			borderColor(Color("#FFFFFF50"))
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val primaryLink by style {
		backgroundColor(Color("#00D4FF20"))
		borderColor(Color("#00D4FF60"))

		hover(self) style {
			backgroundColor(Color("#00D4FF35"))
			borderColor(Color("#00D4FF"))
		}
	}
}

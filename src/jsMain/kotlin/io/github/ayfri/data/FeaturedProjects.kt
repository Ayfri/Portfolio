package io.github.ayfri.data

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.AppStyle
import io.github.ayfri.components.A
import io.github.ayfri.components.FontAwesomeType
import io.github.ayfri.components.I
import io.github.ayfri.localImage
import io.github.ayfri.tint
import io.github.ayfri.utils.gradientBorderBackground
import io.github.ayfri.utils.neonEdgeGradient
import io.github.ayfri.utils.revealOnScroll
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
		tagline = "The whole Pokémon TCG catalogue, English and Japanese, with cards and prices refreshed every day, filters and the collection you actually own.",
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
		tags = listOf("Svelte 5", "TypeScript", "Supabase"),
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

/**
 * The whole card opens the project page, or the GitHub repository when the repo is missing from the snapshot
 * ([stars] is null then): the title link stretches over the card, the Live and Source links sit above it.
 */
@Composable
fun FeaturedProjectCard(project: FeaturedProject, stars: Int?, hero: Boolean = false) {
	val repositoryUrl = "https://github.com/${project.repository}"

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
				H3 {
					A(if (stars != null) "/projects/${project.repository}/" else repositoryUrl, {
						classes(FeaturedProjectStyle.cardLink)
					}) {
						Text(project.name)
					}
				}

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
					Span({ classes(AppStyle.tag) }) { Text(tag) }
				}
			}

			Div({
				classes(FeaturedProjectStyle.links)
			}) {
				project.liveUrl?.let { url ->
					A(url, {
						classes(AppStyle.button, FeaturedProjectStyle.projectLink, FeaturedProjectStyle.primaryLink)
					}) {
						I(FontAwesomeType.SOLID, "arrow-up-right-from-square")
						Text("Live")
					}
				}

				A(repositoryUrl, {
					classes(AppStyle.button, FeaturedProjectStyle.projectLink)
				}) {
					I(FontAwesomeType.BRAND, "github")
					Text("Source")
				}
			}
		}
	}
}

object FeaturedProjectStyle : StyleSheet() {
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

	/** Neutral at rest, the neon logo edge, a violet halo and a slow screenshot zoom only show up on hover. */
	val card by style {
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		overflow(Overflow.Hidden)
		position(Position.Relative)
		borderRadius(0.9.cssRem)
		border(1.px, LineStyle.Solid, Color.transparent)
		gradientBorderBackground(Color(AppStyle.CARD_BACKGROUND))
		revealOnScroll()

		transitions {
			properties("box-shadow") { duration(0.3.s) }
		}

		hover(self) style {
			gradientBorderBackground(Color(AppStyle.CARD_BACKGROUND), neonEdgeGradient)
			boxShadow("0 12px 40px -18px ${tint(AppStyle.VIOLET_COLOR, 60)}")
		}

		desc(hover(self), "img") style {
			property("scale", "1.04")
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
		backgroundColor(Color(AppStyle.CONTENT_BACKGROUND_COLOR))
		borderBottom(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
		property("aspect-ratio", "16 / 10")
		overflow(Overflow.Hidden)

		"img" {
			width(100.percent)
			height(100.percent)
			objectFit(ObjectFit.Cover)
			property("object-position", "top center")
			property("transition", "scale .6s cubic-bezier(.2, .8, .2, 1)")
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
		alignItems(AlignItems.Center)
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		display(DisplayStyle.Flex)
		fontFamily(AppStyle.MONO_FONT_FAMILY, "monospace")
		fontSize(0.85.cssRem)
		gap(0.35.cssRem)
		whiteSpace(WhiteSpace.NoWrap)
	}

	val tagline by style {
		margin(0.px)
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		fontSize(0.95.cssRem)
		lineHeight(1.55.number)
	}

	val tags by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.4.cssRem)
	}

	/** Stays above the stretched title link so both buttons keep their own targets. */
	val links by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.6.cssRem)
		marginTop(autoLength)
		paddingTop(0.4.cssRem)
		position(Position.Relative)
		zIndex(1)
	}

	val cardLink by style {
		color(Color.white)

		self + after style {
			content("")
			position(Position.Absolute)
			property("inset", "0")
		}

		hover(self) style {
			color(Color(AppStyle.ACCENT_COLOR))
		}
	}

	/** Compact [AppStyle.button]; the live link only differs by its cyan label, a filled button per card would be too loud. */
	val projectLink by style {
		fontSize(0.9.cssRem)
		padding(0.4.cssRem, 0.85.cssRem)
	}

	val primaryLink by style {
		color(Color(AppStyle.LINK_COLOR))
	}
}

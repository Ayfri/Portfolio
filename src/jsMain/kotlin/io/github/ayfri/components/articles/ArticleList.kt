package io.github.ayfri.components.articles

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.navigation.Anchor
import io.github.ayfri.*
import io.github.ayfri.data.ArticleEntry
import io.github.ayfri.markdownParagraph
import js.date.Date
import js.intl.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*


@Composable
fun ArticleList(entries: List<ArticleEntry>) {
	Ul({
		classes(ArticleListStyle.articleList)
	}) {
		entries.forEach { entry ->
			ArticleEntry(entry)
		}
	}
}

@Composable
fun ArticleEntry(entry: ArticleEntry) {
	Li({
		classes(ArticleListStyle.articleEntry)
	}) {
		Anchor(href = entry.path.ensureSuffix("/")) {
			Div({
				classes(ArticleListStyle.articleContent)
			}) {
				Div({
					classes(ArticleListStyle.articleHeader)
				}) {
					H2 {
						Text(entry.title)
					}

					Div({
						classes(ArticleListStyle.articleMeta)
					}) {
						// Format and display date
						val date = try {
							val jsDate = Date(entry.date)
							jsDate.toLocaleDateString("en-US", DateTimeFormatOptions(
								year = YearFormat.numeric,
								month = MonthFormat.long,
								day = DayFormat.numeric
							))
						} catch (e: Exception) {
							entry.date.split("T")[0]
						}

						Span({
							classes(ArticleListStyle.metaItem)
						}) {
							I({
								classes("fas", "fa-calendar-alt")
							})
							Text(" $date")
						}

						// Reading time estimate
						val readingTime = calculateReadingTime(entry.content)
						Span({
							classes(ArticleListStyle.metaItem)
						}) {
							I({
								classes("fas", "fa-clock")
							})
							Text(" $readingTime min read")
						}

						// Check if article was updated
						if (entry.date != entry.dateModified) {
							Span({
								classes(ArticleListStyle.metaItem, ArticleListStyle.updated)
							}) {
								I({
									classes("fas", "fa-sync-alt")
								})
								Text(" Updated")
							}
						}
					}
				}

				P({
					classes(ArticleListStyle.articleDescription)
					markdownParagraph(entry.desc)
				})

				if (entry.keywords.isNotEmpty()) {
					Div({
						classes(ArticleListStyle.keywordsContainer)
					}) {
						entry.keywords.forEach { keyword ->
							Span({
								classes(ArticleListStyle.keyword)
							}) {
								Text(keyword)
							}
						}
					}
				}

				Div({
					classes(ArticleListStyle.readMore)
				}) {
					Span {
						Text("Read more")
						I({
							classes("fas", "fa-arrow-right")
							style {
								marginLeft(0.5.cssRem)
							}
						})
					}
				}
			}
		}
	}
}

object ArticleListStyle : StyleSheet() {
	// Updated color constants to match design system
	const val ARTICLE_BACKGROUND_COLOR = "#1A1225"
	const val ARTICLE_HOVER_BACKGROUND_COLOR = "#1E1535"
	const val PARAGRAPH_TEXT_COLOR = "#FFFFFFCC"
	const val META_TEXT_COLOR = "#FFFFFF99"
	const val UPDATED_TEXT_COLOR = "#6EBAE7"
	const val KEYWORD_BACKGROUND_COLOR = "#FFFFFF15"
	const val KEYWORD_TEXT_COLOR = "#FFFFFFDD"
	const val KEYWORD_HOVER_BACKGROUND_COLOR = "#FFFFFF25"
	const val READ_MORE_COLOR = "#6EBAE7"

	val title by style {
		backgroundClip(BackgroundClip.Text)
		backgroundImage(linearGradient(45.deg) {
			add(Color("#00D4FF"))
			add(Color("#FF0080"))
		})
		fontSize(3.cssRem)
		marginBottom(1.5.cssRem)
		textAlign(TextAlign.Center)
		property("-webkit-background-clip", "text")
		property("-webkit-text-fill-color", "transparent")
		property("text-shadow", "0 0 20px rgba(0, 212, 255, 0.5)")
	}

	val articleList by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.5.cssRem)
		listStyle(ListStyle.None)
		padding(0.px)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val articleEntry by style {
		backgroundColor(Color(ARTICLE_BACKGROUND_COLOR))
		backgroundImage("""
			linear-gradient(${ARTICLE_BACKGROUND_COLOR}, ${ARTICLE_BACKGROUND_COLOR}) padding-box,
			linear-gradient(45deg, #00D4FF, #FF0080) border-box
		""")
		border(2.px, LineStyle.Solid, Color.transparent)
		borderRadius(1.cssRem)
		boxShadow("0 0 30px rgba(0, 212, 255, 0.15)")
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)

		animation(AnimationsStyle.appearFromBelow) {
			fillMode(AnimationFillMode.Forwards)
			duration(0.6.s)
			timingFunction(AnimationTimingFunction.EaseInOut)
		}
		opacity(0)

		transitions {
			defaultDuration(0.3.s)
			defaultTimingFunction(AnimationTimingFunction.EaseInOut)
			properties("background-color", "transform", "box-shadow")
		}

		self + hover style {
			backgroundColor(Color(ARTICLE_HOVER_BACKGROUND_COLOR))
			boxShadow("0 0 40px rgba(255, 0, 128, 0.3)")
			transform {
				translateY((-5).px)
			}
		}

		"p" {
			color(Color(PARAGRAPH_TEXT_COLOR))
			margin(0.px)
		}

		"a" {
			padding(0.px)
		}

		linkStyle(self)
	}

	val articleContent by style {
		padding(1.5.cssRem)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
	}

	val articleHeader by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.8.cssRem)

		"h2" {
			backgroundClip(BackgroundClip.Text)
			backgroundImage(linearGradient(45.deg) {
				add(Color("#00D4FF"))
				add(Color("#FF0080"))
			})
			margin(0.px)
			fontSize(1.8.cssRem)
			property("-webkit-background-clip", "text")
			property("-webkit-text-fill-color", "transparent")
		}
	}

	val articleMeta by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(1.cssRem)
		color(Color(META_TEXT_COLOR))
		fontSize(0.9.cssRem)
	}

	val metaItem by style {
		display(DisplayStyle.LegacyInlineFlex)
		alignItems(AlignItems.Center)

		"i" {
			marginRight(0.3.cssRem)
			fontSize(0.9.em)
		}
	}

	val updated by style {
		color(Color(UPDATED_TEXT_COLOR))
	}

	val articleDescription by style {
		lineHeight(1.6.em)
	}

	val keywordsContainer by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.5.cssRem)
		marginTop(0.5.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val keyword by style {
		backgroundColor(Color(KEYWORD_BACKGROUND_COLOR))
		borderRadius(1.cssRem)
		padding(0.4.cssRem, 0.8.cssRem)
		fontSize(0.8.cssRem)
		color(Color(KEYWORD_TEXT_COLOR))

		transitions {
			defaultDuration(0.2.s)
			properties("background-color", "transform")
		}

		hover style {
			backgroundColor(Color(KEYWORD_HOVER_BACKGROUND_COLOR))
			transform {
				scale(1.05)
			}
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val readMore by style {
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.FlexEnd)
		marginTop(1.cssRem)
		color(Color(READ_MORE_COLOR))
		fontSize(0.95.cssRem)
		fontWeight(600)

		transitions {
			properties("transform") {
				duration(0.2.s)
			}
		}

		self + hover style {
			transform {
				translateX(5.px)
			}
		}
	}

	// Responsive styles
	init {
		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			articleContent style {
				padding(1.5.cssRem)
			}

			articleHeader style {
				"h2" {
					fontSize(1.5.cssRem)
				}
			}
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			articleContent style {
				padding(1.2.cssRem)
			}

			keywordsContainer style {
				marginTop(0.8.cssRem)
			}
		}
	}
}

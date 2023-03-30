package pages

import A
import P
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import data.GitHubRepository
import data.data
import localImage
import markdownParagraph
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import style.AppStyle
import style.utils.*

data class Language(
	val name: String,
	val since: Int,
	val learnedFor: String,
	val nowUsing: String,
	val level: Int,
	val description: String,
	val iconUrl: String,
	val githubProjects: List<String> = listOf(),
	val schoolProjects: List<String> = listOf(),
)

data class Skill(
	val language: Language,
	var githubProjects: MutableList<GitHubRepository> = mutableListOf(),
	var schoolProjects: MutableList<GitHubRepository> = mutableListOf(),
) {
	@Composable
	fun Display() {
		Div({
			classes("top")
		}) {
			Div({
				classes("info")
			}) {
				Div({
					classes("left")
				}) {
					Img(language.iconUrl, alt = language.name)
					P(language.name, AppStyle.monoFont)
				}


				P({
					val learnedAndNowUserFor = if (language.learnedFor == language.nowUsing) "<br>Using for: ${language.learnedFor}"
					else "<br>Learned for: ${language.learnedFor}<br>Now using: ${language.nowUsing}"

					markdownParagraph(
						"""
							Since: ${language.since}$learnedAndNowUserFor
							Level: ${"<i class='fa-solid fa-star'></i> ".repeat(language.level)}
						""".trimIndent(), true
					)
				})
			}

			P({
				markdownParagraph(language.description, false, AppStyle.monoFont, "description")
			})
		}

		Div({
			classes("bottom")
		}) {
			@Composable
			fun section(name: String, list: List<GitHubRepository>) {
				if (list.isEmpty()) return

				H4({
					classes(AppStyle.monoFont)
				}) {
					Text(name)
				}

				Ul {
					list.distinctBy { it.fullName }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }).forEach {
						Li {
							A(it.htmlUrl, it.name)
						}
					}
				}
			}

			val githubNotSchoolProjects = githubProjects.filter { project -> schoolProjects.none { project.fullName == it.fullName } }
			val (contributedProjects, ownProjects) = githubNotSchoolProjects.partition { it.fork || it.owner.login != "Ayfri" }
			section("GitHub Projects:", ownProjects)
			section("Contributed Projects:", contributedProjects)
			section("School Projects:", schoolProjects)
		}
	}

	@Composable
	fun DisplaySimple() {
		Img(language.iconUrl, alt = language.name)
		P(language.name, AppStyle.monoFont)
	}
}

fun devIcon(name: String, suffix: String = "original") = "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/$name/$name-$suffix.svg"

val skills = listOf(
	Language(
		name = "Bash",
		since = 2022,
		learnedFor = "Creating scripts on Linux.",
		nowUsing = "Creating scripts on Linux.",
		level = 2,
		description = """
			Bash is a sh-compatible command language interpreter that executes commands read from the standard input or from a file.
			bash also incorporates useful features from the Korn and C shells (ksh and csh).
		""".trimIndent(),
		iconUrl = devIcon("bash")
	),
	Language(
		name = "C++",
		since = 2016,
		learnedFor = "Testing low-level programmation.",
		nowUsing = "Low-level games.",
		level = 4,
		description = """
			C++ is an object-oriented programming (OOP) language that is viewed by many as the best language for creating large-scale applications.
			C++ is a superset of the C language. A related programming language, Java, is based on C++ but optimized for the distribution of program objects in a network such as the Internet.
		""".trimIndent(),
		iconUrl = devIcon("cplusplus"),
		schoolProjects = listOf(
			"Ayfri/Cpp-TP1",
			"Ayfri/Cpp-TP2",
			"Ayfri/Cpp-TP3",
			"Ayfri/Cpp-TP4",
			"Ayfri/Cpp-TP5"
		)
	),
	Language(
		name = "C#",
		since = 2017,
		learnedFor = "Creating games in Unity.",
		nowUsing = "Creating games in Unity.",
		level = 3,
		description = """
			C# (pronounced "C-sharp") is an object-oriented programming language from Microsoft that aims to combine the computing power of C++ with the programming ease of Visual Basic.
			C# is based on C++ and contains features similar to those of Java.
		""".trimIndent(),
		iconUrl = devIcon("csharp")
	),
	Language(
		name = "CSS",
		since = 2017,
		learnedFor = "Styling websites.",
		nowUsing = "Styling websites.",
		level = 5,
		description = """
			Cascading Style Sheets (CSS) is a stylesheet language used to describe the presentation of a document written in HTML or XML (including XML dialects such as SVG, MathML or XHTML).
			CSS describes how elements should be rendered on screen, on paper, in speech, or on other media.
		""".trimIndent(),
		iconUrl = devIcon("css3"),
		schoolProjects = listOf(
			"antaww/game-overflow",
			"Ayfri/Cat-in-Space",
			"Ayfri/Challenge-Discovery",
			"Ayfri/Hangman-Web",
			"Ayfri/HTML-Menu",
			"Ayfri/HTML-TP1",
			"Ayfri/Infra-Website",
			"Ayfri/VersionCraft",
			"B-One-Ynov/Extranet-Ynov",
		),
	),
	Language(
		name = "GoLang",
		since = 2021,
		learnedFor = "Creating websites/APIs.",
		nowUsing = "Creating websites/APIs.",
		level = 4,
		description = """
			It is a statically typed language with syntax loosely derived from that of C, adding garbage collection, type safety, some dynamic-typing capabilities, additional built-in types such as variable-length arrays & key-value maps, and a large standard library.
		""".trimIndent(),
		iconUrl = devIcon("go", "original-wordmark"),
		schoolProjects = listOf(
			"Ayfri/Cat-in-Space",
			"Ayfri/Challenge-Go",
			"Ayfri/hangman-classic",
			"Ayfri/Hangman-Web",
			"Ayfri/Project-Red",
		),
		githubProjects = listOf(
			"Ayfri/VersionCraft"
		)
	),
	Language(
		name = "Godot",
		since = 2023,
		learnedFor = "Creating games.",
		nowUsing = "Creating games.",
		level = 3,
		description = """
			Godot is a feature-packed, cross-platform game engine to create 2D and 3D games from a unified interface.
			Godot provides a comprehensive set of common tools, so you can just focus on making your game without reinventing the wheel.
		""".trimIndent(),
		iconUrl = devIcon("godot"),
		schoolProjects = listOf(
			"Ayfri/Cat-aclysm-Claw-of-the-Dead",
		),
	),
	Language(
		name = "Java",
		since = 2016,
		learnedFor = "Minecraft mods/Bots/Games.",
		nowUsing = "Backend of websites.",
		level = 4,
		description = """
			Java is a widely used object-oriented programming language and software platform that runs on billions of devices, including notebook computers, mobile devices, gaming consoles, medical devices and many others.
			The rules and syntax of Java are based on the C and C++ languages.
		""".trimIndent(),
		iconUrl = devIcon("java"),
		schoolProjects = listOf(
			"Ayfri/Java-TP1",
			"Ayfri/Java-TP2",
			"Ayfri/Java-TP3",
		)
	),
	Language(
		name = "JavaScript",
		since = 2018,
		learnedFor = "Dynamic websites, bots/games/scripts/cli.",
		nowUsing = "Dynamic websites without Kotlin.",
		level = 5,
		description = """
			JavaScript (often shortened to JS) is a lightweight, interpreted, object-oriented language with first-class functions, and is best known as the scripting language for Web pages, but it's used in many non-browser environments as well.
		""".trimIndent(),
		iconUrl = devIcon("javascript"),
		githubProjects = listOf("Les-Laboratoires-JS/tips"),
		schoolProjects = listOf(
			"antaww/game-overflow",
			"Ayfri/Cat-in-Space",
			"Ayfri/Challenge-Discovery",
			"Ayfri/Hangman-Web",
			"Ayfri/Infra-Website",
			"Ayfri/TP-JS",
			"Ayfri/VersionCraft",
		)
	),
	Language(
		name = "HTML",
		since = 2017,
		learnedFor = "Creating websites.",
		nowUsing = "Creating websites.",
		level = 5,
		description = """
			HyperText Markup Language (HTML) is the basic scripting language used by web browsers to render pages on the World Wide Web. HyperText allows a user to click a link and be redirected to a new page referenced by that link.
		""".trimIndent(),
		iconUrl = devIcon("html5"),
		githubProjects = listOf(
			"Ayfri/atom-clicker"
		),
		schoolProjects = listOf(
			"antaww/game-overflow",
			"Ayfri/Cat-in-Space",
			"Ayfri/Challenge-Discovery",
			"Ayfri/Hangman-Web",
			"Ayfri/HTML-Menu",
			"Ayfri/HTML-TP1",
			"Ayfri/Infra-Website",
			"Ayfri/VersionCraft",
		)
	),
	Language(
		name = "Kotlin",
		since = 2020,
		learnedFor = "Improving Java projects.",
		nowUsing = "Pretty much everything.",
		level = 5,
		description = """
			Kotlin is a static type, object-oriented programing (OOP) language that is interoperable with the Java virtual machine, Java libraries and Android.
			Kotlin saves time for developers as the less verbose language provides briefer and less redundant code. It can be compiled into JavaScript or an LLVM encoder.
		""".trimIndent(),
		iconUrl = localImage("Kotlin Logo.svg"),
		schoolProjects = listOf(
			"Ayfri/Ayfri.github.io",
			"HelysioFR/FallenKingdom",
		)
	),
	Language(
		name = "MySQL",
		since = 2019,
		learnedFor = "Storing data other than in a JSON.",
		nowUsing = "Storing data efficiently.",
		level = 4,
		description = """
			MySQL is an Oracle-backed open source relational database management system (RDBMS) based on Structured Query Language (SQL).
			MySQL runs on virtually all platforms, including Linux, UNIX and Windows.
		""".trimIndent(),
		iconUrl = devIcon("mysql"),
		githubProjects = listOf(
			"Galileo-Bot/galileo",
			"Galileo-Bot/Rocket-Pub-Manager"
		),
		schoolProjects = listOf(
			"antaww/game-overflow",
			"B-One-Ynov/Extranet-Ynov",
		)
	),
	Language(
		name = "PHP",
		since = 2022,
		learnedFor = "Creating websites/plugins with WordPress.",
		nowUsing = "Creating websites/plugins with WordPress.",
		level = 4,
		description = """
			PHP is a server side scripting language that is embedded in HTML. It is used to manage dynamic content, databases, session tracking, even build entire e-commerce sites.
			It is integrated with a number of popular databases, including MySQL, PostgreSQL, Oracle, Sybase, Informix, and Microsoft SQL Server.
		""".trimIndent(),
		iconUrl = devIcon("php")
	),
	Language(
		name = "Python",
		since = 2014,
		learnedFor = "Trying programmation.",
		nowUsing = "Creating scripts/CLI.",
		level = 3,
		description = """
			Python is a computer programming language often used to build websites and software, automate tasks, and conduct data analysis.
			Python is a general-purpose language, meaning it can be used to create a variety of different programs and isn't specialized for any specific problems.
		""".trimIndent(),
		iconUrl = devIcon("python"),
		schoolProjects = listOf(
			"Ayfri/Python-TP1",
			"Ayfri/Python-TP2",
			"Ayfri/Python-TP3"
		)
	),
	Language(
		name = "TypeScript",
		since = 2019,
		learnedFor = "Improving JavaScript projects.",
		nowUsing = "Bots & web games.",
		level = 4,
		description = """
			TypeScript is an open-source, object-oriented language developed and maintained by Microsoft, licensed under Apache 2 license.
			TypeScript extends JavaScript by adding data types, classes and other object-oriented features with type-checking. It is a typed superset of JavaScript that compiles to plain JavaScript.
		""".trimIndent(),
		iconUrl = devIcon("typescript")
	),
).map(::Skill)

@Composable
fun Skills() {
	Style(SkillsStyle)

	val repos = remember { mutableStateListOf<GitHubRepository>() }

	if (repos.isEmpty()) {
		LaunchedEffect(Unit) {
			data.then { gitHubData ->
				repos += gitHubData.repos.filter { it.language != null }
			}
		}
	}

	Div({
		classes(AppStyle.sections, SkillsStyle.skills)
	}) {
		H1({
			classes(AppStyle.monoFont, AppStyle.title)
		}) {
			Text("My Skills:")
		}

		Section({
			classes(SkillsStyle.skillsList)
		}) {
			skills.sortedWith(compareByDescending<Skill> { it.language.level }.thenByDescending { it.language.since }.thenBy { it.language.name })
				.forEach { skill ->
					if (skill.githubProjects.isEmpty()) {
						skill.githubProjects += repos.filter {
							it.language!!.equals(skill.language.name, true)
						} + repos.filter { it.fullName in skill.language.githubProjects }
					}

					if (skill.schoolProjects.isEmpty()) {
						skill.schoolProjects += repos.filter {
							it.language!!.equals(skill.language.name, true) && it.description?.contains("school") == true
						} + repos.filter { it.fullName in skill.language.schoolProjects }
					}

					Div({
						id(skill.language.name)
						classes(SkillsStyle.skill)
					}) {
						skill.Display()
					}
				}
		}
	}
}

object SkillsStyle : StyleSheet() {
	const val skillsBackgroundColor = "#363636"
	const val skillBackgroundColor = "#141414"

	val skills by style {
		backgroundColor(Color(skillsBackgroundColor))
	}

	val skillsList by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns(repeat("auto-fill", minmax(22.5.cssRem, 1.fr)))
		gap(2.cssRem)


		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			self {
				display(DisplayStyle.Flex)
				flexDirection(FlexDirection.Column)
			}
		}
	}

	val skill by style {
		val borderGradient = linearGradient(135.deg) {
			stop(Color("#701CB3"))
			stop(Color("#534BAB"))
			stop(Color("#A24598"))
			stop(Color("#033596"))
		}

		borderRadius(.8.cssRem)
		border {
			color(Color.transparent)
			style(LineStyle.Solid)
			width(.1.cssRem)
		}

		background("""${
			linearGradient {
				stop(Color(skillBackgroundColor))
				stop(Color(skillBackgroundColor))
			}
		} padding-box,
			$borderGradient border-box""")

		color(Color.white)

		"img" {
			size(3.5.cssRem)
			borderRadius(.3.cssRem)
		}

		"p" {
			fontSize(.92.cssRem)
		}

		child(self, type("div")) + not(empty) style {
			padding(max(1.cssRem, 1.5.vw))
		}

		className("top") style {
			textAlign(TextAlign.Center)

			group(className("description"), desc(className("description"), type("p"))) style {
				margin(1.2.cssRem, 0.px, 0.px)
			}

			className("info") style {
				display(DisplayStyle.Flex)
				flexDirection(FlexDirection.Row)
				alignItems(AlignItems.Center)
				gap(.8.cssRem)

				textAlign(TextAlign.Start)

				className("left") style {
					backgroundColor(Color("#00000070"))
					borderRadius(.5.cssRem)
					padding(.7.cssRem)
					textAlign(TextAlign.Center)
					size(fitContent)

					"p" {
						margin(.5.cssRem, 0.px, 0.px)
					}
				}

				child(self, type("p")) style {
					fontWeight(700)
					margin(0.px)
					lineHeight(1.5.cssRem)

					"p" {
						margin(0.px)
					}

					"i" {
						color(Color("#FFE547"))
					}
				}
			}
		}

		className("bottom") style {
			self + not(empty) style {
				borderTop {
					style(LineStyle.Solid)
					width(.1.cssRem)
				}
				borderImageSource(borderGradient)
				borderImageSlice(1)
			}

			"h4" {
				margin(0.px)

				self + nthOfType(2.n) style {
					marginTop(1.2.cssRem)
				}
			}

			"ul" {
				paddingLeft(2.cssRem)

				"li" + selector("::marker") style {
					fontSize(.8.cssRem)
				}
			}

			"a" {
				color(Color(AppStyle.linkColor))
				textDecoration("none")

				hover {
					color(Color(AppStyle.linkHoverColor))
					textDecoration("underline")
				}
			}
		}

		media(mediaMaxWidth(AppStyle.mobileSecondBreak)) {
			self {
				desc(className("description"), type("p")) style {
					fontSize(.85.cssRem)
				}
			}
		}

		media(mediaMaxWidth(AppStyle.mobileFourthBreak)) {
			self {
				desc(className("top"), className("info")) style {
					flexDirection(FlexDirection.Column)

					child(self, type("p")) style {
						alignSelf(AlignSelf.Start)
					}

					className("left") style {
						padding(.8.cssRem, 1.5.cssRem)
					}
				}
			}
		}
	}
}

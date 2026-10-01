package io.github.ayfri.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.max
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.icons.lucide.LucideGitBranch
import com.varabyte.kobweb.silk.components.icons.lucide.LucideGraduationCap
import com.varabyte.kobweb.silk.components.icons.lucide.LucideStar
import io.github.ayfri.AppStyle
import io.github.ayfri.components.A
import io.github.ayfri.components.GitHubIcon
import io.github.ayfri.components.Icon
import io.github.ayfri.components.P
import io.github.ayfri.data.GitHubRepository
import io.github.ayfri.data.portfolioData
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.localImage
import io.github.ayfri.markdownParagraph
import io.github.ayfri.utils.gradientBorderBackground
import io.github.ayfri.utils.marker
import io.github.ayfri.utils.n
import io.github.ayfri.utils.revealOnScroll
import io.github.ayfri.utils.size
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.AlignSelf
import org.jetbrains.compose.web.dom.*

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
	/** Language names GitHub reports that belong to this skill, e.g. Godot claiming its GDScript repos. */
	val aliases: List<String> = listOf(),
) {
	fun matches(repoLanguage: String) = repoLanguage.equals(name, true) || aliases.any { repoLanguage.equals(it, true) }
}

/** The snapshot is a build-time constant, so every skill's project list is derived once instead of during composition. */
private val languageRepos by lazy { portfolioData().repos.filter { it.language != null } }

/**
 * Coursework markers actually used across the repos: a `TP` in the name, or a description mentioning school,
 * Ynov, a course, exercices, a hackathon or a Master project. The previous `description.contains("school")`
 * only recognised 6 of them.
 */
private val schoolNameRegex = Regex("""(?:^|[-_])tp[-_]?\d*(?:$|[-_])""", RegexOption.IGNORE_CASE)
private val schoolDescRegex =
	Regex("""\b(school|ynov|course|exercices?|exercises?|tp|hackaton|hackathon|master \d)\b""", RegexOption.IGNORE_CASE)

private val GitHubRepository.isSchoolProject
	get() = schoolNameRegex.containsMatchIn(name) ||
		schoolDescRegex.containsMatchIn(name) ||
		description?.let(schoolDescRegex::containsMatchIn) == true

data class Skill(val language: Language) {
	private val ownRepos by lazy { languageRepos.filter { language.matches(it.language!!) } }

	val githubProjects by lazy { ownRepos + languageRepos.filter { it.fullName in language.githubProjects } }

	val schoolProjects by lazy {
		ownRepos.filter { it.isSchoolProject } + languageRepos.filter { it.fullName in language.schoolProjects }
	}

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
					Img(language.iconUrl, alt = "${language.name} icon")
					H2 {
						Text(language.name)
					}
				}

				Div({
					classes("details")
				}) {
					P({
						val learnedAndNowUserFor = if (language.learnedFor == language.nowUsing) "<br>Using for: ${language.learnedFor}"
						else "<br>Learned for: ${language.learnedFor}<br>Now using: ${language.nowUsing}"

						markdownParagraph("Since: ${language.since}$learnedAndNowUserFor")
					})

					P {
						Text("Level: ")
						repeat(language.level) { LucideStar() }
					}
				}
			}

			P({
				markdownParagraph(language.description, false, "description")
			})
		}

		Div({
			classes("bottom")
		}) {
			@Composable
			fun section(name: String, list: List<GitHubRepository>, icon: Icon) {
				if (list.isEmpty()) return

				H3 {
					icon()
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
			section("GitHub projects", ownProjects) { GitHubIcon() }
			section("Contributions", contributedProjects) { LucideGitBranch() }
			section("School projects", schoolProjects) { LucideGraduationCap() }
		}
	}

	@Composable
	fun DisplaySimple() {
		Img(language.iconUrl, alt = "${language.name} language")
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
		level = 4,
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
			"Ayfri/Cpp-TP1", "Ayfri/Cpp-TP2", "Ayfri/Cpp-TP3", "Ayfri/Cpp-TP4", "Ayfri/Cpp-TP5"
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
		iconUrl = devIcon("csharp"),
		schoolProjects = listOf(
			"Ayfri/AyBreak"
		)
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
			"Ayfri/challenge-48h",
			"Ayfri/Challenge-Discovery",
			"Ayfri/Hangman-Web",
			"Ayfri/HTML-Menu",
			"Ayfri/HTML-TP1",
			"Ayfri/Infra-Website",
			"Ayfri/Pokestore",
			"Ayfri/VersionCraft",
			"B-One-Ynov/Extranet-Ynov",
		),
	),
	Language(
		name = "Go",
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
		level = 4,
		description = """
			Godot is a feature-packed, cross-platform game engine to create 2D and 3D games from a unified interface.
			Godot provides a comprehensive set of common tools, so you can just focus on making your game without reinventing the wheel.
		""".trimIndent(),
		iconUrl = devIcon("godot"),
		aliases = listOf("GDScript"),
		githubProjects = listOf(
			"Cat-aclsym/Cat-aclsym_Claw_of_the_dead",
		),
		schoolProjects = listOf(
			"Ayfri/Cat-aclysm-Claw-of-the-Dead",
			"Cat-aclsym/Cat-aclsym_Claw_of_the_dead",
		),
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
			"Ayfri/challenge-48h",
			"Ayfri/Challenge-Discovery",
			"Ayfri/Hangman-Web",
			"Ayfri/HTML-Menu",
			"Ayfri/HTML-TP1",
			"Ayfri/Infra-Website",
			"Ayfri/Pokestore",
			"Ayfri/VersionCraft",
		)
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
			"Ayfri/challenge-48h",
			"Ayfri/Challenge-Discovery",
			"Ayfri/Hangman-Web",
			"Ayfri/Infra-Website",
			"Ayfri/TP-JS",
			"Ayfri/VersionCraft",
		)
	),
	Language(
		// Named without the "Notebook" so the skill's HTML id stays whitespace-free; the alias matches what GitHub reports.
		name = "Jupyter",
		since = 2024,
		learnedFor = "Machine learning courses.",
		nowUsing = "Prototyping data pipelines and deep learning models.",
		level = 4,
		description = """
			Jupyter notebooks interleave code, its output and prose in one document, so a dataset can be explored, transformed and charted step by step without rerunning the whole script.
			They are the usual place for the exploratory half of a machine learning project, before anything worth keeping is moved into real modules.
		""".trimIndent(),
		iconUrl = devIcon("jupyter"),
		aliases = listOf("Jupyter Notebook"),
		schoolProjects = listOf(
			"Ayfri/UF_Project_B3",
		),
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
		iconUrl = devIcon("kotlin"),
		schoolProjects = listOf(
			"Ayfri/Artificial-Infiltration",
			"Ayfri/MongoDB-ReplicaSet",
			"Ayfri/Portfolio",
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
			"Galileo-Bot/galileo", "Galileo-Bot/Rocket-Pub-Manager"
		),
		schoolProjects = listOf(
			"antaww/game-overflow",
			"B-One-Ynov/Extranet-Ynov",
		)
	),
	Language(
		name = "MCFunction",
		since = 2017,
		learnedFor = "Runnin more efficiently Minecraft commands.",
		nowUsing = "Create fully-fledged Minecraft datapacks.",
		level = 5,
		description = """
			Functions are data pack files, allowing players to run lists of commands.
		""".trimIndent(),
		iconUrl = localImage("minecraft-new.avif"),
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
		iconUrl = devIcon("php"),
		schoolProjects = listOf(
			"Ayfri/Cyber-TP-1",
		)
	),
	Language(
		name = "PowerShell",
		since = 2022,
		learnedFor = "Automating tasks on Windows.",
		nowUsing = "Automating tasks on Windows.",
		level = 3,
		description = """
			PowerShell is a task automation and configuration management framework from Microsoft, consisting of a command-line shell and associated scripting language.
			Unlike most shells, which accept and return text, PowerShell is built on top of the .NET Common Language Runtime (CLR), and accepts and returns .NET objects.
		""".trimIndent(),
		iconUrl = devIcon("powershell")
	),
	Language(
		name = "Python",
		since = 2014,
		learnedFor = "Trying programmation.",
		nowUsing = "Creating AIs, scripts & websites.",
		level = 5,
		description = """
			Python is a computer programming language often used to build websites and software, automate tasks, and conduct data analysis.
			Python is a general-purpose language, meaning it can be used to create a variety of different programs and isn't specialized for any specific problems.
		""".trimIndent(),
		iconUrl = devIcon("python"),
		schoolProjects = listOf(
			"Ayfri/docker-tp",
			"Ayfri/Python-TP1",
			"Ayfri/Python-TP2",
			"Ayfri/Python-TP3",
			"Ayfri/Redis-Cluster",
			"Ayfri/UF_Project_B3"
		)
	),
	Language(
		name = "Svelte",
		since = 2024,
		learnedFor = "Creating web apps.",
		nowUsing = "Creating web apps with SvelteKit.",
		level = 5,
		description = """
			Svelte is a compiler that turns declarative components into imperative JavaScript, so there is no virtual DOM and no framework runtime shipped to the browser.
			Svelte 5 replaces the old reactive statements and stores with runes, an explicit signal-based reactivity model that works the same in components and plain modules.
		""".trimIndent(),
		iconUrl = devIcon("svelte"),
	),
	Language(
		name = "TypeScript",
		since = 2019,
		learnedFor = "Improving JavaScript projects.",
		nowUsing = "Creating typed JS libraries.",
		level = 5,
		description = """
			TypeScript is an open-source, object-oriented language developed and maintained by Microsoft, licensed under Apache 2 license.
			TypeScript extends JavaScript by adding data types, classes and other object-oriented features with type-checking. It is a typed superset of JavaScript that compiles to plain JavaScript.
		""".trimIndent(),
		iconUrl = devIcon("typescript"),
		schoolProjects = listOf(
			"Ayfri/Pokestore",
		)
	),
	Language(
		name = "Redis",
		since = 2023,
		learnedFor = "Caching data.",
		nowUsing = "Caching data.",
		level = 3,
		description = """
			Redis is an open-source, in-memory data structure store, used as a database, cache, and message broker.
			It supports data structures such as strings, hashes, lists, sets, sorted sets with range queries, bitmaps, hyperloglogs, geospatial indexes with radius queries and streams.
		""".trimIndent(),
		iconUrl = devIcon("redis"),
		schoolProjects = listOf(
			"Ayfri/Redis-Cluster",
		)
	)
).map(::Skill)

/** Display order for the skills page and the home page's featured strip; sorting once beats re-sorting per recomposition. */
val skillsByLevel = skills.sortedWith(
	compareByDescending<Skill> { it.language.level }
		.thenByDescending { it.language.since }
		.thenBy { it.language.name }
)

@Page("/skills/index")
@Composable
fun Skills() {
	PageLayout(
		"Skills",
		description = "Technical skills of Pierre Roy (Ayfri): Kotlin, TypeScript, Python, Java, C#, Godot, and more, with real GitHub and school projects for each.",
		keywords = "Kotlin skills, TypeScript developer, Python developer, Java developer, programming languages, tech stack, GitHub projects, Godot developer, MySQL, Redis",
	) {
		Style(SkillsStyle)

		Div({
			classes(AppStyle.sections)
		}) {
			H1({
				classes(AppStyle.title)
			}) {
				Text("Skills")
			}

			Section({
				classes(SkillsStyle.skillsList)
			}) {
				skillsByLevel.forEach { skill ->
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
}

object SkillsStyle : StyleSheet() {
	val skillsList by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns {
			repeat(GridEntry.Repeat.Auto.Type.AutoFill) {
				minmax(22.5.cssRem, 1.fr)
			}
		}
		gap(2.cssRem)

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			self {
				display(DisplayStyle.Flex)
				flexDirection(FlexDirection.Column)
			}
		}
	}

	val skill by style {
		borderRadius(.9.cssRem)
		border(1.px, LineStyle.Solid, Color.transparent)
		gradientBorderBackground(Color(AppStyle.CARD_BACKGROUND))
		color(Color(AppStyle.TEXT_COLOR))
		revealOnScroll()

		"img" {
			size(3.cssRem)
			borderRadius(.4.cssRem)
		}

		"h2" {
			color(Color.white)
			fontSize(1.15.cssRem)
			margin(.5.cssRem, 0.px, 0.px)
		}

		"p" {
			fontSize(.95.cssRem)
		}

		child(self, type("div")) + not(empty) style {
			padding(max(1.2.cssRem, 1.5.vw))
		}

		className("top") style {
			group(className("description"), desc(className("description"), type("p"))) style {
				color(Color(AppStyle.TEXT_MUTED_COLOR))
				margin(1.cssRem, 0.px, 0.px)
			}

			className("info") style {
				display(DisplayStyle.Flex)
				flexDirection(FlexDirection.Row)
				alignItems(AlignItems.Center)
				gap(.8.cssRem)

				textAlign(TextAlign.Start)

				className("left") style {
					minWidth(6.cssRem)
					textAlign(TextAlign.Center)
				}

				className("details") style {
					color(Color(AppStyle.TEXT_MUTED_COLOR))
					lineHeight(1.5.cssRem)

					"p" {
						margin(0.px)
					}

					"svg" {
						color(Color(AppStyle.ACCENT_COLOR))
						property("fill", "currentColor")
						fontSize(.75.cssRem)
						marginRight(.25.em)
					}
				}
			}
		}

		className("bottom") style {
			self + not(empty) style {
				borderTop(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
			}

			"h3" {
				alignItems(AlignItems.Center)
				color(Color.white)
				display(DisplayStyle.Flex)
				fontSize(.95.cssRem)
				gap(.5.cssRem)
				margin(0.px)

				"svg" {
					color(Color(AppStyle.TEXT_MUTED_COLOR))
				}

				self + nthOfType(2.n) style {
					marginTop(1.2.cssRem)
				}
			}

			"ul" {
				paddingLeft(2.cssRem)

				type("li") + marker style {
					fontSize(.8.cssRem)
				}
			}

			"a" {
				color(Color(AppStyle.LINK_COLOR))
				textDecorationLine(TextDecorationLine.None)

				hover {
					color(Color(AppStyle.LINK_HOVER_COLOR))
					textDecorationLine(TextDecorationLine.Underline)
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

					className("details") style {
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

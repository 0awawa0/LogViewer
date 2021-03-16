package ru.awawa

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.http.content.*
import io.ktor.request.*
import kotlinx.coroutines.runBlocking
import kotlinx.html.*
import kotlinx.css.*
import java.io.BufferedInputStream
import java.io.File
import java.io.FileReader
import java.util.*
import kotlin.collections.ArrayList

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    routing {
        static {
            resource("assets/scripts.js", "assets/scripts.js")
        }

        get("/") {
            try {
                call.respond(
                    FreeMarkerContent(
                        "index.ftl",
                        mapOf(
                            "name" to "123",
                            "entries" to emptyList<LogEntry>()
                        )
                    )
                )
            } catch (ex: Exception) {
                println(ex.stackTraceToString())
            }
        }

        post("/") {
            try {
                val tmpDir = File("tmp")
                if (!tmpDir.exists()) tmpDir.mkdir()

                val multipartData = call.receiveMultipart()
                val uuid = UUID.randomUUID().toString()
                multipartData.forEachPart { part ->
                    // if part is a file (could be form item)
                    if (part is PartData.FileItem) {
                        // retrieve file name of upload
                        val file = File("tmp/$uuid")

                        // use InputStream from part to save file
                        part.streamProvider().use { its ->
                            // copy the stream to the file with buffering
                            file.outputStream().buffered().use {
                                // note that this is blocking
                                its.copyTo(it)
                            }
                        }
                    }
                    // make sure to dispose of the part after use to prevent leaks
                    part.dispose()
                }

                val log = FileReader("tmp/$uuid").readLines()
                val entries = LinkedList<LogEntry>()
                var lastColor = COLOR_LOG_TITLE
                val debugRegexp = Regex("^\\d\\d.\\d\\d.\\d\\d\\d\\d \\d\\d:\\d\\d:\\d\\d -> D/")
                val infoRegexp = Regex("^\\d\\d.\\d\\d.\\d\\d\\d\\d \\d\\d:\\d\\d:\\d\\d -> I/")
                val warnRegexp = Regex("^\\d\\d.\\d\\d.\\d\\d\\d\\d \\d\\d:\\d\\d:\\d\\d -> W/")
                val errorRegexp = Regex("^\\d\\d.\\d\\d.\\d\\d\\d\\d \\d\\d:\\d\\d:\\d\\d -> E/")
                val assertRegexp = Regex("^\\d\\d.\\d\\d.\\d\\d\\d\\d \\d\\d:\\d\\d:\\d\\d -> A/")

                var i = 1
                val digitCount = log.size.toString().length
                log.forEach {
                    when {
                        debugRegexp.containsMatchIn(it) -> lastColor = COLOR_DEBUG
                        infoRegexp.containsMatchIn(it) -> lastColor = COLOR_INFO
                        warnRegexp.containsMatchIn(it) -> lastColor = COLOR_WARN
                        errorRegexp.containsMatchIn(it) -> lastColor = COLOR_ERROR
                        assertRegexp.containsMatchIn(it) -> lastColor = COLOR_ASSERT
                    }
                    entries.add(LogEntry(String.format("%0" + digitCount + "d", i), it, lastColor))
                    i++
                }

                File("tmp/$uuid").delete()
                call.respond(
                    FreeMarkerContent(
                        "index.ftl",
                        mapOf(
                            "entries" to entries
                        )
                    )
                )
            } catch (ex: Exception) {
                println(ex.stackTraceToString())
            }
        }

        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.red
                }
                p {
                    fontSize = 2.em
                }
                rule("p.myclass") {
                    color = Color.blue
                }
            }
        }
    }
}

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

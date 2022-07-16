import io.github.aakira.napier.Napier
import io.github.rkbalgi.iso4k.*
import io.github.rkbalgi.iso4k.charsets.encodedString
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.js.button
import kotlinx.html.js.onClickFunction
import org.w3c.dom.*
import kotlin.coroutines.CoroutineContext

val app: Application = Application()
fun main() {


    doInit()

    document.addEventListener("DOMContentLoaded", {
        app.start()
    })


}

class Application : CoroutineScope {

    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    fun start() {
        document.body?.buildView()
    }


    private fun Node.buildView() {


        append {


            div {}.append {
                button {
                    text("ISO Parser")
                    this.onClickFunction = {

                        val htmlDivElement = document.getElementById("parserDiv") as HTMLDivElement
                        if (htmlDivElement.style.display == "block") {
                            htmlDivElement.style.display = "none"
                        } else {
                            htmlDivElement.style.display = "block"
                        }

                    }
                }.style.apply {
                    width = "100px"
                    fontFamily = "inherit"
                }


            }



            val parserDiv = div { this.id = "parserDiv" }
                parserDiv.apply {
                this.style.display = "none"
                this.style.width="600px"
                append {
                    h1 {
                        +"ISO8583 Spec Parser"
                    }

                    div {

                    }.append {

                        h2 { +"Sample Traces" }
                        div {
                            append {
                                ul {
                                    li {
                                        text("")
                                        ul {
                                            li {
                                                text("31313030f02420000000100e000000010000000131363435363739303938343536373132333530303430303030303030303030303030323937373935383132323034f8f4f077fcbd9ffc0dfa6f001072657365727665645f310a9985a28599a5858460f2f0f1f1383738373736323235323531323334e47006f5de8c70b9")
                                            }
                                        }
                                    }

                                }
                            }

                        }
                    }


                    div {

                        style = """
                                                padding: 10px;cursor: move;z-index: 10; background-color: #2196F3;color: #fff;
                                            """.trimIndent()
                        text("Raw Hex Trace")
                    }
                    div {}.append {

                        val hexTrace = textArea(
                            cols = "80",
                            rows = "20"
                        ) {
                        } as HTMLTextAreaElement
                        hexTrace.style.run {
                            backgroundColor = "coral"
                            fontFamily = "inherit"

                            width = "600px"
                        }
                        br { }

                        var button1: HTMLElement? = null
                        var button2: HTMLElement? = null
                        val buttonDiv = div {
                        } as HTMLDivElement


                        buttonDiv.run {

                            style.apply {
                                alignItems = "center"
                                display = "flex"
                                flex = "1"
                                justifyContent = "center"
                                //margin = "auto"
                                width = "500px"

                                append {

                                    button1 = button {
                                        text("Parse")
                                    }.apply {
                                        style.fontFamily = "inherit"
                                        width = "200px"
                                    }
                                    button2 = button {
                                        text("Clear")
                                    }.apply {
                                        style.fontFamily = "'M PLUS 1 Code', sans-serif;"
                                        style.fontFamily = "inherit"
                                        width = "200px"
                                    }
                                }
                            }
                        }

                        val fieldsDiv = div {} as HTMLDivElement


                        button2!!.onclick = {
                            fieldsDiv.childNodes.asList().forEach { fieldsDiv.removeChild(it) }
                        }

                        button1!!.onclick = {
                            val data = fromHexString(hexTrace.value)
                            Napier.d {
                                "Parsing - ${hexTrace.value}"
                            }
                            //parserDiv.style.apply { display="none" }


                            val allFields = mutableListOf<Pair<String, String>>()
                            val isoSpec = Spec.spec("SampleSpec2")!!
                            val msgName = isoSpec.findMessage(data)
                            if (msgName != null) {
                                val msg = isoSpec.message(msgName)?.parse(data)!!
                                val parsedMsg = ParsedMessage(msg)




                                fieldsDiv.append {
                                    div {
                                        id="pdiv"
                                        this.style = """
                                            position: absolute; z-index: 9; background-color: #f1f1f1; border: 1px solid #d3d3d3;
                                        """.trimIndent()
                                        div {
                                            id="pdivheader"
                                            style = """
                                                padding: 10px;cursor: move;z-index: 10; background-color: #2196F3;color: #fff;
                                            """.trimIndent()
                                            text("Parsed Message")
                                        }

                                        ul {
                                            addFields(this, parsedMsg)
                                        }
                                    }

                                }

                            }
                        }
                    }
                }
            }
        }
    }
}


fun addFields(ul: UL, parsedMessage: ParsedMessage) {

    ul.li {
        text("MTI")
        ul {
            li {
                textInput {
                    value = parsedMessage.mti()
                    this.size = "${value.length}"
                    this.style = "color: blue; font-family: inherit;"
                }
            }
        }
    }
    ul.li {
        text("Bitmap")
        ul {
            li {

                textInput {
                    value = parsedMessage.bitmap().bytes().toHexString()
                    this.size = "${value.length}"
                    this.style = "color: blue; font-family: inherit;"
                }

            }

            //add children
            parsedMessage.fields.forEachIndexed() { index: Int, fieldData: FieldData ->
                ul.li {
                    if (fieldData.field.position > 0) {
                        checkBoxInput { this.checked = true }
                    }
                    this.text(
                        "(${
                            if (fieldData.field.position > 0) {
                                fieldData.field.position.toString()
                            } else {
                                ""
                            }
                        }) - ${fieldData.field.name}"
                    )
                    if (index % 2 == 0) {
                        this.style = "background-color: white;"
                    } else {
                        this.style = "background-color: lightgrey;"
                    }
                    ul {
                        val parentUl = this;
                        li {

                            textInput {
                                value = fieldData.encodeToString()
                                this.size = "${value.length}"
                                this.style = "color: blue; font-family: inherit;"
                            }


                            if (index % 2 == 0) {
                                this.style = "background-color: white;"
                            } else {
                                this.style = "background-color: lightgrey;"
                            }


                        }

                        if (fieldData.field.hasChildren()) {
                            add(parentUl, parsedMessage, fieldData.field)
                        }


                    }
                }
            }
        }
    }


}

fun add(parentUl: UL, parsedMessage: ParsedMessage, field: IsoField) {

    field.children?.forEachIndexed() { index, child ->
        parentUl.ul {
            val parentUl2 = this;
            li {
                text(child.name)
            }
            ul {
                li {
                    textInput {
                        value = parsedMessage.msg.get(child.name)?.encodeToString()!!
                        this.size = "${value.length}"
                        this.style = "color: blue; font-family: inherit;"
                    }

                    if (index % 2 == 0) {
                        this.style = "background-color: white;"
                    } else {
                        this.style = "background-color: lightgrey;"
                    }
                }
            }


            if (child.hasChildren()) {
                add(parentUl2, parsedMessage, child)
            }


        }
    }

}

class ParsedMessage(val msg: Message) {

    val fields = mutableListOf<FieldData>()

    init {

        for (i in 1..192) {


            if (i != 1 && i != 65 && i != 129) {
                if (msg.bitmap().isOn(i)) {
                    fields.add(msg.bitmap().get(i)!!)
                }
            }
        }
    }

    fun mti(): String = msg.get("message_type")!!.encodeToString()

    fun bitmap(): IsoBitmap = msg.bitmap()


}

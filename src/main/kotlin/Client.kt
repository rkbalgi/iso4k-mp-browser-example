import io.github.aakira.napier.Napier
import io.github.rkbalgi.iso4k.Spec
import io.github.rkbalgi.iso4k.fromHexString
import io.github.rkbalgi.iso4k.toHexString
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.dom.append
import org.w3c.dom.*
import kotlin.collections.set


val isoSpec = Spec.spec("SampleSpec")

fun main() {
    window.onload = { document.body?.buildView() }
}

fun Node.buildView() {

    append {

        h1 {
            +"ISO8583 Spec Parser"
        }


        div {

            append {

                val hexTrace = textArea(
                    cols = "80",
                    rows = "10"
                ) {

                    this.style {
                        attributes["background-color"] = "red"
                    }


                } as HTMLTextAreaElement

                br { }


                var button1: HTMLElement? = null
                var button2: HTMLElement? = null

                div {


                    append {
                        button1 = button {
                            text("Parse")
                            style { attributes["color"] = "#4CAF50" }


                        }

                        button2 = button {
                            text("Clear")
                            style { attributes["color"] = "#4CAF50" }


                        }
                    }
                    style { attributes["align"] = "center" }
                }

                val fieldsDiv = div {

                } as HTMLDivElement

                button2!!.onclick = {
                    fieldsDiv.childNodes.asList().forEach { fieldsDiv.removeChild(it) }
                }

                button1!!.onclick = {
                    var data = fromHexString(hexTrace.value!!)
                    Napier.d {
                        "Parsing - ${hexTrace.value}"
                    }
                    var allFields = mutableListOf<Pair<String, String>>()
                    var msgName = isoSpec?.findMessage(data)
                    if (msgName != null) {
                        var msg = isoSpec!!.message(msgName)?.parse(data)!!
                        allFields.add(Pair("Message Type", msgName))
                        allFields.add(Pair("MTI", msg.get("message_type")!!.encodeToString()))
                        allFields.add(Pair("Bitmap", msg.bitmap().bytes().toHexString()))

                        for (i in 1..192) {


                            if (i != 1 && i != 65 && i != 129) {
                                if (msg!!.bitmap().isOn(i)) {
                                    allFields.add(
                                        Pair(
                                            "Bit($i) - ${msg.bitmap().get(i)?.field?.name}",
                                            msg.bitmap().get(i)!!.encodeToString()
                                        )
                                    )

                                }
                            }
                        }



                        fieldsDiv.append {
                            ul {
                                allFields.forEach {
                                    li {
                                        this.text(it.first)
                                        ul {
                                            li {
                                                text(it.second)
                                                style { attributes["background-color"] = "red" }
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
}
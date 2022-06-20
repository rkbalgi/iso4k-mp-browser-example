import io.github.aakira.napier.Napier
import io.github.rkbalgi.iso4k.Spec
import io.github.rkbalgi.iso4k.fromHexString
import io.github.rkbalgi.iso4k.toHexString
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.dom.append
import org.w3c.dom.Node


val isoSpec = Spec.spec("SampleSpec")

fun main() {
    window.onload = { document.body?.buildView() }
}

fun Node.buildView() {

    append {

        h1{
            +"ISO8583 Spec Parser"
        }
        val hexTrace = textArea {
            cols = "40"
            rows = "10"
        }

        val button = button {
            this.text("Parse")
            style { attributes["background-color"] = "blue" }


        }

        var fieldsDiv = div {

        }

        button.onclick = {
            var data = fromHexString(hexTrace.innerText)
            Napier.d {
                "Parsing - ${hexTrace.innerText}"
            }
            var allFields = mutableListOf<Pair<String, String>>()
            var msgName = isoSpec?.findMessage(data)
            if (msgName != null) {
                var msg = isoSpec!!.message(msgName)?.parse(data)!!
                allFields.add(Pair("Message Type", msgName))
                allFields.add(Pair("MTI", msg.get("message_type")!!.encodeToString()))
                for (i in 0..192) {

                    allFields.add(Pair("Bitmap", msg.bitmap().bytes().toHexString()))

                    if (i != 1 && i != 65 && i != 129) {
                        if (msg!!.bitmap().isOn(i)) {
                            allFields.add(Pair("Field#$i", msg.bitmap().get(i)!!.encodeToString()))

                        }
                    }
                }

            }

            fieldsDiv.append({
                ol {
                    allFields.forEach {
                        li {
                            this.text(it.first + "->" + it.second)
                        }
                    }
                }

            })


        }

    }
}
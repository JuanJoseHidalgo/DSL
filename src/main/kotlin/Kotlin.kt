import kotlinx.html.*
import kotlinx.html.stream.createHTML
import java.io.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable


@Serializable
data class Ipaddress(
    val ip: String,
    val city: String,
    val region: String,
    val country: String,
    val loc: String,
    val org: String,
    val postal: String,
    val timezone: String,
)

fun main() {
    val dir=File("c:/html")
    val f=File("c:/html/index.html")
    val c=File("c:/html/styles.css")
    val s=File("styles.css")
    val ft=File("c:/html/Xeolocaliza.webp")
    val st=File("Xeolocaliza.webp")
    val client = HttpClient.newHttpClient()

    if (!dir.exists()) dir.mkdirs()
    if (f.exists()) f.delete()


    print("Introduce unha ip pública: ")
    val ip = readln().toString()
    val todo = "https://ipinfo.io/" + ip + "/json?token=437bf12a0fd22b"

    // crear solicitud
    val request = HttpRequest.newBuilder()
        .uri(URI.create(todo))
        .GET()
        .build()

    //  Enviar la solicitud con el cliente
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    // obtener string con datos
    val jsonBody = response.body()
    val json = Json { ignoreUnknownKeys = true }
    val ipAddressInfo: Ipaddress = json.decodeFromString(jsonBody)

    val htmlContent = createHTML().html {
        head {
            meta(charset = "UTF-8")
            title("WHEREIS IP?")
            link(rel = "stylesheet", href = "styles.css", type = "text/css")
        }
        body {
            ul {
                h1 { +"O enderezo da IP Pública: ${ipAddressInfo.ip}" }
            }
            hr {}
            ul {
                li { h2 { +"Corresponde á seguinte cidade: ${ipAddressInfo.city}" } }
                li { h2 { +"Situada no seguinte código postal: ${ipAddressInfo.postal}" } }
                li { h2 { +"Que está na rexion de: ${ipAddressInfo.region}" } }
                li { h2 { +"Ubicada no seguinte pais: ${ipAddressInfo.country}" } }
                li { h2 { +"O fuso horario é: ${ipAddressInfo.timezone}" } }
                li { h2 { +"Corresponde á seguinte compañía telefónica: ${ipAddressInfo.org}" } }
                li { h2 { +"As súas coordenadas GPS son: ${ipAddressInfo.loc}" } }
            }
            hr {}
            h1 { a(href = "https://www.google.es/maps/place/${ipAddressInfo.loc}") {+"Ver en Google Maps" }}
        }
    }
    try {
        f.writeText(htmlContent)
        s.copyTo(c, true)
        st.copyTo(ft, true)
    }catch (e:Exception){
        println("Erro: ${e.message}")
    }
    println(htmlContent)
}
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
            link(rel = "stylesheet", href = "c:/html/styles.css", type = "text/css")
        }
        body {
            h1 { +"O enderezo da IP Pública: ${ipAddressInfo.ip}" }
            h2 { +"Corresponde á seguinte cidade: ${ipAddressInfo.city}" }
            h2 { +"Situada no seguinte código postal: ${ipAddressInfo.postal}" }
            h2 { +"Que está na rexion de: ${ipAddressInfo.region}" }
            h2  { +"Ubicada no seguinte pais: ${ipAddressInfo.country}" }
            h2 { +"O fuso horario é: ${ipAddressInfo.timezone}" }
            h2 { +"Corresponde á seguinte compañía telefónica: ${ipAddressInfo.org}" }
            h2 { +"As súas coordenadas GPS son: ${ipAddressInfo.loc}" }
            h1 { a(href = "https://www.google.es/maps/place/${ipAddressInfo.loc}") {+"Ver en Google Maps" }}
        }
    }
    try {
        f.writeText(htmlContent)
        s.copyTo(c, true)
    }catch (e:Exception){
        println("Erro: ${e.message}")
    }
    println(htmlContent)
}
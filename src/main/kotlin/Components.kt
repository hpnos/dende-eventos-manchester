import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val formatoDataHora: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
val formatoData: DateTimeFormatter     = DateTimeFormatter.ofPattern("dd/MM/yyyy")

// -------------------------------------------------------
// Funções de leitura com validação em loop
// -------------------------------------------------------

fun readInt(message: String, errorMessage: String, range: IntRange = 0..Int.MAX_VALUE): Int {
    while (true) {
        print(message)
        val valor = readLine()?.trim()?.toIntOrNull()
        if (valor != null && valor in range) return valor
        println(errorMessage)
    }
}

fun readDouble(message: String, errorMessage: String, minValue: Double = 0.0, maxValue: Double = Double.MAX_VALUE): Double {
    while (true) {
        print(message)
        val valor = readLine()?.trim()?.toDoubleOrNull()
        if (valor != null && valor >= minValue && valor <= maxValue) return valor
        println(errorMessage)
    }
}

fun readString(message: String, errorMessage: String, minLength: Int = 0): String {
    while (true) {
        print(message)
        val valor = readLine()?.trim() ?: ""
        if (valor.length >= minLength) return valor
        println(errorMessage)
    }
}

fun printTable(header: String, items: List<Any>) {
    val linha = "-".repeat(60)
    println(linha)
    println(header)
    println(linha)
    if (items.isEmpty()) {
        println("Nenhum registro encontrado.")
    } else {
        items.forEach { println(it) }
    }
    println(linha)
}

// -------------------------------------------------------
// Funções auxiliares usadas nas User Stories
// -------------------------------------------------------

fun lerData(message: String): LocalDate? {
    val texto = readString(message, "")
    if (texto.isBlank()) return null
    return runCatching { LocalDate.parse(texto, formatoData) }.getOrNull()
}

fun lerDataHora(message: String): LocalDateTime? {
    val texto = readString(message, "")
    if (texto.isBlank()) return null
    return runCatching { LocalDateTime.parse(texto, formatoDataHora) }.getOrNull()
}

fun lerSimNao(message: String): Boolean {
    val v = readString(message, "").lowercase()
    return v == "s"
}

fun lerSexo(): Sexo {
    val op = readInt(
        "Sexo (1=Masculino, 2=Feminino, 3=Outro, 4=Prefiro não dizer): ",
        "Opção inválida.",
        1..4
    )
    return when (op) {
        1    -> Sexo.MASCULINO
        2    -> Sexo.FEMININO
        3    -> Sexo.OUTRO
        else -> Sexo.PREFIRO_NAO_DIZER
    }
}

fun lerModalidade(): ModalidadeEvento {
    val op = readInt(
        "Modalidade (1=Presencial, 2=Remoto, 3=Híbrido): ",
        "Opção inválida.",
        1..3
    )
    return when (op) {
        1    -> ModalidadeEvento.PRESENCIAL
        2    -> ModalidadeEvento.REMOTO
        else -> ModalidadeEvento.HIBRIDO
    }
}

fun lerTipoEvento(): TipoEvento {
    val tipos = TipoEvento.entries
    println("Tipos de evento:")
    tipos.forEachIndexed { i, t -> println("  ${i + 1}) $t") }
    val op = readInt("Escolha o tipo: ", "Opção inválida.", 1..tipos.size)
    return tipos[op - 1]
}

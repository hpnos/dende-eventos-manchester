import java.time.LocalDate
import java.time.LocalDateTime

enum class Sexo {
    MASCULINO, FEMININO, OUTRO, PREFIRO_NAO_DIZER
}

enum class TipoEvento {
    SOCIAL, CORPORATIVO, ACADEMICO, CULTURAL_ENTRETENIMENTO, RELIGIOSO, ESPORTIVO, FEIRA, CONGRESSO,
    OFICINA, CURSO, TREINAMENTO, AULA, SEMINARIO, PALESTRA, SHOW, FESTIVAL, EXPOSICAO, RETIRO,
    CULTO, CELEBRACAO, CAMPEONATO, CORRIDA
}

enum class ModalidadeEvento {
    PRESENCIAL, REMOTO, HIBRIDO
}

enum class StatusIngresso {
    ATIVO, CANCELADO
}

data class Empresa(
    var cnpj: String,
    var razaoSocial: String,
    var nomeFantasia: String
)

data class Usuario(
    val id: String = java.util.UUID.randomUUID().toString(),
    var nome: String,
    var dataNascimento: LocalDate,
    var sexo: Sexo,
    val email: String,
    var senha: String,
    var ativo: Boolean = true,
    var empresa: Empresa? = null
)

data class Evento(
    val id: String = java.util.UUID.randomUUID().toString(),
    val organizadorId: String,
    var pagina: String,
    var nome: String,
    var descricao: String,
    var inicio: LocalDateTime,
    var fim: LocalDateTime,
    var tipo: TipoEvento,
    var modalidade: ModalidadeEvento,
    var capacidade: Int,
    var localOuLink: String,
    var ativo: Boolean,
    var preco: Double,
    var estornaEmCancelamento: Boolean,
    var taxaEstornoPercent: Double,
    var eventoPrincipalId: String? = null
)

data class Ingresso(
    val id: String = java.util.UUID.randomUUID().toString(),
    val usuarioId: String,
    val eventoId: String,
    var status: StatusIngresso = StatusIngresso.ATIVO,
    var valorPago: Double
)

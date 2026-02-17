import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.UUID

enum class Sexo { MASCULINO, FEMININO, OUTRO, PREFIRO_NAO_DIZER }

enum class TipoEvento {
    SOCIAL, CORPORATIVO, ACADEMICO, CULTURAL_ENTRETENIMENTO, RELIGIOSO, ESPORTIVO, FEIRA, CONGRESSO,
    OFICINA, CURSO, TREINAMENTO, AULA, SEMINARIO, PALESTRA, SHOW, FESTIVAL, EXPOSICAO, RETIRO,
    CULTO, CELEBRACAO, CAMPEONATO, CORRIDA
}

enum class ModalidadeEvento { PRESENCIAL, REMOTO, HIBRIDO }

enum class StatusIngresso { ATIVO, CANCELADO }

data class Empresa(
    var cnpj: String,
    var razaoSocial: String,
    var nomeFantasia: String
)

data class Usuario(
    val id: String = UUID.randomUUID().toString(),
    var nome: String,
    var dataNascimento: LocalDate,
    var sexo: Sexo,
    val email: String, // não pode mudar
    var senha: String,
    var ativo: Boolean = true,
    var empresa: Empresa? = null // se for organizador com empresa (opcional)
)

data class Evento(
    val id: String = UUID.randomUUID().toString(),
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
    val id: String = UUID.randomUUID().toString(),
    val usuarioId: String,
    val eventoId: String,
    var status: StatusIngresso = StatusIngresso.ATIVO,
    var valorPago: Double
)

fun main() {
    val usuarios = mutableListOf<Usuario>()
    val eventos = mutableListOf<Evento>()
    val ingressos = mutableListOf<Ingresso>()

    val fmtDataHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val fmtData = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    var opcao = ""

    do {
        // Atualiza eventos que já passaram do fim: não devem aparecer no feed (US11)
        val agora = LocalDateTime.now()
        for (ev in eventos) {
            if (ev.fim.isBefore(agora) && ev.ativo) {
                ev.ativo = false
            }
        }

        println("\n=== Dendê Eventos (Console) ===")
        println("1) Cadastrar usuário comum (US1)")
        println("2) Cadastrar usuário organizador (US2)")
        println("3) Alterar perfil do usuário (US3)")
        println("4) Visualizar perfil do usuário (US4)")
        println("5) Inativar usuário (US5)")
        println("6) Reativar usuário (US6)")

        println("7) Cadastrar evento (US6-evento)")
        println("8) Alterar evento (US7) [exceto ativação]")
        println("9) Ativar evento (US8)")
        println("10) Desativar evento (US9) [cancela ingressos vendidos]")

        println("11) Listar eventos do organizador (US10)")
        println("12) Feed de eventos (US11)")
        println("13) Comprar ingresso (US12)")
        println("14) Cancelar ingresso (US13)")
        println("15) Listar ingressos do usuário (US14)")

        println("0) Sair")
        print("Opção: ")

        val entrada = readLine() ?: break
        opcao = entrada.trim()

        if (opcao.isBlank()) {
            println("Digite uma opção válida.")
            continue
        }

        when (opcao) {

            "1" -> {
                print("Nome: "); val nome = readLine()?.trim() ?: ""
                print("Data de nascimento (dd/MM/yyyy): "); val dnStr = readLine()?.trim() ?: ""
                print("Sexo (1=M,2=F,3=O,4=PND): "); val sxStr = readLine()?.trim() ?: ""
                print("E-mail: "); val email = readLine()?.trim() ?: ""
                print("Senha: "); val senha = readLine()?.trim() ?: ""

                if (usuarios.any { it.email.equals(email, ignoreCase = true) }) {
                    println(" Não podem existir dois usuários com o mesmo e-mail.")
                } else {
                    val dn = runCatching { LocalDate.parse(dnStr, fmtData) }.getOrNull()
                    if (dn == null) {
                        println(" Data inválida.")
                    } else {
                        val sexo = when (sxStr) {
                            "1" -> Sexo.MASCULINO
                            "2" -> Sexo.FEMININO
                            "3" -> Sexo.OUTRO
                            else -> Sexo.PREFIRO_NAO_DIZER
                        }
                        usuarios.add(Usuario(nome = nome, dataNascimento = dn, sexo = sexo, email = email, senha = senha))
                        println(" Usuário comum cadastrado!")
                    }
                }
            }

            "2" -> {
                print("Nome: "); val nome = readLine()?.trim() ?: ""
                print("Data de nascimento (dd/MM/yyyy): "); val dnStr = readLine()?.trim() ?: ""
                print("Sexo (1=M,2=F,3=O,4=PND): "); val sxStr = readLine()?.trim() ?: ""
                print("E-mail: "); val email = readLine()?.trim() ?: ""
                print("Senha: "); val senha = readLine()?.trim() ?: ""

                if (usuarios.any { it.email.equals(email, ignoreCase = true) }) {
                    println(" Não podem existir dois usuários com o mesmo e-mail.")
                } else {
                    val dn = runCatching { LocalDate.parse(dnStr, fmtData) }.getOrNull()
                    if (dn == null) {
                        println(" Data inválida.")
                    } else {
                        val sexo = when (sxStr) {
                            "1" -> Sexo.MASCULINO
                            "2" -> Sexo.FEMININO
                            "3" -> Sexo.OUTRO
                            else -> Sexo.PREFIRO_NAO_DIZER
                        }

                        print("Você é uma empresa? (s/n): "); val isEmp = (readLine()?.trim() ?: "n").lowercase()
                        var emp: Empresa? = null
                        if (isEmp == "s") {
                            print("CNPJ: "); val cnpj = readLine()?.trim() ?: ""
                            print("Razão Social: "); val razao = readLine()?.trim() ?: ""
                            print("Nome Fantasia: "); val fantasia = readLine()?.trim() ?: ""
                            emp = Empresa(cnpj, razao, fantasia)
                        }

                        usuarios.add(Usuario(nome = nome, dataNascimento = dn, sexo = sexo, email = email, senha = senha, empresa = emp))
                        println(" Usuário organizador cadastrado!")
                    }
                }
            }

            "3" -> {
                print("E-mail: "); val email = readLine()?.trim() ?: ""
                val u = usuarios.find { it.email.equals(email, ignoreCase = true) }

                if (u == null) println(" Usuário não encontrado.")
                else {
                    print("Senha: "); val senha = readLine()?.trim() ?: ""
                    if (senha != u.senha) println(" Senha incorreta.")
                    else if (!u.ativo) println(" Usuário inativo. Reative primeiro.")
                    else {
                        print("Novo nome (enter mantém): "); val novoNome = readLine()?.trim() ?: ""
                        print("Nova data nasc (dd/MM/yyyy) (enter mantém): "); val novoDnStr = readLine()?.trim() ?: ""
                        print("Novo sexo (1=M,2=F,3=O,4=PND) (enter mantém): "); val novoSxStr = readLine()?.trim() ?: ""
                        print("Nova senha (enter mantém): "); val novaSenha = readLine()?.trim() ?: ""

                        if (novoNome.isNotBlank()) u.nome = novoNome
                        if (novaSenha.isNotBlank()) u.senha = novaSenha

                        if (novoDnStr.isNotBlank()) {
                            val dn = runCatching { LocalDate.parse(novoDnStr, fmtData) }.getOrNull()
                            if (dn != null) u.dataNascimento = dn else println(" Data inválida, mantendo a anterior.")
                        }

                        if (novoSxStr.isNotBlank()) {
                            u.sexo = when (novoSxStr) {
                                "1" -> Sexo.MASCULINO
                                "2" -> Sexo.FEMININO
                                "3" -> Sexo.OUTRO
                                else -> Sexo.PREFIRO_NAO_DIZER
                            }
                        }

                        if (u.empresa != null) {
                            print("Alterar empresa? (s/n): "); val alt = (readLine()?.trim() ?: "n").lowercase()
                            if (alt == "s") {
                                print("CNPJ (enter mantém): "); val cnpj = readLine()?.trim() ?: ""
                                print("Razão Social (enter mantém): "); val razao = readLine()?.trim() ?: ""
                                print("Nome Fantasia (enter mantém): "); val fantasia = readLine()?.trim() ?: ""
                                if (cnpj.isNotBlank()) u.empresa!!.cnpj = cnpj
                                if (razao.isNotBlank()) u.empresa!!.razaoSocial = razao
                                if (fantasia.isNotBlank()) u.empresa!!.nomeFantasia = fantasia
                            }
                        }

                        println(" Perfil atualizado (e-mail mantido).")
                    }
                }
            }

            "4" -> {
                print("E-mail: "); val email = readLine()?.trim() ?: ""
                val u = usuarios.find { it.email.equals(email, ignoreCase = true) }

                if (u == null) println(" Usuário não encontrado.")
                else {
                    val p = Period.between(u.dataNascimento, LocalDate.now())
                    println("\n=== PERFIL ===")
                    println("Nome: ${u.nome}")
                    println("E-mail: ${u.email}")
                    println("Ativo: ${if (u.ativo) "SIM" else "NÃO"}")
                    println("Nascimento: ${u.dataNascimento.format(fmtData)}")
                    println("Idade: ${p.years} anos, ${p.months} meses, ${p.days} dias")
                    println("Sexo: ${u.sexo}")

                    if (u.empresa != null) {
                        println("\n--- EMPRESA ---")
                        println("CNPJ: ${u.empresa!!.cnpj}")
                        println("Razão Social: ${u.empresa!!.razaoSocial}")
                        println("Nome Fantasia: ${u.empresa!!.nomeFantasia}")
                    }
                }
            }

            "5" -> {
                print("E-mail: "); val email = readLine()?.trim() ?: ""
                val u = usuarios.find { it.email.equals(email, ignoreCase = true) }

                if (u == null) println(" Usuário não encontrado.")
                else {
                    print("Senha: "); val senha = readLine()?.trim() ?: ""
                    if (senha != u.senha) println(" Senha incorreta.")
                    else if (!u.ativo) println(" Usuário já está inativo.")
                    else {
                        val agora2 = LocalDateTime.now()
                        val temEventoAtivoOuEmExecucao = eventos.any {
                            it.organizadorId == u.id && it.ativo && agora2.isBefore(it.fim)
                        }

                        if (temEventoAtivoOuEmExecucao) {
                            println(" Organizador só pode inativar se não tiver eventos ativos ou em execução.")
                        } else {
                            u.ativo = false
                            println(" Usuário inativado.")
                        }
                    }
                }
            }

            "6" -> {
                print("E-mail: "); val email = readLine()?.trim() ?: ""
                print("Senha: "); val senha = readLine()?.trim() ?: ""
                val u = usuarios.find { it.email.equals(email, ignoreCase = true) }

                if (u == null) println(" Usuário não encontrado.")
                else if (senha != u.senha) println(" Senha incorreta.")
                else {
                    u.ativo = true
                    println(" Perfil reativado.")
                }
            }

            "7" -> {
                print("E-mail do organizador: "); val emailOrg = readLine()?.trim() ?: ""
                val org = usuarios.find { it.email.equals(emailOrg, ignoreCase = true) }

                if (org == null) println(" Organizador não encontrado.")
                else if (!org.ativo) println(" Organizador inativo.")
                else {
                    print("Página do evento: "); val pagina = readLine()?.trim() ?: ""
                    print("Nome do evento: "); val nome = readLine()?.trim() ?: ""
                    print("Descrição: "); val desc = readLine()?.trim() ?: ""
                    print("Início (dd/MM/yyyy HH:mm): "); val iniStr = readLine()?.trim() ?: ""
                    print("Fim (dd/MM/yyyy HH:mm): "); val fimStr = readLine()?.trim() ?: ""

                    val tipos = TipoEvento.entries
                    println("Tipo (número):")
                    for (i in tipos.indices) println("${i + 1}) ${tipos[i]}")
                    print("Escolha: ")
                    val tipoN = (readLine()?.trim() ?: "").toIntOrNull()

                    print("Modalidade (1=PRESENCIAL,2=REMOTO,3=HIBRIDO): "); val modStr = readLine()?.trim() ?: ""
                    print("Capacidade: "); val cap = (readLine()?.trim() ?: "").toIntOrNull() ?: 0
                    print("Local/Link: "); val local = readLine()?.trim() ?: ""
                    print("Evento ativo? (s/n): "); val ativoStr = (readLine()?.trim() ?: "s").lowercase()
                    print("Preço: "); val preco = (readLine()?.trim() ?: "").toDoubleOrNull() ?: 0.0

                    print("Estorna em cancelamento? (s/n): "); val estStr = (readLine()?.trim() ?: "n").lowercase()
                    val estorna = estStr == "s"
                    var taxa = 0.0
                    if (estorna) {
                        print("Taxa de estorno (%) (ex: 10): ")
                        taxa = (readLine()?.trim() ?: "").toDoubleOrNull() ?: 0.0
                        taxa = taxa.coerceIn(0.0, 100.0)
                    }

                    print("Está ligado a um evento principal? (s/n): "); val ligadoStr = (readLine()?.trim() ?: "n").lowercase()
                    var principalId: String? = null
                    if (ligadoStr == "s") {
                        print("ID do evento principal: "); val pid = readLine()?.trim() ?: ""
                        if (pid.isNotBlank()) principalId = pid
                    }

                    val inicio = runCatching { LocalDateTime.parse(iniStr, fmtDataHora) }.getOrNull()
                    val fim = runCatching { LocalDateTime.parse(fimStr, fmtDataHora) }.getOrNull()

                    if (inicio == null || fim == null) println(" Data/hora inválida.")
                    else {
                        val agora3 = LocalDateTime.now()
                        val durMin = java.time.Duration.between(inicio, fim).toMinutes()

                        if (inicio.isBefore(agora3)) println(" Início não pode ser anterior à data corrente.")
                        else if (fim.isBefore(agora3)) println(" Fim não pode ser anterior à data corrente.")
                        else if (fim.isBefore(inicio)) println(" Fim não pode ser anterior ao início.")
                        else if (durMin < 30) println(" Duração mínima: 30 minutos.")
                        else if (tipoN == null || tipoN !in 1..tipos.size) println(" Tipo inválido.")
                        else if (cap <= 0) println(" Capacidade deve ser > 0.")
                        else {
                            if (principalId != null && eventos.none { it.id == principalId }) {
                                println(" Evento principal não encontrado.")
                                continue
                            }

                            val tipo = tipos[tipoN - 1]
                            val modalidade = when (modStr) {
                                "1" -> ModalidadeEvento.PRESENCIAL
                                "2" -> ModalidadeEvento.REMOTO
                                else -> ModalidadeEvento.HIBRIDO
                            }

                            eventos.add(
                                Evento(
                                    organizadorId = org.id,
                                    pagina = pagina,
                                    nome = nome,
                                    descricao = desc,
                                    inicio = inicio,
                                    fim = fim,
                                    tipo = tipo,
                                    modalidade = modalidade,
                                    capacidade = cap,
                                    localOuLink = local,
                                    ativo = (ativoStr == "s"),
                                    preco = preco,
                                    estornaEmCancelamento = estorna,
                                    taxaEstornoPercent = taxa,
                                    eventoPrincipalId = principalId
                                )
                            )

                            println(" Evento cadastrado! ID: ${eventos.last().id}")
                        }
                    }
                }
            }

            "8" -> {
                print("E-mail do organizador: "); val emailOrg = readLine()?.trim() ?: ""
                val org = usuarios.find { it.email.equals(emailOrg, ignoreCase = true) }

                if (org == null) println(" Organizador não encontrado.")
                else if (!org.ativo) println(" Organizador inativo.")
                else {
                    print("ID do evento: "); val idEv = readLine()?.trim() ?: ""
                    val ev = eventos.find { it.id == idEv && it.organizadorId == org.id }

                    if (ev == null) println(" Evento não encontrado.")
                    else if (!ev.ativo) println(" Só é possível alterar um evento ativo (ativação é separada).")
                    else {
                        print("Página (enter mantém): "); val pagina = readLine()?.trim() ?: ""
                        print("Nome (enter mantém): "); val nome = readLine()?.trim() ?: ""
                        print("Descrição (enter mantém): "); val desc = readLine()?.trim() ?: ""
                        print("Início (dd/MM/yyyy HH:mm) (enter mantém): "); val iniStr = readLine()?.trim() ?: ""
                        print("Fim (dd/MM/yyyy HH:mm) (enter mantém): "); val fimStr = readLine()?.trim() ?: ""
                        print("Capacidade (enter mantém): "); val capStr = readLine()?.trim() ?: ""
                        print("Local/Link (enter mantém): "); val local = readLine()?.trim() ?: ""
                        print("Preço (enter mantém): "); val precoStr = readLine()?.trim() ?: ""

                        // alterações simples
                        if (pagina.isNotBlank()) ev.pagina = pagina
                        if (nome.isNotBlank()) ev.nome = nome
                        if (desc.isNotBlank()) ev.descricao = desc
                        if (local.isNotBlank()) ev.localOuLink = local

                        if (capStr.isNotBlank()) {
                            val c = capStr.toIntOrNull()
                            if (c != null && c > 0) ev.capacidade = c
                        }

                        if (precoStr.isNotBlank()) {
                            val pr = precoStr.toDoubleOrNull()
                            if (pr != null && pr >= 0.0) ev.preco = pr
                        }

                        // datas com validação mínima
                        var novoInicio = ev.inicio
                        var novoFim = ev.fim

                        if (iniStr.isNotBlank()) {
                            val i = runCatching { LocalDateTime.parse(iniStr, fmtDataHora) }.getOrNull()
                            if (i != null) novoInicio = i
                        }
                        if (fimStr.isNotBlank()) {
                            val f = runCatching { LocalDateTime.parse(fimStr, fmtDataHora) }.getOrNull()
                            if (f != null) novoFim = f
                        }

                        val agora4 = LocalDateTime.now()
                        val durMin = java.time.Duration.between(novoInicio, novoFim).toMinutes()

                        if (novoInicio.isBefore(agora4) || novoFim.isBefore(agora4) || novoFim.isBefore(novoInicio) || durMin < 30) {
                            println(" Datas inválidas. Mantendo datas anteriores.")
                        } else {
                            ev.inicio = novoInicio
                            ev.fim = novoFim
                        }

                        // tipo/modalidade/estorno
                        print("Alterar tipo? (s/n): "); val altTipo = (readLine()?.trim() ?: "n").lowercase()
                        if (altTipo == "s") {
                            val tipos = TipoEvento.entries
                            for (i in tipos.indices) println("${i + 1}) ${tipos[i]}")
                            print("Escolha: ")
                            val n = (readLine()?.trim() ?: "").toIntOrNull()
                            if (n != null && n in 1..tipos.size) ev.tipo = tipos[n - 1]
                        }

                        print("Alterar modalidade? (1=P,2=R,3=H, enter mantém): ")
                        val mod = readLine()?.trim() ?: ""
                        if (mod.isNotBlank()) {
                            ev.modalidade = when (mod) {
                                "1" -> ModalidadeEvento.PRESENCIAL
                                "2" -> ModalidadeEvento.REMOTO
                                else -> ModalidadeEvento.HIBRIDO
                            }
                        }

                        print("Estorna em cancelamento? (s/n/enter mantém): ")
                        val est = readLine()?.trim() ?: ""
                        if (est.isNotBlank()) {
                            ev.estornaEmCancelamento = est.lowercase() == "s"
                            if (ev.estornaEmCancelamento) {
                                print("Taxa estorno (%): ")
                                val t = (readLine()?.trim() ?: "").toDoubleOrNull()
                                if (t != null) ev.taxaEstornoPercent = t.coerceIn(0.0, 100.0)
                            } else {
                                ev.taxaEstornoPercent = 0.0
                            }
                        }

                        println(" Evento atualizado (ativação é separada).")
                    }
                }
            }

            "9" -> {
                print("E-mail do organizador: "); val emailOrg = readLine()?.trim() ?: ""
                val org = usuarios.find { it.email.equals(emailOrg, ignoreCase = true) }

                if (org == null) println(" Organizador não encontrado.")
                else if (!org.ativo) println(" Organizador inativo.")
                else {
                    print("ID do evento: "); val idEv = readLine()?.trim() ?: ""
                    val ev = eventos.find { it.id == idEv && it.organizadorId == org.id }
                    if (ev == null) println(" Evento não encontrado.")
                    else {
                        ev.ativo = true
                        println(" Evento ativado.")
                    }
                }
            }

            "10" -> {
                print("E-mail do organizador: "); val emailOrg = readLine()?.trim() ?: ""
                val org = usuarios.find { it.email.equals(emailOrg, ignoreCase = true) }

                if (org == null) println(" Organizador não encontrado.")
                else if (!org.ativo) println(" Organizador inativo.")
                else {
                    print("ID do evento: "); val idEv = readLine()?.trim() ?: ""
                    val ev = eventos.find { it.id == idEv && it.organizadorId == org.id }

                    if (ev == null) println(" Evento não encontrado.")
                    else {
                        ev.ativo = false
                        println(" Evento desativado. Venda suspensa.")

                        val vendidosAtivos = ingressos.filter { it.eventoId == ev.id && it.status == StatusIngresso.ATIVO }
                        if (vendidosAtivos.isNotEmpty()) {
                            println(" Cancelando ingressos e reembolsando (simulado):")
                            for (ing in vendidosAtivos) {
                                ing.status = StatusIngresso.CANCELADO

                                if (ev.estornaEmCancelamento) {
                                    val taxa = ev.taxaEstornoPercent.coerceIn(0.0, 100.0)
                                    val reembolso = ing.valorPago * (1.0 - taxa / 100.0)
                                    println(" - ${ing.id}: CANCELADO | Reembolso: R$ ${"%.2f".format(reembolso)} (taxa ${taxa}%)")
                                } else {
                                    println(" - ${ing.id}: CANCELADO | Sem reembolso (evento não estorna).")
                                }
                            }
                        } else {
                            println("Sem ingressos ativos vendidos para este evento.")
                        }
                    }
                }
            }

            // US10
            "11" -> {
                print("E-mail do organizador: "); val emailOrg = readLine()?.trim() ?: ""
                val org = usuarios.find { it.email.equals(emailOrg, ignoreCase = true) }

                if (org == null) println(" Organizador não encontrado.")
                else {
                    val meus = eventos.filter { it.organizadorId == org.id }
                    if (meus.isEmpty()) println("Sem eventos cadastrados.")
                    else {
                        println("\n=== EVENTOS DO ORGANIZADOR (US10) ===")
                        for (ev in meus) {
                            println("- ${ev.nome}")
                            println("  Período: ${ev.inicio.format(fmtDataHora)} -> ${ev.fim.format(fmtDataHora)}")
                            println("  Local/Link: ${ev.localOuLink}")
                            println("  Preço: R$ ${"%.2f".format(ev.preco)} | Capacidade: ${ev.capacidade}")
                            println("  Ativo: ${if (ev.ativo) "SIM" else "NÃO"} | ID: ${ev.id}")
                            println()
                        }
                    }
                }
            }

            // US11
            "12" -> {
                val agora5 = LocalDateTime.now()
                val ativos = eventos.filter { ev ->
                    // “eventos ativos na plataforma”
                    ev.ativo &&
                        // “eventos que já finalizaram não devem aparecer”
                        ev.fim.isAfter(agora5) &&
                        // “eventos esgotados não aparecem”
                        ingressos.count { it.eventoId == ev.id && it.status == StatusIngresso.ATIVO } < ev.capacidade
                }

                if (ativos.isEmpty()) {
                    println("Sem eventos no feed.")
                } else {
                    println("\n=== FEED (US11) ===")
                    println("Ordenar por:")
                    println("1) Data/hora de início")
                    println("2) Ordem alfabética (nome)")
                    print("Escolha: ")
                    val ord = readLine()?.trim() ?: "1"

                    val ordenados = if (ord == "2") {
                        ativos.sortedWith(compareBy<Evento> { it.nome.lowercase() }.thenBy { it.inicio })
                    } else {
                        ativos.sortedWith(compareBy<Evento> { it.inicio }.thenBy { it.nome.lowercase() })
                    }

                    for (ev in ordenados) {
                        val vendidos = ingressos.count { it.eventoId == ev.id && it.status == StatusIngresso.ATIVO }
                        val vagas = ev.capacidade - vendidos
                        println("- ${ev.nome} | ${ev.inicio.format(fmtDataHora)} | vagas: $vagas | R$ ${"%.2f".format(ev.preco)}")
                        if (ev.eventoPrincipalId != null) println("  Vinculado ao principal: ${ev.eventoPrincipalId}")
                        println("  ID: ${ev.id}")
                    }
                }
            }

            // US12
            "13" -> {
                print("E-mail do usuário comum: "); val email = readLine()?.trim() ?: ""
                val u = usuarios.find { it.email.equals(email, ignoreCase = true) }

                if (u == null) println(" Usuário não encontrado.")
                else if (!u.ativo) println(" Usuário inativo.")
                else {
                    print("ID do evento: "); val idEv = readLine()?.trim() ?: ""
                    val ev = eventos.find { it.id == idEv }

                    if (ev == null) println(" Evento não encontrado.")
                    else {
                        val agora6 = LocalDateTime.now()
                        val vendidosEv = ingressos.count { it.eventoId == ev.id && it.status == StatusIngresso.ATIVO }

                        if (!ev.ativo) println(" Evento não está ativo.")
                        else if (ev.fim.isBefore(agora6)) println(" Evento já finalizou.")
                        else if (vendidosEv >= ev.capacidade) println(" Evento esgotado.")
                        else {
                            if (ev.eventoPrincipalId != null) {
                                val principal = eventos.find { it.id == ev.eventoPrincipalId }
                                if (principal == null) {
                                    println(" Evento principal não encontrado.")
                                } else {
                                    val vendidosPr = ingressos.count { it.eventoId == principal.id && it.status == StatusIngresso.ATIVO }

                                    if (!principal.ativo) println(" Evento principal não está ativo.")
                                    else if (principal.fim.isBefore(agora6)) println(" Evento principal já finalizou.")
                                    else if (vendidosPr >= principal.capacidade) println(" Evento principal esgotado.")
                                    else {
                                        val total = principal.preco + ev.preco
                                        ingressos.add(Ingresso(usuarioId = u.id, eventoId = principal.id, valorPago = principal.preco))
                                        ingressos.add(Ingresso(usuarioId = u.id, eventoId = ev.id, valorPago = ev.preco))
                                        println(" Compra concluída! Total: R$ ${"%.2f".format(total)}")
                                        println("Ingressos gerados (1 por evento):")
                                        println("- ${ingressos[ingressos.size - 2].id} (principal: ${principal.nome})")
                                        println("- ${ingressos.last().id} (evento: ${ev.nome})")
                                    }
                                }
                            } else {
                                ingressos.add(Ingresso(usuarioId = u.id, eventoId = ev.id, valorPago = ev.preco))
                                println(" Ingresso comprado! ID: ${ingressos.last().id} | Valor: R$ ${"%.2f".format(ev.preco)}")
                            }
                        }
                    }
                }
            }

            // US13
            "14" -> {
                print("E-mail do usuário: "); val email = readLine()?.trim() ?: ""
                val u = usuarios.find { it.email.equals(email, ignoreCase = true) }

                if (u == null) println(" Usuário não encontrado.")
                else if (!u.ativo) println(" Usuário inativo.")
                else {
                    // listar ingressos do usuário para ele escolher melhor
                    val meus = ingressos.filter { it.usuarioId == u.id }
                    if (meus.isEmpty()) {
                        println("Sem ingressos para cancelar.")
                    } else {
                        println("\nSeus ingressos:")
                        for (ing in meus) {
                            val ev = eventos.find { it.id == ing.eventoId }
                            println("- ${ing.id} | ${(ev?.nome ?: "Evento removido")} | ${ing.status}")
                        }

                        print("Digite o ID do ingresso para cancelar: ")
                        val ingId = readLine()?.trim() ?: ""
                        val ing = ingressos.find { it.id == ingId && it.usuarioId == u.id }

                        if (ing == null) println(" Ingresso não encontrado.")
                        else if (ing.status == StatusIngresso.CANCELADO) println(" Ingresso já está cancelado.")
                        else {
                            val ev = eventos.find { it.id == ing.eventoId }
                            if (ev == null) {
                                ing.status = StatusIngresso.CANCELADO
                                println(" Ingresso cancelado. (Evento não encontrado para regras de estorno).")
                            } else {
                                ing.status = StatusIngresso.CANCELADO

                                // estorno conforme regras do evento
                                if (ev.estornaEmCancelamento) {
                                    val taxa = ev.taxaEstornoPercent.coerceIn(0.0, 100.0)
                                    val estorno = ing.valorPago * (1.0 - taxa / 100.0)
                                    println(" Ingresso cancelado. Valor estornado: R$ ${"%.2f".format(estorno)} (taxa ${taxa}%)")
                                } else {
                                    println(" Ingresso cancelado. Sem estorno (evento não estorna).")
                                }

                                // "evento deve ter livre mais um ingresso para venda"
                                // (como a contagem de ingressos ativos diminuiu, a vaga já “volta” automaticamente)
                                println("Vaga liberada para venda (contagem de ingressos ativos atualizada).")
                            }
                        }
                    }
                }
            }

            // US14
            "15" -> {
                print("E-mail do usuário: "); val email = readLine()?.trim() ?: ""
                val u = usuarios.find { it.email.equals(email, ignoreCase = true) }

                if (u == null) println(" Usuário não encontrado.")
                else {
                    val meus = ingressos.filter { it.usuarioId == u.id }

                    if (meus.isEmpty()) {
                        println("Sem ingressos.")
                    } else {
                        val agora7 = LocalDateTime.now()

                        // Separa "primeiros": eventos ainda ativos e não realizados (ainda não acabou)
                        val primeiros = meus.filter { ing ->
                            ing.status == StatusIngresso.ATIVO &&
                                run {
                                    val ev = eventos.find { it.id == ing.eventoId }
                                    ev != null && ev.ativo && ev.fim.isAfter(agora7)
                                }
                        }

                        // "últimos": cancelados OU eventos já finalizados
                        val ultimos = meus.filter { ing ->
                            ing.status == StatusIngresso.CANCELADO ||
                                run {
                                    val ev = eventos.find { it.id == ing.eventoId }
                                    ev != null && ev.fim.isBefore(agora7)
                                }
                        }

                        fun key(ing: Ingresso): Pair<LocalDateTime, String> {
                            val ev = eventos.find { it.id == ing.eventoId }
                            val inicio = ev?.inicio ?: LocalDateTime.MIN
                            val nomeEv = ev?.nome ?: "Evento removido"
                            return Pair(inicio, nomeEv.lowercase())
                        }

                        val primeirosOrd = primeiros.sortedWith(compareBy<Ingresso> { key(it).first }.thenBy { key(it).second })
                        val ultimosOrd = ultimos.sortedWith(compareBy<Ingresso> { key(it).first }.thenBy { key(it).second })

                        println("\n=== INGRESSOS (US14) ===")
                        println(">> ATIVOS (eventos ativos e não realizados) <<")
                        if (primeirosOrd.isEmpty()) println("(nenhum)")
                        else {
                            for (ing in primeirosOrd) {
                                val ev = eventos.find { it.id == ing.eventoId }
                                println("- ${ing.id} | ${ev?.nome} | início: ${ev?.inicio?.format(fmtDataHora)} | status: ${ing.status}")
                            }
                        }

                        println("\n>> CANCELADOS ou EVENTOS FINALIZADOS <<")
                        if (ultimosOrd.isEmpty()) println("(nenhum)")
                        else {
                            for (ing in ultimosOrd) {
                                val ev = eventos.find { it.id == ing.eventoId }
                                val nomeEv = ev?.nome ?: "Evento removido"
                                val ini = ev?.inicio?.format(fmtDataHora) ?: "-"
                                println("- ${ing.id} | $nomeEv | início: $ini | status: ${ing.status}")
                            }
                        }
                    }
                }
            }

            "0" -> println("Saindo...")

            else -> println("Opção inválida.")
        }

    } while (opcao != "0")
}

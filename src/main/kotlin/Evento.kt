import java.time.LocalDateTime

// -------------------------------------------------------
// US7 - Cadastrar Evento
// -------------------------------------------------------
fun cadastrarEvento() {
    println("\n--- Cadastrar Evento ---")

    val emailOrg = readString("E-mail do organizador: ", "E-mail não pode ser vazio.", minLength = 1)
    val org      = findUsuarioPorEmail(emailOrg)

    if (org == null) { println("Organizador não encontrado."); return }
    if (!org.ativo) { println("Organizador inativo."); return }

    val pagina    = readString("Página do evento: ", "Não pode ser vazio.", minLength = 1)
    val nome      = readString("Nome do evento: ", "Não pode ser vazio.", minLength = 1)
    val descricao = readString("Descrição: ", "Não pode ser vazio.", minLength = 1)
    val inicio    = lerDataHora("Início (dd/MM/yyyy HH:mm): ")
    val fim       = lerDataHora("Fim (dd/MM/yyyy HH:mm): ")

    if (inicio == null || fim == null) { println("Data/hora inválida."); return }

    val agora = LocalDateTime.now()
    if (inicio.isBefore(agora)) { println("O início não pode ser no passado."); return }
    if (fim.isBefore(inicio))   { println("O fim não pode ser antes do início."); return }
    if (java.time.Duration.between(inicio, fim).toMinutes() < 30) { println("Duração mínima: 30 minutos."); return }

    val tipo      = lerTipoEvento()
    val modal     = lerModalidade()
    val cap       = readInt("Capacidade: ", "Capacidade deve ser maior que zero.", range = 1..Int.MAX_VALUE)
    val local     = readString("Local/Link: ", "Não pode ser vazio.", minLength = 1)
    val ativo     = lerSimNao("Evento ativo ao cadastrar? (s/n): ")
    val preco     = readDouble("Preço (0 para gratuito): ", "Valor inválido.")
    val estorna   = lerSimNao("Estorna em cancelamento? (s/n): ")
    val taxa      = if (estorna) readDouble("Taxa de estorno (0 a 100): ", "Valor inválido.", 0.0, 100.0) else 0.0

    var principalId: String? = null
    if (lerSimNao("Ligado a um evento principal? (s/n): ")) {
        val pid = readString("ID do evento principal: ", "")
        if (pid.isNotBlank()) {
            if (findEventoPorId(pid) == null) { println("Evento principal não encontrado."); return }
            principalId = pid
        }
    }

    val evento = Evento(
        organizadorId         = org.id,
        pagina                = pagina,
        nome                  = nome,
        descricao             = descricao,
        inicio                = inicio,
        fim                   = fim,
        tipo                  = tipo,
        modalidade            = modal,
        capacidade            = cap,
        localOuLink           = local,
        ativo                 = ativo,
        preco                 = preco,
        estornaEmCancelamento = estorna,
        taxaEstornoPercent    = taxa,
        eventoPrincipalId     = principalId
    )

    salvarEvento(evento)
    println("Evento cadastrado! ID: ${evento.id}")
}

// -------------------------------------------------------
// US8 - Alterar Evento
// -------------------------------------------------------
fun alterarEvento() {
    println("\n--- Alterar Evento ---")

    val emailOrg = readString("E-mail do organizador: ", "E-mail não pode ser vazio.", minLength = 1)
    val org      = findUsuarioPorEmail(emailOrg)

    if (org == null) { println("Organizador não encontrado."); return }
    if (!org.ativo)  { println("Organizador inativo."); return }

    val idEv = readString("ID do evento: ", "ID não pode ser vazio.", minLength = 1)
    val ev   = eventos.find { it.id == idEv && it.organizadorId == org.id }

    if (ev == null)  { println("Evento não encontrado."); return }
    if (!ev.ativo)   { println("Só é possível alterar um evento ativo."); return }

    val novoNome      = readString("Novo nome (enter mantém): ", "")
    val novaDescricao = readString("Nova descrição (enter mantém): ", "")
    val novaPagina    = readString("Nova página (enter mantém): ", "")
    val novoLocal     = readString("Novo local/link (enter mantém): ", "")
    val novaCapStr    = readString("Nova capacidade (enter mantém): ", "")
    val novoPrecoStr  = readString("Novo preço (enter mantém): ", "")

    if (novoNome.isNotBlank())      ev.nome       = novoNome
    if (novaDescricao.isNotBlank()) ev.descricao  = novaDescricao
    if (novaPagina.isNotBlank())    ev.pagina     = novaPagina
    if (novoLocal.isNotBlank())     ev.localOuLink = novoLocal
    novaCapStr.toIntOrNull()?.takeIf { it > 0 }?.let { ev.capacidade = it }
    novoPrecoStr.toDoubleOrNull()?.takeIf { it >= 0 }?.let { ev.preco = it }

    val novoIniStr = readString("Novo início (dd/MM/yyyy HH:mm) (enter mantém): ", "")
    val novoFimStr = readString("Novo fim (dd/MM/yyyy HH:mm) (enter mantém): ", "")

    val novoInicio = if (novoIniStr.isNotBlank()) runCatching { java.time.LocalDateTime.parse(novoIniStr, formatoDataHora) }.getOrNull() ?: ev.inicio else ev.inicio
    val novoFim    = if (novoFimStr.isNotBlank()) runCatching { java.time.LocalDateTime.parse(novoFimStr, formatoDataHora) }.getOrNull() ?: ev.fim    else ev.fim

    val agora = LocalDateTime.now()
    if (novoInicio.isBefore(agora) || novoFim.isBefore(novoInicio) || java.time.Duration.between(novoInicio, novoFim).toMinutes() < 30) {
        println("Datas inválidas, mantendo as anteriores.")
    } else {
        ev.inicio = novoInicio
        ev.fim    = novoFim
    }

    if (lerSimNao("Alterar tipo? (s/n): ")) ev.tipo = lerTipoEvento()

    val novaModStr = readString("Modalidade (1=P, 2=R, 3=H) (enter mantém): ", "")
    if (novaModStr.isNotBlank()) {
        ev.modalidade = when (novaModStr) {
            "1"  -> ModalidadeEvento.PRESENCIAL
            "2"  -> ModalidadeEvento.REMOTO
            else -> ModalidadeEvento.HIBRIDO
        }
    }

    val estStr = readString("Estorna em cancelamento? (s/n) (enter mantém): ", "")
    if (estStr.isNotBlank()) {
        ev.estornaEmCancelamento = estStr.lowercase() == "s"
        ev.taxaEstornoPercent = if (ev.estornaEmCancelamento)
            readDouble("Taxa de estorno (0 a 100): ", "Valor inválido.", 0.0, 100.0)
        else 0.0
    }

    alterarEvento(ev)
    println("Evento atualizado com sucesso.")
}

// -------------------------------------------------------
// US9 - Ativar Evento
// -------------------------------------------------------
fun ativarEvento() {
    println("\n--- Ativar Evento ---")

    val emailOrg = readString("E-mail do organizador: ", "E-mail não pode ser vazio.", minLength = 1)
    val org      = findUsuarioPorEmail(emailOrg)

    if (org == null) { println("Organizador não encontrado."); return }
    if (!org.ativo)  { println("Organizador inativo."); return }

    val idEv = readString("ID do evento: ", "ID não pode ser vazio.", minLength = 1)
    val ev   = eventos.find { it.id == idEv && it.organizadorId == org.id }

    if (ev == null) { println("Evento não encontrado."); return }

    if (ev.fim.isBefore(LocalDateTime.now())) {
        println("Não é possível ativar um evento que já encerrou.")
        return
    }

    if (ev.ativo) {
        println("O evento '${ev.nome}' já está ativo.")
        return
    }

    ev.ativo = true
    alterarEvento(ev)
    println("Evento '${ev.nome}' ativado com sucesso.")
}

// -------------------------------------------------------
// US10 - Desativar Evento
// -------------------------------------------------------
fun desativarEvento() {
    println("\n--- Desativar Evento ---")

    val emailOrg = readString("E-mail do organizador: ", "E-mail não pode ser vazio.", minLength = 1)
    val org      = findUsuarioPorEmail(emailOrg)

    if (org == null) { println("Organizador não encontrado."); return }
    if (!org.ativo)  { println("Organizador inativo."); return }

    val idEv = readString("ID do evento: ", "ID não pode ser vazio.", minLength = 1)
    val ev   = eventos.find { it.id == idEv && it.organizadorId == org.id }

    if (ev == null)  { println("Evento não encontrado."); return }
    if (!ev.ativo)   { println("O evento '${ev.nome}' já está inativo."); return }

    ev.ativo = false
    alterarEvento(ev)
    println("Evento '${ev.nome}' desativado. Venda suspensa.")

    val ingressosAtivos = filterIngressosAtivosPorEvento(ev.id)

    if (ingressosAtivos.isEmpty()) {
        println("Nenhum ingresso ativo para cancelar.")
    } else {
        println("Cancelando ingressos:")
        for (ing in ingressosAtivos) {
            ing.status = StatusIngresso.CANCELADO
            alterarIngresso(ing)

            if (ev.estornaEmCancelamento) {
                val taxa      = ev.taxaEstornoPercent.coerceIn(0.0, 100.0)
                val reembolso = ing.valorPago * (1.0 - taxa / 100.0)
                println("  - Ingresso ${ing.id}: CANCELADO | Reembolso: R$ ${"%.2f".format(reembolso)} (taxa ${taxa}%)")
            } else {
                println("  - Ingresso ${ing.id}: CANCELADO | Sem reembolso.")
            }
        }
    }
}

// -------------------------------------------------------
// US11 - Listar Eventos do Organizador
// -------------------------------------------------------
fun listarEventosDoOrganizador() {
    println("\n--- Listar Eventos do Organizador ---")

    val emailOrg = readString("E-mail do organizador: ", "E-mail não pode ser vazio.", minLength = 1)
    val org      = findUsuarioPorEmail(emailOrg)

    if (org == null) { println("Organizador não encontrado."); return }

    val lista = filterEventosPorOrganizador(org.id)

    if (lista.isEmpty()) {
        println("Nenhum evento cadastrado.")
        return
    }

    printTable("EVENTOS DE ${org.nome.uppercase()}", lista)
}

// -------------------------------------------------------
// US12 - Feed de Eventos
// -------------------------------------------------------
fun feedDeEventos() {
    println("\n--- Feed de Eventos ---")
    println("Ordenar por: 1) Data de início  2) Nome")

    val op    = readInt("Escolha: ", "Opção inválida.", 1..2)
    val lista = filterEventosAtivosDisponiveis()

    if (lista.isEmpty()) {
        println("Nenhum evento disponível no momento.")
        return
    }

    val ordenada = if (op == 2)
        lista.sortedWith(compareBy({ it.nome.lowercase() }, { it.inicio }))
    else
        lista.sortedWith(compareBy({ it.inicio }, { it.nome.lowercase() }))

    printTable("FEED DE EVENTOS", ordenada)
}

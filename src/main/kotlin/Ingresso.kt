import java.time.LocalDateTime

// -------------------------------------------------------
// US13 - Comprar Ingresso
// -------------------------------------------------------
fun comprarIngresso() {
    println("\n--- Comprar Ingresso ---")

    val email   = readString("E-mail do usuário: ", "E-mail não pode ser vazio.", minLength = 1)
    val usuario = findUsuarioPorEmail(email)

    if (usuario == null) { println("Usuário não encontrado."); return }
    if (!usuario.ativo)  { println("Usuário inativo."); return }

    val idEv  = readString("ID do evento: ", "ID não pode ser vazio.", minLength = 1)
    val evento = findEventoPorId(idEv)

    if (evento == null) { println("Evento não encontrado."); return }

    val agora = LocalDateTime.now()

    if (!evento.ativo)                                       { println("Evento não está ativo."); return }
    if (evento.fim.isBefore(agora))                          { println("Evento já encerrou."); return }
    if (contarIngressosAtivos(evento.id) >= evento.capacidade) { println("Evento esgotado."); return }

    // Evento vinculado a um principal: compra os dois juntos
    if (evento.eventoPrincipalId != null) {
        val principal = findEventoPorId(evento.eventoPrincipalId!!)

        if (principal == null)                                             { println("Evento principal não encontrado."); return }
        if (!principal.ativo)                                              { println("Evento principal não está ativo."); return }
        if (principal.fim.isBefore(agora))                                 { println("Evento principal já encerrou."); return }
        if (contarIngressosAtivos(principal.id) >= principal.capacidade)   { println("Evento principal esgotado."); return }

        val ingPrincipal = Ingresso(usuarioId = usuario.id, eventoId = principal.id, valorPago = principal.preco)
        val ingSubEvento = Ingresso(usuarioId = usuario.id, eventoId = evento.id,    valorPago = evento.preco)

        salvarIngresso(ingPrincipal)
        salvarIngresso(ingSubEvento)

        val total = principal.preco + evento.preco
        println("Compra concluída! Total: R$ ${"%.2f".format(total)}")
        println("  - ${ingPrincipal.id} (${principal.nome})")
        println("  - ${ingSubEvento.id} (${evento.nome})")

    } else {
        val ingresso = Ingresso(usuarioId = usuario.id, eventoId = evento.id, valorPago = evento.preco)
        salvarIngresso(ingresso)
        println("Ingresso comprado! ID: ${ingresso.id} | Valor: R$ ${"%.2f".format(evento.preco)}")
    }
}

// -------------------------------------------------------
// US14 - Cancelar Ingresso
// -------------------------------------------------------
fun cancelarIngresso() {
    println("\n--- Cancelar Ingresso ---")

    val email   = readString("E-mail do usuário: ", "E-mail não pode ser vazio.", minLength = 1)
    val usuario = findUsuarioPorEmail(email)

    if (usuario == null) { println("Usuário não encontrado."); return }
    if (!usuario.ativo)  { println("Usuário inativo."); return }

    val meusIngressos = filterIngressosPorUsuario(usuario.id)

    if (meusIngressos.isEmpty()) {
        println("Você não possui ingressos.")
        return
    }

    println("\nSeus ingressos:")
    meusIngressos.forEach { ing ->
        val nomeEv = findEventoPorId(ing.eventoId)?.nome ?: "Evento removido"
        println("  - ${ing.id} | $nomeEv | ${ing.status}")
    }

    val idIng   = readString("ID do ingresso a cancelar: ", "ID não pode ser vazio.", minLength = 1)
    val ingresso = ingressos.find { it.id == idIng && it.usuarioId == usuario.id }

    if (ingresso == null)                              { println("Ingresso não encontrado."); return }
    if (ingresso.status == StatusIngresso.CANCELADO)   { println("Ingresso já está cancelado."); return }

    ingresso.status = StatusIngresso.CANCELADO
    alterarIngresso(ingresso)

    val evento = findEventoPorId(ingresso.eventoId)

    if (evento != null && evento.estornaEmCancelamento) {
        val taxa    = evento.taxaEstornoPercent.coerceIn(0.0, 100.0)
        val estorno = ingresso.valorPago * (1.0 - taxa / 100.0)
        println("Ingresso cancelado. Reembolso: R$ ${"%.2f".format(estorno)} (taxa ${taxa}%)")
    } else {
        println("Ingresso cancelado. Sem reembolso.")
    }

    println("Vaga liberada.")
}

// -------------------------------------------------------
// US15 - Listar Ingressos do Usuário
// -------------------------------------------------------
fun listarIngressosDoUsuario() {
    println("\n--- Listar Ingressos do Usuário ---")

    val email   = readString("E-mail do usuário: ", "E-mail não pode ser vazio.", minLength = 1)
    val usuario = findUsuarioPorEmail(email)

    if (usuario == null) { println("Usuário não encontrado."); return }

    val agora   = LocalDateTime.now()
    val todos   = filterIngressosPorUsuario(usuario.id)

    if (todos.isEmpty()) {
        println("Nenhum ingresso encontrado.")
        return
    }

    val ativos = todos.filter { ing ->
        ing.status == StatusIngresso.ATIVO &&
        findEventoPorId(ing.eventoId)?.let { it.ativo && it.fim.isAfter(agora) } == true
    }

    val historico = todos.filter { ing ->
        ing.status == StatusIngresso.CANCELADO ||
        findEventoPorId(ing.eventoId)?.fim?.isBefore(agora) == true
    }

    println("\n>> ATIVOS <<")
    if (ativos.isEmpty()) println("(nenhum)")
    else printTable("Ingressos ativos", ativos)

    println("\n>> CANCELADOS / ENCERRADOS <<")
    if (historico.isEmpty()) println("(nenhum)")
    else printTable("Histórico", historico)
}

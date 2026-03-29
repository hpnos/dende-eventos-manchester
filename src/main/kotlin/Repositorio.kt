// Listas que funcionam como banco de dados em memória
val usuarios  = mutableListOf<Usuario>()
val eventos   = mutableListOf<Evento>()
val ingressos = mutableListOf<Ingresso>()

// -------------------------------------------------------
// Funções de Usuario
// -------------------------------------------------------

fun salvarUsuario(usuario: Usuario) {
    usuarios.add(usuario)
}

fun alterarUsuario(usuarioAtualizado: Usuario) {
    val index = usuarios.indexOfFirst { it.id == usuarioAtualizado.id }
    if (index >= 0) usuarios[index] = usuarioAtualizado
}

fun excluirUsuario(id: String) {
    usuarios.removeIf { it.id == id }
}

fun findUsuarioPorEmail(email: String): Usuario? {
    return usuarios.find { it.email.equals(email, ignoreCase = true) }
}

fun filterUsuariosAtivos(): List<Usuario> {
    return usuarios.filter { it.ativo }
}

// -------------------------------------------------------
// Funções de Evento
// -------------------------------------------------------

fun salvarEvento(evento: Evento) {
    eventos.add(evento)
}

fun alterarEvento(eventoAtualizado: Evento) {
    val index = eventos.indexOfFirst { it.id == eventoAtualizado.id }
    if (index >= 0) eventos[index] = eventoAtualizado
}

fun excluirEvento(id: String) {
    eventos.removeIf { it.id == id }
}

fun findEventoPorId(id: String): Evento? {
    return eventos.find { it.id == id }
}

fun filterEventosPorOrganizador(organizadorId: String): List<Evento> {
    return eventos.filter { it.organizadorId == organizadorId }
}

fun filterEventosAtivosDisponiveis(): List<Evento> {
    val agora = java.time.LocalDateTime.now()
    return eventos.filter { ev ->
        ev.ativo &&
        ev.fim.isAfter(agora) &&
        contarIngressosAtivos(ev.id) < ev.capacidade
    }
}

fun desativarEventosExpirados() {
    val agora = java.time.LocalDateTime.now()
    for (ev in eventos) {
        if (ev.ativo && ev.fim.isBefore(agora)) {
            ev.ativo = false
        }
    }
}

// -------------------------------------------------------
// Funções de Ingresso
// -------------------------------------------------------

fun salvarIngresso(ingresso: Ingresso) {
    ingressos.add(ingresso)
}

fun alterarIngresso(ingressoAtualizado: Ingresso) {
    val index = ingressos.indexOfFirst { it.id == ingressoAtualizado.id }
    if (index >= 0) ingressos[index] = ingressoAtualizado
}

fun excluirIngresso(id: String) {
    ingressos.removeIf { it.id == id }
}

fun findIngressoPorId(id: String): Ingresso? {
    return ingressos.find { it.id == id }
}

fun filterIngressosPorUsuario(usuarioId: String): List<Ingresso> {
    return ingressos.filter { it.usuarioId == usuarioId }
}

fun filterIngressosAtivosPorEvento(eventoId: String): List<Ingresso> {
    return ingressos.filter { it.eventoId == eventoId && it.status == StatusIngresso.ATIVO }
}

fun contarIngressosAtivos(eventoId: String): Int {
    return ingressos.count { it.eventoId == eventoId && it.status == StatusIngresso.ATIVO }
}

import java.time.LocalDate
import java.time.Period

// -------------------------------------------------------
// US1 - Cadastrar Usuário Comum
// -------------------------------------------------------
fun cadastrarUsuarioComum() {
    println("\n--- Cadastrar Usuário Comum ---")

    val nome  = readString("Nome: ", "O nome não pode ser vazio.", minLength = 1)
    val dn    = lerData("Data de nascimento (dd/MM/yyyy): ")
    if (dn == null) { println("Data inválida."); return }
    val sexo  = lerSexo()
    val email = readString("E-mail: ", "O e-mail não pode ser vazio.", minLength = 1)
    val senha = readString("Senha: ", "A senha não pode ser vazia.", minLength = 1)

    if (findUsuarioPorEmail(email) != null) {
        println("Já existe um usuário com esse e-mail.")
        return
    }

    val usuario = Usuario(nome = nome, dataNascimento = dn, sexo = sexo, email = email, senha = senha)
    salvarUsuario(usuario)
    println("Usuário comum cadastrado com sucesso!")
}

// -------------------------------------------------------
// US2 - Cadastrar Usuário Organizador
// -------------------------------------------------------
fun cadastrarUsuarioOrganizador() {
    println("\n--- Cadastrar Usuário Organizador ---")

    val nome  = readString("Nome: ", "O nome não pode ser vazio.", minLength = 1)
    val dn    = lerData("Data de nascimento (dd/MM/yyyy): ")
    if (dn == null) { println("Data inválida."); return }
    val sexo  = lerSexo()
    val email = readString("E-mail: ", "O e-mail não pode ser vazio.", minLength = 1)
    val senha = readString("Senha: ", "A senha não pode ser vazia.", minLength = 1)

    if (findUsuarioPorEmail(email) != null) {
        println("Já existe um usuário com esse e-mail.")
        return
    }

    var empresa: Empresa? = null
    if (lerSimNao("Possui empresa? (s/n): ")) {
        val cnpj     = readString("CNPJ: ", "CNPJ não pode ser vazio.", minLength = 1)
        val razao    = readString("Razão Social: ", "Razão Social não pode ser vazia.", minLength = 1)
        val fantasia = readString("Nome Fantasia: ", "Nome Fantasia não pode ser vazio.", minLength = 1)
        empresa = Empresa(cnpj = cnpj, razaoSocial = razao, nomeFantasia = fantasia)
    }

    val usuario = Usuario(nome = nome, dataNascimento = dn, sexo = sexo, email = email, senha = senha, empresa = empresa)
    salvarUsuario(usuario)
    println("Usuário organizador cadastrado com sucesso!")
}

// -------------------------------------------------------
// US3 - Alterar Perfil do Usuário
// -------------------------------------------------------
fun alterarPerfilDoUsuario() {
    println("\n--- Alterar Perfil do Usuário ---")

    val email = readString("E-mail: ", "E-mail não pode ser vazio.", minLength = 1)
    val senha = readString("Senha: ", "Senha não pode ser vazia.", minLength = 1)

    val usuario = findUsuarioPorEmail(email)
    if (usuario == null) { println("Usuário não encontrado."); return }
    if (usuario.senha != senha) { println("Senha incorreta."); return }
    if (!usuario.ativo) { println("Usuário inativo. Reative primeiro."); return }

    val novoNome  = readString("Novo nome (enter mantém): ", "")
    val novaDnStr = readString("Nova data de nascimento (dd/MM/yyyy) (enter mantém): ", "")
    val novoSxStr = readString("Novo sexo (1=M, 2=F, 3=O, 4=PND) (enter mantém): ", "")
    val novaSenha = readString("Nova senha (enter mantém): ", "")

    if (novoNome.isNotBlank()) usuario.nome = novoNome
    if (novaSenha.isNotBlank()) usuario.senha = novaSenha

    if (novaDnStr.isNotBlank()) {
        val novaData = runCatching { java.time.LocalDate.parse(novaDnStr, formatoData) }.getOrNull()
        if (novaData != null) usuario.dataNascimento = novaData
        else println("Data inválida, mantendo a anterior.")
    }

    if (novoSxStr.isNotBlank()) {
        usuario.sexo = when (novoSxStr) {
            "1"  -> Sexo.MASCULINO
            "2"  -> Sexo.FEMININO
            "3"  -> Sexo.OUTRO
            else -> Sexo.PREFIRO_NAO_DIZER
        }
    }

    if (usuario.empresa != null && lerSimNao("Alterar dados da empresa? (s/n): ")) {
        val cnpj     = readString("CNPJ (enter mantém): ", "")
        val razao    = readString("Razão Social (enter mantém): ", "")
        val fantasia = readString("Nome Fantasia (enter mantém): ", "")
        if (cnpj.isNotBlank())     usuario.empresa!!.cnpj         = cnpj
        if (razao.isNotBlank())    usuario.empresa!!.razaoSocial  = razao
        if (fantasia.isNotBlank()) usuario.empresa!!.nomeFantasia = fantasia
    }

    alterarUsuario(usuario)
    println("Perfil atualizado! O e-mail não pode ser alterado.")
}

// -------------------------------------------------------
// US4 - Visualizar Perfil do Usuário
// -------------------------------------------------------
fun visualizarPerfilDoUsuario() {
    println("\n--- Visualizar Perfil do Usuário ---")

    val email   = readString("E-mail: ", "E-mail não pode ser vazio.", minLength = 1)
    val usuario = findUsuarioPorEmail(email)

    if (usuario == null) { println("Usuário não encontrado."); return }

    val periodo = Period.between(usuario.dataNascimento, LocalDate.now())

    println("\n=== PERFIL ===")
    println("Nome:       ${usuario.nome}")
    println("E-mail:     ${usuario.email}")
    println("Ativo:      ${if (usuario.ativo) "SIM" else "NÃO"}")
    println("Nascimento: ${usuario.dataNascimento.format(formatoData)}")
    println("Idade:      ${periodo.years} anos, ${periodo.months} meses, ${periodo.days} dias")
    println("Sexo:       ${usuario.sexo}")

    if (usuario.empresa != null) {
        println("\n--- Empresa ---")
        println("CNPJ:          ${usuario.empresa!!.cnpj}")
        println("Razão Social:  ${usuario.empresa!!.razaoSocial}")
        println("Nome Fantasia: ${usuario.empresa!!.nomeFantasia}")
    }
}

// -------------------------------------------------------
// US5 - Inativar Usuário
// -------------------------------------------------------
fun inativarUsuario() {
    println("\n--- Inativar Usuário ---")

    val email   = readString("E-mail: ", "E-mail não pode ser vazio.", minLength = 1)
    val senha   = readString("Senha: ", "Senha não pode ser vazia.", minLength = 1)
    val usuario = findUsuarioPorEmail(email)

    if (usuario == null) { println("Usuário não encontrado."); return }
    if (usuario.senha != senha) { println("Senha incorreta."); return }
    if (!usuario.ativo) { println("Usuário já está inativo."); return }

    val agora = java.time.LocalDateTime.now()
    val temEventoAtivo = eventos.any { it.organizadorId == usuario.id && it.ativo && it.fim.isAfter(agora) }

    if (temEventoAtivo) {
        println("Não é possível inativar. O organizador possui eventos ativos ou em andamento.")
        return
    }

    usuario.ativo = false
    alterarUsuario(usuario)
    println("Usuário inativado com sucesso.")
}

// -------------------------------------------------------
// US6 - Reativar Usuário
// -------------------------------------------------------
fun reativarUsuario() {
    println("\n--- Reativar Usuário ---")

    val email   = readString("E-mail: ", "E-mail não pode ser vazio.", minLength = 1)
    val senha   = readString("Senha: ", "Senha não pode ser vazia.", minLength = 1)
    val usuario = findUsuarioPorEmail(email)

    if (usuario == null) { println("Usuário não encontrado."); return }
    if (usuario.senha != senha) { println("Senha incorreta."); return }

    usuario.ativo = true
    alterarUsuario(usuario)
    println("Usuário reativado com sucesso.")
}

fun main() {

    var opcao: String

    do {
        desativarEventosExpirados()

        println("""
            
=== Dendê Eventos ===
-- Usuários --
1)  Cadastrar Usuário Comum
2)  Cadastrar Usuário Organizador
3)  Alterar Perfil do Usuário
4)  Visualizar Perfil do Usuário
5)  Inativar Usuário
6)  Reativar Usuário
-- Eventos --
7)  Cadastrar Evento
8)  Alterar Evento
9)  Ativar Evento
10) Desativar Evento
11) Listar Eventos do Organizador
12) Feed de Eventos
-- Ingressos --
13) Comprar Ingresso
14) Cancelar Ingresso
15) Listar Ingressos do Usuário
-- Sistema --
0)  Sair
        """.trimIndent())

        opcao = readString("Opção: ", "")

        when (opcao) {
            "1"  -> cadastrarUsuarioComum()
            "2"  -> cadastrarUsuarioOrganizador()
            "3"  -> alterarPerfilDoUsuario()
            "4"  -> visualizarPerfilDoUsuario()
            "5"  -> inativarUsuario()
            "6"  -> reativarUsuario()
            "7"  -> cadastrarEvento()
            "8"  -> alterarEvento()
            "9"  -> ativarEvento()
            "10" -> desativarEvento()
            "11" -> listarEventosDoOrganizador()
            "12" -> feedDeEventos()
            "13" -> comprarIngresso()
            "14" -> cancelarIngresso()
            "15" -> listarIngressosDoUsuario()
            "0"  -> println("Saindo...")
            else -> println("Opção inválida.")
        }

    } while (opcao != "0")
}

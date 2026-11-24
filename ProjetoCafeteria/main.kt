// Café & Companhia
// Integrantes: Joice Esperafico Sipp e Kelly Larissa Thomas

import java.util.Scanner
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
val scanner = Scanner(System.`in`, "UTF-8")

enum class CategoriaProduto(val descricao: String) {
    BEBIDA("Bebida"),
    COMIDA("Comida")
}

open class Produto(
    val nome: String,
    var preco: Double,
    var estoque: Int,
    val categoria: CategoriaProduto) {
    open fun exibirInformacoes() {
        println("Nome: $nome")
        println("Preco: R$ $preco")
        println("Estoque: $estoque")
        println("Categoria: ${categoria.descricao}")
    }
    override fun toString(): String {
        return "$nome - R$ $preco - Estoque: $estoque"
    }
}

class Bebida(
    nome: String,
    preco: Double,
    estoque: Int
) : Produto(nome, preco, estoque, CategoriaProduto.BEBIDA) {

    override fun exibirInformacoes() {
        super.exibirInformacoes()
        println("Tipo especifico: Bebida")
    }
}

class Comida(
    nome: String,
    preco: Double,
    estoque: Int
) : Produto(nome, preco, estoque, CategoriaProduto.COMIDA) {

    override fun exibirInformacoes() {
        super.exibirInformacoes()
        println("Tipo especifico: Comida")
    }
}

data class ItemPedido(
    val produto: Produto,
    var quantidade: Int
) {
    fun calcularSubtotal(): Double {
        return produto.preco * quantidade
    }
}


data class ItemVenda(
    val nomeProduto: String,
    val precoUnitario: Double,
    val quantidade: Int
) {
    fun calcularSubtotal(): Double {
        return precoUnitario * quantidade
    }
}



data class Venda(
    val itens: List<ItemVenda>,
    val total: Double,
    val data: LocalDateTime = LocalDateTime.now()
) {
    fun exibirResumo() {
        val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

        println("Data: ${data.format(formato)}")
        println("Itens vendidos:")

        for (item in itens) {
            println(
                "${item.nomeProduto} - " +
                "${item.quantidade} unidade(s) - " +
                "Subtotal: R$ ${item.calcularSubtotal()}"
            )
        }

        println("Total da venda: R$ $total")
    }
}


class Pedido {
    val itens = mutableListOf<ItemPedido>()
    fun copiarItem(item: ItemPedido): ItemPedido {
        return item.copy()
    }
    fun adicionarItem(produto: Produto, quantidade: Int) {
        val itemExistente = itens.find { it.produto.nome == produto.nome }
        if (itemExistente != null) {
            itemExistente.quantidade += quantidade
        } else {
            itens.add(ItemPedido(produto, quantidade))
        }
    }
    fun adicionarItem(produto: Produto) {
        adicionarItem(produto, 1)
    }

    fun calcularTotal(): Double {
        var total = 0.0
        for (item in itens) {
            total += item.calcularSubtotal()
        }
        return total
    }

    fun exibirPedido() {
        println("\n=== PEDIDO ===")
        if (itens.isEmpty()) {
            println("Pedido vazio.")
            return
        }
        for (item in itens) {
            println(
                "${item.produto.nome} | " +
                "${item.quantidade} unidade(s) | " +
                "Subtotal: R$ ${item.calcularSubtotal()}"
            )
        }
        println("Total: R$ ${calcularTotal()}")
    }
}

object Cafeteria {
    val produtos = mutableListOf<Produto>()
    val estoquePorProduto = HashMap<String, Int>()
    val vendas = mutableListOf<Venda>()
}

fun cadastrarProduto() {
    println("\n=== CADASTRO DE PRODUTO ===")
    print("Nome do produto: ")
    val nome = scanner.nextLine()?.trim() ?: ""
    if (nome.isBlank()) {
        println("Nome invalido!")
        return
    }
    if (Cafeteria.produtos.any { it.nome.equals(nome, ignoreCase = true) }) {
        println("Ja existe um produto com esse nome!")
        return
    }
    print("Preco: ")
    val preco = scanner.nextLine()?.toDoubleOrNull() ?: -1.0
    if (preco <= 0) {
        println("Preco invalido!")
        return
    }
    print("Quantidade em estoque: ")
    val estoque = scanner.nextLine()?.toIntOrNull() ?: -1
    if (estoque < 0) {
        println("Estoque invalido!")
        return
    }
    println("Categoria:")

    for ((indice, categoria) in CategoriaProduto.values().withIndex()) {
        println("${indice + 1} - ${categoria.descricao}")
    }
    print("Escolha: ")
    val escolha = scanner.nextLine()?.toIntOrNull() ?: 0
    val produto = if (escolha == 1) {
        Bebida(nome, preco, estoque)
    } else if (escolha == 2) {
        Comida(nome, preco, estoque)
    } else {
        println("Categoria invalida!")
        return
    }
    Cafeteria.produtos.add(produto)
    Cafeteria.estoquePorProduto[produto.nome] = produto.estoque
    println("\nProduto cadastrado com sucesso!")
}

fun listarProdutos() {
    println("\n=== LISTA DE PRODUTOS ===")
    if (Cafeteria.produtos.isEmpty()) {
        println("Nenhum produto cadastrado.")
        return
    }
    for ((indice, produto) in Cafeteria.produtos.withIndex()) {
        println("${indice + 1}. $produto")
    }
    println("\n=== INFORMACOES DETALHADAS ===")

    for (produto in Cafeteria.produtos) {
        produto.exibirInformacoes()
        println("--------------------")
    }
}


fun consultarEstoque() {
    println("\n=== CONSULTA DE ESTOQUE ===")
    if (Cafeteria.estoquePorProduto.isEmpty()) {
        println("Nenhum produto cadastrado.")
        return
    }
    for ((nome, quantidade) in Cafeteria.estoquePorProduto) {
        println("$nome - Estoque: $quantidade")
    }
}

fun adicionarProdutosAoPedido(pedido: Pedido) {
    while (true) {
        println("\n=== PRODUTOS DISPONIVEIS ===")
        if (Cafeteria.produtos.isEmpty()) {
            println("Nenhum produto cadastrado.")
            return
        }
        for ((indice, produto) in Cafeteria.produtos.withIndex()) {
            println("${indice + 1} - $produto")
        }
        println("0 - Finalizar")
        print("Escolha um produto: ")
        val escolha = scanner.nextLine().toIntOrNull() ?: -1
        if (escolha == 0) {
            return
        }
        if (escolha !in 1..Cafeteria.produtos.size) {
            println("Escolha invalida!")
            continue
        }
        val produto = Cafeteria.produtos[escolha - 1]
        val quantidadeJaAdicionada = pedido.itens
            .find { it.produto.nome == produto.nome }?.quantidade ?: 0
        val estoqueDisponivel = produto.estoque - quantidadeJaAdicionada
        if (estoqueDisponivel <= 0) {
            println("Produto sem quantidade disponivel para adicionar.")
            continue
        }
        print("Quantidade desejada: ")
        val quantidade = scanner.nextLine().toIntOrNull() ?: 0
        if (quantidade <= 0) {
            println("Quantidade invalida!")
            continue
        }
        if (quantidade > estoqueDisponivel) {
            println("Quantidade maior que o estoque disponivel!")
            println("Disponivel: $estoqueDisponivel")
            continue
        }
        pedido.adicionarItem(produto, quantidade)
        println("Produto adicionado ao pedido!")
        pedido.exibirPedido()
    }
}

fun montarPedido(): Pedido? {
    val pedido = Pedido()
    adicionarProdutosAoPedido(pedido)
    if (pedido.itens.isEmpty()) {
        println("Pedido cancelado ou vazio.")
        return null
    }
    println("\n=== REVISAO DO PEDIDO ===")
    pedido.exibirPedido()
    return pedido
}

fun finalizarVenda(pedido: Pedido): Boolean {
    println("\n=== FINALIZAR VENDA ===")
    pedido.exibirPedido()
    print("\nConfirmar venda? (s/n): ")
    val confirmacao = scanner.nextLine().trim().lowercase()
    if (confirmacao != "s") {
        println("Venda cancelada.")
        return false
    }
    for (item in pedido.itens) {
        if (item.quantidade > item.produto.estoque) {
            println("Estoque insuficiente para ${item.produto.nome}!")
            return false
        }
    }
    for (item in pedido.itens) {
        item.produto.estoque -= item.quantidade
        Cafeteria.estoquePorProduto[item.produto.nome] =
            item.produto.estoque
    }
    
    val venda = Venda(
        itens = pedido.itens.map {
            ItemVenda(
                nomeProduto = it.produto.nome,
                precoUnitario = it.produto.preco,
                quantidade = it.quantidade
            )
        },
        total = pedido.calcularTotal()
    )


    Cafeteria.vendas.add(venda)

    println("\nVenda realizada com sucesso!")
    println("Total recebido: R$ ${venda.total}")
    return true
    }

fun realizarPedido() {
    val pedido = montarPedido() ?: return
    finalizarVenda(pedido)
}

fun main() {
    var opcao: Int
    do {
        println("\n=== CAFE & COMPANHIA ===")
        println("1 - Cadastrar produto")
        println("2 - Listar produtos")
        println("3 - Consultar estoque")
        println("4 - Realizar pedido")
        println("5 - Consultar produtos por categoria")
        println("6 - Ordenar produtos por preco")
        println("7 - Pesquisar produtos")
        println("8 - Consultar historico de vendas")
        println("9 - Consultar faturamento")
        println("0 - Sair")
        print("Escolha uma opcao: ")
        opcao = scanner.nextLine()?.toIntOrNull() ?: -1
        when (opcao) {
            1 -> cadastrarProduto()
            2 -> listarProdutos()
            3 -> consultarEstoque()
            4 -> realizarPedido()
            5 -> consultarProdutosPorCategoria()
            6 -> ordenarProdutosPorPreco()
            7 -> pesquisarProdutos()
            8 -> consultarHistoricoVendas()
            9 -> consultarFaturamento()
            0 -> println("Encerrando o sistema...")
            else -> println("Opcao invalida!")
        }
    } while (opcao != 0)
}

fun filtrarProdutos(filtro: (Produto) -> Boolean): List<Produto> {
    return Cafeteria.produtos.filter(filtro)
}

fun consultarProdutosPorCategoria() {
    println("\n=== CONSULTA POR CATEGORIA ===")
    println("1 - Bebidas")
    println("2 - Comidas")
    print("Escolha uma categoria: ")

    val escolha = scanner.nextLine().toIntOrNull() ?: 0

    val categoriaEscolhida = if (escolha == 1) {
        CategoriaProduto.BEBIDA
    } else if (escolha == 2) {
        CategoriaProduto.COMIDA
    } else {
        println("Categoria invalida!")
        return
    }

    val resultado = filtrarProdutos { produto ->
        produto.categoria == categoriaEscolhida
    }

    if (resultado.isEmpty()) {
        println("Nenhum produto encontrado nessa categoria.")
    } else {
        println("\n=== PRODUTOS ENCONTRADOS ===")
        for (produto in resultado) {
            println(produto)
        }
    }
}

fun ordenarProdutosPorPreco() {
    println("\n=== PRODUTOS POR PRECO ===")
    println("1 - Menor para o maior preco")
    println("2 - Maior para o menor preco")
    print("Escolha uma opcao: ")

    val escolha = scanner.nextLine().toIntOrNull() ?: 0

    val produtosOrdenados = if (escolha == 1) {
        Cafeteria.produtos.sortedBy { it.preco }
    } else if (escolha == 2) {
        Cafeteria.produtos.sortedByDescending { it.preco }
    } else {
        println("Opcao invalida!")
        return
    }

    if (produtosOrdenados.isEmpty()) {
        println("Nenhum produto cadastrado.")
        return
    }

    println("\n=== LISTA ORDENADA ===")
    for (produto in produtosOrdenados) {
        println("$produto")
    }
}

fun registrarProduto(
    nome: String,
    preco: Double,
    estoque: Int = 0,
    categoria: CategoriaProduto = CategoriaProduto.BEBIDA
) {
    if (nome.isBlank() || preco <= 0 || estoque < 0) {
        println("Dados invalidos!")
        return
    }

    if (Cafeteria.produtos.any {
        it.nome.equals(nome, ignoreCase = true)
    }) {
        println("Ja existe um produto com esse nome!")
        return
    }

    val produto = if (categoria == CategoriaProduto.BEBIDA) {
        Bebida(nome, preco, estoque)
    } else {
        Comida(nome, preco, estoque)
    }

    Cafeteria.produtos.add(produto)
    Cafeteria.estoquePorProduto[nome] = estoque

    println("Produto registrado com sucesso!")
}

class ConsultorProdutos {

    fun pesquisar(termo: String): List<Produto> {
        return Cafeteria.produtos.filter {
            it.nome.contains(termo, ignoreCase = true)
        }
    }

    fun pesquisar(precoMaximo: Double): List<Produto> {
        return Cafeteria.produtos.filter {
            it.preco <= precoMaximo
        }
    }
}

fun pesquisarProdutos() {
    val consultor = ConsultorProdutos()

    println("\n=== PESQUISAR PRODUTOS ===")
    println("1 - Pesquisar pelo nome")
    println("2 - Pesquisar pelo preco")
    print("Escolha uma opcao: ")

    val escolha = scanner.nextLine().toIntOrNull() ?: 0

    val resultado = if (escolha == 1) {
        print("Digite o nome: ")
        val nome = scanner.nextLine()
        consultor.pesquisar(nome)

    } else if (escolha == 2) {
        print("Digite o preco maximo: ")
        val preco = scanner.nextLine().trim().replace(",", ".").toDoubleOrNull()

        if (preco == null || preco < 0) {
            println("Preco invalido!")
            return
        }

        consultor.pesquisar(preco)

    } else {
        println("Opcao invalida!")
        return
    }

    if (resultado.isEmpty()) {
        println("Nenhum produto encontrado.")
    } else {
        println("\n=== RESULTADO DA PESQUISA ===")
        for (produto in resultado) {
            println(produto)
        }
    }
}

fun consultarHistoricoVendas() {
    println("\n=== HISTORICO DE VENDAS ===")

    if (Cafeteria.vendas.isEmpty()) {
        println("Nenhuma venda realizada.")
        return
    }

    for ((indice, venda) in Cafeteria.vendas.withIndex()) {
        println("\nVenda ${indice + 1}")
        println("--------------------")
        venda.exibirResumo()
    }
}

fun consultarFaturamento() {
    println("\n=== RELATORIO DE FATURAMENTO ===")

    val quantidadeVendas = Cafeteria.vendas.size

    if (quantidadeVendas == 0) {
        println("Nenhuma venda realizada.")
        return
    }

    var faturamentoTotal = 0.0

    for (venda in Cafeteria.vendas) {
        faturamentoTotal += venda.total
    }

    val ticketMedio = faturamentoTotal / quantidadeVendas

    println("Quantidade de vendas: $quantidadeVendas")
    println("Faturamento total: R$ $faturamentoTotal")
    println("Ticket medio: R$ $ticketMedio")
}
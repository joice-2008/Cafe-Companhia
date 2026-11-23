// Café & Companhia
// Integrantes: Joice e Kelly

import java.util.Scanner
val scanner = Scanner(System.`in`, "windows-1252")

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
) : Produto(nome, preco, estoque, CategoriaProduto.BEBIDA)

class Comida(
    nome: String,
    preco: Double,
    estoque: Int
) : Produto(nome, preco, estoque, CategoriaProduto.COMIDA)

data class ItemPedido(
    val produto: Produto,
    var quantidade: Int
) {
    fun calcularSubtotal(): Double {
        return produto.preco * quantidade
    }
}

class Pedido {
    val itens = mutableListOf<ItemPedido>()
    fun adicionarItem(produto: Produto, quantidade: Int) {
        val itemExistente = itens.find { it.produto.nome == produto.nome }
        if (itemExistente != null) {
            itemExistente.quantidade += quantidade
        } else {
            itens.add(ItemPedido(produto, quantidade))
        }
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
    println("1 - Bebida")
    println("2 - Comida")
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
    println("\nVenda realizada com sucesso!")
    println("Total recebido: R$ ${pedido.calcularTotal()}")
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
        println("0 - Sair")
        print("Escolha uma opcao: ")
        opcao = scanner.nextLine()?.toIntOrNull() ?: -1
        when (opcao) {
            1 -> cadastrarProduto()
            2 -> listarProdutos()
            3 -> consultarEstoque()
            4 -> realizarPedido()
            0 -> println("Encerrando o sistema...")
            else -> println("Opcao invalida!")
        }
    } while (opcao != 0)
}
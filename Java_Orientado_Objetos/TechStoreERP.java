import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// =========================================================================
// CLASSES DE DOMÍNIO (Orientação a Objetos)
// =========================================================================
class Produto {
    int codigo;
    String nome;
    double custo;
    double preco;
    int estoque;
    int estoqueMinimo;

    public Produto(int codigo, String nome, double custo, double preco, int estoque, int estoqueMinimo) {
        this.codigo = codigo;
        this.nome = nome;
        this.custo = custo;
        this.preco = preco;
        this.estoque = estoque;
        this.estoqueMinimo = estoqueMinimo;
    }
}

class Cliente {
    String cpf;
    String nome;
    double totalGasto;

    public Cliente(String cpf, String nome) {
        this.cpf = cpf;
        this.nome = nome;
        this.totalGasto = 0;
    }
}

class ItemVenda {
    Produto produto;
    int quantidade;
    double custoUnitario; // Guardamos o valor no momento da compra
    double precoUnitario; 

    public ItemVenda(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.custoUnitario = produto.custo;
        this.precoUnitario = produto.preco;
    }

    public double getSubtotalVenda() { return precoUnitario * quantidade; }
    public double getSubtotalCusto() { return custoUnitario * quantidade; }
}

class Venda {
    int id;
    Cliente cliente;
    List<ItemVenda> itens = new ArrayList<>();
    double desconto;
    double totalFinal;
    String formaPagamento;

    public Venda(int id, Cliente cliente) {
        this.id = id;
        this.cliente = cliente;
    }
}

// =========================================================================
// CLASSE PRINCIPAL DO SISTEMA
// =========================================================================
public class TechStoreERP {
    // --- ESTADO GLOBAL (BANCO DE DADOS EM MEMÓRIA) ---
    static List<Produto> catalogo = new ArrayList<>();
    static List<Cliente> clientes = new ArrayList<>();
    static List<Venda> historicoVendas = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    static String usuarioAtual = "";
    static boolean caixaAberto = false;
    static double fundoCaixa = 0, totalDinheiro = 0, totalCartao = 0, totalPix = 0, despesasCompras = 0;
    static int contadorVendas = 0;

    public static void main(String[] args) {
        iniciarDados();
        login();

        int opcao = -1;
        while (opcao != 0) {
            cabecalho();
            System.out.println("---------------- MENU PRINCIPAL ----------------");
            System.out.println(" 1 - PDV: Nova Venda (Frente de Caixa)");
            
            if (usuarioAtual.equals("ADMIN")) {
                System.out.println(" 2 - GERÊNCIA: Cadastrar Novo Produto");
                System.out.println(" 3 - GERÊNCIA: Repor Estoque (Compras)");
                System.out.println(" 4 - GERÊNCIA: Abrir ou Fechar Caixa");
                System.out.println(" 5 - GERÊNCIA: Relatórios Financeiros e CRM");
            }
            
            System.out.println(" 6 - Sistema: Trocar Usuário (Logoff)");
            System.out.println(" 0 - Sair do Sistema");
            System.out.println("------------------------------------------------");
            opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1: pdvFrenteDeCaixa(); break;
                case 2: if (usuarioAtual.equals("ADMIN")) cadastrarProduto(); break;
                case 3: if (usuarioAtual.equals("ADMIN")) entradaMercadoria(); break;
                case 4: if (usuarioAtual.equals("ADMIN")) abrirFecharCaixa(); break;
                case 5: if (usuarioAtual.equals("ADMIN")) relatoriosAdmin(); break;
                case 6: login(); break;
                case 0: System.out.println("Encerrando TechStore ERP..."); break;
                default: System.out.println("Opção inválida!"); pausar();
            }
        }
    }

    // =========================================================================
    // MÓDULOS DE GERENCIAMENTO (ADMIN)
    // =========================================================================
    static void cadastrarProduto() {
        cabecalho();
        System.out.println("--- CADASTRAR NOVO PRODUTO ---");
        listarProdutos();
        System.out.println("");

        int cod = lerInteiro("Código numérico (Novo): ");
        System.out.print("Nome/Descrição do Produto: ");
        String nome = scanner.nextLine().toUpperCase();
        double custo = lerDouble("Custo Unitário de Fábrica R$: ");
        double preco = lerDouble("Preço Unitário de Venda R$: ");
        int estoque = lerInteiro("Quantidade Inicial em Estoque: ");
        int estqMin = lerInteiro("Alerta de Estoque Mínimo: ");

        catalogo.add(new Produto(cod, nome, custo, preco, estoque, estqMin));
        despesasCompras += (custo * estoque);

        System.out.println("\nProduto cadastrado com sucesso!");
        pausar();
    }

    static void entradaMercadoria() {
        cabecalho();
        System.out.println("--- REPOSIÇÃO DE ESTOQUE ---");
        listarProdutos();
        
        int cod = lerInteiro("Digite o CÓDIGO do produto fornecido: ");
        Produto p = buscarProduto(cod);

        if (p != null) {
            System.out.println("Produto: " + p.nome);
            int qtd = lerInteiro("Quantidade recebida: ");
            p.estoque += qtd;
            despesasCompras += (p.custo * qtd);
            System.out.println("Estoque atualizado para: " + p.estoque + " unidades.");
        } else {
            System.out.println("ERRO: Produto não encontrado!");
        }
        pausar();
    }

    static void abrirFecharCaixa() {
        cabecalho();
        if (!caixaAberto) {
            System.out.println("--- ABERTURA DE CAIXA ---");
            fundoCaixa = lerDouble("Informe o fundo de caixa (Troco Inicial) R$: ");
            caixaAberto = true;
            System.out.println("Caixa aberto com sucesso!");
        } else {
            System.out.println("--- FECHAMENTO DE CAIXA (Z-READING) ---");
            System.out.printf(" Fundo Inicial (Troco) : R$ %.2f\n", fundoCaixa);
            System.out.printf(" Entradas em Dinheiro  : R$ %.2f\n", totalDinheiro);
            System.out.println("-----------------------------------------");
            System.out.printf(" TOTAL ESPERADO GAVETA : R$ %.2f\n\n", (fundoCaixa + totalDinheiro));
            System.out.printf(" Total Cartão          : R$ %.2f\n", totalCartao);
            System.out.printf(" Total PIX             : R$ %.2f\n", totalPix);
            System.out.println("-----------------------------------------");
            
            caixaAberto = false;
            totalDinheiro = 0; totalCartao = 0; totalPix = 0;
            System.out.println("Caixa fechado com sucesso!");
        }
        pausar();
    }

    // =========================================================================
    // FRENTE DE CAIXA (PDV)
    // =========================================================================
    static void pdvFrenteDeCaixa() {
        if (!caixaAberto) {
            cabecalho();
            System.out.println("ERRO: O Caixa está fechado! Solicite a abertura ao ADMIN.");
            pausar();
            return;
        }

        cabecalho();
        System.out.println("--- NOVA VENDA ---");
        
        System.out.print("Informe o CPF do cliente (ENTER para anônimo): ");
        String cpf = scanner.nextLine().trim();
        Cliente clienteAtual = null;

        if (!cpf.isEmpty()) {
            clienteAtual = buscarCliente(cpf);
            if (clienteAtual == null) {
                System.out.print("Cliente novo. Informe o Nome: ");
                String nome = scanner.nextLine().toUpperCase();
                clienteAtual = new Cliente(cpf, nome);
                clientes.add(clienteAtual);
                System.out.println("Cliente cadastrado com sucesso!");
            } else {
                System.out.println("Bem-vindo de volta, " + clienteAtual.nome + "!");
            }
        }

        contadorVendas++;
        Venda vendaAtual = new Venda(contadorVendas, clienteAtual);
        double subtotal = 0;
        String continuar = "S";

        while (continuar.equalsIgnoreCase("S")) {
            cabecalho();
            listarProdutos();
            System.out.printf("\n=== CARRINHO ATUAL: R$ %.2f ===\n", subtotal);
            
            int cod = lerInteiro("Código do Produto: ");
            Produto p = buscarProduto(cod);

            if (p != null) {
                System.out.printf("Produto: %s | R$ %.2f\n", p.nome, p.preco);
                int qtd = lerInteiro("Quantidade: ");

                if (qtd <= p.estoque) {
                    p.estoque -= qtd;
                    vendaAtual.itens.add(new ItemVenda(p, qtd));
                    subtotal += (p.preco * qtd);
                    System.out.println(">> Item adicionado!");
                } else {
                    System.out.println(">> ERRO: Estoque insuficiente! (Disponível: " + p.estoque + ")");
                }
            } else {
                System.out.println(">> ERRO: Código Inválido!");
            }
            
            System.out.print("Passar outro item? (S/N): ");
            continuar = scanner.nextLine();
        }

        if (subtotal > 0) {
            cabecalho();
            System.out.println("--- FINALIZAÇÃO DA VENDA ---");
            System.out.printf("Subtotal: R$ %.2f\n", subtotal);
            double desconto = lerDouble("Desconto R$ (0 para nenhum): ");
            
            double totalPagar = Math.max(0, subtotal - desconto);
            vendaAtual.desconto = desconto;
            vendaAtual.totalFinal = totalPagar;
            
            System.out.printf("TOTAL A PAGAR: R$ %.2f\n", totalPagar);
            System.out.println("Forma de Pagamento: [1] Dinheiro  [2] Cartão  [3] PIX");
            String forma = scanner.nextLine();

            if (forma.equals("1")) {
                vendaAtual.formaPagamento = "DINHEIRO";
                double valorRecebido = lerDouble("Valor recebido R$: ");
                while (valorRecebido < totalPagar) {
                    System.out.println("Valor insuficiente!");
                    valorRecebido = lerDouble("Valor recebido R$: ");
                }
                double troco = valorRecebido - totalPagar;
                totalDinheiro += totalPagar;
                System.out.printf("TROCO A DEVOLVER: R$ %.2f\n", troco);
            } else if (forma.equals("2")) {
                vendaAtual.formaPagamento = "CARTÃO";
                totalCartao += totalPagar;
                System.out.println("Transação aprovada!");
            } else {
                vendaAtual.formaPagamento = "PIX";
                totalPix += totalPagar;
                System.out.println("PIX recebido!");
            }

            historicoVendas.add(vendaAtual);
            if (clienteAtual != null) clienteAtual.totalGasto += totalPagar;

            System.out.println("\n===========================================");
            System.out.println("     CUPOM FISCAL GERADO COM SUCESSO!      ");
            System.out.println("===========================================");
        } else {
            System.out.println("Venda cancelada.");
            contadorVendas--;
        }
        pausar();
    }

    // =========================================================================
    // RELATÓRIOS
    // =========================================================================
    static void relatoriosAdmin() {
        cabecalho();
        System.out.println("--- PAINEL DE CONTROLE FINANCEIRO E CRM ---");
        
        System.out.println("\n>> ALERTAS DE ESTOQUE:");
        for (Produto p : catalogo) {
            if (p.estoque <= p.estoqueMinimo) {
                System.out.println("   [CRÍTICO] " + p.nome + " - Restam apenas " + p.estoque + " un.");
            }
        }

        double faturamentoTotal = 0;
        double custoTotalVendido = 0;

        for (Venda v : historicoVendas) {
            faturamentoTotal += v.totalFinal;
            for (ItemVenda item : v.itens) {
                custoTotalVendido += item.getSubtotalCusto();
            }
        }

        double lucroLiquido = faturamentoTotal - custoTotalVendido - despesasCompras;

        System.out.println("\n>> BALANÇO FINANCEIRO DA LOJA:");
        System.out.printf("   Faturamento Bruto (Vendas) : R$ %.2f\n", faturamentoTotal);
        System.out.printf("   Custo Mercadorias Vendidas : R$ %.2f\n", custoTotalVendido);
        System.out.printf("   Despesas c/ Novas Compras  : R$ %.2f\n", despesasCompras);
        System.out.println("   ---------------------------------------");
        System.out.printf("   LUCRO LÍQUIDO REAL         : R$ %.2f\n", lucroLiquido);

        System.out.println("\n>> BASE DE CLIENTES (CRM):");
        if (clientes.isEmpty()) {
            System.out.println("   Nenhum cliente cadastrado.");
        } else {
            for (Cliente c : clientes) {
                System.out.printf("   %s | CPF: %s | Total Gasto: R$ %.2f\n", c.nome, c.cpf, c.totalGasto);
            }
        }
        pausar();
    }

    // =========================================================================
    // MÉTODOS AUXILIARES
    // =========================================================================
    static void login() {
        cabecalho();
        System.out.println("--- CONTROLE DE ACESSO ---");
        System.out.println(" 1 - Entrar como CAIXA (Operador)");
        System.out.println(" 2 - Entrar como ADMIN (Gerente)");
        int perfil = lerInteiro("Selecione o perfil: ");

        if (perfil == 2) {
            System.out.print("Digite a senha (Dica: 123): ");
            String senha = scanner.nextLine();
            if (senha.equals("123")) {
                usuarioAtual = "ADMIN";
            } else {
                System.out.println("Senha incorreta! Acesso restrito a Caixa.");
                usuarioAtual = "CAIXA";
                pausar();
            }
        } else {
            usuarioAtual = "CAIXA";
        }
    }

    static void listarProdutos() {
        System.out.println("--- CATÁLOGO DE PRODUTOS ---");
        for (Produto p : catalogo) {
            if (usuarioAtual.equals("ADMIN")) {
                System.out.printf("Cód: %d | %s | Estq: %d | Custo: R$ %.2f | Venda: R$ %.2f\n", 
                                  p.codigo, p.nome, p.estoque, p.custo, p.preco);
            } else {
                System.out.printf("Cód: %d | %s | Estq: %d | Venda: R$ %.2f\n", 
                                  p.codigo, p.nome, p.estoque, p.preco);
            }
        }
        System.out.println("------------------------------------------------------");
    }

    static void cabecalho() {
        // Simula limpeza de tela
        for (int i = 0; i < 30; i++) System.out.println();
        System.out.println("======================================================");
        System.out.println("           TechStore - ERP & Frente de Caixa          ");
        System.out.println("======================================================");
        System.out.println(" Operador: " + usuarioAtual);
        System.out.println(" Status do Caixa: " + (caixaAberto ? "ABERTO" : "FECHADO"));
        System.out.println("======================================================");
    }

    static void pausar() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }

    static int lerInteiro(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("ERRO: Digite um número inteiro válido.");
            }
        }
    }

    static double lerDouble(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return Double.parseDouble(scanner.nextLine().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("ERRO: Digite um valor numérico válido.");
            }
        }
    }

    static Produto buscarProduto(int codigo) {
        for (Produto p : catalogo) if (p.codigo == codigo) return p;
        return null;
    }

    static Cliente buscarCliente(String cpf) {
        for (Cliente c : clientes) if (c.cpf.equals(cpf)) return c;
        return null;
    }

    static void iniciarDados() {
        catalogo.add(new Produto(101, "PLACA MÃE ASUS B550M", 500.00, 750.00, 10, 5));
        catalogo.add(new Produto(102, "PROCESSADOR INTEL CORE I5 12400F", 800.00, 1150.00, 10, 5));
        catalogo.add(new Produto(103, "PROCESSADOR AMD RYZEN 5 5600G", 750.00, 1050.00, 10, 5));
        catalogo.add(new Produto(104, "MEMÓRIA RAM 16GB DDR4 KINGSTON", 180.00, 280.00, 10, 5));
        catalogo.add(new Produto(105, "SSD 1TB NVME M.2 KINGSTON", 300.00, 450.00, 10, 5));
        catalogo.add(new Produto(106, "PLACA DE VÍDEO RTX 3060 12GB", 1600.00, 2200.00, 10, 5));
        catalogo.add(new Produto(107, "FONTE CORSAIR 650W 80 PLUS BRONZE", 280.00, 420.00, 10, 5));
        catalogo.add(new Produto(108, "GABINETE GAMER ATX VIDRO TEMPERADO", 150.00, 290.00, 10, 5));
        catalogo.add(new Produto(109, "MONITOR IPS 24 POL FULL HD LG", 550.00, 780.00, 10, 5));
        catalogo.add(new Produto(110, "KIT TECLADO E MOUSE MECÂNICO RGB", 120.00, 220.00, 10, 5));
    }
}
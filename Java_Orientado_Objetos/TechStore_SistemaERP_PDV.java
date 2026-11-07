import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.time.LocalDate; // NOVO: Importa a captura de data
import java.time.format.DateTimeFormatter; // NOVO: Importa o formatador de data

// --- CLASSES DE DOMÍNIO ---
class Produto {
    int codigo;
    String nome;
    double custo;
    double preco;
    int estoque;

    public Produto(int codigo, String nome, double custo, double preco, int estoque) {
        this.codigo = codigo;
        this.nome = nome;
        this.custo = custo;
        this.preco = preco;
        this.estoque = estoque;
    }
}

class ItemVenda {
    Produto produto;
    int quantidade;

    public ItemVenda(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public double getSubtotalCusto() { return produto.custo * quantidade; }
    public double getSubtotalVenda() { return produto.preco * quantidade; }
}

class Venda {
    int id;
    String data;
    ArrayList<ItemVenda> itens;

    public Venda(int id, String data) {
        this.id = id;
        this.data = data;
        this.itens = new ArrayList<>();
    }
    
    public double getTotalVenda() {
        double total = 0;
        for (ItemVenda item : itens) {
            total += item.getSubtotalVenda();
        }
        return total;
    }
}

// --- CLASSE PRINCIPAL DO SISTEMA ---
public class TechStore_SistemaERP_PDV {
    static ArrayList<Produto> estoque = new ArrayList<>();
    static ArrayList<Venda> historicoVendas = new ArrayList<>();
    static int contadorVendas = 0;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        carregarDadosIniciais();
        int opcaoMain = -1;

        while (opcaoMain != 4) {
            cabecalho();
            System.out.println("---------------- MENU PRINCIPAL ----------------");
            System.out.println(" 1 - Gerenciar Produtos (Estoque)");
            System.out.println(" 2 - Realizar Venda (Frente de Caixa)");
            System.out.println(" 3 - Relatórios e Faturamento");
            System.out.println(" 4 - Sair do Sistema");
            System.out.println("------------------------------------------------");
            opcaoMain = lerInteiroSeguro("Escolha uma opção: ");

            switch (opcaoMain) {
                case 1: menuProdutos(); break;
                case 2: realizarVenda(); break;
                case 3: menuRelatorios(); break;
                case 4: System.out.println("Encerrando o sistema TechStore..."); break;
                default: System.out.println("Opção inválida!"); pausar();
            }
        }
    }

    // ====================================================
    // MÓDULO 1: GERENCIAR PRODUTOS (CRUD)
    // ====================================================
    static void menuProdutos() {
        int opcao = -1;
        while (opcao != 0) {
            cabecalho();
            System.out.println("--- SUBMENU: GERENCIAR PRODUTOS ---");
            System.out.println(" 1 - Incluir Produto");
            System.out.println(" 2 - Editar Produto");
            System.out.println(" 3 - Excluir Produto");
            System.out.println(" 4 - Listar Produtos");
            System.out.println(" 0 - Voltar");
            opcao = lerInteiroSeguro("Opção: ");

            switch (opcao) {
                case 1:
                    listarProdutosSimples("PRODUTOS JÁ CADASTRADOS");
                    System.out.println("\n-- INSERIR NOVO PRODUTO --");
                    int cod = lerInteiroSeguro("Código numérico: ");
                    System.out.print("Nome/Descrição (Maiúsculas): ");
                    String nome = scanner.nextLine().toUpperCase();
                    double custo = lerDoubleSeguro("Preço de Custo Unitário (R$): ");
                    double preco = lerDoubleSeguro("Preço de Venda Unitário (R$): ");
                    int qtd = lerInteiroSeguro("Quantidade Inicial em Estoque: ");
                    estoque.add(new Produto(cod, nome, custo, preco, qtd));
                    System.out.println("Produto cadastrado com sucesso!");
                    pausar();
                    break;
                case 2:
                    listarProdutosSimples("PRODUTOS JÁ CADASTRADOS");
                    int codEditar = lerInteiroSeguro("\nDigite o CÓDIGO do produto para editar: ");
                    Produto pEditar = buscarProdutoPorCodigo(codEditar);
                    if (pEditar != null) {
                        System.out.println("Editando: " + pEditar.nome);
                        pEditar.codigo = lerInteiroSeguro("Novo Código: ");
                        System.out.print("Novo Nome: ");
                        pEditar.nome = scanner.nextLine().toUpperCase();
                        pEditar.custo = lerDoubleSeguro("Novo Preço de Custo (R$): ");
                        pEditar.preco = lerDoubleSeguro("Novo Preço de Venda (R$): ");
                        pEditar.estoque = lerInteiroSeguro("Nova Quantidade em Estoque: ");
                        System.out.println("Atualizado com sucesso!");
                    } else {
                        System.out.println("Código não encontrado!");
                    }
                    pausar();
                    break;
                case 3:
                    int codExcluir = lerInteiroSeguro("Digite o CÓDIGO do produto para excluir: ");
                    Produto pExcluir = buscarProdutoPorCodigo(codExcluir);
                    if (pExcluir != null) {
                        estoque.remove(pExcluir);
                        System.out.println("Produto excluído com sucesso!");
                    } else {
                        System.out.println("Código não encontrado!");
                    }
                    pausar();
                    break;
                case 4:
                    cabecalho();
                    System.out.println("--- ESTOQUE ATUAL ---");
                    for (Produto p : estoque) {
                        System.out.printf("Cód: %d | %s\n   Estoque: %d un | Custo Un: R$ %.2f | Venda Un: R$ %.2f\n-\n", 
                                          p.codigo, p.nome, p.estoque, p.custo, p.preco);
                    }
                    pausar();
                    break;
                case 0: break;
                default: System.out.println("Opção inválida."); pausar();
            }
        }
    }

    static void listarProdutosSimples(String titulo) {
        cabecalho();
        System.out.println("--- ATENÇÃO: " + titulo + " ---");
        for (Produto p : estoque) {
            System.out.printf("[Cód: %d] - Estoque: %d | %s\n", p.codigo, p.estoque, p.nome);
        }
        System.out.println("----------------------------------------");
    }

    static Produto buscarProdutoPorCodigo(int codigo) {
        for (Produto p : estoque) {
            if (p.codigo == codigo) return p;
        }
        return null;
    }

    // ====================================================
    // MÓDULO 2: FRENTE DE CAIXA
    // ====================================================
    static void realizarVenda() {
        cabecalho();
        System.out.println("--- NOVA VENDA ---");
        
        // CAPTURA AUTOMÁTICA DA DATA DO SISTEMA
        LocalDate hoje = LocalDate.now();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataAtual = hoje.format(formatador);
        
        System.out.println("Data da Venda: " + dataAtual); // Exibe a data capturada para o caixa
        
        contadorVendas++;
        Venda vendaAtual = new Venda(contadorVendas, dataAtual);
        String resp = "S";

        while (resp.equalsIgnoreCase("S")) {
            System.out.println("\n--- PRODUTOS DISPONÍVEIS ---");
            for (Produto p : estoque) {
                System.out.printf("[Cód: %d] - R$ %.2f | Estq: %d | %s\n", p.codigo, p.preco, p.estoque, p.nome);
            }
            
            int codBusca = lerInteiroSeguro("Digite o CÓDIGO do produto: ");
            Produto produtoEncontrado = buscarProdutoPorCodigo(codBusca);

            if (produtoEncontrado != null) {
                System.out.printf(">> Selecionado: %s\n>> Valor Unitário: R$ %.2f\n", 
                                  produtoEncontrado.nome, produtoEncontrado.preco);
                int qtd = lerInteiroSeguro("Quantidade: ");

                // VALIDAÇÃO DE ESTOQUE
                if (qtd <= produtoEncontrado.estoque) {
                    produtoEncontrado.estoque -= qtd; // Abate automático
                    vendaAtual.itens.add(new ItemVenda(produtoEncontrado, qtd));
                    System.out.println("--> Item adicionado ao carrinho! Estoque restante: " + produtoEncontrado.estoque);
                } else {
                    System.out.println("ERRO: Estoque insuficiente! Você tem apenas " + produtoEncontrado.estoque + " unidades disponíveis.");
                }
            } else {
                System.out.println("ERRO: Produto não cadastrado!");
            }

            System.out.print("\nDeseja passar mais um item nesta venda? (S/N): ");
            resp = scanner.nextLine();
        }

        if (!vendaAtual.itens.isEmpty()) {
            historicoVendas.add(vendaAtual);
            
            // CUPOM FISCAL
            cabecalho();
            System.out.println("======================================================");
            System.out.println("                  CUPOM FISCAL                        ");
            System.out.println(" Data da Compra: " + vendaAtual.data);
            System.out.println(" Venda ID: " + vendaAtual.id);
            System.out.println("======================================================");
            
            for (ItemVenda item : vendaAtual.itens) {
                System.out.println(item.produto.nome);
                System.out.printf("  %d un  x  R$ %.2f = Subtotal: R$ %.2f\n-\n", 
                                  item.quantidade, item.produto.preco, item.getSubtotalVenda());
            }
            
            System.out.println("======================================================");
            System.out.printf(" TOTAL A PAGAR: R$ %.2f\n", vendaAtual.getTotalVenda());
            System.out.println("======================================================");
        } else {
            System.out.println("Venda cancelada (nenhum item no carrinho).");
            contadorVendas--; // Reverte o ID se a venda não foi concluída
        }
        pausar();
    }

    // ====================================================
    // MÓDULO 3: RELATÓRIOS E FATURAMENTO
    // ====================================================
    static void menuRelatorios() {
        int opcao = -1;
        while (opcao != 0) {
            cabecalho();
            System.out.println("--- SUBMENU: RELATÓRIOS E FATURAMENTO ---");
            System.out.println(" 1 - Histórico Detalhado por Venda");
            System.out.println(" 2 - Faturamento e Lucro por Produto");
            System.out.println(" 0 - Voltar");
            opcao = lerInteiroSeguro("Opção: ");

            switch (opcao) {
                case 1:
                    cabecalho();
                    System.out.println("--- HISTÓRICO DE VENDAS REALIZADAS ---");
                    if (historicoVendas.isEmpty()) System.out.println("Nenhuma venda realizada ainda.");
                    
                    for (Venda v : historicoVendas) {
                        System.out.println("======================================");
                        System.out.printf(" VENDA ID: %d | DATA: %s\n", v.id, v.data);
                        System.out.println("======================================");
                        
                        for (ItemVenda item : v.itens) {
                            System.out.printf(" -> %dx %s\n", item.quantidade, item.produto.nome);
                            System.out.printf("    Custo Un: R$ %.2f | Venda Un: R$ %.2f\n", item.produto.custo, item.produto.preco);
                            System.out.printf("    Subtotal: R$ %.2f\n", item.getSubtotalVenda());
                            System.out.println(" - - - - - - - - - - - - - - - - - - -");
                        }
                        System.out.printf(" TOTAL DA VENDA: R$ %.2f\n\n", v.getTotalVenda());
                    }
                    pausar();
                    break;
                case 2:
                    cabecalho();
                    System.out.println("--- FATURAMENTO E LUCRO POR PRODUTO ---");
                    if (estoque.isEmpty()) {
                        System.out.println("Estoque vazio.");
                    } else {
                        double fatGeral = 0;
                        double lucroGeral = 0;

                        for (Produto p : estoque) {
                            int qtdVendidaTotal = 0;
                            double fatBruto = 0, custoAcumulado = 0;

                            for (Venda v : historicoVendas) {
                                for (ItemVenda item : v.itens) {
                                    if (item.produto.codigo == p.codigo) {
                                        qtdVendidaTotal += item.quantidade;
                                        custoAcumulado += item.getSubtotalCusto();
                                        fatBruto += item.getSubtotalVenda();
                                    }
                                }
                            }

                            if (qtdVendidaTotal > 0) {
                                double lucroLiquido = fatBruto - custoAcumulado;
                                fatGeral += fatBruto;
                                lucroGeral += lucroLiquido;

                                System.out.printf("Cód: %d | %s\n", p.codigo, p.nome);
                                System.out.println(" -> Unidades Vendidas: " + qtdVendidaTotal);
                                System.out.printf(" -> Custo Total (Base): R$ %.2f\n", custoAcumulado);
                                System.out.printf(" -> Faturamento Bruto: R$ %.2f\n", fatBruto);
                                System.out.printf(" -> LUCRO LÍQUIDO: R$ %.2f\n", lucroLiquido);
                                System.out.println("......................................");
                            }
                        }
                        
                        // RODAPÉ COM O RESUMO FINANCEIRO DA LOJA
                        if (fatGeral > 0) {
                            System.out.println("\n======================================");
                            System.out.println("        BALANÇO GERAL DA LOJA         ");
                            System.out.println("======================================");
                            System.out.printf(" FATURAMENTO TOTAL : R$ %.2f\n", fatGeral);
                            System.out.printf(" LUCRO LÍQUIDO TOTAL: R$ %.2f\n", lucroGeral);
                            System.out.println("======================================");
                        } else {
                            System.out.println("Nenhum registro de faturamento encontrado.");
                        }
                    }
                    pausar();
                    break;
                case 0: break;
                default: System.out.println("Opção inválida!"); pausar();
            }
        }
    }

    // ====================================================
    // MÉTODOS AUXILIARES E DE SEGURANÇA
    // ====================================================
    static void cabecalho() {
        System.out.print("\033[H\033[2J"); System.out.flush(); 
        System.out.println("======================================================");
        System.out.println("           TechStore - Sistema de Gestão ERP          ");
        System.out.println("======================================================");
    }

    static void pausar() {
        System.out.print("Pressione ENTER para continuar...");
        scanner.nextLine();
    }

    static int lerInteiroSeguro(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                int valor = scanner.nextInt();
                scanner.nextLine(); 
                return valor;
            } catch (InputMismatchException e) {
                System.out.println("ERRO: Digite apenas números inteiros válidos!");
                scanner.nextLine();
            }
        }
    }

    static double lerDoubleSeguro(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                double valor = scanner.nextDouble();
                scanner.nextLine(); 
                return valor;
            } catch (InputMismatchException e) {
                System.out.println("ERRO: Digite um valor numérico válido (use vírgula para casas decimais).");
                scanner.nextLine();
            }
        }
    }

    static void carregarDadosIniciais() {
        estoque.add(new Produto(101, "PLACA MÃE ASUS B550M", 500.00, 750.00, 10));
        estoque.add(new Produto(102, "PROCESSADOR INTEL CORE I5 12400F", 800.00, 1150.00, 10));
        estoque.add(new Produto(103, "PROCESSADOR AMD RYZEN 5 5600G", 750.00, 1050.00, 10));
        estoque.add(new Produto(104, "MEMÓRIA RAM 16GB DDR4 KINGSTON", 180.00, 280.00, 10));
        estoque.add(new Produto(105, "SSD 1TB NVME M.2 KINGSTON", 300.00, 450.00, 10));
        estoque.add(new Produto(106, "PLACA DE VÍDEO RTX 3060 12GB", 1600.00, 2200.00, 10));
        estoque.add(new Produto(107, "FONTE CORSAIR 650W 80 PLUS BRONZE", 280.00, 420.00, 10));
        estoque.add(new Produto(108, "GABINETE GAMER ATX VIDRO TEMPERADO", 150.00, 290.00, 10));
        estoque.add(new Produto(109, "MONITOR IPS 24 POL FULL HD LG", 550.00, 780.00, 10));
        estoque.add(new Produto(110, "KIT TECLADO E MOUSE MECÂNICO RGB", 120.00, 220.00, 10));
    }
}
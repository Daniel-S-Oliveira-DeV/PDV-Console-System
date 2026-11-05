import java.util.ArrayList;
import java.util.Scanner;

public class SistemaCaixaCRUD {

    // 1. ESTRUTURAS DE DADOS (Substituem os vetores do VisuAlg)
    static class Produto {
        int codigo;
        String nome;
        double preco;

        public Produto(int codigo, String nome, double preco) {
            this.codigo = codigo;
            this.nome = nome;
            this.preco = preco;
        }
    }

    static class ItemVenda {
        Produto produto;
        int quantidade;
        double subtotal;

        public ItemVenda(Produto produto, int quantidade) {
            this.produto = produto;
            this.quantidade = quantidade;
            this.subtotal = produto.preco * quantidade;
        }
    }

    // 2. FUNÇÕES DE VALIDAÇÃO (Impedem o sistema de quebrar com dados errados)
    public static int lerInteiro(Scanner sc, String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("ERRO: Dado inválido! Por favor, digite apenas números inteiros.");
            }
        }
    }

    public static double lerDouble(Scanner sc, String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                // Substitui vírgula por ponto para evitar erros de formatação regional
                String entrada = sc.nextLine().replace(",", ".");
                return Double.parseDouble(entrada);
            } catch (NumberFormatException e) {
                System.out.println("ERRO: Dado inválido! Por favor, digite um número válido (ex: 10.50).");
            }
        }
    }

    public static void cabecalhoLoja() {
        System.out.println("\n==================================================");
        System.out.println("             GESTOR DE PRODUTOS                  ");
        System.out.println(" Sistema de Gestao e Frente de Caixa - Modulo PDV ");
        System.out.println("==================================================\n");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Produto> estoque = new ArrayList<>(); // Lista dinâmica (sem limite de 50)
        int opcao;

        do {
            cabecalhoLoja();
            System.out.println("-------------- MENU PRINCIPAL --------------");
            System.out.println(" 1 - Incluir Produto");
            System.out.println(" 2 - Excluir Produto");
            System.out.println(" 3 - Lista de Produtos (Editar Cadastro)");
            System.out.println(" 4 - ABRIR CAIXA (Fazer Venda)");
            System.out.println(" 5 - Sair do Sistema");
            System.out.println("--------------------------------------------");
            
            opcao = lerInteiro(sc, "Escolha uma opção: ");

            switch (opcao) {
                // ====================================================
                // OPÇÃO 1: INCLUIR PRODUTO (Create)
                // ====================================================
                case 1:
                    String resp;
                    do {
                        cabecalhoLoja();
                        System.out.println("--- CADASTRAR PRODUTO ---");
                        int codigo = lerInteiro(sc, "Código Numérico do Produto: ");
                        
                        System.out.print("Nome/Descrição: ");
                        String nome = sc.nextLine();
                        
                        double preco = lerDouble(sc, "Valor Unitário (R$): ");

                        // Adiciona um novo objeto Produto à lista
                        estoque.add(new Produto(codigo, nome, preco));
                        System.out.println("--> Produto salvo com sucesso!");

                        System.out.print("\nDeseja cadastrar outro produto? (S/N): ");
                        resp = sc.nextLine().toUpperCase();
                    } while (resp.equals("S"));
                    break;

                // ====================================================
                // OPÇÃO 2: EXCLUIR PRODUTO (Delete)
                // ====================================================
                case 2:
                    if (estoque.isEmpty()) {
                        System.out.println("Nenhum produto cadastrado. Pressione ENTER para voltar...");
                        sc.nextLine();
                    } else {
                        cabecalhoLoja();
                        System.out.println("--- EXCLUIR PRODUTO ---");
                        for (int i = 0; i < estoque.size(); i++) {
                            System.out.printf("Índice [%d] - Cód: %d | %s\n", i, estoque.get(i).codigo, estoque.get(i).nome);
                        }
                        
                        int indice = lerInteiro(sc, "Digite o ÍNDICE do produto para EXCLUIR (ou -1 para cancelar): ");
                        
                        if (indice >= 0 && indice < estoque.size()) {
                            estoque.remove(indice); // O Java faz o "Shift" automaticamente!
                            System.out.println("Produto excluído com sucesso! Pressione ENTER para voltar...");
                            sc.nextLine();
                        }
                    }
                    break;

                // ====================================================
                // OPÇÃO 3: LISTAR E EDITAR (Read e Update)
                // ====================================================
                case 3:
                    if (estoque.isEmpty()) {
                        System.out.println("Nenhum produto cadastrado. Pressione ENTER para voltar...");
                        sc.nextLine();
                    } else {
                        int subOpcao;
                        do {
                            cabecalhoLoja();
                            System.out.println("--- ESTOQUE CADASTRADO ---");
                            for (int i = 0; i < estoque.size(); i++) {
                                Produto p = estoque.get(i);
                                System.out.printf("Índice [%d] | Cód: %d | Produto: %s | R$ %.2f\n", i, p.codigo, p.nome, p.preco);
                            }
                            System.out.println("--------------------------");
                            System.out.println("1 - Editar um Produto");
                            System.out.println("0 - Voltar ao Menu Principal");
                            
                            subOpcao = lerInteiro(sc, "Opção: ");

                            if (subOpcao == 1) {
                                int indice = lerInteiro(sc, "Digite o ÍNDICE do produto que deseja alterar: ");
                                
                                if (indice >= 0 && indice < estoque.size()) {
                                    Produto p = estoque.get(indice);
                                    System.out.println("- Digite os novos dados para '" + p.nome + "' -");
                                    
                                    p.codigo = lerInteiro(sc, "Novo Código: ");
                                    System.out.print("Novo Nome: ");
                                    p.nome = sc.nextLine();
                                    p.preco = lerDouble(sc, "Novo Preço (R$): ");
                                    
                                    System.out.println("-> Cadastro atualizado! Pressione ENTER...");
                                    sc.nextLine();
                                } else {
                                    System.out.println("Índice inválido! Pressione ENTER...");
                                    sc.nextLine();
                                }
                            }
                        } while (subOpcao != 0);
                    }
                    break;

                // ====================================================
                // OPÇÃO 4: CAIXA / FRENTE DE VENDAS
                // ====================================================
                case 4:
                    if (estoque.isEmpty()) {
                        System.out.println("Cadastre produtos antes de vender! Pressione ENTER...");
                        sc.nextLine();
                    } else {
                        ArrayList<ItemVenda> carrinho = new ArrayList<>();
                        double totalVenda = 0;
                        String respCaixa;
                        
                        do {
                            cabecalhoLoja();
                            System.out.println("--- FRENTE DE CAIXA: PRODUTOS DISPONÍVEIS ---");
                            for (Produto p : estoque) {
                                System.out.printf("Cód: %d | %s | R$ %.2f\n", p.codigo, p.nome, p.preco);
                            }
                            System.out.println("---------------------------------------------");
                            
                            int codBusca = lerInteiro(sc, "Digite o CÓDIGO do produto: ");
                            
                            // Busca o produto pelo código
                            Produto produtoEncontrado = null;
                            for (Produto p : estoque) {
                                if (p.codigo == codBusca) {
                                    produtoEncontrado = p;
                                    break;
                                }
                            }

                            if (produtoEncontrado != null) {
                                int qtd = lerInteiro(sc, "Quantidade vendida: ");
                                if (qtd > 0) {
                                    ItemVenda item = new ItemVenda(produtoEncontrado, qtd);
                                    carrinho.add(item);
                                    totalVenda += item.subtotal;
                                    System.out.println("> " + produtoEncontrado.nome + " adicionado ao cupom!");
                                } else {
                                    System.out.println("Quantidade deve ser maior que zero!");
                                }
                            } else {
                                System.out.println("ERRO: Código não encontrado!");
                            }
                            
                            System.out.print("\nPassar outro produto? (S/N): ");
                            respCaixa = sc.nextLine().toUpperCase();
                            
                        } while (respCaixa.equals("S"));

                        // IMPRESSÃO DA NOTA FISCAL
                        cabecalhoLoja();
                        System.out.println("==================================================");
                        System.out.println("                  CUPOM FISCAL                    ");
                        System.out.println("==================================================");
                        System.out.println("QTD  | PRODUTO               | V. UN   | SUBTOTAL");
                        System.out.println("--------------------------------------------------");
                        
                        for (ItemVenda item : carrinho) {
                            System.out.printf("%d un | %-21s | R$ %-5.2f | R$ %.2f\n", 
                                item.quantidade, item.produto.nome, item.produto.preco, item.subtotal);
                        }
                        
                        System.out.println("--------------------------------------------------");
                        System.out.printf("TOTAL DA VENDA: R$ %.2f\n", totalVenda);
                        System.out.println("==================================================");
                        System.out.println("Obrigado pela preferência!");
                        System.out.print("Pressione ENTER para voltar ao menu...");
                        sc.nextLine();
                    }
                    break;

                case 5:
                    System.out.println("Fechando o sistema. Até logo!");
                    break;

                default:
                    System.out.println("Opção inválida! Pressione ENTER para tentar novamente.");
                    sc.nextLine();
            }
        } while (opcao != 5);
        
        sc.close();
    }
}
using System;
using System.Collections.Generic;
using System.Linq;

namespace TechStoreERP
{
    // --- CLASSES DE DOMÍNIO ---
    class Produto
    {
        public int Codigo { get; set; }
        public string Nome { get; set; }
        public double Custo { get; set; }
        public double Preco { get; set; }
        public int Estoque { get; set; }

        public Produto(int codigo, string nome, double custo, double preco, int estoque)
        {
            Codigo = codigo;
            Nome = nome;
            Custo = custo;
            Preco = preco;
            Estoque = estoque;
        }
    }

    class ItemVenda
    {
        public Produto Produto { get; set; }
        public int Quantidade { get; set; }

        // Expressões Lambda para cálculo automático
        public double SubtotalCusto => Produto.Custo * Quantidade;
        public double SubtotalVenda => Produto.Preco * Quantidade;

        public ItemVenda(Produto produto, int quantidade)
        {
            Produto = produto;
            Quantidade = quantidade;
        }
    }

    class Venda
    {
        public int Id { get; set; }
        public string Data { get; set; }
        public List<ItemVenda> Itens { get; set; } = new List<ItemVenda>();

        // LINQ: Soma automaticamente o subtotal de todos os itens na lista
        public double TotalVenda => Itens.Sum(item => item.SubtotalVenda);

        public Venda(int id, string data)
        {
            Id = id;
            Data = data;
        }
    }

    // --- CLASSE PRINCIPAL DO SISTEMA ---
    class Program
    {
        static List<Produto> estoque = new List<Produto>();
        static List<Venda> historicoVendas = new List<Venda>();
        static int contadorVendas = 0;

        static void Main(string[] args)
        {
            CarregarDadosIniciais();
            int opcaoMain = -1;

            while (opcaoMain != 4)
            {
                Cabecalho();
                Console.WriteLine("---------------- MENU PRINCIPAL ----------------");
                Console.WriteLine(" 1 - Gerenciar Produtos (Estoque)");
                Console.WriteLine(" 2 - Realizar Venda (Frente de Caixa)");
                Console.WriteLine(" 3 - Relatórios e Faturamento");
                Console.WriteLine(" 4 - Sair do Sistema");
                Console.WriteLine("------------------------------------------------");
                
                opcaoMain = LerInteiroSeguro("Escolha uma opção: ");

                switch (opcaoMain)
                {
                    case 1: MenuProdutos(); break;
                    case 2: RealizarVenda(); break;
                    case 3: MenuRelatorios(); break;
                    case 4: Console.WriteLine("Encerrando o sistema TechStore..."); break;
                    default: Console.WriteLine("Opção inválida!"); Pausar(); break;
                }
            }
        }

        // ====================================================
        // MÓDULO 1: GERENCIAR PRODUTOS (CRUD)
        // ====================================================
        static void MenuProdutos()
        {
            int opcao = -1;
            while (opcao != 0)
            {
                Cabecalho();
                Console.WriteLine("--- SUBMENU: GERENCIAR PRODUTOS ---");
                Console.WriteLine(" 1 - Incluir Produto");
                Console.WriteLine(" 2 - Editar Produto");
                Console.WriteLine(" 3 - Excluir Produto");
                Console.WriteLine(" 4 - Listar Produtos");
                Console.WriteLine(" 0 - Voltar");
                
                opcao = LerInteiroSeguro("Opção: ");

                switch (opcao)
                {
                    case 1:
                        ListarProdutosSimples("PRODUTOS JÁ CADASTRADOS");
                        Console.WriteLine("\n-- INSERIR NOVO PRODUTO --");
                        int cod = LerInteiroSeguro("Código numérico: ");
                        Console.Write("Nome/Descrição (Maiúsculas): ");
                        string nome = Console.ReadLine().ToUpper();
                        double custo = LerDoubleSeguro("Preço de Custo Unitário (R$): ");
                        double preco = LerDoubleSeguro("Preço de Venda Unitário (R$): ");
                        int qtd = LerInteiroSeguro("Quantidade Inicial em Estoque: ");
                        
                        estoque.Add(new Produto(cod, nome, custo, preco, qtd));
                        Console.WriteLine("Produto cadastrado com sucesso!");
                        Pausar();
                        break;

                    case 2:
                        ListarProdutosSimples("PRODUTOS JÁ CADASTRADOS");
                        int codEditar = LerInteiroSeguro("\nDigite o CÓDIGO do produto para editar: ");
                        Produto pEditar = BuscarProdutoPorCodigo(codEditar);
                        
                        if (pEditar != null)
                        {
                            Console.WriteLine($"Editando: {pEditar.Nome}");
                            pEditar.Codigo = LerInteiroSeguro("Novo Código: ");
                            Console.Write("Novo Nome: ");
                            pEditar.Nome = Console.ReadLine().ToUpper();
                            pEditar.Custo = LerDoubleSeguro("Novo Preço de Custo (R$): ");
                            pEditar.Preco = LerDoubleSeguro("Novo Preço de Venda (R$): ");
                            pEditar.Estoque = LerInteiroSeguro("Nova Quantidade em Estoque: ");
                            Console.WriteLine("Atualizado com sucesso!");
                        }
                        else
                        {
                            Console.WriteLine("Código não encontrado!");
                        }
                        Pausar();
                        break;

                    case 3:
                        int codExcluir = LerInteiroSeguro("Digite o CÓDIGO do produto para excluir: ");
                        Produto pExcluir = BuscarProdutoPorCodigo(codExcluir);
                        
                        if (pExcluir != null)
                        {
                            estoque.Remove(pExcluir);
                            Console.WriteLine("Produto excluído com sucesso!");
                        }
                        else
                        {
                            Console.WriteLine("Código não encontrado!");
                        }
                        Pausar();
                        break;

                    case 4:
                        Cabecalho();
                        Console.WriteLine("--- ESTOQUE ATUAL ---");
                        foreach (var p in estoque)
                        {
                            Console.WriteLine($"Cód: {p.Codigo} | {p.Nome}\n   Estoque: {p.Estoque} un | Custo Un: R$ {p.Custo:F2} | Venda Un: R$ {p.Preco:F2}\n-");
                        }
                        Pausar();
                        break;

                    case 0: break;
                    default: Console.WriteLine("Opção inválida."); Pausar(); break;
                }
            }
        }

        static void ListarProdutosSimples(string titulo)
        {
            Cabecalho();
            Console.WriteLine($"--- ATENÇÃO: {titulo} ---");
            foreach (var p in estoque)
            {
                Console.WriteLine($"[Cód: {p.Codigo}] - Estoque: {p.Estoque} | {p.Nome}");
            }
            Console.WriteLine("----------------------------------------");
        }

        static Produto BuscarProdutoPorCodigo(int codigo)
        {
            // LINQ simplifica a busca na lista
            return estoque.FirstOrDefault(p => p.Codigo == codigo);
        }

        // ====================================================
        // MÓDULO 2: FRENTE DE CAIXA
        // ====================================================
        static void RealizarVenda()
        {
            Cabecalho();
            Console.WriteLine("--- NOVA VENDA ---");
            
            // Captura automática da data do sistema
            string dataAtual = DateTime.Now.ToString("dd/MM/yyyy");
            Console.WriteLine($"Data da Venda: {dataAtual}");
            
            contadorVendas++;
            Venda vendaAtual = new Venda(contadorVendas, dataAtual);
            string resp = "S";

            while (resp.Equals("S", StringComparison.OrdinalIgnoreCase))
            {
                Console.WriteLine("\n--- PRODUTOS DISPONÍVEIS ---");
                foreach (var p in estoque)
                {
                    Console.WriteLine($"[Cód: {p.Codigo}] - R$ {p.Preco:F2} | Estq: {p.Estoque} | {p.Nome}");
                }
                
                int codBusca = LerInteiroSeguro("Digite o CÓDIGO do produto: ");
                Produto produtoEncontrado = BuscarProdutoPorCodigo(codBusca);

                if (produtoEncontrado != null)
                {
                    Console.WriteLine($">> Selecionado: {produtoEncontrado.Nome}");
                    Console.WriteLine($">> Valor Unitário: R$ {produtoEncontrado.Preco:F2}");
                    int qtd = LerInteiroSeguro("Quantidade: ");

                    // VALIDAÇÃO DE ESTOQUE
                    if (qtd <= produtoEncontrado.Estoque)
                    {
                        produtoEncontrado.Estoque -= qtd; 
                        vendaAtual.Itens.Add(new ItemVenda(produtoEncontrado, qtd));
                        Console.WriteLine($"--> Item adicionado ao carrinho! Estoque restante: {produtoEncontrado.Estoque}");
                    }
                    else
                    {
                        Console.WriteLine($"ERRO: Estoque insuficiente! Você tem apenas {produtoEncontrado.Estoque} unidades disponíveis.");
                    }
                }
                else
                {
                    Console.WriteLine("ERRO: Produto não cadastrado!");
                }

                Console.Write("\nDeseja passar mais um item nesta venda? (S/N): ");
                resp = Console.ReadLine();
            }

            if (vendaAtual.Itens.Any())
            {
                historicoVendas.Add(vendaAtual);
                
                // CUPOM FISCAL
                Cabecalho();
                Console.WriteLine("======================================================");
                Console.WriteLine("                  CUPOM FISCAL                        ");
                Console.WriteLine($" Data da Compra: {vendaAtual.Data}");
                Console.WriteLine($" Venda ID: {vendaAtual.Id}");
                Console.WriteLine("======================================================");
                
                foreach (var item in vendaAtual.Itens)
                {
                    Console.WriteLine(item.Produto.Nome);
                    Console.WriteLine($"  {item.Quantidade} un  x  R$ {item.Produto.Preco:F2} = Subtotal: R$ {item.SubtotalVenda:F2}\n-");
                }
                
                Console.WriteLine("======================================================");
                Console.WriteLine($" TOTAL A PAGAR: R$ {vendaAtual.TotalVenda:F2}");
                Console.WriteLine("======================================================");
            }
            else
            {
                Console.WriteLine("Venda cancelada (nenhum item no carrinho).");
                contadorVendas--;
            }
            Pausar();
        }

        // ====================================================
        // MÓDULO 3: RELATÓRIOS E FATURAMENTO
        // ====================================================
        static void MenuRelatorios()
        {
            int opcao = -1;
            while (opcao != 0)
            {
                Cabecalho();
                Console.WriteLine("--- SUBMENU: RELATÓRIOS E FATURAMENTO ---");
                Console.WriteLine(" 1 - Histórico Detalhado por Venda");
                Console.WriteLine(" 2 - Faturamento e Lucro por Produto");
                Console.WriteLine(" 0 - Voltar");
                opcao = LerInteiroSeguro("Opção: ");

                switch (opcao)
                {
                    case 1:
                        Cabecalho();
                        Console.WriteLine("--- HISTÓRICO DE VENDAS REALIZADAS ---");
                        if (!historicoVendas.Any()) Console.WriteLine("Nenhuma venda realizada ainda.");
                        
                        foreach (var v in historicoVendas)
                        {
                            Console.WriteLine("======================================");
                            Console.WriteLine($" VENDA ID: {v.Id} | DATA: {v.Data}");
                            Console.WriteLine("======================================");
                            
                            foreach (var item in v.Itens)
                            {
                                Console.WriteLine($" -> {item.Quantidade}x {item.Produto.Nome}");
                                Console.WriteLine($"    Custo Un: R$ {item.Produto.Custo:F2} | Venda Un: R$ {item.Produto.Preco:F2}");
                                Console.WriteLine($"    Subtotal: R$ {item.SubtotalVenda:F2}");
                                Console.WriteLine(" - - - - - - - - - - - - - - - - - - -");
                            }
                            Console.WriteLine($" TOTAL DA VENDA: R$ {v.TotalVenda:F2}\n");
                        }
                        Pausar();
                        break;

                    case 2:
                        Cabecalho();
                        Console.WriteLine("--- FATURAMENTO E LUCRO POR PRODUTO ---");
                        if (!estoque.Any())
                        {
                            Console.WriteLine("Estoque vazio.");
                        }
                        else
                        {
                            double fatGeral = 0;
                            double lucroGeral = 0;

                            foreach (var p in estoque)
                            {
                                // Poder do LINQ: Extrai os dados do produto específico cruzando toda a base de vendas
                                var itensDesteProduto = historicoVendas
                                    .SelectMany(v => v.Itens)
                                    .Where(i => i.Produto.Codigo == p.Codigo)
                                    .ToList();

                                if (itensDesteProduto.Any())
                                {
                                    int qtdVendidaTotal = itensDesteProduto.Sum(i => i.Quantidade);
                                    double custoAcumulado = itensDesteProduto.Sum(i => i.SubtotalCusto);
                                    double fatBruto = itensDesteProduto.Sum(i => i.SubtotalVenda);
                                    
                                    double lucroLiquido = fatBruto - custoAcumulado;
                                    fatGeral += fatBruto;
                                    lucroGeral += lucroLiquido;

                                    Console.WriteLine($"Cód: {p.Codigo} | {p.Nome}");
                                    Console.WriteLine($" -> Unidades Vendidas: {qtdVendidaTotal}");
                                    Console.WriteLine($" -> Custo Total (Base): R$ {custoAcumulado:F2}");
                                    Console.WriteLine($" -> Faturamento Bruto: R$ {fatBruto:F2}");
                                    Console.WriteLine($" -> LUCRO LÍQUIDO: R$ {lucroLiquido:F2}");
                                    Console.WriteLine("......................................");
                                }
                            }
                            
                            if (fatGeral > 0)
                            {
                                Console.WriteLine("\n======================================");
                                Console.WriteLine("        BALANÇO GERAL DA LOJA         ");
                                Console.WriteLine("======================================");
                                Console.WriteLine($" FATURAMENTO TOTAL : R$ {fatGeral:F2}");
                                Console.WriteLine($" LUCRO LÍQUIDO TOTAL: R$ {lucroGeral:F2}");
                                Console.WriteLine("======================================");
                            }
                            else
                            {
                                Console.WriteLine("Nenhum registro de faturamento encontrado.");
                            }
                        }
                        Pausar();
                        break;
                    case 0: break;
                    default: Console.WriteLine("Opção inválida!"); Pausar(); break;
                }
            }
        }

        // ====================================================
        // MÉTODOS AUXILIARES E DE SEGURANÇA
        // ====================================================
        static void Cabecalho()
        {
            Console.Clear();
            Console.WriteLine("======================================================");
            Console.WriteLine("           TechStore - Sistema de Gestão ERP          ");
            Console.WriteLine("======================================================");
        }

        static void Pausar()
        {
            Console.Write("Pressione ENTER para continuar...");
            Console.ReadLine();
        }

        static int LerInteiroSeguro(string mensagem)
        {
            int valor;
            while (true)
            {
                Console.Write(mensagem);
                if (int.TryParse(Console.ReadLine(), out valor))
                {
                    return valor;
                }
                Console.WriteLine("ERRO: Digite apenas números inteiros válidos!");
            }
        }

        static double LerDoubleSeguro(string mensagem)
        {
            double valor;
            while (true)
            {
                Console.Write(mensagem);
                if (double.TryParse(Console.ReadLine(), out valor))
                {
                    return valor;
                }
                Console.WriteLine("ERRO: Digite um valor numérico válido (use vírgula para casas decimais).");
            }
        }

        static void CarregarDadosIniciais()
        {
            estoque.Add(new Produto(101, "PLACA MÃE ASUS B550M", 500.00, 750.00, 10));
            estoque.Add(new Produto(102, "PROCESSADOR INTEL CORE I5 12400F", 800.00, 1150.00, 10));
            estoque.Add(new Produto(103, "PROCESSADOR AMD RYZEN 5 5600G", 750.00, 1050.00, 10));
            estoque.Add(new Produto(104, "MEMÓRIA RAM 16GB DDR4 KINGSTON", 180.00, 280.00, 10));
            estoque.Add(new Produto(105, "SSD 1TB NVME M.2 KINGSTON", 300.00, 450.00, 10));
            estoque.Add(new Produto(106, "PLACA DE VÍDEO RTX 3060 12GB", 1600.00, 2200.00, 10));
            estoque.Add(new Produto(107, "FONTE CORSAIR 650W 80 PLUS BRONZE", 280.00, 420.00, 10));
            estoque.Add(new Produto(108, "GABINETE GAMER ATX VIDRO TEMPERADO", 150.00, 290.00, 10));
            estoque.Add(new Produto(109, "MONITOR IPS 24 POL FULL HD LG", 550.00, 780.00, 10));
            estoque.Add(new Produto(110, "KIT TECLADO E MOUSE MECÂNICO RGB", 120.00, 220.00, 10));
        }
    }
}
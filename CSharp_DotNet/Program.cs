using System;
using System.Collections.Generic;
using System.Linq;
using System.Globalization;

namespace TechStoreERP
{
    // =========================================================================
    // CLASSES DE DOMÍNIO (Orientação a Objetos em C#)
    // =========================================================================
    class Produto
    {
        public int Codigo { get; set; }
        public string Nome { get; set; }
        public double Custo { get; set; }
        public double Preco { get; set; }
        public int Estoque { get; set; }
        public int EstoqueMinimo { get; set; }

        public Produto(int codigo, string nome, double custo, double preco, int estoque, int estoqueMinimo)
        {
            Codigo = codigo; Nome = nome; Custo = custo; Preco = preco;
            Estoque = estoque; EstoqueMinimo = estoqueMinimo;
        }
    }

    class Cliente
    {
        public string Cpf { get; set; }
        public string Nome { get; set; }
        public double TotalGasto { get; set; }

        public Cliente(string cpf, string nome)
        {
            Cpf = cpf; Nome = nome; TotalGasto = 0;
        }
    }

    class ItemVenda
    {
        public Produto ProdutoObj { get; set; }
        public int Quantidade { get; set; }
        public double CustoUnitario { get; set; }
        public double PrecoUnitario { get; set; }

        public ItemVenda(Produto produto, int quantidade)
        {
            ProdutoObj = produto;
            Quantidade = quantidade;
            CustoUnitario = produto.Custo;
            PrecoUnitario = produto.Preco;
        }

        public double SubtotalVenda => PrecoUnitario * Quantidade;
        public double SubtotalCusto => CustoUnitario * Quantidade;
    }

    class Venda
    {
        public int Id { get; set; }
        public Cliente ClienteObj { get; set; }
        public List<ItemVenda> Itens { get; set; } = new List<ItemVenda>();
        public double Desconto { get; set; }
        public double TotalFinal { get; set; }
        public string FormaPagamento { get; set; }

        public Venda(int id, Cliente cliente)
        {
            Id = id; ClienteObj = cliente;
        }
    }

    // =========================================================================
    // CLASSE PRINCIPAL DO SISTEMA
    // =========================================================================
    class Program
    {
        static List<Produto> catalogo = new List<Produto>();
        static List<Cliente> clientes = new List<Cliente>();
        static List<Venda> historicoVendas = new List<Venda>();

        static string usuarioAtual = "";
        static bool caixaAberto = false;
        static double fundoCaixa = 0, totalDinheiro = 0, totalCartao = 0, totalPix = 0, despesasCompras = 0;
        static int contadorVendas = 0;

        static void Main(string[] args)
        {
            IniciarDados();
            Login();

            int opcao = -1;
            while (opcao != 0)
            {
                Cabecalho();
                Console.WriteLine("---------------- MENU PRINCIPAL ----------------");
                Console.WriteLine(" 1 - PDV: Nova Venda (Frente de Caixa)");

                if (usuarioAtual == "ADMIN")
                {
                    Console.WriteLine(" 2 - GERÊNCIA: Cadastrar Novo Produto");
                    Console.WriteLine(" 3 - GERÊNCIA: Repor Estoque (Compras)");
                    Console.WriteLine(" 4 - GERÊNCIA: Abrir ou Fechar Caixa");
                    Console.WriteLine(" 5 - GERÊNCIA: Relatórios Financeiros e CRM");
                }

                Console.WriteLine(" 6 - Sistema: Trocar Usuário (Logoff)");
                Console.WriteLine(" 0 - Sair do Sistema");
                Console.WriteLine("------------------------------------------------");
                opcao = LerInteiro("Escolha uma opção: ");

                switch (opcao)
                {
                    case 1: PdvFrenteDeCaixa(); break;
                    case 2: if (usuarioAtual == "ADMIN") CadastrarProduto(); break;
                    case 3: if (usuarioAtual == "ADMIN") EntradaMercadoria(); break;
                    case 4: if (usuarioAtual == "ADMIN") AbrirFecharCaixa(); break;
                    case 5: if (usuarioAtual == "ADMIN") RelatoriosAdmin(); break;
                    case 6: Login(); break;
                    case 0: Console.WriteLine("Encerrando TechStore ERP..."); break;
                    default: Console.WriteLine("Opção inválida!"); Pausar(); break;
                }
            }
        }

        // =========================================================================
        // MÓDULOS DE GERENCIAMENTO (ADMIN)
        // =========================================================================
        static void CadastrarProduto()
        {
            Cabecalho();
            Console.WriteLine("--- CADASTRAR NOVO PRODUTO ---");
            ListarProdutos();
            Console.WriteLine();

            int cod = LerInteiro("Código numérico (Novo): ");
            Console.Write("Nome/Descrição do Produto: ");
            string nome = Console.ReadLine().ToUpper();
            double custo = LerDouble("Custo Unitário de Fábrica R$: ");
            double preco = LerDouble("Preço Unitário de Venda R$: ");
            int estoque = LerInteiro("Quantidade Inicial em Estoque: ");
            int estqMin = LerInteiro("Alerta de Estoque Mínimo: ");

            catalogo.Add(new Produto(cod, nome, custo, preco, estoque, estqMin));
            despesasCompras += (custo * estoque);

            Console.WriteLine("\nProduto cadastrado com sucesso!");
            Pausar();
        }

        static void EntradaMercadoria()
        {
            Cabecalho();
            Console.WriteLine("--- REPOSIÇÃO DE ESTOQUE ---");
            ListarProdutos();

            int cod = LerInteiro("Digite o CÓDIGO do produto fornecido: ");
            Produto p = BuscarProduto(cod);

            if (p != null)
            {
                Console.WriteLine($"Produto: {p.Nome}");
                int qtd = LerInteiro("Quantidade recebida: ");
                p.Estoque += qtd;
                despesasCompras += (p.Custo * qtd);
                Console.WriteLine($"Estoque atualizado para: {p.Estoque} unidades.");
            }
            else
            {
                Console.WriteLine("ERRO: Produto não encontrado!");
            }
            Pausar();
        }

        static void AbrirFecharCaixa()
        {
            Cabecalho();
            if (!caixaAberto)
            {
                Console.WriteLine("--- ABERTURA DE CAIXA ---");
                fundoCaixa = LerDouble("Informe o fundo de caixa (Troco Inicial) R$: ");
                caixaAberto = true;
                Console.WriteLine("Caixa aberto com sucesso!");
            }
            else
            {
                Console.WriteLine("--- FECHAMENTO DE CAIXA (Z-READING) ---");
                Console.WriteLine($" Fundo Inicial (Troco) : R$ {fundoCaixa:F2}");
                Console.WriteLine($" Entradas em Dinheiro  : R$ {totalDinheiro:F2}");
                Console.WriteLine("-----------------------------------------");
                Console.WriteLine($" TOTAL ESPERADO GAVETA : R$ {(fundoCaixa + totalDinheiro):F2}\n");
                Console.WriteLine($" Total Cartão          : R$ {totalCartao:F2}");
                Console.WriteLine($" Total PIX             : R$ {totalPix:F2}");
                Console.WriteLine("-----------------------------------------");

                caixaAberto = false;
                totalDinheiro = 0; totalCartao = 0; totalPix = 0;
                Console.WriteLine("Caixa fechado com sucesso!");
            }
            Pausar();
        }

        // =========================================================================
        // FRENTE DE CAIXA (PDV)
        // =========================================================================
        static void PdvFrenteDeCaixa()
        {
            if (!caixaAberto)
            {
                Cabecalho();
                Console.WriteLine("ERRO: O Caixa está fechado! Solicite a abertura ao ADMIN.");
                Pausar();
                return;
            }

            Cabecalho();
            Console.WriteLine("--- NOVA VENDA ---");

            Console.Write("Informe o CPF do cliente (ENTER para anônimo): ");
            string cpf = Console.ReadLine().Trim();
            Cliente clienteAtual = null;

            if (!string.IsNullOrEmpty(cpf))
            {
                clienteAtual = BuscarCliente(cpf);
                if (clienteAtual == null)
                {
                    Console.Write("Cliente novo. Informe o Nome: ");
                    string nome = Console.ReadLine().ToUpper();
                    clienteAtual = new Cliente(cpf, nome);
                    clientes.Add(clienteAtual);
                    Console.WriteLine("Cliente cadastrado com sucesso!");
                }
                else
                {
                    Console.WriteLine($"Bem-vindo de volta, {clienteAtual.Nome}!");
                }
            }

            contadorVendas++;
            Venda vendaAtual = new Venda(contadorVendas, clienteAtual);
            double subtotal = 0;
            string continuar = "S";

            while (continuar.ToUpper() == "S")
            {
                Cabecalho();
                ListarProdutos();
                Console.WriteLine($"\n=== CARRINHO ATUAL: R$ {subtotal:F2} ===");

                int cod = LerInteiro("Código do Produto: ");
                Produto p = BuscarProduto(cod);

                if (p != null)
                {
                    Console.WriteLine($"Produto: {p.Nome} | R$ {p.Preco:F2}");
                    int qtd = LerInteiro("Quantidade: ");

                    if (qtd <= p.Estoque)
                    {
                        p.Estoque -= qtd;
                        vendaAtual.Itens.Add(new ItemVenda(p, qtd));
                        subtotal += (p.Preco * qtd);
                        Console.WriteLine(">> Item adicionado!");
                    }
                    else
                    {
                        Console.WriteLine($">> ERRO: Estoque insuficiente! (Disponível: {p.Estoque})");
                    }
                }
                else
                {
                    Console.WriteLine(">> ERRO: Código Inválido!");
                }

                Console.Write("Passar outro item? (S/N): ");
                continuar = Console.ReadLine();
            }

            if (subtotal > 0)
            {
                Cabecalho();
                Console.WriteLine("--- FINALIZAÇÃO DA VENDA ---");
                Console.WriteLine($"Subtotal: R$ {subtotal:F2}");
                double desconto = LerDouble("Desconto R$ (0 para nenhum): ");

                double totalPagar = Math.Max(0, subtotal - desconto);
                vendaAtual.Desconto = desconto;
                vendaAtual.TotalFinal = totalPagar;

                Console.WriteLine($"TOTAL A PAGAR: R$ {totalPagar:F2}");
                Console.WriteLine("Forma de Pagamento: [1] Dinheiro  [2] Cartão  [3] PIX");
                string forma = Console.ReadLine().Trim();

                if (forma == "1")
                {
                    vendaAtual.FormaPagamento = "DINHEIRO";
                    double valorRecebido = LerDouble("Valor recebido R$: ");
                    while (valorRecebido < totalPagar)
                    {
                        Console.WriteLine("Valor insuficiente!");
                        valorRecebido = LerDouble("Valor recebido R$: ");
                    }
                    double troco = valorRecebido - totalPagar;
                    totalDinheiro += totalPagar;
                    Console.WriteLine($"TROCO A DEVOLVER: R$ {troco:F2}");
                }
                else if (forma == "2")
                {
                    vendaAtual.FormaPagamento = "CARTÃO";
                    totalCartao += totalPagar;
                    Console.WriteLine("Transação aprovada!");
                }
                else
                {
                    vendaAtual.FormaPagamento = "PIX";
                    totalPix += totalPagar;
                    Console.WriteLine("PIX recebido!");
                }

                historicoVendas.Add(vendaAtual);
                if (clienteAtual != null) clienteAtual.TotalGasto += totalPagar;

                Console.WriteLine("\n===========================================");
                Console.WriteLine("     CUPOM FISCAL GERADO COM SUCESSO!      ");
                Console.WriteLine("===========================================");
            }
            else
            {
                Console.WriteLine("Venda cancelada.");
                contadorVendas--;
            }
            Pausar();
        }

        // =========================================================================
        // RELATÓRIOS
        // =========================================================================
        static void RelatoriosAdmin()
        {
            Cabecalho();
            Console.WriteLine("--- PAINEL DE CONTROLE FINANCEIRO E CRM ---");

            Console.WriteLine("\n>> ALERTAS DE ESTOQUE:");
            foreach (var p in catalogo)
            {
                if (p.Estoque <= p.EstoqueMinimo)
                {
                    Console.WriteLine($"   [CRÍTICO] {p.Nome} - Restam apenas {p.Estoque} un.");
                }
            }

            double faturamentoTotal = historicoVendas.Sum(v => v.TotalFinal);
            double custoTotalVendido = historicoVendas.SelectMany(v => v.Itens).Sum(i => i.SubtotalCusto);

            double lucroLiquido = faturamentoTotal - custoTotalVendido - despesasCompras;

            Console.WriteLine("\n>> BALANÇO FINANCEIRO DA LOJA:");
            Console.WriteLine($"   Faturamento Bruto (Vendas) : R$ {faturamentoTotal:F2}");
            Console.WriteLine($"   Custo Mercadorias Vendidas : R$ {custoTotalVendido:F2}");
            Console.WriteLine($"   Despesas c/ Novas Compras  : R$ {despesasCompras:F2}");
            Console.WriteLine("   ---------------------------------------");
            Console.WriteLine($"   LUCRO LÍQUIDO REAL         : R$ {lucroLiquido:F2}");

            Console.WriteLine("\n>> BASE DE CLIENTES (CRM):");
            if (clientes.Count == 0)
            {
                Console.WriteLine("   Nenhum cliente cadastrado.");
            }
            else
            {
                foreach (var c in clientes)
                {
                    Console.WriteLine($"   {c.Nome} | CPF: {c.Cpf} | Total Gasto: R$ {c.TotalGasto:F2}");
                }
            }
            Pausar();
        }

        // =========================================================================
        // MÉTODOS AUXILIARES
        // =========================================================================
        static void Login()
        {
            Cabecalho();
            Console.WriteLine("--- CONTROLE DE ACESSO ---");
            Console.WriteLine(" 1 - Entrar como CAIXA (Operador)");
            Console.WriteLine(" 2 - Entrar como ADMIN (Gerente)");
            int perfil = LerInteiro("Selecione o perfil: ");

            if (perfil == 2)
            {
                Console.Write("Digite a senha (Dica: 123): ");
                string senha = Console.ReadLine();
                if (senha == "123")
                {
                    usuarioAtual = "ADMIN";
                }
                else
                {
                    Console.WriteLine("Senha incorreta! Acesso restrito a Caixa.");
                    usuarioAtual = "CAIXA";
                    Pausar();
                }
            }
            else
            {
                usuarioAtual = "CAIXA";
            }
        }

        static void ListarProdutos()
        {
            Console.WriteLine("--- CATÁLOGO DE PRODUTOS ---");
            foreach (var p in catalogo)
            {
                if (usuarioAtual == "ADMIN")
                {
                    Console.WriteLine($"Cód: {p.Codigo} | {p.Nome} | Estq: {p.Estoque} | Custo: R$ {p.Custo:F2} | Venda: R$ {p.Preco:F2}");
                }
                else
                {
                    Console.WriteLine($"Cód: {p.Codigo} | {p.Nome} | Estq: {p.Estoque} | Venda: R$ {p.Preco:F2}");
                }
            }
            Console.WriteLine("------------------------------------------------------");
        }

        static void Cabecalho()
        {
            try { Console.Clear(); } catch { Console.WriteLine("\n\n\n\n\n"); }
            Console.WriteLine("======================================================");
            Console.WriteLine("           TechStore - ERP & Frente de Caixa          ");
            Console.WriteLine("======================================================");
            Console.WriteLine($" Operador: {usuarioAtual}");
            Console.WriteLine($" Status do Caixa: {(caixaAberto ? "ABERTO" : "FECHADO")}");
            Console.WriteLine("======================================================");
        }

        static void Pausar()
        {
            Console.Write("\nPressione ENTER para continuar...");
            Console.ReadLine();
        }

        static int LerInteiro(string mensagem)
        {
            while (true)
            {
                Console.Write(mensagem);
                if (int.TryParse(Console.ReadLine(), out int resultado))
                    return resultado;
                Console.WriteLine("ERRO: Digite um número inteiro válido.");
            }
        }

        static double LerDouble(string mensagem)
        {
            while (true)
            {
                Console.Write(mensagem);
                string input = Console.ReadLine().Replace(",", ".");
                if (double.TryParse(input, NumberStyles.Any, CultureInfo.InvariantCulture, out double resultado))
                    return resultado;
                Console.WriteLine("ERRO: Digite um valor numérico válido.");
            }
        }

        static Produto BuscarProduto(int codigo) => catalogo.FirstOrDefault(p => p.Codigo == codigo);

        static Cliente BuscarCliente(string cpf) => clientes.FirstOrDefault(c => c.Cpf == cpf);

        static void IniciarDados()
        {
            catalogo.Add(new Produto(101, "PLACA MÃE ASUS B550M", 500.00, 750.00, 10, 5));
            catalogo.Add(new Produto(102, "PROCESSADOR INTEL CORE I5 12400F", 800.00, 1150.00, 10, 5));
            catalogo.Add(new Produto(103, "PROCESSADOR AMD RYZEN 5 5600G", 750.00, 1050.00, 10, 5));
            catalogo.Add(new Produto(104, "MEMÓRIA RAM 16GB DDR4 KINGSTON", 180.00, 280.00, 10, 5));
            catalogo.Add(new Produto(105, "SSD 1TB NVME M.2 KINGSTON", 300.00, 450.00, 10, 5));
            catalogo.Add(new Produto(106, "PLACA DE VÍDEO RTX 3060 12GB", 1600.00, 2200.00, 10, 5));
            catalogo.Add(new Produto(107, "FONTE CORSAIR 650W 80 PLUS BRONZE", 280.00, 420.00, 10, 5));
            catalogo.Add(new Produto(108, "GABINETE GAMER ATX VIDRO TEMPERADO", 150.00, 290.00, 10, 5));
            catalogo.Add(new Produto(109, "MONITOR IPS 24 POL FULL HD LG", 550.00, 780.00, 10, 5));
            catalogo.Add(new Produto(110, "KIT TECLADO E MOUSE MECÂNICO RGB", 120.00, 220.00, 10, 5));
        }
    }
}
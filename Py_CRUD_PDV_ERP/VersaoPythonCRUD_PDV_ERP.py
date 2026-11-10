import os

# =========================================================================
# CLASSES DE DOMÍNIO (Orientação a Objetos em Python)
# =========================================================================
class Produto:
    def __init__(self, codigo, nome, custo, preco, estoque, estoque_minimo):
        self.codigo = codigo
        self.nome = nome
        self.custo = custo
        self.preco = preco
        self.estoque = estoque
        self.estoque_minimo = estoque_minimo

class Cliente:
    def __init__(self, cpf, nome):
        self.cpf = cpf
        self.nome = nome
        self.total_gasto = 0.0

class ItemVenda:
    def __init__(self, produto, quantidade):
        self.produto = produto
        self.quantidade = quantidade
        self.custo_unitario = produto.custo
        self.preco_unitario = produto.preco

    @property
    def subtotal_venda(self):
        return self.preco_unitario * self.quantidade

    @property
    def subtotal_custo(self):
        return self.custo_unitario * self.quantidade

class Venda:
    def __init__(self, id_venda, cliente=None):
        self.id = id_venda
        self.cliente = cliente
        self.itens = []
        self.desconto = 0.0
        self.total_final = 0.0
        self.forma_pagamento = ""

# =========================================================================
# ESTADO GLOBAL DO SISTEMA (Banco de Dados em Memória)
# =========================================================================
catalogo = []
clientes = []
historico_vendas = []

usuario_atual = ""
caixa_aberto = False
fundo_caixa = total_dinheiro = total_cartao = total_pix = despesas_compras = 0.0
contador_vendas = 0

# =========================================================================
# MÉTODOS AUXILIARES E UI
# =========================================================================
def cabecalho():
    os.system('clear')  # Limpa o terminal no Ubuntu/Linux
    print("======================================================")
    print("           TechStore - ERP & Frente de Caixa          ")
    print("======================================================")
    print(f" Operador: {usuario_atual}")
    status = "ABERTO" if caixa_aberto else "FECHADO"
    print(f" Status do Caixa: {status}")
    print("======================================================")

def pausar():
    input("\nPressione ENTER para continuar...")

def ler_inteiro(mensagem):
    while True:
        try:
            return int(input(mensagem))
        except ValueError:
            print("ERRO: Digite um número inteiro válido.")

def ler_float(mensagem):
    while True:
        try:
            return float(input(mensagem).replace(',', '.'))
        except ValueError:
            print("ERRO: Digite um valor numérico válido.")

def buscar_produto(codigo):
    for p in catalogo:
        if p.codigo == codigo:
            return p
    return None

def buscar_cliente(cpf):
    for c in clientes:
        if c.cpf == cpf:
            return c
    return None

def iniciar_dados():
    catalogo.append(Produto(101, "PLACA MÃE ASUS B550M", 500.00, 750.00, 10, 5))
    catalogo.append(Produto(102, "PROCESSADOR INTEL CORE I5 12400F", 800.00, 1150.00, 10, 5))
    catalogo.append(Produto(103, "PROCESSADOR AMD RYZEN 5 5600G", 750.00, 1050.00, 10, 5))
    catalogo.append(Produto(104, "MEMÓRIA RAM 16GB DDR4 KINGSTON", 180.00, 280.00, 10, 5))
    catalogo.append(Produto(105, "SSD 1TB NVME M.2 KINGSTON", 300.00, 450.00, 10, 5))
    catalogo.append(Produto(106, "PLACA DE VÍDEO RTX 3060 12GB", 1600.00, 2200.00, 10, 5))
    catalogo.append(Produto(107, "FONTE CORSAIR 650W 80 PLUS BRONZE", 280.00, 420.00, 10, 5))
    catalogo.append(Produto(108, "GABINETE GAMER ATX VIDRO TEMPERADO", 150.00, 290.00, 10, 5))
    catalogo.append(Produto(109, "MONITOR IPS 24 POL FULL HD LG", 550.00, 780.00, 10, 5))
    catalogo.append(Produto(110, "KIT TECLADO E MOUSE MECÂNICO RGB", 120.00, 220.00, 10, 5))

def listar_produtos():
    print("--- CATÁLOGO DE PRODUTOS ---")
    for p in catalogo:
        if usuario_atual == "ADMIN":
            print(f"Cód: {p.codigo} | {p.nome} | Estq: {p.estoque} | Custo: R$ {p.custo:.2f} | Venda: R$ {p.preco:.2f}")
        else:
            print(f"Cód: {p.codigo} | {p.nome} | Estq: {p.estoque} | Venda: R$ {p.preco:.2f}")
    print("------------------------------------------------------")

def login():
    global usuario_atual
    cabecalho()
    print("--- CONTROLE DE ACESSO ---")
    print(" 1 - Entrar como CAIXA (Operador)")
    print(" 2 - Entrar como ADMIN (Gerente)")
    perfil = ler_inteiro("Selecione o perfil: ")

    if perfil == 2:
        senha = input("Digite a senha (Dica: 123): ")
        if senha == "123":
            usuario_atual = "ADMIN"
        else:
            print("Senha incorreta! Acesso restrito a Caixa.")
            usuario_atual = "CAIXA"
            pausar()
    else:
        usuario_atual = "CAIXA"

# =========================================================================
# MÓDULOS DE GERENCIAMENTO (ADMIN)
# =========================================================================
def cadastrar_produto():
    global despesas_compras
    cabecalho()
    print("--- CADASTRAR NOVO PRODUTO ---")
    listar_produtos()
    print("")

    cod = ler_inteiro("Código numérico (Novo): ")
    nome = input("Nome/Descrição do Produto: ").upper()
    custo = ler_float("Custo Unitário de Fábrica R$: ")
    preco = ler_float("Preço Unitário de Venda R$: ")
    estoque = ler_inteiro("Quantidade Inicial em Estoque: ")
    estq_min = ler_inteiro("Alerta de Estoque Mínimo: ")

    catalogo.append(Produto(cod, nome, custo, preco, estoque, estq_min))
    despesas_compras += (custo * estoque)

    print("\nProduto cadastrado com sucesso!")
    pausar()

def entrada_mercadoria():
    global despesas_compras
    cabecalho()
    print("--- REPOSIÇÃO DE ESTOQUE ---")
    listar_produtos()
    
    cod = ler_inteiro("Digite o CÓDIGO do produto fornecido: ")
    p = buscar_produto(cod)

    if p:
        print(f"Produto: {p.nome}")
        qtd = ler_inteiro("Quantidade recebida: ")
        p.estoque += qtd
        despesas_compras += (p.custo * qtd)
        print(f"Estoque atualizado para: {p.estoque} unidades.")
    else:
        print("ERRO: Produto não encontrado!")
    pausar()

def abrir_fechar_caixa():
    global caixa_aberto, fundo_caixa, total_dinheiro, total_cartao, total_pix
    cabecalho()
    if not caixa_aberto:
        print("--- ABERTURA DE CAIXA ---")
        fundo_caixa = ler_float("Informe o fundo de caixa (Troco Inicial) R$: ")
        caixa_aberto = True
        print("Caixa aberto com sucesso!")
    else:
        print("--- FECHAMENTO DE CAIXA (Z-READING) ---")
        print(f" Fundo Inicial (Troco) : R$ {fundo_caixa:.2f}")
        print(f" Entradas em Dinheiro  : R$ {total_dinheiro:.2f}")
        print("-----------------------------------------")
        print(f" TOTAL ESPERADO GAVETA : R$ {(fundo_caixa + total_dinheiro):.2f}\n")
        print(f" Total Cartão          : R$ {total_cartao:.2f}")
        print(f" Total PIX             : R$ {total_pix:.2f}")
        print("-----------------------------------------")
        
        caixa_aberto = False
        total_dinheiro = total_cartao = total_pix = 0.0
        print("Caixa fechado com sucesso!")
    pausar()

# =========================================================================
# FRENTE DE CAIXA (PDV)
# =========================================================================
def pdv_frente_de_caixa():
    global contador_vendas, total_dinheiro, total_cartao, total_pix
    if not caixa_aberto:
        cabecalho()
        print("ERRO: O Caixa está fechado! Solicite a abertura ao ADMIN.")
        pausar()
        return

    cabecalho()
    print("--- NOVA VENDA ---")
    
    cpf = input("Informe o CPF do cliente (ENTER para anônimo): ").strip()
    cliente_atual = None

    if cpf:
        cliente_atual = buscar_cliente(cpf)
        if not cliente_atual:
            nome = input("Cliente novo. Informe o Nome: ").upper()
            cliente_atual = Cliente(cpf, nome)
            clientes.append(cliente_atual)
            print("Cliente cadastrado com sucesso!")
        else:
            print(f"Bem-vindo de volta, {cliente_atual.nome}!")

    contador_vendas += 1
    venda_atual = Venda(contador_vendas, cliente_atual)
    subtotal = 0.0
    continuar = "S"

    while continuar.upper() == "S":
        cabecalho()
        listar_produtos()
        print(f"\n=== CARRINHO ATUAL: R$ {subtotal:.2f} ===")
        
        cod = ler_inteiro("Código do Produto: ")
        p = buscar_produto(cod)

        if p:
            print(f"Produto: {p.nome} | R$ {p.preco:.2f}")
            qtd = ler_inteiro("Quantidade: ")

            if qtd <= p.estoque:
                p.estoque -= qtd
                venda_atual.itens.append(ItemVenda(p, qtd))
                subtotal += (p.preco * qtd)
                print(">> Item adicionado!")
            else:
                print(f">> ERRO: Estoque insuficiente! (Disponível: {p.estoque})")
        else:
            print(">> ERRO: Código Inválido!")
        
        continuar = input("Passar outro item? (S/N): ")

    if subtotal > 0:
        cabecalho()
        print("--- FINALIZAÇÃO DA VENDA ---")
        print(f"Subtotal: R$ {subtotal:.2f}")
        desconto = ler_float("Desconto R$ (0 para nenhum): ")
        
        total_pagar = max(0.0, subtotal - desconto)
        venda_atual.desconto = desconto
        venda_atual.total_final = total_pagar
        
        print(f"TOTAL A PAGAR: R$ {total_pagar:.2f}")
        print("Forma de Pagamento: [1] Dinheiro  [2] Cartão  [3] PIX")
        forma = input("Opção: ").strip()

        if forma == "1":
            venda_atual.forma_pagamento = "DINHEIRO"
            valor_recebido = ler_float("Valor recebido R$: ")
            while valor_recebido < total_pagar:
                print("Valor insuficiente!")
                valor_recebido = ler_float("Valor recebido R$: ")
            
            troco = valor_recebido - total_pagar
            total_dinheiro += total_pagar
            print(f"TROCO A DEVOLVER: R$ {troco:.2f}")
        elif forma == "2":
            venda_atual.forma_pagamento = "CARTÃO"
            total_cartao += total_pagar
            print("Transação aprovada!")
        else:
            venda_atual.forma_pagamento = "PIX"
            total_pix += total_pagar
            print("PIX recebido!")

        historico_vendas.append(venda_atual)
        if cliente_atual:
            cliente_atual.total_gasto += total_pagar

        print("\n===========================================")
        print("     CUPOM FISCAL GERADO COM SUCESSO!      ")
        print("===========================================")
    else:
        print("Venda cancelada.")
        contador_vendas -= 1
    
    pausar()

# =========================================================================
# RELATÓRIOS
# =========================================================================
def relatorios_admin():
    cabecalho()
    print("--- PAINEL DE CONTROLE FINANCEIRO E CRM ---")
    
    print("\n>> ALERTAS DE ESTOQUE:")
    for p in catalogo:
        if p.estoque <= p.estoque_minimo:
            print(f"   [CRÍTICO] {p.nome} - Restam apenas {p.estoque} un.")

    faturamento_total = sum(v.total_final for v in historico_vendas)
    custo_total_vendido = sum(item.subtotal_custo for v in historico_vendas for item in v.itens)

    lucro_liquido = faturamento_total - custo_total_vendido - despesas_compras

    print("\n>> BALANÇO FINANCEIRO DA LOJA:")
    print(f"   Faturamento Bruto (Vendas) : R$ {faturamento_total:.2f}")
    print(f"   Custo Mercadorias Vendidas : R$ {custo_total_vendido:.2f}")
    print(f"   Despesas c/ Novas Compras  : R$ {despesas_compras:.2f}")
    print("   ---------------------------------------")
    print(f"   LUCRO LÍQUIDO REAL         : R$ {lucro_liquido:.2f}")

    print("\n>> BASE DE CLIENTES (CRM):")
    if not clientes:
        print("   Nenhum cliente cadastrado.")
    else:
        for c in clientes:
            print(f"   {c.nome} | CPF: {c.cpf} | Total Gasto: R$ {c.total_gasto:.2f}")
    pausar()

# =========================================================================
# LOOP PRINCIPAL DO SISTEMA
# =========================================================================
def main():
    iniciar_dados()
    login()

    opcao = -1
    while opcao != 0:
        cabecalho()
        print("---------------- MENU PRINCIPAL ----------------")
        print(" 1 - PDV: Nova Venda (Frente de Caixa)")
        
        if usuario_atual == "ADMIN":
            print(" 2 - GERÊNCIA: Cadastrar Novo Produto")
            print(" 3 - GERÊNCIA: Repor Estoque (Compras)")
            print(" 4 - GERÊNCIA: Abrir ou Fechar Caixa")
            print(" 5 - GERÊNCIA: Relatórios Financeiros e CRM")
        
        print(" 6 - Sistema: Trocar Usuário (Logoff)")
        print(" 0 - Sair do Sistema")
        print("------------------------------------------------")
        opcao = ler_inteiro("Escolha uma opção: ")

        if opcao == 1:
            pdv_frente_de_caixa()
        elif opcao == 2 and usuario_atual == "ADMIN":
            cadastrar_produto()
        elif opcao == 3 and usuario_atual == "ADMIN":
            entrada_mercadoria()
        elif opcao == 4 and usuario_atual == "ADMIN":
            abrir_fechar_caixa()
        elif opcao == 5 and usuario_atual == "ADMIN":
            relatorios_admin()
        elif opcao == 6:
            login()
        elif opcao == 0:
            print("Encerrando TechStore ERP...")
        else:
            print("Opção inválida ou acesso negado!")
            pausar()

if __name__ == "__main__":
    main()
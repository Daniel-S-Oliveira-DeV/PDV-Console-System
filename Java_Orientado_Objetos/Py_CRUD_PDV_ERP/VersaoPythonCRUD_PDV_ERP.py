import os
from datetime import date

# --- CLASSES DE DOMÍNIO ---
class Produto:
    def __init__(self, codigo, nome, custo, preco, estoque):
        self.codigo = codigo
        self.nome = nome
        self.custo = custo
        self.preco = preco
        self.estoque = estoque

class ItemVenda:
    def __init__(self, produto, quantidade):
        self.produto = produto
        self.quantidade = quantidade

    @property
    def subtotal_custo(self):
        return self.produto.custo * self.quantidade

    @property
    def subtotal_venda(self):
        return self.produto.preco * self.quantidade

class Venda:
    def __init__(self, id_venda, data_venda):
        self.id = id_venda
        self.data = data_venda
        self.itens = []

    @property
    def total_venda(self):
        return sum(item.subtotal_venda for item in self.itens)

# --- ESTADO GLOBAL DO SISTEMA ---
estoque = []
historico_vendas = []
contador_vendas = 0

# --- MÉTODOS AUXILIARES ---
def cabecalho():
    os.system('clear') # Limpa a tela no Linux/Ubuntu
    print("======================================================")
    print("           TechStore - Sistema de Gestão ERP          ")
    print("======================================================")

def pausar():
    input("Pressione ENTER para continuar...")

def ler_inteiro_seguro(mensagem):
    while True:
        try:
            return int(input(mensagem))
        except ValueError:
            print("ERRO: Digite apenas números inteiros válidos!")

def ler_float_seguro(mensagem):
    while True:
        try:
            return float(input(mensagem).replace(',', '.'))
        except ValueError:
            print("ERRO: Digite um valor numérico válido.")

def buscar_produto(codigo):
    for p in estoque:
        if p.codigo == codigo:
            return p
    return None

def carregar_dados_iniciais():
    estoque.append(Produto(101, "PLACA MÃE ASUS B550M", 500.00, 750.00, 10))
    estoque.append(Produto(102, "PROCESSADOR INTEL CORE I5 12400F", 800.00, 1150.00, 10))
    estoque.append(Produto(103, "PROCESSADOR AMD RYZEN 5 5600G", 750.00, 1050.00, 10))
    estoque.append(Produto(104, "MEMÓRIA RAM 16GB DDR4 KINGSTON", 180.00, 280.00, 10))
    estoque.append(Produto(105, "SSD 1TB NVME M.2 KINGSTON", 300.00, 450.00, 10))
    estoque.append(Produto(106, "PLACA DE VÍDEO RTX 3060 12GB", 1600.00, 2200.00, 10))
    estoque.append(Produto(107, "FONTE CORSAIR 650W 80 PLUS BRONZE", 280.00, 420.00, 10))
    estoque.append(Produto(108, "GABINETE GAMER ATX VIDRO TEMPERADO", 150.00, 290.00, 10))
    estoque.append(Produto(109, "MONITOR IPS 24 POL FULL HD LG", 550.00, 780.00, 10))
    estoque.append(Produto(110, "KIT TECLADO E MOUSE MECÂNICO RGB", 120.00, 220.00, 10))

def listar_produtos_simples(titulo):
    cabecalho()
    print(f"--- ATENÇÃO: {titulo} ---")
    for p in estoque:
        print(f"[Cód: {p.codigo}] - Estoque: {p.estoque} | {p.nome}")
    print("----------------------------------------")

# --- MÓDULOS PRINCIPAIS ---
def menu_produtos():
    opcao = -1
    while opcao != 0:
        cabecalho()
        print("--- SUBMENU: GERENCIAR PRODUTOS ---")
        print(" 1 - Incluir Produto")
        print(" 2 - Editar Produto")
        print(" 3 - Excluir Produto")
        print(" 4 - Listar Produtos")
        print(" 0 - Voltar")
        
        opcao = ler_inteiro_seguro("Opção: ")

        if opcao == 1:
            listar_produtos_simples("PRODUTOS JÁ CADASTRADOS")
            print("\n-- INSERIR NOVO PRODUTO --")
            cod = ler_inteiro_seguro("Código numérico: ")
            nome = input("Nome/Descrição (Maiúsculas): ").upper()
            custo = ler_float_seguro("Preço de Custo Unitário (R$): ")
            preco = ler_float_seguro("Preço de Venda Unitário (R$): ")
            qtd = ler_inteiro_seguro("Quantidade Inicial em Estoque: ")
            estoque.append(Produto(cod, nome, custo, preco, qtd))
            print("Produto cadastrado com sucesso!")
            pausar()
            
        elif opcao == 2:
            listar_produtos_simples("PRODUTOS JÁ CADASTRADOS")
            cod_editar = ler_inteiro_seguro("\nDigite o CÓDIGO do produto para editar: ")
            p_editar = buscar_produto(cod_editar)
            
            if p_editar:
                print(f"Editando: {p_editar.nome}")
                p_editar.codigo = ler_inteiro_seguro("Novo Código: ")
                p_editar.nome = input("Novo Nome: ").upper()
                p_editar.custo = ler_float_seguro("Novo Preço de Custo (R$): ")
                p_editar.preco = ler_float_seguro("Novo Preço de Venda (R$): ")
                p_editar.estoque = ler_inteiro_seguro("Nova Quantidade em Estoque: ")
                print("Atualizado com sucesso!")
            else:
                print("Código não encontrado!")
            pausar()
            
        elif opcao == 3:
            cod_excluir = ler_inteiro_seguro("Digite o CÓDIGO do produto para excluir: ")
            p_excluir = buscar_produto(cod_excluir)
            
            if p_excluir:
                estoque.remove(p_excluir)
                print("Produto excluído com sucesso!")
            else:
                print("Código não encontrado!")
            pausar()
            
        elif opcao == 4:
            cabecalho()
            print("--- ESTOQUE ATUAL ---")
            for p in estoque:
                print(f"Cód: {p.codigo} | {p.nome}\n   Estoque: {p.estoque} un | Custo Un: R$ {p.custo:.2f} | Venda Un: R$ {p.preco:.2f}\n-")
            pausar()

def realizar_venda():
    global contador_vendas
    cabecalho()
    print("--- NOVA VENDA ---")
    
    data_atual = date.today().strftime("%dd/%mm/%Y")
    print(f"Data da Venda: {data_atual}")
    
    contador_vendas += 1
    venda_atual = Venda(contador_vendas, data_atual)
    resp = "S"

    while resp.upper() == "S":
        print("\n--- PRODUTOS DISPONÍVEIS ---")
        for p in estoque:
            print(f"[Cód: {p.codigo}] - R$ {p.preco:.2f} | Estq: {p.estoque} | {p.nome}")
            
        cod_busca = ler_inteiro_seguro("Digite o CÓDIGO do produto: ")
        produto_encontrado = buscar_produto(cod_busca)

        if produto_encontrado:
            print(f">> Selecionado: {produto_encontrado.nome}")
            print(f">> Valor Unitário: R$ {produto_encontrado.preco:.2f}")
            qtd = ler_inteiro_seguro("Quantidade: ")

            if qtd <= produto_encontrado.estoque:
                produto_encontrado.estoque -= qtd
                venda_atual.itens.append(ItemVenda(produto_encontrado, qtd))
                print(f"--> Item adicionado ao carrinho! Estoque restante: {produto_encontrado.estoque}")
            else:
                print(f"ERRO: Estoque insuficiente! Você tem apenas {produto_encontrado.estoque} unidades disponíveis.")
        else:
            print("ERRO: Produto não cadastrado!")

        resp = input("\nDeseja passar mais um item nesta venda? (S/N): ")

    if venda_atual.itens:
        historico_vendas.append(venda_atual)
        cabecalho()
        print("======================================================")
        print("                  CUPOM FISCAL                        ")
        print(f" Data da Compra: {venda_atual.data}")
        print(f" Venda ID: {venda_atual.id}")
        print("======================================================")
        
        for item in venda_atual.itens:
            print(item.produto.nome)
            print(f"  {item.quantidade} un  x  R$ {item.produto.preco:.2f} = Subtotal: R$ {item.subtotal_venda:.2f}\n-")
            
        print("======================================================")
        print(f" TOTAL A PAGAR: R$ {venda_atual.total_venda:.2f}")
        print("======================================================")
    else:
        print("Venda cancelada (nenhum item no carrinho).")
        contador_vendas -= 1
        
    pausar()

def menu_relatorios():
    opcao = -1
    while opcao != 0:
        cabecalho()
        print("--- SUBMENU: RELATÓRIOS E FATURAMENTO ---")
        print(" 1 - Histórico Detalhado por Venda")
        print(" 2 - Faturamento e Lucro por Produto")
        print(" 0 - Voltar")
        opcao = ler_inteiro_seguro("Opção: ")

        if opcao == 1:
            cabecalho()
            print("--- HISTÓRICO DE VENDAS REALIZADAS ---")
            if not historico_vendas:
                print("Nenhuma venda realizada ainda.")
            
            for v in historico_vendas:
                print("======================================")
                print(f" VENDA ID: {v.id} | DATA: {v.data}")
                print("======================================")
                for item in v.itens:
                    print(f" -> {item.quantidade}x {item.produto.nome}")
                    print(f"    Custo Un: R$ {item.produto.custo:.2f} | Venda Un: R$ {item.produto.preco:.2f}")
                    print(f"    Subtotal: R$ {item.subtotal_venda:.2f}")
                    print(" - - - - - - - - - - - - - - - - - - -")
                print(f" TOTAL DA VENDA: R$ {v.total_venda:.2f}\n")
            pausar()
            
        elif opcao == 2:
            cabecalho()
            print("--- FATURAMENTO E LUCRO POR PRODUTO ---")
            if not estoque:
                print("Estoque vazio.")
            else:
                fat_geral = 0
                lucro_geral = 0

                for p in estoque:
                    itens_deste_produto = [item for venda in historico_vendas for item in venda.itens if item.produto.codigo == p.codigo]

                    if itens_deste_produto:
                        qtd_vendida_total = sum(i.quantidade for i in itens_deste_produto)
                        custo_acumulado = sum(i.subtotal_custo for i in itens_deste_produto)
                        fat_bruto = sum(i.subtotal_venda for i in itens_deste_produto)
                        
                        lucro_liquido = fat_bruto - custo_acumulado
                        fat_geral += fat_bruto
                        lucro_geral += lucro_liquido

                        print(f"Cód: {p.codigo} | {p.nome}")
                        print(f" -> Unidades Vendidas: {qtd_vendida_total}")
                        print(f" -> Custo Total (Base): R$ {custo_acumulado:.2f}")
                        print(f" -> Faturamento Bruto: R$ {fat_bruto:.2f}")
                        print(f" -> LUCRO LÍQUIDO: R$ {lucro_liquido:.2f}")
                        print("......................................")
                
                if fat_geral > 0:
                    print("\n======================================")
                    print("        BALANÇO GERAL DA LOJA         ")
                    print("======================================")
                    print(f" FATURAMENTO TOTAL : R$ {fat_geral:.2f}")
                    print(f" LUCRO LÍQUIDO TOTAL: R$ {lucro_geral:.2f}")
                    print("======================================")
                else:
                    print("Nenhum registro de faturamento encontrado.")
            pausar()

def main():
    carregar_dados_iniciais()
    opcao = -1
    while opcao != 4:
        cabecalho()
        print("---------------- MENU PRINCIPAL ----------------")
        print(" 1 - Gerenciar Produtos (Estoque)")
        print(" 2 - Realizar Venda (Frente de Caixa)")
        print(" 3 - Relatórios e Faturamento")
        print(" 4 - Sair do Sistema")
        print("------------------------------------------------")
        
        opcao = ler_inteiro_seguro("Escolha uma opção: ")

        if opcao == 1:
            menu_produtos()
        elif opcao == 2:
            realizar_venda()
        elif opcao == 3:
            menu_relatorios()
        elif opcao == 4:
            print("Encerrando o sistema TechStore...")
        else:
            print("Opção inválida!")
            pausar()

# Ponto de entrada do script Python
if __name__ == "__main__":
    main()
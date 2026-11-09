# 🛒 TechStore - Micro-ERP & Frente de Caixa (Multi-Language)

Este repositório documenta a evolução de um sistema de vendas e gestão de stock para uma loja de hardware (TechStore). Mais do que um simples registo de vendas, este projeto funciona como a **"Pedra de Roseta"** do meu portfólio de Engenharia de Software: demonstra a implementação das mesmas regras de negócio complexas através de diferentes linguagens e paradigmas de programação.

## 🎯 O Problema de Negócio (Domínio)

O sistema resolve desafios reais da gestão de uma loja física, integrando três pilares fundamentais numa única aplicação de consola:
- **Gestão de Materiais (Estoque):** CRUD de produtos com controlo dinâmico de quantidades.
- **Frente de Caixa (PDV):** Validação em tempo real (bloqueia vendas sem saldo em stock), baixa automática de unidades, captura autónoma de datas pelo sistema operativo e geração de Cupão Fiscal detalhado.
- **Inteligência Financeira (Relatórios):** Cruzamento do histórico de transações com o Custo da Mercadoria Vendida (CMV/COGS). O sistema processa autonomamente a Faturação Bruta, o Lucro Líquido por produto e entrega o **Balanço Geral** (Bottom Line) agrupado da operação.

## 🏗️ Arquitetura e Evolução Tecnológica

O projeto está modularizado para demonstrar adaptabilidade e maturidade técnica técnica:

### 1. 🟦 `VisuAlg_LogicaBase` (O Alicerce Estruturado)
* **Conceitos:** Lógica de programação pura, vetores paralelos em memória, laços de repetição nativos e modularização via procedimentos.
* **Objetivo:** Demonstrar o controlo de estado em memória e o raciocínio algorítmico sem o auxílio de bibliotecas prontas.

### 2. ☕ `Java_Orientado_Objetos` (O Padrão Corporativo Clássico)
* **Conceitos:** Paradigma de Orientação a Objetos (OOP), Encapsulamento, tipagem estática e listas dinâmicas (`ArrayList`).
* **Destaques:** 
  * Tratamento rigoroso de exceções (`try-catch`) para evitar falhas do sistema perante *inputs* incorretos.
  * Uso da API `java.time` para controlo temporal automático.
  * Lógica de agrupamento de objetos `ItemVenda` dentro da entidade `Venda`.

### 3. 🟣 `CSharp_DotNet` (O Ecossistema Microsoft)
* **Conceitos:** OOP moderna, Propriedades Automáticas (`{ get; set; }`) e coleções fortemente tipadas (`List<T>`).
* **Destaques:** Utilização de **LINQ (Language Integrated Query)** e Expressões Lambda (`=>`) para extração rápida de relatórios financeiros e cruzamento de dados, simulando queries de bancos de dados.

### 4. 🐍 `Python_Scripts` (Sintaxe Dinâmica e Minimalista)
* **Conceitos:** Tipagem dinâmica, processamento rápido de dados e Orientação a Objetos nativa do Python.
* **Destaques:** Utilização do decorador `@property` para cálculo automático de subtotais e *List Comprehensions* para otimizar a varredura do histórico de vendas com poucas linhas de código.

## 💻 Como Executar

Clone o repositório para a sua máquina local:
```bash
git clone [https://github.com/Daniel-S-Oliveira-DeV/PDV-Console-System.git](https://github.com/Daniel-S-Oliveira-DeV/PDV-Console-System.git)
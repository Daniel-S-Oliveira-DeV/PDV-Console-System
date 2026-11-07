# 🛒 TechStore - Sistema ERP de Gestão e Frente de Caixa

Este repositório contém a evolução de um sistema de vendas e gestão de stock para uma loja de informática (TechStore). O projeto demonstra a transição de uma lógica estruturada básica para uma arquitetura robusta em **Java Orientado a Objetos (OOP)**, atuando como um micro-ERP completo rodando diretamente no terminal.

## 🚀 O que este sistema faz?

Muito além de um simples registo de vendas, este sistema resolve problemas reais de gestão de loja, integrando três pilares fundamentais:
- **Gestão de Materiais (Estoque):** CRUD completo com controlo dinâmico de quantidades.
- **Frente de Caixa (PDV):** Validação em tempo real de disponibilidade de stock, captura automática de datas e geração de Cupão Fiscal detalhado.
- **Balanço Financeiro:** Cruzamento do histórico de vendas com os custos de aquisição (COGS) para gerar relatórios de faturamento bruto e lucro líquido real.

## 🛠️ Tecnologias e Arquitetura

O repositório está dividido para demonstrar a evolução da engenharia de software:

* **Versão V1 (VisuAlg - Lógica Estruturada):** Base do sistema construída com vetores paralelos, laços de repetição complexos e modularização (Procedimentos). Foco no desenvolvimento do raciocínio lógico e gestão de estado em memória.
* **Versão V2 (Java - Orientação a Objetos):** Refatoração completa utilizando classes (`Produto`, `ItemVenda`, `Venda`), encapsulamento e listas dinâmicas (`ArrayList`). 
  * Tratamento rigoroso de exceções (`try-catch`) para garantir a estabilidade do sistema contra erros de digitação do utilizador (letras em vez de números).
  * Utilização da biblioteca `java.time.LocalDate` para automatização temporal.

## 📦 Principais Funcionalidades

1. **Menu de Estoque Dinâmico:** Inclusão, Edição, Exclusão e Listagem de produtos (Mock data inicial de 10 hardwares reais).
2. **Sistema de Frente de Caixa Inteligente:**
   * Proíbe a venda de quantidades superiores ao stock disponível.
   * Dá baixa automática (`estoque -= qtd`) no momento da compra.
   * Permite múltiplas compras na mesma transação.
3. **Geração de Cupão Fiscal:** Impressão em consola da Data, ID da Venda, Subtotais e Valor Final a pagar.
4. **Relatórios Analíticos:**
   * **Histórico Agrupado:** Exibe as transações separadas por ID de Venda e Data, facilitando a auditoria de fecho de caixa.
   * **Lucratividade:** Calcula automaticamente o Custo Total dos Itens Vendidos versus o Faturamento Bruto, entregando o **Balanço Geral** e o **Lucro Líquido** da operação.

## 💻 Como executar o projeto (Versão Java)

Certifique-se de que tem o [Java JDK](https://www.oracle.com/java/technologies/downloads/) instalado na sua máquina.

1. Clone este repositório:
   ```bash
   git clone [https://github.com/Daniel-S-Oliveira-DeV/PDV-Console-System.git](https://github.com/Daniel-S-Oliveira-DeV/PDV-Console-System.git)

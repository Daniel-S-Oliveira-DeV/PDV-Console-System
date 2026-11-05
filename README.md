# 🛒 Sistema de Frente de Caixa (PDV) - Console CRUD

Este repositório contém o desenvolvimento de um sistema de Frente de Caixa (PDV) completo, demonstrando a evolução da lógica de programação estruturada para a Orientação a Objetos. O projeto foi construído como parte das atividades práticas do curso Técnico em Informática do Senac.

## 📂 Estrutura do Projeto

O repositório está dividido em duas abordagens tecnológicas:

1. **VisuAlg_LogicaBase**: Contém o algoritmo estruturado (`.alg`) utilizando vetores (arrays) para simular uma base de dados na memória. Foco total em laços de repetição, lógica de ordenação (Shift) e manipulação direta de índices.
2. **Java_Orientado_Objetos**: Contém a evolução do sistema (`.java`) utilizando classes (`Produto`, `ItemVenda`), listas dinâmicas (`ArrayList`) e tratamento de exceções (`try-catch`) para garantir a segurança da inserção de dados.

## 🚀 Funcionalidades (Regras de Negócio)

*   **CRUD Completo de Estoque**: Criação, Leitura, Atualização e Exclusão de produtos.
*   **Frente de Caixa (Carrinho)**: Módulo isolado de vendas que busca produtos pelo código, calcula subtotais e gera uma nota fiscal temporária.
*   **Validação à Prova de Falhas (Java)**: Bloqueio de falhas de sistema utilizando um `Scanner` seguro. Se o utilizador inserir letras em campos de preços ou quantidades, o sistema intercepta a exceção (`NumberFormatException`) e solicita os dados novamente sem interromper a execução.
*   **Geração de Cupom Fiscal**: Impressão em consola do resumo da compra com alinhamento formatado de valores monetários.

## 🛠️ Como Executar

### Versão VisuAlg
1. Descarregue o ficheiro `Sistema_Caixa_CRUD.alg` da pasta correspondente.
2. Abra o emulador VisuAlg 3.0 (ou superior).
3. Cole o código e prima `F9` para executar.

### Versão Java
1. Certifique-se de ter o JDK instalado na máquina.
2. Abra o terminal na pasta `Java_Orientado_Objetos`.
3. Compile o código executando: `javac SistemaCaixaCRUD.java`
4. Execute o sistema com: `java SistemaCaixaCRUD`

## 👨‍💻 Autor

Desenvolvido por **Daniel**

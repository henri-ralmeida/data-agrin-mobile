# Guia de Contribuição

## Idioma do Projeto

Este projeto utiliza **Português Brasileiro (PT-BR)** como idioma padrão para:
- Mensagens de commit
- Documentação (README, guias, wikis)
- Comentários no código
- Issues e Pull Requests

**Importante:** Nomes de variáveis, funções, classes e outros elementos de código devem seguir as convenções padrão da linguagem utilizada (geralmente em inglês para melhor compatibilidade e legibilidade internacional).

## Padrão de Mensagens de Commit

### Formato

As mensagens de commit devem seguir o padrão:

```
<tipo>: <descrição curta em PT-BR>

[corpo opcional explicando o que e por que]

[rodapé opcional com referências]
```

### Tipos de Commit

- **feat**: Nova funcionalidade
- **fix**: Correção de bug
- **docs**: Alterações na documentação
- **style**: Formatação, ponto e vírgula faltando, etc (sem mudança de código)
- **refactor**: Refatoração de código (sem correção de bug ou nova funcionalidade)
- **test**: Adição ou correção de testes
- **chore**: Atualização de tarefas de build, configurações, etc

### Exemplos

```
feat: adiciona tela de login do agricultor

Implementa a tela de autenticação com validação de CPF
e integração com backend de autenticação.
```

```
fix: corrige cálculo de área plantada

O cálculo anterior não considerava áreas irregulares.
Agora utiliza algoritmo de triangulação para precisão.
```

```
docs: atualiza README com instruções de instalação

Adiciona seção sobre dependências do Android SDK
e configuração do ambiente de desenvolvimento.
```

## Como Contribuir

1. Faça fork do projeto
2. Crie uma branch para sua feature usando o tipo de commit como prefixo:
   - `feat/nova-funcionalidade` para novas features
   - `fix/correcao-bug` para correções
   - `docs/atualiza-readme` para documentação
3. Commit suas mudanças seguindo o padrão acima
4. Push para a branch (`git push origin feat/nova-funcionalidade`)
5. Abra um Pull Request

## Código de Conduta

- Seja respeitoso com outros contribuidores
- Aceite críticas construtivas
- Foque no que é melhor para a comunidade e o projeto
- Mantenha discussões técnicas e profissionais

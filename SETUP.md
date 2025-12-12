# Configuração do Ambiente

## Configurar Template de Commit

### Configuração Local (Recomendado)

Para usar o template de mensagem de commit apenas neste projeto, execute:

```bash
git config commit.template .gitmessage
```

### Configuração Global (Opcional)

Se desejar usar este template em todos os seus projetos Git:

```bash
git config --global commit.template ~/.gitmessage
cp .gitmessage ~/.gitmessage
```

**Nota:** A configuração global afetará todos os seus repositórios. Use apenas se desejar aplicar o mesmo padrão em todos os projetos.

## Configuração do Git (Recomendado)

Configure seu nome e email se ainda não o fez:

```bash
git config --global user.name "Seu Nome"
git config --global user.email "seu.email@example.com"
```

## Dependências do Projeto

### Android Development

- Android Studio (versão mais recente)
- JDK 11 ou superior
- Android SDK
- Kotlin Plugin

### Ferramentas Recomendadas

- Git
- Gradle (incluído no Android Studio)

## Estrutura do Projeto

```
data-agrin-mobile/
├── .gitmessage          # Template de mensagens de commit
├── CONTRIBUTING.md      # Guia de contribuição
├── LICENSE             # Licença Apache 2.0
├── README.md           # Documentação principal
└── SETUP.md            # Este arquivo
```

## Próximos Passos

1. Configure o template de commit (veja acima)
2. Leia o [CONTRIBUTING.md](CONTRIBUTING.md) para entender os padrões
3. Configure seu ambiente de desenvolvimento Android
4. Comece a desenvolver!

## Suporte

Para questões ou problemas, abra uma issue no GitHub.

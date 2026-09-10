# DataAgrin Mobile

**Tarefas agrícolas, registro de atividades e clima no Android.**

Kotlin · Jetpack Compose · Material 3 · Room · Firebase Firestore

Aplicativo desenvolvido para um desafio técnico da Data Agrin. Reúne o planejamento de tarefas por talhão, o histórico de atividades e a consulta do clima pela localização do dispositivo.

## O que você encontra

| Área | Recursos |
| --- | --- |
| Tarefas | Cadastro, edição, exclusão e alteração de status |
| Atividades | Registro de tipo de atividade, talhão, horários e observações |
| Clima | Temperatura, umidade e previsão por hora via Open-Meteo |
| Experiência | Tema claro/escuro, layouts adaptáveis e estados de localização/conectividade |
| Dados | Persistência local com Room e integração de tarefas com Firestore |

## Começar

1. Clone o projeto:

   ```bash
   git clone https://github.com/henri-ralmeida/data-agrin-mobile.git
   cd data-agrin-mobile
   ```

2. Abra a pasta no Android Studio e configure um **Gradle JDK 17**, conforme a [compatibilidade do AGP 8.6](https://developer.android.com/build/releases/agp-8-6-0-release-notes).
3. Instale o **Android SDK 35** e aguarde a sincronização do Gradle.
4. Configure seu ambiente Firebase conforme a seção abaixo.
5. Selecione um dispositivo ou emulador com **Android 8.0 / API 26 ou superior** e execute o módulo `app`.

O alvo de bytecode Java/Kotlin é 11; isso é diferente do JDK usado para executar o Gradle.

### Firebase

O projeto já contém `app/google-services.json`. Para executar em seu próprio ambiente, substitua-o pela configuração do seu app Android no Firebase, usando o application ID `com.example.dataagrin.app`.

A integração usa a coleção `tasks` e a subcoleção `history` de cada tarefa. O acesso depende da configuração e das regras do Firestore; as regras não estão versionadas neste repositório. Não use dados pessoais reais em uma instância de demonstração sem configurar esse acesso.

### Compilar pelo terminal

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

No PowerShell, substitua `./gradlew` por `.\gradlew.bat`. O comando de instalação requer um dispositivo ou emulador conectado.

APK de debug: `app/build/outputs/apk/debug/app-debug.apk`.

## Como os dados se comportam

**Room é a fonte das listas locais.** Tarefas e registros são consultados a partir do banco no dispositivo.

**A integração com Firestore tem limites.** Ao criar uma tarefa, o app consulta o Firestore para escolher um ID antes de gravá-la localmente. Em falha, o repositório usa o ID 1 como fallback. Por isso, a criação não deve ser tratada como um fluxo offline confiável.

Edições e exclusões geram registros locais e tentativas de envio ao Firestore. A exclusão remota registra um evento no histórico, mas a remoção do documento está comentada no código. Essa integração não representa uma sincronização bidirecional completa.

**O clima depende da rede para obter dados novos.** A implementação guarda resultados no Room e pode usar cache após uma carga bem-sucedida, conforme o estado informado pela tela. Uma primeira consulta sem rede não garante previsão disponível.

Esses comportamentos estão em [TaskViewModel](app/src/main/java/com/example/dataagrin/app/presentation/viewmodel/TaskViewModel.kt), [TaskFirestoreRepository](app/src/main/java/com/example/dataagrin/app/data/firebase/TaskFirestoreRepository.kt) e [WeatherRepositoryImpl](app/src/main/java/com/example/dataagrin/app/data/repository/WeatherRepositoryImpl.kt).

## Clima e localização

A integração chama `https://api.open-meteo.com/v1/forecast`, enviando latitude e longitude. O cliente atual não envia chave de API.

Para experimentar a tela de clima:

1. Habilite a localização no dispositivo e conceda a permissão solicitada.
2. Faça a primeira consulta com internet disponível.
3. Confira os dados atuais e a previsão horária.
4. Desative a rede para verificar como a tela apresenta a disponibilidade do cache.

## Tecnologias

| Parte | Configuração versionada |
| --- | --- |
| Android | minSdk 26, compileSdk/targetSdk 35 |
| Linguagem | Kotlin 2.0.0 |
| Interface | Jetpack Compose e Material 3 |
| Dependências | Koin 4.1.1 |
| Persistência local | Room 2.6.1 |
| HTTP | Retrofit 3.0.0 |
| Concorrência | Coroutines e Flow |
| Testes | JUnit 4, MockK e Robolectric |
| Qualidade | JaCoCo, ktlint e detekt |

As fontes das versões são [libs.versions.toml](gradle/libs.versions.toml) e [app/build.gradle.kts](app/build.gradle.kts).

## Organização

O projeto separa interface, regras e acesso a dados:

```text
app/src/main/java/com/example/dataagrin/app/
├── presentation/   # Telas Compose e ViewModels
├── domain/         # Modelos, contratos e casos de uso
├── data/           # Room, Firestore, clima e localização
├── di/             # Configuração do Koin
└── ui/theme/       # Cores, tipografia e tema
```

## Testes e qualidade

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:jacocoTestReport
./gradlew :app:ktlintCheck :app:detekt
```

O relatório HTML de cobertura é gerado em `app/build/reports/jacoco/index.html`. A [suíte versionada](app/src/test/java/com/example/dataagrin/app) cobre casos de uso, repositórios, ViewModels, validações e outras partes da aplicação.

Quantidades de testes aprovados e percentuais de cobertura dependem da execução; não são indicadores de CI neste README.

## Histórico e licença

- [CHANGELOG](CHANGELOG.md)
- [Apache License 2.0](LICENSE)

Desenvolvido por [Henrique Almeida](https://github.com/henri-ralmeida).


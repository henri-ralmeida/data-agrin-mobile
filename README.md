# DataAgrin Mobile 🌾

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android"/>
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"/>
  <img src="https://img.shields.io/badge/Material%203-1976D2?style=for-the-badge&logo=material-design&logoColor=white" alt="Material 3"/>
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase"/>
  <img src="https://img.shields.io/badge/Room-0078D4?style=for-the-badge&logo=microsoft&logoColor=white" alt="Room"/>
</p>

<p align="center">
  <b>App Android nativo para monitoramento de tarefas agrícolas, registro de atividades e previsão climática.</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Version-1.1.0-blue?style=flat-square" alt="Version"/>
  <img src="https://img.shields.io/badge/Testes-180%20passando-brightgreen?style=flat-square" alt="Tests"/>
  <img src="https://img.shields.io/badge/Cobertura%20UseCases-100%25-brightgreen?style=flat-square" alt="Coverage"/>
  <img src="https://img.shields.io/badge/Build-Passing-brightgreen?style=flat-square" alt="Build"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Min%20SDK-26-blue?style=flat-square" alt="Min SDK"/>
  <img src="https://img.shields.io/badge/Target%20SDK-34-blue?style=flat-square" alt="Target SDK"/>
  <img src="https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white" alt="Gradle"/>
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=flat-square" alt="License"/>
</p>

---

## 📑 Índice

- [Funcionalidades](#-funcionalidades)
- [Screenshots](#-screenshots)
- [UX & Design](#-ux--design)
- [Stack Técnico](#️-stack-técnico)
- [Arquitetura](#-arquitetura)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Como Executar](#-como-executar)
- [Testes](#-testes)
- [Responsividade](#-responsividade)
- [API de Clima](#-api-de-clima)
- [Localização GPS](#-localização-gps)
- [Banco de Dados](#️-banco-de-dados)
- [Sincronização Firebase](#-sincronização-firebase)

---

## ✨ Funcionalidades

### 📋 Tela de Tarefas (100% Offline)
| Recurso               | Descrição                                     |
|-----------------------|-----------------------------------------------|
| ✅ Listar tarefas      | Nome, área/talhão, horário previsto           |
| ✅ Status visual       | Pendente 🔴 / Em andamento 🟠 / Finalizada 🟢 |
| ✅ Atualizar status    | Um clique para mudar estado                   |
| ✅ Editar tarefa       | Nome, horários, área, observações             |
| ✅ Deletar tarefa      | Com confirmação                               |
| ✅ Persistência local  | Room Database                                 |
| ✅ Sincronização cloud | Firebase Firestore                            |
| ✅ Layout responsivo   | Grid em tablets, lista em smartphones         |
| ✅ Dark Mode           | Tema escuro automático                        |

### 📝 Registro de Atividades (100% Offline)
| Recurso               | Descrição                                  |
|-----------------------|--------------------------------------------|
| ✅ Formulário completo | Tipo, talhão, hora início/fim, observações |
| ✅ Autocomplete        | Sugestões de atividades agrícolas          |
| ✅ Validação de campos | Horários válidos, campos obrigatórios      |
| ✅ Histórico           | Lista de todas atividades registradas      |
| ✅ Persistência local  | Room Database                              |
| ✅ Sincronização cloud | Firebase Firestore                         |
| ✅ Layout responsivo   | Side-by-side em tablets                    |
| ✅ Dark Mode           | Tema escuro automático                     |

### 🌤️ Previsão Climática (Online + Cache)
| Recurso                      | Descrição                                          |
|------------------------------|----------------------------------------------------|
| ✅ Localização GPS            | Obtém clima da localização real do usuário         |
| ✅ Dados atuais               | Temperatura, umidade, condição                     |
| ✅ Previsão horária           | Carrossel horizontal com próximas horas            |
| ✅ Ícones dinâmicos           | Emojis por condição climática                      |
| ✅ Indicador de fonte         | 🟢 Online / 🟡 Offline (cache)                     |
| ✅ Fallback inteligente       | Última localização salva quando offline            |
| ✅ Pull to refresh            | Atualização manual                                 |
| ✅ **Alertas dinâmicos**      | ⚠️ Badges vermelho/laranja para GPS/Internet off   |
| ✅ **Mensagens informativas** | "Sem Conexão/Geolocalização" embaixo dos dados     |
| ✅ **Timeout seguro**         | 10s GPS + 5s geocoding para evitar travamentos     |
| ✅ **Correção de crash**      | Flag isResumed previne "Already resumed" exception |
| ✅ **Botão inteligente**      | "Tentar novamente" abre configurações de rede      |
| ✅ Dark Mode                  | Tema escuro automático                             |

### 🎨 UX & Melhorias Visuais
| Recurso                          | Descrição                                      |
|----------------------------------|------------------------------------------------|
| ✅ Dark Mode completo             | Suporte a tema claro/escuro do sistema         |
| ✅ Botão pulsante                 | FAB com animação para nova tarefa              |
| ✅ Carrossel de previsão          | Scroll horizontal para previsão horária        |
| ✅ GPS dinâmico                   | Localização real sem valores hardcoded         |
| ✅ **Alertas dinâmicos**          | Badges vermelho/laranja para conectividade/GPS |
| ✅ **Mensagens contextuais**      | Avisos embaixo dos dados quando offline        |
| ✅ **Telas de erro persistentes** | Aparecem sempre ao reiniciar app               |
| ✅ **Estado em tempo real**       | GPS/Internet atualizam dinamicamente           |
| ✅ Telas de erro amigáveis        | Feedback visual para GPS/permissões            |

---

## 📱 Screenshots

### Tema Claro ☀️

|        Tarefas         |         Registrar         |      Clima      |
|:----------------------:|:-------------------------:|:---------------:|
| Lista com FAB pulsante | Formulário + Autocomplete | GPS + Carrossel |

### Tema Escuro 🌙

|    Tarefas    |    Registrar    |     Clima     |
|:-------------:|:---------------:|:-------------:|
| Cards escuros | Formulário dark | Previsão dark |

### Telas de Estado

| Sem Permissão GPS  | GPS Indisponível | Offline com Cache  |
|:------------------:|:----------------:|:------------------:|
| Solicita permissão | Pede ativar GPS  | Mostra última loc. |

---

## 🎨 UX & Design

### Dark Mode

O app detecta automaticamente o tema do sistema e adapta todas as telas:

| Propriedade | Tema Claro                                                                                                                         | Tema Escuro                                                                                                |
|-------------|------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|
| Background  | <span style="background-color: #FAFAFA; color: #000; padding: 2px 4px; border-radius: 3px;">#FAFAFA</span>                         | <span style="background-color: #121212; color: #FFF; padding: 2px 4px; border-radius: 3px;">#121212</span> |
| Cards       | <span style="background-color: #FFFFFF; color: #000; padding: 2px 4px; border-radius: 3px; border: 1px solid #CCC;">#FFFFFF</span> | <span style="background-color: #1E1E1E; color: #FFF; padding: 2px 4px; border-radius: 3px;">#1E1E1E</span> |
| Primary     | <span style="background-color: #4CAF50; color: #FFF; padding: 2px 4px; border-radius: 3px;">#4CAF50</span>                         | <span style="background-color: #81C784; color: #000; padding: 2px 4px; border-radius: 3px;">#81C784</span> |
| Text        | <span style="background-color: #1A1A1A; color: #FFF; padding: 2px 4px; border-radius: 3px;">#1A1A1A</span>                         | <span style="background-color: #E0E0E0; color: #000; padding: 2px 4px; border-radius: 3px;">#E0E0E0</span> |

### Fluxo de Localização GPS

```
                    ┌──────────────┐
                    │ Abriu o App  │
                    └──────┬───────┘
                           │
                           ▼
                ┌─────────────────────┐
                │ Tem permissão GPS?  │
                └─────────┬───────────┘
                          │
              ┌───────────┴───────────┐
              │                       │
           ❌ NÃO                   ✅ SIM
              │                       │
              ▼                       ▼
    ┌──────────────────┐    ┌──────────────────┐
    │ Tela: "Permissão │    │ Conseguiu obter  │
    │   necessária"    │    │  localização?    │
    │                  │    └────────┬─────────┘
    │ [Abrir Config]   │             │
    └──────────────────┘  ┌──────────┴──────────┐
                          │                     │
                       ❌ NÃO                 ✅ SIM
                          │                     │
                          ▼                     ▼
               ┌───────────────────┐   ┌────────────────┐
               │ Tem localização   │   │ Salva loc. +   │
               │   salva (cache)?  │   │ Mostra clima   │
               └─────────┬─────────┘   │ da cidade real │
                         │             └────────────────┘
              ┌──────────┴──────────┐
              │                     │
           ❌ NÃO                 ✅ SIM
              │                     │
              ▼                     ▼
    ┌─────────────────┐   ┌──────────────────────┐
    │ Tela: "GPS      │   │ Usa última loc.      │
    │  indisponível"  │   │ + marca como offline │
    └─────────────────┘   └──────────────────────┘
```

### Autocomplete de Atividades
Conforme você preenche no aplicativo, ele salva localmente para sugerir caso você delete ou altere a atividade.

- 🌱 Plantio
- 🌱 Plantio de mudas
- 📋 Planejamento de safra

### Carrossel de Previsão Horária

Previsão por hora ➡️

| Agora | 15h | 16h | 17h |
|:-----:|:---:|:---:|:---:|
|  🌤️  | 🌤️ | 🌥️ | 🌙  |
|  28°  | 27° | 25° | 23° |
|  65%  | 68% | 72% | 75% |

---

## 🛠️ Stack Técnico

<table align="center">
  <tr>
    <td align="center"><img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/><br><b>Kotlin 2.0</b></td>
    <td align="center"><img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"/><br><b>Compose</b></td>
    <td align="center"><img src="https://img.shields.io/badge/Material%203-1976D2?style=for-the-badge&logo=material-design&logoColor=white"/><br><b>Material 3</b></td>
    <td align="center"><img src="https://img.shields.io/badge/Koin-FF6B00?style=for-the-badge&logo=koin&logoColor=white"/><br><b>Koin 3.5</b></td>
  </tr>
  <tr>
    <td align="center"><img src="https://img.shields.io/badge/Room-0078D4?style=for-the-badge&logo=microsoft&logoColor=white"/><br><b>Room 2.6</b></td>
    <td align="center"><img src="https://img.shields.io/badge/Retrofit-00D9FF?style=for-the-badge&logo=retrofit&logoColor=black"/><br><b>Retrofit 2.9</b></td>
    <td align="center"><img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black"/><br><b>Firestore</b></td>
    <td align="center"><img src="https://img.shields.io/badge/Coroutines-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/><br><b>Coroutines</b></td>
  </tr>
  <tr>
    <td align="center"><img src="https://img.shields.io/badge/Location-4285F4?style=for-the-badge&logo=google&logoColor=white"/><br><b>Play Services</b></td>
    <td align="center"><img src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white"/><br><b>JUnit 5</b></td>
    <td align="center"><img src="https://img.shields.io/badge/MockK-FF6B00?style=for-the-badge&logo=kotlin&logoColor=white"/><br><b>MockK</b></td>
    <td align="center"><img src="https://img.shields.io/badge/JaCoCo-003A86?style=for-the-badge&logo=codecov&logoColor=white"/><br><b>JaCoCo</b></td>
  </tr>
</table>

### Stack Completo

| Categoria | Tecnologia |
|-----------|------------|
| **Linguagem** | Kotlin 2.0 |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Arquitetura** | Clean Architecture + MVVM |
| **Injeção de Dependência** | Koin 3.5 |
| **Banco de Dados Local** | Room 2.6 |
| **HTTP Client** | Retrofit 2.9 + OkHttp |
| **Async & Reactive** | Coroutines + Flow + StateFlow |
| **Cloud Sync** | Firebase Firestore |
| **Localização** | Google Play Services Location |
| **Testes Unitários** | JUnit 4 + MockK + Coroutines Test |
| **Cobertura de Código** | JaCoCo |
| **Responsividade** | WindowSizeClass |
| **Tema** | Material 3 Dynamic Colors + Dark Mode |

---

## 📐 Arquitetura

### Clean Architecture + MVVM

```
┌─────────────────────────────────────────────────────────────┐
│                        PRESENTATION                         │
│  ┌──────────────┐    ┌──────────────┐    ┌─────────────┐    │
│  │  TaskScreen  │    │ TaskRegistry │    │WeatherScreen│    │
│  │  (Compose)   │    │    Screen    │    │  (Compose)  │    │
│  └──────┬───────┘    └──────┬───────┘    └──────┬──────┘    │
│         │                   │                   │           │
│  ┌──────▼──────┐     ┌──────▼───────┐    ┌──────▼──────┐    │
│  │TaskViewModel│     │ TaskRegistry │    │   Weather   │    │
│  │             │     │  ViewModel   │    │  ViewModel  │    │
│  └──────┬──────┘     └──────┬───────┘    └──────┬──────┘    │
└─────────┼───────────────────┼───────────────────┼───────────┘
          │                   │                   │
┌─────────▼───────────────────▼───────────────────▼───────────┐
│                          DOMAIN                             │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐    │
│  │ GetTasksUse │     │ InsertTask  │     │  GetWeather │    │
│  │    Case     │     │ RegistryUse │     │   UseCase   │    │
│  └──────┬──────┘     └──────┬──────┘     └──────┬──────┘    │
│         │                   │                   │           │
│  ┌──────▼───────────────────▼───────────────────▼──────┐    │
│  │                Repository Interfaces                │    │
│  └──────┬───────────────────┬────────────────────┬─────┘    │
└─────────┼───────────────────┼────────────────────┼──────────┘
          │                   │                    │
┌─────────▼───────────────────▼────────────────────▼───────────┐
│                           DATA                               │
│  ┌─────────────┐     ┌──────────────┐    ┌─────────────┐     │
│  │  TaskRepo   │     │ TaskRegistry │    │ WeatherRepo │     |
│  │    Impl     │     │   RepoImpl   │    │    Impl     │     │
│  └──────┬──────┘     └──────┬───────┘    └──────┬──────┘     │
│         │                   │                   │            |
│  ┌──────▼──────┐     ┌──────▼───────┐    ┌──────▼──────┐     │
│  │  Room DAO   │     │   Room DAO   │    │   Retrofit  │     │
│  │  Firebase   │     │   Firebase   │    │   Room DAO  │     │
│  └─────────────┘     └──────────────┘    └─────────────┘     │
└──────────────────────────────────────────────────────────────┘
```

### Fluxo de Dados

```
User Action → ViewModel → UseCase → Repository → DataSource (Room/API/Firebase)
                ↑                                      │
                └──────────── StateFlow ←──────────────┘
```

---

## 📁 Estrutura do Projeto

```
app/src/
├── main/java/com/example/dataagrin/app/
│   ├── domain/
│   │   ├── model/              # Task, TaskRegistry, Weather, TaskStatus
│   │   ├── repository/         # Interfaces dos repositórios
│   │   └── usecase/            # GetTasks, InsertTask, UpdateTask, DeleteTask...
│   │
│   ├── data/
│   │   ├── local/              # Room: AppDatabase, DAOs, Entities
│   │   ├── remote/             # Retrofit: WeatherApi, DTOs
│   │   ├── firebase/           # Firestore: TaskFirestoreRepository, Mappers
│   │   ├── repository/         # Implementações dos repositórios
│   │   ├── connectivity/       # ConnectivityChecker
│   │   └── location/           # LocationHelper (GPS + Geocoder)
│   │
│   ├── presentation/
│   │   ├── ui/
│   │   │   ├── TaskScreen.kt
│   │   │   ├── TaskRegistryScreen.kt
│   │   │   ├── WeatherScreen.kt
│   │   │   ├── Navigation.kt
│   │   │   ├── components/     # DetailItemWithIcon, TimeInputField...
│   │   │   └── utils/          # TimeValidation
│   │   └── viewmodel/          # TaskViewModel, TaskRegistryViewModel, WeatherViewModel
│   │
│   ├── di/                     # Koin: AppModule
│   ├── ui/theme/               # Material Theme, Colors (Dark/Light), Typography
│   ├── DataAgrinApp.kt         # Application (Koin init)
│   └── MainActivity.kt         # Activity + WindowSizeClass + Location Permission
│
└── test/java/com/example/dataagrin/app/
    ├── domain/
    │   ├── model/              # TaskTest, TaskRegistryTest
    │   └── usecase/            # GetTasksUseCaseTest, InsertTaskUseCaseTest...
    ├── data/
    │   ├── firebase/           # FirestoreMappersTest
    │   └── repository/         # TaskRepositoryImplTest, TaskRegistryRepositoryImplTest
    └── presentation/
        ├── ui/utils/           # TimeValidationTest
        └── viewmodel/          # TaskViewModelTest, TaskRegistryViewModelTest, WeatherViewModelTest
```

---

## 🚀 Como Executar

### Pré-requisitos

| Requisito | Versão | Obrigatório |
|-----------|--------|-------------|
| ![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=flat-square&logo=android&logoColor=white) | 2023.1+ | ✅ |
| ![JDK](https://img.shields.io/badge/JDK-ED8B00?style=flat-square&logo=openjdk&logoColor=white) | 11+ | ✅ |
| ![Android SDK](https://img.shields.io/badge/Android%20SDK-3DDC84?style=flat-square&logo=android&logoColor=white) | 26-34 | ✅ |
| ![Emulador](https://img.shields.io/badge/Emulador%20ou%20Dispositivo-3DDC84?style=flat-square&logo=android&logoColor=white) | - | ✅ |

### Instalação

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/DataAgrinMobile.git
cd DataAgrinMobile

# 2. Abra no Android Studio
File → Open → selecione a pasta DataAgrinMobile

# 3. Aguarde o sync do Gradle

# 4. Execute o app
Run → Run 'app' (Shift+F10)
```

### Build via Terminal

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Instalar no device conectado
./gradlew installDebug
```

---

## 🧪 Testes

### Executar Testes

```bash
# Todos os testes unitários
./gradlew testDebugUnitTest

# Com relatório de cobertura (JaCoCo)
./gradlew testDebugUnitTest jacocoTestReport

# Abrir relatório HTML
start app/build/reports/jacoco/index.html
```

### Cobertura de Código

| Pacote                   | Cobertura   | Testes                                                                |
|--------------------------|-------------|-----------------------------------------------------------------------|
| `domain.usecase`         | 🟢 **100%** | GetTasks, GetTaskById, InsertTask, UpdateTask, DeleteTask             |
| `domain.repository`      | 🟢 **100%** | Interfaces                                                            |
| `presentation.viewmodel` | 🟢 **81%**  | TaskViewModel, TaskRegistryViewModel, WeatherViewModel                |
| `presentation.ui.utils`  | 🟢 **93%**  | TimeValidation, TaskFormValidation                                    |
| `domain.model`           | 🟢 **87%**  | Task, TaskRegistry, TaskStatus, SyncStatus                            |
| `data.repository`        | 🟢 **93%**  | TaskRepositoryImpl, TaskRegistryRepositoryImpl, WeatherRepositoryImpl |
| `data.connectivity`      | 🟢 **91%**  | ConnectivityChecker                                                   |
| `data.location`          | 🟢 **81%**  | LocationHelper                                                        |
| `data.local`             | 🟢 **71%**  | DAOs, Converters                                                      |
| `data.firebase`          | 🟡 **38%**  | FirestoreMappers                                                      |
| `presentation.ui`        | 🔴 **0%**   | Telas Compose (não testáveis unitariamente)                           |
| `ui.theme`               | 🔴 **0%**   | Temas (não testáveis unitariamente)                                   |

**Total: 180 testes passando ✅ | Cobertura geral: 18% (devido ao UI não ser coberto por testes unitários)**

### Estrutura de Testes

```kotlin
// Exemplo: GetTasksUseCaseTest.kt
@Test
fun `invoke should return tasks from repository`() = runBlocking {
    val fakeTasks = listOf(Task(1, "Plantio", "Área A", "08:00", "", "", TaskStatus.PENDING))
    coEvery { taskRepository.getAllTasks() } returns flowOf(fakeTasks)

    val result = getTasksUseCase().first()

    assertEquals(fakeTasks, result)
}
```

---

## 📱 Responsividade

O app adapta o layout baseado no **WindowSizeClass**:

| Classe       | Dispositivo           | Layout                        |
|--------------|-----------------------|-------------------------------|
| **Compact**  | Smartphones portrait  | Lista vertical                |
| **Medium**   | Smartphones landscape | Layout adaptado               |
| **Expanded** | Tablets / Desktop     | Grid 2 colunas / Side-by-side |

### Implementação

```kotlin
// MainActivity.kt
val windowSizeClass = calculateWindowSizeClass(this)
AppNavigation(windowSizeClass = windowSizeClass)

// TaskScreen.kt
if (isExpandedScreen) {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) { ... }
} else {
    LazyColumn { ... }
}
```

---

## 🌐 API de Clima

### Open-Meteo (Gratuita)

| Propriedade      | Valor                                                    |
|------------------|----------------------------------------------------------|
| **Base URL**     | `https://api.open-meteo.com/v1/forecast`                 |
| **Autenticação** | Nenhuma                                                  |
| **Rate Limit**   | 10.000 req/dia                                           |
| **Documentação** | [open-meteo.com/en/docs](https://open-meteo.com/en/docs) |

### Parâmetros Utilizados

```
?latitude={GPS_LAT}
&longitude={GPS_LON}
&current=temperature_2m,relative_humidity_2m,weather_code
&hourly=temperature_2m,weather_code,relative_humidity_2m
```

### Localização

📍 **Dinâmica via GPS** - O app obtém a localização real do dispositivo

---

## 📍 Localização GPS

### Permissões Necessárias

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

### Fluxo de Obtenção

```
┌───────────────┬─────────────────────────────────────────────┐
│ Etapa         │ Descrição                                   │
├───────────────┼─────────────────────────────────────────────┤
│ 1. Permissão  │ Solicita permissão ao usuário na abertura   │
│ 2. GPS        │ Usa FusedLocationProviderClient             │
│ 3. Geocoder   │ Converte coordenadas em nome da cidade      │
│ 4. Cache      │ Salva última localização para uso offline   │
│ 5. Fallback   │ Se offline, usa última localização salva    │
└───────────────┴─────────────────────────────────────────────┘
```

### Comportamento por Cenário

| Cenário                         | Comportamento                    |
|---------------------------------|----------------------------------|
| ✅ Permissão concedida + GPS on  | Mostra clima da localização real |
| ✅ Permissão concedida + Offline | Usa última localização salva     |
| ❌ Permissão negada              | Exibe tela solicitando permissão |
| ❌ GPS desligado (sem cache)     | Exibe tela "GPS indisponível"    |

### Implementação

```kotlin
// LocationHelper.kt
class LocationHelper(context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    
    suspend fun getCurrentLocation(): LocationData? {
        // 1. Verifica permissão
        // 2. Tenta lastLocation (rápido)
        // 3. Fallback: getCurrentLocation do GPS
        // 4. Geocoder para nome da cidade
        // 5. Salva no SharedPreferences
    }
    
    suspend fun getLocationOrSavedFallback(): LocationData? {
        return getCurrentLocation() ?: getSavedLocation()
    }
}
```

---

## 🗄️ Banco de Dados

### Room Database (v4)

```kotlin
@Database(
    entities = [TaskEntity::class, TaskRegistryEntity::class, WeatherCacheEntity::class, HourlyWeatherCacheEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase()
```

### Tabelas

```
┌────────────────────────┬──────────────────────────────────────────────────────────────────────────────┐
│ Tabela                 │ Campos                                                                       │
├────────────────────────┼──────────────────────────────────────────────────────────────────────────────┤
│ `tasks`                │ id, name, area, scheduledTime, endTime, observations, status                 │
│ `task_registry`        │ id, type, area, startTime, endTime, observations                             │
│ `weather_cache`        │ id, temperature, humidity, weatherCode, weatherDescription, lastUpdated      │
│ `hourly_weather_cache` │ id, time, temperature, weatherCode, humidity, description, weatherId (FK)    │
└────────────────────────┴──────────────────────────────────────────────────────────────────────────────┘
```

---

## 🔥 Sincronização Firebase

### Firestore Collections

```
firestore/
├── tasks/
│   └── {taskId}/
│       ├── id: Int
│       ├── name: String
│       ├── area: String
│       ├── scheduledTime: String
│       ├── endTime: String
│       ├── observations: String
│       └── status: String
│
└── taskRegistries/
    └── {registryId}/
        ├── id: Int
        ├── type: String
        ├── area: String
        ├── startTime: String
        ├── endTime: String
        └── observations: String
```

### Histórico de Alterações

O app mantém um log de todas as alterações em tarefas, registrando:
- Ação (criação, edição, exclusão, mudança de status)
- Timestamp
- Dados anteriores e posteriores

---

## ⚙️ Configurações

### AndroidManifest.xml

```xml
<!-- Internet -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Localização GPS -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<application
    android:name=".DataAgrinApp"
    android:theme="@style/Theme.DataAgrinMobile">
    ...
</application>
```

### Dependências Principais

```kotlin
// build.gradle.kts (app)
dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    
    // Room
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // Koin
    implementation("io.insert-koin:koin-android:3.5.0")
    implementation("io.insert-koin:koin-androidx-compose:3.5.0")
    
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-firestore")
    
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Google Play Services - Location
    implementation("com.google.android.gms:play-services-location:21.1.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}
```

---

## 📊 Checklist do Desafio

### Requisitos Obrigatórios

- [x] Android Nativo com Kotlin
- [x] UI com Jetpack Compose
- [x] Padrão arquitetural (MVVM + Clean Architecture)
- [x] Persistência local (Room Database)
- [x] Código versionado no GitHub
- [x] README com instruções

### Funcionalidades

- [x] Tela de Tarefas (offline) com status
- [x] Tela de Registro de Atividades (offline)
- [x] Tela de Clima (online + cache)
- [x] Indicador visual de fonte de dados

### Diferenciais Implementados

- [x] Sincronização com Firebase Firestore
- [x] Testes unitários (180 testes)
- [x] Cobertura de código com JaCoCo
- [x] UI responsiva (WindowSizeClass)
- [x] Animações via Compose
- [x] Injeção de dependência (Koin)
- [x] Modo offline completo
- [x] **Dark Mode** completo (segue tema do sistema)
- [x] **GPS dinâmico** (localização real do usuário)
- [x] **Autocomplete** de atividades agrícolas
- [x] **Carrossel** de previsão horária
- [x] **Botão pulsante** (FAB animado)
- [x] **Telas de erro** amigáveis (GPS/permissões)
- [x] **Geocoder** (converte coordenadas → nome da cidade)

---

## 📝 Licença

Este projeto foi desenvolvido como parte de um desafio técnico para a **Data Agrin**.

---

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black"/>
  <img src="https://img.shields.io/badge/Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"/>
</p>

<p align="center">
  <b>Desenvolvido com ❤️ usando Kotlin + Jetpack Compose</b>
</p>

<p align="center">
  <a href="https://github.com/seu-usuario">
    <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" alt="GitHub"/>
  </a>
</p>

<p align="center">
  <a href="#dataagrin-mobile-">⬆️ Voltar ao topo</a>
</p>

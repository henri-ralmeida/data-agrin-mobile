# DataAgrin Mobile 🌾

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android"/>
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"/>
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase"/>
</p>

<p align="center">
  <b>App Android nativo para monitoramento de tarefas agrícolas, registro de atividades e previsão climática.</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Testes-76%20passando-brightgreen" alt="Tests"/>
  <img src="https://img.shields.io/badge/Cobertura%20UseCases-100%25-brightgreen" alt="Coverage"/>
  <img src="https://img.shields.io/badge/Min%20SDK-26-blue" alt="Min SDK"/>
  <img src="https://img.shields.io/badge/Target%20SDK-34-blue" alt="Target SDK"/>
</p>

---

## 📑 Índice

- [Funcionalidades](#-funcionalidades)
- [Screenshots](#-screenshots)
- [Stack Técnico](#️-stack-técnico)
- [Arquitetura](#-arquitetura)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Como Executar](#-como-executar)
- [Testes](#-testes)
- [Responsividade](#-responsividade)
- [API de Clima](#-api-de-clima)
- [Banco de Dados](#️-banco-de-dados)
- [Sincronização Firebase](#-sincronização-firebase)

---

## ✨ Funcionalidades

### 📋 Tela de Tarefas (100% Offline)
| Recurso | Descrição |
|---------|-----------|
| ✅ Listar tarefas | Nome, área/talhão, horário previsto |
| ✅ Status visual | Pendente 🔴 / Em andamento 🟠 / Finalizada 🟢 |
| ✅ Atualizar status | Um clique para mudar estado |
| ✅ Editar tarefa | Nome, horários, área, observações |
| ✅ Deletar tarefa | Com confirmação |
| ✅ Persistência local | Room Database |
| ✅ Sincronização cloud | Firebase Firestore |
| ✅ Layout responsivo | Grid em tablets, lista em smartphones |

### 📝 Registro de Atividades (100% Offline)
| Recurso | Descrição |
|---------|-----------|
| ✅ Formulário completo | Tipo, talhão, hora início/fim, observações |
| ✅ Validação de campos | Horários válidos, campos obrigatórios |
| ✅ Histórico | Lista de todas atividades registradas |
| ✅ Persistência local | Room Database |
| ✅ Sincronização cloud | Firebase Firestore |
| ✅ Layout responsivo | Side-by-side em tablets |

### 🌤️ Previsão Climática (Online + Cache)
| Recurso | Descrição |
|---------|-----------|
| ✅ Dados atuais | Temperatura, umidade, condição |
| ✅ Previsão horária | Próximas 3-6 horas |
| ✅ Ícones dinâmicos | Emojis por condição climática |
| ✅ Indicador de fonte | 🟢 API / 🟡 Cache |
| ✅ Fallback offline | Última consulta salva |
| ✅ Pull to refresh | Atualização manual |

---

## 📱 Screenshots

| Tarefas (Smartphone) | Registros (Smartphone) | Clima |
|:--------------------:|:----------------------:|:-----:|
| Lista vertical | Formulário + Histórico | Dados + Previsão |

| Tarefas (Tablet/Landscape) | Registros (Tablet/Landscape) |
|:--------------------------:|:----------------------------:|
| Grid 2 colunas | Side-by-side |

---

## 🛠️ Stack Técnico

| Categoria | Tecnologia |
|-----------|------------|
| **Linguagem** | Kotlin 2.0 |
| **UI** | Jetpack Compose + Material 3 |
| **Arquitetura** | Clean Architecture + MVVM |
| **DI** | Koin 3.5 |
| **Database** | Room 2.6 |
| **Network** | Retrofit 2.9 + OkHttp |
| **Async** | Coroutines + Flow + StateFlow |
| **Cloud Sync** | Firebase Firestore |
| **Testes** | JUnit 4 + MockK + Coroutines Test |
| **Cobertura** | JaCoCo |
| **Responsividade** | WindowSizeClass |

---

## 🏗 Arquitetura

### Clean Architecture + MVVM

```
┌─────────────────────────────────────────────────────────────┐
│                      PRESENTATION                           │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐     │
│  │ TaskScreen  │    │TaskRegistry │    │WeatherScreen│     │
│  │ (Compose)   │    │  Screen     │    │  (Compose)  │     │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘     │
│         │                  │                  │             │
│  ┌──────▼──────┐    ┌──────▼──────┐    ┌──────▼──────┐     │
│  │TaskViewModel│    │TaskRegistry │    │Weather      │     │
│  │             │    │  ViewModel  │    │  ViewModel  │     │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘     │
└─────────┼──────────────────┼──────────────────┼─────────────┘
          │                  │                  │
┌─────────▼──────────────────▼──────────────────▼─────────────┐
│                        DOMAIN                               │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐     │
│  │GetTasksUse  │    │InsertTask   │    │GetWeather   │     │
│  │  Case       │    │RegistryUse │    │  UseCase    │     │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘     │
│         │                  │                  │             │
│  ┌──────▼──────────────────▼──────────────────▼──────┐     │
│  │              Repository Interfaces                │     │
│  └──────┬──────────────────┬──────────────────┬──────┘     │
└─────────┼──────────────────┼──────────────────┼─────────────┘
          │                  │                  │
┌─────────▼──────────────────▼──────────────────▼─────────────┐
│                         DATA                                │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐     │
│  │TaskRepo     │    │TaskRegistry │    │WeatherRepo  │     │
│  │  Impl       │    │  RepoImpl   │    │    Impl     │     │
│  └──────┬──────┘    └──────┬──────┘    └─────┬───────┘     │
│         │                  │                 │              │
│  ┌──────▼──────┐    ┌──────▼──────┐    ┌────▼────┐        │
│  │  Room DAO   │    │  Room DAO   │    │Retrofit │        │
│  │  Firebase   │    │  Firebase   │    │Room DAO │        │
│  └─────────────┘    └─────────────┘    └─────────┘        │
└─────────────────────────────────────────────────────────────┘
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
│   │   └── connectivity/       # ConnectivityChecker
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
│   ├── ui/theme/               # Material Theme, Colors, Typography
│   ├── DataAgrinApp.kt         # Application (Koin init)
│   └── MainActivity.kt         # Activity + WindowSizeClass
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

- **Android Studio** Hedgehog (2023.1+) ou mais recente
- **JDK** 11+
- **Android SDK** 26-34
- Emulador ou dispositivo físico

### Instalação

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/DataAgrinMobile.git
cd DataAgrinMobile

# 2. Abra no Android Studio
# File → Open → selecione a pasta DataAgrinMobile

# 3. Aguarde o sync do Gradle

# 4. Execute o app
# Run → Run 'app' (Shift+F10)
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

| Pacote | Cobertura | Testes |
|--------|-----------|--------|
| `domain.usecase` | 🟢 **100%** | GetTasks, GetTaskById, InsertTask, UpdateTask, DeleteTask |
| `domain.repository` | 🟢 **100%** | Interfaces |
| `presentation.viewmodel` | 🟢 **88%** | TaskViewModel, TaskRegistryViewModel, WeatherViewModel |
| `presentation.ui.utils` | 🟢 **89%** | TimeValidation |
| `domain.model` | 🟢 **80%** | Task, TaskRegistry |
| `data.firebase` | 🟡 **38%** | FirestoreMappers |

**Total: 76 testes passando ✅**

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

| Classe | Dispositivo | Layout |
|--------|-------------|--------|
| **Compact** | Smartphones portrait | Lista vertical |
| **Medium** | Smartphones landscape | Layout adaptado |
| **Expanded** | Tablets / Desktop | Grid 2 colunas / Side-by-side |

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

| Propriedade | Valor |
|-------------|-------|
| **Base URL** | `https://api.open-meteo.com/v1/forecast` |
| **Autenticação** | Nenhuma |
| **Rate Limit** | 10.000 req/dia |
| **Documentação** | [open-meteo.com/en/docs](https://open-meteo.com/en/docs) |

### Parâmetros Utilizados

```
?latitude=-23.55
&longitude=-46.64
&current=temperature_2m,relative_humidity_2m,weather_code
&hourly=temperature_2m,weather_code
&timezone=America/Sao_Paulo
```

### Localização Padrão

📍 **São Paulo, SP** (-23.55, -46.64)

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

| Tabela | Campos |
|--------|--------|
| `tasks` | id, name, area, scheduledTime, endTime, observations, status |
| `task_registry` | id, type, area, startTime, endTime, observations |
| `weather_cache` | id, temperature, humidity, weatherCode, weatherDescription, lastUpdated |
| `hourly_weather_cache` | id, time, temperature, weatherCode, humidity, description, weatherId (FK) |

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
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

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
- [x] Testes unitários (76 testes)
- [x] Cobertura de código com JaCoCo
- [x] UI responsiva (WindowSizeClass)
- [x] Animações via Compose
- [x] Injeção de dependência (Koin)
- [x] Modo offline completo

---

## 📝 Licença

Este projeto foi desenvolvido como parte de um desafio técnico para a **Data Agrin**.

---

<p align="center">
  <b>Desenvolvido com ❤️ usando Kotlin + Jetpack Compose</b>
</p>

<p align="center">
  <a href="#dataagrin-mobile-">⬆️ Voltar ao topo</a>
</p>

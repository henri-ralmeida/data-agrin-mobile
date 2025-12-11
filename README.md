# DataAgrin Mobile 🌾

App Android nativo em Kotlin com Jetpack Compose para monitoramento de tarefas agrícolas, registro de atividades e previsão climática.

## ✨ Funcionalidades

### 📋 Tela de Tarefas (Offline)
- Listar tarefas do dia com status (Pendente/Em andamento/Finalizada)
- Atualizar status em tempo real
- Persistência local com Room Database
- Layout responsivo (compacto e expandido)

### 📝 Registro de Atividades (Offline)
- Formulário para registro de atividades no campo
- Campos: tipo de atividade, talhão/área, hora de início/fim, observações
- Histórico de atividades salvas
- Validações obrigatórias
- Persistência local com Room Database

### 🌤️ Previsão Climática (Online + Cache)
- Integração com API Open-Meteo (gratuita)
- Temperatura atual, umidade, descrição do clima
- Previsão horária (próximas 24 horas)
- Ícones dinâmicos por condição climática
- Cache local para acesso offline
- Indicador visual: verde (dados da API) / amarelo (dados em cache)
- Botão de atualização e retry em caso de erro

## 🛠️ Stack Técnico

- **Linguagem:** Kotlin
- **UI Framework:** Jetpack Compose
- **Arquitetura:** Clean Architecture (Domain → Data → Presentation)
- **Injeção de Dependência:** Koin
- **Banco de Dados Local:** Room Database (v4)
- **Rede:** Retrofit + OkHttp
- **Async:** Coroutines + Flow
- **Padrão de State:** StateFlow (MVVM)

## 📁 Estrutura de Pastas

```
app/src/main/java/com/example/dataagrin/app/
├── domain/
│   ├── model/          # Task, Activity, Weather
│   ├── repository/     # Interfaces (TaskRepository, ActivityRepository, WeatherRepository)
│   └── usecase/        # UseCases (GetTasks, UpdateTask, InsertActivity, GetWeather, etc)
├── data/
│   ├── local/          # Room entities, DAOs, AppDatabase
│   ├── remote/         # WeatherApi, DTOs (WeatherDto)
│   └── repository/     # Implementações (TaskRepositoryImpl, ActivityRepositoryImpl, WeatherRepositoryImpl)
├── presentation/
│   ├── ui/             # Composables (TaskScreen, ActivityScreen, WeatherScreen, Navigation)
│   └── viewmodel/      # ViewModels (TaskViewModel, ActivityViewModel, WeatherViewModel)
├── di/                 # Koin modules (AppModule)
├── DataAgrinApp.kt     # Application class (Koin initialization)
└── MainActivity.kt     # Activity raiz
```

## 🚀 Como Executar

### Pré-requisitos
- Android Studio (2023.1+)
- Android SDK min 24 (Android 7.0)
- Emulador ou dispositivo físico

### Passos
1. **Clone o repositório**
   ```bash
   git clone <url-do-repositorio>
   cd DataAgrinMobile
   ```

2. **Abra no Android Studio**
   - File → Open → DataAgrinMobile
   - Aguarde sync do Gradle

3. **Configure o dispositivo**
   - Conecte um dispositivo físico OU abra um emulador
   - Verifique permissões de Internet no AndroidManifest.xml

4. **Rode a aplicação**
   - Run → Run 'app' (Shift+F10)
   - Ou clique no botão ▶ (Run) na toolbar

### Testando Funcionalidades

#### Tarefas
- A tela inicial mostra 3 tarefas de exemplo
- Clique em uma tarefa para ver detalhes
- Atualize o status (Pendente → Em andamento → Finalizada)
- Status persiste localmente

#### Atividades
- Na aba "Registros", preencha o formulário
- Digite tipo, talhão, horas de início/fim e observações
- Clique "Salvar Atividade"
- Visualize no histórico abaixo

#### Clima
- Na aba "Clima", os dados aparecem automaticamente
- Conectado à internet → ícone verde (dados da API)
- Sem internet → ícone amarelo (dados em cache)
- Clique "Atualizar" para refetch da API
- Próximas 24h aparecem em cards deslizáveis

## 🗄️ Banco de Dados

O Room Database (AppDatabase) possui 3 tabelas:

1. **tasks** (tarefas)
   - id, name, area, scheduledTime, status

2. **activities** (atividades)
   - id, type, area, startTime, endTime, observations

3. **weather_cache** + **hourly_weather_cache** (clima)
   - weather_cache: temperatura, umidade, weatherCode, lastUpdated
   - hourly_weather_cache: time, temperature, weatherId (FK)

## 🧪 Testes

Testes unitários em `app/src/test/` para:
- `GetTasksUseCaseTest`
- `UpdateTaskUseCaseTest`
- `InsertActivityUseCaseTest`
- `GetActivitiesUseCaseTest`
- `GetWeatherUseCaseTest`
- `TaskViewModelTest`
- `ActivityViewModelTest`
- `WeatherViewModelTest`

**Rodar testes:**
```bash
./gradlew test
```

## 🌐 API Integrada

### Open-Meteo Weather API
- **URL:** https://api.open-meteo.com/v1/forecast
- **Params:** latitude, longitude, current, hourly
- **Resposta:** Temperatura, umidade, weather_code, previsão horária
- **Sem autenticação requerida**
- **Docs:** https://open-meteo.com/en/docs

**Localização padrão:** São Paulo, SP (-23.55, -46.64)

## 📱 Responsividade

A app detecta tamanho de tela via `WindowSizeClass`:
- **Compact (smartphones):** Layout single-column
- **Expanded (tablets/landscape):** Layout side-by-side

## 🔄 Offline-First Design

- ✅ Tarefas: 100% offline (Room local)
- ✅ Atividades: 100% offline (Room local)
- ✅ Clima: Online com fallback (Open-Meteo + Room cache)

Dados são sincronizados automaticamente quando você abre cada tela.

## 📊 Padrões Arquiteturais

### Clean Architecture
```
Domain (regras de negócio)
  ↓
Data (persistência, rede)
  ↓
Presentation (UI, ViewModels)
```

### MVVM
- **Model:** Data classes (Task, Activity, Weather)
- **View:** Composables (TaskScreen, ActivityScreen, WeatherScreen)
- **ViewModel:** State management (TaskViewModel, ActivityViewModel, WeatherViewModel)

### Injeção de Dependência com Koin
```kotlin
// app/src/main/java/com/example/dataagrin/app/di/AppModule.kt
val appModule = module {
    // Repositories
    single<TaskRepository> { TaskRepositoryImpl(get()) }
    single<ActivityRepository> { ActivityRepositoryImpl(get()) }
    single<WeatherRepository> { WeatherRepositoryImpl(get(), get()) }
    
    // ViewModels
    viewModel { TaskViewModel(get(), get()) }
    viewModel { ActivityViewModel(get(), get()) }
    viewModel { WeatherViewModel(get()) }
}
```

## ⚙️ Configurações Importantes

### AndroidManifest.xml
```xml
<application android:name=".DataAgrinApp">
    <!-- Permissões: INTERNET, ACCESS_NETWORK_STATE, ACCESS_COARSE_LOCATION -->
</application>
```

### Room Database Version
- **Versão atual:** 4
- **fallbackToDestructiveMigration:** Enabled (recria DB em schema changes)

## 🚦 Próximos Passos / Diferenciais

- [ ] Sincronização com Firebase Firestore ou Supabase
- [ ] Suporte a localização dinâmica (GPS) para clima
- [ ] Autenticação de usuário
- [ ] Sincronização automática em background
- [ ] Animações Lottie nas transições
- [ ] Modo escuro (Dark Mode)
- [ ] Kotlin Multiplatform (KMP) para iOS

## 📝 Notas de Desenvolvimento

### Converters
`Converters.kt` mapeia `TaskStatus` enum ↔ String para Room:
```kotlin
@TypeConverter
fun fromTaskStatus(status: TaskStatus): String = status.name

@TypeConverter
fun toTaskStatus(status: String): TaskStatus = TaskStatus.valueOf(status)
```

### Error Handling
- Weather API falha → Tenta carregar do cache
- Cache vazio → Exibe botão "Tentar novamente"
- Logs com tag "WeatherRepository" para debug

## 📄 Licença

Este projeto é fornecido como está para fins educacionais.

---

**Desenvolvido com ❤️ por Data Agrin**

Para dúvidas ou sugestões, abra uma issue no repositório.

# CHANGELOG

## [1.0.0] - 2025-12-11

### ✨ Funcionalidades Core Implementadas

#### 📋 Tela de Tarefas
- ✅ Listagem de tarefas com status (Pendente/Em andamento/Finalizada)
- ✅ Atualização de status em tempo real
- ✅ Layout responsivo (Compact para smartphones, Expanded para tablets)
- ✅ Persistência local com Room Database
- ✅ Use-cases: GetTasksUseCase, UpdateTaskUseCase
- ✅ ViewModels com StateFlow
- ✅ Testes unitários (GetTasksUseCaseTest, UpdateTaskUseCaseTest, TaskViewModelTest)

#### 📝 Tela de Atividades (Registros)
- ✅ Formulário de registro com validações
- ✅ Campos: Tipo, Talhão/Área, Hora Início, Hora Fim, Observações
- ✅ Histórico de atividades salvas
- ✅ Persistência local com Room Database
- ✅ Time picker para entrada de horas (string-based com validação)
- ✅ Use-cases: InsertActivityUseCase, GetActivitiesUseCase
- ✅ Testes unitários (InsertActivityUseCaseTest, GetActivitiesUseCaseTest, ActivityViewModelTest)

#### 🌤️ Tela de Clima
- ✅ Integração com API Open-Meteo (gratuita)
- ✅ Exibição: Temperatura, Umidade, Descrição do clima
- ✅ Previsão horária (próximas 24h)
- ✅ Ícones dinâmicos por condição (sol, nuvem, chuva, chuvisco)
- ✅ Cache local com Room Database (2 tabelas: weather_cache + hourly_weather_cache)
- ✅ Fallback offline (último clima em cache)
- ✅ Indicador visual: Verde (API) / Amarelo (cache)
- ✅ Botão Atualizar + Tentar novamente
- ✅ Loading state com feedback ao usuário
- ✅ Error handling com logs (tag: WeatherRepository)
- ✅ Use-case: GetWeatherUseCase
- ✅ Testes unitários (GetWeatherUseCaseTest, WeatherViewModelTest)

### 🏗️ Arquitetura & Infraestrutura

- ✅ Clean Architecture (Domain → Data → Presentation)
- ✅ MVVM Pattern
- ✅ Injeção de Dependência com Koin
- ✅ Room Database v4 (3 tabelas: tasks, activities, weather_cache, hourly_weather_cache)
- ✅ Retrofit + OkHttp para rede
- ✅ Coroutines + Flow para async
- ✅ StateFlow para state management
- ✅ Navigation com bottom bar (3 abas)
- ✅ Responsive Design (WindowSizeClass detection)

### 📱 UI & Compose

- ✅ Jetpack Compose para todas as telas
- ✅ Material Design 3 components
- ✅ Transições fade entre telas
- ✅ Cards para exibição de dados
- ✅ LazyColumn/LazyRow para listas
- ✅ Validação visual com mensagens de erro
- ✅ Bottom Navigation Bar com 3 itens

### 🧪 Testes

- ✅ MainCoroutineRule para testes com Coroutines
- ✅ MockK para mocking
- ✅ 9 testes unitários escritos e funcionando
  - GetTasksUseCaseTest
  - UpdateTaskUseCaseTest
  - InsertActivityUseCaseTest
  - GetActivitiesUseCaseTest
  - GetWeatherUseCaseTest
  - TaskViewModelTest
  - ActivityViewModelTest
  - WeatherViewModelTest

### 📚 Documentação

- ✅ README.md completo
  - Setup e como rodar
  - Estrutura de pastas
  - Stack técnico
  - Offline-first design
  - Arquitetura explicada
  - Notas de desenvolvimento

### 🔧 Configurações

- ✅ AndroidManifest.xml com Application (DataAgrinApp)
- ✅ Converters para TaskStatus enum
- ✅ Database schema com ForeignKeys
- ✅ AppModule.kt com Koin configuration
- ✅ Gradle dependencies alinhadas

### 🐛 Bugs Corrigidos

1. **WeatherApi**: Query params hardcoded → Dinâmicos com defaults
2. **Forecast**: Retornava 168h → Limitado a 24h
3. **WeatherIcons**: Faltava ícone de chuva/chuvisco → Adicionado
4. **Error Handling**: Sem logging → Adicionado Log.e com tag
5. **ActivityForm**: Horários vazios → Adicionado campos com validação
6. **WeatherViewModel**: Sem isLoading → Adicionado StateFlow
7. **WeatherScreen UI**: Crashes on null → Melhorado com estados distintos

### 📝 Commits Aplicados

1. `add: corrigindo TaskStatus converter e melhorando ActivityScreen com time picker`
2. `fix: corrigindo WeatherScreen e integrando API Open-Meteo com caching local`
3. `add: criando README completo com arquitetura e instruções de uso`
4. `add: testando isLoading state no WeatherViewModelTest`

### 🚀 Próximos Passos (Diferenciais)

- [ ] Sincronização com Firebase Firestore / Supabase
- [ ] Suporte a localização dinâmica (GPS)
- [ ] Autenticação de usuário
- [ ] Sincronização automática em background
- [ ] Animações Lottie
- [ ] Dark Mode
- [ ] Kotlin Multiplatform (KMP)

### ⚠️ Notas Técnicas

- **Room v4**: Database com migration strategy = fallbackToDestructiveMigration
- **API Open-Meteo**: Sem rate limits, sem autenticação, gratuita
- **Coords Default**: São Paulo (-23.55, -46.64) - dinâmicas via params
- **Forecast Horário**: Retorna apenas 24 primeiras horas
- **Cache Strategy**: FullWeatherCache com Relation + ForeignKey

---

**Status:** ✅ Core completo e testado. Pronto para emulação e testes.

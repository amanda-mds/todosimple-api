# ToDo Simple API

API REST de lista de tarefas (to-do list) construída com Spring Boot, com autenticação, tratamento de erros padronizado e front-end consumindo a API ponta a ponta.

## Funcionalidades

- Cadastro e login de usuário, com senha criptografada (BCrypt)
- CRUD completo de tarefas, vinculadas ao usuário dono
- Validação de dados de entrada (Bean Validation)
- Tratamento de erros centralizado, com respostas HTTP e JSON padronizados (404, 400, 401)
- Testes unitários da camada de serviço (JUnit + Mockito)
- Ambiente totalmente containerizado com Docker (API + banco de dados sobem juntos, com um único comando)
- Front-end em HTML, CSS e JavaScript puro, consumindo a API via `fetch()`

## Tecnologias

- Java 21
- Spring Boot 4 (Web, Data JPA, Validation)
- Hibernate / JPA
- MySQL
- BCrypt (spring-security-crypto) para hash de senha
- Lombok
- JUnit 5 + Mockito
- Docker e Docker Compose
- HTML, CSS e JavaScript (sem frameworks)

## Endpoints

### Usuário

| Método | Rota          | Descrição                              |
|--------|---------------|-----------------------------------------|
| POST   | `/user`       | Cadastra um novo usuário                |
| POST   | `/user/login` | Autentica usuário e senha               |
| GET    | `/user/{id}`  | Busca usuário por id                    |
| PUT    | `/user/{id}`  | Atualiza a senha do usuário             |
| DELETE | `/user/{id}`  | Remove o usuário                        |

### Tarefas

| Método | Rota                  | Descrição                          |
|--------|------------------------|-------------------------------------|
| GET    | `/task/{id}`           | Busca tarefa por id                 |
| GET    | `/task/user/{userId}`  | Lista todas as tarefas de um usuário|
| POST   | `/task`                | Cria uma nova tarefa                |
| PUT    | `/task/{id}`           | Atualiza a descrição da tarefa      |
| DELETE | `/task/{id}`           | Remove a tarefa                     |

## Como rodar o projeto

### Opção 1 — Com Docker (recomendado)

Só é preciso ter o [Docker](https://www.docker.com/products/docker-desktop) instalado. Nenhuma outra instalação é necessária (nem Java, nem Maven, nem MySQL).

1. Crie um arquivo `.env` na raiz do projeto com:

DB_PASSWORD=uma_senha_de_sua_escolha

2. Rode:
```bash
   docker-compose up --build
```
3. A API sobe em `http://localhost:8080`, junto com o banco de dados MySQL já configurado.

### Opção 2 — Rodando localmente

Pré-requisitos: Java 21, Maven, MySQL rodando localmente.

Configure a senha do banco como variável de ambiente (não deixe em texto puro no `application.properties`):

```properties
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}
```

Defina `DB_PASSWORD` no seu ambiente (ou nas Run Configurations da sua IDE) e rode:

```bash
./mvnw spring-boot:run
```

O banco `todosimple` é criado automaticamente na primeira execução.

### Front-end

Os arquivos do front estão na pasta `view/`. Como o front faz chamadas `fetch()` para a API, é preciso servi-lo por um servidor local (não abrir o `.html` direto pelo `file://`):

- Pelo IntelliJ: abra `view/signup.html` e clique no ícone de navegador no editor
- Ou, via terminal, dentro da pasta `view`: `npx serve .`

### Fluxo de uso

1. Acesse `signup.html` e cadastre um usuário
2. Acesse `login.html` e entre com usuário e senha
3. Gerencie suas tarefas em `index.html`

### Rodando os testes

```bash
./mvnw test
```

Os testes cobrem a camada de serviço (`UserService` e `TaskService`), incluindo casos de sucesso, erro de autenticação e validação de que a senha nunca é persistida em texto puro.

## Decisões técnicas

- **Comparação de entidades por id**: `equals()`/`hashCode()` de `User` e `Task` comparam apenas pelo `id`, evitando problemas de hash instável em coleções (padrão recomendado para entidades JPA).
- **Exceções customizadas**: em vez de `RuntimeException` genérica, o projeto usa exceções específicas (`ObjectNotFoundException`, `DataIntegrityException`, `InvalidCredentialsException`) mapeadas para os status HTTP corretos por um `@ControllerAdvice`.
- **`@JsonIgnore` em `User.tasks`**: evita recursão infinita na serialização JSON causada pelo relacionamento bidirecional entre `User` e `Task`.
- **Senha via variável de ambiente**: nenhuma credencial fica versionada no repositório; tanto em ambiente local quanto no Docker, a senha do banco é injetada em tempo de execução.

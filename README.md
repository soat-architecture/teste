<div align="center">

<h1>Oficina de Reparos Automotivos</h1>
<p align="left">
    Este projeto é um sistema de backend robusto para gerenciar uma oficina de reparos automotivos, construído com Java e Spring Boot. Ele fornece um conjunto abrangente de recursos para otimizar as operações,
    incluindo cadastro de clientes e veículos, agendamento de serviços e gerenciamento de pedidos. Ao aproveitar uma arquitetura limpa e princípios de design orientado a domínio (DDD), o sistema
    garante manutenibilidade, escalabilidade e uma clara separação de preocupações. Essa abordagem promove uma estrutura orientada para os negócios, tornando o sistema adaptável à evolução dos
    requisitos e mais fácil de gerenciar ao longo do tempo.

</p>

[![Test](https://github.com/12SOAT/auto-repair-shop/actions/workflows/continuous-integration-pipeline.yml/badge.svg)](https://github.com/12SOAT/auto-repair-shop/actions/workflows/continuous-integration-pipeline.yml)
[![SonarQube](https://github.com/12SOAT/auto-repair-shop/actions/workflows/sonarqube.yaml/badge.svg)](https://github.com/12SOAT/auto-repair-shop/actions/workflows/sonarqube.yaml)

</div>

# :clipboard: Índice

- [Sobre o Projeto](#star-sobre-o-projeto)
  - [Tecnologias](#microscope-tecnologias)
  - [Ferramentas](#wrench-ferramentas)
- [Começando](#beginner-começando)
  - [Executando a aplicação](#computer-executando-a-aplicação)
  - [Executando os testes](#test_tube-executando-os-testes)
- [Uso do Docker](#whale-uso-do-docker)
  - [Executando a Aplicação com Docker](#-executando-a-aplicação-com-docker)
  - [Acessando a Documentação da API (Swagger)](#-acessando-a-documentação-da-api-swagger)
  - [Parando a Aplicação](#-parando-a-aplicação)

## :star: Sobre o projeto
Descrição sobre o projeto

### :microscope: Tecnologias
- [Java](https://www.java.com/pt-BR/)
- [Gradle](https://gradle.org/)
### :wrench: Ferramentas
- [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- [Postman](https://www.postman.com/)



## :beginner: Começando

### :computer: Executando a aplicação
Clone o projeto
```bash
git clone https://github.com/12SOAT/auto-repair-shop.git
```
Vá para o diretório desejado
```bash
cd auto-repair-shop
```
Em sistemas macOS/UNIX, você precisará tornar o script auxiliar do Gradle `gradlew` executável. Para fazer isso, use o comando `chmod`:
```bash
chmod +x gradlew
```
Compile o projeto
```bash
./gradlew build
```
Execute a aplicação
```bash
./gradlew run
```

### :test_tube: Executando os testes
Para executar os testes, use o seguinte comando
```bash
./gradlew test
```

## :whale: Uso do Docker

### 🚀 Executando a Aplicação com Docker

Para facilitar a execução do ambiente completo (aplicação, banco de dados), utilize o Docker Compose.

**Pré-requisitos:**
*   [Docker](https://docs.docker.com/get-docker/)
*   [Docker Compose](https://docs.docker.com/compose/install/)

**Passos:**

1.  Execute o seguinte comando para construir as imagens e iniciar os contêineres:
```bash
docker-compose up --build
```
Este comando irá baixar as imagens necessárias, construir a imagem da aplicação e iniciar os serviços `app` e `postgres-auto-repair-shop`.

Para executar em modo *detached* (em segundo plano), utilize a flag `-d`:
```bash
docker-compose up --build -d
```

### 📚 Acessando a Documentação da API (Swagger)

Após a aplicação iniciar com sucesso, a documentação da API, gerada pelo Swagger UI, estará disponível no seguinte endereço:

[http://localhost:9000/api/swagger-ui.html](http://localhost:9000/api/swagger-ui.html)

### 🛑 Parando a Aplicação

Para parar todos os contêineres e remover as redes criadas pelo Docker Compose, execute o comando abaixo no diretório raiz do projeto:

```bash
docker-compose down
```

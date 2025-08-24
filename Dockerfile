# 📝 PT-BR
# Dockerfile para Aplicação Java usando Build em Múltiplos Estágios
#
# Este Dockerfile foi projetado para construir e executar uma aplicação Java usando um processo de build em múltiplos estágios.
# O build em múltiplos estágios ajuda a criar uma imagem Docker final menor e mais segura, separando o ambiente de build do ambiente de execução.
#
# Estágios:
# 1. Estágio de Construção: Usa o JDK para compilar e construir a aplicação Java.
# 2. Estágio Final: Usa o JRE para executar a aplicação compilada.
#
# Benefícios:
#
# Tamanho de Imagem Reduzido: A imagem final é mínima, pois inclui apenas o JRE e o JAR da aplicação.
# Segurança Aprimorada: Ao excluir ferramentas de build e arquivos desnecessários, a superfície de ataque da imagem é reduzida.
# Implantação Mais Rápida: Tamanhos de imagem menores levam a tempos de download mais rápidos e inicialização mais rápida, melhorando a eficiência geral da implantação.
# Ambiente de Execução Otimizado: Usar o JRE em vez do JDK completo no estágio final fornece um ambiente de execução mais leve.
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./gradlew build -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/auto-repair-shop.jar ./app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]

# docker build -t [username]/[image_name]:[version] .
# docker push [username]/[image_name]:[version]
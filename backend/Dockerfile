# =============================================================================
# Imagem da API UniBus para o Render (Docker em duas etapas)
#   Etapa 1: compila o projeto com Maven e gera o JAR executável
#   Etapa 2: executa o JAR em uma imagem leve, apenas com o Java (JRE)
# =============================================================================

# ---------- Etapa 1: build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
COPY src ./src

# Os testes rodam localmente (./mvnw test); no deploy só geramos o JAR (target/backend.jar)
RUN mvn -B -q clean package -DskipTests

# ---------- Etapa 2: execução ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Roda como usuário comum (sem privilégios de root)
RUN groupadd --system app && useradd --system --gid app --no-create-home app
COPY --from=build /build/target/backend.jar app.jar
USER app

# O Render usa o perfil "prod": banco PostgreSQL (Supabase) configurado por variáveis de ambiente
ENV SPRING_PROFILES_ACTIVE=prod

# Ajustes de memória para o plano gratuito do Render (512 MB) e partida mais rápida
ENV JAVA_OPTS="-XX:+UseSerialGC -XX:MaxRAMPercentage=65 -XX:TieredStopAtLevel=1 -Xss512k"

# A porta real vem da variável PORT (o Render a define); 8080 é só o padrão local
EXPOSE 8080

# "exec" faz o Java receber o SIGTERM do Render e encerrar de forma graciosa
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

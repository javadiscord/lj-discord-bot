FROM openjdk:21-jdk

WORKDIR /usr/bot

COPY /build/libs/Java-Discord-Bot.jar /usr/bot

ENV BOT_TOKEN=<token> \
    CHATGPT_API_KEY=<token> \
    TENOR_API_KEY=<token>

ENTRYPOINT ["java", "-jar", "Java-Discord-Bot.jar"]

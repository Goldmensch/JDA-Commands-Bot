module dev.goldmensch.jdacbot {
    requires io.github.kaktushose.jda.commands.core;
    requires net.dv8tion.jda;
    requires kotlin.stdlib;

    requires java.net.http;
    requires jdk.httpserver;
    requires io.github.kaktushose.jda.commands.extension.guice;
    requires com.google.guice;
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;

    exports dev.goldmensch.jdacbot;
    exports dev.goldmensch.jdacbot.cmd;
    exports dev.goldmensch.jdacbot.service.webhook;
    exports dev.goldmensch.jdacbot.service.webhook.pojo;


    opens dev.goldmensch.jdacbot.service.webhook.pojo to com.fasterxml.jackson.databind;
}
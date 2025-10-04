package dev.goldmensch.jdacbot.data;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.datasource.DataSourceCreator;
import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.chojo.sadu.queries.api.query.Query;
import de.chojo.sadu.sqlite.databases.SqLite;
import de.chojo.sadu.updater.SqlUpdater;
import jakarta.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Collection;

@Singleton
public class ConfigRepository {

    private final JDA jda;

    private ConfigRepository(JDA jda) {
        this.jda = jda;
    }

    public static ConfigRepository create(JDA jda) {
        Path configPath = Path.of("data", "config.sqlite");
        prepareConfigFile(configPath);

        HikariDataSource source = DataSourceCreator.create(SqLite.get())
                .configure(config ->
                        config.path(configPath))
                .create()
                .build();
        update(source);
        configureQuery(source);

        return new ConfigRepository(jda);
    }

    private static void prepareConfigFile(Path config) {
        try {
            Files.createDirectories(config.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Error while preparing config", e);
        }
    }

    private static void update(DataSource dataSource) {
        try {
            SqlUpdater.builder(dataSource, SqLite.get())
                    .withClassLoader(ConfigRepository.class.getClassLoader())
                    .execute();
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Error during sql schema update", e);
        }
    }

    private static void configureQuery(DataSource source) {
        QueryConfiguration config = QueryConfiguration.builder(source)
                .setThrowExceptions(true)
                .build();
        QueryConfiguration.setDefault(config);
    }

    public void addReleaseChannel(TextChannel channel) {
        Query.query("INSERT INTO release_channel (id) VALUES (:id)")
                .single(Call.of().bind("id", channel.getIdLong()))
                .insert();
    }

    public void removeReleaseChannel(TextChannel channel) {
        Query.query("DELETE FROM release_channel WHERE id = :id")
                .single(Call.of().bind("id", channel.getIdLong()))
                .delete();
    }

    public Collection<TextChannel> retrieveReleaseChannels() {
        return Query.query("SELECT id FROM release_channel")
                .single()
                .map(row -> row.getLong("id"))
                .all()
                .stream()
                .map(jda::getTextChannelById)
                .toList();
    }
}

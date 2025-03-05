package com.lehdonjoki.listoo.testutils;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.flywaydb.test.annotation.FlywayTest;
import org.springframework.test.context.TestPropertySource;

@FlywayTest
@TestPropertySource(properties = {"zonky.test.database.postgres.docker.image=postgres:16-alpine"})
@AutoConfigureEmbeddedDatabase(
    type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES,
    provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.DOCKER,
    refresh = AutoConfigureEmbeddedDatabase.RefreshMode.BEFORE_EACH_TEST_METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestingDatabase {}

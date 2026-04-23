package io.camtrack.totalapi.config;


import java.io.IOException;

import org.postgresql.util.PGobject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
public class PgJsonbJacksonConfig {

    public static class PGobjectJsonSerializer extends JsonSerializer<PGobject> {

        @Override
        public void serialize(PGobject value,
                              JsonGenerator gen,
                              SerializerProvider serializers) throws IOException {

            if (value == null || value.getValue() == null) {
                gen.writeNull();
                return;
            }

            String type = value.getType();
            String json = value.getValue();

            if ("json".equalsIgnoreCase(type) || "jsonb".equalsIgnoreCase(type)) {
                // Écrit directement le JSON brut (pas une string)
                gen.writeRawValue(json);
            } else {
                gen.writeString(json);
            }
        }
    }

    @Bean
    public Module pgobjectModule() {
        SimpleModule module = new SimpleModule("pgobject-jsonb-module");
        module.addSerializer(PGobject.class, new PGobjectJsonSerializer());
        return module;
    }
}

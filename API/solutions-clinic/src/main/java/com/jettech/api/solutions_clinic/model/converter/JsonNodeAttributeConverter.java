package com.jettech.api.solutions_clinic.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converte entre JsonNode (entidade) e String (coluna JSONB) para persistência em PostgreSQL.
 */
@Converter(autoApply = false)
public class JsonNodeAttributeConverter implements AttributeConverter<JsonNode, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JsonNode attribute) {
        if (attribute == null || attribute.isNull()) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Falha ao serializar JSON para coluna", e);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readTree(dbData);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Falha ao deserializar JSON da coluna", e);
        }
    }
}

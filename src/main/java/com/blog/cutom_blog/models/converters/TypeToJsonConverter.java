package com.blog.cutom_blog.models.converters;

import com.blog.cutom_blog.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;
import lombok.SneakyThrows;



public abstract class TypeToJsonConverter<Typ> implements AttributeConverter< Typ, String> {

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(final Typ attribute) {
        return attribute != null
            ? Utils.writeJson(attribute)
            : null;
    }

    @SneakyThrows
    @Override
    public Typ convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : Utils.parseJson(dbData, new TypeReference<Typ>(){});
    }
}

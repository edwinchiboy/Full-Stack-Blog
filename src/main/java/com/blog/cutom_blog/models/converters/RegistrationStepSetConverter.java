package com.blog.cutom_blog.models.converters;

import com.blog.cutom_blog.constants.RegistrationStep;
import com.blog.cutom_blog.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;


import lombok.SneakyThrows;

import java.util.Set;


public class RegistrationStepSetConverter extends TypeToJsonConverter<Set<RegistrationStep>> {

    @SneakyThrows
    @Override
    public Set<RegistrationStep> convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : Utils.parseJson(dbData, new TypeReference<Set<RegistrationStep>>() {});
    }

}

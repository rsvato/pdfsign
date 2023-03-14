package org.example.pdfsign.utils;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class FieldNameGenerator {

    public String generateFieldName() {
        return RandomStringUtils.random(8, true, false);
    }

}

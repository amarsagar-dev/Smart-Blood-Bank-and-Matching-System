package com.bloodbank.bloodbank.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BloodGroupConverter implements AttributeConverter<Donor.BloodGroup, String> {

    @Override
    public String convertToDatabaseColumn(Donor.BloodGroup attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public Donor.BloodGroup convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Donor.BloodGroup.fromValue(dbData);
    }
}
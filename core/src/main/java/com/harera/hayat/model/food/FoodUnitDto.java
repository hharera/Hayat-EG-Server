package com.harera.hayat.model.food;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harera.hayat.model.BaseEntityDto;

import lombok.Data;

@Data
public class FoodUnitDto extends BaseEntityDto {

    @JsonProperty("arabic_name")
    private String arabicName;

    @JsonProperty("english_name")
    private String englishName;

    @Column(name = "description")
    private String description;
}

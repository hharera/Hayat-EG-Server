package com.harera.hayat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntityDto {

    private Long id;
    private Boolean active;
}
package com.harera.hayat.model.donation.medicine;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true,
                value = { "id", "active", "city", "user", "unit", "medicine" })
public class MedicineDonationRequest extends MedicineDonationDto {

    @JsonProperty(value = "city_id")
    private long cityId;
    @JsonProperty(value = "unit_id")
    private long unitId;
    @JsonProperty(value = "medicine_id")
    private Long medicineId;
    @JsonProperty(value = "medicine_expiration_date")
    private OffsetDateTime medicineExpirationDate;
}

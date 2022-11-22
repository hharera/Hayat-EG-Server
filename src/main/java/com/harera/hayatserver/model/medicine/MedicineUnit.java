package com.harera.hayatserver.model.medicine;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.harera.hayatserver.model.BaseEntity;

@Entity
@Table(name = "medicine_unit")
public class MedicineUnit extends BaseEntity {

    @Basic
    @Column(name = "name")
    private String name;
}

package com.harera.hayat.model.donation;

import java.time.OffsetDateTime;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.harera.hayat.config.OffsetDateTimeSerializer;
import com.harera.hayat.model.BaseEntity;
import com.harera.hayat.model.city.City;
import com.harera.hayat.model.donation.image.DonationImage;
import com.harera.hayat.model.user.User;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "donation")
@Setter
@Getter
public class Donation extends BaseEntity {

    @Basic
    @Column(name = "title")
    private String title;

    @Basic
    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "donation_date")
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    private OffsetDateTime donationDate;

    @Basic
    @Column(name = "expiration_date")
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    private OffsetDateTime donationExpirationDate;

    @Column(name = "communication_method")
    private CommunicationMethod communicationMethod;

    @Column(name = "category")
    private DonationCategory category;

    @Column(name = "state")
    private DonationState state;

    @ManyToOne(targetEntity = City.class)
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private City city;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "donation", fetch = FetchType.LAZY)
    private List<DonationImage> images;
}


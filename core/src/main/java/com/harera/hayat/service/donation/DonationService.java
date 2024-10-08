package com.harera.hayat.service.donation;

import java.util.LinkedList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.harera.hayat.model.donation.Donation;
import com.harera.hayat.model.donation.DonationDto;
import com.harera.hayat.model.donation.DonationResponse;
import com.harera.hayat.model.donation.food.FoodDonationResponse;
import com.harera.hayat.model.donation.property.PropertyDonation;
import com.harera.hayat.model.donation.property.PropertyDonationRequest;
import com.harera.hayat.repository.city.CityRepository;
import com.harera.hayat.repository.donation.DonationRepository;
import com.harera.hayat.repository.donation.FoodDonationRepository;

@Service
public class DonationService {

    private final DonationRepository donationRepository;
    private final FoodDonationRepository foodDonationRepository;
    private final DonationValidation donationValidation;
    private final ModelMapper modelMapper;


    public DonationService(DonationRepository donationRepository,
                    FoodDonationRepository foodDonationRepository,
                    DonationValidation donationValidation, CityRepository cityRepository,
                    ModelMapper modelMapper) {
        this.donationRepository = donationRepository;
        this.foodDonationRepository = foodDonationRepository;
        this.donationValidation = donationValidation;
        this.modelMapper = modelMapper;
    }

    public List<DonationResponse> list(Integer page, Integer size, String query,
                    String category) {
        donationValidation.validateList(page, size);
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Donation> all = donationRepository.findAll(pageable);
        List<DonationResponse> donationResponses = all.map(
                        donation -> modelMapper.map(donation, DonationResponse.class))
                        .toList();
        return donationResponses;
    }

    public ResponseEntity<DonationResponse> donateProperty(
                    PropertyDonationRequest propertyDonationRequest) {
        donationValidation.validateDonateProperty(propertyDonationRequest);
        Donation donation = modelMapper.map(propertyDonationRequest, Donation.class);
        PropertyDonation propertyDonation =
                        modelMapper.map(propertyDonationRequest, PropertyDonation.class);
        Donation save = donationRepository.save(donation);
        DonationResponse donationResponse = modelMapper.map(save, DonationResponse.class);
        return ResponseEntity.ok(donationResponse);
    }


    public List<FoodDonationResponse> listFoodDonations() {
        List<FoodDonationResponse> foodDonationList = new LinkedList<>();
        foodDonationRepository.findAll().forEach(foodDonation -> {
            FoodDonationResponse foodDonationResponse =
                            modelMapper.map(foodDonation, FoodDonationResponse.class);
            DonationDto donationDto = modelMapper.map(foodDonation.getDonation(),
                            DonationDto.class);
            modelMapper.map(donationDto, foodDonationResponse);
            foodDonationList.add(foodDonationResponse);
        });
        return foodDonationList;
    }
}

package com.harera.hayat.stub.city;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.harera.hayat.model.city.City;
import com.harera.hayat.repository.city.CityRepository;

@Service
public class CityStubs {

    @Autowired
    private CityRepository repository;

    public City insert(String arabicName, String englishName) {
        City city = create(arabicName, englishName);
        repository.save(city);
        return city;
    }

    private City create(String arabicName, String englishName) {
        City city = new City();
        city.setArabicName(arabicName);
        city.setEnglishName(englishName);
        return city;
    }
}

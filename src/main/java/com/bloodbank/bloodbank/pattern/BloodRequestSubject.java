package com.bloodbank.bloodbank.pattern;

import com.bloodbank.bloodbank.model.BloodRequest;
import com.bloodbank.bloodbank.model.Donor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BloodRequestSubject {

    private final List<DonorObserver> observers;

    public BloodRequestSubject(List<DonorObserver> observers) {
        this.observers = observers;
    }

    public void notifyObservers(List<Donor> donors, BloodRequest request) {
        for (Donor donor : donors) {
            for (DonorObserver observer : observers) {
                observer.update(donor, request);
            }
        }
    }
}

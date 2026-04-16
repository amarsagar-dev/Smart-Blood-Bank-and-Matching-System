package com.bloodbank.bloodbank.pattern;

import com.bloodbank.bloodbank.model.BloodRequest;
import com.bloodbank.bloodbank.model.Donor;

public interface DonorObserver {
	void update(Donor donor, BloodRequest request);
}

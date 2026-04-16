package com.bloodbank.bloodbank.pattern;

import com.bloodbank.bloodbank.model.Donor;
import com.bloodbank.bloodbank.model.Donor.BloodGroup;
import java.util.List;

public interface MatchingStrategy {
	List<Donor> findMatchingDonors(BloodGroup requestedGroup);
}

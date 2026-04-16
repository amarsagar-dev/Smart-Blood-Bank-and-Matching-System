package com.bloodbank.bloodbank.pattern;

import com.bloodbank.bloodbank.model.Donor;
import com.bloodbank.bloodbank.model.Donor.AvailabilityStatus;
import com.bloodbank.bloodbank.model.Donor.BloodGroup;
import com.bloodbank.bloodbank.repository.DonorRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class BloodGroupMatchingStrategy implements MatchingStrategy {

	private final DonorRepository donorRepository;

	private static final Map<BloodGroup, List<BloodGroup>> COMPATIBLE_DONORS = new EnumMap<>(BloodGroup.class);

	static {
		COMPATIBLE_DONORS.put(BloodGroup.O_NEG, List.of(BloodGroup.O_NEG));
		COMPATIBLE_DONORS.put(BloodGroup.O_POS, List.of(BloodGroup.O_POS, BloodGroup.O_NEG));
		COMPATIBLE_DONORS.put(BloodGroup.A_NEG, List.of(BloodGroup.A_NEG, BloodGroup.O_NEG));
		COMPATIBLE_DONORS.put(BloodGroup.A_POS, List.of(BloodGroup.A_POS, BloodGroup.A_NEG, BloodGroup.O_POS, BloodGroup.O_NEG));
		COMPATIBLE_DONORS.put(BloodGroup.B_NEG, List.of(BloodGroup.B_NEG, BloodGroup.O_NEG));
		COMPATIBLE_DONORS.put(BloodGroup.B_POS, List.of(BloodGroup.B_POS, BloodGroup.B_NEG, BloodGroup.O_POS, BloodGroup.O_NEG));
		COMPATIBLE_DONORS.put(BloodGroup.AB_NEG, List.of(BloodGroup.AB_NEG, BloodGroup.A_NEG, BloodGroup.B_NEG, BloodGroup.O_NEG));
		COMPATIBLE_DONORS.put(BloodGroup.AB_POS, List.of(
				BloodGroup.AB_POS,
				BloodGroup.AB_NEG,
				BloodGroup.A_POS,
				BloodGroup.A_NEG,
				BloodGroup.B_POS,
				BloodGroup.B_NEG,
				BloodGroup.O_POS,
				BloodGroup.O_NEG
		));
	}

	public BloodGroupMatchingStrategy(DonorRepository donorRepository) {
		this.donorRepository = donorRepository;
	}

	@Override
	public List<Donor> findMatchingDonors(BloodGroup requestedGroup) {
		List<Donor> matches = new ArrayList<>();
		for (BloodGroup donorGroup : COMPATIBLE_DONORS.getOrDefault(requestedGroup, List.of())) {
			matches.addAll(donorRepository.findByBloodGroupAndAvailabilityStatus(donorGroup, AvailabilityStatus.AVAILABLE));
		}
		return matches;
	}
}

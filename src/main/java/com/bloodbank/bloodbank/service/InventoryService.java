package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.model.BloodInventory;
import com.bloodbank.bloodbank.model.Donor;
import com.bloodbank.bloodbank.model.Hospital;
import com.bloodbank.bloodbank.repository.BloodInventoryRepository;
import com.bloodbank.bloodbank.repository.HospitalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class InventoryService {

	private final BloodInventoryRepository bloodInventoryRepository;
	private final HospitalRepository hospitalRepository;

	public InventoryService(BloodInventoryRepository bloodInventoryRepository,
							HospitalRepository hospitalRepository) {
		this.bloodInventoryRepository = bloodInventoryRepository;
		this.hospitalRepository = hospitalRepository;
	}

	public List<BloodInventory> getInventoryByHospital(Integer hospitalId) {
		validateHospital(hospitalId);
		return bloodInventoryRepository.findByHospitalHospitalId(hospitalId)
				.stream()
				.sorted(Comparator.comparing(i -> i.getBloodGroup().name()))
				.toList();
	}

	public long getTotalUnits(Integer hospitalId) {
		validateHospital(hospitalId);
		return bloodInventoryRepository.findByHospitalHospitalId(hospitalId)
				.stream()
				.mapToLong(i -> i.getUnitsAvailable() == null ? 0 : i.getUnitsAvailable())
				.sum();
	}

	public BloodInventory upsertInventory(Integer hospitalId, String bloodGroup, int units, String expiryDate) {
		Hospital hospital = validateHospital(hospitalId);

		Donor.BloodGroup parsedGroup = Donor.BloodGroup.fromValue(bloodGroup);
		int sanitizedUnits = Math.max(0, units);

		BloodInventory entry = bloodInventoryRepository
				.findByHospitalHospitalIdAndBloodGroup(hospitalId, parsedGroup)
				.orElse(new BloodInventory());

		entry.setHospital(hospital);
		entry.setBloodGroup(parsedGroup);
		entry.setUnitsAvailable(sanitizedUnits);
		entry.setUpdatedAt(LocalDateTime.now());

		if (expiryDate != null && !expiryDate.isBlank()) {
			entry.setExpiryDate(LocalDate.parse(expiryDate));
		}

		return bloodInventoryRepository.save(entry);
	}

	private Hospital validateHospital(Integer hospitalId) {
		return hospitalRepository.findById(hospitalId)
				.orElseThrow(() -> new RuntimeException("Hospital not found"));
	}
}

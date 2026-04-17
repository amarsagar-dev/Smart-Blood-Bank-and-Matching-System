// package com.bloodbank.bloodbank.service;

// import com.bloodbank.bloodbank.model.Hospital;
// import com.bloodbank.bloodbank.model.User;
// import com.bloodbank.bloodbank.repository.HospitalRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// @Service
// public class HospitalService {

//     @Autowired
//     private HospitalRepository hospitalRepository;

//     public Hospital saveHospital(User user, String hospitalName,
//                                   String licenseNumber, String address, String city) {
//         Hospital hospital = hospitalRepository
//                 .findByUserUserId(user.getUserId())
//                 .orElse(new Hospital());

//         hospital.setUser(user);
//         hospital.setHospitalName(hospitalName);
//         hospital.setLicenseNumber(licenseNumber);
//         hospital.setAddress(address);
//         hospital.setCity(city);

//         return hospitalRepository.save(hospital);
//     }

//     public boolean hasProfile(Integer userId) {
//         return hospitalRepository.findByUserUserId(userId).isPresent();
//     }
// }package com.bloodbank.bloodbank.service;

// import com.bloodbank.bloodbank.model.Hospital;
// import com.bloodbank.bloodbank.model.User;
// import com.bloodbank.bloodbank.repository.HospitalRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// @Service
// public class HospitalService {

//     @Autowired
//     private HospitalRepository hospitalRepository;

//     public Hospital saveHospital(User user, String hospitalName,
//                                   String licenseNumber, String address, String city) {
//         Hospital hospital = hospitalRepository
//                 .findByUserUserId(user.getUserId())
//                 .orElse(new Hospital());

//         hospital.setUser(user);
//         hospital.setHospitalName(hospitalName);
//         hospital.setLicenseNumber(licenseNumber);
//         hospital.setAddress(address);
//         hospital.setCity(city);

//         return hospitalRepository.save(hospital);
//     }

//     public boolean hasProfile(Integer userId) {
//         return hospitalRepository.findByUserUserId(userId).isPresent();
//     }
// }   

package com.bloodbank.bloodbank.service;

import com.bloodbank.bloodbank.model.Hospital;
import com.bloodbank.bloodbank.model.User;
import com.bloodbank.bloodbank.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    public Hospital saveHospital(User user, String hospitalName,
                                  String licenseNumber, String address, String city) {
        Hospital hospital = hospitalRepository
                .findByUserUserId(user.getUserId())
                .orElse(new Hospital());

        hospital.setUser(user);
        hospital.setHospitalName(hospitalName);
        hospital.setLicenseNumber(licenseNumber);
        hospital.setAddress(address);
        hospital.setCity(city);

        return hospitalRepository.save(hospital);
    }

    public boolean hasProfile(Integer userId) {
        return hospitalRepository.findByUserUserId(userId).isPresent();
    }
}
package com.gulshan.doctor.appointment.Service.Implementation;


import com.gulshan.doctor.appointment.Entity.*;
import com.gulshan.doctor.appointment.Entity.Projections.DoctorDetailsProjection;
import com.gulshan.doctor.appointment.Exceptions.ResourceNotFoundException;
import com.gulshan.doctor.appointment.Repository.DoctorRepository;
import com.gulshan.doctor.appointment.Repository.SpecializationRepository;
import com.gulshan.doctor.appointment.Repository.UserProfileRepository;
import com.gulshan.doctor.appointment.Repository.UserRepository;
import com.gulshan.doctor.appointment.RequestDTO.DoctorCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.DoctorUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.DoctorCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.DoctorResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.PatientUpdateResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.DoctorServiceInterface;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorServiceInterface {

    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper;

    @Override
    public DoctorCreateResponseDto addDoctorDetails(String imageDir,
                                                    DoctorCreateRequestDto doctorCreateRequestDto,
                                                    MultipartFile imageFile) throws IOException {
        String name = imageFile.getOriginalFilename();
        String imageName = UUID.randomUUID() + "_" + name;
        String filePath = imageDir + File.separator + imageName;

        File dir = new File(imageDir);
        if(!dir.exists()){
            dir.mkdir();
        }

        Files.copy(imageFile.getInputStream(), Paths.get(filePath));
        doctorCreateRequestDto.setImageName(imageName);
        Doctor doctor = doctorMapperMethod(doctorCreateRequestDto);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        doctor.setUser(user);
        Doctor savedDoctor = null;
        try {
             savedDoctor =  doctorRepository.save(doctor);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        DoctorCreateResponseDto responseDto = modelMapper.map(savedDoctor, DoctorCreateResponseDto.class);
        return responseDto;
    }

    @Override
    public DoctorResponseDto getDoctorByUserId(Long userId) {
        DoctorDetailsProjection doctorDetailsProjection = doctorRepository.findDoctorDetailsByUserId(userId)
                .orElseThrow( () -> new ResourceNotFoundException("Doctor details not found."));
        DoctorResponseDto doctorResponseDto = modelMapper.map(doctorDetailsProjection,DoctorResponseDto.class);
        return doctorResponseDto;
    }

    @Override
    public List<DoctorResponseDto> getAllDoctorsBySpecializationId(Long specializationId) {
        List<DoctorDetailsProjection> doctorDetailsProjectionList = doctorRepository.findAllBySpecialization_SpecializationId(specializationId);
        List<DoctorResponseDto> doctorResponseDtoList = doctorDetailsProjectionList.stream()
                .map(doctor -> modelMapper.map(doctor,DoctorResponseDto.class)).toList();
        return doctorResponseDtoList;
    }

    @Override
    public DoctorResponseDto updateDoctorDetails(String imageDir, DoctorUpdateRequestDto doctorUpdateRequestDto, MultipartFile imageFile) throws IOException {
        User user = userRepository.findById(doctorUpdateRequestDto.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        UserProfile profile = userProfileRepository
                .findByUser_IdAndDeletedFalse(doctorUpdateRequestDto.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User profile not found"));

        Doctor doctor = doctorRepository
                .findByUser_Id(doctorUpdateRequestDto.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient not found"));

        user.setName(doctorUpdateRequestDto.getName());
        user.setEmail(doctorUpdateRequestDto.getEmail());
        profile.setCity(doctorUpdateRequestDto.getCity());
        profile.setMobNumber(doctorUpdateRequestDto.getMobNumber());
        profile.setDateOfBirth(doctorUpdateRequestDto.getDateOfBirth());
        profile.setGender(doctorUpdateRequestDto.getGender());
        profile.setAddressLine(doctorUpdateRequestDto.getAddressLine());
        profile.setPincode(doctorUpdateRequestDto.getPincode());
        profile.setState(doctorUpdateRequestDto.getState());
        profile.setCountry(doctorUpdateRequestDto.getCountry());

        doctor.setQualificationDegree(doctorUpdateRequestDto.getQualificationDegree());
        doctor.setSpecialization(specializationRepository.findById(doctorUpdateRequestDto.getSpecializationId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found with the provided ID")));
        doctor.setLicenseNumber(doctorUpdateRequestDto.getLicenseNumber());
        doctor.setConsultationFee(doctorUpdateRequestDto.getConsultationFee());
        doctor.setPracticeStartDate(doctorUpdateRequestDto.getPracticeStartDate());
        doctor.setAvgConsultationTime(doctorUpdateRequestDto.getAvgConsultationTime());


        if(!imageFile.getOriginalFilename().equals(doctor.getImageName())){
            String name = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
           String oldImagPath = imageDir + File.separator + doctor.getImageName();
           String newImagPath = imageDir + File.separator + name;
           Files.deleteIfExists(Paths.get(oldImagPath));
           Files.copy(imageFile.getInputStream(),Paths.get(newImagPath));
           doctor.setImageName(name);
        }

        User updatedUser = userRepository.save(user);
        UserProfile updatedUserProfile = userProfileRepository.save(profile);
        Doctor updatedDoctor = doctorRepository.save(doctor);

        DoctorResponseDto response = mapperMethodForUpdatedDoctorResponse(updatedUser,updatedUserProfile,updatedDoctor);
        return response;

    }

    public Doctor doctorMapperMethod(DoctorCreateRequestDto doctorCreateRequestDto){
        Doctor response = new Doctor();
        response.setQualificationDegree(doctorCreateRequestDto.getQualificationDegree());
        response.setLicenseNumber(doctorCreateRequestDto.getLicenseNumber());
        response.setConsultationFee(doctorCreateRequestDto.getConsultationFee());
        response.setPracticeStartDate(doctorCreateRequestDto.getPracticeStartDate());
        response.setImageName(doctorCreateRequestDto.getImageName());
        response.setAvgConsultationTime(doctorCreateRequestDto.getAvgConsultationTime());

        Specialization specialization = specializationRepository.findById(doctorCreateRequestDto.getSpecializationId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not exist with id : "+ doctorCreateRequestDto.getSpecializationId()));
        response.setSpecialization(specialization);
        return response;
    }


    private DoctorResponseDto mapperMethodForUpdatedDoctorResponse(User user, UserProfile userProfile, Doctor doctor){
        DoctorResponseDto response = new DoctorResponseDto();
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt());

        response.setMobNumber(userProfile.getMobNumber());
        response.setDateOfBirth(userProfile.getDateOfBirth());
        response.setGender(userProfile.getGender());
        response.setAddressLine(userProfile.getAddressLine());
        response.setCity(userProfile.getCity());
        response.setPincode(userProfile.getPincode());
        response.setState(userProfile.getState());
        response.setCountry(userProfile.getCountry());

        response.setSpecializationName(doctor.getSpecialization().getSpecializationName());
        response.setAvgConsultationTime(doctor.getAvgConsultationTime());
        response.setConsultationFee(doctor.getConsultationFee());
        response.setImageName(doctor.getLicenseNumber());
        response.setLicenseNumber(doctor.getLicenseNumber());
        response.setPracticeStartDate(doctor.getPracticeStartDate());
        response.setQualificationDegree(doctor.getQualificationDegree());
        return response;
    }

}

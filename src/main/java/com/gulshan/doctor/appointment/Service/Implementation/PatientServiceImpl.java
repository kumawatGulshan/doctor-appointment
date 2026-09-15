package com.gulshan.doctor.appointment.Service.Implementation;

import com.gulshan.doctor.appointment.Entity.Patient;
import com.gulshan.doctor.appointment.Entity.User;
import com.gulshan.doctor.appointment.Entity.UserProfile;
import com.gulshan.doctor.appointment.Exceptions.ResourceNotFoundException;
import com.gulshan.doctor.appointment.Repository.PatientRepository;
import com.gulshan.doctor.appointment.Repository.UserProfileRepository;
import com.gulshan.doctor.appointment.Repository.UserRepository;
import com.gulshan.doctor.appointment.RequestDTO.PatientCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.PatientUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.PatientCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.PatientResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.PatientUpdateResponseDto;
import com.gulshan.doctor.appointment.Entity.Projections.PatientDetailsProjection;
import com.gulshan.doctor.appointment.Service.Interface.PatientServiceInterface;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientServiceInterface {


    private final UserProfileRepository userProfileRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Override
    public PatientCreateResponseDto addPatientDetails(PatientCreateRequestDto patientCreateRequestDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Patient patient = modelMapper.map(patientCreateRequestDto,Patient.class);
        patient.setUser(user);
        Patient savedpatient = patientRepository.save(patient);
        PatientCreateResponseDto response = modelMapper.map(savedpatient, PatientCreateResponseDto.class);
        return response;
    }

    @Override
    public PatientResponseDto getPatientByUserId(long userId) {
        PatientDetailsProjection patientDetailsProjection = patientRepository.findPatientDetailsByUserId(userId)
                .orElseThrow( () -> new ResourceNotFoundException("Patient details not found."));
        PatientResponseDto patientResponseDto = modelMapper.map(patientDetailsProjection, PatientResponseDto.class);
        return patientResponseDto;
    }

    public PatientUpdateResponseDto updatePateint() {
        return updatePateint(null);
    }

    @Override
    public PatientUpdateResponseDto updatePateint(PatientUpdateRequestDto patientUpdateRequestDto) {
        User user = userRepository.findById(patientUpdateRequestDto.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        UserProfile profile = userProfileRepository
                .findByUser_IdAndDeletedFalse(patientUpdateRequestDto.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User profile not found"));

        Patient patient = patientRepository
                .findByUser_Id(patientUpdateRequestDto.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient not found"));

        user.setName(patientUpdateRequestDto.getName());
        user.setEmail(patientUpdateRequestDto.getEmail());
        profile.setCity(patientUpdateRequestDto.getCity());
        profile.setMobNumber(patientUpdateRequestDto.getMobNumber());
        profile.setDateOfBirth(patientUpdateRequestDto.getDateOfBirth());
        profile.setGender(patientUpdateRequestDto.getGender());
        profile.setAddressLine(patientUpdateRequestDto.getAddressLine());
        profile.setPincode(patientUpdateRequestDto.getPincode());
        profile.setState(patientUpdateRequestDto.getState());
        profile.setCountry(patientUpdateRequestDto.getCountry());
        patient.setBloodGroup(patientUpdateRequestDto.getBloodGroup());
        patient.setEmergencyContactNumber(patientUpdateRequestDto.getEmergencyContactNumber());



        User updatedUser = userRepository.save(user);
        UserProfile updatedUserProfile = userProfileRepository.save(profile);
        Patient updatedPatient = patientRepository.save(patient);



        PatientUpdateResponseDto patientUpdateResponseDto = mapperMethod(updatedUser,updatedUserProfile,updatedPatient);
        return patientUpdateResponseDto;
    }

    @Override
    public List<PatientResponseDto> getAllPatients() {
        List<PatientDetailsProjection> patientDetailsProjections =  patientRepository.findAllPatientDetails();
        return patientDetailsProjections.stream().map(
                p -> modelMapper.map(p,PatientResponseDto.class))
                .toList();
    }

    private PatientUpdateResponseDto mapperMethod(User user,UserProfile userProfile, Patient patient){
        PatientUpdateResponseDto response = new PatientUpdateResponseDto();
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

        response.setBloodGroup(patient.getBloodGroup());
        response.setEmergencyContactNumber(patient.getEmergencyContactNumber());
        return response;
    }

}

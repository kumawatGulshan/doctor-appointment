package com.gulshan.doctor.appointment.Service.Implementation;


import com.gulshan.doctor.appointment.Entity.Specialization;
import com.gulshan.doctor.appointment.Exceptions.ResourceNotFoundException;
import com.gulshan.doctor.appointment.Repository.SpecializationRepository;
import com.gulshan.doctor.appointment.RequestDTO.SpecializationCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.SpecializationUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.SpecializationResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.SpecializationServiceInterface;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecializationServiceImpl implements SpecializationServiceInterface {

    private final SpecializationRepository specializationRepository;
    private final ModelMapper modelMapper;

    @Override
    public String addSpecialization(SpecializationCreateRequestDto specializationCreateRequestDto) {
        Specialization specialization = modelMapper.map(specializationCreateRequestDto, Specialization.class);
        Specialization response = specializationRepository.save(specialization);
        return response.getSpecializationName() + " added successfully.";
    }

    @Override
    public String updateSpecialization(SpecializationUpdateRequestDto specializationUpdateRequestDto) {
        Specialization specialization = modelMapper.map(specializationUpdateRequestDto,Specialization.class);
        Specialization response = specializationRepository.save(specialization);
        return response.getSpecializationName() + " updated successfully";
    }

    @Override
    public List<SpecializationResponseDto> getAllSpecialization() {
        List<Specialization> specializations = specializationRepository.findAll();
        List<SpecializationResponseDto> specializationsResult = specializations.stream()
                .map(spec -> modelMapper.map(spec,SpecializationResponseDto.class))
                .toList();
        return specializationsResult;
    }

    @Override
    public SpecializationResponseDto getSpecializationById(Long specializationId) {
        Specialization specialization = specializationRepository.findById(specializationId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not exist with id : "+ specializationId));
        return  modelMapper.map(specialization,SpecializationResponseDto.class);
    }


}

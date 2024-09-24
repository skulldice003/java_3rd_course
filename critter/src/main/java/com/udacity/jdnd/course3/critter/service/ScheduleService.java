package com.udacity.jdnd.course3.critter.service;


import com.udacity.jdnd.course3.critter.entity.EmployeeEntity;
import com.udacity.jdnd.course3.critter.entity.PetEntity;
import com.udacity.jdnd.course3.critter.entity.ScheduleEntity;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import com.udacity.jdnd.course3.critter.schedule.ScheduleDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    CustomerRepository customerRepository;

    public ScheduleDTO create(ScheduleDTO scheduleDTO) {
        ScheduleEntity scheduleEntity = new ScheduleEntity();

        List<EmployeeEntity> employeeEntities = convertEmployeeIdsToEmployeeEntities(scheduleDTO.getEmployeeIds());
        scheduleEntity.setEmployees(employeeEntities);

        List<PetEntity> petEntities = convertPetIdsToPetEntities(scheduleDTO.getPetIds());
        scheduleEntity.setPets(petEntities);

        scheduleEntity.setDate(scheduleDTO.getDate());
        scheduleEntity.setActivities(scheduleDTO.getActivities());

        for (EmployeeEntity employeeEntity : employeeEntities) {
            List<ScheduleEntity> oldScheduleList = employeeEntity.getSchedules() == null ? new ArrayList<>() : employeeEntity.getSchedules();
            oldScheduleList.add(scheduleEntity);
            employeeEntity.setSchedules(oldScheduleList);
        }
        employeeRepository.saveAll(employeeEntities);

        for (PetEntity petEntity : petEntities) {
            List<ScheduleEntity> oldScheduleList = petEntity.getSchedules() == null ? new ArrayList<>() : petEntity.getSchedules();
            oldScheduleList.add(scheduleEntity);
            petEntity.setSchedules(oldScheduleList);
        }
        petRepository.saveAll(petEntities);
        scheduleRepository.save(scheduleEntity);

        scheduleDTO.setId(scheduleEntity.getId());
        return scheduleDTO;

    }

    public List<ScheduleDTO> getAllSchedules() {
        List<ScheduleEntity> scheduleEntities = scheduleRepository.findAll();
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for (ScheduleEntity scheduleEntity : scheduleEntities) {
            ScheduleDTO scheduleDTO = new ScheduleDTO();
            scheduleDTO.setId(scheduleEntity.getId());

            List<Long> employeeIds = new ArrayList<>();
            for (EmployeeEntity employeeEntity : CollectionUtils.emptyIfNull(scheduleEntity.getEmployees())) {
                employeeIds.add(employeeEntity.getId());
            }
            scheduleDTO.setEmployeeIds(employeeIds);

            List<Long> petIds = new ArrayList<>();
            for (PetEntity petEntity : CollectionUtils.emptyIfNull(scheduleEntity.getPets())) {
                petIds.add(petEntity.getId());
            }
            scheduleDTO.setPetIds(petIds);

            scheduleDTO.setDate(scheduleEntity.getDate());
            scheduleDTO.setActivities(scheduleEntity.getActivities());
            scheduleDTOS.add(scheduleDTO);
        }
        return scheduleDTOS;
    }


    public List<ScheduleDTO> getScheduleForPet(long petId) {
        Optional<PetEntity> petEntityOptional = petRepository.findById(petId);
        if (petEntityOptional.isPresent()) {
            PetEntity petEntity = petEntityOptional.get();
            List<ScheduleEntity> scheduleEntities = petEntity.getSchedules();
            return convertScheduleEntitiesToScheduleDTO(scheduleEntities);
        }
        return null;
    }

    public List<ScheduleDTO> getScheduleForEmployee(long employeeId) {
        Optional<EmployeeEntity> employeeEntityOptional = employeeRepository.findById(employeeId);
        if (employeeEntityOptional.isPresent()) {
            EmployeeEntity employeeEntity = employeeEntityOptional.get();
            List<ScheduleEntity> scheduleEntities = employeeEntity.getSchedules();
            return convertScheduleEntitiesToScheduleDTO(scheduleEntities);
        }
        return null;
    }

    public List<ScheduleDTO> getScheduleForCustomer(long customerId) {
        // find customer entity by id
//        Optional<CustomerEntity> customerEntityOptional = customerRepository.findById(customerId);
////        if (customerEntityOptional.isPresent()) {
////            // find pet list of custoemr
////
////
////            CustomerEntity customerEntity = customerEntityOptional.get();
////            List<ScheduleEntity> scheduleEntities = customerEntity.getSchedules();
////            return convertScheduleEntitiesToScheduleDTO(scheduleEntities);
////        }
////        return null;
        // find all pets by customer id
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        List<PetEntity> petEntities = petRepository.findByCustomerId(customerId);
        for (PetEntity petEntity : CollectionUtils.emptyIfNull(petEntities)) {
            List<ScheduleDTO> list = getScheduleForPet(petEntity.getId());
            if (!list.isEmpty()) {
                scheduleDTOS.addAll(list);
            }
        }
        return scheduleDTOS;
    }

    private List<EmployeeEntity> convertEmployeeIdsToEmployeeEntities(List<Long> employeeIds) {
        List<EmployeeEntity> employeeEntities = new ArrayList<>();
        for (Long employeeId : employeeIds) {
            Optional<EmployeeEntity> employeeEntityOptional = employeeRepository.findById(employeeId);
            if (employeeEntityOptional.isPresent()) {
                employeeEntities.add(employeeEntityOptional.get());
            }
        }
        return employeeEntities;
    }

    private List<PetEntity> convertPetIdsToPetEntities(List<Long> petIds) {
        List<PetEntity> petEntities = new ArrayList<>();
        for (Long petId : petIds) {
            Optional<PetEntity> petEntityOptional = petRepository.findById(petId);
            if (petEntityOptional.isPresent()) {
                petEntities.add(petEntityOptional.get());
            }
        }
        return petEntities;
    }

    private List<ScheduleDTO> convertScheduleEntitiesToScheduleDTO(List<ScheduleEntity> scheduleEntities) {
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for (ScheduleEntity scheduleEntity : CollectionUtils.emptyIfNull(scheduleEntities)) {
            ScheduleDTO scheduleDTO = new ScheduleDTO();
            scheduleDTO.setId(scheduleEntity.getId());

            List<Long> employeeIds = new ArrayList<>();
            for (EmployeeEntity employeeEntity : CollectionUtils.emptyIfNull(scheduleEntity.getEmployees())) {
                employeeIds.add(employeeEntity.getId());
            }
            scheduleDTO.setEmployeeIds(employeeIds);

            List<Long> petIds = new ArrayList<>();
            for (PetEntity petEntity : CollectionUtils.emptyIfNull(scheduleEntity.getPets())) {
                petIds.add(petEntity.getId());
            }
            scheduleDTO.setPetIds(petIds);

            scheduleDTO.setDate(scheduleEntity.getDate());
            scheduleDTO.setActivities(scheduleEntity.getActivities());

            scheduleDTOS.add(scheduleDTO);
        }
        return scheduleDTOS;
    }


}

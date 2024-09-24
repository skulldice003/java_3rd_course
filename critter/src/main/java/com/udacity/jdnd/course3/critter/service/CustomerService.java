package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.CustomerEntity;
import com.udacity.jdnd.course3.critter.entity.PetEntity;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.user.CustomerDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PetRepository petRepository;

    public CustomerDTO create(CustomerDTO customerDTO) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setName(customerDTO.getName());
        customerEntity.setPhoneNumber(customerDTO.getPhoneNumber());
        customerEntity.setNotes(customerDTO.getNotes());
        List<PetEntity> petEntities = convertPetIdsToPetEntities(customerDTO.getPetIds());
        customerEntity.setPets(petEntities);
        petRepository.saveAll(petEntities);
        customerRepository.save(customerEntity);
        customerDTO.setId(customerEntity.getId());
        return customerDTO;
    }

    public List<CustomerDTO> getAllCustomers() {
        List<CustomerEntity>  list = customerRepository.findAll();
        List<CustomerDTO> customerDTOList = new ArrayList<>();

        for (CustomerEntity customerEntity : list) {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setId(customerEntity.getId());
            customerDTO.setName(customerEntity.getName());
            customerDTO.setPhoneNumber(customerEntity.getPhoneNumber());
            customerDTO.setNotes(customerEntity.getNotes());
            customerDTO.setPetIds(convertPetEntitiesToPetIds(customerEntity.getPets()));
            customerDTOList.add(customerDTO);
        }
        return customerDTOList;
    }

    private List<PetEntity> convertPetIdsToPetEntities(List<Long> petIds) {
        List<PetEntity> petEntities = new ArrayList<>();
        for (Long id : CollectionUtils.emptyIfNull(petIds)) {
            Optional<PetEntity> petEntityOptional = petRepository.findById(id);
            if (petEntityOptional.isPresent()) {
                petEntities.add(petEntityOptional.get());
            }
        }
        return petEntities;
    }

    private List<Long> convertPetEntitiesToPetIds(List<PetEntity> petEntities) {
        List<Long> petIds = new ArrayList<>();
        for (PetEntity petEntity : CollectionUtils.emptyIfNull(petEntities)) {
            if (petEntity.getId() != null) petIds.add(petEntity.getId());
        }
        return petIds;
    }
}

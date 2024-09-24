package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.EmployeeEntity;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.user.EmployeeDTO;
import com.udacity.jdnd.course3.critter.user.EmployeeRequestDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    public EmployeeDTO create(EmployeeDTO employeeDTO) {
        EmployeeEntity employeeEntity = new EmployeeEntity();
        employeeEntity.setName(employeeDTO.getName());
        employeeEntity.setSkills(employeeDTO.getSkills());
        employeeEntity.setDaysAvailable(employeeDTO.getDaysAvailable());
        employeeRepository.save(employeeEntity);
        employeeDTO.setId(employeeEntity.getId());
        return employeeDTO;
    }

    public EmployeeDTO getEmployeeById(Long employeeId) {
        Optional<EmployeeEntity> employeeEntityOptional = employeeRepository.findById(employeeId);
        if(employeeEntityOptional.isPresent()) {
            EmployeeEntity employeeEntity = employeeEntityOptional.get();
            EmployeeDTO employeeDTO = new EmployeeDTO();
            employeeDTO.setId(employeeEntity.getId());
            employeeDTO.setName(employeeEntity.getName());
            employeeDTO.setSkills(employeeEntity.getSkills());
            employeeDTO.setDaysAvailable(employeeEntity.getDaysAvailable());
            return employeeDTO;
        }
        else return null;
    }

    public void updateDaysAvailable(Set<DayOfWeek> daysAvailable, long employeeId) {
        Optional<EmployeeEntity> employeeEntityOptional = employeeRepository.findById(employeeId);
        if(employeeEntityOptional.isPresent()) {
            EmployeeEntity employeeEntity = employeeEntityOptional.get();
            employeeEntity.setDaysAvailable(daysAvailable);
            employeeRepository.save(employeeEntity);
        }
    }

    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeDTO) {
        List<EmployeeEntity> employeeEntities = employeeRepository
                .findByDaysAvailable(employeeDTO.getDate().getDayOfWeek()).stream()
                .filter(employee -> employee.getSkills().containsAll(employeeDTO.getSkills()))
                .collect(Collectors.toList());

        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        for (EmployeeEntity employeeEntity : CollectionUtils.emptyIfNull(employeeEntities)) {
            EmployeeDTO temp = new EmployeeDTO();
            temp.setId(employeeEntity.getId());
            temp.setName(employeeEntity.getName());
            temp.setSkills(employeeEntity.getSkills());
            temp.setDaysAvailable(employeeEntity.getDaysAvailable());
            employeeDTOList.add(temp);
        }

        return employeeDTOList;
    }
}

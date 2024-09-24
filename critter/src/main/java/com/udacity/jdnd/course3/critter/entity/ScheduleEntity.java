package com.udacity.jdnd.course3.critter.entity;

import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "SCHEDULE")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleEntity {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="schedule_employee", joinColumns = @JoinColumn(name="schedule_id"),
            inverseJoinColumns = @JoinColumn(name="employee_id"))
    private List<EmployeeEntity> employees;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="schedule_pet", joinColumns = @JoinColumn(name="schedule_id"),
            inverseJoinColumns = @JoinColumn(name="pet_id"))
    private List<PetEntity> pets;

    private LocalDate date;

    @ElementCollection
    private Set<EmployeeSkill> activities;

}

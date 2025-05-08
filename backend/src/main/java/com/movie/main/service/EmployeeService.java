package com.movie.main.service;

import org.springframework.stereotype.Service;

import com.movie.main.dto.request.EmployeeRequestDto;
import com.movie.main.dto.response.EmployeeResponseDto;
import com.movie.main.entity.Employee;
import com.movie.main.repository.EmployeeRepository;

import jakarta.validation.constraints.NotNull;

@Service
public class EmployeeService extends AbstractUserDetailsService<EmployeeRequestDto, EmployeeResponseDto, Employee> {
    @NotNull
    private final EmployeeRepository repository;

    @NotNull
    private UserService userService;

    public EmployeeService(@NotNull final EmployeeRepository repository, @NotNull final UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public EmployeeResponseDto createResponseDtoFromEntity(@NotNull final Employee employee) {
        return new EmployeeResponseDto(this.userService.createResponseDtoFromEntity(employee.getUser()),
                employee.getJobTitle(), employee.getSalary(), employee.getShiftStart(), employee.getShiftEnd(),
                employee.getBeginWorkingDate(), employee.getPermissions());
    }

    @Override
    protected Employee createEntityFromRequestDto(@NotNull final EmployeeRequestDto requestDto) {
        return new Employee(this.userService.createEntityFromRequestDto(requestDto.userRequestDto()),
                requestDto.jobTitle(), requestDto.salary(), requestDto.shiftStart(), requestDto.shiftEnd(),
                requestDto.beginWorkingDate(), requestDto.permissions());
    }

    @Override
    protected Employee updateEntityFromRequestDto(@NotNull final Employee employee,
            @NotNull final EmployeeRequestDto requestDto) {
        employee.setUser(this.userService.updateEntityFromRequestDto(employee.getUser(), requestDto.userRequestDto()));
        employee.setJobTitle(requestDto.jobTitle());
        employee.setSalary(requestDto.salary());
        employee.setShiftStart(requestDto.shiftStart());
        employee.setShiftEnd(requestDto.shiftEnd());
        employee.setBeginWorkingDate(requestDto.beginWorkingDate());

        return employee;
    }

    @Override
    public EmployeeRepository getRepository() {
        return this.repository;
    }
}

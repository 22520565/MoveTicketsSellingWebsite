package com.movie.main.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.movie.main.dto.request.EmployeeRequestDto;
import com.movie.main.dto.request.EmployeeSelfRequestDto;
import com.movie.main.entity.Employee;
import com.movie.main.repository.EmployeeRepository;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeService {
    public enum CreationError {
        USERNAME_EXISTS, UNSPECIFIED,
    }

    public enum UpdateError {
        ENTITY_NOT_EXISTS, USERNAME_EXISTS, UNSPECIFIED,
    }

    public enum SetPasswordResult {
        SUCCESS, ENTITY_NOT_EXISTS, UNSPECIFIED,
    }

    public enum ResetPasswordResult {
        SUCCESS, ENTITY_NOT_EXISTS, WRONG_OLD_PASSWORD, UNSPECIFIED,
    }

    public enum MarkBlockedStatusResult {
        SUCCESS, ENTITY_NOT_EXISTS_ERROR, UNSPECIFIED_ERROR,
    }

    public enum MarkDeletedStatusResult {
        SUCCESS, ENTITY_NOT_EXISTS_ERROR, UNSPECIFIED_ERROR,
    }

    @NotNull
    private final EmployeeRepository repository;

    @NotNull
    private final PasswordEncoder passwordEncoder;

    @NotNull
    private final UserRefreshTokenService userRefreshTokenService;

    public EmployeeService(@NotNull final EmployeeRepository repository, @NotNull final PasswordEncoder passwordEncoder,
            @NotNull final UserRefreshTokenService userRefreshTokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.userRefreshTokenService = userRefreshTokenService;
    }

    @Nullable
    public Page<Employee> findAllByBlockedFalseAndDeletedFalse(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByBlockedFalseAndDeletedFalse(pageRequest);
    }

    @Nullable
    public Page<Employee> findAllByBlockedTrueAndDeletedFalse(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByBlockedTrueAndDeletedFalse(pageRequest);
    }

    @Nullable
    public Page<Employee> findAllByDeletedTrue(@NotNull final PageRequest pageRequest) {
        return this.repository.findAllByDeletedTrue(pageRequest);
    }

    @Nullable
    public Employee findByIdAndBlockedFalseAndDeletedFalse(final int id) {
        return this.repository.findByIdAndBlockedFalseAndDeletedFalse(id).orElse(null);
    }

    @Nullable
    public Employee findByIdAndBlockedTrueAndDeletedFalse(final int id) {
        return this.repository.findByIdAndBlockedTrueAndDeletedFalse(id).orElse(null);
    }

    @Nullable
    public Employee findById(final int id) {
        return this.repository.findById(id).orElse(null);
    }

    @Nullable
    public Employee findByIdAndDeletedFalse(final int id) {
        return this.repository.findByIdAndDeletedFalse(id).orElse(null);
    }

    @Nullable
    public Employee findByIdAndDeletedTrue(final int id) {
        return this.repository.findByIdAndDeletedTrue(id).orElse(null);
    }

    @Nullable
    public Employee findByUsernameAndDeletedFalse(final String username) {
        return this.repository.findByUsernameAndDeletedFalse(username).orElse(null);
    }

    @Nullable
    public Employee findByUsername(final String username) {
        return this.repository.findByUsername(username).orElse(null);
    }

    public boolean existsByUsername(final String username) {
        return this.repository.existsByUsername(username);
    }

    @NotNull
    public Expected<Employee, CreationError> create(@NotNull final EmployeeRequestDto requestDto) {
        if (this.existsByUsername(requestDto.username())) {
            return Expected.failure(CreationError.USERNAME_EXISTS);
        }

        final var newEmployee = new Employee(requestDto.name(), requestDto.birthDate(), requestDto.email(),
                requestDto.phoneNumber(), requestDto.username(), this.passwordEncoder.encode(requestDto.password()),
                requestDto.jobTitle(), requestDto.salary(), requestDto.shiftStart(), requestDto.shiftEnd(),
                requestDto.beginWorkingDate(), requestDto.permissions());

        try {
            return Expected.success(this.repository.save(newEmployee));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(CreationError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<Employee, UpdateError> updateByIdAndDeletedFalse(final int id,
            @NotNull final EmployeeRequestDto requestDto) {
        final var employee = this.findByIdAndBlockedFalseAndDeletedFalse(id);
        if (employee == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var newUsername = requestDto.username();
        if ((!Objects.equals(employee.getUsername(), newUsername)) && (this.existsByUsername(newUsername))) {
            return Expected.failure(UpdateError.USERNAME_EXISTS);
        }

        employee.setName(requestDto.name());
        employee.setBirthDate(requestDto.birthDate());
        employee.setEmail(requestDto.email());
        employee.setPhoneNumber(requestDto.phoneNumber());
        employee.setUsername(requestDto.username());
        employee.setHashedPassword(this.passwordEncoder.encode(requestDto.password()));
        employee.setJobTitle(requestDto.jobTitle());
        employee.setSalary(requestDto.salary());
        employee.setShiftStart(requestDto.shiftStart());
        employee.setShiftEnd(requestDto.shiftEnd());
        employee.setBeginWorkingDate(requestDto.beginWorkingDate());
        employee.setPermissions(requestDto.permissions());

        try {
            return Expected.success(this.repository.save(employee));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public Expected<Employee, UpdateError> updateByIdAndDeletedFalse(final int id,
            @NotNull final EmployeeSelfRequestDto requestDto) {
        final var employee = this.findByIdAndBlockedFalseAndDeletedFalse(id);
        if (employee == null) {
            return Expected.failure(UpdateError.ENTITY_NOT_EXISTS);
        }

        final var newUsername = requestDto.username();
        if ((!Objects.equals(employee.getUsername(), newUsername)) && (this.existsByUsername(newUsername))) {
            return Expected.failure(UpdateError.USERNAME_EXISTS);
        }

        employee.setName(requestDto.name());
        employee.setBirthDate(requestDto.birthDate());
        employee.setEmail(requestDto.email());
        employee.setPhoneNumber(requestDto.phoneNumber());
        employee.setUsername(requestDto.username());
        employee.setJobTitle(requestDto.jobTitle());
        employee.setSalary(requestDto.salary());
        employee.setShiftStart(requestDto.shiftStart());
        employee.setShiftEnd(requestDto.shiftEnd());
        employee.setBeginWorkingDate(requestDto.beginWorkingDate());

        try {
            return Expected.success(this.repository.save(employee));
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return Expected.failure(UpdateError.UNSPECIFIED);
        }
    }

    @NotNull
    public MarkBlockedStatusResult markAsBlockedById(final int id) {
        return this.markBlockedStatusById(id, true);
    }

    @NotNull
    public MarkBlockedStatusResult markAsUnblockedById(final int id) {
        return this.markBlockedStatusById(id, false);
    }

    @NotNull
    public MarkBlockedStatusResult markBlockedStatusById(final int id, final boolean blockedStatusToMark) {
        final var employee = this.findByIdAndDeletedFalse(id);
        if (employee == null) {
            return MarkBlockedStatusResult.ENTITY_NOT_EXISTS_ERROR;
        }

        employee.setBlocked(blockedStatusToMark);

        try {
            this.repository.save(employee);
            return MarkBlockedStatusResult.SUCCESS;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return MarkBlockedStatusResult.UNSPECIFIED_ERROR;
        }
    }

    @NotNull
    public SetPasswordResult setPasswordByIdAndDeletedFalse(final int id, final String newPassword) {
        final var employee = this.findByIdAndBlockedFalseAndDeletedFalse(id);
        if (employee == null) {
            return SetPasswordResult.ENTITY_NOT_EXISTS;
        }

        employee.setHashedPassword(this.passwordEncoder.encode(newPassword));

        try {
            this.repository.save(employee);
            return SetPasswordResult.SUCCESS;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return SetPasswordResult.UNSPECIFIED;
        }
    }

    @NotNull
    public ResetPasswordResult resetPasswordAndDeletedFalse(final int id, final String oldPassword,
            final String newPassword) {
        var employee = this.findByIdAndBlockedFalseAndDeletedFalse(id);
        if (employee == null)
            return ResetPasswordResult.ENTITY_NOT_EXISTS;

        if (!this.passwordEncoder.matches(oldPassword, employee.getHashedPassword())) {
            return ResetPasswordResult.WRONG_OLD_PASSWORD;
        }

        employee.setHashedPassword(this.passwordEncoder.encode(newPassword));

        try {
            this.repository.save(employee);
            return ResetPasswordResult.SUCCESS;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return ResetPasswordResult.UNSPECIFIED;
        }
    }

    @NotNull
    public MarkDeletedStatusResult markAsDeletedById(final int id) {
        return this.markDeletedStatusById(id, true);
    }

    @NotNull
    public MarkDeletedStatusResult markAsUndeletedById(final int id) {
        return this.markDeletedStatusById(id, false);
    }

    @NotNull
    public MarkDeletedStatusResult markDeletedStatusById(final int id, final boolean deletedStatusToMark) {
        final var employee = this.findById(id);
        if (employee == null) {
            return MarkDeletedStatusResult.ENTITY_NOT_EXISTS_ERROR;
        }

        employee.setDeleted(deletedStatusToMark);

        try {
            this.repository.save(employee);
            return MarkDeletedStatusResult.SUCCESS;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return MarkDeletedStatusResult.UNSPECIFIED_ERROR;
        }
    }
}

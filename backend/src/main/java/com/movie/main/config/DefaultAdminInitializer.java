package com.movie.main.config;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.movie.main.entity.Employee;
import com.movie.main.entity.Employee.Permission;
import com.movie.main.repository.EmployeeRepository;
import com.movie.main.resource.ResourceStrings;

@Component
public class DefaultAdminInitializer implements ApplicationRunner {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultAdminInitializer(final EmployeeRepository employeeRepository, final PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(final ApplicationArguments args) {
        final var adminExists = this.employeeRepository.existsAnyAdmin();

        if (adminExists) {
            return;
        }

        final var employee = this.employeeRepository
                .findByUsername(ResourceStrings.DEFAULT_ADMIN_USERNAME)
                .orElse(null);
        if (employee != null) {
            employee.setHashedPassword(this.passwordEncoder.encode(ResourceStrings.DEFAULT_ADMIN_PASSWORD));
            employee.setPermissions(EnumSet.of(Permission.NORMAL, Permission.ADMIN));
            employeeRepository.save(employee);
            return;
        }

        final var defaultAdmin = new Employee(
                "Admin",
                LocalDate.now(),
                "admin@admin.com",
                "0912345678",
                ResourceStrings.DEFAULT_ADMIN_USERNAME,
                this.passwordEncoder.encode(ResourceStrings.DEFAULT_ADMIN_PASSWORD),
                "Admin",
                0,
                LocalTime.MIN,
                LocalTime.MAX,
                LocalDate.now(),
                EnumSet.of(Permission.NORMAL, Permission.ADMIN));

        employeeRepository.save(defaultAdmin);
    }
}

package com.accenture.sast.tracker.service;

import com.accenture.sast.tracker.model.Employee;
import com.accenture.sast.tracker.repository.EmployeeRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;
    private final JdbcTemplate jdbc;

    public EmployeeService(EmployeeRepository repository, JdbcTemplate jdbc) {
        this.repository = repository;
        this.jdbc = jdbc;
    }

    public Employee save(Employee employee) {
        return repository.save(employee);
    }

    public List<Employee> findAll() {
        return repository.findAll();
    }

    public Optional<Employee> findById(Long id) {
        return repository.findById(id);
    }

    public List<Employee> search(String name) {
        String sql = "SELECT id, name, role, email, onboarding_date " +
                     "FROM employee WHERE LOWER(name) LIKE LOWER(?)";

        return jdbc.query(sql,
                ps -> ps.setString(1, "%" + name + "%"),
                (rs, row) -> {
                    Employee e = new Employee();
                    e.setName(rs.getString("name"));
                    e.setRole(rs.getString("role"));
                    e.setEmail(rs.getString("email"));
                    return e;
                });
    }
}

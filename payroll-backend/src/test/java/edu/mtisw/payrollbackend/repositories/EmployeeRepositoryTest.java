package edu.mtisw.payrollbackend.repositories;

import edu.mtisw.payrollbackend.entities.EmployeeEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void whenFindByRut_thenReturnEmployee() {
        // given: es lo que le doy
        EmployeeEntity employee = new EmployeeEntity(null, "12345678-9", "John Doe", 50000, 2, "A");
        entityManager.persistAndFlush(employee);

        // when: que haga lo que le estoy dando
        EmployeeEntity found = employeeRepository.findByRut(employee.getRut());

        // then: despues de procesar las cosas
        assertThat(found.getRut()).isEqualTo(employee.getRut());
    }

    @Test
    public void whenFindByCategory_thenReturnEmployees() {
        // given
        EmployeeEntity employee1 = new EmployeeEntity(null, "12345678-9", "John Doe", 50000, 2, "A");
        EmployeeEntity employee2 = new EmployeeEntity(null, "98765432-1", "Jane Doe", 60000, 1, "A");
        entityManager.persist(employee1);
        entityManager.persist(employee2);
        entityManager.flush();

        // when
        List<EmployeeEntity> foundEmployees = employeeRepository.findByCategory("A");

        // then
        assertThat(foundEmployees).hasSize(2).extracting(EmployeeEntity::getCategory).containsOnly("A");
    }

    @Test
    public void whenFindBySalaryGreaterThan_thenReturnEmployees() {
        // given
        EmployeeEntity lowSalaryEmployee = new EmployeeEntity(null, "12345678-9", "John Doe", 30000, 2, "B");
        EmployeeEntity highSalaryEmployee = new EmployeeEntity(null, "98765432-1", "Jane Doe", 60000, 1, "A");
        entityManager.persist(lowSalaryEmployee);
        entityManager.persist(highSalaryEmployee);
        entityManager.flush();

        // when
        List<EmployeeEntity> foundEmployees = employeeRepository.findBySalaryGreaterThan(50000);

        // then
        assertThat(foundEmployees).hasSize(1).extracting(EmployeeEntity::getName).containsOnly("Jane Doe");
    }

    @Test
    public void whenFindByChildrenBetween_thenReturnEmployees() {
        // given
        EmployeeEntity employee1 = new EmployeeEntity(null, "12345678-9", "John Doe", 50000, 0, "A");
        EmployeeEntity employee2 = new EmployeeEntity(null, "98765432-1", "Jane Doe", 60000, 2, "B");
        entityManager.persist(employee1);
        entityManager.persist(employee2);
        entityManager.flush();

        // when
        List<EmployeeEntity> foundEmployees = employeeRepository.findByChildrenBetween(1, 3);

        // then
        assertThat(foundEmployees).hasSize(1).extracting(EmployeeEntity::getName).containsOnly("Jane Doe");
    }

    @Test
    public void whenFindByRutNativeQuery_thenReturnEmployee() {
        // given
        EmployeeEntity employee = new EmployeeEntity(null, "12345678-9", "John Doe", 50000, 2, "A");
        entityManager.persistAndFlush(employee);

        // when
        EmployeeEntity found = employeeRepository.findByRutNativeQuery(employee.getRut());

        // then
        assertThat(found.getRut()).isEqualTo(employee.getRut());
    }
}
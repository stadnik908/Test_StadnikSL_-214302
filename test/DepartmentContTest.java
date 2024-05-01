package com.certification.test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DepartmentContTest {

    @Mock
    private DepartmentRepo departmentRepo;

    @Mock
    private Model model;

    private DepartmentCont departmentCont;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        departmentCont = new DepartmentCont();
        departmentCont.setDepartmentRepo(departmentRepo);
    }

    @Test
    public void testDepartments() {
        List<Department> departments = new ArrayList<>();
        departments.add(new Department(1L, "Department 1"));
        departments.add(new Department(2L, "Department 2"));
        when(departmentRepo.findAll()).thenReturn(departments);

        String viewName = departmentCont.departments(model);

        verify(departmentRepo, times(1)).findAll();
        verify(model, times(1)).addAttribute("departments", departments);

        assertEquals("departments", viewName);
    }

    @Test
    public void testAddDepartment() {
        String departmentName = "New Department";

        String redirectUrl = departmentCont.addDepartment(departmentName);

        verify(departmentRepo, times(1)).save(any(Department.class));

        assertEquals("redirect:/departments", redirectUrl);
    }

    @Test
    public void testEditDepartment() {
        Long departmentId = 1L;
        String newDepartmentName = "Updated Department";

        Department department = new Department(departmentId, "Department 1");
        when(departmentRepo.getReferenceById(departmentId)).thenReturn(department);

        String redirectUrl = departmentCont.editDepartment(departmentId, newDepartmentName);

        verify(departmentRepo, times(1)).getReferenceById(departmentId);
        verify(departmentRepo, times(1)).save(department);

        assertEquals("redirect:/departments", redirectUrl);
        assertEquals(newDepartmentName, department.getName());
    }

    @Test
    public void testDeleteDepartment() {
        Long departmentId = 1L;

        Department department = new Department(departmentId, "Department 1");
        when(departmentRepo.getReferenceById(departmentId)).thenReturn(department);

        String redirectUrl = departmentCont.deleteDepartment(departmentId);

        verify(departmentRepo, times(1)).getReferenceById(departmentId);
        verify(departmentRepo, times(1)).deleteById(departmentId);

        assertEquals("redirect:/departments", redirectUrl);
        assertEquals(null, department.getUsers());
    }
}
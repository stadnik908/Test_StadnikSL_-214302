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
@RunWith(MockitoJUnitRunner.class)
public class CertificationControllerTest {

    @Mock
    private DepartmentRepo departmentRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private CertificationController certificationController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(certificationController).build();
    }

    @Test
    public void testCertification() throws Exception {
        Role role = Role.USER;
        Department department = new Department(1L, "Department 1");

        List<Department> departments = new ArrayList<>();
        departments.add(department);

        List<AppUser> users = new ArrayList<>();
        users.add(new AppUser("user1", role, department, true));
        users.add(new AppUser("user2", role, department, false));

        when(departmentRepo.findAll()).thenReturn(departments);
        when(userRepo.findAllByRole(Role.USER)).thenReturn(users);

        mockMvc.perform(get("/certification"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("countTrue"))
                .andExpect(model().attributeExists("countFalse"))
                .andExpect(model().attributeExists("departmentString"))
                .andExpect(model().attributeExists("departmentFloat"))
                .andExpect(view().name("certification"));

        verify(departmentRepo, times(1)).findAll();
        verify(userRepo, times(1)).findAllByRole(Role.USER);
    }
}

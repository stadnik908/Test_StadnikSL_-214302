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
public class TestContTest {

    @Mock
    private TestRepo testRepo;

    @Mock
    private DepartmentRepo departmentRepo;

    @Mock
    private PassingRepo passingRepo;

    @InjectMocks
    private TestCont testCont;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(testCont).build();
    }

    @Test
    public void testTestsForUserRoleWithDepartment() throws Exception {
        Role role = Role.USER;
        Department department = new Department(1L, "Department 1");
        AppUser user = new AppUser("user", role, department);

        List<Test> tests = new ArrayList<>();
        tests.add(new Test("Test 1", department));

        when(testRepo.findAllByDepartment_Id(department.getId())).thenReturn(tests);

        mockMvc.perform(get("/tests"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("tests"))
                .andExpect(model().attribute("tests", tests))
                .andExpect(view().name("tests"));

        verify(testRepo, times(1)).findAllByDepartment_Id(department.getId());
    }
    @Test
    public void testSearchTests() throws Exception {
        String testName = "Test 1";
        Long departmentId = 1L;
        Department department = new Department(departmentId, "Department 1");

        List<Test> tests = new ArrayList<>();
        tests.add(new Test("Test 1", department));

        when(testRepo.findAllByNameContainingAndDepartment_Id(testName, departmentId)).thenReturn(tests);
        when(departmentRepo.findAll()).thenReturn(Collections.singletonList(department));
        when(departmentRepo.getReferenceById(departmentId)).thenReturn(department);

        mockMvc.perform(get("/tests/search")
                        .param("name", testName)
                        .param("departmentId", departmentId.toString()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("tests"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attribute("tests", tests))
                .andExpect(model().attribute("name", testName))
                .andExpect(model().attribute("department", department))
                .andExpect(view().name("tests"));

        verify(testRepo, times(1)).findAllByNameContainingAndDepartment_Id(testName, departmentId);
        verify(departmentRepo, times(1)).findAll();
        verify(departmentRepo, times(1)).getReferenceById(departmentId);
    }

    @Test
    public void testAddTest() throws Exception {
        String testName = "New Test";
        Long departmentId = 1L;
        Department department = new Department(departmentId, "Department 1");
        Test test = new Test(testName, department);

        when(testRepo.save(any(Test.class))).thenReturn(test);

        mockMvc.perform(post("/tests/add")
                        .param("name", testName)
                        .param("departmentId", departmentId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/tests/*"));

        verify(testRepo, times(1)).save(any(Test.class));
    }

    @Test
    public void testDeleteTest() throws Exception {
        Long testId = 1L;

        mockMvc.perform(get("/tests/{testId}/delete", testId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tests"));

        verify(testRepo, times(1)).deleteById(testId);
    }


    @Test
    public void testEditTest() throws Exception {
        Long testId = 1L;
        Test test = new Test("Test 1");
        test.setStatus(true);

        when(testRepo.getReferenceById(testId)).thenReturn(test);
        when(passingRepo.deleteAllByIdInBatch(anyList())).thenReturn(null);
        when(testRepo.save(any(Test.class))).thenReturn(test);

        mockMvc.perform(get("/tests/{testId}/status", testId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/tests/*"));

        verify(testRepo, times(1)).getReferenceById(testId);
        verify(passingRepo, times(1)).deleteAllByIdInBatch(anyList());
        verify(testRepo, times(1)).save(any(Test.class));
    }


}
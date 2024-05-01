package com.certification.test;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserContTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private DepartmentRepo departmentRepo;

    @InjectMocks
    private UserCont userCont;

    @Test
    public void testUsers() {
        Model model = mock(Model.class);
        List<AppUser> users = new ArrayList<>();
        users.add(new AppUser());
        users.add(new AppUser());
        when(userRepo.findAll()).thenReturn(users);

        String result = userCont.users(model);

        verify(userRepo).findAll();
        verify(model).addAttribute("users", users);
        verify(model).addAttribute("roles", Role.values());
        verify(model).addAttribute("departments", departmentRepo.findAll());
        assertEquals("users", result);
    }

    @Test
    public void testSearch() {
        Model model = mock(Model.class);
        String name = "John";
        List<AppUser> users = new ArrayList<>();
        users.add(new AppUser());
        users.add(new AppUser());
        when(userRepo.findAllByFioContaining(name)).thenReturn(users);

        String result = userCont.search(model, name);

        verify(userRepo).findAllByFioContaining(name);
        verify(model).addAttribute("name", name);
        verify(model).addAttribute("users", users);
        verify(model).addAttribute("roles", Role.values());
        verify(model).addAttribute("departments", departmentRepo.findAll());
        assertEquals("users", result);
    }

    @Test
    public void testUser() {
        Model model = mock(Model.class);
        Long id = 1L;
        AppUser user = new AppUser();
        user.setPassings(new ArrayList<>());
        when(userRepo.getReferenceById(id)).thenReturn(user);

        String result = userCont.user(model, id);

        verify(userRepo).getReferenceById(id);
        verify(model).addAttribute("profile", user);
        verify(model).addAttribute("countTrue", 0);
        verify(model).addAttribute("countFalse", 0);
        assertEquals("user", result);
    }

    @Test
    public void testEditUser() {
        Long id = 1L;
        Role role = Role.ADMIN;
        Long departmentId = 2L;
        AppUser user = new AppUser();
        when(userRepo.getReferenceById(id)).thenReturn(user);
        when(departmentRepo.getReferenceById(departmentId)).thenReturn(new Department());

        String result = userCont.editUser(id, role, departmentId);

        verify(userRepo).getReferenceById(id);
        verify(departmentRepo).getReferenceById(departmentId);
        verify(userRepo).save(user);
        assertEquals("redirect:/users", result);
    }

    @Test
    public void testDeleteUser() {
        Long id = 1L;

        String result = userCont.deleteUser(id);

        verify(userRepo).deleteById(id);
        assertEquals("redirect:/users", result);
    }

    @Test
    public void testCertificationUser() {
        Long id = 1L;
        AppUser user = new AppUser();
        when(userRepo.getReferenceById(id)).thenReturn(user);

        String result = userCont.certificationUser(id);

        verify(userRepo).getReferenceById(id);
        verify(userRepo).save(user);
        assertEquals("redirect:/certification", result);
    }

    @Test
    public void testCertificationPdf() {
        Model model = mock(Model.class);
        List<AppUser> users = new ArrayList<>();
        users.add(new AppUser());
        users.add(new AppUser());
        when(userRepo.findAllByRole(Role.USER)).thenReturn(users);

        String result = userCont.certificationPdf(model);

        verify(userRepo).findAllByRole(Role.USER);
        verify(model).addAttribute("users", users);
        assertEquals("certificationPdf", result);
    }

    @Test
    public void testProfileFio() throws IOException {
        Model model = mock(Model.class);
        MultipartFile photo = new MockMultipartFile("photo", "test.jpg", "image/jpeg", "test image".getBytes());

        String uploadImg = "path/to/upload/directory";
        String expectedPhotoPath = "user/uuid_test.jpg";
        AppUser user = new AppUser();
        when(userRepo.getUser()).thenReturn(user);

        String result = userCont.profileFio(model, photo);

        verify(userRepo).getUser();
        verify(userRepo).save(user);
        assertEquals("redirect:/profile", result);
        assertEquals(expectedPhotoPath, user.getPhoto());

        // Verify file transfer
        File expectedFile = new File(uploadImg + "/" + expectedPhotoPath);
        verify(photo).transferTo(expectedFile);
    }

    @Test
    public void testProfile() {
        Model model = mock(Model.class);
        AppUser user = new AppUser();
        List<Passing> passings = new ArrayList<>();
        passings.add(new Passing(true));
        passings.add(new Passing(false));
        user.setPassings(passings);
        when(userRepo.getUser()).thenReturn(user);

        String result = userCont.profile(model);

        verify(userRepo).getUser();
        assertEquals("profile", result);
        assertEquals(1, model.getAttribute("countTrue"));
        assertEquals(1, model.getAttribute("countFalse"));
    }

    @Test
    public void testPassing() {
        Model model = mock(Model.class);
        Long passingId = 1L;
        Passing passing = new Passing();
        passing.setStatus(false);
        when(passingRepo.getReferenceById(passingId)).thenReturn(passing);

        String result = userCont.passing(model, passingId);

        verify(passingRepo).getReferenceById(passingId);
        verify(model).addAttribute("passing", passing);
        assertEquals("passing", result);
    }

    @Test
    public void testArticle() {
        Model model = mock(Model.class);
        Long id = 1L;
        Article article = new Article();
        when(articleRepo.getReferenceById(id)).thenReturn(article);

        String result = userCont.article(model, id);

        verify(articleRepo).getReferenceById(id);
        verify(model).addAttribute("article", article);
        assertEquals("article", result);
    }
}
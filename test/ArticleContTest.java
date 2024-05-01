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
public class ArticleContTest {

    @Mock
    private ArticleRepo articleRepo;

    @Mock
    private Model model;

    @InjectMocks
    private ArticleCont articleCont;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(articleCont).build();
    }

    @Test
    public void testArticles() throws Exception {
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("Article 1", "Author 1", "Theme 1", "", "", "Description 1"));
        articles.add(new Article("Article 2", "Author 2", "Theme 2", "", "", "Description 2"));

        when(articleRepo.findAll()).thenReturn(articles);

        mockMvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("articles", articles))
                .andExpect(view().name("articles"));

        verify(articleRepo, times(1)).findAll();
    }

    @Test
    public void testSearchArticles() throws Exception {
        String searchName = "Article 1";
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("Article 1", "Author 1", "Theme 1", "", "", "Description 1"));

        when(articleRepo.findAllByNameContaining(searchName)).thenReturn(articles);

        mockMvc.perform(get("/articles/search").param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("articles", articles))
                .andExpect(model().attribute("name", searchName))
                .andExpect(view().name("articles"));

        verify(articleRepo, times(1)).findAllByNameContaining(searchName);
    }

    @Test
    public void testArticle() throws Exception {
        Long articleId = 1L;
        Article article = new Article("Article 1", "Author 1", "Theme 1", "", "", "Description 1");

        when(articleRepo.getReferenceById(articleId)).thenReturn(article);

        mockMvc.perform(get("/articles/{id}", articleId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("article", article))
                .andExpect(view().name("article"));

        verify(articleRepo, times(1)).getReferenceById(articleId);
    }

    @Test
    public void testDeleteArticle() throws Exception {
        Long articleId = 1L;

        mockMvc.perform(get("/articles/{id}/delete", articleId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles"));

        verify(articleRepo, times(1)).deleteById(articleId);
    }

    @Test
    public void testAddArticle() throws Exception {
        String articleName = "New Article";
        String author = "Author";
        String theme = "Theme";
        String description = "Description";

        MockMultipartFile photoFile = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", "photo content".getBytes());
        MockMultipartFile file = new MockMultipartFile("file", "file.pdf", "application/pdf", "file content".getBytes());

        mockMvc.perform(multipart("/articles/add")
                        .file(photoFile)
                        .file(file)
                        .param("name", articleName)
                        .param("author", author)
                        .param("theme", theme)
                        .param("description", description))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/articles/*"));

        ArgumentCaptor<Article> articleCaptor = ArgumentCaptor.forClass(Article.class);
        verify(articleRepo, times(1)).save(articleCaptor.capture());
        Article savedArticle = articleCaptor.getValue();

        assertEquals(articleName, savedArticle.getName());
        assertEquals(author, savedArticle.getAuthor());
        assertEquals(theme, savedArticle.getTheme());
        assertEquals(description, savedArticle.getDescription());
        // Проверка сохранения фото и файла в соответствующих полях статьи
        assertNotNull(savedArticle.getPhoto());
        assertNotNull(savedArticle.getFile());
    }

    @Test
    public void testEditArticle() throws Exception {
        Long articleId = 1L;
        String articleName = "Updated Article";
        String author = "Author";
        String theme = "Theme";
        String description = "Description";

        MockMultipartFile photoFile = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", "photo content".getBytes());
        MockMultipartFile file = new MockMultipartFile("file", "file.pdf", "application/pdf", "file content".getBytes());

        Article existingArticle = new Article("Article 1", "Андрей", "Тема 1", "", "", "Описание 1");
        existingArticle.setId(articleId);

        when(articleRepo.findById(articleId)).thenReturn(Optional.of(existingArticle));

        mockMvc.perform(multipart("/articles/{id}/edit", articleId)
                        .file(photoFile)
                        .file(file)
                        .param("name", articleName)
                        .param("author", author)
                        .param("theme", theme)
                        .param("description", description))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/articles/" + articleId));

        ArgumentCaptor<Article> articleCaptor = ArgumentCaptor.forClass(Article.class);
        verify(articleRepo, times(1)).save(articleCaptor.capture());
        Article updatedArticle = articleCaptor.getValue();

        assertEquals(articleId, updatedArticle.getId());
        assertEquals(articleName, updatedArticle.getName());
        assertEquals(author, updatedArticle.getAuthor());
        assertEquals(theme, updatedArticle.getTheme());
        assertEquals(description, updatedArticle.getDescription());
        // Проверка обновления фото и файла в соответствующих полях статьи
        assertNotNull(updatedArticle.getPhoto());
        assertNotNull(updatedArticle.getFile());
    }
}

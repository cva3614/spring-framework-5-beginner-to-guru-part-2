package com.udemy.springmvcdemo.controllers;

import com.udemy.springmvcdemo.commands.IngredientCommand;
import com.udemy.springmvcdemo.commands.RecipeCommand;
import com.udemy.springmvcdemo.services.IngredientService;
import com.udemy.springmvcdemo.services.RecipeService;
import com.udemy.springmvcdemo.services.UnitOfMeasureService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class IngredientControllerTest {

    @Mock
    private RecipeService recipeService;

    private IngredientController controller;

    private MockMvc mockMvc;

    @Mock
    private IngredientService ingredientService;

    @Mock
    private UnitOfMeasureService unitOfMeasureService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        controller = new IngredientController(recipeService, ingredientService, unitOfMeasureService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testListIngredients() throws Exception {
        RecipeCommand recipeCommand = new RecipeCommand();
        when(recipeService.findCommandById(anyLong())).thenReturn(recipeCommand);
        mockMvc.perform(get("/recipe/1/ingredients"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredients"))
                .andExpect(model().attributeExists("recipe"));
        verify(recipeService, times(1)).findCommandById(anyLong());
    }

    @Test
    public void testShowIngredient() throws Exception {
        IngredientCommand ingredientCommand = new IngredientCommand();
        when(ingredientService.findByRecipeIdAndIngredientId(anyLong(), anyLong())).thenReturn(ingredientCommand);
        mockMvc.perform(get("/recipe/1/ingredients/2/show"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient"))
                .andExpect(model().attributeExists("ingredient"));
    }

    @Test
    public void testUpdateIngredientForm() throws Exception {
        IngredientCommand ingredientCommand = new IngredientCommand();
        when(ingredientService.findByRecipeIdAndIngredientId(anyLong(), anyLong())).thenReturn(ingredientCommand);
        when(unitOfMeasureService.listAllUnits()).thenReturn(new HashSet<>());
        mockMvc.perform(get("/recipe/1/ingredient/2/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient_form"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attributeExists("uomList"));
    }

    @Test
    public void testSaveOrUpdate() throws Exception {
        IngredientCommand command = new IngredientCommand();
        command.setId(3L);
        command.setRecipeId(2L);
        when(ingredientService.saveIngredientCommand(any())).thenReturn(command);
        mockMvc.perform(post("/recipe/2/ingredient")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "")
                .param("description", "some string")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/2/ingredient/3/show"));

    }

    @Test
    public void testNewIngredientForm() throws Exception {
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(1L);
        when(recipeService.findCommandById(anyLong())).thenReturn(recipeCommand);
        when(unitOfMeasureService.listAllUnits()).thenReturn(new HashSet<>());
        mockMvc.perform(get("/recipe/1/ingredient/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient_form"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attributeExists("uomList"));
        verify(recipeService, times(1)).findCommandById(anyLong());
    }
}

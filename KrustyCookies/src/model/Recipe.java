package model;

import java.util.ArrayList;

public class Recipe {
	
	public ArrayList<Ingredient> ingredients;
	public String recipeName;
	
	public Recipe(String recipeName, ArrayList<Ingredient> ingredients) {
		this.recipeName = recipeName;
		this.ingredients = ingredients;
	}

}

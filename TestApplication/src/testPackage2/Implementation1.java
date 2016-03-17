package testPackage2;

import java.util.HashMap;
import java.util.Set;

public class Implementation1 implements TestInterface {

	private HashMap<TestEnum, String> menu = new HashMap<TestEnum, String>();
	private HashMap<TestEnum, String> times = new HashMap<TestEnum, String>();
	
	public Implementation1(){
		populateMenuAndTimes();
	}
	
	@Override
	public void getFullMenu() {
		//No spacing in some lines
		Set<TestEnum> keys = menu.keySet();
		for(TestEnum x : keys){
			System.out.println(menu.get(x));
		}
	}

	@Override
	public void getTimeMenu(TestEnum meal) {
		//No block if and else statements
		if(menu.get(meal)!= null)	
			System.out.println(menu.get(meal));
		else
			System.out.println("No items for this meal");
	}

	@Override
	public void getMenuHours(TestEnum meal) {
		if(menu.get(times)!= null)	
			System.out.println(times.get(meal));
		else
			System.out.println("No items for this meal");
	}

	@Override
	public void populateMenuAndTimes() {
		//Populating menu
		this.menu.put(TestEnum.BREAKFAST, "Bacon And Eggs");
		this.menu.put(TestEnum.BRUNCH, "French Toast");
		this.menu.put(TestEnum.DESSERT, "Icecream");
		this.menu.put(TestEnum.DINNER, "Steak");
		
		//Populating times
		this.times.put(TestEnum.BREAKFAST, "7-10");
		this.times.put(TestEnum.BRUNCH, "10-11.30");
		this.times.put(TestEnum.DESSERT, "ALL DAY");
		this.times.put(TestEnum.DINNER, "7-10");

		
		
	}

}

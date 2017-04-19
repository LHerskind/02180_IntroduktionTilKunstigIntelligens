package feb21;

import java.util.Random;

public class BackwardReasoning {

	static String andOperator = "&";
	static String implicationOperator = "->";
	static String splitter = ",";
	private static Random random = new Random();

	public static void main(String[] args) {

		String input = "Food, Drink, Juice";

		String KB = "Food -> Egg & Bacon,Food -> Egg & Bread, Drink -> Coffee, Drink -> The";

		String sample = "Coffee, Juice, Bread, Bacon, Egg";
		
		input = input.replaceAll("\\s", "");
		KB = KB.replaceAll("\\s", "");
		sample = sample.replaceAll("\\s", "");		
		
		System.out.println("Can i make breakfeast with my ingredients");
		System.out.println((solve(input, KB, sample) ? "yes" : "no"));
	}

	public static boolean solve(String input, String KB, String sample) {		
		
		String[] inputArray = input.split(splitter);
		
		// Remove the once we have directly in sample
		String nextInputArrayString = "";
		for(int i = 0 ; i < inputArray.length; i++){
			if(!sample.contains(inputArray[i])){
				nextInputArrayString += inputArray[i] + splitter;
			}
		}
		
		if (nextInputArrayString.isEmpty()) {
			return true;
		}
		
		inputArray = nextInputArrayString.split(splitter);
		
		int index = random.nextInt(inputArray.length);

		String[] rulesArray = KB.split(splitter);
		for (int i = 0; i < rulesArray.length; i++) {
			if (rulesArray[i].contains(inputArray[index] + implicationOperator)) {

				String nextInput = "";
				for (int j = 0; j < inputArray.length; j++) {
					if (j != index) {
						nextInput += inputArray[j] + splitter;
					}
				}

				String rulesString = rulesArray[i].split(implicationOperator)[1]; // Getting after the implicationOperator
				String[] toInput = rulesString.split(andOperator);
				for (int j = 0; j < toInput.length; j++) {
					nextInput += toInput[j] + splitter;
				}
			
				if (solve(nextInput, KB, sample)) {
					return true;
				}
			}
		}
		return false;
	}

}

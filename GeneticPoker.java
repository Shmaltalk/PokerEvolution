/**
	Genetic Poker algorithm. Randomly generates 100 hands of poker, then develops
	better hands over time using crossover, and mutation of "parent" hands to 
	produce more fit "children."

	Talie Massachi
	2/12/2016 
**/

import java.util.*;

public class GeneticPoker{
	public static void main(String[] args){
		loop();
	}
	

	/*
		Runs the genetic algorithm, either until average fitness > 150,
		or until 100 turns have passed.
		Input: none
		Output: genetic algorithm runs
	*/
	public static void loop(){
		//creates array to hold "parent" and "child" hands
		PokerHand[][] hands = new PokerHand[2][100];
		
		//populates array with randomized hands of fitness less than or equal to a pair
		for (int i=0; i<hands[0].length; i++) {
			do{
				hands[0][i] = new PokerHand();
			}while(hands[0][i].getFitness()>2);
		}

		int turns = 1;

		//actual looping run
		//stops when average fitness > 270 (average is slightly above a straight flush)
		//or stops when there have been 100 runs.
		do{
			System.out.println("GENERATION " + turns);
			System.out.println("Average Fitness: " + getAverageFitness(hands));
			System.out.print("Best Hand: ");
			printHand(hands[0][maxHand(hands)].getHand());

			//sets 80% of "child" hands to be a crossover between two parents
			for (int i = 0; i<hands[0].length*.8; i++) {
				hands[1][i] = new PokerHand(crossover(findHand(hands), findHand(hands)));
			}
			//sets 10% of child hands to be a mutation of a parent
			for (int i=(int) (hands[0].length*.8); i<hands[0].length*.9; i++) {
				hands[1][i] = new PokerHand(findHand(hands).mutate());
			}
			//sets 10% of child hands to be exact copies of a parent
			for (int i=(int) (hands[0].length*.9); i<hands[0].length; i++) {
				hands[1][i] = findHand(hands);
			}

			//sets the child generation back to be the next parent generation
			for (int i=0; i<hands[0].length; i++) {
				hands[0][i] = hands[1][i];
			}
			turns++;
		}while((getAverageFitness(hands) < 270) && (turns <= 200));
	}

	/*
		Finds the best hand in the current parent group
		Input: Array of PokerHands
		Ouput: Column location of the best hand in the parent row (int)
	*/
	public static int maxHand(PokerHand[][] hands){
		int max =0;
		int location=0;
		for (int i=0; i<hands[0].length; i++) {
			if(hands[0][i].getFitness() > max){
				max = hands[0][i].getFitness();
				location = i;
			}
		}
		return location;
	}

	/*
		Finds a randomly-chosen (weighted based on fitness) hand from the parent generation.
		Input: Array of PokerHands
		Output: Randomly-chosen PokerHand
	*/
	public static PokerHand findHand(PokerHand[][] hands){
		int fitness = randomHand(hands);
		int total = 0;
		for(int i=0; i<hands[0].length; i++){
			total+=hands[0][i].getFitness();
			if (total>=fitness) {
				return hands[0][i];
			}
		}
		return hands[0][hands[0].length];
	}

	/*
		Sums the total fitness of all parent hands and returns a random value out between 0 and that value
		Input: Array of PokerHands
		Output: int containing a random value within the total fitness of all parent hands
	*/
	public static int randomHand(PokerHand[][] hands){
		Random rand = new Random();
		int total = 1;
		for (int i = 0; i<hands[0].length; i++) {
			total+=hands[0][i].getFitness();
		}

		return rand.nextInt(total);
	}

	/*
		Finds the average fitness of the parents in an array of PokerHands
		Input: Array of PokerHands
		Output: Average fitness as an integer
	*/
	public static double getAverageFitness(PokerHand[][] hands){
		double total = 0;
		for (int i = 0; i<hands[0].length; i++) {
			total+=hands[0][i].getFitness();
		}
		return total/100;
	}

	/*
		Crossover function combines two hands together and returns a combined hand.
		Input: Two PokerHand objects to combine
		Output: A 2x5 array representing a new hand.
	*/
	public static int[][] crossover(PokerHand one, PokerHand two){
		Random rand = new Random();
		
		int[][] firstHand = one.getHand();
		int[][] secondHand = two.getHand();
		//Gets number of cards to be used from the first hand 
		int cross = rand.nextInt(4);
		//Creates the new array
		int[][] newHand = new int[2][5];

		//inputs the first "cross" values of the first hand into the new one
		for(int i=0; i<=cross; i++){
			//becuase firstHand must be a legal hand, all of these cards must be unique
			newHand[0][i] = firstHand[0][i];
			newHand[1][i] = firstHand[1][i];
		}


		int j = secondHand[0].length-1;
		//fills the empty spaces in newHand with cards from secondHand
		for(int i=newHand[0].length-1; i>cross; i--){
			newHand[0][i] = secondHand[0][j];
			newHand[1][i] = secondHand[1][j];

			//if the new card is a duplicate, reset the counter back one and replace that card with 
			//the next card contained in the second hand.
			if(isRepeat(newHand, i)){
				i++;
			}
			j--;
		}

		return newHand;

	}

	/*
		If the card at the given location can legally be added (it is not a duplicate of another card)
		then legal returns true. Otherwise return false.
		Input: array to check and position of the card we are checking
		Output: True if card can legally be added, false if not.
	*/
	public static boolean isRepeat(int[][] given, int position){
		for(int j = 0; j<given[0].length; j++){
			//Does not compare a card to itself
			if(j!=position){
				//if the number and suit of the card in column j is the same as the one in the
				//given position, then the given card is a duplicate.
				if((given[0][j] == given[0][position]) && (given[1][j] == given[1][position])){
					return true;
				}
			}
		}
		return false;
	}

	/*
		Properly prints a hand of cards, as numbering of the array is off and suits 
		are incorrectly represented in the array.
		Input: Array
		Output: Array nicely printed as a hand of cards
	*/
	public static void printHand(int[][] hand){

		for (int i=0; i<hand[0].length; i++) {
			String valRep = ""; //number representation for current card
			//finds the value of the card based on int in the first row
			if (hand[0][i]<9) { //numbered cards
				valRep = String.valueOf(i+2);
			}else if (hand[0][i] == 9) {
				valRep = "Jack";
			}else if (hand[0][i] == 10) {
				valRep = "Queen";
			}else if (hand[0][i] == 11) {
				valRep = "King";
			}else if (hand[0][i] == 12) {
				valRep = "Ace";
			}

			//suit of the card based on int in the second row
			//(int corresponds to location in suits array)
			String[] suits = new String[] {"Clubs", "Diamonds", "Hearts", "Spades"};
			System.out.print("[" + valRep + ", " + suits[hand[1][i]-1] + "]");
		}
		System.out.println();
	}

}





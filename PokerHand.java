/**
	Class containing one poker hand.
	Contains:
		2x5 array with encoded hand (either randomized or passed in at initialization)
		fitness value as int (sets itself based on the hand)

	Has methods to mutate, copy, and generate a randomized hand.

	Sort method is a modified version of CrunchifyBubbleSortAsceMethod found here:
	http://crunchify.com/java-bubble-sort-algorithm-ascending-order-sample/
**/

import java.util.*;


public class PokerHand{
	private int[][] hand;
	private int fitness;

	/*
		Constructs a hand of cards, given no information
		Input: none
		Output: Randomized PokerHand object
	*/
	public PokerHand(){
		this.hand = makeHand();
		sort();
		this.fitness = fitness();
	}

	/*
		Constructs a hand of cards object, given an array with the hand in it
		Input: array containing the hand
		Output: creates a PokerHand object
	*/
	public PokerHand(int[][] create){
		this.hand = create;
		sort();
		this.fitness = fitness();
	}

	public int[][] getHand(){
		return this.hand;
	}

	public int getFitness(){
		return this.fitness;
	}
	/*
		This method creates a hand of cards in a 2x5 array
		hand[rows][columns]
		row 0 - card value (0 to 12)
		row 1 - card suit (0 to 3)
		columns - each represents a card
		Input: none
		Output: 2x5 array of cards
	*/
	private int[][] makeHand(){
		Random rand = new Random();
		int[][] temp = new int[2][5];
		for(int i=0; i<temp[0].length; i++){
			temp[0][i] = rand.nextInt(13);
			temp[1][i] = rand.nextInt(4)+1;
			//resets card that was just added if it is a duplicate
			if(isRepeat(temp, i)){
				i--;
			}
		}
		return temp;
	}

	/*
		Checks to see if the card in a hand at a given position is repeated anywhere else in
		the hand.
		Input: hand array, position of the card being checked (int)
		Output: TRUE if the card IS a repeat; FALSE if the card is unique
	*/
	private boolean isRepeat(int[][] given, int position){
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
		mutation function. Returns an array for a mutated hand of cards based on the 
		current one (either changes number or suit of one card)
		Input: none
		Output: Array of new hand
	*/
	public int[][] mutate(){
		Random rand = new Random();
		//Randomly chooses the card to be changed
		int position = rand.nextInt(5);
		
		//copys the current hand into a new array
		int[][] newHand = copyHand();
		
		//changes the previously decided card in the hand
		//repeats the mutation until there are no duplicate cards
		do{
			newHand[0][position] = rand.nextInt(13);
			newHand[1][position] = rand.nextInt(4)+1;
		}while(isRepeat(newHand, position));

		return newHand;
	}

	/*
		Copies the current hand into a new array.
		Input: none
		Output: copy of the current hand in a different array.
	*/
	public int[][] copyHand(){
		
		//replicates this hand as a new array
		int[][] newHand = new int[2][5];
		for(int i=0; i<this.hand[0].length; i++){
			newHand[0][i] = this.hand[0][i];
			newHand[1][i] = this.hand[1][i];
		}

		return newHand;
	}

	/*
		Fitness function. Sets the fitness of the hand based on how good the hand is.
		Input: none
		Output: Sets fitness value
	*/
	private int fitness(){
		//sets this value now so that sameVal() does not get called multiple times.
		int ifSameVal = sameVal();
		if (isFlush() && isStraight() && hand[0][4]==12) {
			return 512;
		}else if (isFlush() && isStraight()) {
			return 256;
		}else if (ifSameVal == 4) {
			return 128;
		}else if (ifSameVal == 5) {
			return 64;
		}else if (isFlush()) {
			return 32;
		}else if (isStraight()) {
			return 16;
		}else if (ifSameVal == 3) {
			return 8;
		}else if (ifSameVal == 2) {
			return 4;
		}else if (ifSameVal == 1) {
			return 2;
		}else{
			return 1;
		}
	}

	/*
		Returns whether or not the current hand is "flush" or all the same suit.
		Input: none
		Output: True if hand is flush, false if hand is not flush
	*/
	private boolean isFlush(){
		for(int i : hand[1]){
			if(i != hand[1][0]){
				return false;
			}
		}
		return true;
	}

	/*
		Returns whether or not the hand is a straight (all of the cards are in a row)
		Input: none
		Output: true if hand is straight, false if hand is not straight
	*/
	private boolean isStraight(){
		for(int i=1; i<hand[0].length; i++){
			if (hand[0][i] != (hand[0][i-1]+1)) {
				return false;
			}
		}
		return true;
	}

	/*
		Checks if there are any repeated values (ex. four 4s) in the hand, and returns the
		type of hand based on this criteria.
		Input: none
		Output: returns 5 if full house, 4 if 4 of a kind, 3 if 3 of a kind, 2 if 2 pairs,
		1 if 1 pair, and 0 if all cards have unique values.
	*/
	private int sameVal(){
		int count1 = 1;
		int count2 = 1;
		int i;
		for (i=1; i<hand[0].length; i++) {
			//because cards are sorted, cards of same value will be next to each other
			if(hand[0][i]==hand[0][i-1]){
				count1++;
			}else if(count1>1){
				break;
			}
		}

		for (i=i; i<hand[0].length; i++) {
			if (hand[0][i]==hand[0][i-1]) {
				count2++;
			}
		}

		if(count1 == 4){ //four of a kind
			return 4;
		}else if((count1+count2) == 5){ //full house
			return 5;
		}else if((count1==3) || (count2==3)){ //three of a kind
			return 3;
		}else if ((count1==2) && (count2==2)) { //two pairs
			return 2;
		}else if (count1==2) { //one pair
			return 1;
		}
		return 0;
	}

	/*
		Sorts the hand based on card values (not suit).
		I use bubblesort because each hand is only 5 items, and has a worst case of 25 steps per
		hand. The extra time isn't that large, and bubblesort is simple, clear, and easy to modify.

		Original sort function taken from:
		http://crunchify.com/java-bubble-sort-algorithm-ascending-order-sample/
	*/
	private void sort(){
        int tempVal;
        int tempSuit;
        for(int i=0; i < hand[0].length-1; i++){
 
            for(int j=1; j < hand[0].length-i; j++){
                if(hand[0][j-1] > hand[0][j]){
                    tempVal = hand[0][j-1];
                    tempSuit = hand[1][j-1];
                    hand[0][j-1] = hand[0][j];
                    hand[1][j-1] = hand[1][j];
                    hand[0][j] = tempVal;
                    hand[1][j] = tempSuit;
                }
            }
        }
	}
}

//////////////// FILE HEADER (INCLUDE IN EVERY FILE) //////////////////////////
//
// Title: P02-Matching-Game
//
// Author: Monniiesh Velmurugan, Mouna Kacem
//
///////////////////////////////////////////////////////////////////////////////
import java.io.File;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * This class contains the methods to run the game.
 */
public class MemoryGame {
	// Congratulations message
	private final static String CONGRA_MSG = "CONGRATULATIONS! YOU WON!";
	// Cards not matched message
	private final static String NOT_MATCHED = "CARDS NOT MATCHED. Try again!";
	// Cards matched message
	private final static String MATCHED = "CARDS MATCHED! Good Job!";
	// 2D-array which stores cards coordinates on the window display
	private final static float[][] CARDS_COORDINATES = new float[][] { { 170, 170 }, { 324, 170 }, 
		{ 478, 170 },{ 632, 170 }, { 170, 324 }, { 324, 324 }, { 478, 324 }, { 632, 324 }, 
		{ 170, 478 }, { 324, 478 }, { 478, 478 }, { 632, 478 } };
	// Array that stores the card images filenames
	private final static String[] CARD_IMAGES_NAMES = new String[] { "ball.png", "redFlower.png", 
			"yellowFlower.png", "apple.png", "peach.png", "shark.png" };
	// PApplet object that represents the graphic display window
	private static PApplet processing;
	// one dimensional array of cards
	private static Card[] cards;
	// array of images of the different cards
	private static PImage[] images;
	// First selected card
	private static Card selectedCard1;
	// Second selected card
	private static Card selectedCard2;
	// boolean evaluated true if the game is won, and false otherwise
	private static boolean winner;
	// number of cards matched so far in one session of the game
	private static int matchedCardsCount;
	// Displayed message to the display window
	private static String message;

	/**
	 * This method defines the initial environment properties of this game as the
	 * program starts.
	 *
	 * @param processing PApplet object that represents the graphic display window
	 */
	public static void setup(PApplet processing) {

		processing.background(245, 255, 250); // Mint cream color

		images = new PImage[CARD_IMAGES_NAMES.length];

		for (int i = 0; i < images.length; i++) {
			images[i] = processing.loadImage("images" + File.separator + CARD_IMAGES_NAMES[i]);
		}
		MemoryGame.processing = processing;
		startNewGame();

	}

	/**
	 * This method initializes the Game by shuffling all the cards and keeping them
	 * turned down on the board.
	 */
	public static void startNewGame() {
		selectedCard1 = null;
		selectedCard2 = null;
		matchedCardsCount = 0;
		winner = false;
		message = "";

		cards = new Card[CARDS_COORDINATES.length];

		int[] mixedUp = Utility.shuffleCards(cards.length);

		for (int i = 0; i < cards.length; i++) {
			cards[i] = new Card(images[mixedUp[i]], CARDS_COORDINATES[i][0], 
					CARDS_COORDINATES[i][1]);
			cards[i].draw();
		}

	}

	/**
	 * This method checks if a user has entered "n" or "N" key and restarts the game
	 * accordingly.
	 */
	public static void keyPressed() {
		if (processing.key == 'n' || processing.key == 'N') {
			startNewGame();
		}
	}

	/**
	 * This method ensures that the application window is drawn continously
	 * incorporating any visual changes made in the game.
	 */
	public static void draw() {
		processing.background(245, 255, 250); // Mint cream color
		for (int i = 0; i < cards.length; i++) {
			cards[i].draw();
		}
		if (selectedCard2 != null && !selectedCard2.isMatched()) {
			displayMessage(NOT_MATCHED);
		}
		if (selectedCard2 != null && selectedCard2.isMatched() && !winner) {
			displayMessage(MATCHED);
		}
		if (winner) {
			displayMessage(CONGRA_MSG);
		}

	}

	/**
	 * This method displays a given message to the display window.
	 *
	 * @param message to be displayed to the display window
	 */
	public static void displayMessage(String message) {
		processing.fill(0);
		processing.textSize(20);
		processing.text(message, processing.width / 2, 50);
		processing.textSize(12);
	}

	/**
	 * Thsi method checks whether the mouse is over a given Card
	 *
	 * @return true if the mouse is over the storage list, false otherwise
	 */
	public static boolean isMouseOver(Card card) {

		int halfLength = card.getHeight() / 2;
		int xValue = Math.round(card.getX());
		int yValue = Math.round(card.getY());

		if ((processing.mouseX <= (xValue + halfLength)) && 
				(processing.mouseX >= (xValue - halfLength))) {
			if ((processing.mouseY <= (yValue + halfLength)) && 
					(processing.mouseY >= (yValue - halfLength))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * This method ensures the functioning of the game. It is responsible to unflip
	 * cards when mouse-clicked over an unmatched card. This method is also
	 * responsible for flipping cards down when the 2 cards flipped don't match.
	 */
	public static void mousePressed() {

		for (int i = 0; i < cards.length; i++) {
			if (isMouseOver(cards[i])) {
				// If mouse is clicked over an unmatcehd card
				if (!(cards[i].isMatched())) {
					// if the 2 cards flipped dont match then set them invisible
					// and deselect them
					if (selectedCard2 != null && !selectedCard2.isMatched()) {
						selectedCard1.deselect();
						selectedCard2.deselect();
						selectedCard1.setVisible(false);
						selectedCard2.setVisible(false);
						selectedCard1 = null;
						selectedCard2 = null;
					}
					// if the 2 cards flipped match then deselect them
					else if (selectedCard2 != null && selectedCard2.isMatched()) {
						selectedCard1.deselect();
						selectedCard2.deselect();
						selectedCard1 = null;
						selectedCard2 = null;
					}
					cards[i].setVisible(true);
					cards[i].select();
					if (selectedCard1 == null) {
						selectedCard1 = cards[i];
					} else if (selectedCard2 == null) {
						selectedCard2 = cards[i];
						matchingCards(selectedCard1, selectedCard2);
					}
					break;
				}
			}
		}
	}

	/**
	 * This method checks whether the two cards match or not.
	 *
	 * @param card1 reference to the first card
	 * @param card2 reference to the second card
	 * @return true if card1 and card2 image references are the same, false
	 *         otherwise
	 */
	public static boolean matchingCards(Card card1, Card card2) {
		if (card1 == null || card2 == null) {
			return false;
		} else if (card1.getImage().equals(card2.getImage())) {
			card1.setMatched(true);
			card2.setMatched(true);
			matchedCardsCount++;
			if (matchedCardsCount == 6) {
				winner = true;
			}
			return true;
		} else {

			return false;
		}

	}

	public static void main(String[] args) {

		Utility.startApplication();

	}
}

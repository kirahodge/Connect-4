import java.util.ArrayList;
import java.awt.Point;
import java.util.List;

public class Board {

	public final int NO_PLAYER = 0;
	public final int PLAYER_USER = 1; //user
	public final int PLAYER_AI_OR_USER2 = 2; //computer
	public static final int ROW_COUNT = 6;
	public static final int COLUMN_COUNT = 7;
	int[][] board = new int [ROW_COUNT][COLUMN_COUNT];
	public ArrayList<Integer> possibleMoveScores = new ArrayList<>();
	public int computerMove;
	public Point mostRecentPlayed;
	public static ArrayList<Column> columns = new ArrayList<>();
	
	public void createColumns() {
		columns.clear();
		for (int i = 0; i < COLUMN_COUNT; i++) {
			columns.add(new Column (i,ROW_COUNT - 1));
		}
	}
	
	public boolean isGameOver() {
		return hasPlayerWon(PLAYER_AI_OR_USER2)|| hasPlayerWon(PLAYER_USER) || getAvailableCells().isEmpty();
	}
	
	public boolean hasPlayerWon(int player) {
		if (mostRecentPlayed == null) {
			return false;
		}
		int xOfRecent = mostRecentPlayed.x;
		int yOfRecent = mostRecentPlayed.y;
		return (isHorizontalWin(yOfRecent, player) || isVerticalWin(xOfRecent, yOfRecent) || isDiagonalWin(xOfRecent, yOfRecent, player));
	}
	
	public boolean isHorizontalWin (int y, int player) {
		int i = 0;
		int count = 0;
		while (i < COLUMN_COUNT - 1) {
			if (board[y][i] == board[y][i+1]) {
				count ++;
				if (count == 3){
					if (board[y][i] == player) {
						return true;
					}
				}
			}
			else {
				count = 0;
			}
			i++;
		}
		return false;
	}
	
	public boolean isVerticalWin(int x, int y) {
		if (y>2) {
			return false;
		}
		
		if (board[y][x]== board[y+1][x]&& board[y+1][x] == board[y+2][x]&& board[y+2][x]==board[y+3][x]) {
			return true;
		}
		return false;
	}
	
	public boolean isDiagonalWin(int x, int y, int player) {
		int sumOfCoords = x + y;
		int difference = x - y;
		int count;
		int i;
		int j;
		
		//top left to bottom right
		Point topLeftPoint;
		if (Math.abs(difference) < 3) {
			if (difference == 0) {
				topLeftPoint = new Point(0,0);
			}
			else if (difference < 0) {
				topLeftPoint = new Point(0,Math.abs(difference));
			}
			else {
				topLeftPoint = new Point (difference, 0);
			}
			
			i = topLeftPoint.x;
			j = topLeftPoint.y;
			count = 0;
			while (i < COLUMN_COUNT - 1 && j < ROW_COUNT - 1) {
				if (board[j][i] == board[j+1][i+1]) {
					count ++;
					if (count == 3){
						if (board[j][i]== player) {
							return true;
						}
					}
				}
				else {
					count = 0;
				}
				i++;
				j++;
			}
		}
		
		//top right to bottom left
		if (sumOfCoords < 9 && sumOfCoords > 2) {
			i = x;
			j = y;
			
			while (i<COLUMN_COUNT-1 && j>0) {
				i++;
				j--;
			}
			count = 0;
			while (i > 0 && j < ROW_COUNT-1) {
				if (board[j][i] == board[j+1][i-1]) {
					count ++;
					if (count == 3){
						if (board[j][i]== player) {
							return true;
						}					
					}
				}
				else {
					count = 0;
				}
				i--;
				j++;
			}
		}
		return false;
		
	}
	
	public List<Point> getAvailableCells(){	
		List<Point> availableCells = new ArrayList<>();
		for (Column column : columns) {
			Point available = null;
			for (int i = ROW_COUNT - 1; i >= 0; i--) {
				if (board[i][column.x] == 0) {
					available = (new Point (column.x, i));
					break;
				}
			}
			availableCells.add(available);
		}
		return availableCells;
	}
	
	public boolean placeAMove (Point userMove, int player) {
		if (board[userMove.y][userMove.x] != NO_PLAYER) {
			return false;
		}
		board[userMove.y][userMove.x] = player;
		mostRecentPlayed = new Point (userMove.x, userMove.y);
		
		return true;
	}
	
	
	public int minimax(int depth, int turn, int alpha, int beta) {

		List<Point> availableCells = getAvailableCells(); 

		if (isGameOver()) {
			if (hasPlayerWon(PLAYER_AI_OR_USER2)) {
				return 1000;
			}
			else if (hasPlayerWon(PLAYER_USER)) {
				return -1000;
			}
		}
		else if (availableCells.isEmpty()) {
			return 0;
		}
		else if (depth==0){
			return evaluate(turn);
		}
		
		int currentScore = 0;
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		

		for (Point point: availableCells) {
			if (point!= null) {
				board[point.y][point.x] = turn;

				if (turn==PLAYER_AI_OR_USER2) {
					placeAMove(point, PLAYER_AI_OR_USER2);
					currentScore = minimax(depth-1, PLAYER_USER, alpha, beta);
					max = Math.max(currentScore, max);
					alpha = Math.max(alpha, currentScore);

				}
				else if (turn == PLAYER_USER) {
					placeAMove(point, PLAYER_USER);
					currentScore = minimax(depth-1, PLAYER_AI_OR_USER2, alpha, beta);
					min = Math.min(currentScore,  min);
					beta = Math.min(beta, currentScore);

				}
				board[point.y][point.x] = NO_PLAYER;
				
				
				
				if (alpha > beta) {
					break;
				}
				if (depth == 4) {
					possibleMoveScores.add(currentScore);
				}

			}
			else if(depth == 4) {
				possibleMoveScores.add(null);
			}

		}

		return turn == PLAYER_AI_OR_USER2 ? max : min;

	}
	
	public int evaluate(int player) {
		int score = 0;
		ArrayList<Integer> window = new ArrayList<>();
		Point bestPoint;
		
		// score center (value of +3)
		int centerCount = 0;
		for (int i = 0; i < ROW_COUNT; i++) {
			if (board[i][4]==player) {
				centerCount ++;
			}
			score = centerCount * 3;
		}
		
		// score horizontal
		for (int i = 0; i< ROW_COUNT; i++) {
			for (int j = 0; j<COLUMN_COUNT - 3; j++) {
				window.clear();
				window.add(board[i][j]);
				window.add(board[i][j+1]);
				window.add(board[i][j+2]);
				window.add(board[i][j+3]);
				score += evaluateWindow (window, player);
			}
		}
		
		// score vertical
		for (int i = 0; i < COLUMN_COUNT; i ++) {
			for (int j = 0; j <ROW_COUNT - 3; j++) {
				window.clear();
				window.add(board[j][i]);
				window.add(board[j+1][i]);
				window.add(board[j+2][i]);
				window.add(board[j+3][i]);
				score += evaluateWindow (window,player);
			}
		}
		
		// score diagonal
		for (int i = 0 ; i< COLUMN_COUNT - 3; i++) {
			for (int j = 0; j <ROW_COUNT - 3; j++) {
				window.clear();
				window.add(board[j][i]);
				window.add(board[j+1][i+1]);
				window.add(board[j+2][i+2]);
				window.add(board[j+3][i+3]);
				score += evaluateWindow (window, player);
			}
		}
		// other diagonal
		for (int i = 6; i > 2; i--) {
			for (int j = 0; j<ROW_COUNT - 3; j++) {
				window.clear();
				window.add(board[j][i]);
				window.add(board[j+1][i-1]);
				window.add(board[j+2][i-2]);
				window.add(board[j+3][i-3]);			
			}
		}

		return score;
	}
	
	public static int evaluateWindow(ArrayList<Integer> window, int player) {
		int score = 0;
		int count = 0;
		int emptySpaces = 0;
		int oppPiece = 0;
		for (int piece: window) {
			if (piece == player) {
				count++;
			}
			else if (piece == 0) {
				emptySpaces++;
			}
			else {
				oppPiece++;
			}
		}
		if (count == 4) {
			score += 1000;
		}
		else if (count == 3 && emptySpaces == 1) {
			score += 5;
		}
		else if (count == 2 && emptySpaces == 2) {
			score += 2;
		}
		if (oppPiece == 2 && emptySpaces == 2) {
			score -=2;
		}
		else if (oppPiece == 3 && emptySpaces == 1) {
			score -= 5;
		}
		else if (oppPiece == 4) {
			score -= 1000;
		}
		
		return score;
	}
	
	public void pickBestMove() {
		int bestScore = Integer.MIN_VALUE;
		int bestCol = 3;
    	minimax(4, PLAYER_AI_OR_USER2, Integer.MIN_VALUE, Integer.MAX_VALUE);
    	for (int i=0; i<possibleMoveScores.size(); i++) {
    		Integer score = possibleMoveScores.get(i);
    		if (score!=null) {
    			if (score > bestScore) {
        			bestScore = score;
        			bestCol = i;
    			}
    		}
    	}
    	
    	computerMove = bestCol;
    	possibleMoveScores.clear();
	}
}


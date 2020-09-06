import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DrawGrid {
    private static JFrame frame;
    private static JOptionPane gameOver;
    private static JFrame welcomeScreen;
    private static JFrame singleOrMulti;
    private static int turn;
    private static MultiDraw board;
    private static boolean isSinglePlayer;
    static Board b;

    public DrawGrid() {
        frame = new JFrame("Connect 4");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(frame.getSize());
        board = new MultiDraw(frame.getSize());
        frame.add(board);
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void main(String... argv) {
    	new DrawGrid();
    	board.singleOrMulti();
    }

    public static class MultiDraw extends JPanel implements MouseListener {
        int startX = 10;
        int startY = 10;
        int cellWidth = 40;
        static int rows = 6;
        static int cols = 7;
        public Color playerColor;
        public Color aiColor;
        public Graphics2D nextPiece;
        static Color[][] grid = new Color[rows][cols];        

        public MultiDraw(Dimension dimension) {
            setSize(dimension);
            setPreferredSize(dimension);
            addMouseListener(this);
            int x = 0;
            for (int row = 0; row < grid.length; row++) {
                for (int col = 0; col < grid[0].length; col++) {
                	grid[row][col] = new Color (255,255,255);
                   
                }
            }
        }

        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            Dimension d = getSize();
            g2.setColor(new Color(0, 0, 0));
            g2.fillRect(0,0,d.width,d.height);
            startX = 40;
            startY = 120;
            nextPiece = (Graphics2D)g;
            nextPiece.setColor(playerColor);

            for (int row = 0; row < grid.length; row++) {
                for (int col = 0; col < grid[0].length; col++) {
                	g2.setColor(grid[row][col]);
                	g2.fillOval(startX, startY, cellWidth, cellWidth);
                	startX += cellWidth;
                }
                startY += cellWidth;
                startX = 40;
            }

            g2.setColor(new Color(255, 255, 255));
            
        }
        
        public void computerMove() {
        	b.pickBestMove();
        	int col = b.computerMove;
        	Point move = new Point(col,getNextY(col));
        	b.placeAMove(move, b.PLAYER_AI_OR_USER2);
        	int x = move.x;
        	int y = move.y;
        	grid[y][x] = aiColor;
        	repaint();
        	if (b.isGameOver()) {
        		gameIsOverState(b.PLAYER_AI_OR_USER2);
        		return;
        	}
        	turn = b.PLAYER_USER;
        }
        
        public static int getNextY(int columnNumber) {
        	Column selectedColumn = b.columns.get(columnNumber);
       		int nextY = selectedColumn.nextY;
       		selectedColumn.nextY = nextY - 1;
       		return nextY;
        }

        public void mousePressed(MouseEvent e) {
        	if (turn == b.PLAYER_AI_OR_USER2) {
        		return;
        	}
        	int x = e.getX();
        	int xSpot = x/cellWidth - 1;
        	if (xSpot > 6) {
        		return;
        	}
        	int ySpot;

        	ySpot = getNextY(xSpot);
        	if (ySpot < 0) {
        		return;
        	}
        	Point newPoint = new Point (xSpot, ySpot);

        	if (b.placeAMove(newPoint, b.PLAYER_USER)) {
        		b.placeAMove(newPoint, b.PLAYER_USER);
        		grid[ySpot][xSpot] =playerColor;
        		turn = b.PLAYER_AI_OR_USER2;
        		repaint();
        	}
        	if(b.isGameOver()) {
        		gameIsOverState(b.PLAYER_USER);
        		return;
        	}
 
        }

        public void mouseReleased(MouseEvent e) {
        	if (turn == b.PLAYER_AI_OR_USER2) {
        		try {
        			TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	computerMove();

        	}

        }

        public void mouseEntered(MouseEvent e) {

        }

        public void mouseExited(MouseEvent e) {

        }

        public void mouseClicked(MouseEvent e) {

        }
        
        public void gameIsOverState(int winningPlayer) {
        	gameOver = new JOptionPane();
        	            
    		String result;
    		if(winningPlayer == b.PLAYER_AI_OR_USER2) {
    			result = "You lost";
    		}
    		else {
    			result = "You won";
    		}
    		gameOver.showMessageDialog(frame, result, "Game over!!", JOptionPane.QUESTION_MESSAGE);
    		int isPlayingAgain = gameOver.showConfirmDialog(frame, "would you like to play again?", "Game over!!", 
    									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    		
    		if (isPlayingAgain == JOptionPane.YES_OPTION) {
    			frame.dispose();
    			new DrawGrid();
    	        board.welcomeScreen();
    		}
    		else if (isPlayingAgain == JOptionPane.NO_OPTION) {
    			System.exit(0);
    		}
    		
    	}
        
        public void playGame(int startingPlayer, Color chosenColor, Color otherColor) {
			b = new Board();
			b.createColumns();
			turn = startingPlayer;
			playerColor = chosenColor;
			aiColor = otherColor;
			if (turn == b.PLAYER_AI_OR_USER2) {
				computerMove();
			}		
        }
        
        public void singleOrMulti() {
        	singleOrMulti = new JFrame("single or multiplayer?");
        	singleOrMulti.setSize(600, 400);
        	singleOrMulti.setLayout(null);
        	
        	JLabel lblWelcome = new JLabel("Welcome to Connect4!");
        	lblWelcome.setBounds(150, 10 , 200, 60);
        	
        	JLabel lblInstructions = new JLabel("When it is your turn, click on the column in which you would like "
        			+ "to play your piece");
        	lblInstructions.setBounds(20,50,600,60);
        	
        	singleOrMulti.add(lblWelcome);
        	singleOrMulti.add(lblInstructions);
        	
        	JLabel lblQuestion = new JLabel ("Would you like single player or multiplayer?");
        	lblQuestion.setBounds(20, 150, 400, 25);
        	
        	JRadioButton btnSingle = new JRadioButton("single");
        	JRadioButton btnMulti = new JRadioButton("multi");
        	
        	btnSingle.setBounds(40, 175, 100, 30);
        	btnMulti.setBounds(140, 175, 100, 30);
        	btnSingle.setSelected(true);
        	
        	ButtonGroup orderGroup = new ButtonGroup();
        	orderGroup.add(btnSingle);
        	orderGroup.add(btnMulti);
        	
        	JRadioButton btnNext = new JRadioButton("next");
        	btnNext.setBounds(200,300,70,30);
        	btnNext.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	if (btnSingle.isSelected()){
                		isSinglePlayer = true;
                	}
                	else {
                		isSinglePlayer = false;
                	}
                	board.welcomeScreen();
                	singleOrMulti.dispose();                	
                }
        	});
        	
        	singleOrMulti.add(btnSingle);
        	singleOrMulti.add(btnMulti);
        	singleOrMulti.add(btnNext);
        	singleOrMulti.add(lblQuestion);
        	singleOrMulti.setVisible(true);
        }
		
        public void welcomeScreen() {
        	welcomeScreen = new JFrame("Welcome");
        	welcomeScreen.setSize(600, 400);
        	welcomeScreen.setLayout(null);
        	
        	JRadioButton btnFirst = new JRadioButton("first");
        	JRadioButton btnSecond = new JRadioButton("second");
        	JRadioButton btnRed = new JRadioButton("Red");
        	JRadioButton btnBlue = new JRadioButton("blue");
    		JLabel lblQuestion = new JLabel ();
        	JLabel lblColor = new JLabel ("what colour would you like to be?");


        	
        	if (isSinglePlayer) {
        		lblQuestion.setText("would you like to play...");
            	lblQuestion.setBounds(20, 150, 400, 25);
            	         	
            	btnFirst.setBounds(40, 175, 100, 30);
            	btnSecond.setBounds(140, 175, 100, 30);
            	btnFirst.setSelected(true);
            	
            	ButtonGroup orderGroup = new ButtonGroup();
            	orderGroup.add(btnFirst);
            	orderGroup.add(btnSecond);
            	
            	lblColor.setText("what colour would you like to be?");
            	           	           		            	                       	
        	}
        	
        	else {
        		lblColor.setText("would you like player one to be...");
        	}
        	lblColor.setBounds(350, 150, 600, 25);

        	btnRed.setSelected(true);
        	btnRed.setBounds(380, 175, 100, 30);
        	btnBlue.setBounds(500, 175, 100, 30);
        	btnFirst.setSelected(true);
        	
        	ButtonGroup colorGroup = new ButtonGroup();
        	colorGroup.add(btnRed);
        	colorGroup.add(btnBlue);
              	
        	JButton btnPlay = new JButton("Play");
        	btnPlay.setBounds(200,300,70,30);
        	
        	JButton btnCancel = new JButton("Cancel");
        	btnCancel.setBounds(300, 300, 70, 30);
        	
        	btnPlay.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int choice;
                    Color chosenColor;
                    Color otherColor;
                    if (btnFirst.isSelected()) {
                    	choice = 1;
                    }
                    else {
                    	choice = 2;
                    }
                    if(btnRed.isSelected()) {
                    	chosenColor = new Color (255,0,0);
                    	otherColor = new Color (0,0,255);
                    }
                    else {
                    	otherColor = new Color (255,0,0);
                    	chosenColor = new Color (0,0,255);
                    }
                    
                    playGame(choice, chosenColor, otherColor);
                    welcomeScreen.dispose();                   
                }
            });  
        	btnCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	System.exit(0);
                }
            });   
        	
            welcomeScreen.add(lblQuestion);
            welcomeScreen.add(btnFirst);
            welcomeScreen.add(btnSecond);
            welcomeScreen.add(btnPlay);
            welcomeScreen.add(btnCancel);
            welcomeScreen.add(btnRed);
            welcomeScreen.add(btnBlue);
            welcomeScreen.add(lblColor);
        	welcomeScreen.setVisible(true);        	
        	
        }
    }
  }
    

    

    



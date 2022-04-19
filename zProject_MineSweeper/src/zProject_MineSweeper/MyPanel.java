package zProject_MineSweeper;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.*;

import javax.swing.JPanel;


public class MyPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static final int screenW = 750;
	static final int screenH = 400;
	
	static final int unitSize = 25;
	
	static final int rowsNum = screenW/unitSize;
	static final int colsNum = screenH/unitSize;
	
	
	// In this code i have defined bomb as -5
	final int bombNum = -5;
	final double bombProb = 0.25;
	final int bombGap = unitSize/4;
	final int fontSize = unitSize/2;
	
	boolean firstTurn;
	
	boolean gameOver;
	
	int[][] values;
	
	boolean[][] path;
	
	Random random;	
	
	MyPanel(){
		initializePanel();
		hideCells();
	}
	
	void initializePanel() {
		
		this.addMouseListener(new MyMouseListeners());
		this.setPreferredSize(new Dimension(screenW,screenH));
		this.setFocusable(true);
		this.setBackground(new Color(200,200,200));
	}
	
	public void hideCells() {
		
		path = new boolean[rowsNum][colsNum];

		for(int i=0; i<rowsNum; i++) {
			for(int j=0; j<colsNum; j++) {
					path[i][j] = false;
			}
		}
		
		firstTurn = true;
		
	}
	
	public boolean checkReserved(int i, int j, int iRes, int jRes){
		
		// For the first click, it will reserve all 8 cells to the cell clicked
		
		for(int x=-1; x<2; x++) {
			for(int y=-1; y<2; y++) {
				
				int iChk = iRes+x;
				int jChk = jRes+y;
				
				if(iChk == i && jChk == j) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	void initializeValues(int iRes, int jRes) {
		
		random = new Random();
		
		values = new int[rowsNum][colsNum];
		
		double randomBombProb;
		
		for(int i=0; i<rowsNum; i++) {
			for(int j=0; j<colsNum; j++) {
				randomBombProb = random.nextDouble();
				if (randomBombProb< bombProb && !checkReserved(i,j,iRes,jRes)) {
					values[i][j] = bombNum;
				}
			}
		}
		
		for(int i=0; i<rowsNum; i++) {
			for(int j=0; j<colsNum; j++) {
				if (values[i][j] != bombNum) {
					setNumbers(i,j);	
				}
			}
		}
		
		gameOver = false;
	}

	private void setNumbers(int i, int j) {
		//Check number of bombs near each node
		int sum = 0;
		
		for(int x=-1; x<2; x++) {
			for(int y=-1; y<2; y++) {
				int rowx = (i + x + rowsNum) / rowsNum;
				int coly = (j + y + colsNum) / colsNum;
				if (rowx==1 && coly==1) {
					if(values[i+x][j+y] == bombNum) {
						sum++;
					}
				}
			}
		}
		
		values[i][j] = sum;
		
	}
	
	public void update(int i, int j) {
		floodFill(i,j);
		checkGameOver(i,j);
	}
	
	public void floodFill(int i, int j) {
		
		if(i>=0 && i<rowsNum && j>=0 && j<colsNum) {
			if (values[i][j]!=0) {
				path[i][j] = true;
				return;
			}
			
			if(path[i][j] == false) {
				path[i][j] = true;
				
				for(int x=-1; x<2; x++) {
					for(int y=-1; y<2; y++) {
						floodFill(i+x,j+y);
					}
				}
			}
		}
		
	}
	
	public void checkGameOver(int i, int j) {
		if(values[i][j] == bombNum) {
			gameOver = true;
		}
	}
	
	public void reset(){
		hideCells();
		gameOver = false;
		repaint();
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		draw(g);
		
		if(gameOver)
			drawGameOverScreen(g);
	}


	private void draw(Graphics g) {
		
		g.setFont(new Font("Times Roman", Font.BOLD, fontSize));
		FontMetrics metrics = getFontMetrics(g.getFont()); 

		for(int i=0; i<rowsNum; i++) {
			for(int j=0; j<colsNum; j++) {
				if (path[i][j]==true) {
					drawValue(g,metrics,i,j);
				}
				else {
					g.setColor(new Color(120,120,120));
					g.fillRect(i*unitSize, j*unitSize, unitSize, unitSize);
				}
			}
		}
		
		drawGrid(g);
	}

	private void drawValue(Graphics g, FontMetrics metrics,int i, int j) {
		
		if (values[i][j] != bombNum) {
			g.setColor(new Color(10,10,10));
			g.drawString("" + values[i][j], 
						 i*unitSize + unitSize/2 - metrics.stringWidth("" + values[i][j])/2, 
						 j*unitSize + unitSize/2 + fontSize/2);
		}
		else {
			g.setColor(Color.RED);
			g.fillOval(i*unitSize+bombGap, j*unitSize+bombGap, unitSize-(2*bombGap), unitSize-(2*bombGap));
		}
				
	}

	private void drawGrid(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setStroke(new BasicStroke(2));
		g2.setColor(new Color(10,10,10));
		
		for(int i=0; i<rowsNum; i++) {
			g2.drawLine(unitSize*i, 0, unitSize*i, screenH);
		}
		
		for(int i=0; i<colsNum; i++) {
			g2.drawLine(0, unitSize*i, screenW, unitSize*i);
		}
	}
	
	private void drawGameOverScreen(Graphics g) {
		
		g.setFont(new Font("Times Roman", Font.ITALIC, 50));
		FontMetrics metrics = getFontMetrics(g.getFont()); 
		
		g.setColor(new Color(120,120,120));
		//g.fillRect(0,0,screenW,screenH);
		g.setColor(new Color(200,0,0));
		g.drawString("Game Over", 
					 screenW/2 - metrics.stringWidth("Game Over")/2, 
					 screenH/2);
	}
	
	public class MyMouseListeners extends MouseAdapter{

		public void mouseClicked(MouseEvent e) {
			if(e.getButton() == 1 && gameOver==false) {
				int i = e.getX()/unitSize;
				int j = e.getY()/unitSize;
				if(firstTurn) {
					System.out.println("First Turn");
					initializeValues(i,j);
					firstTurn = false;
				}
				update(i,j);
				repaint();
			}
		}
	}

}

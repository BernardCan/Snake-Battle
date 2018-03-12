import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;

public class snakeCanvas extends Panel implements Runnable, KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 621829409143218298L;
	private final int BOX_HEIGHT = 20;
	private final int BOX_WIDTH = 20;
	private final int GRID_HEIGHT = 750/BOX_HEIGHT-6;
	private final int GRID_WIDTH = 290/BOX_WIDTH;
	private final int LIMIT = 100;
	private int speed = 150;
	private int add = 5;
	
	
	
	private LinkedList<Point> snake;
	private LinkedList<Point> snake1;
	private LinkedList<Point> Sbody;
	private LinkedList<Point> Sbody1;
	private LinkedList<Point> dropWall;
	//private LinkedList<Point> wall;
	
	private Point fruit, fruit1, body, body1, ver, ver2;
	private int direction = Direction.NO_DIRECTION;
	private int direction1 = Direction1.NO_DIRECTION1;
	
	private Thread runThread;
	private int score = 0;
	private String highScore = "";
	
	private Image menuImage = null;
	private boolean isInMenu = true;
	private boolean isAtEndGame = false;
	private boolean Sbdy = false, Sbdy1 = false;
	
	
	public void init()
	{
		this.addKeyListener(this);
	}
	public snakeCanvas(){}
	
	public snakeCanvas(LinkedList<Point> snake, LinkedList<Point> snake1, LinkedList<Point> sbody,
			LinkedList<Point> sbody1, Point fruit, Point fruit1, Point body, Point body1, int direction, int direction1,
			Thread runThread, int score, String highScore, Image menuImage, boolean isInMenu, boolean isAtEndGame,
			boolean sbdy, boolean sbdy1) {
		super();
		this.snake = snake;
		this.snake1 = snake1;
		Sbody = sbody;
		Sbody1 = sbody1;
		this.fruit = fruit;
		this.fruit1 = fruit1;
		this.body = body;
		this.body1 = body1;
		this.direction = direction;
		this.direction1 = direction1;
		this.runThread = runThread;
		this.score = score;
		this.highScore = highScore;
		this.menuImage = menuImage;
		this.isInMenu = isInMenu;
		this.isAtEndGame = isAtEndGame;
		Sbdy = sbdy;
		Sbdy1 = sbdy1;
	}

	public void paint(Graphics g)
	{
		

		if (runThread == null)
		{
			this.setPreferredSize(new Dimension(300, 750));
			this.addKeyListener(this);
			runThread = new Thread(this);
			runThread.start();
		}
		
		if (isInMenu)
		{
			DrawMenu(g);
		}
		else if(isAtEndGame)
		{
			DrawEndGame(g);
		}
		else 
		{	
			if (snake == null)
			{
				snake = new LinkedList<Point>();
				snake1 = new LinkedList<Point>();
				Sbody = new LinkedList<Point>();
				Sbody1 = new LinkedList<Point>();
				dropWall = new LinkedList<Point>();
				ver = new Point(0, 0);
				ver2 = new Point(0,0);
				Point a = new Point(7,31);
				dropWall.push(a);
				//wall = new LinkedList<Point>();
				
				GenerateDefaultSnake();
				//GenerateDefaultSnake1();
				//GenerateWall();
				
				//PlaceFruit();
				//PlaceFruit1();
			}
			
			if (highScore.equals(""))
			{
				highScore = this.GetHighScoreValue();
			}
			
			

			DrawGrid(g);
			//DrawWall(g);
			//DrawFruit(g);
			//DrawFruit1(g);
			
			DrawSnake(g);
			//DrawSnake1(g);
			if(!dropWall.isEmpty())
				snake1DropWall(g);
			if(Sbdy)
				DrawSbody(g);
			if(Sbdy1)
				DrawSbody1(g);
			DrawScore(g);
			DrawOrder(g);
			
		}	
	}
	
	
	public void DrawMenu(Graphics g)
	{
		if(this.menuImage == null)
		{
			try
			{
				URL imagePath = snakeCanvas.class.getResource("snake (2).png");
				this.menuImage = ImageIO.read( imagePath );

			}
			catch (Exception e)
			{
				//image not exist
				e.printStackTrace();
			}
			
		}
		
		g.drawImage(menuImage, 0, 0, 300, 750, this);
	}
	
	public void update(Graphics g)
	{
		// default update contains double buffering
		Graphics offScreenGraphics;
		BufferedImage offScreen = null;
		Dimension d = this.getSize();
		
		offScreen = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		offScreenGraphics = offScreen.getGraphics();
		offScreenGraphics.setColor(this.getBackground());
		offScreenGraphics.fillRect(0,  0, d.width, d.height);
		offScreenGraphics.setColor(this.getForeground());
		paint(offScreenGraphics);
		
		//flip
		g.drawImage(offScreen, 0, 0, this);
	}
	
	public void GenerateDefaultSnake()
	{
		score = 0;
		snake.clear();
		snake.add(new Point(2,30));
		snake.add(new Point(1,30));
		snake.add(new Point(0,30));
		direction = Direction.EAST;
		dropWall = new LinkedList<Point>();
		ver = new Point(0, 0);
		ver2 = new Point(0,0);
		Point a = new Point(7,31);
		dropWall.push(a);

	}
	
	public void GenerateDefaultSnake1()
	{
		score = 0;
		snake1.clear();
		snake1.add(new Point(1,3));
		snake1.add(new Point(1,2));
		snake1.add(new Point(1,1));
		direction1 = Direction1.NO_DIRECTION1;
	}
	
	public void snake1DropWall(Graphics g){
		
		Image hal = null;
		try
		{
			URL imagePath = snakeCanvas.class.getResource("block.png");
			hal= Toolkit.getDefaultToolkit().getImage(imagePath);
		}
		catch (Exception e)
		{
			//image not exist
			e.printStackTrace();
		}
		Point last;
		for(int i = 0; i < dropWall.size(); i++){
			last = dropWall.get(i);
			g.drawImage(hal, last.x * BOX_WIDTH, last.y * BOX_HEIGHT,BOX_WIDTH -1, BOX_HEIGHT-1,this);
		}
	}
	public void dropSnake1(){
		Point last = new Point(snake.getLast());
		dropWall.push(last);
		//snake.removeLast();
	}
	
	/*public void GenerateWall(){
		for(int i = 15; i < 45; i++){
			wall.add(new Point(i,5));
			if(i == 28)i++;
			if(i == 29)i++;
		}
		for(int i = 15; i < 45; i++){
			wall.add(new Point(i,25));
			if(i == 28)i++;
			if(i == 29)i++;
		}
		for(int i = 6; i < 25; i++){
			wall.add(new Point(15,i));
			if(i == 14)i++;
			if(i == 15)i++;
		}
		for(int i = 6; i < 25; i++){
			wall.add(new Point(44,i));
			if(i == 14)i++;
			if(i == 15)i++;
		}
	}
	*/
	public void Move()
	{
		if(this.direction == Direction.NO_DIRECTION)
			return;
		
		Point head = snake.peekFirst();
		Point newPoint = head;
		switch(direction)
		{
		case Direction.NORTH:
			newPoint = new Point(head.x, head.y-1);
			break;
		case Direction.SOUTH:
			newPoint = new Point(head.x, head.y + 1);
			break;
		case Direction.WEST:
			newPoint = new Point(head.x - 1, head.y);
			break;
		case Direction.EAST:
			newPoint = new Point(head.x + 1, head.y);
			break;
		
		}
		
		
		if (dropWall.peekFirst().x != 7)
		{
			isAtEndGame = true;
			CheckScore();
			//won = false;
			return;
		}
		else
			if (newPoint.x < 0 && newPoint.y != dropWall.peekFirst().y)
			{
				//we went obb, reset the game
				//CheckScore();
				//won = false;
				//isAtEndGame = true;
				//return;
				switch(direction)
				{
				case Direction.WEST:{
					//newPoint = new Point(0, head.y -1);
					direction = Direction.EAST;
					break;
				}
					
				}
				
				
			}
			else
				if (newPoint.x > (GRID_WIDTH) && newPoint.y != dropWall.peekFirst().y)
				{
					//we went obb, reset the game
					//CheckScore();
					//won = false;
					//isAtEndGame = true;
					//return;
					switch(direction)
					{
					case Direction.EAST:{
						//newPoint = new Point(0, head.y -1);
						direction = Direction.WEST;
						break;
					}
						
					}
					
					
				}
				else
			  if (newPoint.x < 0)
		{
			//we went obb, reset the game
			//CheckScore();
			//won = false;
			//isAtEndGame = true;
			//return;
			switch(direction)
			{
			case Direction.WEST:{
				newPoint = new Point(0, head.y -1);
				direction = Direction.NORTH;
				break;
			}
				
			}
			ver = new Point(0, newPoint.y);
			
			
		}
		else if (newPoint.x > (GRID_WIDTH))
		{
			//we went obb, reset the game
			//CheckScore();
			//won = false;
			//isAtEndGame = true;
			//return;
			switch(direction)
			{
			case Direction.EAST:{
				newPoint = new Point(head.x, head.y -1);
				direction = Direction.NORTH;
				break;
			}
				
			}
			ver2 = new Point(newPoint.x, newPoint.y);
			
			
		}
		else if (newPoint.y == ver2.y-1)
		{
			//we went obb, reset the game
			//CheckScore();
			//won = false;
			//isAtEndGame = true;
			//return;
			switch(direction)
			{
			case Direction.NORTH:{
				newPoint = new Point(head.x - 1, head.y);
				direction = Direction.WEST;
				break;
			}
				
			}
			ver2 = new Point(0, 0);
			
			
		}
		else if (newPoint.y == ver.y-1)
		{
			//we went obb, reset the game
			//CheckScore();
			//won = false;
			//isAtEndGame = true;
			//return;
			switch(direction)
			{
			case Direction.NORTH:{
				newPoint = new Point(head.x + 1, head.y);
				direction = Direction.EAST;
				break;
			}
				
			}
			ver = new Point(0, 0);
			
			
		}
		else if (newPoint.y < 0 || newPoint.y > (GRID_HEIGHT))
		{
			//we went obb, reset the game
			//CheckScore();
			//won = false;
			//isAtEndGame = true;
			//return;
			switch(direction)
			{
			case Direction.NORTH:
				newPoint = new Point(head.x+1, 0);
				break;
			case Direction.SOUTH:
				newPoint = new Point(head.x, 0);
				break;
				
			case Direction.NORTH_WEST:
				if (head.x > GRID_WIDTH/2)
					newPoint = new Point(GRID_WIDTH -1, GRID_WIDTH - head.x);
				else
					newPoint = new Point(GRID_HEIGHT + head.x, GRID_HEIGHT - 1);
				break;
			case Direction.SOUTH_WEST:
				if (head.x > GRID_WIDTH/2)
					newPoint = new Point(GRID_WIDTH -1, GRID_HEIGHT - (GRID_WIDTH - head.x));
				else
					newPoint = new Point(head.x + GRID_HEIGHT, 0);
				break;
			case Direction.NORTH_EAST:
				if (head.x < GRID_WIDTH/2)
					newPoint = new Point(0, head.x);
				else
					newPoint = new Point(head.x - GRID_HEIGHT+1 , GRID_HEIGHT - 1);
				break;
			case Direction.SOUTH_EAST:
				if (head.x < GRID_WIDTH/2)
					newPoint = new Point(0,GRID_HEIGHT-1 - head.x);
				else
					newPoint = new Point(head.x - GRID_HEIGHT , 0);
				break;
			}
		}
		
		snake.push(newPoint);
		if(this.direction != Direction.NO_DIRECTION)
			snake.remove(snake.peekLast());
	}
	
	public void Move1()
	{
		if(this.direction1 == Direction1.NO_DIRECTION1)
			return;
		
		Point head = snake1.peekFirst();
		Point newPoint = head;
		switch(direction1)
		{
		case Direction1.UP:
			newPoint = new Point(head.x, head.y - 1);
			break;
		case Direction1.DOWN:
			newPoint = new Point(head.x, head.y + 1);
			break;
		case Direction1.LEFT:
			newPoint = new Point(head.x - 1, head.y);
			break;
		case Direction1.RIGHT:
			newPoint = new Point(head.x + 1, head.y);
			break;
		}
		
		
		if (snake.size() == LIMIT-1 ||snake1.size() == LIMIT-1)
		{
			isAtEndGame = true;
			//won = false;
			return;
		}
		else
		if (snake.contains(newPoint)||dropWall.contains(newPoint))
		{
			for(int i = 0; i < snake1.size(); i++)
			{
				body1 = snake1.get(i);
				Sbody1.add(body1);
			}
			newPoint = new Point(1, 3);
			GenerateDefaultSnake1();
			snake1.remove(snake1.peekFirst());
			Sbdy1 = true;
		}
		else
			
			if (Sbody1.contains(newPoint))
			{
				Point temp;
				for(int i = 0; i < Sbody1.size(); i++ ){
					temp = Sbody1.get(i);
					if(temp.equals(newPoint)){
						snake1.push(temp);
						Sbody1.remove(temp);
					}	
				}
			}
			else
				
				if (Sbody.contains(newPoint))
				{
					Point temp;
					for(int i = 0; i < Sbody.size(); i++ ){
						temp = Sbody.get(i);
						if(temp.equals(newPoint)){
							snake1.push(temp);
							Sbody.remove(temp);
						}	
					}
				}
		else
			
			if (newPoint.equals(fruit)||newPoint.equals(fruit1))
		{
			//the snake hits the fruit
			score += 10;
			Point addPoint = (Point) newPoint.clone();
			
			switch(direction1)
			{
			case Direction1.UP:
				newPoint = new Point(head.x, head.y - 1);
				break;
			case Direction1.DOWN:
				newPoint = new Point(head.x, head.y + 1);
				break;
			case Direction1.LEFT:
				newPoint = new Point(head.x - 1, head.y);
				break;
			case Direction1.RIGHT:
				newPoint = new Point(head.x + 1, head.y);
				break;
			}
			snake1.push(addPoint);
			if (newPoint.equals(fruit))
				PlaceFruit();
			else
				PlaceFruit1();
		}
		else if (newPoint.x < 0 || newPoint.x > (GRID_WIDTH - 1))
		{
			//we went obb, reset the game
			//CheckScore();
			//won = false;
			//isAtEndGame = true;
			//return;
			switch(direction1)
			{
			case Direction1.LEFT:
				newPoint = new Point(GRID_WIDTH, head.y);
				break;
			case Direction1.RIGHT:
				newPoint = new Point(0, head.y);
				break;
			}
			
			
		}
		else if (newPoint.y < 0 || newPoint.y > (GRID_HEIGHT))
		{
			//we went obb, reset the game
			//CheckScore();
			//won = false;
			//isAtEndGame = true;
			//return;
			switch(direction1)
			{
			case Direction1.UP:
				newPoint = new Point(head.x, GRID_HEIGHT );
				break;
			case Direction1.DOWN:
				newPoint = new Point(head.x, 0);
				break;
			}
		}
		/*else if (snake1.contains(newPoint))
		{
			if(this.direction1 != Direction1.NO_DIRECTION1)
			{
				CheckScore();
				won = false;
				isAtEndGame = true;
				//we run into ourselves, reset the game
				return;
				
			}
			
		}
		*/
		
		
		//if we reach this point, we are still good
		
		snake1.push(newPoint);
		if(this.direction1 != Direction1.NO_DIRECTION1)
			snake1.remove(snake1.peekLast());
	}
	
	
	public void DrawScore(Graphics g)
	{
		
		g.setFont(new Font("Comic Sans MS", Font.PLAIN, 150)); 
		g.drawString("" + (dropWall.size()), 115, 140);
		g.drawString("Cobra: " + (snake.size()), 1050, 700);
		g.setColor(Color.BLACK);
		g.setFont(new Font("Comic Sans MS", Font.BOLD, 38)); 
		g.drawString("CLASSIC SNAKE.IO", 430, 700);
		//g.setColor(Color.CYAN);
		g.setFont(new Font("TimesRoman", Font.BOLD, 30)); 
		//g.drawString("Try to escape from each other", 400, 675);
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(4));
		g2.setColor(Color.black);
		g2.drawLine(3, 640, 300, 640);
		g2.drawLine(3, 5, 3, 640);
		g2.drawLine(3, 5, 300, 5);
		g2.drawLine(300, 5, 300, 640);
		g.drawString("Highscore: " + highScore, 0, BOX_HEIGHT * GRID_HEIGHT + 100); 
	}
	public void DrawOrder(Graphics g){
		g.setColor(Color.BLACK);
		g.setFont(new Font("Comic Sans MS", Font.PLAIN, 20)); 
		if(snake.size()<=snake1.size())
		{
			g.drawString("Trio: " + (snake1.size()), 1070, 30);
			g.drawString("Cobra: " + (snake.size()), 1070, 60);
		}
		else{
			g.drawString("Cobra: " + (snake.size()), 1070, 30);
			g.drawString("Trio: " + (snake1.size()), 1070, 60);
		}
		
	}
	/*
	public void DrawWall(Graphics g){
		Image hal = null;
		Point p;
		for (int i = 0; i < wall.size(); i++)
		{
			Image hol = null;
			p = wall.get(i);
			try
			{
				URL imagePath = snakeCanvas.class.getResource("wall.png");
				hol= Toolkit.getDefaultToolkit().getImage(imagePath);
			}
			catch (Exception e)
			{
				//image not exist
				e.printStackTrace();
			}
			
		
		
			g.drawImage(hol, p.x * BOX_WIDTH, p.y * BOX_HEIGHT, BOX_WIDTH-5, BOX_HEIGHT-5,this);
			
			//p = snake.get(i);
			//g.fillRect(p.x * BOX_WIDTH, p.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
		}
	}
	*/
	public void DrawEndGame(Graphics g)
	{
		try
		{
			URL imagePath = snakeCanvas.class.getResource("end.jpg");
			this.menuImage = ImageIO.read( imagePath );

		}
		catch (Exception e)
		{
			//image not exist
			e.printStackTrace();
		}
		
	
	
	g.drawImage(menuImage, 0, 0, 600, 600, this);
	g.setColor(Color.GREEN);
	g.setFont(new Font("TimesRoman", Font.PLAIN, 38)); 
		if(snake1.size() < snake.size()){
			g.drawString("Right Snake won!",150, 250);
			g.drawString("It's score: " + (snake.size()+1 ), 150, 300);
		}
		else{
			g.drawString("Left Snake won!",150, 250);
			g.drawString("It's score: " + (snake1.size()+1 ), 150, 300);
		}
		
		


	}
	
	public void CheckScore()
	{
		if(highScore.equals(""))
			return;
		if (dropWall.size()  > Integer.parseInt((highScore)))
		{
			highScore =  ""+(dropWall.size()-1);
			
			File scoreFile = new File("highscore.dat");
			if (!scoreFile.exists())
			{
				try{
					scoreFile.createNewFile();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
			FileWriter writeFile = null;
			BufferedWriter writer = null;
			try
			{
				writeFile = new FileWriter(scoreFile);
				writer = new BufferedWriter(writeFile);
				writer.write(this.highScore);
			}
			catch (Exception e)
			{
				//errors
			}
			finally
			{
				try 
				{
					if (writer != null)
						writer.close();
				}
				catch(Exception e){} 
			}
			
		}
	}
	
	public void DrawGrid(Graphics g)
	{
		

		Image hal = null;
		try
		{
			URL imagePath = snakeCanvas.class.getResource("lolas.png");
			hal= Toolkit.getDefaultToolkit().getImage(imagePath);
		}
		catch (Exception e)
		{
			//image not exist
			e.printStackTrace();
		}
		
	
	
		g.drawImage(hal, 0, 0, 300, 750,this);
	}
	
	public void DrawSnake(Graphics g)
	{	
		
		Image hal = null;
		Point p = snake.get(0);
		try
		{
			URL imagePath = snakeCanvas.class.getResource("block1.png");
			hal= Toolkit.getDefaultToolkit().getImage(imagePath);
		}
		catch (Exception e)
		{
			//image not exist
			e.printStackTrace();
		}
		
		//g.setColor(Color.BLUE);
		//g.fillRect(p.x * BOX_WIDTH, p.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
		g.setColor(Color.BLACK);
		g.drawImage(hal, p.x * BOX_WIDTH, p.y * BOX_HEIGHT, BOX_WIDTH-2, BOX_HEIGHT-2,this);
		for (int i = 1; i < snake.size(); i++)
		{
			Image hol = null;
			p = snake.get(i);
			try
			{
				URL imagePath = snakeCanvas.class.getResource("block.png");
				hol= Toolkit.getDefaultToolkit().getImage(imagePath);
			}
			catch (Exception e)
			{
				//image not exist
				e.printStackTrace();
			}
			
		
		
			g.drawImage(hol, p.x * BOX_WIDTH, p.y * BOX_HEIGHT, BOX_WIDTH-2, BOX_HEIGHT-2,this);
			
			//p = snake.get(i);
			//g.fillRect(p.x * BOX_WIDTH, p.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
		}
		
	}
	
	public void DrawSnake1(Graphics g)
	{
		Image hal = null;
		Point p = snake1.get(0);
		try
		{
			URL imagePath = snakeCanvas.class.getResource("head2.png");
			hal= Toolkit.getDefaultToolkit().getImage(imagePath);
		}
		catch (Exception e)
		{
			//image not exist
			e.printStackTrace();
		}
		
	
	
		g.drawImage(hal, p.x * BOX_WIDTH, p.y * BOX_HEIGHT, BOX_WIDTH-1, BOX_HEIGHT-1,this);
		for (int i = 1; i < snake1.size(); i++)
		{
			Image hol = null;
			p = snake1.get(i);
			try
			{
				URL imagePath = snakeCanvas.class.getResource("body2.png");
				hol= Toolkit.getDefaultToolkit().getImage(imagePath);
			}
			catch (Exception e)
			{
				//image not exist
				e.printStackTrace();
			}
			
		
		
			g.drawImage(hol, p.x * BOX_WIDTH, p.y * BOX_HEIGHT, BOX_WIDTH-1, BOX_HEIGHT-1,this);
		}
		g.setColor(Color.BLACK);
	}
	
	public void DrawSbody(Graphics g){
		Image hal = null;
		try
		{
			URL imagePath = snakeCanvas.class.getResource("fruit.png");
			hal= Toolkit.getDefaultToolkit().getImage(imagePath);
		}
		catch (Exception e)
		{
			//image not exist
			e.printStackTrace();
		}
		Point temp = new Point();
		for(int i = 0; i < Sbody.size(); i++){
			temp = Sbody.get(i);
			g.drawImage(hal, temp.x * BOX_WIDTH, temp.y * BOX_HEIGHT,BOX_WIDTH -1, BOX_HEIGHT-1,this);
			
		}
		
		
	}
	public void DrawSbody1(Graphics g){
		Image hal = null;
		try
		{
			URL imagePath = snakeCanvas.class.getResource("fruit.png");
			hal= Toolkit.getDefaultToolkit().getImage(imagePath);
		}
		catch (Exception e)
		{
			//image not exist
			e.printStackTrace();
		}
		Point temp = new Point();
		for(int i = 0; i < Sbody1.size(); i++){
			temp = Sbody1.get(i);
			g.drawImage(hal, temp.x * BOX_WIDTH, temp.y * BOX_HEIGHT,BOX_WIDTH -1, BOX_HEIGHT-1,this);
			
		}
		
		
	}
	public void DrawFruit(Graphics g)
	{
		Image hal = null;
		try
		{
			URL imagePath = snakeCanvas.class.getResource("fruit.png");
			hal= Toolkit.getDefaultToolkit().getImage(imagePath);
		}
		catch (Exception e)
		{
			//image not exist
			e.printStackTrace();
		}
		
	
	   
		   
		   g.drawImage(hal, fruit.x * BOX_WIDTH, fruit.y * BOX_HEIGHT,BOX_WIDTH -1, BOX_HEIGHT-1,this);
	  
		
		//g.fillOval(fruit.x * BOX_WIDTH, fruit.y * BOX_HEIGHT,BOX_WIDTH, BOX_HEIGHT);
		//g.setColor(Color.GRAY);
	}
	
	public void DrawFruit1(Graphics g)
	{
		Image hal = null;
		try
		{
			URL imagePath = snakeCanvas.class.getResource("fruit.png");
			hal= Toolkit.getDefaultToolkit().getImage(imagePath);
		}
		catch (Exception e)
		{
			//image not exist
			e.printStackTrace();
		}
		
	
	   
		   
		   g.drawImage(hal, fruit1.x * BOX_WIDTH, fruit1.y * BOX_HEIGHT,BOX_WIDTH -1, BOX_HEIGHT-1,this);
	  
		
		//g.fillOval(fruit.x * BOX_WIDTH, fruit.y * BOX_HEIGHT,BOX_WIDTH, BOX_HEIGHT);
		//g.setColor(Color.GRAY);
	}
	
	
	public void PlaceFruit()
	{
		Random rand = new Random();
		int randomX = rand.nextInt(GRID_WIDTH-1);
		int randomY = rand.nextInt(GRID_HEIGHT-1);
		Point randomPoint = new Point(randomX, randomY);
		while (snake.contains(randomPoint)||snake1.contains(randomPoint))
		{
			randomX = rand.nextInt(GRID_WIDTH-1);
			randomY = rand.nextInt(GRID_HEIGHT-1);
			randomPoint = new Point(randomX, randomY);
		}
		fruit = randomPoint;
	}
	
	public void PlaceFruit1()
	{
		Random rand = new Random();
		int randomX = rand.nextInt(GRID_WIDTH-1);
		int randomY = rand.nextInt(GRID_HEIGHT-1);
		Point randomPoint = new Point(randomX, randomY);
		while (snake.contains(randomPoint)||snake1.contains(randomPoint))
		{
			randomX = rand.nextInt(GRID_WIDTH-1);
			randomY = rand.nextInt(GRID_HEIGHT-1);
			randomPoint = new Point(randomX, randomY);
		}
		fruit1 = randomPoint;
	}

	@Override
	public void run() 
	{
		//runs infinitely
		while(true)
		{
			
			
			if(!isInMenu && !isAtEndGame){
				Move1();
				Move();
				
				if(add < dropWall.size()){
					add = add + 2;
					speed = speed-15;
				}
				
			}
			
			try
			{
				Thread.currentThread();
				
				Thread.sleep(speed);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			repaint();
		}
		
		
	}
	
	public String GetHighScoreValue()
	{
		//format: brandon: 100
		FileReader readFile = null;
		BufferedReader reader = null;
		try
		{
			readFile = new FileReader("highscore.dat");
			reader = new BufferedReader(readFile);
			return reader.readLine();
		}
		catch (Exception e)
		{
			return "0";
		}
		finally
		{
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_UP:
			if (direction != Direction.SOUTH)
				direction = Direction.NORTH;
			break;
		case KeyEvent.VK_DOWN:
			if (direction != Direction.NORTH)
				direction = Direction.SOUTH;
			break;
		case KeyEvent.VK_RIGHT:
			if (direction != Direction.WEST)
				direction = Direction.EAST;
			break;
		case KeyEvent.VK_LEFT:
			if (direction != Direction.EAST)
				direction = Direction.WEST;
			break;
			
		case KeyEvent.VK_O:
			if (direction != Direction.SOUTH_WEST)
				direction = Direction.NORTH_EAST;
			break;
		case KeyEvent.VK_I:
			if (direction != Direction.SOUTH_EAST)
				direction = Direction.NORTH_WEST;
			break;
		case KeyEvent.VK_L:
			if (direction != Direction.NORTH_WEST)
				direction = Direction.SOUTH_EAST;
			break;
		case KeyEvent.VK_K:
			if (direction != Direction.NORTH_EAST)
				direction = Direction.SOUTH_WEST;
			break;
		case KeyEvent.VK_SPACE:
				dropSnake1();
			break;
			
			
		case KeyEvent.VK_W:
			if (direction1 != Direction1.DOWN)
				direction1 = Direction1.UP;
			break;
		case KeyEvent.VK_S:
			if (direction1 != Direction1.UP)
				direction1 = Direction1.DOWN;
			break;
		case KeyEvent.VK_D:
			if (direction1 != Direction1.LEFT)
				direction1 = Direction1.RIGHT;
			break;
		case KeyEvent.VK_A:
			if (direction1 != Direction1.RIGHT)
				direction1 = Direction1.LEFT;
			break;
			
			
			
		case KeyEvent.VK_ENTER:
			if(isInMenu){
				isInMenu = false;
				repaint();
			}
			break;
		case KeyEvent.VK_G:
			if(isAtEndGame)
			{
				isAtEndGame = false;
				//won = false;
				dropWall=null;
				GenerateDefaultSnake();
				speed = 150;
				add = 5;
				direction = Direction.EAST;
				repaint();
			}
			break;	
		 
		case KeyEvent.VK_ESCAPE:
			isInMenu = true;
			break;
		
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}

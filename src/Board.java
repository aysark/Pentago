import java.awt.*;

import javax.swing.*;

public class Board
{
	Component mainWindow;
	Rectangle[] quadrants;

	private Rectangle[][] positions;
	private Image[] pieceImages;
	private Image[] rollOverImages;
	public int[][] board;

	private boolean currentSpot;

	private final int BORDER = 73;
	private final int TOP_OFFSET = 95;
	private final int POS_SIZE = 50;
	private final int BLACK = 1;
	private final int WHITE = -1;
	private final int RIGHT = 1;

	private int width;
	private int height;

	public Board(Component window)
	{
		mainWindow = window;
		board = new int[6][6];
		positions = new Rectangle[6][6];

		pieceImages = new Image[6];
		pieceImages[0] = new ImageIcon("images/v2player2PieceQ1&4.png")
				.getImage();
		pieceImages[1] = new ImageIcon("images/v2player2PieceQ2&3.png")
				.getImage();
		pieceImages[2] = new ImageIcon("images/emptyPieceQ1&4.png").getImage();
		pieceImages[3] = new ImageIcon("images/emptyPieceQ2&3.png").getImage();
		pieceImages[4] = new ImageIcon("images/v2playerPieceQ1&4.png")
				.getImage();
		pieceImages[5] = new ImageIcon("images/v2playerPieceQ2&3.png")
				.getImage();

		rollOverImages = new Image[2];
		rollOverImages[0] = new ImageIcon("images/emptyPieceQ1&4_R.png")
				.getImage();
		rollOverImages[1] = new ImageIcon("images/emptyPieceQ2&3_R.png")
				.getImage();

		width = pieceImages[2].getWidth(mainWindow) + 5;
		height = pieceImages[2].getHeight(mainWindow) + 5;

		quadrants = new Rectangle[4];
		quadrants[0] = new Rectangle(53, TOP_OFFSET, (3 * width) - 5,
				(3 * height) - 5);
		quadrants[1] = new Rectangle(53 + ((3 * width) - 5) + 25, TOP_OFFSET,
				(3 * width) - 5, (3 * height) - 5);
		quadrants[2] = new Rectangle(53, (TOP_OFFSET + (3 * height) + 20),
				3 * width - 5, (3 * height) - 5);
		quadrants[3] = new Rectangle(53 + ((3 * width) - 5) + 25, (TOP_OFFSET
				+ (3 * height) + 20), (3 * width) - 5, (3 * width) - 5);

		for (int row = 0; row < positions.length; row++)
			for (int col = 0; col < positions[row].length; col++)
			{
				int nextX = col * width + BORDER;
				int nextY = height * row + TOP_OFFSET;

				if (row < 3)
				{
					if (col < 3)
					{
						nextX -= 20;
						positions[row][col] = new Rectangle(nextX, nextY,
								width - 5, height - 5);
					}
					else
						positions[row][col] = new Rectangle(nextX, nextY,
								width - 5, height - 5);
				}
				else
				{
					nextY += 20;
					if (col < 3)
					{
						nextX -= 20;
						positions[row][col] = new Rectangle(nextX, nextY,
								width - 5, height - 5);
					}
					else
					{
						positions[row][col] = new Rectangle(nextX, nextY,
								width - 5, height - 5);
					}
				}

				board[row][col] = 0;
			}

		currentSpot = false;

	}

	public int selectedQuadrant(Point point)
	{
		for (int currentQuad = 0; currentQuad < quadrants.length; currentQuad++)
		{
			if (quadrants[currentQuad].contains(point))
				return currentQuad;
		}
		return -1;
	}

	public Point getCentre(int rotatingQuadrant)
	{
		Point centre = new Point(quadrants[rotatingQuadrant].x
				+ quadrants[rotatingQuadrant].width / 2,
				quadrants[rotatingQuadrant].y
						+ quadrants[rotatingQuadrant].height / 2);

		return centre;
	}

	public boolean makeMove(Point pos, int player)
	{
		for (int row = 0; row < positions.length; row++)
			for (int col = 0; col < positions[row].length; col++)
			{
				if (positions[row][col].contains(pos) && board[row][col] == 2)
				{
					board[row][col] = player;
					return true;
				}
			}
		return false;
	}

	public void makeOppenentMove(Point pos, int currentPlayer)
	{
		for (int row = 0; row < positions.length; row++)
			for (int col = 0; col < positions[row].length; col++)
			{
				if (positions[row][col].contains(pos))
				{
					board[row][col] = currentPlayer;
					return;
				}
			}

	}

	public boolean showCurrentPos(Point pos)
	{
		for (int row = 0; row < positions.length; row++)
			for (int col = 0; col < positions[row].length; col++)
			{
				if (!positions[row][col].contains(pos) && board[row][col] == 2)
					board[row][col] = 0;

				else if (positions[row][col].contains(pos)
						&& board[row][col] == 0)
				{
					board[row][col] = 2;
					return true;
				}
			}
		return false;
	}

	public boolean isFilled(Point currentPos)
	{
		for (int row = 0; row < board.length; row++)
			for (int col = 0; col < board[row].length; col++)
			{
				if (positions[row][col].contains(currentPos)
						&& board[row][col] != 0 && board[row][col] != 2)
					return true;
			}
		return false;
	}

	public void rotate(int quad, int direction)
	{

		int[] quadCopy = new int[9];

		int nextRow = 0;
		int nextCol = 0;

		if (quad == 1)
			nextCol = 3;
		else if (quad == 2)
			nextRow = 3;
		else if (quad == 3)
		{
			nextRow = 3;
			nextCol = 3;
		}

		int index = 0;
		for (int row = nextRow; row < (nextRow + 3); row++)
			for (int col = nextCol; col < (nextCol + 3); col++)
			{
				quadCopy[index] = board[row][col];
				index++;
			}

		index = 0;

		if (direction == RIGHT)
		{
			for (int col = (nextCol + 2); col >= nextCol; col--)
				for (int row = nextRow; row < (nextRow + 3); row++)
				{
					board[row][col] = quadCopy[index];
					index++;
				}

		}
		else
		{
			for (int col = nextCol; col < nextCol + 3; col++)
				for (int row = nextRow + 2; row >= nextRow; row--)
				{
					board[row][col] = quadCopy[index];
					index++;
				}
		}

	}

	public boolean isOnGrid(int[][] borad, int row, int col)
	{
		if (row < board.length && col < board[0].length && row >= 0 && col >= 0)
			return true;
		return false;
	}

	public boolean checkForWinner(int currentPlayer)
	{
		for (int row = 0; row < board.length; row++)
			for (int col = 0; col < board[0].length; col++)
				for (int dy = -1; dy <= 1; dy++)
					for (int dx = -1; dx <= 1; dx++)
					{
						int player = board[row][col];
						if ((dy != 0 || dx != 0) && player != 0)
						{

							int checkRow = row + dy;
							int checkCol = col + dx;
							int index = 1;

							while (isOnGrid(board, checkRow, checkCol)
									&& index < 5
									&& board[checkRow][checkCol] == player)
							{
								index++;
								checkRow += dy;
								checkCol += dx;
							}
							if (index >= 4)
								if (index >= 5)
									return true;
						}
					}
		return false;

	}

	public boolean tie()
	{
		for (int row = 0; row < board.length; row++)
		{
			for (int col = 0; col < board[row].length; col++)
			{
				if (board[row][col] == 0)
					return false;
			}
		}
		return true;
	}

	public boolean tie2()
	{
		if (checkForWinner(1) && checkForWinner(-1))
			return true;
		return false;
	}

	public void draw(Graphics2D g, int rotatingQuadrant, int angle)
	{
		g.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON));

		for (int row = 0; row < 6; row++)
			for (int col = 0; col < 6; col++)
			{
				int nextX = col * width + BORDER;
				int nextY = height * row + TOP_OFFSET;

				if (rotatingQuadrant == -1
						|| !quadrants[rotatingQuadrant].contains(new Point(
								nextX + 20, nextY + 20)))
				{

					if (row < 3)
					{
						if (col < 3)
						{
							nextX -= 20;
							g.drawImage(pieceImages[2], nextX, nextY,
									width - 5, height - 5, mainWindow);
						}
						else
						{
							g.drawImage(pieceImages[3], nextX, nextY,
									width - 5, height - 5, mainWindow);
						}
					}
					else
					{
						nextY += 20;
						if (col < 3)
						{
							nextX -= 20;
							g.drawImage(pieceImages[3], nextX, nextY,
									width - 5, height - 5, mainWindow);
						}
						else
						{
							g.drawImage(pieceImages[2], nextX, nextY,
									width - 5, height - 5, mainWindow);
						}
					}

					if (currentSpot)
					{
						// g.setColor(Color.black);
						// g.fillOval(nextX + 5, nextY + 5, POS_SIZE - 10,
						// POS_SIZE - 10);
						if (((col < 3) && (row < 3))
								|| ((col > 2) && (row > 2)))
						{
							g.drawImage(rollOverImages[0], nextX, nextY, 50,
									50, mainWindow);
						}
						else
						{
							g.drawImage(rollOverImages[1], nextX, nextY, 50,
									50, mainWindow);
						}
						// g.setColor(Color.cyan);
						// g.fillRoundRect(nextX, nextY, width, height, 25, 25);
					}

					if (board[row][col] == BLACK)
					{
						if (((col < 3) && (row < 3))
								|| ((col > 2) && (row > 2)))
						{
							g.drawImage(pieceImages[4], nextX, nextY, 50, 50,
									mainWindow);
						}
						else
						{
							g.drawImage(pieceImages[5], nextX, nextY, 50, 50,
									mainWindow);
						}
						// g.setColor(Color.black);
						// g.fillOval(nextX + 5, nextY + 5, POS_SIZE - 10,
						// POS_SIZE - 10);
					}
					else if (board[row][col] == WHITE)
					{
						if (((col < 3) && (row < 3))
								|| ((col > 2) && (row > 2)))
						{
							g.drawImage(pieceImages[0], nextX, nextY, 50, 50,
									mainWindow);
						}
						else
						{
							g.drawImage(pieceImages[1], nextX, nextY, 50, 50,
									mainWindow);
						}
						// g.setColor(Color.white);
						// g.fillOval(nextX + 5, nextY + 5, POS_SIZE - 10,
						// POS_SIZE - 10);
					}
					else if (board[row][col] == 2)
					{
						if (((col < 3) && (row < 3))
								|| ((col > 2) && (row > 2)))
						{
							g.drawImage(rollOverImages[0], nextX, nextY, 50,
									50, mainWindow);
						}
						else
						{
							g.drawImage(rollOverImages[1], nextX, nextY, 50,
									50, mainWindow);
						}
						// g.setColor(Color.red);
						// g.fillRect(nextX, nextY, POS_SIZE, POS_SIZE);

						g.setColor(new Color(68, 57, 41));
						g.drawRect(nextX, nextY, POS_SIZE, POS_SIZE);

					}
				}
			}

		if (rotatingQuadrant != -1)
		{
			Point centre = new Point(quadrants[rotatingQuadrant].x
					+ quadrants[rotatingQuadrant].width / 2,
					quadrants[rotatingQuadrant].y
							+ quadrants[rotatingQuadrant].height / 2);
			g.rotate(Math.toRadians(angle), centre.x, centre.y);

			for (int row = 0; row < 6; row++)
				for (int col = 0; col < 6; col++)
				{
					int nextX = col * width + BORDER;
					int nextY = height * row + TOP_OFFSET;

					if (quadrants[rotatingQuadrant].contains(new Point(
							nextX + 20, nextY + 20)))
					{
						if (row < 3)
						{
							if (col < 3)
							{
								nextX -= 20;
								g.drawImage(rollOverImages[0], nextX, nextY,
										width - 5, height - 5, mainWindow);
							}
							else
								g.drawImage(rollOverImages[1], nextX, nextY,
										width - 5, height - 5, mainWindow);
						}
						else
						{
							nextY += 20;
							if (col < 3)
							{
								nextX -= 20;
								g.drawImage(rollOverImages[1], nextX, nextY,
										width - 5, height - 5, mainWindow);
							}
							else
							{
								g.drawImage(rollOverImages[0], nextX, nextY,
										width - 5, height - 5, mainWindow);
							}
						}

						if (board[row][col] == BLACK)
						{
							g.drawImage(pieceImages[4], nextX, nextY, 50, 50,
									mainWindow);
							// g.setColor(Color.black);
							// g.fillOval(nextX + 5, nextY + 5, POS_SIZE - 10,
							// POS_SIZE - 10);
						}
						else if (board[row][col] == WHITE)
						{
							g.drawImage(pieceImages[0], nextX, nextY, 50, 50,
									mainWindow);
							// g.setColor(Color.white);
							// g.fillOval(nextX + 5, nextY + 5, POS_SIZE - 10,
							// POS_SIZE - 10);
						}
					}

				}
		}
		// g.setColor(Color.WHITE);
		// g.drawRect(quadrants[3].x,
		// quadrants[3].y,quadrants[3].width,quadrants[3].height);
	}

}

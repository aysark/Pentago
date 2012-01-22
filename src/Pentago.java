import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import java.util.*;
import java.io.*;
import java.net.*;
import java.applet.*;

public class Pentago extends JFrame implements Runnable
{

	private DrawingPanel drawingArea;

	private Board gameBoard;

	private int currentPlayer;

	private int rotatingQuad;

	private int direction;

	private int angle;

	private int gameType; // game type possible values: 0 (pVp), 1 (pVc),2(pVp

	private int gameEndStatus; // 0 = win, 1=loss,2=tie

	private int rotationCount;

	private boolean needToRotate;

	private boolean gameOver;

	private boolean gameOn;

	private boolean quickPlay;

	private boolean connected;

	private boolean profileLoaded;

	private boolean myMove;

	public boolean joined;

	private String playerName;

	private String gameStatus;

	private Point start;

	private Profile player1;

	private Profile player2;

	private Profile curntPlayer;

	private Image[] images;

	private Image[] gameOverImages;

	private Image[] gameTipsterImages;

	private Image quads;

	private Image quads2;

	private final int BLACK = 1;

	private final int WHITE = -1;

	private final int RIGHT = 1;

	private final int LEFT = -1;

	private final int NO_ROTATING_QUAD = -1;

	private final int PORTNUMBER = 4587;

	// Sound FX
	private AudioClip rotateSound;

	private AudioClip moveSound;

	private AudioClip winningSound;

	private AudioClip badMoveSound;

	private AudioClip introSound;

	private Thread lookForInput;

	private ServerSocket serverSocket;

	private Socket clientSocket;

	private InetAddress inet;

	private PrintWriter out;

	private BufferedReader in;

	public Pentago()
	{
		super("Pentago");
		setLocation(120, 20);
		setIconImage(new ImageIcon("gameIcon.png").getImage());
		drawingArea = new DrawingPanel();
		add(drawingArea, BorderLayout.CENTER);

		introSound = Applet
				.newAudioClip(getCompleteURL("sounds/fx/Startup.wav"));
		rotateSound = Applet
				.newAudioClip(getCompleteURL("sounds/fx/swing.wav"));
		moveSound = Applet.newAudioClip(getCompleteURL("sounds/fx/blip.wav"));
		winningSound = Applet
				.newAudioClip(getCompleteURL("sounds/fx/winner.wav"));
		badMoveSound = Applet
				.newAudioClip(getCompleteURL("sounds/fx/badMove.wav"));

		images = new Image[14];
		images[0] = new ImageIcon("images/pentagoMainBG.jpg").getImage();
		images[1] = new ImageIcon("images/boardBG.png").getImage();
		images[2] = new ImageIcon("images/playerConsole.png").getImage();
		images[3] = new ImageIcon("images/gameConsole.png").getImage();
		images[4] = new ImageIcon("images/gameTipster10.png").getImage();
		images[5] = new ImageIcon("images/noProfileLoaded.png").getImage();
		images[6] = new ImageIcon("images/avatarMalePlayer.png").getImage();
		images[7] = new ImageIcon("images/avatarFemalePlayer.png").getImage();
		images[8] = new ImageIcon("images/tupperware.png").getImage();
		images[9] = new ImageIcon("images/user_green.png").getImage();
		images[10] = new ImageIcon("images/user_blue.png").getImage();
		images[11] = new ImageIcon("images/user.png").getImage();
		images[12] = new ImageIcon("images/help.png").getImage();
		images[13] = new ImageIcon("images/avatarAI.png").getImage();

		quads = new ImageIcon("images/Quadrant1&4BG.png").getImage();
		quads2 = new ImageIcon("images/Quadrant2&3BG.png").getImage();

		gameOverImages = new Image[3];
		gameOverImages[0] = new ImageIcon("images/youWin.jpg").getImage();
		gameOverImages[1] = new ImageIcon("images/youLose.jpg").getImage();
		gameOverImages[2] = new ImageIcon("images/youTied.jpg").getImage();

		gameTipsterImages = new Image[10];
		gameTipsterImages[0] = new ImageIcon("images/gameTipster.png")
				.getImage();
		gameTipsterImages[1] = new ImageIcon("images/gameTipster2.png")
				.getImage();
		gameTipsterImages[2] = new ImageIcon("images/gameTipster3.png")
				.getImage();
		gameTipsterImages[3] = new ImageIcon("images/gameTipster4.png")
				.getImage();
		gameTipsterImages[4] = new ImageIcon("images/gameTipster5.png")
				.getImage();
		gameTipsterImages[5] = new ImageIcon("images/gameTipster6.png")
				.getImage();
		gameTipsterImages[6] = new ImageIcon("images/gameTipster7.png")
				.getImage();
		gameTipsterImages[7] = new ImageIcon("images/gameTipster8.png")
				.getImage();
		gameTipsterImages[8] = new ImageIcon("images/gameTipster9.png")
				.getImage();
		gameTipsterImages[9] = new ImageIcon("images/gameTipster10.png")
				.getImage();

		myMove = false;
		lookForInput = null;
		serverSocket = null;
		clientSocket = null;

		profileLoaded = false;
		connected = false;
		gameOn = false;
		gameOver = false;
		quickPlay = false;
		addMenus();
	}

	public URL getCompleteURL(String fileName)
	{
		try
		{
			return new URL("file:" + System.getProperty("user.dir") + "/"
					+ fileName);
		}
		catch (MalformedURLException e)
		{
			System.err.println(e.getMessage());
		}
		return null;
	}

	public void showStatus(String nextMessage)
	{
		this.gameStatus = nextMessage;
	}

	public void newGame()
	{
		gameBoard = new Board(this);
		rotatingQuad = NO_ROTATING_QUAD;
		currentPlayer = BLACK;
		curntPlayer = player1;
		direction = RIGHT;

		if (!quickPlay && !connected)
		{
			gameStatus = "Game Initialized, " + curntPlayer.playerName
					+ "\n go ahead.";
		}
		else
			gameStatus = "Game Initialized, " + "Player One...";

		needToRotate = false;
		gameOver = false;
		profileLoaded = true;
		gameOn = true;
		angle = 0;
		introSound.play();

	}

	public void addMenus()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");

		JMenuItem quickGameOption = new JMenuItem("Quick Game");
		quickGameOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evnt)
			{
				quickPlay = true;
				newGame();
			}
		});

		JMenuItem newGameOption = new JMenuItem("New Game");
		newGameOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK));
		newGameOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evnt)
			{

				gameType = 0;

				String[] choices = { "New Profile", "Existing Profile",
						"Cancel" };

				int profile = JOptionPane
						.showOptionDialog(
								drawingArea,
								"Would you like to create a new profile or load up an existing one ?",
								"Load a Profile or Create One of You Own",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE, null, choices, null);

				if (profile == 0)
				{
					if (!profileLoaded)
					{
						Object returnedInput = JOptionPane
								.showInputDialog("Please enter the name of your profile:");

						if (returnedInput != null)
						{
							playerName = returnedInput.toString().trim();

							while ((checkProfileName(playerName))
									|| (playerName.length() >= 20)
									|| (playerName.length() < 2))
							{
								JOptionPane
										.showMessageDialog(
												drawingArea,
												"You did not successfully create your profile; due to one of the following reasons:"
														+ "\n 1. You did not enter a name."
														+ "\n 2. Your profile name was too short, it must be 4 or more letters long."
														+ "\n 3. Your profile name was too long, it much not exceed 18 letters long."
														+ "\n 4. Your profile name is taken.");
								returnedInput = JOptionPane
										.showInputDialog("Please enter the name of your profile:");

								if (returnedInput == null)
									playerName = "Noname"
											+ (Math.random() + 100);
								else
								{
									playerName = returnedInput.toString();
								}
							}

							String[] possibleValues2 = { "Male", "Female",
									"Artificial Intelligence", "A Container",
									"Green", "Blue", "Yellow",
									"I need help picking..." };
							Object avatar = JOptionPane.showInputDialog(null,
									"Choose what best describes you...",
									"Input", JOptionPane.INFORMATION_MESSAGE,
									null, possibleValues2, possibleValues2[0]);

							if (avatar != null)
							{
								int avatarIcon = 0;

								if (avatar == null)
									avatarIcon = 0;
								else if (avatar.equals("Male"))
									avatarIcon = 0;
								else if (avatar.equals("Female"))
									avatarIcon = 1;
								else if (avatar
										.equals("Artificial Intelligence"))
									avatarIcon = 2;
								else if (avatar.equals("A Container"))
									avatarIcon = 3;
								else if (avatar.equals("Green"))
									avatarIcon = 4;
								else if (avatar.equals("Blue"))
									avatarIcon = 5;
								else if (avatar.equals("Yellow"))
									avatarIcon = 6;
								else
									avatarIcon = 7;

								player1 = new Profile(playerName, avatarIcon);
								curntPlayer = player1;
								saveProfile(player1, true);
								profileLoaded = true;
								repaint();
							}
						}
					}
					else
					{
						JOptionPane
								.showMessageDialog(
										drawingArea,
										"You must quit the current loaded profile to create a new one.",
										"Can't do that...",
										JOptionPane.ERROR_MESSAGE);
					}

				}
				else if (profile == 1)
				{
					if (!profileLoaded)
					{
						player1 = null;

						Object returnedInput = JOptionPane
								.showInputDialog("Please enter the name of your profile:");

						if (returnedInput != null)
						{
							playerName = returnedInput.toString().trim();

							if ((playerName.length() >= 19)
									|| (playerName.length() <= 2))
							{
								JOptionPane
										.showMessageDialog(drawingArea,
												"Sorry! There is no profile by that name.");
							}
							else
							{
								try
								{
									Scanner fileScanner = new Scanner(new File(
											"players.txt"));
									while ((fileScanner.hasNext())
											&& (!profileLoaded))
									{
										String line = fileScanner.nextLine();
										if (line.equals(playerName))
										{
											int wins = fileScanner.nextInt();
											int losses = fileScanner.nextInt();
											int ties = fileScanner.nextInt();
											int avatar = fileScanner.nextInt();
											int bestTime = fileScanner
													.nextInt();

											player1 = new Profile(playerName,
													wins, losses, ties, avatar,
													bestTime);
											JOptionPane.showMessageDialog(
													drawingArea,
													"Success! Profile loaded.");
											curntPlayer = player1;
											profileLoaded = true;
											repaint();
										}
									}
									if (player1 == null)
									{
										JOptionPane
												.showMessageDialog(drawingArea,
														"Sorry! There is no profile by that name.");
									}
								}
								catch (FileNotFoundException e)
								{
									System.out.println(e);
								}
							}
						}
					}
					else
					{

						JOptionPane
								.showMessageDialog(
										drawingArea,
										"You must quit the current loaded profile to create a new one.",
										"Can't do that...",
										JOptionPane.ERROR_MESSAGE);
					}

				}
				else if (profile == 2)
				{
					JOptionPane.showMessageDialog(drawingArea,
							"You must either load an exisitng profile or make a new one before"
									+ "\na new game", "No Profile Loaded",
							JOptionPane.ERROR_MESSAGE);

				}

				JOptionPane
						.showMessageDialog(
								drawingArea,
								"You have chosen to play a PvP on the same computer, \r\nplease allow player 2 to create/load his/her profile now.");

				// ask if they would like to load/create a profile
				int answer = JOptionPane
						.showConfirmDialog(
								null,
								"Player 2, would you like to load an existing profile?",
								"Played here before?",
								JOptionPane.YES_NO_OPTION);

				if (answer == JOptionPane.YES_OPTION)
				{

					player2 = null;
					boolean profile2Loaded = false;
					Object returnedInput = JOptionPane
							.showInputDialog("Please enter the name of your profile:");

					if ((returnedInput != null)
							&& (!returnedInput.equals(curntPlayer.playerName)))
					{
						playerName = returnedInput.toString().trim();

						if ((playerName.length() >= 19)
								|| (playerName.length() <= 2))
						{
							JOptionPane.showMessageDialog(drawingArea,
									"Sorry! There is no profile by that name.");
						}
						else
						{
							try
							{
								Scanner fileScanner = new Scanner(new File(
										"players.txt"));
								while ((fileScanner.hasNext())
										&& (!profile2Loaded))
								{
									String line = fileScanner.nextLine();
									if (line.equals(playerName))
									{
										int wins = fileScanner.nextInt();
										int losses = fileScanner.nextInt();
										int ties = fileScanner.nextInt();
										int avatar = fileScanner.nextInt();
										int bestTime = fileScanner.nextInt();

										player2 = new Profile(playerName, wins,
												losses, ties, avatar, bestTime);
										JOptionPane.showMessageDialog(
												drawingArea,
												"Success! Profile loaded.");
										curntPlayer = player1;
										profile2Loaded = true;
										profileLoaded = true;
										newGame();
										repaint();
									}
								}
								if (player2 == null)
								{
									JOptionPane
											.showMessageDialog(drawingArea,
													"Sorry! There is no profile by that name.");
								}
							}
							catch (FileNotFoundException e)
							{
								System.out.println(e);
							}
						}
					}

				}
				else
				{

					Object returnedInput = JOptionPane
							.showInputDialog("Player 2, please enter the name of your profile:");

					if (returnedInput != null)
					{
						playerName = returnedInput.toString().trim();

						while ((checkProfileName(playerName))
								|| (playerName.length() >= 20)
								|| (playerName.length() < 2))
						{
							JOptionPane
									.showMessageDialog(
											drawingArea,
											"You did not successfully create your profile; due to one of the following reasons:"
													+ "\n 1. You did not enter a name."
													+ "\n 2. Your profile name was too short, it must be 4 or more letters long."
													+ "\n 3. Your profile name was too long, it much not exceed 18 letters long."
													+ "\n 4. Your profile name is taken.");
							returnedInput = JOptionPane
									.showInputDialog("Please enter the name of your profile:");

							if (returnedInput == null)
								playerName = "Noname" + (Math.random() + 100);
							else
							{
								playerName = returnedInput.toString();
							}
						}

						String[] possibleValues1 = { "Male", "Female",
								"Artificial Intelligence", "A Container",
								"Green", "Blue", "Yellow",
								"I need help picking..." };
						Object avatar = JOptionPane.showInputDialog(null,
								"Choose what best describes you...", "Input",
								JOptionPane.INFORMATION_MESSAGE, null,
								possibleValues1, possibleValues1[0]);

						if (avatar != null)
						{
							int avatarIcon = 0;

							if (avatar == null)
								avatarIcon = 0;
							else if (avatar.equals("Male"))
								avatarIcon = 0;
							else if (avatar.equals("Female"))
								avatarIcon = 1;
							else if (avatar.equals("Artificial Intelligence"))
								avatarIcon = 2;
							else if (avatar.equals("A Container"))
								avatarIcon = 3;
							else if (avatar.equals("Green"))
								avatarIcon = 4;
							else if (avatar.equals("Blue"))
								avatarIcon = 5;
							else if (avatar.equals("Yellow"))
								avatarIcon = 6;
							else
								avatarIcon = 7;

							player2 = new Profile(playerName, avatarIcon);

							saveProfile(player2, true);
							profileLoaded = true;
							curntPlayer = player1;

							newGame();
							repaint();
						}
					}
				}

				// profileLoaded = false;
				// gameOver = true;
				// gameOn = false;
				// repaint();
			}
		});

		JMenuItem newlanGameOption = new JMenuItem("Create LAN Game");
		newlanGameOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				InputEvent.CTRL_MASK));
		newlanGameOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evnt)
			{
				String[] choices = { "Host", "Join" };

				int connection = JOptionPane
						.showOptionDialog(
								drawingArea,
								"Please choose to begin a connecting by being a Host or join an "
										+ "\n"
										+ "existing connecting from someone in the same nework",
								"Rotate", JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE, null, choices, null);

				if (connection == 0)
				{
					setupHostPlayer();
					newGame();
					quickPlay = true;
				}
				else if (connection == 1)
				{
					String clientIP = JOptionPane
							.showInputDialog(
									drawingArea,
									"Enter the address of the computer you want to connect with",
									"Connect to Player",
									JOptionPane.INFORMATION_MESSAGE);

					setupJoiningPlayer(clientIP);
					newGame();
					quickPlay = true;
					joined = true;
				}
				repaint();

			}
		});

		JMenuItem quitProfile = new JMenuItem("Quit Profile");
		quitProfile.addActionListener(new ActionListener() {
			/**
			 * Responds to the Exit Menu choice
			 * 
			 * @param event
			 *            The event that selected this menu option
			 */
			public void actionPerformed(ActionEvent event)
			{
				if (!quickPlay)
				{
					if (gameOn && !gameOver)
					{

						if (gameType == 0)
						{ // if its a 2 player game on same
							// comp.
							int answer = JOptionPane
									.showConfirmDialog(
											null,
											"Quiting the current game will result in both players \r\nrecieving a loss on their stats.  Are you sure you want to quit? ",
											"Are you sure?",
											JOptionPane.YES_NO_OPTION);
							if (answer == JOptionPane.YES_OPTION)
							{

								player1.loss();
								saveProfile(player1, false);
								player2.loss();
								saveProfile(player2, false);

								profileLoaded = false;
								gameOver = true;
								gameOn = false;
								repaint();
							}
						}
					}
					else
					{
						profileLoaded = false;
						gameOver = true;
						gameOn = false;
						repaint();
					}
				}

				else
				{
					JOptionPane.showMessageDialog(drawingArea,
							"You are not using any profiles",
							"No Profile Loaded", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		JMenuItem exitGameOption = new JMenuItem("Exit");
		exitGameOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event)
			{

				if (gameOn)
				{
					if (gameType == 0)
					{ // if its a 2 player game on same
						// comp.
						int answer = JOptionPane
								.showConfirmDialog(
										null,
										"Exiting the current game will result in both players /r/n"
												+ "recieving a loss on their stats.  Are you sure you want to exit? ",
										"Are you sure?",
										JOptionPane.YES_NO_OPTION);
						if (answer == JOptionPane.YES_OPTION)
						{

							if (!quickPlay)
							{
								player1.loss();
								player2.loss();
							}

							System.exit(0);
						}
					}
				}
				else
				{
					System.exit(0);
				}
			}
		});

		setJMenuBar(menuBar);
		menuBar.add(gameMenu);
		gameMenu.add(quickGameOption);
		gameMenu.add(newGameOption);
		gameMenu.addSeparator();
		gameMenu.add(newlanGameOption);
		gameMenu.addSeparator();
		gameMenu.add(quitProfile);
		gameMenu.add(exitGameOption);

		JMenu optionMenu = new JMenu("Options");

		JMenuItem hiScoresOption = new JMenuItem("Highscores");
		hiScoresOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evnt)
			{

				String[] possibleValues = { "Player VS Player",
						"Player VS Computer", "Player VS Player (LAN)" };
				Object highScoreType = JOptionPane.showInputDialog(null,
						"Choose which highscore table you would like to see: ",
						"Pentago Highscores", JOptionPane.INFORMATION_MESSAGE,
						null, possibleValues, possibleValues[0]);

				Profile first = null;
				Profile second = null;
				Profile third = null;
				if (highScoreType != null)
				{
					if (highScoreType.equals("Player VS Player"))
					{

						try
						{
							Scanner fileScanner = new Scanner(new File(
									"players.txt"));

							while (fileScanner.hasNext())
							{
								String playerName = fileScanner.nextLine();
								int wins = fileScanner.nextInt();
								int losses = fileScanner.nextInt();
								int ties = fileScanner.nextInt();
								int avatar = fileScanner.nextInt();
								int bestTime = fileScanner.nextInt();

								if (fileScanner.hasNext())
									fileScanner.nextLine();

								if ((wins + losses + ties) != 0)
								{

									Profile holder = new Profile(playerName,
											wins, losses, ties, avatar,
											bestTime);

									if ((first == null)
											|| (isAbove(first, holder)))
									{
										third = second;
										second = first;
										first = new Profile(playerName, wins,
												losses, ties, avatar, bestTime);
									}
									else if ((second == null)
											|| (isAbove(second, holder)))
									{
										third = second;
										second = new Profile(playerName, wins,
												losses, ties, avatar, bestTime);
									}
									else if ((third == null)
											|| (isAbove(third, holder)))
									{
										third = new Profile(playerName, wins,
												losses, ties, avatar, bestTime);
									}
								}

							}
							// %,32d%n
							JOptionPane
									.showMessageDialog(
											drawingArea,
											"Top 3 Players in Pentago in PvP..."
													+ "\n                                   Wins      Losses      Ties"
													+ "\n 1. "
													+ first.playerName
													+ "            "
													+ first.wins
													+ "            "
													+ first.losses
													+ "              "
													+ first.ties
													+ "\n 2. "
													+ second.playerName
													+ "                      "
													+ second.wins
													+ "            "
													+ second.losses
													+ "              "
													+ second.ties
													+ "\n 3. "
													+ third.playerName
													+ "                      "
													+ third.wins
													+ "            "
													+ third.losses
													+ "              "
													+ third.ties
													+ "\n\n Think you can beat them?");

						}
						catch (FileNotFoundException e)
						{
							System.out.println(e);
						}

					}
					else if (highScoreType.equals("Player VS Computer"))
					{

					}
					else
					{

					}
				}

			}
		});

		JMenuItem audioOption = new JMenuItem("Audio");
		audioOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evnt)
			{

			}
		});

		menuBar.add(optionMenu);
		optionMenu.add(hiScoresOption);
		optionMenu.add(audioOption);

		JMenu helpMenu = new JMenu("Help");

		JMenuItem featsOption = new JMenuItem("Game Features");
		featsOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evnt)
			{

			}
		});

		JMenuItem howToPlayOption = new JMenuItem("How to Play...");
		howToPlayOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evnt)
			{

			}
		});

		JMenuItem aboutOption = new JMenuItem("About");
		aboutOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evnt)
			{

			}
		});

		menuBar.add(helpMenu);
		helpMenu.add(featsOption);
		helpMenu.add(howToPlayOption);
		helpMenu.addSeparator();
		helpMenu.add(aboutOption);

	}

	public void readInput()
	{
		if (lookForInput == null)
		{
			lookForInput = new Thread(this);
			lookForInput.start();
		}
	}

	public void run()
	{
		Thread thisThread = Thread.currentThread();

		while (lookForInput == thisThread)
		{
			if (connected && !myMove)
				readAndExecuteOpponentsMove();

			try
			{
				Thread.sleep(25);

			}
			catch (InterruptedException e)
			{

			}
		}
	}

	public void setupHostPlayer()
	{
		try
		{
			inet = InetAddress.getLocalHost();
			serverSocket = new ServerSocket(PORTNUMBER);
		}
		catch (IOException e)
		{
			showStatus("Could not connect to Port Number: " + PORTNUMBER);
		}

		try
		{
			clientSocket = serverSocket.accept();
		}
		catch (IOException e)
		{
			showStatus("Connection Failed");
			System.exit(1);
		}

		setupInputOutputStreams();
		showStatus("Connection Astablished with Player 2");

		readInput();
		myMove = true;
		connected = true;

	}

	public void setupJoiningPlayer(String hostAddress)
	{
		try
		{
			clientSocket = new Socket(hostAddress, PORTNUMBER);
		}
		catch (UnknownHostException e)
		{
			showStatus("Host cannot be found");
		}
		catch (Exception e)
		{
			showStatus("Connection is Denied");
		}

		if (clientSocket == null)
		{
			showStatus("This host does not exisit");
			return;
		}

		setupInputOutputStreams();
		showStatus("Connection Astablished with Player 1");

		readInput();
		myMove = false;
		connected = true;
	}

	public void setupInputOutputStreams()
	{
		try
		{
			in = new BufferedReader(new InputStreamReader(clientSocket
					.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);

		}
		catch (IOException e)
		{
			showStatus("Input/Output connection Failed");
		}
	}

	public void readAndExecuteOpponentsMove()
	{
		try
		{
			if (in.ready())
			{
				String opponentMove = in.readLine();

				if (opponentMove.charAt(0) == 'm')
				{
					int middle = opponentMove.indexOf(':');

					if (middle >= 0)
					{
						int x = Integer.parseInt(opponentMove.substring(1,
								middle));
						int y = Integer.parseInt(opponentMove
								.substring(middle + 1));

						gameBoard.makeOppenentMove(new Point(x, y), -1);

						boolean tie = gameBoard.tie();
						if (gameBoard.checkForWinner(currentPlayer) || tie)
						{
							repaint();
							endGame(tie, false, currentPlayer);
						}

						repaint();
					}
				}
				else if (opponentMove.charAt(0) == 'r')
				{
					int middle = opponentMove.indexOf(':');

					if (middle >= 0)
					{
						int quad = Integer.parseInt(opponentMove.substring(1,
								middle));
						int direction = Integer.parseInt(opponentMove
								.substring(middle + 1));

						gameBoard.rotate(quad, direction);

						boolean tie = gameBoard.tie();
						boolean tie2 = gameBoard.tie2();
						if (gameBoard.checkForWinner(currentPlayer))
						{
							repaint();
							endGame(false, false, currentPlayer);
						}
						else if (tie || tie2)
							endGame(tie, tie2, currentPlayer);

						angle = 0;
						myMove = true;
						repaint();
					}
				}
				else if (opponentMove.charAt(0) == 'a')
				{
					int middle = opponentMove.indexOf(':');

					if (middle >= 0)
					{
						rotatingQuad = Integer.parseInt(opponentMove.substring(
								1, middle));
						angle = Integer.parseInt(opponentMove
								.substring(middle + 1));

						repaint();

					}
				}

			}
		}

		catch (IOException e)
		{
			showStatus("Couldn't read in a character");
		}

	}

	private class DrawingPanel extends JPanel
	{
		public DrawingPanel()
		{

			setResizable(false);
			setBackground(new Color(39, 33, 24));
			setPreferredSize(new Dimension(665, 600));

			this.addMouseListener(new MouseHandler());
			this.addMouseMotionListener(new MouseMotionHandler());

		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.addRenderingHints(new RenderingHints(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON));

			g2d.drawImage(images[0], 0, 0, 665, 368, this);
			g2d.drawImage(images[1], 33, 74, 390, 391, this);

			if (!quickPlay)
				g2d.drawImage(images[2], 451, 74, 186, 187, this);

			if (!quickPlay)
				g2d.drawImage(images[3], 451, 275, 186, 150, this);
			else
				g2d.drawImage(images[3], 451, 74, 186, 150, this);

			int randomImgNum = (int) (Math.random() * 10);

			if (!quickPlay)
				g2d.drawImage(gameTipsterImages[randomImgNum], 451, 440, 186,
						134, this);
			else
				g2d.drawImage(gameTipsterImages[randomImgNum], 451, 240, 186,
						134, this);

			g2d.drawImage(quads, 45, 88, 177, 176, this);
			g2d.drawImage(quads2, 232, 88, 177, 176, this);
			g2d.drawImage(quads2, 45, 274, 177, 176, this);
			g2d.drawImage(quads, 232, 274, 177, 176, this);

			if (!profileLoaded && !connected)
			{
				g2d.drawImage(images[5], 462, 110, 48, 48, this);
				g2d.setFont(new Font("Tahoma", Font.BOLD, 12));
				g2d.setColor(new Color(39, 33, 24));
				g2d.drawString("No profile loaded,", 513, 120);
				g2d.setFont(new Font("Tahoma", Font.PLAIN, 12));
				g2d.drawString("please load an", 513, 136);
				g2d.drawString("existing profile or", 513, 152);
				g2d.drawString("create a new one by", 462, 170);
				g2d.drawString("hitting CTRL + A to start", 462, 185);
				g2d.drawString("playing.", 462, 200);
			}
			else if (profileLoaded && !connected && !quickPlay)
			{

				if (curntPlayer.avatar == 0)
				{
					g2d.drawImage(images[6], 455, 110, 50, 50, this);
				}
				else if (curntPlayer.avatar == 1)
				{
					g2d.drawImage(images[7], 455, 110, 50, 50, this);
				}
				else if (curntPlayer.avatar == 2)
				{
					g2d.drawImage(images[13], 455, 110, 48, 48, this);
				}
				else if (curntPlayer.avatar == 3)
				{
					g2d.drawImage(images[8], 455, 110, 48, 48, this);
				}
				else if (curntPlayer.avatar == 4)
				{
					g2d.drawImage(images[9], 455, 110, 48, 48, this);
				}
				else if (curntPlayer.avatar == 5)
				{
					g2d.drawImage(images[10], 455, 110, 48, 48, this);
				}
				else if (curntPlayer.avatar == 6)
				{
					g2d.drawImage(images[11], 455, 110, 48, 48, this);
				}
				else
				{
					g2d.drawImage(images[12], 455, 110, 48, 48, this);
				}

				g2d.setFont(new Font("Tahoma", Font.BOLD, 12));
				g2d.drawString(curntPlayer.playerName, 505, 120);

				g2d.setColor(new Color(148, 148, 148));
				g2d.setFont(new Font("Tahoma", Font.PLAIN, 10));
				g2d.drawString("Wins: " + curntPlayer.wins + "  Losses: "
						+ curntPlayer.losses + "  Ties: " + curntPlayer.ties,
						505, 130);
				g2d.drawString("Best Time: " + curntPlayer.bestTime / 1000
						+ " seconds", 505, 140);
				g2d.drawString(curntPlayer.winRatioStr(), 505, 150);
				g2d.drawString(curntPlayer.totalGames(), 505, 160);

				if (gameOn)
				{
					// Player Console
					g2d.setFont(new Font("Tahoma", Font.BOLD, 14));
					g2d.setColor(new Color(39, 33, 24));
					g2d.drawString("Pieces Played:", 460, 178);
					g2d.setFont(new Font("Tahoma", Font.PLAIN, 14));
					g2d.drawString(" " + curntPlayer.moveCount, 560, 178);

					// Game Console
					g2d.setFont(new Font("Tahoma", Font.BOLD, 14));
					g2d.setColor(new Color(39, 33, 24));
					g2d.drawString("Game Status:", 460, 315);
					g2d.setFont(new Font("Tahoma", Font.PLAIN, 11));
					g2d.drawString(gameStatus, 463, 330);

					g2d.setFont(new Font("Tahoma", Font.BOLD, 14));
					g2d.setColor(new Color(39, 33, 24));
					g2d.drawString("Remaining Moves:", 460, 348);
					g2d.setFont(new Font("Tahoma", Font.PLAIN, 14));
					g2d.drawString(" "
							+ (36 - (player1.moveCount + player2.moveCount)),
							585, 348);

					g2d.setFont(new Font("Tahoma", Font.BOLD, 14));
					g2d.setColor(new Color(39, 33, 24));
					g2d.drawString("Total Moves Played:", 460, 366);
					g2d.setFont(new Font("Tahoma", Font.PLAIN, 14));
					g2d
							.drawString(" "
									+ (player1.moveCount + player2.moveCount),
									600, 366);

					g2d.setFont(new Font("Tahoma", Font.BOLD, 14));
					g2d.setColor(new Color(39, 33, 24));
					g2d.drawString("Rotation Count:", 460, 385);
					g2d.setFont(new Font("Tahoma", Font.PLAIN, 14));
					g2d.drawString(" " + (rotationCount), 575, 385);

				}
			}

			if (gameOn)
			{
				if (gameOver)
				{
					g2d.drawImage(gameOverImages[gameEndStatus], 180, 500, 100,
							50, this);
				}

				g2d.setFont(new Font("Tahoma", Font.BOLD, 14));
				g2d.setColor(new Color(39, 33, 24));

				if (!quickPlay)
					g2d.drawString("Game Status:", 460, 315);
				else
					g2d.drawString("Game Status:", 460, 115);

				g2d.setFont(new Font("Tahoma", Font.PLAIN, 11));

				if (!quickPlay)
					g2d.drawString(gameStatus, 463, 330);
				else
					g2d.drawString(gameStatus, 463, 130);

				g2d.setFont(new Font("Tahoma", Font.BOLD, 14));
				g2d.setColor(new Color(39, 33, 24));

				if (!quickPlay)
					g2d.drawString("Rotation Count:", 460, 385);
				else
					g2d.drawString("Rotation Count:", 460, 185);

				g2d.setFont(new Font("Tahoma", Font.PLAIN, 14));

				if (!quickPlay)
					g2d.drawString(" " + (rotationCount), 575, 385);
				else
					g2d.drawString(" " + (rotationCount), 575, 185);

				gameBoard.draw(g2d, rotatingQuad, angle);

			}
		}

	}

	private class MouseHandler extends MouseAdapter
	{
		public void mousePressed(MouseEvent event)
		{
			if (!connected)
			{
				if (gameOn && !gameOver)
				{
					if (!needToRotate
							&& gameBoard.makeMove(event.getPoint(),
									currentPlayer))
					{
						currentPlayer *= WHITE;

						if (!quickPlay)
							curntPlayer.moveCount++;

						if (!quickPlay)
							gameStatus = curntPlayer.playerName
									+ " please rotate...";
						else
						{
							if (currentPlayer == 1)
								gameStatus = "Player One" + " please rotate...";
							else
								gameStatus = "Player Two" + " please rotate...";
						}

						moveSound.play();
						needToRotate = true;

						boolean tie = gameBoard.tie();
						boolean tie2 = false;
						if (gameBoard.checkForWinner(currentPlayer) || tie)
						{
							repaint();
							endGame(tie, tie2, currentPlayer);
						}

						repaint();
					}
					else
						rotatingQuad = gameBoard.selectedQuadrant(event
								.getPoint());
				}
			}
			else if (connected)
			{
				if (gameOn)
				{
					if (myMove && !needToRotate
							&& !gameBoard.isFilled(event.getPoint()))
					{

						gameBoard.makeMove(event.getPoint(), currentPlayer);
						needToRotate = true;

						boolean tie = gameBoard.tie();
						boolean tie2 = false;
						if (gameBoard.checkForWinner(currentPlayer) || tie)
						{
							repaint();
							endGame(tie, tie2, currentPlayer);
						}

						out.println("m" + event.getX() + ":" + (event.getY()));

					}
					else
						rotatingQuad = gameBoard.selectedQuadrant(event
								.getPoint());

				}

			}

			start = event.getPoint();
			repaint();

		}

		public void mouseReleased(MouseEvent event)
		{
			if (gameOn && !gameOver)
			{
				if (!needToRotate)
				{
					if (!quickPlay && !connected)
						gameStatus = curntPlayer.playerName
								+ " please make a move...";
					else
					{
						if (currentPlayer == 1)
							gameStatus = "Player One"
									+ " please make a move...";
						else
							gameStatus = "Player Two"
									+ " please make a move...";
					}

					if (gameBoard.isFilled(event.getPoint()))
						gameStatus = "That spot is already taken!";
					badMoveSound.play();
				}
			}

			rotatingQuad = -1;
			angle = 0;
			repaint();
		}

	}

	private class MouseMotionHandler extends MouseMotionAdapter
	{
		public void mouseMoved(MouseEvent event)
		{
			if (gameOn && !gameOver)
			{
				gameBoard.showCurrentPos(event.getPoint());
				repaint();
			}
		}

		public void mouseDragged(MouseEvent event)
		{
			if (gameOn && !gameOver)
			{
				if (rotatingQuad == NO_ROTATING_QUAD)
					return;

				if (needToRotate)
				{
					Point centre = gameBoard.getCentre(rotatingQuad);

					int firstX = start.x - centre.x;
					int firstY = start.y - centre.y;

					double firstAngle = (Math.toRadians(Math.atan2(firstY,
							firstX)));
					Point end = event.getPoint();

					int lastX = end.x - centre.x;
					int lastY = end.y - centre.y;

					double lastAngle = (Math
							.toRadians(Math.atan2(lastY, lastX)));

					if (!gameOver)
					{
						if (lastAngle > firstAngle)
						{
							angle += 8;

							if (!quickPlay && !connected)
								gameStatus = curntPlayer.playerName
										+ " is rotating right...";
							else
							{
								if (currentPlayer == 1)
									gameStatus = "Player One"
											+ " is rotating right...";
								else
									gameStatus = "Player Two"
											+ " is rotating right...";
							}

							direction = RIGHT;
						}
						else
						{
							angle -= 8;

							if (!quickPlay && !connected)
								gameStatus = curntPlayer.playerName
										+ " is rotating left...";
							else
							{
								if (currentPlayer == 1)
									gameStatus = "Player One"
											+ " is rotating left...";
								else
									gameStatus = "Player Two"
											+ " is rotating left...";
							}

							direction = LEFT;
						}
					}

					if (connected && myMove)
						out.println("a" + rotatingQuad + ":" + angle);

					if (angle >= 90)
					{
						angle = 90;
						gameBoard.rotate(rotatingQuad, direction);

						if (connected && myMove)
							out.println("r" + rotatingQuad + ":" + direction);
						rotatingQuad = NO_ROTATING_QUAD;
						myMove = false;

						needToRotate = false;

						if (currentPlayer == BLACK)
						{
							curntPlayer = player1;
						}
						else
						{
							curntPlayer = player2;
						}

						rotateSound.play();
						boolean tie = gameBoard.tie();
						boolean tie2 = gameBoard.tie2();
						if (gameBoard.checkForWinner(currentPlayer))
						{
							repaint();
							endGame(false, false, currentPlayer);
						}
						else if (tie || tie2)
							endGame(tie, tie2, currentPlayer);

						rotationCount++;
						repaint();

					}
					else if (angle <= -90)
					{
						angle = -90;
						gameBoard.rotate(rotatingQuad, direction);

						if (connected && myMove)
							out.println("r" + rotatingQuad + ":" + direction);
						rotatingQuad = NO_ROTATING_QUAD;
						myMove = false;

						needToRotate = false;

						if (currentPlayer == BLACK)
						{
							curntPlayer = player1;
						}
						else
						{
							curntPlayer = player2;
						}

						rotateSound.play();

						boolean tie = gameBoard.tie();
						boolean tie2 = gameBoard.tie2();
						if (gameBoard.checkForWinner(currentPlayer))
						{
							repaint();
							endGame(false, false, currentPlayer);
						}
						else if (tie || tie2)
							endGame(tie, tie2, currentPlayer);

						rotationCount++;
						repaint();
					}

					start = event.getPoint();
					repaint();
				}
			}
		}
	}

	public void endGame(boolean tie, boolean tie2, int currentPlayer)
	{
		repaint();
		winningSound.play();
		gameOver = true;

		if (tie || tie2)
		{
			gameStatus = "Tie Game!";
			gameEndStatus = 2;

			if (!quickPlay)
			{
				player1.tie();
				player2.tie();
			}
		}
		else if (!tie && !tie2)
		{

			if (currentPlayer == BLACK)
			{
				curntPlayer = player1;

				if (!quickPlay)
				{
					player2.win();
					player1.loss();
				}

				if (!quickPlay)
					gameStatus = player2.playerName + " has Won the Game!";
				else
					gameStatus = "Player Two" + " has Won the Game!";

				if (connected && !joined)
					gameEndStatus = 1;

			}
			else
			{
				curntPlayer = player1;

				if (!quickPlay)
				{
					player2.loss();
					player1.win();
				}

				if (!quickPlay)
					gameStatus = player1.playerName + " has Won the Game!";
				else
					gameStatus = "Player One" + " has Won the Game!";

				gameEndStatus = 0;

			}
			if (!quickPlay)
				gameEndStatus = 0;
		}
		repaint();

		if (!quickPlay)
		{
			saveProfile(player1, false);
			saveProfile(player2, false);
		}
	}

	private boolean checkProfileName(String name)
	{
		String line = "";

		try
		{
			FileReader fr = new FileReader("players.txt");
			BufferedReader br = new BufferedReader(fr);

			while ((line = br.readLine()) != null)
			{
				if (line.equals(name))
				{
					return true;
				}
			}
			return false;

		}
		catch (IOException e)
		{
			System.out.println("Uh oh, got an IOException error!");
		}
		return false;
	}

	private void saveProfile(Profile player, boolean newProfile)
	{
		try
		{
			if (newProfile)
			{
				FileWriter file = new FileWriter("players.txt", true);

				file.write(player.toString());
				file.close();

			}
			else
			{

				FileWriter file = new FileWriter("players.txt", true);
				Scanner fileScanner = new Scanner(new File("players.txt"));

				boolean found = false;
				int index = 0;
				ArrayList<String> fileContent = new ArrayList<String>();

				while ((fileScanner.hasNext()))
				{
					String line = fileScanner.nextLine();

					if (line.equals(player.playerName))
					{
						fileContent.add(player.playerName + "\r\n");
						fileContent.add(player.wins + " " + player.losses + " "
								+ player.ties + " " + player.avatar + "\r\n"
								+ player.bestTime);
						found = true;
						index = 0;
					}
					index++;
					if (!found || (index > 2))
					{
						if (fileScanner.hasNext())
							fileContent.add(line + "\r\n");
						else
							fileContent.add(line);
					}
				}

				file.close();

				FileWriter file2 = new FileWriter("players.txt", false);
				for (String next : fileContent)
				{
					file2.write(next);
				}
				file2.close();
			}

		}
		catch (FileNotFoundException e)
		{
			System.out.println(e);
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}

	private boolean isAbove(Profile main, Profile other)
	{
		double mainTotalGames = main.wins + main.ties + main.losses;
		double otherTotalGames = other.wins + other.ties + other.losses;
		int mainPts = 0;
		int otherPts = 0;

		if ((main.wins / mainTotalGames) > (other.wins / otherTotalGames))
		{
			mainPts++;
		}
		else if ((main.wins / mainTotalGames) < (other.wins / otherTotalGames))
		{
			otherPts++;
		}

		if ((main.losses / mainTotalGames) > (other.losses / otherTotalGames))
		{
			mainPts--;
		}
		else if ((main.losses / mainTotalGames) < (other.losses / otherTotalGames))
		{
			otherPts--;
		}

		if ((main.ties / mainTotalGames) > (other.ties / otherTotalGames))
		{
			mainPts++;
		}
		else if ((main.ties / mainTotalGames) < (other.ties / otherTotalGames))
		{
			otherPts++;
		}

		if (mainPts > otherPts)
		{
			return false;
		}
		else if (mainPts < otherPts)
		{
			return true;
		}
		else
		{
			if (main.bestTime > other.bestTime)
			{
				return true;
			}
			return false;
		}
	}

	public static void main(String[] args)
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				Pentago mainFrame = new Pentago();
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.pack();
				mainFrame.setVisible(true);
			}
		});

	}

}

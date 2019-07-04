package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Unit;
import edu.osu.cse.marl.GameOptions;
import edu.osu.cse.marl.Score;

public class Display extends JFrame {

	private JFrame playingField;
	private JLabel scoreLabel;
	private JLabel teamTurnLabel;
	private JButton nextTurn;
	private JButton prevTurn;
	private int columnSize;
	private int rowSize;
	private List<List<Unit>> currentTeams;
	private List<List<Unit>> previousTeams;
	private boolean teamSwitch = false;
	private boolean replay;
	private int turnSpeed;
	private int textSpacing = 500;
	private boolean actionText;

	public Display(GameOptions gameOptions) {
		columnSize = gameOptions.getBoardSize()[0];
		rowSize = gameOptions.getBoardSize()[1];
		replay = gameOptions.doReplay();
		turnSpeed = gameOptions.getTurnSpeed();
		actionText = gameOptions.getActionText();
		currentTeams = new LinkedList<List<Unit>>();
		previousTeams = new LinkedList<List<Unit>>();
		initializeScoreLabel();
		initializeTeamTurnLabel();
		if (!actionText) {
			textSpacing = 0;
		}
		initializeButtons();
		initializeFrame();
	}

	public volatile boolean continueTurn = false;
	
	public void notify(Score score, List<Unit> team1, List<Unit> team2) {
		if (!currentTeams.isEmpty()) {
			previousTeams = hardCopyTeam(currentTeams);
			currentTeams = hardCopyUnits(team1, team2);
		} else {
			previousTeams = hardCopyUnits(team1, team2);
			currentTeams = hardCopyUnits(team1, team2);
		}
		while(playingField == null){}
		playingField.repaint();
		scoreLabel.setText(score.toString());
		if (replay) {
			while(replay && !continueTurn){
				System.out.print("");
			}
			continueTurn = false;
		} else {
			try {Thread.sleep(turnSpeed);} catch (InterruptedException e) {
				System.out.println("game not waiting");
			}
		}
	}
	
	public Unit hardCopySingleUnit(Unit unit) {
		Unit copyUnit = new Unit(unit.getTeam(), unit.getBoardService(), Character.getNumericValue(unit.getUnitName().charAt(unit.getUnitName().length()-1)));
		copyUnit.setAlive(unit.isAlive());
		copyUnit.setPosition(unit.getPosition());
		copyUnit.setLastRelativeAction(unit.getLastRelativeAction());
		return copyUnit;
	}
	
	public List<List<Unit>> hardCopyUnits(List<Unit> team1, List<Unit> team2) {
		List<List<Unit>> teamCopy = new LinkedList<List<Unit>>();
		teamCopy.add(new LinkedList<Unit>());
		for (Unit team1Units : team1) {
			teamCopy.get(0).add(hardCopySingleUnit(team1Units));
		}
		teamCopy.add(new LinkedList<Unit>());
		for (Unit team2Units : team2) {
			teamCopy.get(1).add(hardCopySingleUnit(team2Units));
		}
		return teamCopy;
	}
	
	public List<List<Unit>> hardCopyTeam(List<List<Unit>> team) {
		List<List<Unit>> teamCopy = new LinkedList<List<Unit>>();
		teamCopy.add(new LinkedList<Unit>());
		for (Unit team1Units : team.get(0)) {
			teamCopy.get(0).add(hardCopySingleUnit(team1Units));
		}
		teamCopy.add(new LinkedList<Unit>());
		for (Unit team2Units : team.get(1)) {
			teamCopy.get(1).add(hardCopySingleUnit(team2Units));
		}
		return teamCopy;
	}
	
	public void printTeam(List<List<Unit>> team) {
		for (List<Unit> teams : team){
			System.out.println(teams);
			for (Unit teamUnit : teams) {
				System.out.println(teamUnit);
			}
		}
	}

	public void initializeFrame() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				playingField = new JFrame("XCOM?");
				playingField.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				playingField.setLayout(new BorderLayout());
				playingField.add(scoreLabel);
				if (replay) {
					playingField.add(nextTurn);
					playingField.add(prevTurn);
				}
				if (actionText) {
					playingField.add(teamTurnLabel);
				}
				playingField.add(new createPanel());
				playingField.setSize(360+textSpacing, 360);
				playingField.setLocationRelativeTo(null);
				playingField.setVisible(true);
			}
		});
	}
	
	public void initializeScoreLabel() {
		scoreLabel = new JLabel("");
		scoreLabel.setLocation(0, 0);
		scoreLabel.setSize(500,30);
	}
	
	//Action will display after they are taken, not display as the action about to be taken
	//Action will display movement as relative movement and reverse in the y direction
	public void initializeTeamTurnLabel() {
		teamTurnLabel = new JLabel("Actions taken this turn:");
		teamTurnLabel.setSize(textSpacing, 360);
		teamTurnLabel.setLocation(365, teamTurnLabel.getSize().height-textSpacing+10);
	}
	
	public void initializeButtons() {
		nextTurn = new JButton ("Next");
		nextTurn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!teamSwitch) {
					continueTurn = true;
				} else {
					List<List<Unit>> tempTeam = hardCopyTeam(previousTeams);
					previousTeams = hardCopyTeam(currentTeams);
					currentTeams = hardCopyTeam(tempTeam);
					playingField.repaint();
					teamSwitch = false;
				}
			}
		});
		prevTurn = new JButton("Prev");
		prevTurn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!teamSwitch) {
					List<List<Unit>> tempTeams = hardCopyTeam(currentTeams);
					currentTeams = hardCopyTeam(previousTeams);
					previousTeams = hardCopyTeam(tempTeams);
					playingField.repaint();
					teamSwitch = true;
				} else {
					System.out.println("Cannot go back further than one turn");
				}
			}
		});
	}

	/** The panel is created by a series of rectangles that will be drawn as a certain color based on the team. 
	 * The mouse handler so far only tracks where a user is selecting.
	 * TODO: Add action listener for a player to make a move. */
	public class createPanel extends JPanel {

		private List<Rectangle> cells;
		private Point selectedCell;

		public createPanel() {
			cells = new ArrayList<>(columnSize * rowSize);
			MouseAdapter mouseHandler;
			mouseHandler = new MouseAdapter() {
				public void mouseMoved(MouseEvent e) {
					int width = getWidth()-textSpacing;
					int height = getHeight()-60;
					int cellWidth = width / columnSize;
					int cellHeight = height / rowSize;
					int xOffset = (width - (columnSize * cellWidth)) / 2;
					int yOffset = (height - (rowSize * cellHeight)) / 2;
					int column = (e.getX() - xOffset) / cellWidth;
					int row = (e.getY() - yOffset) / cellHeight;
					selectedCell = null;
					if (e.getX() >= xOffset && e.getY() >= yOffset) {
						if (column >= 0 && row >= 0 && column < columnSize && row < rowSize) {
							selectedCell = new Point(column, row);
						}
					}
					repaint();
				}
			};
//			addMouseMotionListener(mouseHandler);
		}

		public Dimension getPerfereedSize() {
			return new Dimension(200, 200);
		}

		public void invalidate() {
			cells.clear();
			selectedCell = null;
			super.invalidate();
		}

		public void repaintComponents() {
			scoreLabel.setLocation(0, getHeight()-30);
			scoreLabel.setSize(getWidth(), 30);
			scoreLabel.repaint();
			nextTurn.setLocation((7*getWidth()/8)-textSpacing, getHeight()-60);
			nextTurn.setSize(getWidth()/8, 30);
			nextTurn.repaint();
			prevTurn.setLocation(0, getHeight()-60);
			prevTurn.setSize(getWidth()/8, 30);
			prevTurn.repaint();
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			repaintComponents();
			teamTurnLabel.setText("<html>Actions taken this turn: ");
			Graphics2D g2D = (Graphics2D) g.create();
			int width = getWidth()-textSpacing;
			int height = getHeight()-60;
			int cellWidth = width / columnSize;
			int cellHeight = height / rowSize;
			int xOffset = (width - (columnSize * cellWidth)) / 2;
			int yOffset = (height - (rowSize * cellHeight)) / 2;
			if (selectedCell != null) { //if you want to select a cell that is occupied by another color, put this code after the next block that colors units
				int index = selectedCell.x + (selectedCell.y * columnSize);
				Rectangle cell = cells.get(index);
				g2D.setColor(Color.BLUE); 
				g2D.fill(cell);
			}
			for (int i = 0; i < rowSize; i++) {
				for (int j = 0; j < columnSize; j++) {
					Rectangle cell = new Rectangle(xOffset + (j * cellWidth),
							yOffset + (i * cellHeight), cellWidth, cellHeight);
					for (Unit unit : currentTeams.get(0)) {
						if (unit.isAlive() && unit.getPosition().getX() == j && unit.getPosition().getY() == i) {
							g2D.setColor(Color.GREEN);
							g2D.fill(cell);
							g2D.setColor(Color.BLACK);
							g2D.drawString(unit.getDisplayName(), cell.x+(cellWidth/8), cell.y+(3*cellHeight/4));
							teamTurnLabel.setText(teamTurnLabel.getText()+"<br>  "+unit.getDisplayAction()+"</br>");
						}
					}
					for (Unit unit : currentTeams.get(1)) {
						if (unit.isAlive() && unit.getPosition().getX() == j && unit.getPosition().getY() == i) {
							g2D.setColor(Color.RED);
							g2D.fill(cell);
							g2D.setColor(Color.BLACK);
							g2D.drawString(unit.getDisplayName(), cell.x+(cellWidth/8), cell.y+(3*cellHeight/4));
							teamTurnLabel.setText(teamTurnLabel.getText()+"<br>  "+unit.getDisplayAction()+"</br>");
						}
					}
					cells.add(cell);
				}
			}
			g2D.setColor(Color.GRAY);
			for (Rectangle cell : cells) {
				g2D.draw(cell);
			}
			g2D.dispose();
			teamTurnLabel.setText(teamTurnLabel.getText()+"</html>");
			int numDeadUnits = 0;
			for (List<Unit> team : currentTeams) {
				for (Unit unit : team) {
					if (!unit.isAlive()) {
						numDeadUnits++;
					}
				}
			}
			teamTurnLabel.setLocation(365, teamTurnLabel.getSize().height-textSpacing+20-(9*numDeadUnits));
			teamTurnLabel.repaint();
		}
	}
}

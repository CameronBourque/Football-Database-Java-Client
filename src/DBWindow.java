import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.util.List;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.security.auth.Subject;
import javax.swing.*;

public class DBWindow extends JFrame{
	public static final int MAX_ATTR = 6;
	public static final int PADDING = 4;
	public static final String[] TABLES = {"Player","in_year", "Team", "Game"};
	public static final String[] CONFERENCES = {"Atlantic Coast Conference", "Big 12 Conference", "Big East Conference", "Big Ten Conference", "Conference USA","Independent","Mid-American Conference","Mountain West Conference","Pacific-10 Conference", "Southeastern Conference", "Sun Belt Conference", "Western Athletic Conference", "Ind", "Ivy", "Southwestern"};
	
	private JPanel ui;
	private JTextArea output;
	private JScrollPane scroll;
	private JLabel retrieveAll;
	private JLabel whoHave;
	private JButton go;
	private JButton save;
	private JButton go_part1;
	private JLabel saveToFile;
	private JTextField filename;
	private JTextField t1_part1;
	private JTextField t2_part1;
	private static HashMap<String, String[]> table_attributes;
	private static HashMap<Integer, String> teamcodemap;
	private static HashMap<String, Integer> teamnamemap;

	
	private JTableSelector tables1;
	private JAttrSelector attrs1;
	
	//Query 2
	private JTextField player1Choice;
	private JTextField player2Choice;
	private JButton getPlayerConnection;

	//Query 3
	private JTextField teamChoice;
	private JButton findMostRushYards;
	
	//Query 4
	private JButton getHomeFieldAdvantage;
	private JComboBox<String> conferenceChoice;
	static Connection conn;
	
	DBWindow(){
		
		//Login Details
		dbSetupExample my = new dbSetupExample();
		
		//DB Setup
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/team7_905_cfb", my.user, my.pswd);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);
		}
		
		//GUI initialization
		setSize(800, 533);
		setTitle("Database GUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Output TextArea
		output = new JTextArea();
		output.setLineWrap(true);
		output.setEditable(false);
		output.setVisible(true);
		scroll = new JScrollPane(output);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		ui = new JPanel();
		
		//HashMap values
		table_attributes = new HashMap<String, String[]>();
		table_attributes.put("", new String[] {""});
		table_attributes.put("Player", new String[]{"Player Code", "Last Name"});
		table_attributes.put("in_year", new String[]{"Player Code", "Year", "Team Code", "Uniform Number", "Height", "Weight", "Class", "Position"});
		table_attributes.put("Team", new String[]{"Team Code", "Name"});
		table_attributes.put("Game", new String[]{"Game Code", "Date", "Visit Team Code", "Home Team Code"});
		table_attributes.put("Stadium", new String[]{"Name", "City"});
		
		//Initialize Go Button
		go = new JButton("Go");
		go.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					//Create SQL Statement
					//Vulnerable to SQL injection
					
					String query = new String("SELECT * FROM  ");
					String[] table_names = tables1.getSelectItems();
					for(int i=0;i<table_names.length;i++) {
						query += removeNumFromTable(table_names[i]) + " AS " + table_names[i];
						if(i != table_names.length-1)
							query += ",";
					}
					if(!attrs1.get(0).isEmpty()) {
						query += " WHERE ";
						for (int i = 0; i < attrs1.current_size; i++) {
							if(i != 0)
								query += " AND ";
							query+= attrs1.get(i).toSQL();

						}
					}
					PreparedStatement pstmt = conn.prepareStatement(query);
					if(pstmt.execute()) { //There were results
						

						Font f = new Font(Font.MONOSPACED,Font.PLAIN,12);
						output.setFont(f);
						output.setText(resultSetToString(pstmt.getResultSet()));
					}
				} catch (SQLException f) {
					// TODO Auto-generated catch block
					f.printStackTrace();
				}
			}
		});
		
		//Initialize Save Button
		save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					File file = new File(filename.getText());
					String toadd = "";
					file.createNewFile(); // creates new or overwrites old file with same name
					FileWriter writer = new FileWriter(file);
					toadd = output.getText();
					writer.write(toadd);
					writer.flush();
					writer.close();
				} catch (IOException f) {
					f.printStackTrace();
				}
			}
		});
		
		//Initialize attributes section
		attrs1 = new JAttrSelector(this, 6);
		
		//Initialize tables
		tables1 = new JTableSelector(this, TABLES, 6);
		tables1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				attrs1.setAttributes(generateAllAttributes(tables1.getSelectItems()));
			}});
		
		//Initial values for attributes
		attrs1.setAttributes(generateAllAttributes(tables1.getSelectItems()));
		
		//create player text fields
		player1Choice = new JTextField();
		player2Choice = new JTextField();
		
		//Initialize getPlayerConnection Button
		getPlayerConnection = new JButton("Player Connection");
		getPlayerConnection.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				String player1 = player1Choice.getText();
				String player2 = player2Choice.getText();
				if(player1.split(" ").length != 2 || player2.split(" ").length != 2) {
					return;
				}
				
				try {
					//get the player codes from the input names
					String playerQuery = "select \"Player Code\" from player where" +
							" \"First Name\"='" + player1.split(" ")[0] + "' and \"Last Name\"='" + player1.split(" ")[1] +
							"' or " + "\"First Name\"='" + player2.split(" ")[0] + "' and \"Last Name\"='" + player2.split(" ")[1] + "'";
					PreparedStatement pstmt = conn.prepareStatement(playerQuery);
					int[] codes = new int[2];
					int index = 0;
					
					if(pstmt.execute()) {
						ResultSet rs = pstmt.getResultSet();
						while(rs.next()) {
							codes[index] = rs.getInt(1);
							index++;
						}
					}
					
					String results = connectPlayer(codes[0], codes[1]);
					
					//reformat the results for readability
					String[] resSplit = results.split(":");
					String out = "";
					for(int i = 0; i < resSplit.length; i++) {
						if(resSplit[i].split("@")[0].equals("player")) {
							String temp = playerFormat(resSplit[i].split("@")[1], resSplit[i+1].split("@")[1], resSplit[i+2].split("@")[1]);
							out += temp.split(":")[0] + " played on " + temp.split(":")[1] + " in " + temp.split(":")[2];
							i += 2;
						}
						else if(resSplit[i].split("@")[0].equals("game")) {
							out += "\n who played against ";
							if(resSplit[i+1].split("@")[0].equals("player")) {
								String temp = gameFormat(resSplit[i].split("@")[1], resSplit[i+2].split("@")[1],
														 resSplit[i+1].split("@")[1], resSplit[i+3].split("@")[1]);
								out += temp.split(":")[1] + " on " + temp.split(":")[0] + " which " + temp.split(":")[2] +
										" played for in " + temp.split(":")[3];
								i += 3;
							}
							else {
								String temp = gameFormat(resSplit[i].split("@")[1], resSplit[i+1].split("@")[1],"","");
								out += temp.split(":")[1] + " on " + temp.split(":")[0];
								i++;
							}
						}
					}
					output.setText(out);
					
					
				} catch (SQLException f) {
					// TODO Auto-generated catch block
					f.printStackTrace();
				}
				//FILL
			}
		});

		teamChoice = new JTextField();
		findMostRushYards = new JButton("Teams with Most Rush Yards Against");
		findMostRushYards.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String team = teamChoice.getText();

					String teamQuery = "SELECT DISTINCT ROW_NUMBER() OVER(ORDER BY sum(t2.\"Rush Yard\") DESC) as \"Ranking\", t2.\"Team Code\", team2.\"Name\" as \"Team Name\", sum(t2.\"Rush Yard\") as \"Overall Rushing Yards\"\r\n" +
							"FROM team_game_statistics t1\r\n" +
							"JOIN team_game_statistics t2\r\n" +
							"ON t1.\"Game Code\" = t2.\"Game Code\"\r\n" +
							"JOIN team team1\r\n" +
							"ON t1.\"Team Code\" = team1.\"Team Code\"\r\n" +
							"JOIN team team2\r\n" +
							"ON team2.\"Team Code\" = t2.\"Team Code\"\r\n" +
							"WHERE \r\n" +
							"team1.\"Name\" = '" +
							team +
							"'\r\n" +
							"AND t2.\"Team Code\"<>team1.\"Team Code\"\r\n" +
							"GROUP BY t2.\"Team Code\", team2.\"Name\"\r\n" +
							"ORDER BY \"Overall Rushing Yards\" DESC\r\n" +
							"LIMIT 10;";
					PreparedStatement pstmt = conn.prepareStatement(teamQuery);
					if(pstmt.execute()) { //There were results
						Font f = new Font(Font.MONOSPACED,Font.PLAIN,12);
						output.setFont(f);
						output.setText(resultSetToString(pstmt.getResultSet()));
					}
				} catch (SQLException f) {
					// TODO Auto-generated catch block
					f.printStackTrace();
				}
			}
		});

		//Initialize getHomeFieldAdvantage Button
		getHomeFieldAdvantage = new JButton("Home Field Advantage");
		getHomeFieldAdvantage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//mean and std points for visitors in all conferences.
				double mean = 23.3621;
				double std = 13.6618;
				try {
					String query1 = new String("select avg(\"Visit Team Points\"), stddev_pop(\"Visit Team Points\") from game_results join Team on \"Home Team Code\"=\"Team Code\" join conference on team.\"Conference\"=conference.code where conference.\"name\"=? and \"Year\"='06/06/2012'");
					PreparedStatement pstmt = conn.prepareStatement(query1);
					pstmt.setString(1, (String) conferenceChoice.getSelectedItem());

					if(pstmt.execute()) {
						ResultSet rs = pstmt.getResultSet();
						rs.next();
						mean=rs.getDouble(1);
						std=rs.getDouble(2);
					}
					
	
					String query= new String("select team.\"Name\", avg(\"Home Team Points\"), stddev_pop(\"Home Team Points\")"
							+ " from game_results join Team on \"Home Team Code\"=\"Team Code\""
							+ " join conference on team.\"Conference\"=conference.code "
							+ "where conference.\"name\"=? and \"Year\"='06/06/2012' "
							+ "group by team.\"Name\" order by avg(\"Home Team Points\") desc;");
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, (String) conferenceChoice.getSelectedItem());
					
					if(pstmt.execute()) {
						ResultSet rs = pstmt.getResultSet();
						ResultSetMetaData md = rs.getMetaData();
						//Return String
						String return_string = new String();
						//Add Table Column names
						return_string += String.format("| %-20s", "Team");
						return_string += String.format("| %-20s", "Home Field Advantage");
						return_string += '|';
						return_string += "\n";
						return_string += "\n";
						//Add Table Data
						double hometeam_mean;
						double hometeam_std;
						while(rs.next()) {
							hometeam_mean = rs.getDouble(2);
							hometeam_std = rs.getDouble(3);
							
							return_string += String.format("| %-20s",rs.getString(1));
							return_string += String.format("| %-20s", 100*(1-cdf_approx( (mean-hometeam_mean) / Math.sqrt(std*std + hometeam_std*hometeam_std))));
							return_string += '|';
							return_string += "\n";
						}
						Font f = new Font(Font.MONOSPACED,Font.PLAIN,12);
						output.setFont(f);
						output.setText(return_string);
					}
				} catch (SQLException f) {
					// TODO Auto-generated catch block
					f.printStackTrace();
				}
				
			}
			
		});

		// part 1
		go_part1 = new JButton("Go");
		t1_part1 = new JTextField();
		t2_part1 = new JTextField();
		teamcodemap = new HashMap<Integer, String>();
		teamnamemap = new HashMap<String, Integer>();
		go_part1.addActionListener(new ActionListener() {
			   @Override
			   public void actionPerformed(ActionEvent e) {
				   try {
				   		String teamquery = new String("select * from team");
				   		Statement stmt1 = conn.createStatement();
				   		ResultSet result1 = stmt1.executeQuery(teamquery);

				   		while (result1.next()) {
				   			teamcodemap.put(result1.getInt("Team Code"),result1.getString("Name"));
							teamnamemap.put(result1.getString("Name"),result1.getInt("Team Code"));
						}

					   DijkstrasShortestPathAdjacencyList chain = new DijkstrasShortestPathAdjacencyList(13000);
					   String query = new String("select * from game_results");
					   Statement stmt = conn.createStatement();
					   ResultSet result = stmt.executeQuery(query);

					   String return_string = new String();

					   while (result.next()) {
					   	if (result.getString("Home Team Won").equals("t")){
					   		chain.addEdge(result.getInt("Home Team Code"),result.getInt("Visit Team Code"),1,result.getLong("Game Code"));
						} else {
							chain.addEdge(result.getInt("Visit Team Code"),result.getInt("Home Team Code"),1,result.getLong("Game Code"));
						}
					   }

//					   String team1played = new String("Select \"Game Code\" from game where \"Visit Team Code\"=" + teamnamemap.get(t1_part1.getText()).toString() + "or \"Home Team Code\" = " + teamnamemap.get(t1_part1.getText()).toString());
//					   String team2played = new String("Select \"Game Code\" from game where \"Visit Team Code\"=" + teamnamemap.get(t2_part1.getText()).toString() + "or \"Home Team Code\" = " + teamnamemap.get(t2_part1.getText()).toString());
//					   ArrayList<Long> team1games = new ArrayList<Long>();
//					   ArrayList<Long> team2games = new ArrayList<Long>();
//
//					   Statement stmt2 = conn.createStatement();
//					   ResultSet result2 = stmt.executeQuery(team1played);
//
//					   while (result2.next()) {
//					   	team1games.add(result2.getLong("Game Code"));
//					   }
//
//					   Statement stmt3 = conn.createStatement();
//					   ResultSet result3 = stmt.executeQuery(team2played);
//
//					   while (result3.next()) {
//					   	team2games.add(result3.getLong("Game Code"));
//					   }
//
//					   System.out.println(team2games);
//
//					   DijkstrasShortestPathAdjacencyList chain2 = new DijkstrasShortestPathAdjacencyList(100);
//					   chain2.addEdge(6,1,1, 1);
//					   chain2.addEdge(6,1,1,2);
//					   chain2.addEdge(6,1,1, 3);
//					   chain2.addEdge(1,2,1, 4);
//					   chain2.addEdge(8,1,1, 5);
//					   chain2.addEdge(1,8,1, 6);
//					   chain2.addEdge(4,1,1, 7);
//					   chain2.addEdge(8,4,1, 11);
//					   chain2.addEdge(2,9,1, 8);
//					   chain2.addEdge(9,8,1, 9);
//					   chain2.addEdge(8,0,1, 10);
//					   chain2.addEdge(0,4,1, 12);
//					   chain2.addEdge(0,3,1, 13);
//					   System.out.println(chain2.reconstructPath(6,8));

					   List<Long> resu = chain.reconstructPath(teamnamemap.get(t1_part1.getText()),teamnamemap.get(t2_part1.getText()));

					   System.out.println(resu);

					   String finalans = "";
					   String winteam = "";
					   String loseteam = "";
					   String oldwinteam = "";
					   String oldloseteam = "";

					   for (int i = 0; i < resu.size(); i++) {
						   String qry = new String("Select * from game where \"Game Code\"=" + resu.get(i).toString());
						   ResultSet result2 = stmt.executeQuery(qry);
						   result2.next();
						   String year = result2.getDate("Date").toString();
						   String qry1 = new String("Select * from game_results where \"Game Code\"=" + resu.get(i).toString());
						   ResultSet result3 = stmt.executeQuery(qry1);

						   result3.next();
						   if (result3.getString("Home Team Won").equals("t")) {
						   	oldwinteam = winteam;
						   	oldloseteam = loseteam;
						   	winteam = teamcodemap.get(result3.getInt("Home Team Code"));
						   	loseteam = teamcodemap.get(result3.getInt("Visit Team Code"));
						   } else {
							   oldwinteam = winteam;
							   oldloseteam = loseteam;
							   loseteam = teamcodemap.get(result3.getInt("Home Team Code"));
							   winteam = teamcodemap.get(result3.getInt("Visit Team Code"));
						   }
						   if (!oldwinteam.equals(winteam) && !oldloseteam.equals(loseteam)){
						   	finalans += winteam + " beat " + loseteam + " on " + year + '\n';
						   }
					   }

					   Font f = new Font(Font.MONOSPACED,Font.PLAIN,12);
					   output.setFont(f);
					   output.setText(finalans);

				   } catch (SQLException f) {
					   f.printStackTrace();
				   }
			   }
		   });

		//Initialize
		conferenceChoice = new JComboBox<String>(CONFERENCES);
		update();
		
		setVisible(true);
	}
	
	public void update() {
		
		ui.setVisible(false);
		ui.removeAll();
		
		GridBagLayout group = new GridBagLayout();
		ui.setLayout(group);
		
		int ylvl = 0;
		
		//First Row
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = ylvl;
		c.gridwidth = 5;
		c.weightx = 0.5;
		c.ipady = 250;
		ui.add(scroll, c);
		
		ylvl++;
		//Second row
		retrieveAll = new JLabel("Retrieve all");
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = ylvl;
		c.weightx = 0.03;
		ui.add(retrieveAll, c);
		
		whoHave = new JLabel("who have");
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = ylvl;
		c.weightx = 0.03;
		c.insets = new Insets(0, PADDING, 0, 0);
		ui.add(whoHave, c);

		//Add Go button
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 4;
		c.gridy = 1;
		c.weightx = 0;
		c.insets = new Insets(0, PADDING, 0, 0);
		ui.add(go, c);
		
		//Add Tables
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = ylvl;
		c.gridheight = tables1.current_size+1;
		c.gridwidth = 1;
		c.weightx = 0.5;
		ui.add(tables1, c);
		
		//Add Attributes
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.gridy = ylvl;
		c.gridheight = attrs1.current_size+1;
		c.gridwidth = 1;
		c.weightx = 0.5;
		ui.add(attrs1, c);

		int maxSize = Math.max(attrs1.current_size, tables1.current_size);
		ylvl+=maxSize;		
		ylvl++;
		
		//Add save button
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = ylvl;
		c.weightx = 0.1;
		ui.add(save, c);

		//Add filename textfield
		filename = new JTextField();
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = ylvl;
		c.weightx = 0.1;
		ui.add(filename, c);
		
		//Query 1
		ylvl++;
		
		//Query 2
		ylvl++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = ylvl;
		c.weightx = 0.1;
		ui.add(player1Choice, c);
		c.gridwidth = 2;
		c.gridx = 1;
		ui.add(player2Choice, c);
		c.gridwidth = 1;
		c.gridx = 3;
		ui.add(getPlayerConnection, c);
		
		//Query 3
		ylvl++;
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = ylvl;
		c.weightx = 0.1;
		ui.add(teamChoice, c);
		c.gridwidth = 3;
		c.gridx = 1;
		ui.add(findMostRushYards, c);

		//Query 4
		ylvl++;

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = ylvl;
		c.weightx = 0.1;
		ui.add(conferenceChoice, c);
		c.gridx = 1;
		c.gridwidth = 3;
		ui.add(getHomeFieldAdvantage, c);

		ylvl++;
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = ylvl;
		c.gridwidth = 2;
		ui.add(t1_part1, c);
		c.gridx = 2;
		c.gridwidth = 2;
		ui.add(t2_part1, c);
		c.gridx = 4;
		ui.add(go_part1, c);

		add(ui);

		ui.setVisible(true);
	}
	
	//Returns String[] of all attributes for all the select tables and "Custom Text"
	private String[] generateAllAttributes(String[] selectedTables) {
		
		ArrayList<String[]> output = new ArrayList<String[]>();
		for(String table: selectedTables)
			output.add(generateAttributes(table));
		int size = 0;
		for(int i=0;i<output.size();i++)
			size += output.get(i).length;
		
		//Increase size for Year and Custom Value
		size += 1;
		String[] real_output = new String[size];
		int curr = 0;
		for(int i=0;i<output.size();i++) {
			for(int j=0;j<output.get(i).length;j++) {
				real_output[curr] = output.get(i)[j];
				curr++;
			}
		}
		real_output[size-1] = "Custom Value";
		return real_output;
		
	}

	//Gets "Player" for "Player1"
	private String removeNumFromTable(String name) {
		if(Character.isDigit(name.charAt(name.length()-1))) {
			return name.substring(0,name.length()-1);
		}else {
			return name;
		}
	}
	//Returns a string containing the attribute names for the table
	//generateAttributes(Player1) would return
	//Player1.Player_Code etc.
	//Only works for tables with # < 10
	private String[] generateAttributes(String table) {
	
		String tableKey;
		if(Character.isDigit(table.charAt(table.length()-1))) {
			tableKey = table.substring(0,table.length()-1);
		}else {
			tableKey = table;
		}
		
		String[] attributeNames = table_attributes.get(tableKey);
		String[] output = new String[attributeNames.length];
		for(int i=0;i<output.length;i++)
			output[i] = table + "." + "\"" + attributeNames[i] +"\"";
		
		return output;
	}


String resultSetToString(ResultSet rs) throws SQLException {

	ResultSetMetaData md = rs.getMetaData();
	//Return String
	String return_string = new String();

	//Add Table Column names
	for(int i=1;i<md.getColumnCount()+1;i++) {
		return_string += String.format("| %-20s",md.getColumnLabel(i));
	}
	return_string += '|';
	return_string += "\n";
	//return_string += String.format("-".repeat(23*md.getColumnCount()+1));
	for(int i = 0; i < 23*md.getColumnCount()+1; i++) {
		return_string += '-';
	}
	return_string += "\n";
	//Add Table Data
	while(rs.next()) {
		for(int i=1;i<md.getColumnCount()+1;i++) {
			return_string += String.format("| %-20s",rs.getString(i));
		}
		return_string += '|';
		return_string += "\n";
	}
	return return_string;
}

double cdf_approx(double z) {

	if(z>0) {
		return 1 - 0.5 * Math.exp(-(z*z+z)/2);
	}else{
		z = -z;
		return 0.5 * Math.exp(-(z*z+z)/2);
	}
}

String connectPlayer(int startPlayer, int endPlayer) {
	String playerStr = "player@" + startPlayer + ":";
	String path = "";
	boolean first = true;
	ArrayList<Integer> used = new ArrayList<Integer>();
	
	try {
		//get the teams the start player played on and when
		String query = "select \"Team Code\", \"Year\" from in_year where \"Player Code\"=" + startPlayer;
		PreparedStatement stmt = conn.prepareStatement(query);
		if(stmt.execute()) {
			ResultSet rs = stmt.getResultSet();
			ArrayList<String> teams = new ArrayList<String>();
			while(rs.next()) {
				int team = rs.getInt(1);
				String ret = "team@" + rs.getInt(1);
				teams.add(rs.getInt(1) + "");
				ret += ":year@" + rs.getDate(2) + ":";
				
				//check if they played on the same team
				query = "select \"Player Code\", \"Year\" from in_year where \"Team Code\"=" + team +
						" and \"Player Code\"=" + endPlayer;
				stmt = conn.prepareStatement(query);
				if(stmt.execute()) {
					ResultSet rs2 = stmt.getResultSet();
					while(rs2.next()) {
						return playerStr + ret + "player@" + rs2.getInt(1) + ":team@" + team + ":year@" + 
								rs2.getDate(2) + ":";
					}
				}
				
				//player could play on multiple teams
				used.add(team);
				String temp = connectTeamtoPlayer(team + "", endPlayer, used, -1);
				if(!temp.equals("N/A")) {
					temp = ret + temp;
					if(first || path.split(":").length > temp.split(":").length) {
						path = temp;
						first = false;
					}
				}
			}
			
			return playerStr + path;
			
		}
	} catch(SQLException e) {
		e.printStackTrace();
	}
	
	//after exception catch
	return "N/A";
}

String connectTeamtoPlayer(String team, int player, ArrayList<Integer> used, int toBeat) {		
	if(toBeat == 0) { return "N/A"; }
	try {
		//see if the team played against the player's team
		String query = "select in_year.\"Player Code\", in_year.\"Team Code\", in_year.\"Year\", game.\"Date\" from in_year, game where " +
		"in_year.\"Team Code\" in (game.\"Visit Team Code\", game.\"Home Team Code\") and (game.\"Visit Team Code\"=" +
		team + " or game.\"Home Team Code\"=" + team + ") and in_year.\"Player Code\"=" + player;
		PreparedStatement stmt = conn.prepareStatement(query);
		if(stmt.execute()) {
			ResultSet rs = stmt.getResultSet();
			while(rs.next()) {
				return "game@" + rs.getDate(4) + ":player@" + rs.getInt(1) + ":team@" + rs.getInt(2) + ":year@" +
						rs.getDate(3) + ":";
			}
		}
		
		//get paths of all teams that the team played against and return shortest one
		query = "select \"Visit Team Code\", \"Home Team Code\", \"Date\" from game where \"Visit Team Code\"=" + team + " or \"Home Team Code\"=" +
				team;
		stmt = conn.prepareStatement(query);
		if(stmt.execute()) {
			ResultSet rs = stmt.getResultSet();
			String path = "";
			boolean first = true;
			while(rs.next()) {
				int oppteam = rs.getInt(1);
				if(team.equals(oppteam + "")) {
					oppteam = rs.getInt(2);
				}
				if(!used.contains(Integer.valueOf(oppteam))) {
					used.add(Integer.valueOf(oppteam));
					
					String temp = connectTeamtoPlayer(oppteam + "", player, used, toBeat-1);
					if(!temp.equals("N/A")) {
						temp = "game@" + rs.getDate(3) + ":team@" + oppteam + ":" + temp;
						if(first || path.split(":").length > temp.split(":").length) {
							path = temp;
							first = false;
							toBeat = path.split(":").length / 2 - 1;
						}
						else {
							while(used.indexOf(Integer.valueOf(oppteam)) != used.size()-1) {
								used.remove(used.indexOf(Integer.valueOf(oppteam)) + 1);
							}
						}
					}
					else {
						while(used.indexOf(Integer.valueOf(oppteam)) != used.size()-1) {
							used.remove(used.indexOf(Integer.valueOf(oppteam)) + 1);
						}
					}
				}
			}
			if(path.length() == 0) {
				return "N/A";
			}
			return path;
		}
		
	} catch(SQLException e) {
		e.printStackTrace();
	}
	
	return "N/A";
}

String playerFormat(String pcode, String tcode, String year) {
	String ret = "";
	try {
		//get name
		String query = "select \"First Name\",\"Last Name\" from player where \"Player Code\"=" + pcode;
		PreparedStatement stmt = conn.prepareStatement(query);
		if(stmt.execute()) {
			ResultSet rs = stmt.getResultSet();
			if(rs.next()) {
				ret = rs.getString(1) + " " + rs.getString(2);
			}
		}
		
		//get team
		query = "select \"Name\" from team where \"Team Code\"=" + tcode;
		stmt = conn.prepareStatement(query);
		if(stmt.execute()) {
			ResultSet rs = stmt.getResultSet();
			if(rs.next()) {
				ret += ":" + rs.getString(1);
			}
		}
		
		//get year
		ret += ":" + year.substring(0, year.indexOf("-"));
		
		return ret;
	} catch(SQLException e) {
		e.printStackTrace();
	}
	
	return "";
}

String gameFormat(String date, String tcode, String pcode, String year) {
	String ret = "";

	try {
		//get date
		ret = date;
		
		//get team
		String query = "select \"Name\" from team where \"Team Code\"=" + tcode;
		PreparedStatement stmt = conn.prepareStatement(query);
		if(stmt.execute()) {
			ResultSet rs = stmt.getResultSet();
			if(rs.next()) {
				ret += ":" + rs.getString(1);
			}
		}
		
		if(pcode.length() > 0) {
			//get name
			query = "select \"First Name\",\"Last Name\" from player where \"Player Code\"=" + pcode;
			stmt = conn.prepareStatement(query);
			if(stmt.execute()) {
				ResultSet rs = stmt.getResultSet();
				if(rs.next()) {
					ret += ":" + rs.getString(1) + " " + rs.getString(2);
				}
			}
		}
		
		if(year.length() > 0) {
			//get year
			ret += ":" + year.substring(0, year.indexOf("-"));
		}
		
		return ret;
		
	} catch(SQLException e) {
		e.printStackTrace();
	}
	
	return "";
}

}

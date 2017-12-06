// Evan Mesa
// 12/5/2017
// CIS 283

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;
import java.util.*;

public class Scraper
{

    static Map<String, Player > playerStatsDict = new HashMap<>();
    static ArrayList<String> playerStatsList = new ArrayList<>();
    static ArrayList<String> playerNamesList = new ArrayList<>();

    private static class Player{
        String name;
        String team;
        String completions;
        String attempts;
        String completionPercentage;
        String yards;
        String yardsPerAtt;
        String yardsPerGame;
        String touchdowns;
        String interceptions;
        String passerRating;

        public Player(String name, String team, String completions, String attempts, String compPercentage, String yards, String yardsPerAtt,
                      String yardsPerGame, String touchdowns, String interceptions, String passerRating) {
            this.name = name;
            this.team = team;
            this.completions = completions;
            this.completionPercentage = compPercentage;
            this.attempts = attempts;
            this.yards = yards;
            this.yardsPerAtt = yardsPerAtt;
            this.yardsPerGame = yardsPerGame;
            this.touchdowns = touchdowns;
            this.interceptions = interceptions;
            this.passerRating = passerRating;
        }
    }

    // pulls the numbers out of a string
    public static String getNumbersFromString(String str){
        int beg = 0;
        int end = 0;
        boolean start = false;
        for ( int i = 0; i < str.length(); i++){
            if( Character.isDigit(str.charAt(i)) && !start  ){
                beg = i;
                start = true;
            }
            else if ( !(str.charAt(i) == '.') && (!Character.isDigit(str.charAt(i)) && start) ){
                end = i;
                break;
            }
        }
        return str.substring(beg, end);
    }
    public static void main(String[] args){

        String url = "https://www.si.com/college-football/stats";

        try{

            URL            u = new URL(url);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.connect();
            Scanner        s = new Scanner(c.getInputStream());
            boolean foundCompletions = false;
            boolean foundAttempts = false;
            boolean foundCompPerc = false;
            boolean foundYards = false;
            boolean foundYardsPerAttempt = false;
            boolean foundYardsPerGame = false;
            boolean foundTouchDowns = false;
            boolean foundInterceptions = false;
            boolean foundPasserRating = false;
            String playerName = "";

            while (s.hasNextLine()){

                String test = s.nextLine();
                String[] strarray = test.split("\\s+");
                String teamName;
                boolean foundPlayer = false;
                boolean foundFirstName = false;
                boolean foundTeamName = false;

                for(String str: strarray){
                    if( str.equals("class=\"player-link\"")) {
                        foundPlayer = true;
                    }
                    else if(foundPlayer){
                        playerName = str;
                        playerName = playerName.substring(7);
                        foundPlayer = false;
                        foundFirstName = true;
                    }
                    else if (foundFirstName){
                        playerName = playerName + " " + str;
                        foundFirstName = false;
                    }

                    else if(str.equals("class=\"unskinned\"")){
                        foundTeamName = true;
                    }
                    else if(foundTeamName){
                        teamName = str;
                        teamName = teamName.substring(29);
                        teamName = teamName.replace("-", " ");

                        for(int i = 0; i < teamName.length(); i++){
                            if (i == 0){
                                String firstCapLetter = teamName.substring(0,1).toUpperCase();
                                teamName = firstCapLetter + teamName.substring(1);

                            }
                            else if(teamName.charAt(i)==' '){
                                String secondCapLetter = teamName.substring(i+1, i+2).toUpperCase();
                                teamName = teamName.substring(0, i)+ " " + secondCapLetter + teamName.substring(i+2);
                            }

                            if (teamName.charAt(i) == '\"'){
                                teamName = teamName.substring(0, i);
                                break;
                            }
                        }

                        playerStatsList.add(teamName);
                        foundTeamName = false;
                        foundCompletions = true;

                    }

                    else if (foundCompletions){

                        String testString = str.replaceAll("[0-9]+", "");
                        if(testString.equals("data-abbreviation=\"COMP\"></td>")){

                            playerStatsList.add(getNumbersFromString(str));
                            foundCompletions = false;
                            foundAttempts = true;
                        }


                    }
                    else if (foundAttempts){
                        String testString = str.replaceAll("[0-9]+", "");
                        if (testString.equals("data-abbreviation=\"ATT\"></td>")) {

                            playerStatsList.add(getNumbersFromString(str));
                            foundAttempts = false;
                            foundCompPerc = true;
                        }
                    }
                    else if (foundCompPerc){
                        String testString = str.replaceAll("(\\d+(?:\\.\\d+)?)", "");
                        if (testString.equals("data-abbreviation=\"COMP%\"></td>")) {

                            playerStatsList.add(getNumbersFromString(str));
                            foundCompPerc = false;
                            foundYards = true;
                        }
                    }
                    else if (foundYards){
                        String testString = str.replaceAll("[0-9]+", "");
                        if (testString.equals("data-abbreviation=\"YDS\"></td>")) {

                            playerStatsList.add(getNumbersFromString(str));
                            foundYards = false;
                            foundYardsPerAttempt = true;
                        }
                    }
                    else if (foundYardsPerAttempt){
                        String testString = str.replaceAll("(\\d+(?:\\.\\d+)?)", "");
                        if (testString.equals("data-abbreviation=\"YDS/ATT\"></td>")) {

                            playerStatsList.add(getNumbersFromString(str));
                            foundYardsPerAttempt = false;
                            foundYardsPerGame = true;
                        }
                    }
                    else if (foundYardsPerGame){
                        String testString = str.replaceAll("(\\d+(?:\\.\\d+)?)", "");
                        if (testString.equals("data-abbreviation=\"YDS/G\"></td>")) {

                            playerStatsList.add(getNumbersFromString(str));
                            foundYardsPerGame = false;
                            foundTouchDowns = true;
                        }
                    }
                    else if (foundTouchDowns){
                        String testString = str.replaceAll("[0-9]+", "");
                        if (testString.equals("data-abbreviation=\"TD\"></td>")) {

                            playerStatsList.add(getNumbersFromString(str));
                            foundTouchDowns = false;
                            foundInterceptions = true;
                        }
                    }
                    else if (foundInterceptions){
                        String testString = str.replaceAll("[0-9]+", "");
                        if (testString.equals("data-abbreviation=\"INT\"></td>")) {

                            playerStatsList.add(getNumbersFromString(str));
                            foundInterceptions = false;
                            foundPasserRating = true;
                        }
                    }
                    else if (foundPasserRating){
                        String testString = str.replaceAll("(\\d+(?:\\.\\d+)?)", "");
                        if (testString.equals("data-abbreviation=\"RATE\"></td>")) {

                            playerStatsList.add(getNumbersFromString(str));
                            foundPasserRating = false;

                            // creates player object and adds it to a hashmap indexed by the players name
                            Player player = new Player(playerName, playerStatsList.get(0), playerStatsList.get(1), playerStatsList.get(2), playerStatsList.get(3),
                                    playerStatsList.get(4), playerStatsList.get(5), playerStatsList.get(6), playerStatsList.get(7), playerStatsList.get(8), playerStatsList.get(9));
                            playerNamesList.add(playerName);
                            playerStatsDict.put(playerName, player);

                            playerName = "";
                            playerStatsList.clear();

                        }
                    }
                }
            }
            System.out.println("Amount of data I'm getting is inconsistent: " +playerNamesList.size() + " rows" );

        }
        catch (IOException ex){
            ex.printStackTrace();
            System.exit(2);
        }



        JPanel panel1 = new JPanel();
        panel1.setPreferredSize(new Dimension(200, 700));
        JPanel panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(600, 700));

        JFrame frame1 = new JFrame("College Quarterback Stats");
        frame1.setSize(800, 700);
        frame1.setLayout(new GridLayout(1,2));

        panel1.setLayout(new BorderLayout());
        panel2.setLayout(new GridLayout(10,2));

        JLabel label1 = new JLabel("Team");
        label1.setVerticalAlignment(JLabel.NORTH);
        label1.setHorizontalAlignment(JLabel.CENTER);

        JLabel label11 = new JLabel("");
        label11.setVerticalAlignment(JLabel.NORTH);
        label11.setHorizontalAlignment(JLabel.CENTER);

        JLabel label2 = new JLabel("Completions");
        label2.setVerticalAlignment(JLabel.NORTH);
        label2.setHorizontalAlignment(JLabel.CENTER);

        JLabel label12 = new JLabel("");
        label12.setVerticalAlignment(JLabel.NORTH);
        label12.setHorizontalAlignment(JLabel.CENTER);

        JLabel label3 = new JLabel("Attempts");
        label3.setVerticalAlignment(JLabel.NORTH);
        label3.setHorizontalAlignment(JLabel.CENTER);

        JLabel label13 = new JLabel("");
        label13.setVerticalAlignment(JLabel.NORTH);
        label13.setHorizontalAlignment(JLabel.CENTER);

        JLabel label4 = new JLabel("Completion %");
        label4.setVerticalAlignment(JLabel.NORTH);
        label4.setHorizontalAlignment(JLabel.CENTER);

        JLabel label14 = new JLabel("");
        label14.setVerticalAlignment(JLabel.NORTH);
        label14.setHorizontalAlignment(JLabel.CENTER);

        JLabel label5 = new JLabel("Yards");
        label5.setVerticalAlignment(JLabel.NORTH);
        label5.setHorizontalAlignment(JLabel.CENTER);

        JLabel label15 = new JLabel("");
        label15.setVerticalAlignment(JLabel.NORTH);
        label15.setHorizontalAlignment(JLabel.CENTER);

        JLabel label6 = new JLabel("Yards Per Attempt");
        label6.setVerticalAlignment(JLabel.NORTH);
        label6.setHorizontalAlignment(JLabel.CENTER);

        JLabel label16 = new JLabel("");
        label16.setVerticalAlignment(JLabel.NORTH);
        label16.setHorizontalAlignment(JLabel.CENTER);

        JLabel label7 = new JLabel("Yards Per Game");
        label7.setVerticalAlignment(JLabel.NORTH);
        label7.setHorizontalAlignment(JLabel.CENTER);

        JLabel label17 = new JLabel("");
        label17.setVerticalAlignment(JLabel.NORTH);
        label17.setHorizontalAlignment(JLabel.CENTER);

        JLabel label8 = new JLabel("Touchdowns");
        label8.setVerticalAlignment(JLabel.NORTH);
        label8.setHorizontalAlignment(JLabel.CENTER);

        JLabel label18 = new JLabel("");
        label18.setVerticalAlignment(JLabel.NORTH);
        label18.setHorizontalAlignment(JLabel.CENTER);

        JLabel label9 = new JLabel("Interceptions");
        label9.setVerticalAlignment(JLabel.NORTH);
        label9.setHorizontalAlignment(JLabel.CENTER);

        JLabel label19 = new JLabel("");
        label19.setVerticalAlignment(JLabel.NORTH);
        label19.setHorizontalAlignment(JLabel.CENTER);

        JLabel label10 = new JLabel("Passer Rating");
        label10.setVerticalAlignment(JLabel.NORTH);
        label10.setHorizontalAlignment(JLabel.CENTER);

        JLabel label20 = new JLabel("");
        label20.setVerticalAlignment(JLabel.NORTH);
        label20.setHorizontalAlignment(JLabel.CENTER);

        panel2.add(label1); panel2.add(label2); panel2.add(label11); panel2.add(label12); panel2.add(label3); panel2.add(label4); panel2.add(label13); panel2.add(label14);
        panel2.add(label5); panel2.add(label6); panel2.add(label15); panel2.add(label16); panel2.add(label7); panel2.add(label8); panel2.add(label17); panel2.add(label18);
        panel2.add(label9); panel2.add(label10); panel2.add(label19); panel2.add(label20);



        // Sorts by passer rating
        for(int i = 1; i < playerNamesList.size();i++){
            if( Double.compare(Double.parseDouble(playerStatsDict.get(playerNamesList.get(i)).passerRating),
                    Double.parseDouble(playerStatsDict.get(playerNamesList.get(i-1)).passerRating) )>0){

                String temp = playerNamesList.get(i-1);
                playerNamesList.set( i-1, playerNamesList.get(i) );
                playerNamesList.set( i, temp );
            }
        }

        JList rowList = new JList(playerNamesList.toArray());
        rowList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                label11.setText(playerStatsDict.get(rowList.getSelectedValue()).team);
                label12.setText(playerStatsDict.get(rowList.getSelectedValue()).completions);
                label13.setText(playerStatsDict.get(rowList.getSelectedValue()).attempts);
                label14.setText(playerStatsDict.get(rowList.getSelectedValue()).completionPercentage);
                label15.setText(playerStatsDict.get(rowList.getSelectedValue()).yards);
                label16.setText(playerStatsDict.get(rowList.getSelectedValue()).yardsPerAtt);
                label17.setText(playerStatsDict.get(rowList.getSelectedValue()).yardsPerGame);
                label18.setText(playerStatsDict.get(rowList.getSelectedValue()).touchdowns);
                label19.setText(playerStatsDict.get(rowList.getSelectedValue()).interceptions);
                label20.setText(playerStatsDict.get(rowList.getSelectedValue()).passerRating);

            }
        });

        JScrollPane scroll = new JScrollPane(rowList);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panel1.add(scroll);
        frame1.add(panel1);
        frame1.add(panel2);
        frame1.pack();
        frame1.setVisible(true);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
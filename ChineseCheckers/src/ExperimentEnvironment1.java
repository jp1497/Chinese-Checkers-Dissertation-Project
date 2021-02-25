import java.io.FileWriter;
import java.io.IOException;

public class ExperimentEnvironment1 {

    double totalRunTime;

    private final int NUM_TESTS = 25;

    public void test(int experimentNum, String filename) throws IOException {
        //FileWriter myWriter = new FileWriter("experiment"+ experimentNum+".txt");
        FileWriter myWriter = new FileWriter(filename);

        for (int i = 0; i < NUM_TESTS; i++){
            Experiment experiment = new Experiment(experimentNum);

            totalRunTime += experiment.getExperimentTime();

            myWriter.append("\nExperiment number: " + i + "           running time: " +
                    experiment.getExperimentTime() + "\n");

            for (Player player: experiment.getFinishedPlayers()){
                if (player.isWinner()){
                    myWriter.append("\nWINNER: " + player.getColour() + ",      MOVES: " + player.getNumMoves() +
                            ",     DISTANCE: " + player.getDistanceFromWin() +
                            ",     AVG THINKING TIME: " + player.getAverageThinkingTime());
                }
                else{
                    myWriter.append("\nLOSER : " + player.getColour() + ",      MOVES: " + player.getNumMoves() +
                            ",     DISTANCE: " + player.getDistanceFromWin() +
                            ",     AVG THINKING TIME: " + player.getAverageThinkingTime());
                }
            }
            myWriter.append("\n");
        }

        myWriter.append("\n Total Experiment time: " + totalRunTime);

        myWriter.close();
    }

    public void testToCSV(int experimentNum, String filename) throws IOException {
        FileWriter csvWriter = new FileWriter(filename);
        csvWriter.append("id,winner,p1moves,p1distance,p1thinkingtime," +
                "p2moves,p2distance,p2thinkingtime," +
//                "p3moves,p3distance,p3thinkingtime," +
//                "p4moves,p4distance,p6thinkingtime," +
//                "p5moves,p5distance,p6thinkingtime," +
//                "p6moves,p6distance,p6thinkingtime," +
                "\n");

        for (int i = 0; i < NUM_TESTS; i++) {
            System.out.println("\n" + "experiment number: " + experimentNum + ", test number: " + i);

            Experiment experiment = new Experiment(experimentNum);
            Player player1 = experiment.getFinishedPlayers().get(0);
            Player player2 = experiment.getFinishedPlayers().get(1);
//            Player player3 = experiment.getFinishedPlayers().get(2);
//            Player player4 = experiment.getFinishedPlayers().get(3);
//            Player player5 = experiment.getFinishedPlayers().get(4);
//            Player player6 = experiment.getFinishedPlayers().get(5);

            String winner = null;
            if(player1 == experiment.getWinner()){
                winner = "p1";
            } else if (player2 == experiment.getWinner()){
                winner = "p2";
//            } else if (player3 == experiment.getWinner()){
//                winner = "p3";
//            }else if (player4 == experiment.getWinner()){
//                winner = "p4";
//            }else if (player5 == experiment.getWinner()){
//                winner = "p5";
//            }else if (player6 == experiment.getWinner()){
//                winner = "p6";
            }

            totalRunTime += experiment.getExperimentTime();

            String id = Integer.toString(i);

            String p1moves = Integer.toString(player1.getNumMoves());
            String p1distance = Integer.toString(player1.getDistanceFromWin());
            String p1thinking = Double.toString(player1.getAverageThinkingTime());

            String p2moves = Integer.toString(player2.getNumMoves());
            String p2distance = Integer.toString(player2.getDistanceFromWin());
            String p2thinking = Double.toString(player2.getAverageThinkingTime());

//            String p3moves = Integer.toString(player3.getNumMoves());
//            String p3distance = Integer.toString(player3.getDistanceFromWin());
//            String p3thinking = Double.toString(player3.getAverageThinkingTime());
//
//            String p4moves = Integer.toString(player4.getNumMoves());
//            String p4distance = Integer.toString(player4.getDistanceFromWin());
//            String p4thinking = Double.toString(player4.getAverageThinkingTime());
//
//            String p5moves = Integer.toString(player5.getNumMoves());
//            String p5distance = Integer.toString(player5.getDistanceFromWin());
//            String p5thinking = Double.toString(player5.getAverageThinkingTime());
//
//            String p6moves = Integer.toString(player6.getNumMoves());
//            String p6distance = Integer.toString(player6.getDistanceFromWin());
//            String p6thinking = Double.toString(player6.getAverageThinkingTime());


            csvWriter.append(id + "," + winner + "," + p1moves + "," + p1distance + "," + p1thinking +
                    "," + p2moves + "," + p2distance + "," + p2thinking +
//                    "," + p3moves + "," + p3distance + "," + p3thinking +
                   // "," + p4moves + "," + p4distance + "," + p4thinking +
                 //   "," + p5moves + "," + p5distance + "," + p5thinking +
                  //  "," + p6moves + "," + p6distance + "," + p6thinking +
                    "\n");

            System.out.println("Winner: " + winner + " with " + player1.getNumMoves() +
                    " moves," + " and a run time of: " + experiment.getExperimentTime());

        }
        csvWriter.append("\n Total Experiment time: " + totalRunTime);

        csvWriter.close();


    }

    public static void main(String[] args) throws IOException {
        ExperimentEnvironment1 environment = new ExperimentEnvironment1();

        //environment.testToCSV(1, "minimax_d2_d3_self_play.csv");
        //environment.testToCSV(2, "minimax_d1_d3_self_play.csv");
        //environment.testToCSV(3, "mcts_self_play.csv");
        //environment.testToCSV(13, "mcts_self_play_3.csv");

//        environment.testToCSV(4, "maxn_self_play.csv");
        //environment.testToCSV(5, "paranoid_self_play.csv");
//        environment.testToCSV(6, "greedy_self_play.csv");
//
//        environment.testToCSV(7, "comparative_d1_3_player.csv");
//        environment.testToCSV(8, "comparative_d2_3_player.csv");
//        environment.testToCSV(9, "comparative_d3_3_player.csv");

        //environment.testToCSV(10, "comparative_d1_6_player.csv");
        //environment.testToCSV(11, "comparative_d2_6_player.csv");
        //environment.testToCSV(12, "comparative_d3_6_player.csv");

    }

}

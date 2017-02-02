import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by loujian on 2/1/17.
 */
public class RSD_three_players {

    public static void main(String[] args)throws Exception
    {
        Scanner cin=new Scanner(new File("SN_Scale_Free_1000_n20m2.txt"));
        File writename = new File("RSD_three_players_SN_Scale_Free_1000_n20m2.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));
        int n = 20; //n means the number of players in the game.

        int num_cases=100;

        double[] social_welfare_array= new double[num_cases+1];
        double[] max_min_array= new double[num_cases+1];
        double[] variance_array= new double[num_cases+1];
        double[] average_max_difference = new double[num_cases+1];
        double[] difference_team_array= new double[num_cases+1];

        double total_sum=0;
        double total_max_min=0;
        double total_max_difference=0;

        double total_correlation_value=0;
        double total_difference_in_team=0;

        double total_variance=0;

        double[][]rank= new double[n+1][num_cases+1]; //Here is the rank of players in all test cases.
        double[][]utility= new double[n+1][num_cases+1]; //Here is the utility players in all test cases.

        for(int iter=1; iter<=num_cases; iter++)
        {
            LinkedList<Integer> order = new LinkedList<Integer>();

            for (int i = 0; i < n; i++)
               order.add(i + 1); //input the order in the game.

            /*
            boolean[] flag_order= new boolean[n+1];
            for(int i=1; i<=n; i++)
                flag_order[i]=false;

            Random rd= new Random();

            while(order.size()<n)
            {
                Integer tmp= rd.nextInt(n);
                tmp++;
                if(flag_order[tmp]==false) {
                    order.add(tmp);
                    flag_order[tmp]=true;
                }
                else
                {
                    continue;
                }
            }
            */


            for(int i=0; i<order.size(); i++)
                rank[order.get(i)][iter]=(double)(i+1)/n; //get the rank of each player we can get a matrix, rank[i][j] means the utility of player i's utility in the case iter



            ArrayList<ArrayList<Integer>> value = new ArrayList<ArrayList<Integer>>(); //simulate the preference profile of all players
            ArrayList<LinkedList<Integer>> linked_value = new ArrayList<LinkedList<Integer>>();

            double[][] normal_value = new double[n+1][n+1]; //here means the normalized value each player get
            for(int i=1; i<=n; i++)
                for(int j=1; j<=n; j++)
                    normal_value[i][j]=0;

            value.clear();
            linked_value.clear();
            for (int i = 0; i <= n; i++) {
                ArrayList<Integer> tmp = new ArrayList<Integer>();
                LinkedList<Integer> tmp1 = new LinkedList<Integer>();
                tmp.clear();
                tmp1.clear();
                for (int j = 0; j <= n; j++) {
                    tmp.add(0);
                }
                value.add(tmp);
                linked_value.add(tmp1);
            }

            for (int i = 1; i <= n; i++) {
                int num_neigh = cin.nextInt();
                for (int j = 1; j <= num_neigh; j++) {
                    Integer tmp = cin.nextInt();
                    value.get(i).set(tmp, n - j + 1); //here we let it be the linear decreasing function

                    normal_value[i][tmp]= 1.0*(num_neigh-j+1)/num_neigh; //here we normalize the utility of players

                    //value.get(i).set(tmp, 1<<(n - j)); //here we let it be the exponential decreasing function
                    linked_value.get(i).add(tmp);
                }
            }

            ArrayList<LinkedList<Integer>> player_teammates = new ArrayList<LinkedList<Integer>>();
            for(int i=0; i<=n; i++)
            {
                LinkedList<Integer> tmp= new LinkedList<Integer>();
                tmp.clear();
                player_teammates.add(tmp);
            } //at the very first, make all players' teammate are "-1"

            while(order.size()>=2)
            {
                Integer proposer= order.getFirst();
                if(linked_value.get(proposer).isEmpty()==true)
                {
                    for(int k=1; k<=n; k++)
                    {
                        linked_value.get(k).remove(proposer);
                    }
                    order.remove(proposer);
                    continue;
                }

                Integer receiver= linked_value.get(proposer).getFirst();
                player_teammates.get(proposer).add(receiver);
                player_teammates.get(receiver).add(proposer);

                for(int k=1; k<=n; k++)
                {
                    linked_value.get(k).remove(receiver);
                }
                order.remove(receiver);

                if(linked_value.get(proposer).isEmpty()==true)
                {
                    for(int k=1; k<=n; k++)
                        linked_value.get(k).remove(proposer);
                    order.remove(proposer);
                }
                else
                {
                    Integer another= linked_value.get(proposer).getFirst();
                    Integer current_teammate= player_teammates.get(proposer).getFirst();
                    player_teammates.get(proposer).add(another);
                    player_teammates.get(current_teammate).add(another);
                    player_teammates.get(another).add(proposer);
                    player_teammates.get(another).add(current_teammate);
                    for(int k=1; k<=n; k++) {
                        linked_value.get(k).remove(proposer);
                        linked_value.get(k).remove(another);
                    }
                    order.remove(proposer);
                    order.remove(another);
                }


            }

            ArrayList<Integer> array = new ArrayList<Integer>();
            array.add(0);
            for(int i=1; i<=n; i++)
            {
                int tmp_sum=0;
                for(int j=0; j<player_teammates.get(i).size(); j++)
                    tmp_sum+= value.get(i).get(player_teammates.get(i).get(j));
                array.add(tmp_sum);
            }

            for (int i = 1; i <= n; i++) {
                out.write(i + " ");
                for(int j=0; j<player_teammates.get(i).size();j++)
                {
                    out.write(player_teammates.get(i).get(j)+ " ");
                }
                out.write("\r\n");
            }

            //compute the social welfare
            social_welfare_array[iter]=0;
            for(int i=1; i<=n; i++)
            {
                social_welfare_array[iter]+= array.get(i);
            }

            out.write("The social welfare is " + social_welfare_array[iter] + "\r\n");



        }

        out.write("The average social welfare is "+ social_welfare(num_cases, social_welfare_array) + "\r\n");

        out.flush();
        out.close();

    }

    static double social_welfare(int number_cases, double[]social_welfare_array)
    {
        double sum=0;
        for(int i=1; i<=number_cases;i++)
            sum+= social_welfare_array[i];
        double average = sum/number_cases;
        return average;
    }
}

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by loujian on 1/26/17.
 */
public class RSD_larger_team {

    public static void main(String[] args)throws Exception
    {
        Scanner cin=new Scanner(new File("newfrat_cleaned.txt"));
        File writename = new File("RSD_newfrat_cleaned");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));

        int num_cases=1;

        int n = 8; //n means the number of players in the game.
        int k_upper=3; //the minimum and maximum size of a team

        for(int iter=1; iter<=num_cases; iter++)
        {
            //we define the team of each player
            ArrayList<LinkedList<Integer>> team_players= new ArrayList<LinkedList<Integer>>();
            team_players.clear();
            for(int i=0; i<=n; i++)
            {
                LinkedList<Integer> tmp= new LinkedList<>();
                tmp.clear();
                team_players.add(tmp);
            }

            //we define the captains
            ArrayList<Integer> captain= new ArrayList<Integer>();
            captain.clear();


            //here we generate the random order for RSD
            LinkedList<Integer> order = new LinkedList<Integer>();
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
            }//till now, we generate the random order

            int[][] value= new int[n+1][n+1]; //simulate the preference profile of all players
            ArrayList<LinkedList<Integer>> linked_value = new ArrayList<LinkedList<Integer>>();

            linked_value.clear();
            for (int i = 0; i <= n; i++) { //initialize the linked_value
                LinkedList<Integer> tmp1 = new LinkedList<Integer>();
                tmp1.clear();
                linked_value.add(tmp1);
            }

            for(int i=1; i<=n; i++) //we cin the values and also initialize the linked_value
            {
                for(int j=1; j<=n; j++)
                    value[i][j]= cin.nextInt();

                boolean[] current_flag= new boolean[n+1];
                for(int j=1; j<=n; j++)
                    current_flag[j]=false;
                for(int j=1; j<n; j++) //we rank all other players
                {
                    int max_value=-1;
                    int max_index=-1;
                    for(int k=1; k<=n; k++)
                    {
                        if(current_flag[k]==false && value[i][k]>max_value)
                        {
                            max_value=value[i][k];
                            max_index=k;
                        }
                    }
                    linked_value.get(i).add(max_index);
                    current_flag[max_index]=true;
                }
            }//Then we have got the value and linked_value

            //Now we will do the RSD


            while(order.size()>=2)
            {
                Integer proposer = order.getFirst();
                captain.add(proposer);
                if(linked_value.get(proposer).isEmpty()==true)
                {
                    for(int k=1; k<=n; k++)
                    {
                        linked_value.get(k).remove(proposer);
                    }
                    order.remove(proposer);
                    continue;
                }

                while(team_players.get(proposer).size() < k_upper-1 && order.size()>1) //if it has not reached the upper bound
                {
                    Integer receiver= linked_value.get(proposer).getFirst();
                    team_players.get(proposer).add(receiver);
                    for(int k=1; k<=n; k++)
                    {
                        linked_value.get(k).remove(receiver);
                    }
                    order.remove(receiver);
                }
                if(order.size()>1)
                {
                    for(int k=1; k<=n; k++)
                    {
                        linked_value.get(k).remove(proposer);
                    }
                    order.remove(proposer);
                }
            }

            int[][] teams = new int[n+1][n+1];
            int number_of_teams= captain.size();

            for(int i=0; i<number_of_teams; i++)
            {
                for(int j=0; j<team_players.get(captain.get(i)).size(); j++)
                {
                    int current_mate= team_players.get(captain.get(i)).get(j);
                    team_players.get(current_mate).add(captain.get(i));
                    for(int k=0; k<team_players.get(captain.get(i)).size(); k++)
                    {
                        if(current_mate!= team_players.get(captain.get(i)).get(k))
                            team_players.get(current_mate).add(team_players.get(captain.get(i)).get(k));
                    }
                }
            }


            for(int i=1; i<=n; i++) {
                for (int j = 0; j < team_players.get(i).size(); j++)
                {
                    out.write(team_players.get(i).get(j)+ " ");
                }
                out.write("\r\n");
            }



            int current_sum= social_welfare(n, team_players, value);
            out.write("The social welfare is "+ current_sum + "\r\n");

        }

        out.flush();
        out.close();


    }

    static int social_welfare(int n, ArrayList<LinkedList<Integer>> team_players, int[][] value)
    {
        int sum=0;
        for(int i=1; i<=n; i++)
        {
            for(int j=0; j<team_players.get(i).size(); j++)
            sum+=value[i][team_players.get(i).get(j)];
        }

        return sum;
    }

}











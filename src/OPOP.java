/**
 * Created by loujian on 1/27/17.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import  java.util.Set;


public class OPOP {

    public static void main(String[] args)throws Exception
    {
        Scanner cin=new Scanner(new File("newfrat_cleaned.txt"));
        File writename = new File("OPOP_newfrat_cleaned");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));

        int num_cases=1;

        int n = 17; //n means the number of players in the game.
        int k_upper=5; //the minimum and maximum size of a team
        int k_lower=4;
        int teams_number= 4;
        int[] teams_size= new int[teams_number+1]; //Here we define the size for each team(set)
        teams_size[0]=5;
        teams_size[1]=4;
        teams_size[2]=4;
        teams_size[3]=4;



        for(int iter=1; iter<=num_cases; iter++)
        {
            LinkedList<Integer> order = new LinkedList<Integer>();
            order.clear();

            for (int i = 0; i < n; i++)
               order.add(i + 1); //input the order in the game.

/*          //here we generate the random order for RSD
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
*/

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



            //Now we do the OPOP
            //we define the teams which defined as sets
            ArrayList<LinkedList<Integer>> teams = new ArrayList<LinkedList<Integer>>();
            teams.clear();
            for(int i=0; i<teams_number; i++)
            {
                LinkedList<Integer> tmp= new LinkedList<Integer>();
                tmp.clear();
                teams.add(tmp);
            }

            boolean[] is_captiain= new boolean[n+1];
            for(int i=1; i<=n; i++)
                is_captiain[i]=false;

            int[] index_set= new int[n+1]; //we need to know which player is in which group
            for(int i=1; i<=n; i++)
                index_set[i]=-1;

            boolean[]flag_round= new boolean[n+1]; //denote whether it has been teammed
            for(int i=1; i<=n; i++)
                flag_round[i]=false;

            for(int i=0; i<teams_number; i++) {
                Integer player= order.get(i);
                is_captiain[player] = true; //make the first T players be captains
                teams.get(i).add(player); //add the captain into the team, which is from 0 to n-1
                index_set[player]=i;//we need to know which player is in which team
                for(int k=1; k<=n; k++) //we have added player into teams, so we need to delete them from the preference
                {
                    linked_value.get(k).remove(player);
                }
                flag_round[player]=true;
                continue;

            }


            int number_of_teamed_players= teams_number;
            for(int i=0; i<order.size(); i=(i+1)%(order.size()))
            {
                if(number_of_teamed_players>=n) //if we have known that all players have been teammed, then we stop
                    break;

                Integer player= order.get(i);

                if(is_captiain[player])
                {
                    int which_set = index_set[player];
                    if(linked_value.get(player).size()==0)
                        continue;

                    if(teams.get(which_set).size()>=teams_size[which_set])//it has reached upper bound of the team size
                        continue;

                    Integer receiver= linked_value.get(player).getFirst(); //get the most preferred teammate from the list
                    teams.get(which_set).add(receiver);
                    index_set[receiver]=which_set;
                    for(int k=1; k<=n; k++) //we have added receiver into teams, so we need to delete them from the preference
                    {
                        linked_value.get(k).remove(receiver);
                    }
                    flag_round[receiver]=true;//we don't need to deal with it in the next round
                    number_of_teamed_players++;
                    index_set[receiver]=which_set;

                }

                else
                {
                    if(flag_round[player]==true) //we don't need to deal with it
                        continue;

                    int sum_rest_utility=0;
                    for(int j=1; j<=n; j++)
                    {
                        if(flag_round[j]==false)
                        sum_rest_utility+= value[player][j];
                    }
                    double average_rest_utility=(double) sum_rest_utility/(n- number_of_teamed_players);

                    int which_set= -1;
                    double max_expected_utility=-1;
                    for(int j=0; j<teams_number; j++)
                    {
                        if(teams.get(j).size()>=teams_size[j])
                            continue;

                        double current_sum_utility=0;
                        for(int k=0; k<teams.get(j).size(); k++)
                        {
                            current_sum_utility+= value[player][teams.get(j).get(k)];
                        }
                        double current_expected_utility= current_sum_utility+ (teams_size[j]-teams.get(j).size()-1)*average_rest_utility;
                        if(current_expected_utility>max_expected_utility)
                        {
                            max_expected_utility=current_expected_utility;
                            which_set=j;
                        }
                    }
                    teams.get(which_set).add(player);
                    flag_round[player]=true;
                    number_of_teamed_players++;
                    index_set[player]=which_set;
                    for(int k=1; k<=n; k++) //we have added player into teams, so we need to delete them from the preference
                        linked_value.get(k).remove(player);

                    if(teams.get(which_set).size()< teams_size[which_set])//if there is still some more space, then we add the best teammate for receiver
                    {
                        Integer best_of_player= linked_value.get(player).getFirst();
                        teams.get(which_set).add(best_of_player);
                        index_set[best_of_player]=which_set;
                        for(int k=1; k<=n; k++) //we have added receiver into teams, so we need to delete them from the preference
                            linked_value.get(k).remove(best_of_player);
                        flag_round[best_of_player]=true;
                        number_of_teamed_players++;
                        index_set[best_of_player]=which_set;
                    }
                }
            }

            for(int i=1; i<=n; i++)
            {
                int which_team=index_set[i];

                for(int j=0; j<teams.get(which_team).size(); j++)
                {
                    out.write(teams.get(which_team).get(j) + " ");
                }
                out.write("\r\n");
            }



        }

        out.flush();
        out.close();



    }


}

















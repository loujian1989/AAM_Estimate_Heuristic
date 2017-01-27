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

        int n = 17; //n means the number of players in the game.
        int k_lower=4;
        int k_upper=5; //the minimum and maximum size of a team

        for(int iter=1; iter<=num_cases; iter++)
        {
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

            ArrayList<ArrayList<Integer>> value = new ArrayList<ArrayList<Integer>>(); //simulate the preference profile of all players
            ArrayList<LinkedList<Integer>> linked_value = new ArrayList<LinkedList<Integer>>();




        }


    }

}


/**
 * Created by loujian on 1/30/17.
 */

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.lang.System;
import java.io.*;
import java.util.Scanner;

public class Heuristic_three_players {

    int n; //number of players
    int depth; //the depth of search
    double accept_threshold;

    LinkedList<Integer> order; //means the list of players in the order
    ArrayList<ArrayList<Integer>> value; //simulate the preference profile of all players, they are all values
    ArrayList<LinkedList<Integer>> linked_value; //the ordinal preferences of all players

    Heuristic_three_players(int n, int depth, LinkedList<Integer> order1, ArrayList<ArrayList<Integer>> value1, ArrayList<LinkedList<Integer>> linked_value1, double accept_threshold)
    {
        this.n= n;
        this.depth= depth;
        this.accept_threshold= accept_threshold;

        order= new LinkedList<Integer>(); //means the list of players in the order
        order.clear();
        order.addAll(order1);

        value = new ArrayList<ArrayList<Integer>>(); //simulate the preference profile of all players
        linked_value= new ArrayList<LinkedList<Integer>>();
        value.clear();
        linked_value.clear();
        value.addAll(value1);
        linked_value.addAll(linked_value1);
    }

    public ArrayList<LinkedList<Integer>> ARM ()
    {
        Integer root_player= order.getFirst();//get the root node of the search tree
        //ArrayList<Integer> array= new ArrayList<Integer>(n+1);
        //array.clear();
        //for(int i=0; i<=n;i++)
        //    array.add(0);

        ArrayList<LinkedList<Integer>> player_teammates = new ArrayList<LinkedList<Integer>>();
        for(int i=0; i<=n; i++)
        {
            LinkedList<Integer> tmp= new LinkedList<Integer>();
            tmp.clear();
            player_teammates.add(tmp);
        } //at the very first, make all players' teammate are "-1"



        Node root_node = new Node(root_player, order, linked_value, player_teammates);

        boolean is_first_teammate= true;

        player_teammates= DFS(n, root_node, player_teammates, depth, value, linked_value, 0, true, accept_threshold);

        return player_teammates;
    }

    private static ArrayList<LinkedList<Integer>> DFS (int n, Node node, ArrayList<LinkedList<Integer>> player_teammates, int depth, ArrayList<ArrayList<Integer>> value, ArrayList<LinkedList<Integer>> linked_value, int rank, boolean is_first_teammate, double accept_threshold)
    {
        if(rank==0 && is_first_teammate==true) {   //Here we do IES procedure
            boolean[] flag_profile = new boolean[n + 1];
            for (int i = 0; i <= n; i++)  //Denote whether some player is still available
                flag_profile[i] = false;

            boolean count = true;

            //here we do IMS
            /*
            while (count == true && node.order.size() > 2) {
                for (int i = 1; i <= n; i++) {
                    if (!node.preference.get(i).isEmpty())
                        flag_profile[i] = true;
                    else
                        flag_profile[i] = false;
                }

                ArrayList<LinkedList<Integer>> prefer = new ArrayList<LinkedList<Integer>>();
                prefer.clear();
                for (int i = 0; i <= n; i++) {
                    LinkedList<Integer> tmp = new LinkedList<>();
                    tmp.clear();
                    prefer.add(tmp);
                }

                for (int i = 1; i <= n; i++) { //we get the two most preferred players
                    if (flag_profile[i] == true) {
                        prefer.get(i).add(node.preference.get(i).getFirst());
                        if (node.preference.get(i).size() > 1)
                            prefer.get(i).add(node.preference.get(i).get(1));
                    }
                }

                count = false;

                for (Integer i = 1; i <= n - 2; i++)
                    for (Integer j = i + 1; j <= n - 1; j++)
                        for (Integer k = j + 1; k <= n; k++) {
                            if (flag_profile[i] == true && flag_profile[j] == true && flag_profile[k] == true) {
                                if (prefer.get(i).indexOf(j) != -1 && prefer.get(i).indexOf(k) != -1 && prefer.get(j).indexOf(i) != -1 &&
                                        prefer.get(j).indexOf(k) != -1 && prefer.get(k).indexOf(i) != -1 && prefer.get(k).indexOf(j) != -1) {
                                    count = true;
                                    player_teammates.get(i).add(j);
                                    player_teammates.get(i).add(k);
                                    player_teammates.get(j).add(i);
                                    player_teammates.get(j).add(k);
                                    player_teammates.get(k).add(i);
                                    player_teammates.get(k).add(j);

                                    for (int l = 1; l <= n; l++) {
                                        if (node.preference.get(l).indexOf(i) != -1)
                                            node.preference.get(l).remove(i);
                                        if (node.preference.get(l).indexOf(j) != -1)
                                            node.preference.get(l).remove(j);
                                        if (node.preference.get(l).indexOf(k) != -1)
                                            node.preference.get(l).remove(k);
                                    }
                                    node.order.remove(i);
                                    node.order.remove(j);
                                    node.order.remove(k);

                                    i = n + 1;
                                    j = n + 1;
                                    k = n + 1; //we could use this way to break the two loops
                                }

                            }

                        }
            }
            */

            if (depth <= 0 || node.order.size() == 0 || node.order.size() == 1) {
                return player_teammates;
            }
        }

        Integer proposer= node.order.getFirst();
        while(node.preference.get(proposer).isEmpty())
        {
            if(node.order.size()==1)
                return player_teammates;
            else
            {
                node.order.removeFirst();
                for (int l = 1; l <= n; l++) {
                    if (node.preference.get(l).indexOf(proposer) != -1)
                        node.preference.get(l).remove(proposer);
                }
                proposer=node.order.getFirst();
            }
        }

        Integer receiver= node.preference.get(proposer).get(rank);
        if(node.player_teammates.get(proposer).size()>=1) //it denote whehter the receiver is the first teammate
            is_first_teammate=false;
        else
            is_first_teammate=true;

        //We will compute the heuristic of the accepting or rejecting the proposal.
        //We firstly copy current node into the order_test
        LinkedList<Integer> order_test = new LinkedList<Integer>(); //the order if the receiver accept the offer
        order_test.clear();
        order_test.addAll(node.order);

        //We also copy current preference to test_preference
        ArrayList<LinkedList<Integer>> test_preference= new ArrayList<LinkedList<Integer>>();
        test_preference.clear();
        LinkedList<Integer> non_sense1= new LinkedList<>();
        non_sense1.add(1);
        test_preference.add(non_sense1); //just to fill the index 0, non-sense here
        for(int k=1; k<=n; k++)
        {
            LinkedList<Integer> tmp1_list= new LinkedList<>();
            tmp1_list.addAll(node.preference.get(k));
            test_preference.add(tmp1_list);
        }

        double accept_or_reject_ratio=0;
        double better_ratio= (double)node.preference.get(receiver).indexOf(proposer)/ (node.preference.get(receiver).size());
        int total_other_player= node.preference.get(receiver).indexOf(proposer); //Here it is possible to be 0

        double total_other_ratio=0;
        for(int k=0; k<node.preference.get(receiver).size(); k++)
        {
            Integer other_player= node.preference.get(receiver).get(k);
            if(other_player==proposer) //we only consider players that is better than proposer for the receiver
                break;

            total_other_ratio+= 1 - node.preference.get(other_player).indexOf(receiver)/ node.preference.get(other_player).size();
        }

        if (total_other_player==0)
            accept_or_reject_ratio=0; //there is no other player, then it has to accept the proposal
        else
            accept_or_reject_ratio= better_ratio* (total_other_ratio/total_other_player);

        if (is_first_teammate==false) //if there are two players in the team, we need to look at
        {
            Integer another_teammate= node.player_teammates.get(proposer).getFirst(); //means the player that has joined the team
            double accept_or_reject_ratio_teammate= 0; //here it denote the ratio for another teammate

            double better_ratio_teammate; //here denote the first part of the equation
            int total_other_player_teammate;
            double total_other_ratio_teammate=0;
            if(node.preference.get(receiver).indexOf(another_teammate)!=-1) {
                better_ratio_teammate = (double) node.preference.get(receiver).indexOf(another_teammate) / (node.preference.get(receiver).size());
                total_other_player_teammate= node.preference.get(receiver).indexOf(another_teammate);
                for(int k=0; k<node.preference.get(receiver).size(); k++)
                {
                    Integer other_player_teammate= node.preference.get(receiver).get(k);
                    if(other_player_teammate==another_teammate) //we only consider players that is better than another_teammate for the receiver
                        break;
                    total_other_ratio_teammate+= 1 - node.preference.get(other_player_teammate).indexOf(receiver)/ node.preference.get(other_player_teammate).size();
                }
            }
            else {
                better_ratio_teammate = 1; //if there is no edge between the receiver and the another_teammate, then we set it to be 1
                total_other_player_teammate= node.preference.get(receiver).size();
                for(int k=0; k<node.preference.get(receiver).size(); k++)
                {
                    Integer other_player_teammate= node.preference.get(receiver).get(k);
                    total_other_ratio_teammate+= 1 - node.preference.get(other_player_teammate).indexOf(receiver)/ node.preference.get(other_player_teammate).size();
                }
            }

            if(total_other_player_teammate==0)
                accept_or_reject_ratio_teammate=0;
            else
                accept_or_reject_ratio_teammate= better_ratio_teammate* (total_other_ratio_teammate/total_other_player_teammate);

            accept_or_reject_ratio= (accept_or_reject_ratio+ accept_or_reject_ratio_teammate)/2; //then we take the average of the two ratio as the estimate of the likelihood
        }

        //Then we will look at accept_or_reject_ratio to prune some subtrees
        if(accept_or_reject_ratio<= accept_threshold)
        {
            order_test.remove(receiver);
            for(int k=1; k<=n; k++) {
                if(test_preference.get(k).indexOf(receiver)!=-1)
                    test_preference.get(k).remove(receiver);
            }

            if(is_first_teammate==false) //if there have been paired 3 players, then delete the proposer
            {
                order_test.remove(proposer);
                for(int k=1; k<=n; k++) {
                    if(test_preference.get(k).indexOf(proposer)!=-1)
                        test_preference.get(k).remove(proposer);
                }
            }

            ArrayList<LinkedList<Integer>> test_player_teammates = new ArrayList<LinkedList<Integer>>();
            for(int i=0; i<=n; i++) {
                LinkedList<Integer>tmp_list= new LinkedList<Integer>();
                tmp_list.addAll(player_teammates.get(i));
                test_player_teammates.add(tmp_list);
            }
            if(is_first_teammate==false) //when there has been one another player in the team, form them
            {
                int another_teammate= test_player_teammates.get(proposer).getFirst();
                test_player_teammates.get(another_teammate).add(receiver);
                test_player_teammates.get(receiver).add(another_teammate);
            }
            test_player_teammates.get(proposer).add(receiver);
            test_player_teammates.get(receiver).add(proposer);

            if(!order_test.isEmpty())
            {
                Integer newplayer = order_test.getFirst();
                Node accept_node = new Node(newplayer, order_test, test_preference, test_player_teammates);
                int tmp_depth=depth;
                if(is_first_teammate==false)
                    tmp_depth--;
                if(tmp_depth>=order_test.size()) //if the depth is greater than the order size, then we do not need to search that deep
                    tmp_depth=order_test.size();

                if(is_first_teammate==false || rank== test_preference.get(proposer).size())
                    rank=0;   //just want to say that if we form receiver inside proposer, then we delete the receiver from the preference of proposer, so rank doesn't need to change, which is different from reject the proposal

                test_player_teammates= DFS(n, accept_node, test_player_teammates, tmp_depth, value, linked_value, rank, !is_first_teammate ,accept_threshold);
            }
            return test_player_teammates;

        }
        else
        {
            int tmp_depth_1 = depth;
            if(rank== test_preference.get(proposer).size()-1)
            {
                rank=0;
                order_test.removeFirst();
                for(int k=1; k<=n; k++) {
                    if(test_preference.get(k).indexOf(proposer)!=-1)
                        test_preference.get(k).remove(proposer);
                }
                tmp_depth_1--;
                if(tmp_depth_1>=order_test.size())
                    tmp_depth_1=order_test.size();
            }
            else
                rank++;

            Integer newplayer1=order_test.getFirst();
            Node reject_node= new Node(newplayer1, order_test, test_preference, player_teammates);
            ArrayList<LinkedList<Integer>> test_player_teammates= DFS(n, reject_node, player_teammates, tmp_depth_1, value, linked_value, rank, is_first_teammate, accept_threshold);
            return test_player_teammates;
        }

    }
}


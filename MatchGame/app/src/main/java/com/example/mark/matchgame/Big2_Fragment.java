package com.example.mark.matchgame;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;



/**
 * Created by Mark on 01/03/2016.
 */
public class Big2_Fragment extends Fragment {

    private LinearLayout topLayer[];
    private LinearLayout middleLayer[];
    private LinearLayout bottomLayer[];

    private TextView userTextView; //dont think i need this
    private TextView player2TextView;
    private TextView player2CTextView;
    private TextView player3TextView;
    private TextView player3CTextView;
    private TextView player4TextView;
    private TextView player4CTextView;
    private TextView fromPlayerTextView;
    private TextView fromTextView;

    private TextView M1,M2,M3,M4,M5;

    private int totalSelectedCards;
    private int cardsOnTable;

    private ArrayList<Card> deck;

    private ArrayList<Card> userCards;

    private ArrayList<Card> p2Cards;
    private CardStats p2Stats;
    private ArrayList<Card> p3Cards;
    private CardStats p3Stats;
    private ArrayList<Card> p4Cards;
    private CardStats p4Stats;

    private String whoStarts;
    private String nextPlayer;

    private Handler handler;

    private final static String p1 = "USER";
    private final static String p2 = "PLAYER 2";
    private final static String p3 = "PLAYER 3";
    private final static String p4 = "PLAYER 4";

    private int p2C;
    private int p3C;
    private int p4C;
    private boolean firstSet = true;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.big_2, container, false);

        //counters
        totalSelectedCards = 0;
        cardsOnTable = 0;
        p2C = 13;
        p3C = 13;
        p4C = 13;
        p2Stats = new CardStats();
        p3Stats = new CardStats();
        p4Stats = new CardStats();
        handler = new Handler();

        //player cards
        deck = new ArrayList<Card>();
        userCards = new ArrayList<Card>();
        p2Cards = new ArrayList<Card>();
        p3Cards = new ArrayList<Card>();
        p4Cards = new ArrayList<Card>();

        //these three lines may not be needed
        topLayer = new LinearLayout[2];
        topLayer[0] = (LinearLayout)view.findViewById(R.id.topLayerRow1);
        topLayer[1] = (LinearLayout)view.findViewById(R.id.topLayerRow2);

        player2TextView = (TextView)view.findViewById(R.id.player2);
        player3TextView = (TextView)view.findViewById(R.id.player3);
        player4TextView = (TextView)view.findViewById(R.id.player4);

        player2CTextView= (TextView)view.findViewById(R.id.player2C);
        player3CTextView= (TextView)view.findViewById(R.id.player3C);
        player4CTextView= (TextView)view.findViewById(R.id.player4C);

        middleLayer = new LinearLayout[2];
        middleLayer[0] = (LinearLayout)view.findViewById(R.id.middleLayerRow1);

        fromPlayerTextView = (TextView)view.findViewById(R.id.fromPlayerTextView);
        fromTextView= (TextView)view.findViewById(R.id.fromTextView);


        middleLayer[1] = (LinearLayout)view.findViewById(R.id.middleLayerRow2);


        M1 = (TextView)view.findViewById(R.id.M1);
        M2 = (TextView)view.findViewById(R.id.M2);
        M3 = (TextView)view.findViewById(R.id.M3);
        M4 = (TextView)view.findViewById(R.id.M4);
        M5 = (TextView)view.findViewById(R.id.M5);


        for(LinearLayout row : middleLayer){
            for (int i = 0; i < row.getChildCount(); i++){
                if(row == middleLayer[1]){
                    TextView textView = (TextView)row.getChildAt(i);
                    textView.setText("-1");
                    textView.setTextColor(Color.TRANSPARENT);
                    textView.setBackgroundColor(Color.CYAN);
                    textView.setClickable(false);
                }
            }
        }

        bottomLayer = new LinearLayout[2];
        bottomLayer[0]= (LinearLayout)view.findViewById(R.id.bottomLayerRow1);
        bottomLayer[1]= (LinearLayout)view.findViewById(R.id.bottomLayerRow2);

        makeDeck();
        distributeCards();
        initialize_playerView();

        play3(); //this looks for whoever has the diamond. not done yet pls implement!

        return view;
    }

    public void play3(){

        if(look_for_3(userCards)){
            whoStarts = p1;
            nextPlayer = p2;
        }
        else if (look_for_3(p2Cards)){
            whoStarts = p2;
            disablePlayerClickable();
            handler.postDelayed(new Runnable(){
                @Override
                public void run() {

                    p2Cards.remove(0);
                    player2CTextView.setText("Cards: " + (p2C -1));
                    fromPlayerTextView.setText(whoStarts);
                    M3.setText("3");
                    M3.setTextColor(Color.BLACK);
                    M3.setBackgroundColor(Color.CYAN);

                    nextPlayer = p3;

                }
            },3000);
        }
        else if (look_for_3(p3Cards)){
            whoStarts = p3;

            disablePlayerClickable();
            handler.postDelayed(new Runnable(){
                @Override
                public void run() {
                    p3Cards.remove(0);
                    player3CTextView.setText("Cards: " + (p3C -1));
                    fromPlayerTextView.setText(whoStarts);
                    M3.setText("3");
                    M3.setTextColor(Color.BLACK);
                    M3.setBackgroundColor(Color.CYAN);

                    nextPlayer = p4;
                }
            },3000);
        }
        else{
            whoStarts = p4;
            disablePlayerClickable();

            handler.postDelayed(new Runnable(){
                @Override
                public void run() {
                    p4Cards.remove(0);
                    player4CTextView.setText("Cards: " + (p4C -1));
                    fromPlayerTextView.setText(whoStarts);
                    M3.setText("3");
                    M3.setTextColor(Color.BLACK);
                    M3.setBackgroundColor(Color.CYAN);
                    nextPlayer = p1;
                }
            },3000);
        }

    }

    public void logicalPlay(String num){
        //if this is the first time card(s) has been put on the table
        if (firstSet){

            makeHand(whoStarts, num);
            firstSet = false;
        }
        else{

        }


    }

    /*Overload functions*/
    public void makeHand(String currentPlayer){


    }

    public void makeHand(String currentPlayer, String num){

        if(currentPlayer.equals(p2)){


        }

    }


    //finds the diamond 3
    public boolean look_for_3 (ArrayList<Card> cardHand){
        for(int i = 0; i < cardHand.size(); i++){
            if (cardHand.get(i).getCardValue().equals("3") && cardHand.get(i).getCardRank().equals("D")){
                return true;
            }
        }
        return false;
    }

    //shows user what cards he/she has in a sorted order
    public void initialize_playerView(){

        for(LinearLayout row : bottomLayer){
            for(int i = 0; i < row.getChildCount(); i++){
                TextView textView = (TextView)row.getChildAt(i);
                textView.setClickable(true);
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundColor(Color.CYAN);

                if((row == bottomLayer[1]) && (i == 6)){
                    textView.setOnClickListener(submitListener);
                }
                else{
                    textView.setOnClickListener(playerPick);
                    if(row == bottomLayer[0]){
                        if(userCards.get(i).getCardValue().equals("1")){
                            textView.setText("Ace" + userCards.get(i).getCardRank());
                        }
                        else if(userCards.get(i).getCardValue().equals("11")){
                            textView.setText("J" + userCards.get(i).getCardRank());
                        }
                        else if(userCards.get(i).getCardValue().equals("12")){
                            textView.setText("Q" + userCards.get(i).getCardRank());
                        }
                        else if(userCards.get(i).getCardValue().equals("13")){
                            textView.setText("K" + userCards.get(i).getCardRank());
                        }
                        else {
                            textView.setText(userCards.get(i).getCardValue() + userCards.get(i).getCardRank());
                        }
                    }
                    else{
                        if(userCards.get(7+i).getCardValue().equals("1")){
                            textView.setText("Ace" + userCards.get(7+i).getCardRank());
                        }
                        else if(userCards.get(7+i).getCardValue().equals("11")){
                            textView.setText("J" + userCards.get(7+i).getCardRank());
                        }
                        else if(userCards.get(7+i).getCardValue().equals("12")){
                            textView.setText("Q" + userCards.get(7+i).getCardRank());
                        }
                        else if(userCards.get(7+i).getCardValue().equals("13")){
                            textView.setText("K" + userCards.get(7+i).getCardRank());
                        }
                        else {
                            textView.setText(userCards.get(7+i).getCardValue() + userCards.get(7+i).getCardRank());
                        }
                    }

                }
            }
        }
    }


    //makes a deck of cards and shuffles it
    public void makeDeck(){
        String baseList[] = {"1", "2", "3", "4", "5", "6", "7", "8","9","10","11","12","13"};
        String baseSign[] = {"S","H","C","D"};

        for(int i = 0; i < 13; i++){
            for(int j =0; j < 4; j++){
                Card tempCard = new Card();
                tempCard.setCardValue((baseList[i]));
                tempCard.setCardRank(baseSign[j]);
                tempCard.setIsClicked(false);
                deck.add(tempCard);
            }
        }
        Collections.shuffle(deck);




    }

    //distribute cards
    public void distributeCards(){
        for(int i = 0; i < 51; i +=4){
            userCards.add(deck.get(i));
            p2Cards.add(deck.get(i+1));
            p2Stats.cardStats[Integer.parseInt(deck.get(i+1).getCardValue())-1]++;
            p3Cards.add(deck.get(i+2));
            p3Stats.cardStats[Integer.parseInt(deck.get(i+2).getCardValue())-1]++;
            p4Cards.add(deck.get(i+3));
            p4Stats.cardStats[Integer.parseInt(deck.get(i+3).getCardValue())-1]++;
        }

        insertionSort(userCards);
        insertionSort(p2Cards);
        insertionSort(p3Cards);
        insertionSort(p4Cards);

    }

    //sort cards
    public void insertionSort(ArrayList<Card> hand){

        for (int i = 1; i < hand.size(); i++){
            int j = i;
                while (j > 0 && (Integer.parseInt(hand.get(j - 1).getCardValue()) >= Integer.parseInt(hand.get(j).getCardValue()))) {
                    Card temp = new Card(hand.get(j).getCardValue(), hand.get(j).getCardRank());

                    if(Integer.parseInt(hand.get(j-1).getCardValue()) == Integer.parseInt(hand.get(j).getCardValue())){
                        if(hand.get(j-1).getCardRank().compareTo(hand.get(j).getCardRank()) > 0){
                            hand.get(j).setCardValue(hand.get(j - 1).getCardValue());
                            hand.get(j).setCardRank(hand.get(j - 1).getCardRank());
                            hand.get(j - 1).setCardValue(temp.getCardValue());
                            hand.get(j - 1).setCardRank(temp.getCardRank());
                            j = j - 1;
                        }
                        else{
                            j= j-1;
                        }
                    }
                    else{
                        hand.get(j).setCardValue(hand.get(j - 1).getCardValue());
                        hand.get(j).setCardRank(hand.get(j - 1).getCardRank());
                        hand.get(j - 1).setCardValue(temp.getCardValue());
                        hand.get(j - 1).setCardRank(temp.getCardRank());

                        j = j - 1;
                    }


                }

        }
    }

    public OnClickListener submitListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            if(totalSelectedCards == 0){
                //not enough cards selected do nothing
            }

            //more than 0 cards selected then check if its a valid play
            else{

            }

        }
    };

    public OnClickListener playerPick = new OnClickListener(){
        @Override
        public void onClick(View v) {



        }
    };


    //disable clicks
    public void disablePlayerClickable(){
        for(LinearLayout row : bottomLayer){
            for(int i = 0; i < row.getChildCount(); i++){
                TextView textView = (TextView)row.getChildAt(i);
                textView.setClickable(false);
            }
        }
    }

    //enable clicks
    public void enablePlayerClickable(){
        for(LinearLayout row : bottomLayer){
            for(int i = 0; i < row.getChildCount(); i++){
                TextView textView = (TextView)row.getChildAt(i);
                textView.setClickable(true);
            }
        }
    }


    public class CardStats{
        int cardStats[];
        public CardStats(){
            cardStats = new int[13];
        }
    }
}

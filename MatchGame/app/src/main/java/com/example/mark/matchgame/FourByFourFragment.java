package com.example.mark.matchgame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.security.SecureRandom;

/**
 * Created by Mark on 25/02/2016.
 */
public class FourByFourFragment extends Fragment{

    private LinearLayout[] guessLinearLayout;
    private TextView c1;
    private TextView c2;

    private int counter;
    private final static int FINAL_SCORE = 16;
    private final static int DIFFERENT_NUM_NUMBERS = 8;
    private int SCORE;
    private SecureRandom Mrandom;
    private Handler handler;

    private Animation firstCardAnim1;
    private Animation firstCardAnim2;
    private Animation secondCardAnim1;
    private Animation secondCardAnim2;

    private boolean CardBack = true;
    private boolean CardBack2 = true;


    private View currentCard;

    private int numberOfGuesses;
    private int numberOfCorrectGuesses;

    private long startTime;
    private long endTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.easy_mode,container,false);

        SCORE = 0;
        counter = 0;
        Mrandom = new SecureRandom();
        handler = new Handler();

        numberOfGuesses = 0;
        numberOfCorrectGuesses = 0;


        //animation initializations
        firstCardAnim1 = AnimationUtils.loadAnimation(getActivity(),R.anim.flip_to_mid);
        firstCardAnim1.setAnimationListener(firstCardFlip);

        firstCardAnim2 = AnimationUtils.loadAnimation(getActivity(),R.anim.mid_to_end);
        firstCardAnim2.setAnimationListener(firstCardFlip);

        secondCardAnim1 = AnimationUtils.loadAnimation(getActivity(),R.anim.flip_to_mid);
        secondCardAnim1.setAnimationListener(secondCardFlip);

        secondCardAnim2 = AnimationUtils.loadAnimation(getActivity(),R.anim.mid_to_end);
        secondCardAnim2.setAnimationListener(secondCardFlip);



        guessLinearLayout= new LinearLayout[4];
        guessLinearLayout[0] = (LinearLayout)view.findViewById(R.id.linearLayout1);
        guessLinearLayout[1] = (LinearLayout)view.findViewById(R.id.linearLayout2);
        guessLinearLayout[2] = (LinearLayout)view.findViewById(R.id.linearLayout3);
        guessLinearLayout[3] = (LinearLayout)view.findViewById(R.id.linearLayout4);

        for(LinearLayout row : guessLinearLayout){

            for(int column = 0; column < row.getChildCount(); column++){
                TextView textView = (TextView)row.getChildAt(column);
                textView.setText("0");
                textView.setTextColor(Color.TRANSPARENT);
                textView.setBackgroundColor(Color.BLACK);
                textView.setOnClickListener(cardClickListener);
            }
        }

        //need to randomly place numbers

        placeNumbers();

        startTime = System.currentTimeMillis();




        return view;
    }


    //method to randomly place numbers in the 4 by 4 grid
    public void placeNumbers(){

        for (int i = 0; i < DIFFERENT_NUM_NUMBERS; i++){


            int tempCounter = 0;
            while(tempCounter < 2) {

                int tempRow = Mrandom.nextInt(4);
                int tempCol = Mrandom.nextInt(4);
                LinearLayout randomRow = guessLinearLayout[tempRow];
                TextView textView = (TextView)randomRow.getChildAt(tempCol);

                if( (Integer.parseInt(textView.getText().toString()) != (i+1)) && textView.getText().toString().equals("0")) {
                    String stringNumber = "" + (i+1);
                    textView.setText(stringNumber);
                    tempCounter++;
                }

            }
        }
    }



    public OnClickListener cardClickListener = new OnClickListener(){
        @Override
        public void onClick(View v) {


            TextView view = (TextView)v;

            //animations
            CardBack = true;
            currentCard = v;
            v.clearAnimation();


            //if first card is picked
            if(counter == 0){

                v.setAnimation(firstCardAnim1);
                v.startAnimation(firstCardAnim1);

                counter++;
                c1 = view;
                disableClickable();
                handler.postDelayed(
                        new Runnable(){
                            @Override
                            public void run() {
                                enableClickable();
                                c1.setClickable(false);
                            }
                        },200); //play around with this number -atm the other cards are only flipable when the first animation is done

            }

            //second card is picked

            else if (counter == 1){
                c2 = view;

                v.setAnimation(secondCardAnim1);
                v.startAnimation(secondCardAnim1);

                disableClickable();
                if(check()){ //if two cards are the same
                    handler.postDelayed(
                            new Runnable(){
                                @Override
                                public void run() {
                                    c1.setBackgroundColor(Color.WHITE);
                                    c1.setTextColor(Color.TRANSPARENT);

                                    c2.setBackgroundColor(Color.WHITE);
                                    c2.setTextColor(Color.TRANSPARENT);

                                    c1.setText("-1");
                                    c2.setText("-1");

                                    enableClickable();



                                }
                            },500);
                    numberOfGuesses++;
                    numberOfCorrectGuesses++;
                    SCORE += 2;



                }
                else{

                    handler.postDelayed(
                            new Runnable(){
                                @Override
                                public void run() {
                                    //c1.setClickable(true);
                                    c1.setTextColor(Color.TRANSPARENT);
                                    c2.setTextColor(Color.TRANSPARENT);
                                    c1.setBackgroundColor(Color.BLACK);
                                    c2.setBackgroundColor(Color.BLACK);
                                    enableClickable();
                                }
                            },500);

                    numberOfGuesses++;

                }

                counter = 0; //reset number of cards chosen

                if(gameOver()){ //if all matches have been made
                    endTime = System.currentTimeMillis();
                    disableClickable();

                    double Accuracy = (double)numberOfCorrectGuesses/(double)numberOfGuesses;
                    double finalAccuracy = Math.round(Accuracy * 100);
                    double finalScore = Math.round(SCORE * 100 * Accuracy);
                    long totalTime = endTime - startTime;

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Congratulations! You Win!");
                    builder.setMessage("Score: " + finalScore + "\nTime: " + (totalTime/1000)
                    + "\nNumber of Guesses: " + numberOfGuesses + "\nNumber of Correct Guesses: " + numberOfCorrectGuesses +
                            "\nAccuracy: " + finalAccuracy + "%");
                    builder.setPositiveButton("Reset", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.postDelayed(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            gameReset();//to make sure the previous game is actually complete
                                        }
                                    },125);

                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("End", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            displayGameOver();

                            //getActivity().startActivity(new Intent(getActivity(),MainActivity.class));
                        }
                    });
                    builder.show();
                }
            }
            else{ //this is not really needed so consider using boolean true/false for the other ifs

            }
            //enableClickable();


        }
    };



    public boolean check(){
        if(c1.getText().toString().equals(c2.getText().toString())){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean gameOver(){
        if(SCORE == FINAL_SCORE){
            return true;
        }
        else{
            return false;
        }
    }

    public void gameReset(){

        SCORE = 0;
        counter = 0;

        endTime = 0;
        numberOfCorrectGuesses = 0;
        numberOfGuesses = 0;
        //c1 = null;
        //c2 = null;
        for(LinearLayout row : guessLinearLayout){

            for(int col = 0; col < row.getChildCount(); col++){
                TextView textView = (TextView)row.getChildAt(col);
                textView.setClickable(true);
                textView.setText("0");
                textView.setBackgroundColor(Color.BLACK);
                textView.setTextColor(Color.TRANSPARENT);
                textView.setOnClickListener(cardClickListener); // not sure if this line is needed, need to test
            }
        }

        placeNumbers();
        startTime = System.currentTimeMillis();
    }

    public void disableClickable(){
        for (LinearLayout row : guessLinearLayout){
            for(int col = 0; col < row.getChildCount(); col++){
                TextView textView = (TextView)row.getChildAt(col);
                textView.setClickable(false);
            }
        }
    }

    public void enableClickable(){
        for (LinearLayout row : guessLinearLayout){
            for(int col = 0; col < row.getChildCount(); col++){
                TextView textView = (TextView)row.getChildAt(col);
                if(!textView.getText().toString().equals("-1")) {
                    textView.setClickable(true);
                }
            }
        }
    }


    public Animation.AnimationListener firstCardFlip = new Animation.AnimationListener(){
        @Override
        public void onAnimationStart(Animation animation) {


        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(animation == firstCardAnim1){
                if(CardBack){ //if the back of card 1 is showing
                    //show back
                    TextView textView = (TextView)currentCard;
                    textView.setTextColor(Color.WHITE);
                    textView.setBackgroundColor(Color.RED);
                    CardBack = false;

                }

                currentCard.startAnimation(firstCardAnim2);
            }
            else{
                CardBack = !CardBack;
            }


        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    public Animation.AnimationListener secondCardFlip = new Animation.AnimationListener(){
        @Override
        public void onAnimationStart(Animation animation) {


        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(animation == secondCardAnim1){
                if(CardBack2){ //if the back of card 1 is showing
                    //show back
                    TextView textView = (TextView)currentCard;
                    textView.setTextColor(Color.WHITE);
                    textView.setBackgroundColor(Color.RED);
                    CardBack2 = false;

                }

                currentCard.startAnimation(secondCardAnim2);
            }
            else{
                CardBack2 = !CardBack2;
            }


        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };



    public void displayGameOver(){

        LinearLayout row2 = guessLinearLayout[1];
        LinearLayout row3 = guessLinearLayout[2];

        String gameString[]  = {"G", "A", "M","E"};
        String overString[]  = {"O", "V", "E", "R"};

        for (int i = 0; i < row2.getChildCount(); i++){


            TextView textView = (TextView)row2.getChildAt(i);
            textView.setText(gameString[i]);
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.RED);
        }

        for (int i = 0; i < row3.getChildCount(); i++){



            TextView textView = (TextView)row3.getChildAt(i);
            textView.setText(overString[i]);
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.RED);
        }

    }




}

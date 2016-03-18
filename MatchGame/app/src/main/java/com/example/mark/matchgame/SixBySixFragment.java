package com.example.mark.matchgame;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;


/**
 * Created by Mark on 26/02/2016.
 */
public class SixBySixFragment extends Fragment {


    private LinearLayout[] sbsLayout;

    private int counter;
    private TextView c1;
    private TextView c2;
    private TextView scoreTextView;
    private TextView timeTextView;
    private final int DIFFERENT_NUM_NUMBERS = 18;
    private final int MAX_SCORE = 36;
    private int SCORE;
    private SecureRandom nRandom;

    private View currentCard;
    private Handler handler;
    private boolean CardBack = true;
    private boolean CardBack2 = true;


    private Animation firstCardAnim1;
    private Animation firstCardAnim2;
    private Animation secondCardAnim1;
    private Animation secondCardAnim2;

    private int numberOfGuesses;
    private int numberOfCorrectGuesses;

    private int multiplier;
    private static final int numPairs = 18;
    private int pairs;

    private long startTime;
    private long endTime;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.medium_mode,container,false);

        SCORE = 0;
        counter = 0;
        handler = new Handler();
        nRandom = new SecureRandom();

        numberOfGuesses = 0;
        numberOfCorrectGuesses = 0;
        multiplier = 1;

        pairs = 0;

        firstCardAnim1 = AnimationUtils.loadAnimation(getActivity(),R.anim.flip_to_mid);
        firstCardAnim1.setAnimationListener(firstCardFlip);

        firstCardAnim2 = AnimationUtils.loadAnimation(getActivity(),R.anim.mid_to_end);
        firstCardAnim2.setAnimationListener(firstCardFlip);

        secondCardAnim1 = AnimationUtils.loadAnimation(getActivity(),R.anim.flip_to_mid);
        secondCardAnim1.setAnimationListener(secondCardFlip);

        secondCardAnim2 = AnimationUtils.loadAnimation(getActivity(),R.anim.mid_to_end);
        secondCardAnim2.setAnimationListener(secondCardFlip);

        scoreTextView = (TextView)view.findViewById(R.id.scoreTextView);
        scoreTextView.setText("Score: 0");
        timeTextView = (TextView)view.findViewById(R.id.timeTextView);
        timeTextView.setVisibility(View.INVISIBLE);

        sbsLayout = new LinearLayout[6];
        sbsLayout[0] = (LinearLayout)view.findViewById(R.id.sbsLinearLayout1);
        sbsLayout[1] = (LinearLayout)view.findViewById(R.id.sbsLinearLayout2);
        sbsLayout[2] = (LinearLayout)view.findViewById(R.id.sbsLinearLayout3);
        sbsLayout[3] = (LinearLayout)view.findViewById(R.id.sbsLinearLayout4);
        sbsLayout[4] = (LinearLayout)view.findViewById(R.id.sbsLinearLayout5);
        sbsLayout[5] = (LinearLayout)view.findViewById(R.id.sbsLinearLayout6);

        for(LinearLayout row : sbsLayout){

            for(int col = 0; col < row.getChildCount(); col++){
                TextView textView = (TextView)row.getChildAt(col);
                textView.setBackground(getResources().getDrawable(R.mipmap.cardback2));
                textView.setText("0");
                textView.setTextColor(Color.TRANSPARENT);
                textView.setOnClickListener(cardClickListener);
            }
        }

        placeNumbers();

        startTime = System.currentTimeMillis();

        return view;
    }

    public OnClickListener cardClickListener = new OnClickListener(){
        @Override
        public void onClick(View v) {

            View view = v;
            CardBack = true;

            currentCard = v;
            v.clearAnimation();



            if(counter == 0){
                c1 = (TextView)view;
                counter++;
                disableClickable();
                v.setAnimation(firstCardAnim1);
                v.startAnimation(firstCardAnim1);
                handler.postDelayed(
                        new Runnable(){
                            @Override
                            public void run() {
                                enableClickable();
                                c1.setClickable(false);
                            }
                        },200);

            }
            else if (counter == 1){
                c2 = (TextView)view;
                disableClickable();

                v.setAnimation(secondCardAnim1);
                v.startAnimation(secondCardAnim1);
                if(check()){
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
                    pairs++;
                    multiplier++;
                    SCORE += 100 * multiplier;
                    scoreTextView.setText("Score: " + SCORE);
                    numberOfCorrectGuesses++;
                    numberOfGuesses++;
                }
                else{
                    handler.postDelayed(
                            new Runnable(){
                                @Override
                                public void run() {
                                    c1.setBackground(getResources().getDrawable(R.mipmap.cardback2));
                                    c1.setTextColor(Color.TRANSPARENT);

                                    c2.setBackground(getResources().getDrawable(R.mipmap.cardback2));
                                    c2.setTextColor(Color.TRANSPARENT);
                                    enableClickable();
                                }
                            },500);

                    numberOfGuesses++;
                    multiplier = 1;
                }
                counter = 0;


                if(gameOver()){
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
                    builder.setPositiveButton("Reset",new DialogInterface.OnClickListener(){
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
                        }
                    });

                    builder.show();
                }

            }

        }
    };


    public boolean gameOver(){

        if(pairs == numPairs){
            return true;
        }

        else{
            return false;
        }
    }
    public boolean check(){
        if(c1.getText().toString().equals(c2.getText().toString())){
            return true;
        }
        else{
            return false;
        }
    }

    public void disableClickable(){
        for(LinearLayout row : sbsLayout){
            for(int col =0; col < row.getChildCount(); col++){
                TextView textView = (TextView)row.getChildAt(col);
                textView.setClickable(false);
            }
        }
    }

    public void enableClickable(){
        for(LinearLayout row : sbsLayout){
            for(int col =0; col < row.getChildCount(); col++){

                TextView textView = (TextView)row.getChildAt(col);
                if(!textView.getText().toString().equals("-1")) {
                    textView.setClickable(true);
                }
            }
        }
    }

    public void placeNumbers(){

        for(int i =0; i < DIFFERENT_NUM_NUMBERS; i++){

            int tempCounter = 0;
            while(tempCounter < 2 ){
                int tempRow = nRandom.nextInt(6);
                int tempCol = nRandom.nextInt(6);
                LinearLayout randomRow = sbsLayout[tempRow];
                TextView textView = (TextView)randomRow.getChildAt(tempCol);
                if((Integer.parseInt(textView.getText().toString()) != (i+1)) && textView.getText().toString().equals("0")){
                    textView.setText("" + (i+1));
                    tempCounter++;
                }

            }

        }

    }

    public void gameReset(){

        SCORE = 0;
        counter =0;
        numberOfGuesses = 0;
        numberOfCorrectGuesses = 0;
        multiplier =1;
        pairs = 0;

        endTime = 0;
        //c1 = null;
        //c2 = null;

        for(LinearLayout row : sbsLayout){
            for(int col =0; col < row.getChildCount();col++){
                TextView textView = (TextView)row.getChildAt(col);
                textView.setText("0");
                textView.setClickable(true);
                textView.setBackgroundColor(Color.BLACK);
                textView.setTextColor(Color.TRANSPARENT);
            }
        }

        placeNumbers();
        startTime = System.currentTimeMillis();

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
                    textView.setBackground(getResources().getDrawable(R.mipmap.cardfront));
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
                if(CardBack){ //if the back of card 1 is showing
                    //show back
                    TextView textView = (TextView)currentCard;
                    textView.setTextColor(Color.WHITE);
                    textView.setBackground(getResources().getDrawable(R.mipmap.cardfront));
                    CardBack = false;

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

        LinearLayout row3 = sbsLayout[2];
        LinearLayout row4 = sbsLayout[3];

        String gameString[]  = {"G", "A", "M","E"};
        String overString[]  = {"O", "V", "E", "R"};

        for (LinearLayout row : sbsLayout){
            for(int k =0; k < row.getChildCount(); k++){
                TextView textView = (TextView)row.getChildAt(k);
                textView.setBackground(getResources().getDrawable(R.mipmap.cardback2));
                textView.setVisibility(View.VISIBLE);
            }
        }

        for (int i = 0; i < 4; i++){

            TextView textView = (TextView)row3.getChildAt(i);
            textView.setText(gameString[i]);
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.RED);
        }

        for (int i = 0; i < 4; i++){

            TextView textView = (TextView)row4.getChildAt(i+2);
            textView.setText(overString[i]);
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.RED);
        }

    }



}

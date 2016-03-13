package com.example.mark.matchgame;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;

/**
 * Created by Mark on 04/03/2016.
 */
public class Diverse6by6Fragment extends Fragment {

    private SecureRandom mRandom;
    private LinearLayout funSixBySix[];

    private Animation card1Animation1;
    private Animation card1Animation2;
    private Animation card2Animation1;
    private Animation card2Animation2;

    private boolean cardBack1 = true;
    private boolean cardBack2 = true;

    private int numOfGuesses;
    private int levelsCompleted;

    //Score System
    private int SCORE;
    private static final int TEN = 10;
    private static final int FIFTY = 50;
    private static final int HUNDRED = 100;
    private static final int FIVEHUNDRED = 500;
    private static final int THOUSAND = 1000;
    private int multiplier;


    private GameTimer roundTime;
    private long timeLeft;
    private final static long initialTime = 15000; //15 seconds
    private final static long INTERVAL = 1000;

    private int collectedPairs;

    private int pairsInRound;

    private boolean Rows[];
    private int existingNumbers[];
    private int RowCount;

    private TextView c1;
    private TextView c2;

    private TextView scoreTextView;
    private TextView timeTextView;

    private View currentCard;

    private int numCardClicked;
    private int numOfCorrectGuesses;

    private Handler handler;

    private boolean hasTimerStart = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.sixbysix, container, false);

        //initialize all variables
        Rows = new boolean[6]; //array indicating whether a row is visible or not
        existingNumbers = new int[16]; //array to hold the number of existing number pairs in round

        RowCount = 0; //use to keep track of number of rows that should be displayed in the current round

        numCardClicked = 0; //variable to indicate the number of cards currently clicked
        SCORE = 0; //variable to hold the users score
        multiplier = 1; //variable to hold current multiplier
        levelsCompleted = 0; //variable to hold the number of levels the user has completed


        collectedPairs = 0; //variable to hold the current number of pairs the user has collected in the round
        pairsInRound = 0; //variable to hold the total number of pairs in the current round

        numOfGuesses = 0; //variable to hold the total number of guesses the user has made
        numOfCorrectGuesses = 0; //variable to hold the total number of correct guesses the user has made

        mRandom = new SecureRandom(); //used to create a random number
        handler = new Handler(); //used to handle delays

        //initialize animations
        card1Animation1 = AnimationUtils.loadAnimation(getActivity(),R.anim.flip_to_mid);
        card1Animation1.setAnimationListener(flip1);

        card2Animation1 = AnimationUtils.loadAnimation(getActivity(),R.anim.flip_to_mid);
        card2Animation1.setAnimationListener(flip2);

        card1Animation2 = AnimationUtils.loadAnimation(getActivity(),R.anim.mid_to_end);
        card1Animation2.setAnimationListener(flip1);

        card2Animation2 = AnimationUtils.loadAnimation(getActivity(),R.anim.mid_to_end);
        card2Animation2.setAnimationListener(flip2);


        //intialize layout
        scoreTextView = (TextView)view.findViewById(R.id.scoreTextView);
        timeTextView = (TextView)view.findViewById(R.id.timeTextView);
        timeTextView.setVisibility(View.VISIBLE);

        funSixBySix = new LinearLayout[6];
        funSixBySix[0]= (LinearLayout)view.findViewById(R.id.sbsLinearLayout1);
        funSixBySix[1]= (LinearLayout)view.findViewById(R.id.sbsLinearLayout2);
        funSixBySix[2]= (LinearLayout)view.findViewById(R.id.sbsLinearLayout3);
        funSixBySix[3]= (LinearLayout)view.findViewById(R.id.sbsLinearLayout4);
        funSixBySix[4]= (LinearLayout)view.findViewById(R.id.sbsLinearLayout5);
        funSixBySix[5]= (LinearLayout)view.findViewById(R.id.sbsLinearLayout6);

        //initialize all textViews for now.
        for (LinearLayout row : funSixBySix){
            for (int i = 0; i < row.getChildCount(); i++){
                TextView textView = (TextView) row.getChildAt(i);
                //textView.setBackgroundColor(Color.BLACK);
                textView.setBackground(getResources().getDrawable(R.mipmap.cardback2));
                textView.setText("0");
                textView.setTextColor(Color.TRANSPARENT);
                textView.setClickable(false);
                textView.setOnClickListener(cardClick);
                textView.setVisibility(View.INVISIBLE);
            }
        }

        //start the game
        begin();


        return view;
    }

    //start game
    public void begin(){ //starts with a simple 1 row
        int tempRow = mRandom.nextInt(6);
        RowCount++;
        Rows[tempRow] = true;
        pairsInRound = RowCount * 3;
        makeRows(tempRow);
        placeNumbers();
        roundTime = new GameTimer(initialTime,INTERVAL); //start with 15 seconds
        timeTextView.setText("Time: " + initialTime/INTERVAL);
        scoreTextView.setText("Score: 0");
        roundTime.start();
        hasTimerStart = true;
    }

    //initialize rows according to levels completed;
    public void makeRows(int r1){

        for(int i = 0; i < funSixBySix[r1].getChildCount(); i++){
            TextView textView = (TextView)funSixBySix[r1].getChildAt(i);
            textView.setClickable(true);
            textView.setVisibility(View.VISIBLE);
        }

    }

    //randomly place numbers
    //number 1-13 are basic cards. 1-10 is 1-10, 11 = J, 12 =Q, 13=K, 14 =T+ (Time increase)
    // 16 = B, CrossHairs that eliminate a random pair on the board, chance it does so is not guaranteed
    // 15 = JK , Used As A Joker -- has not been implemented yet Joker replaces all pairs on the board
    public void placeNumbers(){
        int pairs = RowCount * 3; //times 3 instead of  6 cuz you you need pairs
        for (int i = 0; i < pairs; i++){
            int tempCounter =0;
            //14 for Timer, 15 for Joker, 16 for Crosshairs
            int tempNum;
            if (levelsCompleted < 7){
                tempNum = mRandom.nextInt(15);
            }
            else{ //woo bombs/Crosshairs!
                tempNum = mRandom.nextInt(16);
            }

            while(tempCounter < 2){
                //to get row that is initialized
                int tempRow = mRandom.nextInt(6);
                while(Rows[tempRow] == false){
                    tempRow = mRandom.nextInt(6);
                }
                int tempCol = mRandom.nextInt(6);
                LinearLayout randRow = funSixBySix[tempRow];
                TextView textView = (TextView)randRow.getChildAt(tempCol);
                //if current randomized card is not already intialized

                if (textView.getText().toString().equals("0")){
                    if((tempNum + 1) == 11){
                        textView.setText("J");
                        tempCounter++;
                    }
                    else if((tempNum +1) == 12){
                        textView.setText("Q");
                        tempCounter++;
                    }
                    else if ((tempNum +1) == 13){
                        textView.setText("K");
                        tempCounter++;
                    }
                    else if ((tempNum+1) == 14){
                        textView.setText("T+");
                        tempCounter++;
                    }
                    else if ((tempNum+1) == 15){
                        textView.setText("JK");
                        tempCounter++;
                    }
                    else if ((tempNum+1) == 16){
                        textView.setText("B");
                        tempCounter++;
                    }
                    else {
                        textView.setText("" + (tempNum + 1));
                        tempCounter++;
                    }
                }

            }
            existingNumbers[tempNum]++;
        }
    }

    //clear the board after ever round
    public void clearBoard(){

        for(int i = 0; i < Rows.length;i++){
            Rows[i] = false;
        }

        for (LinearLayout row : funSixBySix){
            for (int i = 0; i < row.getChildCount(); i++){
                TextView textView = (TextView) row.getChildAt(i);
                //textView.setBackgroundColor(Color.BLACK);
                textView.setBackground(getResources().getDrawable(R.mipmap.cardback2));
                textView.setText("0");
                textView.setTextColor(Color.TRANSPARENT);
                textView.setClickable(false);
                textView.setVisibility(View.INVISIBLE);
            }
        }
    }

    //reset game
    public void gameReset(){
        roundTime = new GameTimer(initialTime,INTERVAL);
        timeLeft = 0;
        numCardClicked = 0;
        collectedPairs = 0;
        pairsInRound = 0;
        RowCount = 0;
        SCORE = 0;
        numOfGuesses = 0;
        numOfCorrectGuesses = 0;
        levelsCompleted = 0;
        //falsify all booleans in Rows
        for(int i = 0; i < Rows.length;i++){
            Rows[i] = false;
        }


        for (LinearLayout row : funSixBySix){
            for (int i = 0; i < row.getChildCount(); i++){
                TextView textView = (TextView) row.getChildAt(i);
                //textView.setBackgroundColor(Color.BLACK);
                textView.setBackground(getResources().getDrawable(R.mipmap.cardback2));
                textView.setText("0");
                textView.setTextColor(Color.TRANSPARENT);
                textView.setClickable(false);
                textView.setVisibility(View.INVISIBLE);
            }
        }
        begin();

    }


    public OnClickListener cardClick = new OnClickListener(){
        @Override
        public void onClick(View v) {
            v.clearAnimation();
            currentCard = v;
            if(numCardClicked == 0){
                c1 = (TextView)v;
                v.setAnimation(card1Animation1);
                v.startAnimation(card1Animation1);
                numCardClicked++;
                disableClickable();
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        enableClickable();
                        c1.setClickable(false);
                    }
                }, 200);
            }
            else if (numCardClicked == 1){
                c2 = (TextView)v;
                v.setAnimation(card2Animation1);
                v.startAnimation(card2Animation1);
                disableClickable();
                if(check()){
                    handler.postDelayed(new Runnable(){
                        @Override
                        public void run () {
                            c1.setBackgroundColor(Color.WHITE);
                            c1.setTextColor(Color.TRANSPARENT);
                            c1.setText("-1");
                            c2.setBackgroundColor(Color.WHITE);
                            c2.setTextColor(Color.TRANSPARENT);
                            c2.setText("-1");
                            enableClickable();
                        }
                    },350);
                    collectedPairs++;
                    if (multiplier > 1 ){
                        Toast.makeText(getActivity(), "" + multiplier + "x points!", Toast.LENGTH_SHORT).show();
                    }
                    if(RowCount <= 2){ //for 1 row
                        SCORE += TEN * multiplier;
                    }
                    else if (RowCount == 3){
                        SCORE += FIFTY * multiplier;
                    }
                    else if (RowCount == 4){
                        SCORE += HUNDRED * multiplier;
                    }
                    else if (RowCount == 5){
                        SCORE += FIVEHUNDRED * multiplier;
                    }
                    else if (RowCount ==6){
                        SCORE += THOUSAND * multiplier;
                    }
                    //SCORE +=2;
                    numOfCorrectGuesses++;
                    numOfGuesses++;
                    multiplier++;
                    scoreTextView.setText("Score: " + SCORE);
                }
                else{
                    handler.postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            //c1.setBackgroundColor(Color.BLACK);
                            c1.setBackground(getResources().getDrawable(R.mipmap.cardback2));
                            c1.setTextColor(Color.TRANSPARENT);
                            //c2.setBackgroundColor(Color.BLACK);
                            c2.setBackground(getResources().getDrawable(R.mipmap.cardback2));
                            c2.setTextColor(Color.TRANSPARENT);
                            enableClickable();
                        }
                    },350);

                    numOfGuesses++;
                    multiplier = 1;
                }
                numCardClicked = 0;

                if(roundOver()){
                    intermission();
                }
            }
        }
    };

    public void intermission(){
        levelsCompleted++;
        roundTime.cancel();
        hasTimerStart = false;
        Toast.makeText(getActivity(),"SICK! NEXT ROUND!", Toast.LENGTH_LONG).show();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                if(levelsCompleted < 2){ //one row
                    nextRound();
                    roundTime = new GameTimer((timeLeft + 5000), INTERVAL); //user gets 10 extra seconds
                }
                else if (levelsCompleted == 2){ //increase to 2 rows
                    RowCount++;
                    nextRound();
                    roundTime = new GameTimer((timeLeft + 15000), INTERVAL);
                }
                else if (levelsCompleted > 2 && levelsCompleted < 4){
                    nextRound();
                    roundTime = new GameTimer((timeLeft + 15000), INTERVAL); //user gets 10 extra seconds
                }
                else if (levelsCompleted == 4){ //3 rows
                    RowCount++;
                    nextRound();
                    roundTime = new GameTimer((timeLeft + 25000), INTERVAL); //user gets 20 extra seconds
                }
                else if (levelsCompleted > 4 && levelsCompleted < 7){
                    nextRound();
                    roundTime = new GameTimer((timeLeft + 25000), INTERVAL);
                }
                else if (levelsCompleted == 7){ //4 rows
                    RowCount++;
                    nextRound();
                    roundTime = new GameTimer((timeLeft + 30000), INTERVAL); //user gets 25 extra seconds
                }
                else if (levelsCompleted > 7 && levelsCompleted < 12){
                    nextRound();
                    roundTime = new GameTimer((timeLeft + 30000), INTERVAL);
                }
                else if (levelsCompleted == 12){ //5 rows
                    RowCount++;
                    nextRound();
                    roundTime = new GameTimer((timeLeft + 35000), INTERVAL); //user gets 30 extra seconds
                }

                else if (levelsCompleted > 12 &&  levelsCompleted < 17){
                    nextRound();
                    roundTime = new GameTimer((timeLeft + 35000), INTERVAL);
                }
                else if (levelsCompleted == 17){
                    RowCount++;
                    nextRound();
                    roundTime = new GameTimer((timeLeft + 40000), INTERVAL); //user gets 35 extra seconds
                }
                else{
                    nextRound();
                    roundTime = new GameTimer((timeLeft + 40000), INTERVAL); //user gets 35 extra seconds
                }

                roundTime.start();

                hasTimerStart = true;
            }
        },1000); // 5 seconds till next round
    }

    //initialize next round
    public void nextRound(){
        clearBoard();
        //use RowCount
        //timeLeft = 0;
        collectedPairs = 0;
        pairsInRound = RowCount * 3;
        int tempCounter = 0;
        while(tempCounter < RowCount){
            int tempRow = mRandom.nextInt(6);
            if(Rows[tempRow] == false){
                Rows[tempRow] = true;
                makeRows(tempRow);
                tempCounter++;
            }
        }
        placeNumbers();
    }


    //checks if the current round is over
    public boolean roundOver(){
        if (collectedPairs == pairsInRound){
            return true;
        }
        else{
            return false;
        }
    }

    //checks if the pairs are correct
    public boolean check(){
        String c1String = c1.getText().toString();
        String c2String = c2.getText().toString();
        if (c1String.equals(c2String)){
            //add 5 seconds to timer
            if(c1.getText().toString().equals("T+")){
                roundTime.cancel();
                roundTime = new GameTimer(timeLeft + 5000, INTERVAL);
                roundTime.start();
                timeTextView.setText("Time: " + (timeLeft + 5000));
                existingNumbers[13]--;
            }

            else if (c1.getText().toString().equals("JK")){
                if(collectedPairs + 1 == pairsInRound) {
                    //means its the last pair. If it is the last pair do nothing
                }
                else {
                    REPLACE();
                }
                existingNumbers[14]--;
            }

            else if (c1.getText().toString().equals("B")){
                if(collectedPairs + 1 == pairsInRound){
                    //means do nothing its the last pair
                }
                else {
                    WIPE();
                }
                existingNumbers[15]--;
            }
            else {
                if(c1String.equals("J")){
                    existingNumbers[10]--;
                }
                else if(c1String.equals("Q")){
                    existingNumbers[11]--;
                }
                else if(c1String.equals("K")){
                    existingNumbers[12]--;
                }
                else {
                    existingNumbers[Integer.parseInt(c1String) - 1]--;
                }
            }
            return true;
        }
        else{
            return false;
        }
    }

    //create new pairs but with number of pairs left
    public void REPLACE(){

        int pairsLeft = pairsInRound - collectedPairs;

        clearBoard();
        for (int i = 0; i < pairsLeft; i++){
            int tempCounter = 0;
            int newNum = mRandom.nextInt(16);
            while(tempCounter < 2){
                int tempRow = mRandom.nextInt(6);
                LinearLayout row = funSixBySix[tempRow];
                int tempCol = mRandom.nextInt(6);
                TextView textView = (TextView)row.getChildAt(tempCol);
                if(textView.getText().toString().equals("0")){
                    textView.setText("" + newNum);
                    textView.setVisibility(View.VISIBLE);
                    textView.setClickable(true);
                    tempCounter++;
                }
            }
        }

    }


    public void WIPE(){
        //randomly destroy a pair if it exists
        int tempRandNum = mRandom.nextInt(13);
        if(existingNumbers[tempRandNum] > 0){
            Toast.makeText(getActivity(), "Removing Random Pair", Toast.LENGTH_SHORT).show();
            disintegrate(tempRandNum);
        }
        else{
            Toast.makeText(getActivity(), "Tough Luck", Toast.LENGTH_SHORT).show();
        }

    }

    //randomly destroy a pair on the board
    public void disintegrate(int tempRandNum){
        String toRemove;
        if((tempRandNum + 1) == 11){
            toRemove ="J";
            existingNumbers[10]--;
        }
        else if ((tempRandNum+1) == 12){
            toRemove="Q";
            existingNumbers[11]--;
        }
        else if ((tempRandNum+1) == 13){
            toRemove="K";
            existingNumbers[12]--;
        }
        else{
            toRemove= "" + (tempRandNum + 1);
            existingNumbers[tempRandNum]--;
        }

        int tempCounter = 0;
        for (LinearLayout row : funSixBySix){
            for(int i = 0; i < row.getChildCount(); i++){
                final TextView textView = (TextView)row.getChildAt(i);
                if(tempCounter == 2){
                    break;
                }
                if(textView.getText().toString().equals(toRemove)){
                    row.getChildAt(i).clearAnimation();
                    row.getChildAt(i).setAnimation(card1Animation1);
                    row.getChildAt(i).startAnimation(card1Animation1);
                    textView.setBackground(getResources().getDrawable(R.mipmap.cardfront));
                    textView.setTextColor(Color.WHITE);
                    handler.postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            textView.setText("-1");
                            textView.setTextColor(Color.TRANSPARENT);
                            textView.setBackgroundColor(Color.WHITE);
                        }
                    },350);
                    tempCounter++;
                }
            }
            if (tempCounter == 2){
                break;
            }
        }
        collectedPairs++;
        if(roundOver()){
            intermission();
        }
    }

    //disable all clicks to avoid user spam
    public void disableClickable(){
        for (LinearLayout row : funSixBySix){
            for(int i = 0; i < row.getChildCount();i++){
                TextView textView = (TextView)row.getChildAt(i);
                textView.setClickable(false);
            }
        }
    }


    //re-enable clicks
    public void enableClickable(){
        for(LinearLayout row : funSixBySix){
            for(int col =0; col < row.getChildCount(); col++){

                TextView textView = (TextView)row.getChildAt(col);
                if(!textView.getText().toString().equals("-1")) {
                    textView.setClickable(true);
                }
            }
        }
    }


    //card animations
    public Animation.AnimationListener flip1 = new Animation.AnimationListener(){
        @Override
        public void onAnimationStart(Animation animation) {
            if(animation == card1Animation1){
                if(cardBack1){ //if the back of card 1 is showing
                    //show back
                    TextView textView = (TextView)currentCard;
                    if(textView.getText().toString().equals("T+")){
                        textView.setTextColor(Color.TRANSPARENT);
                        textView.setBackground(getResources().getDrawable(R.mipmap.addtime));
                    }
                    else if (textView.getText().toString().equals("B")){
                        textView.setTextColor(Color.TRANSPARENT);
                        textView.setBackground(getResources().getDrawable(R.mipmap.crosair));
                    }
                    else if (textView.getText().toString().equals("JK")){
                        textView.setTextColor(Color.TRANSPARENT);
                        textView.setBackground(getResources().getDrawable(R.mipmap.replace));
                    }
                    else {
                        textView.setTextColor(Color.WHITE);
                        textView.setBackground(getResources().getDrawable(R.mipmap.cardfront));
                    }
                    //textView.setBackgroundColor(Color.RED);
                    cardBack1 = false;

                }

                currentCard.startAnimation(card1Animation2);
            }
            else{
                cardBack1 = !cardBack1;
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    //card animations
    public Animation.AnimationListener flip2 = new Animation.AnimationListener(){
        @Override
        public void onAnimationStart(Animation animation) {


        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(animation == card2Animation1){
                if(cardBack2){ //if the back of card 1 is showing
                    //show back
                    TextView textView = (TextView)currentCard;
                    if(textView.getText().toString().equals("T+")){
                        textView.setTextColor(Color.TRANSPARENT);
                        textView.setBackground(getResources().getDrawable(R.mipmap.addtime));
                    }
                    else if (textView.getText().toString().equals("B")){
                        textView.setTextColor(Color.TRANSPARENT);
                        textView.setBackground(getResources().getDrawable(R.mipmap.crosair));
                    }
                    else if (textView.getText().toString().equals("JK")){
                        textView.setTextColor(Color.TRANSPARENT);
                        textView.setBackground(getResources().getDrawable(R.mipmap.replace));
                    }
                    else {
                        textView.setTextColor(Color.WHITE);
                        textView.setBackground(getResources().getDrawable(R.mipmap.cardfront));
                    }
                    cardBack2 = false;

                }

                currentCard.startAnimation(card2Animation2);
            }
            else{
                cardBack2 = !cardBack2;
            }


        }

        @Override
        public void onAnimationRepeat(Animation animation) {


        }
    };

    //Game Over Screen
    public void displayGameOver(){

        clearBoard();
        for (int i = 0; i < 16; i++){
            existingNumbers[i]=0;
        }
        LinearLayout row3 = funSixBySix[2];
        LinearLayout row4 = funSixBySix[3];

        for(LinearLayout row : funSixBySix){
            for(int k = 0; k < row.getChildCount();k++){
                TextView textView = (TextView)row.getChildAt(k);
                textView.setBackground(getResources().getDrawable(R.mipmap.cardback2));
                textView.setVisibility(View.VISIBLE);
            }
        }

        String gameString[]  = {"G", "A", "M","E"};
        String overString[]  = {"O", "V", "E", "R"};

        for (int i = 0; i < 4; i++){
            TextView textView = (TextView)row3.getChildAt(i);
            textView.setText(gameString[i]);
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.RED);
            textView.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < 4; i++){
            TextView textView = (TextView)row4.getChildAt(i+2);
            textView.setText(overString[i]);
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.RED);
            textView.setVisibility(View.VISIBLE);
        }

    }



    //for GameTimer
    public class GameTimer extends CountDownTimer {
        public GameTimer(long startTime, long interval){
            super(startTime,interval);
            //timeLeft = startTime;
            //timeTextView.setText("Time: " + timeLeft/INTERVAL);
        }

        @Override
        public void onFinish() {
            hasTimerStart = false;
            disableClickable();
            timeTextView.setText("Time: 0");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Times Up! Game Over!");
            double Accuracy = (double)numOfCorrectGuesses/(double)numOfGuesses;
            double finalAccuracy = Math.round(Accuracy * 100);
            double finalScore = SCORE;
            builder.setMessage("Score: " + finalScore + "\nLevels Completed: " + levelsCompleted
                    +"\nNumber of Guesses: " + numOfGuesses
                    + "\nNumber of Correct Guesses: " + numOfCorrectGuesses +
                    "\nAccuracy: " + finalAccuracy + "%");
            builder.setPositiveButton("Reset",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    gameReset();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("End",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    displayGameOver();
                    dialog.dismiss();
                }
            });


            //should implement share to facebook option -look into facebook api!
            builder.show();

        }

        @Override
        public void onTick(long millisUntilFinished) {
            timeLeft = millisUntilFinished;
            timeTextView.setText("Time: " + timeLeft/INTERVAL);
            final Toast tempToast = Toast.makeText(getActivity(),("" + millisUntilFinished/INTERVAL), Toast.LENGTH_SHORT);
            if(millisUntilFinished < 6000){
                tempToast.show();
                handler.postDelayed(new Runnable(){
                    @Override
                        public void run(){
                            tempToast.cancel();
                        }
                },1000);
            }
        }


    }

    @Override
    public void onStop(){
        super.onStop();
        if(hasTimerStart){
            roundTime.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(hasTimerStart){
            roundTime.cancel();
        }
    }
}

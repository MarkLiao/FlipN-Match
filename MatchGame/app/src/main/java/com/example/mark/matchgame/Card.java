package com.example.mark.matchgame;

/**
 * Created by Mark on 02/03/2016.
 */

public class Card {

    private String CardValue;
    private String CardRank;
    private boolean isClicked;
    private String cardSymbol;

    public Card(){

    }

    public Card (String cardValue, String cardRank){
        this.CardValue = cardValue;
        this.CardRank = cardRank;
        this.isClicked = false;
    }

    public String getCardValue(){
        return this.CardValue;
    }

    public String getCardRank(){
        return this.CardRank;
    }

    public boolean getIsClicked(){
        return this.isClicked;
    }

    public void setCardValue(String cardValue){
        this.CardValue = cardValue;
    }

    public void setCardRank(String cardRank){
        this.CardRank = cardRank;
    }

    public void setIsClicked(boolean IsClicked){
        this.isClicked = IsClicked;
    }
}

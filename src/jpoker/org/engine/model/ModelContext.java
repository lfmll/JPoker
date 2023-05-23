/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.engine.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jpoker.org.poker.api.core.Baraja;
import jpoker.org.poker.api.core.Carta;
import jpoker.org.poker.api.game.BetCommand;
import jpoker.org.poker.api.game.GameInfo;
import jpoker.org.poker.api.game.Settings;
import jpoker.org.poker.api.game.TexasHoldEmUtil;
import jpoker.org.poker.api.game.TexasHoldEmUtil.GameState;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class ModelContext {
    private final GameInfo<PlayerEntity> gameInfo=new GameInfo<>();
    private final Map<String, PlayerEntity> playersByName;
    private int activePlayers;
    private long highBet;
    private Baraja deck;
    private int playerAllIn;
    private BetCommand lastBetCommand;
    private PlayerEntity lastPlayerBet;
    private int bets=0;
    
    public  ModelContext(Settings settings){
        this.gameInfo.setSettings(settings);
        this.gameInfo.setPlayers(new ArrayList<>(TexasHoldEmUtil.MAX_PLAYERS));
        this.playersByName=new HashMap<>(settings.getMaxPlayers());
        this.highBet=0;
    }

    public long getHighBet() {
        return highBet;
    }

    public void setHighBet(long highBet) {
        this.highBet = highBet;
    }

    public Baraja getDeck() {
        return deck;
    }

    public void setDeck(Baraja deck) {
        this.deck = deck;
    }

    public int getPlayerAllIn() {
        return playerAllIn;
    }

    public void setPlayerAllIn(int playerAllIn) {
        this.playerAllIn = playerAllIn;
    }
    
    public int getNumPlayers(){
        return gameInfo.getNumPlayers();
    }
    
    public boolean addPlayer(String playerName){
        PlayerEntity player = new PlayerEntity();
        player.setName(playerName);
        player.setChips(gameInfo.getSettings().getPlayerChip());
        this.playersByName.put(playerName, player);
        return this.gameInfo.addPlayer(player);
    }
    
    public boolean removePlayer(final String playerName){
        return gameInfo.removePlayer(playersByName.get(playerName));        
    }
    
    public int getDealer(){
        return gameInfo.getDealer();
    }
    
    public void setDealer(int dealer) {
        this.gameInfo.setDealer(dealer);
    }
    
    public int getRound(){
        return gameInfo.getRound();
    }
    
    public void setRound(int round){
        this.gameInfo.setRound(round);
    }
    
    public GameState getGameState(){
        return gameInfo.getGameState();
    }
    
    public void setGameState(GameState gameState){
        this.gameInfo.setGameState(gameState);
    }
    
    public String getPlayerTurnName(){
        String result = null;
        int turnPlayer =  gameInfo.getPlayerTurn();
        if (turnPlayer >= 0) {
            result=gameInfo.getPlayer(turnPlayer).getName();
        }
        return result;
    }
    
    public int getPlayerTurn(){
        return gameInfo.getPlayerTurn();
    }
    
    public void setPlayerTurn(int playerTurn){
        this.gameInfo.setPlayerTurn(playerTurn);
    }
    
    public Settings getSettings(){
        return gameInfo.getSettings();
    }
    
    public List<Carta> getCommunityCards(){
        return gameInfo.getCommunityCards();
    }
    
    public void setCommunityCards(List<Carta> communityCards){
        gameInfo.setCommunityCards(communityCards);
    }
    
    public List<PlayerEntity> getPlayers(){
        return this.gameInfo.getPlayers();
    }
    
    public PlayerEntity getPlayer(int player){
        return this.gameInfo.getPlayer(player);
    }
    
    public void setPlayers(List<PlayerEntity> newPlayers){
        this.gameInfo.setPlayers(newPlayers);
        this.playersByName.clear();
        newPlayers.stream().forEach(p->this.playersByName.put(p.getName(), p));
    }
    
    public PlayerEntity getPlayerByName(String playerName){
        return playersByName.get(playerName);
    }
    
    public int getActivePlayers(){
        return activePlayers;
    }
    
    public void setActivePlayers(int activePlayers){
        this.activePlayers=activePlayers;
    }
    
    public void lastResultCommand(PlayerEntity player, BetCommand resultCommand){
        this.lastPlayerBet=player;
        this.lastBetCommand=resultCommand;
    }
    
    public BetCommand getLastBetCommand(){
        return lastBetCommand;
    }
    
    public void setLastBetCommand(BetCommand resultLastBetCommand){
        this.lastBetCommand=resultLastBetCommand;
    }
    
    public int getBets(){
        return bets;
    }
    
    public void setBets(int bets){
        this.bets=bets;
    }
    
    public int addCommunityCards(int numCards){
        boolean added=true;
        int i=0;
        while (i<numCards && added) {            
            added=gameInfo.addCommunityCard(deck.obtenerCarta());
            if (added) {
                i++;
            }
        }
        return i;
    }
    
    public void clearCommunityCard(){
        gameInfo.clearCommunityCard();
    }
}

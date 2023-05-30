/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.engine.controller;

import java.util.Collections;
import java.util.Map;
import static jpoker.org.engine.controller.GameController.SYSTEM_CONTROLLER;
import jpoker.org.engine.model.ModelContext;
import jpoker.org.engine.model.ModelUtil;
import jpoker.org.engine.states.BetRoundState;
import jpoker.org.engine.states.CheckState;
import jpoker.org.engine.states.EndState;
import jpoker.org.engine.states.InitHandState;
import jpoker.org.engine.states.ShowDownState;
import jpoker.org.engine.states.WinnerState;
import jpoker.org.poker.api.core.Baraja;
import jpoker.org.poker.api.game.BetCommand;
import jpoker.org.poker.api.game.Settings;
import jpoker.org.poker.api.game.TexasHoldEmUtil;
import jpoker.org.util.dispatcher.GameEvent;
import jpoker.org.util.dispatcher.IGameEventDispatcher;
import jpoker.org.util.statemachine.IState;
import jpoker.org.util.statemachine.StateDecoratorBuilder;
import jpoker.org.util.statemachine.StateMachine;
import jpoker.org.util.statemachine.StateMachineInstance;
import jpoker.org.util.timer.IGameTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class StateMachineConnector {
    private static final Logger LOGGER = LoggerFactory.getLogger(StateMachineConnector.class);
    private static final int END_HAND_SLEEP_TIME = 1000;
    public static final String NEXT_PLAYER_TURN = "nextPlayerTurn";
    private final StateMachine<ModelContext> texasStateMachine = buildStateMachine();
    private final Map<String, IGameEventDispatcher> playersDispatcher;
    private final IGameTimer timer;
    private ModelContext model;
    private IGameEventDispatcher system;
    private StateMachineInstance<ModelContext> instance;
    private long timeoutId = 0;

    public StateMachineConnector(IGameTimer t, Map<String, IGameEventDispatcher> pd) {
        this.playersDispatcher = pd;
        this.timer = t;
    }
    
    public void setSystem(IGameEventDispatcher system) {
        this.system = system;
    }
    
    public void createGame(Settings settings) {
        if (model == null) {
            LOGGER.debug("createGame: {}", settings);
            model = new ModelContext(settings);
            model.setDealer(-1);
        }
    }
    
    public void addPlayer(String playerName) {
        if (model != null) {
            LOGGER.debug("addPlayer: \"{}\"", playerName);
            model.addPlayer(playerName);
        }
    }
    
    public void startGame() {
        LOGGER.debug("startGame");
        if (instance == null && model != null) {
            model.setDeck(new Baraja());
            instance = texasStateMachine.startInstance(model);
            model.setDealer(0);
            execute();
        }
    }
    
    public void betCommand(String playerName, BetCommand command) {
        LOGGER.debug("betCommand: {} -> {}", playerName, command);
        if (instance != null && playerName.equals(model.getPlayerTurnName())) {
            BetCommand betCommand = command;
            if (betCommand == null) {
                betCommand = new BetCommand(TexasHoldEmUtil.BetCommandType.ERROR);
            }
            model.getPlayerByName(playerName).setBetCommand(betCommand);
            execute();
        }
    }
    
    public void timeOutCommand(Long timeoutId) {
        LOGGER.debug("timeOutCommand: id: {}",timeoutId);
        if (instance != null && timeoutId == this.timeoutId) {
            LOGGER.debug("timeOutCommand: player: {}", model.getPlayerTurnName());
            model.getPlayerByName(model.getPlayerTurnName()).setBetCommand(new BetCommand(TexasHoldEmUtil.BetCommandType.TIMEOUT));
            execute();
        }
    }
    
    private void execute() {
        if (instance.execute().isFinish()) {
            model.setGameState(TexasHoldEmUtil.GameState.END);
            model.setCommunityCards(Collections.emptyList());
            notifyEndGame();
            instance = null;
        }
    }
    
    private void notifyInitHand() {
        notifyEvent(GameController.INIT_HAND_EVENT_TYPE);
    }
    
    private void notifyBetCommand() {
        String playerTurn = model.getLastPlayerBet().getName();
        BetCommand lbc = model.getLastBetCommand();
        LOGGER.debug("notifyBetCommand -> {}: {}", playerTurn, lbc);
        for (String playerName : playersDispatcher.keySet()) {
            playersDispatcher.get(playerName).dispatch(new GameEvent(GameController.BET_COMMAND_EVENT_TYPE, playerTurn, new BetCommand(lbc.getType(), lbc.getChips())));
        }
    }
    
    private void notifyCheck(){
        LOGGER.debug("notifyCheck: {}", GameController.CHECK_PLAYER_EVENT_TYPE);
        for (String playerName : playersDispatcher.keySet()) {
            playersDispatcher.get(playerName).dispatch(new GameEvent(GameController.CHECK_PLAYER_EVENT_TYPE, SYSTEM_CONTROLLER, model.getCommunityCards()));
        }
    }
    
    private void notifyPlayerTurn() {
        String playerTurn = model.getPlayerTurnName();
        if (playerTurn != null) {
            LOGGER.debug("notifyPlayerTurn -> {}", playerTurn);
            playersDispatcher.get(playerTurn).dispatch(new GameEvent(GameController.GET_COMMAND_PLAYER_EVENT_TYPE, SYSTEM_CONTROLLER, PlayerAdapter.toTableState(model, playerTurn)));
        }
        timer.resetTimer(++timeoutId);
    }
    private void notifyEndHand() {
        notifyEvent(GameController.END_HAND_PLAYER_EVENT_TYPE);
        try {
            Thread.sleep(END_HAND_SLEEP_TIME);
        } catch (InterruptedException ex) {
            LOGGER.error("Error en la espera despues de terminar una mano", ex);
        }
    }
    
    private void notifyEndGame(){
        notifyEvent(GameController.END_GAME_PLAYER_EVENT_TYPE);
        system.dispatch(new GameEvent(GameController.EXIT_CONNECTOR_EVENT_TYPE, SYSTEM_CONTROLLER));
        notifyEvent(GameController.EXIT_CONNECTOR_EVENT_TYPE);
    }
    
    private void notifyEvent(String type) {
        LOGGER.debug("notifyEvent: {} -> {}",type, model);
        for (String playerName : playersDispatcher.keySet()) {
            playersDispatcher.get(playerName).dispatch(new GameEvent(type, SYSTEM_CONTROLLER, PlayerAdapter.toTableState(model, playerName)));
        }
    }
    
    private StateMachine<ModelContext> buildStateMachine() {
        StateMachine<ModelContext> sm = new StateMachine<>();
        final IState<ModelContext> initHandState = StateDecoratorBuilder.after(new InitHandState(), () -> notifyInitHand());
        final IState<ModelContext> betRoundState = StateDecoratorBuilder
                .create(new BetRoundState())
                .before(()-> notifyPlayerTurn())
                .after(()-> notifyBetCommand())
                .build();
        final IState<ModelContext> checkState = StateDecoratorBuilder.after(new CheckState(),() -> notifyCheck());
        final IState<ModelContext> showDownState = new ShowDownState();
        final IState<ModelContext> winnerState = new WinnerState();
        final IState<ModelContext> endHandState = StateDecoratorBuilder.before(new EndState(), () -> notifyEndHand());
        sm.setInitState(initHandState);
        sm.setDefaultTransition(initHandState, betRoundState);
        sm.addTransition(betRoundState, betRoundState, c -> c.getPlayerTurn() != ModelUtil.NO_PLAYER_TURN);
        sm.addTransition(betRoundState, winnerState, c -> c.getPlayerAllIn() + c.getActivePlayers() == 1);
        sm.setDefaultTransition(betRoundState, checkState);
        sm.addTransition(checkState, showDownState, c -> c.getGameState() == TexasHoldEmUtil.GameState.SHOWDOWN);
        sm.addTransition(checkState, betRoundState, c -> c.getPlayerTurn() != ModelUtil.NO_PLAYER_TURN);
        sm.setDefaultTransition(checkState, checkState);
        sm.setDefaultTransition(winnerState, endHandState);
        sm.setDefaultTransition(showDownState, endHandState);
        sm.addTransition(endHandState, initHandState, c -> c.getNumPlayers() > 1 && c.getRound() < c.getSettings().getMaxRounds());
        return sm;
       
    }
}

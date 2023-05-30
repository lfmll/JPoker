/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpoker.org.engine.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import jpoker.org.poker.api.core.Carta;
import jpoker.org.poker.api.game.BetCommand;
import jpoker.org.poker.api.game.GameInfo;
import jpoker.org.poker.api.game.IGameController;
import jpoker.org.poker.api.game.IStrategy;
import jpoker.org.poker.api.game.PlayerInfo;
import jpoker.org.poker.api.game.Settings;
import jpoker.org.util.dispatcher.GameEvent;
import jpoker.org.util.dispatcher.GameEventDispatcher;
import jpoker.org.util.dispatcher.IGameEventDispatcher;
import jpoker.org.util.dispatcher.IGameEventProcessor;
import jpoker.org.util.timer.GameTimer;
import jpoker.org.util.timer.IGameTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author luisfernandomedinallorenti
 */
public class GameController implements IGameController, Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);
    private static final int DISPATCHER_THREADS = 1;
    private static final int EXTRA_THREADS = 2;
    public static final String SYSTEM_CONTROLLER = "system";
    public static final String INIT_HAND_EVENT_TYPE = "initHand";
    public static final String BET_COMMAND_EVENT_TYPE = "betCommand";
    public static final String END_GAME_PLAYER_EVENT_TYPE = "endGame";
    public static final String END_HAND_PLAYER_EVENT_TYPE = "endHand";
    public static final String CHECK_PLAYER_EVENT_TYPE = "check";
    public static final String GET_COMMAND_PLAYER_EVENT_TYPE = "getCommand";
    public static final String EXIT_CONNECTOR_EVENT_TYPE = "exit";
    public static final String ADD_PLAYER_CONNECTOR_EVENT_TYPE = "addPlayer";
    public static final String TIMEOUT_CONNECTOR_EVENT_TYPE = "timeOutCommand";
    public static final String CREATE_GAME_CONNECTOR_EVENT_TYPE = "createGame";
    private final Map<String, IGameEventDispatcher> players = new HashMap<>();
    private final List<String> playersByName = new ArrayList<>();
    private final Map<String, IGameEventProcessor<IStrategy>> playerProcessors;
    private final GameEventDispatcher<StateMachineConnector> connectorDispatcher;
    private final StateMachineConnector stateMachineConnector;
    private final IGameTimer timer;
    private Settings settings;
    private ExecutorService executors;
    private List<ExecutorService> subExecutors = new ArrayList<>();
    private boolean finish;
    
    public GameController() {
        timer = new GameTimer(SYSTEM_CONTROLLER, buildExecutor(DISPATCHER_THREADS));
        stateMachineConnector = new StateMachineConnector(timer, players);
        connectorDispatcher = new GameEventDispatcher<>(stateMachineConnector, buildConnectorProcessors(),buildExecutor(1));
        stateMachineConnector.setSystem(connectorDispatcher);
        timer.setDispatcher(connectorDispatcher);
        playerProcessors = buildPlayerProcessors();
    }
    
    private ExecutorService buildExecutor(int threads){
        ExecutorService result= Executors.newFixedThreadPool(threads);
        subExecutors.add(result);
        return result;
    }
    @Override
    public void setSettings(Settings settings) {
        this.settings = settings;
    }
    
    private static Map<String, IGameEventProcessor<StateMachineConnector>> buildConnectorProcessors() {
        Map<String, IGameEventProcessor<StateMachineConnector>> cpm = new HashMap<>();
        cpm.put(CREATE_GAME_CONNECTOR_EVENT_TYPE, (c,e) -> c.createGame((Settings) e.getPayload()));
        cpm.put(ADD_PLAYER_CONNECTOR_EVENT_TYPE, (c,e) -> c.addPlayer(e.getSource()));
        cpm.put(INIT_HAND_EVENT_TYPE, (c,e) -> c.startGame());
        cpm.put(BET_COMMAND_EVENT_TYPE, (c,e) -> c.betCommand(e.getSource(), (BetCommand) e.getPayload()));
        cpm.put(TIMEOUT_CONNECTOR_EVENT_TYPE, (c,e) -> c.timeOutCommand((Long) e.getPayload()));
        return cpm;
    }
    
    private Map<String, IGameEventProcessor<IStrategy>> buildPlayerProcessors() {
        Map<String, IGameEventProcessor<IStrategy>> ppm = new HashMap<>();
        IGameEventProcessor<IStrategy> defaulProcessor = (s,e) -> s.updateState((GameInfo) e.getPayload());
        ppm.put(INIT_HAND_EVENT_TYPE, defaulProcessor);
        ppm.put(END_GAME_PLAYER_EVENT_TYPE, defaulProcessor);
        ppm.put(BET_COMMAND_EVENT_TYPE, (s,e) -> s.onPlayerCommand(e.getSource(), (BetCommand) e.getPayload()));
        ppm.put(CHECK_PLAYER_EVENT_TYPE, (s,e) -> s.check((List<Carta>) e.getPayload()));
        ppm.put(GET_COMMAND_PLAYER_EVENT_TYPE, (s,e) -> {
            GameInfo<PlayerInfo> gi = (GameInfo<PlayerInfo>) e.getPayload();
            String playerTurn = gi.getPlayers().get(gi.getPlayerTurn()).getName();
            BetCommand cmd = s.getCommand(gi);
            connectorDispatcher.dispatch(new GameEvent(BET_COMMAND_EVENT_TYPE, playerTurn,cmd));
        });
        return ppm;
    }
    
    public synchronized boolean addStrategy(IStrategy strategy) {
        boolean result = false;
        String name = strategy.getName();
        if (!players.containsKey(name) && !SYSTEM_CONTROLLER.equals(name)) {
            players.put(name, new GameEventDispatcher<>(strategy,playerProcessors, buildExecutor(DISPATCHER_THREADS)));
            playersByName.add(name);
            result = true;
        }
        return result;
    }
    
    public synchronized void waitFinish() {
        if (!finish) {
            try {
                wait();
            } catch (InterruptedException ex) {
                LOGGER.error("Esperando el...", ex);
            }
        }
    }
    
    private void check(boolean essentialCondition, String exceptionMessage) throws GameException {
        if (!essentialCondition) {
            throw new GameException(exceptionMessage);
        }
    }
            
    @Override
    public synchronized void start() throws GameException {
        LOGGER.debug("start");
        check(settings != null, "No se ha establecido un configuracion");
        check(players.size() > 1, "No se han agregado un numero suficiente de jugadores");
        check(players.size() <= settings.getMaxPlayers(), "El número de jugadores excede");
        check(settings.getMaxErrors() > 0, "El número de máximo de errores debe...");
        check(settings.getMaxRounds() >0, "El número de máximo de rondas debe...");
        check(settings.getRounds4IncrementBlind() > 1, "El numero de rondas hasta...");
        check(settings.getTime() >0, "El tiempo max por jugador debe ser...");
        check(settings.getPlayerChip() >0, "El numero de fichas inicial por ju...");
        check(settings.getSmallBind() > 0, "La apuesta de la ciega pequeña debe..");
        executors = Executors.newFixedThreadPool(players.size() + EXTRA_THREADS);
        players.values().stream().forEach(executors::execute);
        stateMachineConnector.createGame(settings);
        timer.setTime(settings.getTime());
        playersByName.stream().forEach(stateMachineConnector::addPlayer);
        executors.execute(timer);
        finish = false;
        new Thread(this).start();
    }
    
    @Override
    public synchronized void run() {
        LOGGER.debug("run");
        connectorDispatcher.dispatch(new GameEvent(INIT_HAND_EVENT_TYPE, SYSTEM_CONTROLLER));
        connectorDispatcher.run();
        timer.exit();
        executors.shutdown();
        players.values().stream().forEach(IGameEventDispatcher::exit);
        subExecutors.stream().forEach(ExecutorService::shutdown);
        try {
            executors.awaitTermination(0, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            LOGGER.error("Error intentando eliminar cerrar todos los hilos", ex);
        }
        finish = true;
        notifyAll();
    }
            
}

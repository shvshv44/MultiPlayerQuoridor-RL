package com.rl.mpquoridor.models.players;

import com.rl.mpquoridor.models.actions.MovePawnAction;
import com.rl.mpquoridor.models.actions.PlaceWallAction;
import com.rl.mpquoridor.models.actions.TurnAction;
import com.rl.mpquoridor.models.board.Pawn;
import com.rl.mpquoridor.models.board.Position;
import com.rl.mpquoridor.models.board.ReadOnlyPhysicalBoard;
import com.rl.mpquoridor.models.board.Wall;
import com.rl.mpquoridor.models.enums.MovementDirection;
import com.rl.mpquoridor.models.enums.WallDirection;
import com.rl.mpquoridor.models.events.GameEvent;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.rl.mpquoridor.exceptions.IllegalMovementException.Reason;

public class CMDPlayer implements Player {
    private static final String ASK_FOR_INPUT = "---> ";
    private static final String ILLEGAL_INPUT_MESSAGE = "Illegal input !";
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static final Logger logger = LoggerFactory.getLogger(CMDPlayer.class);

    private final Map<Pawn, String> pawnsName = new HashMap<>();
    private ReadOnlyPhysicalBoard board;
    private Pawn myPawn;
    private List<Pawn> playOrder;

    @Override
    public void setPlayOrder(List<Pawn> playOrder) {
        playOrder.forEach(p -> pawnsName.put(p, UUID.randomUUID().toString()));
        logger.info("Play order");
        playOrder.forEach(p -> logger.info("Pawn: " + pawnsName.get(p)));
        this.playOrder = playOrder;
    }

    @Override
    public void getPlayerName() {
	    return "CMD Player";
    }

    @Override
    public void setMyPawn(Pawn myPawn) {
        this.myPawn = myPawn;
        logger.info("My pawn is: " + pawnsName.get(myPawn));
    }

    @Override
    public void illegalMovePlayed(Reason reason) {
        logger.info("Illegal move played: " + reason.getMessage());
    }

    @Override
    public void trigger(GameEvent event) {
        logger.info("Got event " + event.getClass().getSimpleName());
    }

    @Override
    public void setBoard(ReadOnlyPhysicalBoard board) {
        logger.info("got board");
        this.board = board;
    }

    @SneakyThrows
    @Override
    public TurnAction play() {

        System.out.println("It's your turn !");
        System.out.println("Press 'w' to place wall.");
        System.out.println("Press 'm' to move.");
        String act;
        boolean isActionOk = false;
        do {
            System.out.print(ASK_FOR_INPUT);
            act = reader.readLine();
            if(act.equals("w") || act.equals("m")) {
                isActionOk = true;
            } else {
                System.out.println(ILLEGAL_INPUT_MESSAGE);
            }
        }
        while(!isActionOk);

        if(act.equals("w")) {
            return playWall();
        } else {
            return playMovement();
        }
    }

    @SneakyThrows
    private MovePawnAction playMovement() {
        boolean isActionOk = false;
        System.out.println("Press 'a' to go left.");
        System.out.println("Press 'w' to go up.");
        System.out.println("Press 's' to go down.");
        System.out.println("Press 'd' to go right.");
        String act;
        Map<String, MovementDirection> map = new HashMap<>();
        map.put("w", MovementDirection.UP);
        map.put("s", MovementDirection.DOWN);
        map.put("a", MovementDirection.LEFT);
        map.put("d", MovementDirection.RIGHT);

        do {
            System.out.println(ASK_FOR_INPUT);
            act = reader.readLine();
            if(map.containsKey(act)) {
                isActionOk = true;
            } else {
                System.out.println(ILLEGAL_INPUT_MESSAGE);
            }
        } while (!isActionOk);

        return new MovePawnAction(map.get(act));
    }

    @SneakyThrows
    private PlaceWallAction playWall() {
        System.out.println("Insert I coordinate");
        String act;
        int i = 0,j = 0;
        WallDirection dir = null;
        boolean isActionOk = false;
        do {
            System.out.print(ASK_FOR_INPUT);
            act = reader.readLine();
            try {
                i = Integer.parseInt(act);
                isActionOk = true;
            } catch (Exception ignored) {
                System.out.println(ILLEGAL_INPUT_MESSAGE);
            }
        } while (!isActionOk);

        isActionOk = false;
        System.out.println("Insert J coordinate");
        do {
            System.out.print(ASK_FOR_INPUT);
            act = reader.readLine();
            try {
                j = Integer.parseInt(act);
                isActionOk = true;
            } catch (Exception ignored) {
                System.out.println(ILLEGAL_INPUT_MESSAGE);
            }
        } while (!isActionOk);

        Map<String, WallDirection> map =  new HashMap<>();
        map.put("R", WallDirection.RIGHT);
        map.put("D", WallDirection.DOWN);

        System.out.println("Insert wall direction [R/D]");
        do {
            System.out.print(ASK_FOR_INPUT);
            act = reader.readLine();
            if(map.containsKey(act)){
               isActionOk = true;
               dir = map.get(act);
            } else {
                System.out.println(ILLEGAL_INPUT_MESSAGE);
            }
        } while (!isActionOk);

        return new PlaceWallAction(new Wall(new Position(i, j), dir));
    }
}

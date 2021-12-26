package com.app.game;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class GameStateStreamingResponse implements StreamObserver<GameState> {


    private StreamObserver<Die> dieStreamObserver;
    private CountDownLatch latch;

    public GameStateStreamingResponse(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(GameState gameState) {

        List<Player> players = gameState.getPlayerList();
        players.forEach(player -> System.out.println(player.getName() + ": " + player.getPosition()));
        boolean anyWinner = players.stream().anyMatch(player -> player.getPosition() == 100);
        if(anyWinner){
            System.out.println("winner");
            this.dieStreamObserver.onCompleted();
        }else{
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            this.roll();
        }

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        this.latch.countDown();

    }

    public void setDieStreamObserver(StreamObserver<Die> dieStreamObserver){
        this.dieStreamObserver = dieStreamObserver;
    }


    public void roll(){
        int die = ThreadLocalRandom.current().nextInt(1, 7);
        Die die1 = Die.newBuilder().setValue(die).build();
        this.dieStreamObserver.onNext(die1);
    }
}

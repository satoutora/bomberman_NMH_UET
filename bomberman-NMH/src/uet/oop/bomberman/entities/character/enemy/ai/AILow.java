package uet.oop.bomberman.entities.character.enemy.ai;

public class AILow extends AI {

    @Override
    public int calculateDirection() {
        // cài đặt thuật toán tìm đường đi bằng cách random
        return random.nextInt(4);
    }

}

package uet.oop.bomberman.entities.bomb;

import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.Board;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.graphics.Screen;

public class Flame extends Entity {

    protected Board _board;
    protected int _direction;
    private int _radius;
    protected int xOrigin, yOrigin;
    protected FlameSegment[] _flameSegments;


    // x,y : tọa độ băt đầu của Flame
    // direction : hướng của Flame
    // radius : độ dài của Flame
    public Flame(int x, int y, int direction, int radius, Board board) {
        xOrigin = x;
        yOrigin = y;
        _x = x;
        _y = y;
        _direction = direction;
        _radius = radius;
        _board = board;

        _flameSegments = new FlameSegment[ calculatePermitedDistance() ];
        createFlameSegments();
    }

    // Tạo các FlameSegment, mỗi segment ứng với một đơn vị độ dài
    // số segment ứng với độ dài Flame
    private void createFlameSegments() {
        boolean last = false;
        // last : segment cuối cùng
        // tạo các segment dưới đây
        int x = (int)_x;
        int y = (int)_y;
        for (int i = 0; i < _flameSegments.length; i++) {

            last = i == _flameSegments.length -1 ? true : false;
            switch (_direction) {
                case 0: y--; break;
                case 1: x++; break;
                case 2: y++; break;
                case 3: x--; break;
            }
            _flameSegments[i] = new FlameSegment(x, y, _direction, last, _board);
        }

    }


    private int calculatePermitedDistance() {
        //  thực hiện tính toán độ dài của Flame
        // nếu ặp vật caản là Brick/Wall thì độ dài bị cắt ngắn

        int radius = 0;
        int x = (int)_x;
        int y = (int)_y;
        while(radius < _radius) {
            if(_direction == 0) y--;
            if(_direction == 1) x++;
            if(_direction == 2) y++;
            if(_direction == 3) x--;

            Entity a = _board.getEntity(x, y, null);

            if(a instanceof Character){
                ++radius;
            }

            if(a.collide(this) == false) //cannot pass thru
                break;

            ++radius;
        }
        return radius;
    }

    public FlameSegment flameSegmentAt(int x, int y) {
        for (int i = 0; i < _flameSegments.length; i++) {
            if(_flameSegments[i].getX() == x && _flameSegments[i].getY() == y)
                return _flameSegments[i];
        }
        return null;
    }

    @Override
    public void update() {}

    @Override
    public void render(Screen screen) {
        for (int i = 0; i < _flameSegments.length; i++) {
            _flameSegments[i].render(screen);
        }
    }

    @Override
    public boolean collide(Entity e) {
        return true;
    }
}

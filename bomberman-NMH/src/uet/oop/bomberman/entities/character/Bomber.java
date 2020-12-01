package uet.oop.bomberman.entities.character;

import java.util.ArrayList;
import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.input.Keyboard;
import uet.oop.bomberman.output.Audio;

import java.util.Iterator;
import java.util.List;
import uet.oop.bomberman.entities.bomb.Flame;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.entities.tile.item.Item;
import uet.oop.bomberman.level.Coordinates;

public class Bomber extends Character {

    private List<Bomb> _bombs;
    protected Keyboard _input;

    protected int _timeBetweenPutBombs = 0;
    //nếu timeBetween < 0 thì được đặt quả bomb tiếp theo
    //mỗi lần đặt bomb mới thì reset về 1 giá trị > 0

    //list Item of bomber.
    public static List<Item> _listItem = new ArrayList<Item>();


    public Bomber(int x, int y, Board board) {
        super(x, y, board);
        _bombs = _board.getBombs();
        _input = _board.getInput();
        _sprite = Sprite.player_right;
    }

    @Override
    public void update() {
        clearBombs();
        if (!_alive) {
            afterKill();
            return;
        }

        if (_timeBetweenPutBombs < -7500) {
            _timeBetweenPutBombs = 0;
        } else {
            _timeBetweenPutBombs--;
        }

        animate();

        calculateMove();

        detectPlaceBomb();
    }

    @Override
    public void render(Screen screen) {
        calculateXOffset();

        if (_alive) {
            chooseSprite();
        } else {
            _sprite = Sprite.player_dead1;
        }

        screen.renderEntity((int) _x, (int) _y - _sprite.SIZE , this);
    }

    public void calculateXOffset() {
        int xScroll = Screen.calculateXOffset(_board, this);
        Screen.setOffset(xScroll, 0);
    }


    // kiểm tra xem có đặt được bomb hay không, nếu có thì đặt tại vị trí bomber
    private void detectPlaceBomb() {

        if (_input.space && Game.getBombRate() > 0 && _timeBetweenPutBombs < 0) {

            int xt = Coordinates.pixelToTile(_x + _sprite.getSize() / 2);
            int yt = Coordinates.pixelToTile((_y + _sprite.getSize() / 2) - _sprite.getSize()); //subtract half player height and minus 1 y position

            if (bombTrue(xt, yt)) {
                placeBomb(xt, yt); // đặt bom
                Game.addBombRate(-1);
                _timeBetweenPutBombs = 30;

            }
        }
    }

    private boolean bombTrue(int xt, int yt) {
        Bomb b = new Bomb(xt, yt, _board);
        for (Bomb bom : _board.getBombs()) {
            if (bom.equal(b)) {
                return false;
            }
        }
        return true;
    }

    //tạo ra bomb và đặt vào x y
    protected void placeBomb(int x, int y) {
        Bomb b = new Bomb(x, y, _board);
        _board.addBomb(b);

        //phat am thanh dat bom
        Audio bombSound = new Audio(Audio.PLACE_BOMB);
        bombSound.play();
    }

    private void clearBombs() {
        Iterator<Bomb> bs = _bombs.iterator();

        Bomb b;
        while (bs.hasNext()) {
            b = bs.next();
            if (b.isRemoved()) {
                bs.remove();
                Game.addBombRate(1);
            }
        }

    }

    public static void clearItem(){
        Bomber._listItem.clear();
    }

    public boolean isDead() {
        return !_alive;
    }

    @Override
    public void kill() {
        if (!_alive) {
            return;
        }
        _alive = false;

        //phat am thanh khi Bomber chet
        Audio deadSound = new Audio(Audio.DEAD);
        deadSound.play();
    }

    @Override
    protected void afterKill() {
        if (_timeAfter > 0) {
            --_timeAfter;
        }
        else{
            _board.setHeart(-1);
            removePowerups();
            _board.restartLevel();

        }

    }

    @Override
    protected void calculateMove() {
// nhận keyboard và tính toán tọa độ sẽ di chuyển tới

        int xa = 0, ya = 0;
        if (_input.up) {
            ya--;
        }
        if (_input.down) {
            ya++;
        }
        if (_input.left) {
            xa--;
        }
        if (_input.right) {
            xa++;
        }

        if (_input.up || _input.down) {
            if (_x % 16 <= 9 && _x % 16 > 4) {
                _x = (int) (_x / 16) * 16 + 16 - Sprite.player_down.getRealWidth();
            } else if ((_x - (int) (_x / 16) * 16) > 12) {
                _x = (int) (_x / 16) * 16 + 16;
            }
        }
        if (_input.left || _input.right) {
            if (_y % 16 <= 5) {
                _y = (int) (_y / 16) * 16;
            } else if ((_y - (int) (_y / 16) * 16) > 12) {
                _y = (int) (_y / 16) * 16 + 16;
            }
        }
        if (xa != 0 || ya != 0) {
            move(xa * Game.getBomberSpeed(), ya * Game.getBomberSpeed());
            _moving = true;
        } else {
            _moving = false;
        }

    }

    @Override
    public boolean canMove(double x, double y) {
        // ktra xem có thể di chuyển đến x y hay ko
        for (int c = 0; c < 4; c++) { //colision detection for each corner of the player
            double xt = ((_x + x) + c % 2 * 11) / Game.TILES_SIZE; //divide with tiles size to pass to tile coordinate
            double yt = ((_y + y) + c / 2 * 12 - 13) / Game.TILES_SIZE; //these values are the best from multiple tests

            Entity a = _board.getEntity(xt, yt, this);

            if (!a.collide(this)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void move(double xa, double ya) {
        // thực hiện di chuyển

        if (xa > 0) {
            _direction = 1;
        }
        if (xa < 0) {
            _direction = 3;
        }
        if (ya > 0) {
            _direction = 2;
        }
        if (ya < 0) {
            _direction = 0;
        }

        if (canMove(0, ya)) { //separate the moves for the player can slide when is colliding
            _y += ya;
        }

        if (canMove(xa, 0)) {
            _x += xa;
        }
    }

    // kiểm tra va chạm.
    @Override
    public boolean collide(Entity e) {
        // ktra va chạm với lửa
        if (e instanceof Flame) {
            kill();
            return false;
        }
        // ktra va chạm với enemy
        if (e instanceof Enemy) {
            double x1 = this.getX();
            double x2 = this.getY();
            double e1 = e.getX();
            double e2 = e.getY();
            double z1 = this.getSprite().getRealWidth();
            double z2 = this.getSprite().getRealHeight();
            double m1 = e.getSprite().getRealWidth();
            double m2 = e.getSprite().getRealHeight();
            if (Math.abs(x1 - e1) < (z1 + m1) / 2) {
                if (Math.abs(x2 - e2) < (z2 + m2) / 2) {
                    kill();
                    return false;
                }
            }
        }
        return true;
    }

    private void chooseSprite() {
        // chọn hình ảnh để render khi di chuyển
        switch (_direction) {
            case 0:
                _sprite = Sprite.player_up;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_up_1, Sprite.player_up_2, _animate, 20);
                }
                break;
            case 1:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
            case 2:
                _sprite = Sprite.player_down;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_down_1, Sprite.player_down_2, _animate, 20);
                }
                break;
            case 3:
                _sprite = Sprite.player_left;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_left_1, Sprite.player_left_2, _animate, 20);
                }
                break;
            default:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
        }
    }

    // add hadling eating Item.
    public void addPowerup(Item p) {
        if (p.isRemoved()) {
            return;
        }
        _listItem.add(p);

        p.setValues();

    }

    public void clearUsedPowerups() {
        Item p;
        for (int i = 0; i < _listItem.size(); i++) {
            p = _listItem.get(i);
            if (p.isActive() == false) {
                _listItem.remove(i);
            }
        }
    }

    public void removePowerups() {
        for (int i = 0; i < _listItem.size(); i++) {
            _listItem.remove(i);
        }

    }
}

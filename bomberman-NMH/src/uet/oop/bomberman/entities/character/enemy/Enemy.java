package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.Message;
import uet.oop.bomberman.entities.bomb.Flame;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.character.enemy.ai.AI;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.level.Coordinates;

import java.awt.*;

public abstract class Enemy extends Character {
    protected int _points;

    protected double _speed;
    protected AI _ai;

    protected final double MAX_STEPS;
    protected final double rest;
    protected double _steps;

    protected int _finalAnimation = 30;
    protected Sprite _deadSprite;

    public Enemy(int x, int y, Board board, Sprite dead, double speed, int points) {
        super(x, y, board);

        _points = points;
        _speed = speed;

        MAX_STEPS = Game.TILES_SIZE / _speed;
        rest = (MAX_STEPS - (int) MAX_STEPS) / MAX_STEPS;
        _steps = MAX_STEPS;

        _timeAfter = 20;
        _deadSprite = dead;
    }

    @Override
    public void update() {
        animate();

        if(!_alive) {
            afterKill();
            return;
        }

        if(_alive)
            calculateMove();
    }

    @Override
    public void render(Screen screen) {

        if(_alive)
            chooseSprite();
        else {
            if(_timeAfter > 0) {
                _sprite = _deadSprite;
                _animate = 0;
            } else {
                _sprite = Sprite.movingSprite(Sprite.mob_dead1, Sprite.mob_dead2, Sprite.mob_dead3, _animate, 60);
            }

        }

        screen.renderEntity((int)_x, (int)_y - _sprite.SIZE, this);
    }

    @Override
    public void calculateMove() {
        //  Tính toán hướng đi và di chuyển Enemy theo _ai và cập nhật giá trị cho _direction
        //  sử dụng canMove() để kiểm tra xem có thể di chuyển tới điểm đã tính toán hay không
        //  sử dụng move() để di chuyển
        //   cập nhật lại giá trị cờ _moving khi thay đổi trạng thái di chuyển

        int xa = 0, ya = 0;
        if(_steps <= 0){
            _direction = _ai.calculateDirection();
            _steps = MAX_STEPS;
        }

        if(_direction == 0) ya--; // len
        if(_direction == 2) ya++; // phai
        if(_direction == 3) xa--; //trai
        if(_direction == 1) xa++; // xuong

        if(canMove(xa, ya)) {
            _steps -= 1 + rest;
            move(xa * _speed, ya * _speed);
            _moving = true;
        } else {
            _steps = 0;
            _moving = false;
        }
    }

    @Override
    public void move(double xa, double ya) {
        if(!_alive) return;
        _y += ya;
        _x += xa;
    }

    @Override
    public boolean canMove(double x, double y) {
        //  kiểm tra có đối tượng tại vị trí chuẩn bị di chuyển đến và có thể di chuyển tới đó hay không

        double xr = _x, yr = _y - 16; //subtract y to get more accurate results

        //the thing is, subract 15 to 16 (sprite size), so if we add 1 tile we get the next pixel tile with this
        //we avoid the shaking inside tiles with the help of steps
        if(_direction == 0) { yr += _sprite.getSize() -1 ; xr += _sprite.getSize()/2; }
        if(_direction == 1) { yr += _sprite.getSize()/2; xr += 1;}
        if(_direction == 2) { xr += _sprite.getSize()/2; yr += 1;}
        if(_direction == 3) { xr += _sprite.getSize() -1; yr += _sprite.getSize()/2;}

        int xx = Coordinates.pixelToTile(xr) +(int)x;
        int yy = Coordinates.pixelToTile(yr) +(int)y;

        Entity a = _board.getEntity(xx, yy, this); //entity of the position we want to go

        return a.collide(this);
    }

    @Override
    public boolean collide(Entity e) {
        // xử lý va chạm với Flame

        if (e instanceof Flame) {
            kill();
            return false;
        }

        // xử lý va chạm với Bomber
        if (e instanceof Bomber) {
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
                    ((Bomber) e).kill();
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void kill() {
        if(!_alive) return;
        _alive = false;

        _board.addPoints(_points);

        Message msg = new Message("+" + _points, getXMessage(), getYMessage(), 2, Color.white, 14);
        _board.addMessage(msg);
    }


    @Override
    protected void afterKill() {
        if(_timeAfter > 0) --_timeAfter;
        else {
            if(_finalAnimation > 0) --_finalAnimation;
            else
                remove();
        }
    }

    protected abstract void chooseSprite();
}

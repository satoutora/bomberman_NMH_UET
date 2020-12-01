package uet.oop.bomberman.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
//import sun.audio.AudioPlayer;
//import sun.audio.AudioStream;
import uet.oop.bomberman.graphics.IRender;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.level.Coordinates;
import java.util.concurrent.TimeUnit;

/**
 * Lớp đại diện cho tất cả thực thể trong game (Bomber, Enemy, Wall, Brick,...)
 */
public abstract class Entity implements IRender {

    protected double _x, _y;
    protected boolean _removed = false;
    protected Sprite _sprite;

    /**
     * Phương thức này được gọi liên tục trong vòng lặp game,
     * mục đích để xử lý sự kiện và cập nhật trạng thái Entity
     */
    @Override
    public abstract void update();

    /**
     * Phương thức này được gọi liên tục trong vòng lặp game,
     * mục đích để cập nhật hình ảnh của entity theo trạng thái
     */
    @Override
    public abstract void render(Screen screen);

    public void remove() {
        _removed = true;
    }

    public boolean isRemoved() {
        return _removed;
    }

    public Sprite getSprite() {
        return _sprite;
    }

    /**
     * Phương thức này được gọi để xử lý khi hai entity va chạm vào nhau
     * Nếu brick va chạm vơi flame thì blick bị hủy diệt.xem hàm destroyableTile.destroy().
     * Nếu Bomb va chạm với flame thì sẽ tự nổ. Xem hàm Bomb.explode().
     * Nếu bomber va chạm với flame hoặc enemy thì sẽ chết. Hàm Bomberkill().
     * Nếu enemy va chạm với flame thì chết.
     */
    public abstract boolean collide(Entity e);

    public double getX() {
        return _x;
    }

    public double getY() {
        return _y;
    }

    public int getXTile() {
        return Coordinates.pixelToTile(_x + _sprite.SIZE / 2);
    }

    public int getYTile() {
        return Coordinates.pixelToTile(_y - _sprite.SIZE / 2);
    }

}

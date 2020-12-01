package uet.oop.bomberman.level;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.enemy.Balloon;
import uet.oop.bomberman.entities.character.enemy.Doll;
import uet.oop.bomberman.entities.character.enemy.Kondoria;
import uet.oop.bomberman.entities.character.enemy.Minvo;
import uet.oop.bomberman.entities.character.enemy.Oneal;
import uet.oop.bomberman.entities.tile.Grass;
import uet.oop.bomberman.entities.tile.Portal;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.entities.tile.destroyable.Brick;
import uet.oop.bomberman.entities.tile.item.BombItem;
import uet.oop.bomberman.entities.tile.item.FlameItem;
import uet.oop.bomberman.entities.tile.item.HeartItem;
import uet.oop.bomberman.entities.tile.item.SpeedItem;
import uet.oop.bomberman.output.LoadLevelException;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;

public class FileLevelLoader extends LevelLoader {

    private static char[][] _map;

    public FileLevelLoader(Board board, int level) throws LoadLevelException {
        super(board, level);
    }

    /**
     * đọc file cấu hình và lưu trữ ma trận chứa các đối tượng vào mảng 2 chiều cho trước _map
     * file cấu hình được lưu trong file res của project.
     * 3 số đầu của file txt chứa level, height, width
     * @param level
     */
    @Override
    public void loadLevel(int level) {

        ArrayList<String> s = new ArrayList<>();
        FileReader fr = null;
        try {

            fr = new FileReader("Level" + level + ".txt");
            BufferedReader br = new BufferedReader(fr);
            String str = br.readLine();
            int i = 0;
            while (!str.equals("")) {
                s.add(str);
                str = br.readLine();
            }

        } catch (FileNotFoundException ex) {
            System.out.println("File not found !!!!");
        } catch (IOException ex) {
        }

        String[] ar = s.get(0).trim().split(" ");
        _level = Integer.parseInt(ar[0]);
        _height = Integer.parseInt(ar[1]);
        _width = Integer.parseInt(ar[2]);
        _map = new char[_height][_width];
        for (int i = 0; i < _height; i++) {
            for (int j = 0; j < _width; j++) {
                _map[i][j] = s.get(i + 1).charAt(j);
            }
        }
    }

    /**
     * đọc các thông tin trong map để hiển thị trong game.
     */
    @Override
    public void createEntities() {

        for (int y = 0; y < _height; y++) {
            for (int x = 0; x < _width; x++) {
                int pos = x + y * getWidth();
                char c = _map[y][x];
                switch(c) {
                    case '#':
                        _board.addEntity(pos, new Wall(x, y, Sprite.wall));
                        break;
                    case 'b':
                        LayeredEntity layer = new LayeredEntity(x, y,
                                new Grass(x ,y, Sprite.grass),
                                new Brick(x ,y, Sprite.brickItem));

                        if(_board.isItemUsed(x, y, _level) == false) {
                            layer.addBeforeTop(new BombItem(x, y, _level, Sprite.powerup_bombs));
                        }
                        _board.addEntity(pos, layer);
                        break;
                    case 's':
                        layer = new LayeredEntity(x, y,
                                new Grass(x ,y, Sprite.grass),
                                new Brick(x ,y, Sprite.brickItem));

                        if(_board.isItemUsed(x, y, _level) == false) {
                            layer.addBeforeTop(new SpeedItem(x, y, _level, Sprite.powerup_speed));
                        }

                        _board.addEntity(pos, layer);
                        break;
                    case 'f':
                        layer = new LayeredEntity(x, y,
                                new Grass(x ,y, Sprite.grass),
                                new Brick(x ,y, Sprite.brickItem));

                        if(_board.isItemUsed(x, y, _level) == false) {
                            layer.addBeforeTop(new FlameItem(x, y, _level, Sprite.powerup_flames));
                        }

                        _board.addEntity(pos, layer);
                        break;
                    case 'h':
                        layer = new LayeredEntity(x, y,
                                new Grass(x ,y, Sprite.grass),
                                new Brick(x ,y, Sprite.brickItem));

                        if(_board.isItemUsed(x, y, _level) == false) {
                            layer.addBeforeTop(new HeartItem(x, y, _level, Sprite.powerup_detonator));
                        }

                        _board.addEntity(pos, layer);
                        break;
                    case '*':
                        _board.addEntity(pos, new LayeredEntity(x, y,
                                new Grass(x ,y, Sprite.grass),
                                new Brick(x ,y, Sprite.brick)) );
                        break;
                    case 'x':
                        _board.addEntity(pos, new LayeredEntity(x, y,
                                new Grass(x ,y, Sprite.grass),
                                new Portal(x ,y, _board, Sprite.portal),
                                new Brick(x ,y, Sprite.brickPortal)) );

                        break;
                    case 'p':
                        _board.addCharacter( new Bomber(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
                        //_board.addCharacter( new Bomber(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y), _board));
                        Screen.setOffset(0, 0);

                        _board.addEntity(pos, new Grass(x, y, Sprite.grass) );
                        break;
                    //Enemies
                    case '1':
                        _board.addCharacter( new Balloon(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
                        _board.addEntity(pos, new Grass(x, y, Sprite.grass) );
                        break;
                    case '2':
                        _board.addCharacter( new Oneal(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
                        _board.addEntity(pos, new Grass(x, y, Sprite.grass) );
                        break;
                    case '3':
                        _board.addCharacter( new Doll(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
                        _board.addEntity(pos, new Grass(x, y, Sprite.grass) );
                        break;
                    case '4':
                        _board.addCharacter( new Kondoria(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
                        _board.addEntity(pos, new Grass(x, y, Sprite.grass) );
                        break;
                    case '5':
                        _board.addCharacter( new Minvo(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
                        _board.addEntity(pos, new Grass(x, y, Sprite.grass) );
                        break;
                    default:
                        _board.addEntity(pos, new Grass(x, y, Sprite.grass) );
                        break;
                }
            }
        }
    }

}

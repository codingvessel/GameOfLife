package com.codingvessel.gameoflife;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class GameOfLife extends Game {

    public static final int SCREEN_WIDTH = 1920;
    public static final int SCREEN_HEIGHT = 1200;

    public static final int CELL_SIZE = 16;
    public static final int COLUMNS = 120;
    public static final int ROWS = 75;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    public BitmapFont font;

    private Viewport viewport;
    private OrthographicCamera camera;

    private Map<Vector2, Cell> grid = new HashMap<>();

    private boolean simulate;
    private long generation;


    private static final float TICK_DURATION = 1.0f / 10f;
    private float elapsedTime = 0;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        initGrid();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    private void initGrid() {
        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Cell cell = new Cell(x, y);
                cell.setAlive(false);
                grid.put(new Vector2(x, y), cell);
            }
        }
    }

    @Override
    public void render() {
        super.render();
        ScreenUtils.clear(0.35f, 0.35f, 0.35f, 1);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        handleInputs();

        elapsedTime += Gdx.graphics.getDeltaTime();

        while (elapsedTime >= TICK_DURATION) {
            elapsedTime -= TICK_DURATION;
            gameTick();
        }

        drawCells();
        drawLines();

        long livingCellsCounter = grid.entrySet().stream().filter(e -> e.getValue().isAlive()).count();
        batch.begin();
        font.draw(batch, "Living Cells: " + livingCellsCounter, (float) SCREEN_WIDTH / 2, SCREEN_HEIGHT);
        font.draw(batch, "Generation: " + generation, (float) SCREEN_WIDTH / 2, SCREEN_HEIGHT - CELL_SIZE);
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 0, SCREEN_HEIGHT);
        font.draw(batch, "LMB to set Cell alive, RMB to set Cell dead", 0, SCREEN_HEIGHT - CELL_SIZE);
        font.draw(batch, "Press 'Space' to start lifecycle, 'R' to Reset", 0, SCREEN_HEIGHT - 2 * CELL_SIZE);

        batch.end();
    }

    private void handleInputs() {
        if (Gdx.input.isTouched() && !simulate) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touchPos);
            int xPos = MathUtils.floor(touchPos.x) / CELL_SIZE;
            int YPos = MathUtils.floor(touchPos.y) / CELL_SIZE;
            Cell cell = grid.get(new Vector2(xPos, YPos));

            if (isWithinBounds(xPos, YPos)) {
                if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
                    if (cell != null) {
                        cell.setAlive(true);
                    }
                } else if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
                    if (cell != null) {
                        cell.setAlive(false);
                    }
                }
            }

        }

        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            simulate = true;
        }

        if (Gdx.input.isKeyJustPressed(Keys.R)) {
            simulate = false;
            generation = 0;
            grid.forEach((key, value) -> value.setAlive(false));
        }
    }

    private static boolean isWithinBounds(int xPos, int YPos) {
        return xPos >= 0 && xPos < COLUMNS && YPos >= 0 && YPos < ROWS;
    }

    private void gameTick() {
        if (simulate) {
            generation++;
            Map<Vector2, Cell> copiedGrid = deepCopyOfGrid();
            applyRules(copiedGrid);
            grid = copiedGrid;
        }

        if (grid.entrySet().stream().noneMatch(e -> e.getValue().isAlive())) {
            simulate = false;
            generation = 0;
        }
    }

    private Map<Vector2, Cell> deepCopyOfGrid() {
        Map<Vector2, Cell> copiedGrid = new HashMap<>();
        for (Entry<Vector2, Cell> entry : grid.entrySet()) {
            Vector2 key = new Vector2(entry.getKey());
            Cell value = new Cell(entry.getValue().x, entry.getValue().y);
            value.setAlive(entry.getValue().isAlive());
            copiedGrid.put(key, value);
        }
        return copiedGrid;
    }

    private void applyRules(Map<Vector2, Cell> copiedGrid) {
        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Cell cell = copiedGrid.get(new Vector2(x, y));
                cell.setHealthStatus(x, y, grid);
            }
        }
    }

    private void drawCells() {
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 0, 0.9f);

        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                if (grid.get(new Vector2(x, y)).isAlive()) {
                    shapeRenderer.rect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
        shapeRenderer.end();
    }

    private void drawLines() {
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 0.3f);

        for (int i = 0; i <= COLUMNS; i++) {
            float x = i * CELL_SIZE;
            shapeRenderer.line(x, 0, x, SCREEN_HEIGHT);
        }

        for (int i = 0; i <= ROWS; i++) {
            float y = i * CELL_SIZE;
            shapeRenderer.line(0, y, SCREEN_WIDTH, y);
        }

        shapeRenderer.end();
    }


    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}

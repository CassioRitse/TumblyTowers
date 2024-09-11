package com.scs.trickytowers;

import org.jbox2d.common.Vec2;

import com.scs.trickytowers.entity.VibratingPlatform;
import com.scs.trickytowers.entity.shapes.AbstractShape;
import com.scs.trickytowers.entity.shapes.Rectangle;
import com.scs.trickytowers.input.IInputDevice;

import ssmith.lang.Functions;

public class Player {

    public int score, id_ZB;
    public IInputDevice input;
    public AbstractShape currentShape;
    public float prevShapeY;
    private Main_TumblyTowers main;
    public VibratingPlatform vib;
    
    private static int nextId = 0;

	public Player(Main_TumblyTowers _main, IInputDevice _input) {
        super();
        id_ZB = nextId++;
        main = _main;
        input = _input;
    }


    public void process() {
        input.readEvents();

        boolean hasValidShape = currentShape != null && currentShape.body != null;
        if (hasValidShape) { 
            float currentShapeYPosition = currentShape.body.getWorldCenter().y;
            float positionDifference = Math.abs(currentShapeYPosition - prevShapeY);
            
            boolean isShapeTooLow = currentShapeYPosition < 1;
            boolean isShapeMovingSignificantly = positionDifference > 0.01f;

            if (isShapeTooLow || isShapeMovingSignificantly) {
                boolean isFirePressed = this.input.isFirePressed();
                currentShape.applyDrag(!isFirePressed);
                prevShapeY = currentShapeYPosition;
                
                boolean hasShapeNotCollided = !currentShape.collided;
                if (hasShapeNotCollided) {
                    Vec2 newPos = new Vec2(currentShape.body.getWorldCenter());
                    float newAngle = currentShape.body.getAngle();

                    boolean isLeftPressed = input.isLeftPressed();
                    boolean isRightPressed = input.isRightPressed();
                    boolean isSpinLeftPressed = input.isSpinLeftPressed();
                    boolean isSpinRightPressed = input.isSpinRightPressed();
                    
                    boolean canMoveLeft = isLeftPressed && currentShape.body.getWorldCenter().x > main.getLeftBucketPos(id_ZB);
                    boolean canMoveRight = isRightPressed && currentShape.body.getWorldCenter().x < main.getRightBucketPos(id_ZB);
                    
                    if (canMoveLeft) {
                        newPos.x -= Statics.STD_CELL_SIZE / 2;
                        input.clearInputs();
                    } else if (canMoveRight) {
                        newPos.x += Statics.STD_CELL_SIZE / 2;
                        input.clearInputs();
                    } else if (isSpinLeftPressed) {
                        newAngle -= Math.PI / 8;
                        input.clearInputs();
                    } else if (isSpinRightPressed) {
                        newAngle += Math.PI / 8;
                        input.clearInputs();
                    }

                    currentShape.body.setTransform(newPos, newAngle);
                }
            } else {
                boolean isBelowWinningHeight = currentShape.getPosition().y < Statics.LOGICAL_WINNING_HEIGHT;
                if (isBelowWinningHeight) {
                    main.playerWon(this);
                }
                Statics.p("Shape removed");
                currentShape = null;
            }
        } else {
            float startXPosition = main.getShapeStartPosX(id_ZB);
            currentShape = getRandomShape(startXPosition);
            main.addEntity(currentShape);
            main.playSound("shapedropped.ogg");
        }
    }
    
    private AbstractShape getRandomShape(float x) {
        int width = Functions.rnd(1, 4);
        int height = Functions.rnd(1, 4);
        return new Rectangle(main, Statics.STD_CELL_SIZE * width, Statics.STD_CELL_SIZE * height, x);
    }
}
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
		if (currentShape != null && currentShape.body != null) { 
			float diff = Math.abs(currentShape.body.getWorldCenter().y - prevShapeY);
			if (currentShape.body.getWorldCenter().y < 1 || diff > 0.01f) {
				currentShape.applyDrag(!this.input.isFirePressed());
				prevShapeY = currentShape.body.getWorldCenter().y;
				if (currentShape.collided == false) {
					Vec2 newPos = new Vec2(currentShape.body.getWorldCenter());
					float newAngle = currentShape.body.getAngle();
					if (input.isLeftPressed()) {
						if (currentShape.body.getWorldCenter().x > main.getLeftBucketPos(id_ZB)) {
							newPos.x -= Statics.STD_CELL_SIZE/2;
						}
						input.clearInputs();
					} else if (input.isRightPressed()) {
						if (currentShape.body.getWorldCenter().x < main.getRightBucketPos(id_ZB)) {
							newPos.x += Statics.STD_CELL_SIZE/2;
						}
						input.clearInputs();
					} else if (input.isSpinLeftPressed()) {
						newAngle -= Math.PI/8;
						input.clearInputs();
					} else if (input.isSpinRightPressed()) {
						newAngle += Math.PI/8;
						input.clearInputs();
					}
					currentShape.body.setTransform(newPos, newAngle);
				}
			} else {
				if (currentShape.getPosition().y < Statics.LOGICAL_WINNING_HEIGHT) {
					main.playerWon(this);
				}
				Statics.p("Shape removed");
				currentShape = null;
			}
		} else {
			currentShape = getRandomShape(main.getShapeStartPosX(id_ZB));
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

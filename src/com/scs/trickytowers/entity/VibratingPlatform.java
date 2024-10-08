package com.scs.trickytowers.entity;

import java.awt.Color;

import org.jbox2d.dynamics.BodyType;

import com.scs.trickytowers.BodyUserData;
import com.scs.trickytowers.JBox2DFunctions;
import com.scs.trickytowers.Main_TumblyTowers;
import com.scs.trickytowers.entity.components.IProcessable;

import ssmith.util.Timer;

public class VibratingPlatform extends PhysicalEntity implements IProcessable {

	private Timer timer = new Timer(1000);
	
	public VibratingPlatform(Main_TumblyTowers main, float x, float y, float width) {
		super(main, "VibratingPlatform");
		
		BodyUserData bud = new BodyUserData("Rectangle", Color.red, this);
		this.body = JBox2DFunctions.AddRectangle(bud, main.world, x, y, width, 5f, BodyType.STATIC, 0.0f, 0.5f, 2f);
	}

	
	@Override
	public void preprocess(long interpol) {
		if (timer.hasHit(interpol)) {}		
	}

	
	@Override
	public void postprocess(long interpol) {
		
	}

}

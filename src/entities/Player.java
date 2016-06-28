package entities;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity{
	private static final float RUN_SPEED = 30;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -60;
	private static final float JUMP_POWER = 30;
		
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardSpeed = 0;
	
	private boolean isInAir = false;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	
	public int move(Terrain terrain, int[][] collisionTable, int[][] indexTable, List<Entity> entities, int foundEggs){
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		float checkX = super.getPosition().x + dx;
		float checkZ = super.getPosition().z + dz;
		if(collisionTable[(int) checkX][(int) checkZ] != 1){
			super.increasePosition(dx, 0, dz);
		}
		if(collisionTable[(int) super.getPosition().x][(int) super.getPosition().z] == 2){
			entities.remove(indexTable[(int) super.getPosition().x][(int) super.getPosition().z]);
			collisionTable[(int) super.getPosition().x][(int) super.getPosition().z] = 0;
			foundEggs++;
		}
		upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(super.getPosition().y <terrainHeight){
			upwardSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
		return foundEggs;
	}
	
	private void jump(){
		if(!isInAir){
			this.upwardSpeed = JUMP_POWER;
			isInAir = true;
		}
	}
	
	private void checkInputs(){
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			this.currentSpeed = RUN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			this.currentSpeed = -RUN_SPEED;
		}else{
			this.currentSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			this.currentTurnSpeed = -TURN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			this.currentTurnSpeed = TURN_SPEED;

		}else{
			this.currentTurnSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			jump();
		}
	}

}

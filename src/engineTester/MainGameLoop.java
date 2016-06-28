package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		TextMaster.init(loader);
		
		FontType font = new FontType(loader.loadTexture("font"), "font");
		
		int[][] collisionTable = new int[800][800];
		for(int i=0; i<800; i++){
			collisionTable[0][i] = 1;
			collisionTable[800-1][i] = 1;
			collisionTable[i][0] = 1;
			collisionTable[i][800-1] = 1;
		}
				
		//============Terrain Texture==============
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		//=========================================
		
		//============Objects======================
		TexturedModel tree1 = new TexturedModel(OBJLoader.loadObjModel("tree", loader),new ModelTexture(loader.loadTexture("tree")));
		TexturedModel tree2 = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree", loader),new ModelTexture(loader.loadTexture("lowPolyTree")));
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),new ModelTexture(loader.loadTexture("grassTexture")));
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader),new ModelTexture(loader.loadTexture("fern")));
		fern.getTexture().setHasTransparency(true);
		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),new ModelTexture(loader.loadTexture("flower")));
		flower.getTexture().setHasTransparency(true);
		TexturedModel fence = new TexturedModel(OBJLoader.loadObjModel("fence", loader), new ModelTexture(loader.loadTexture("fence")));
		fence.getTexture().setUseFakeLighting(true);
		TexturedModel eggs = new TexturedModel(OBJLoader.loadObjModel("egg", loader), new ModelTexture(loader.loadTexture("eggs")));
		//=========================================
		
		Terrain terrain = new Terrain(0,0,loader,texturePack,blendMap, "heightMap_converted");
		
		//============Generates Random trees and flowers/ferns===================
		List<Entity> entities = new ArrayList<Entity>();
		for(int i=0; i<80; i++){
			float y = terrain.getHeightOfTerrain(0,i*10);
			entities.add(new Entity(fence, new Vector3f(0,y,(i*10) + 4),0,0,0,2));
			y = terrain.getHeightOfTerrain(800,i*10);
			entities.add(new Entity(fence, new Vector3f(800,y,(i*10) + 4),0,0,0,2));
			y = terrain.getHeightOfTerrain(i*10,0);
			entities.add(new Entity(fence, new Vector3f((i*10) + 4,y,0),0,90,0,2));
			y = terrain.getHeightOfTerrain(i*10,800);
			entities.add(new Entity(fence, new Vector3f((i*10) + 4,y,800),0,90,0,2));
		}
		int index = 0;
		int[][] indexTable = new int[800][800];
		Random random = new Random(676452);
		for(int i=0;i<400;i++){
			if(i%20 == 0){
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * 800;				
				float y = terrain.getHeightOfTerrain(x,z);
				entities.add(index,new Entity(grass, new Vector3f(x,y,z),0,random.nextFloat()*360,0,2f));
				index++;
				x = random.nextFloat() * 800;
				z = random.nextFloat() * 800;
				y = terrain.getHeightOfTerrain(x,z);
				entities.add(index,new Entity(flower, new Vector3f(x,y,z),0,random.nextFloat()*360,0,2.5f));
				index++;
			}
			if(i%5 == 0){
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * 800;
				float y = terrain.getHeightOfTerrain(x,z);
				entities.add(index,new Entity(tree1, new Vector3f(x,y,z),0,0,0,random.nextFloat()*2+17));
				index++;
				//====Place in table=====
				collisionTable[(int) x][(int) z] = 1;
				collisionTable[(int) x+1][(int) z] = 1;
				collisionTable[(int) x][(int) z+1] = 1;
				collisionTable[(int) x+1][(int) z+1] = 1;
				collisionTable[(int) x-1][(int) z] = 1;
				collisionTable[(int) x][(int) z-1] = 1;
				collisionTable[(int) x-1][(int) z-1] = 1;
				//=======================
				x = random.nextFloat() * 800;
				z = random.nextFloat() * 800;
				y = terrain.getHeightOfTerrain(x,z);
				entities.add(index,new Entity(tree2, new Vector3f(x,y,z),0,random.nextFloat() *360,0,random.nextFloat() * 0.1f + 2f));
				index++;
				//====Place in Table======
				collisionTable[(int) x][(int) z] = 1;
				for(int i1=0; i1<5; i1++){
					if(x-i1 > 0 && x+i1 < 800 && z-i1 > 0 && z+i1 < 800){
						collisionTable[(int) x+i1][(int) z] = 1;
						collisionTable[(int) x][(int) z+1] = 1;
						collisionTable[(int) x-i1][(int) z] = 1;
						collisionTable[(int) x][(int) z-i1] = 1;
						collisionTable[(int) x+i1][(int) z-i1] = 1;
						collisionTable[(int) x+i1][(int) z+1] = 1;
						collisionTable[(int) x-i1][(int) z+i1] = 1;
						collisionTable[(int) x-i1][(int) z-i1] = 1;
					}
				}
				//========================
				x = random.nextFloat() * 800;
				z = random.nextFloat() * 800;
				y = terrain.getHeightOfTerrain(x,z);
				entities.add(index,new Entity(fern, new Vector3f(x,y,z),0,random.nextFloat() * 360,0,2.4f));
				index++;
			}
			if(i%4 == 0){
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * 800;
				float y = terrain.getHeightOfTerrain(x,z);
				entities.add(index,new Entity(eggs, new Vector3f(x,y,z),0,random.nextFloat()*360,0,.15f));
				indexTable[(int) x][(int) z] = index;
				index++;
				collisionTable[(int) x][(int) z] = 2;
			}
		}
		//=========================================================================
		
		Light light = new Light(new Vector3f(20000,20000,2000),new Vector3f(1,1,1));
		
		MasterRenderer renderer = new MasterRenderer(loader);
		
		RawModel bunnyModel = OBJLoader.loadObjModel("stanfordBunny", loader);
		TexturedModel stanfordbunny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("white")));
		
		Player player = new Player(stanfordbunny, new Vector3f(400,0,400),0,180,0,1);
		Camera camera = new Camera(player);	
		
		int foundEggs = 0;
		GUIText text = new GUIText("Eggs: "+ foundEggs +"/100", 3, font, new Vector2f(0,0), .5f, true);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		while(!Display.isCloseRequested()){
			camera.move();
			foundEggs = player.move(terrain, collisionTable, indexTable, entities, foundEggs);
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			for(Entity entity:entities){
				renderer.processEntity(entity);
			}
			renderer.render(light, camera);
			TextMaster.removeText(text);
			text = new GUIText("Eggs: "+ foundEggs +"/100", 3, font, new Vector2f(0,0), .5f, true);
			TextMaster.render();
			DisplayManager.updateDisplay();
		}

		//==========Clean up===============
		TextMaster.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}

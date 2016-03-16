package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Line;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.LinkedList;
import java.util.Random;


public class Main extends SimpleApplication {
    Random rand = new Random();
    BulletAppState bullet;
    Pegboard pegboard;
    Marble marble;
    Peg peg;
    float time = 0;
    int count;
    LinkedList<Marble> marbles = new LinkedList<Marble>();
    long totalTime;
    long currentTime;
    long timeLen = 4000;

    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        Main app = new Main();
        initAppScreen(app);
        app.start();
    }

    // -------------------------------------------------------------------------
    @Override
    public void simpleInitApp() {
        initGui();
        initLightandShadow();
        //initCoordCross();
        initCam();
        initPhysics();
        pegboard = new Pegboard(this);
        totalTime = System.currentTimeMillis();
        // just so we have one appear so we know it works
        makeMarbles();
    }

    // -------------------------------------------------------------------------
    @Override
    public void simpleUpdate(float tpf) {
         currentTime = System.currentTimeMillis();
         if (currentTime - totalTime >= timeLen) {
            makeMarbles(); 
            totalTime = currentTime;
         }
           
        
    }

    // -------------------------------------------------------------------------
    private static void initAppScreen(SimpleApplication app) {
        AppSettings aps = new AppSettings(true);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        screen.width *= 0.75;
        screen.height *= 0.75;
        aps.setResolution(screen.width, screen.height);
        app.setSettings(aps);
        app.setShowSettings(false);
    }

    // -------------------------------------------------------------------------
    private void initGui() {
        setDisplayFps(true);
        setDisplayStatView(false);
    }

    // -------------------------------------------------------------------------
    private void initLightandShadow() {
        // Light1: white, directional
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.7f, -2.3f, 0.9f)).normalizeLocal());
        sun.setColor(ColorRGBA.Gray);
        rootNode.addLight(sun);

        // Light 2: Ambient, gray
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
        rootNode.addLight(ambient);

        // SHADOW
        // the second parameter is the resolution. Experiment with it! (Must be a power of 2)
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 4096, 1);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);
    }

    // -------------------------------------------------------------------------
    private void initCam() {
        flyCam.setEnabled(true);
        cam.setLocation(new Vector3f(2f, 3f, 4f));
        cam.lookAt(new Vector3f(0.5f, 1, -1f), Vector3f.UNIT_Y);
    }

    // -------------------------------------------------------------------------
    private void initPhysics() {
        // initialize the physics engine
        bullet = new BulletAppState();
        stateManager.attach(bullet);
        bullet.setDebugEnabled(false);
    }

    // -------------------------------------------------------------------------
    private void initCoordCross() {
        Material matRed, matGreen, matBlue;
        matRed = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matRed.setColor("Color", ColorRGBA.Red);
        matGreen = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matGreen.setColor("Color", ColorRGBA.Green);
        matBlue = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matBlue.setColor("Color", ColorRGBA.Blue);
        //
        Line xAxis = new Line(new Vector3f(-3, 0, 0), Vector3f.ZERO);
        Line yAxis = new Line(new Vector3f(0, -3, 0), Vector3f.ZERO);
        Line zAxis = new Line(new Vector3f(0, 0, -3), Vector3f.ZERO);
        Arrow ax = new Arrow(new Vector3f(3, 0, 0));
        Arrow ay = new Arrow(new Vector3f(0, 3, 0));
        Arrow az = new Arrow(new Vector3f(0, 0, 3));
        Geometry geomX = new Geometry("xAxis", xAxis);
        Geometry geomY = new Geometry("yAxis", yAxis);
        Geometry geomZ = new Geometry("zAxis", zAxis);
        geomX.setMaterial(matRed);
        geomY.setMaterial(matGreen);
        geomZ.setMaterial(matBlue);
        Geometry geomXA = new Geometry("xAxis", ax);
        Geometry geomYA = new Geometry("yAxis", ay);
        Geometry geomZA = new Geometry("zAxis", az);
        geomXA.setMaterial(matRed);
        geomYA.setMaterial(matGreen);
        geomZA.setMaterial(matBlue);
        getRootNode().attachChild(geomX);
        getRootNode().attachChild(geomY);
        getRootNode().attachChild(geomZ);
        getRootNode().attachChild(geomXA);
        getRootNode().attachChild(geomYA);
        getRootNode().attachChild(geomZA);
    }
    
    protected void makeMarbles() {
        float random = rand.nextFloat();
        marble = new Marble(this, pegboard.createMarbleStartPosition(random));
    }
}

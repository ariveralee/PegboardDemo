package mygame;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author Rolf
 */
public class Pegboard implements PhysicsCollisionListener{

    private static final int NUMROWS = 7;
    private static final float BOARDSIZE = 4.0f;
    protected static final float PEGDISTANCE = 1.3f;
    private static final float PEGDISTANCEZ = PEGDISTANCE * 0.7f;
    protected static final float RESTITUTION = 0.4f;
    Main msa;
    float tilt; // tilt of board in degrees
    Node boardNode;
    Geometry geomGround, geomBoard;
    LinkedList<Peg> pegs = new LinkedList<Peg>();
    ArrayList<TargetBin> bins = new ArrayList<TargetBin>(NUMROWS);

    public Pegboard(Main m) {
        msa = m;
        tilt = 20.0f;
        initBoard();
        initPhysics();
    }

    // -------------------------------------------------------------------------
    public void setTilt(float degrees) {
        this.tilt = degrees;
        boardNode.setLocalRotation(Matrix3f.IDENTITY);
        boardNode.rotate(tilt / 180.0f * FastMath.PI, 0f, 0f);
    }

    // -------------------------------------------------------------------------
    private void initBoard() {
        //
        // ground
        Material matGround = new Material(msa.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        matGround.setBoolean("UseMaterialColors", true);
        matGround.setColor("Ambient", ColorRGBA.Red.mult(0.1f));
        matGround.setColor("Diffuse", ColorRGBA.White);
        matGround.setColor("Specular", ColorRGBA.White);
        matGround.setTexture("DiffuseMap", msa.getAssetManager().loadTexture("Textures/textureOldTable.jpg"));
        matGround.setFloat("Shininess", 10f);
        //
        Box box = new Box(BOARDSIZE * 3, 0.1f, BOARDSIZE * 5f);
        geomGround = new Geometry("ground", box);
        geomGround.setMaterial(matGround);
        geomGround.setShadowMode(RenderQueue.ShadowMode.Receive);
        geomGround.setLocalTranslation(0f, -0.1f, -0f);
        msa.getRootNode().attachChild(geomGround);
        //
        // board
        boardNode = new Node();
        Material matBoard = new Material(msa.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        matBoard.setBoolean("UseMaterialColors", true);
        matBoard.setColor("Ambient", ColorRGBA.White.mult(0.8f));
        matBoard.setColor("Diffuse", ColorRGBA.White);
        matBoard.setColor("Specular", ColorRGBA.Yellow);
        matBoard.setTexture("DiffuseMap", msa.getAssetManager().loadTexture("Textures/textureBoard.png"));
        matBoard.setFloat("Shininess", 10f);
        //
        box = new Box(BOARDSIZE, 0.05f, BOARDSIZE);
        geomBoard = new Geometry("board", box);
        geomBoard.setMaterial(matBoard);
        geomBoard.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        geomBoard.setLocalTranslation(0f, 0.05f, -BOARDSIZE);
        boardNode.attachChild(geomBoard);
        //
        // Pegs
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col <= row; col++) {
                Peg peg = new Peg(msa);
                float posX = col * PEGDISTANCE - row * PEGDISTANCE / 2f;
                float posZ = row * PEGDISTANCEZ - BOARDSIZE * 1.5f;
                peg.setPosition(posX, posZ);
                boardNode.attachChild(peg.geomPeg);
                pegs.add(peg);
            }
        }
        //
        // Targets
        for (int col = 1; col < NUMROWS; col++) {
            TargetBin tb = new TargetBin(msa, col);
            float posX = col * PEGDISTANCE - NUMROWS * PEGDISTANCE / 2f;
            float posZ = NUMROWS * PEGDISTANCEZ - BOARDSIZE * 1.65f;
            tb.setPosition(posX, posZ);
            boardNode.attachChild(tb.geomBox);
            bins.add(tb);
        }
        //
        // tilt and add board to scenery
        setTilt(tilt);
        msa.getRootNode().attachChild(boardNode);
    }

    // -------------------------------------------------------------------------
    // init physics here
    private void initPhysics() {
        RigidBodyControl tableGround = new RigidBodyControl(100.0f);
        geomGround.addControl(tableGround);
        tableGround.setKinematic(true);
        msa.bullet.getPhysicsSpace().add(tableGround);
        
        RigidBodyControl phyGround = new RigidBodyControl(100.0f); // kinematic: mass does not matter
        geomBoard.addControl(phyGround);
        phyGround.setKinematic(true);
        msa.bullet.getPhysicsSpace().add(phyGround);
        msa.bullet.getPhysicsSpace().addCollisionListener(this);
        
        
    }

    // -------------------------------------------------------------------------
    protected Vector3f createMarbleStartPosition(float interval) {
        // jitter in x
        float x = (float) ((Math.random() - 0.5) * interval);
        // include tilt in y,z
        float a = tilt / 180f * FastMath.PI;
        float y = 1.0f + 2*BOARDSIZE* FastMath.sin(a);
        float z = -BOARDSIZE * 1.8f * FastMath.cos(a);
        return (new Vector3f(x, y, z));
    }
    
    public void collision(PhysicsCollisionEvent event) {
        String binA = null;
        String binB = null;
        boolean targetBinA = false;
        boolean targetBinB = false;
        
        if ( event.getNodeA().getName().startsWith("bin")) {
            binA = event.getNodeA().getName().substring(4);
            targetBinA = true;
            
        } else if (event.getNodeB().getName().startsWith("bin")) {
            binB = event.getNodeB().getName().substring(4);
            targetBinB = true;
            
        }
        
        // if
        if (targetBinA == true) {
            // if we get here, we know the target is node A, so Node B is
            // the marble.
            if (event.getNodeB().getUserData("COLLIDED") == "YES") {
                return;
           }
            event.getNodeB().setUserData("COLLIDED", "YES");
            int a = Integer.parseInt(binA);
            a--;
            int printBin = a + 1;
            System.out.println("The marble just hit bin " + printBin );
            float hit = bins.get(a).hits++;
            bins.get(a).rescale(1f + hit);
           
            
        } else if (targetBinB == true) {
           // if we get here, we know the target is node B, so Node A is the
           // marble.
            if (event.getNodeA().getUserData("COLLIDED") == "YES") {
                return;
           }
            event.getNodeA().setUserData("COLLIDED", "YES");
            System.out.println("The Marble is node A");
            int b = Integer.parseInt(binB);
            b--;
            int printBin = b + 1;
            System.out.println("The marble just hit bin " + printBin );
            float hit = bins.get(b).hits++;
            bins.get(b).rescale(1f + hit);
        }
        
    }
    

}

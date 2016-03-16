/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author Rolf
 */
public class Marble {

    private Main msa;
    BulletAppState bullet = new BulletAppState();
    public static final float RADIUS = 0.15f;
    private static Material matMarble = null;
    Geometry geomMarble;


    // ------------------------------------------------------------------------
    public Marble(Main msa, Vector3f position) {
        this.msa = msa;
        //
        if (matMarble == null) {
            matMarble = new Material(msa.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
            matMarble.setBoolean("UseMaterialColors", true);
            matMarble.setColor("Ambient", new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
            matMarble.setColor("Diffuse", new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
            matMarble.setColor("Specular", ColorRGBA.White);
            matMarble.setFloat("Shininess", 10f); // shininess from 1-128
            matMarble.setTexture("DiffuseMap", msa.getAssetManager().loadTexture("Textures/textureMarble.png"));
        }
        //
        Sphere s = new Sphere(32, 32, RADIUS);
        geomMarble = new Geometry("marble", s);
        geomMarble.setMaterial(matMarble);
        geomMarble.setLocalTranslation(position);
        geomMarble.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        geomMarble.setUserData("COLLIDED", "NO");
        //
        msa.getRootNode().attachChild(geomMarble);
        initPhysics();
    }

    // -------------------------------------------------------------------------
    // initialize physics here
    private void initPhysics() {
        // The marble is created after initPhysics has been called in Main
        // this ensures that we have the bullet app state to reference from main
        RigidBodyControl phySmall = new RigidBodyControl(5.0f);
        geomMarble.addControl(phySmall);
        msa.bullet.getPhysicsSpace().add(phySmall);
       
    }
}

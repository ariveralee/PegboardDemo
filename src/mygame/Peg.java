package mygame;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author Rolf
 */
public class Peg {

    Main msa;
    Geometry geomPeg;
    static Material matPeg;

    // -------------------------------------------------------------------------
    public Peg(Main msa) {
        this.msa = msa;
        initPeg();
    }

    // -------------------------------------------------------------------------
    private void initPeg() {
        //
        // ground
        if (matPeg == null) {
            matPeg = new Material(msa.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
            matPeg.setBoolean("UseMaterialColors", true);
            matPeg.setColor("Ambient", ColorRGBA.White);
            matPeg.setColor("Diffuse", ColorRGBA.White);
            matPeg.setColor("Specular", ColorRGBA.White);
            matPeg.setTexture("DiffuseMap", msa.getAssetManager().loadTexture("Textures/texturePeg.png"));
            matPeg.setFloat("Shininess", 10f);
        }
        //
        Cylinder cyl = new Cylinder(5, 32, 0.12f, 0.3f, true);
        geomPeg = new Geometry("peg", cyl);
        geomPeg.setMaterial(matPeg);
        geomPeg.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        geomPeg.rotate(-FastMath.PI / 2.0f, 0, 0);
        setPosition(0,0);
    }

    // -------------------------------------------------------------------------
    protected void initPhysics(){

    }
    
    
    // -------------------------------------------------------------------------
    void setPosition(float posX, float posY) {
        geomPeg.setLocalTranslation(posX, 0.15f+0.1f, posY);
    }
}

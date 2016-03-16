package mygame;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Rolf
 */
public class TargetBin {

    Main msa;
    Material matTarget;
    Geometry geomBox;
    float posX, posZ;
    int hits;

    // -------------------------------------------------------------------------
    public TargetBin(Main msa, int id) {
        this.msa = msa;
        initTargetBin(id);
        initPhysics();
        
        
    }

    // -------------------------------------------------------------------------
    private void initTargetBin(int id) {
        //
        Material matBin = new Material(msa.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matBin.setColor("Color", new ColorRGBA(0.5f, 0.5f, 0f, 0.5f));
        matBin.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        //
        Box box = new Box(Pegboard.PEGDISTANCE / 2.1f, 0.2f, 0.05f);
        geomBox = new Geometry("bin_"+id, box);
        geomBox.setMaterial(matBin);
        geomBox.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        geomBox.setQueueBucket(Bucket.Transparent);
        
    }

    // -------------------------------------------------------------------------
    void setPosition(float posX, float posZ) {
        this.posX = posX;
        this.posZ = posZ;
        geomBox.updateModelBound();
        Vector3f extent = new Vector3f();
        ((BoundingBox)geomBox.getModelBound()).getExtent(extent);
        geomBox.setLocalTranslation(posX, extent.y+0.15f, posZ);
    }    
    
    // -------------------------------------------------------------------------
    protected void initPhysics(){
         
        GhostControl ghost;
       
        BoundingBox bb = (BoundingBox)geomBox.getModelBound();
        Vector3f boxSize = new Vector3f();
        bb.getExtent(boxSize);
        ghost = new GhostControl(new BoxCollisionShape(boxSize));
        geomBox.addControl(ghost);
        msa.bullet.getPhysicsSpace().add(ghost);
        ghost.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        ghost.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_01);
        
        
    }
    
    // -------------------------------------------------------------------------
    // this does NOT influence any attached physics objects!
    void rescale(float newSize) {
        geomBox.setLocalScale(1,newSize+1f,1); 
        geomBox.updateModelBound();
    }  
}

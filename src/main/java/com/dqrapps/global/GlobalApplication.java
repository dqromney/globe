package com.dqrapps.global;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.media.j3d.*;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.xml.crypto.dsig.Transform;
import java.awt.*;
import java.util.Map;

@SpringBootApplication
public class GlobalApplication implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(GlobalApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(GlobalApplication.class, args);
    }

    @Autowired
    ApplicationContext applicationContext;

    private SimpleUniverse universe = null;
    private Canvas3D canvas = null;
    private TransformGroup viewTrans = null;

    // https://www.youtube.com/watch?v=bAA6NRW3D4s&feature=emb_rel_pause
    @Override
    public void run(String... args) {
//        Map<String, Object> beansOfType = applicationContext.getBeansOfType(Object.class);
//        beansOfType.forEach((s, o) -> {
//            logger.info("{} - {}", s, o.getClass());
//        });
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        canvas = new Canvas3D(config);
        add("Center", canvas);
        universe = new SimpleUniverse(canvas);

        Background bg = new Background(bgTexture.getImage());
        bg.setApplicationBounds(bounds);
        objRoot.addChild(bg);

        objRoot.addChild(createGround());
        objRoot.addChild(createWindFarm());
        objRoot.addChild(createTexturedSpheres());
    }

    public BranchGroup createTexturedSpheres() {
        BranchGroup objRoot = new BranchGroup();
        TransformGroup tg = new TransformGroup();
        Transform3D t3d = new Transform3D();
        t3d.setTranslation(new Vector3f(3.0f, 0.0f, 45.0f));
        tg.setTransform(t3d);

        Appearance app = new Appearance();

        String filename = "SilverGlobe.jpg";

        TextureLoader loader = new TextureLoader(filename, null);
        ImageComponent2D image = loader.getImage();

        Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
        texture.setImage(0, image);
        app.setTexture(texture);

        Material material = new Material();
        app.setMaterial(material);
        TextureAttributes textAttr = new TextureAttributes();
        textAttr.setTextureMode(TextureAttributes.MODULATE);

        int flag = Sphere.GENERATE_NORMALS | Sphere.GENERATE_TEXTURE_COORDS | Sphere.ENABLE_GEOMETRY_PICKING;
        Sphere sphere = new Sphere(0.22f, flag, 30, app);

        SharedGroup shared = new SharedGroup();
        shared.addChild(sphere);

        TransformGroup tg_link = null;
        Transform3D t3d_link = new Transform3D();

        for(float x  = -30.0f; x <= 30.0f; x += 1.5f) {
            for (float z = -0.45f; x <= 0.45f; z += 0.15f) {
                t3d_link.setScale(5.0);
                t3d_link.setTranslation(new Vector3d(x *10 - 2, -0.80, -z * 400));

                tg_link = new TransformGroup(t3d_link);
                tg_link.addChild(new Link(shared));
                tg_link.addChild(tg_link);
            }
        }

        objRoot.addChild(tg);
        objRoot.compile();
        return objRoot;
    }
}

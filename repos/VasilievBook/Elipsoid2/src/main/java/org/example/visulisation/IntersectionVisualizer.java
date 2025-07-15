package org.example.visulisation;
//import geometry.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import org.example.geometry.Ellipsoid;
import org.example.geometry.Plane;

public class IntersectionVisualizer extends Group{
    private final Ellipsoid ellipsoid;
    private final Plane plane;
    private final Sphere sphere;

    public IntersectionVisualizer(double radiusX, double radiusY, double radiusZ,
                                  double planeD) {
        this.ellipsoid = new Ellipsoid(radiusX, radiusY, radiusZ);
        this.plane = new Plane(0, 0, 1, planeD);

        // Create ellipsoid visualization
        this.sphere = new Sphere(1.0);
        sphere.setMaterial(new PhongMaterial(Color.BLUE));
        sphere.getTransforms().add(new Scale(radiusX, radiusY, radiusZ));

        // Create plane visualization
        Rectangle3D planeVisual = new Rectangle3D(
                4.0, 4.0, planeD, Color.rgb(255, 0, 0, 0.3));

        // Create intersection curve
        Group intersectionCurve = new Group();
        ellipsoid.getIntersectionPoints(plane, 100).forEach(point -> {
            Sphere pt = new Sphere(0.05);
            pt.setTranslateX(point[0]);
            pt.setTranslateY(point[1]);
            pt.setTranslateZ(point[2]);
            pt.setMaterial(new PhongMaterial(Color.GREEN));
            intersectionCurve.getChildren().add(pt);
        });

        this.getChildren().addAll(sphere, planeVisual, intersectionCurve);
    }
}

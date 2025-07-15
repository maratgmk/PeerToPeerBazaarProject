package org.example.visulisation;

import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
public class Rectangle3D extends Group {
    public Rectangle3D(double width, double height, double zPos, Paint color) {
        TriangleMesh mesh = new TriangleMesh();

        float w = (float)width/2;
        float h = (float)height/2;

        mesh.getPoints().addAll(
                -w, -h, 0,
                w, -h, 0,
                w,  h, 0,
                -w,  h, 0
        );

        mesh.getTexCoords().addAll(0, 0, 1, 0, 1, 1, 0, 1);
        mesh.getFaces().addAll(0, 0, 1, 1, 2, 2, 0, 0, 2, 2, 3, 3);

        MeshView meshView = new MeshView(mesh);
        meshView.setMaterial(new PhongMaterial((Color) color));
        meshView.setTranslateZ(zPos);
        this.getChildren().add(meshView);
    }

}

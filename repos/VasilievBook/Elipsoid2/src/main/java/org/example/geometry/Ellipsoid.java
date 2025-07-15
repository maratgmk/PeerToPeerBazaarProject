package org.example.geometry;

import java.util.ArrayList;
import java.util.List;

public class Ellipsoid {
    private final double radiusX;
    private final double radiusY;
    private final double radiusZ;

    public Ellipsoid(double radiusX, double radiusY, double radiusZ) {
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.radiusZ = radiusZ;
    }

    // Вычисление точек пересечения с плоскостью
    public List<double[]> getIntersectionPoints(Plane plane, int pointsCount) {
        List<double[]> points = new ArrayList<>();
        double d = plane.getD();
        double radiusFactor = Math.sqrt(1 - Math.pow(d / radiusZ, 2));

        if (Double.isNaN(radiusFactor)) {
            return points;
        }

        double effectiveA = radiusX * radiusFactor;
        double effectiveB = radiusY * radiusFactor;

        for (int i = 0; i < pointsCount; i++) {
            double angle = 2 * Math.PI * i / pointsCount;
            double x = effectiveA * Math.cos(angle);
            double y = effectiveB * Math.sin(angle);
            points.add(new double[]{x, y, d});
        }
        return points;
    }
}
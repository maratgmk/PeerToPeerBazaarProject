package org.example.geometry;

public class Plane {

    private final double a, b, c, d;

    public Plane(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    // Геттеры
    public double getA() { return a; }
    public double getB() { return b; }
    public double getC() { return c; }
    public double getD() { return d; }

    // Метод для проверки пересечения
    public boolean intersectsEllipsoid(double a, double b, double c) {
        double denominator = Math.sqrt(
                Math.pow(this.a/a, 2) +
                        Math.pow(this.b/b, 2) +
                        Math.pow(this.c/c, 2)
        );
        return Math.abs(this.d) <= denominator;
    }
}

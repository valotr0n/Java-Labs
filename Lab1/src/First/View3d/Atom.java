package First.View3d;

import javafx.geometry.Point3D;

public class Atom {
    final String element;
    final double x;
    final double y;
    final double z;
    public Atom(String element, double x, double y, double z) {
        this.element = element;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    Point3D point() {
        return new Point3D(x,y,z);
    }
}

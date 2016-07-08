package main;

import java.awt.Point;
import java.util.ArrayList;

import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGPolygon;
import com.sun.javafx.sg.prism.NGShape;

import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
/**
 * This class was originally made to compute area properties of a polygon. It has since been adapted for
 * drawing polygons on screen. Code was borrowed from the javafx.scene.shape.Polygon class to acomplish this.
 * @author Kyle Cochran
 *
 */
public class Polygon_old extends Shape{

	public double area;
	public double moiX;
	public double moiY;
	public double naXDist;
	public double naYDist;
	public ArrayList<Point> pts = new ArrayList<Point>();
	private final Path2D shape = new Path2D();
	
	public Polygon_old(){}
	
	public Polygon_old(ArrayList<Point> points) {
		this.pts = points;
	}
	
    public Polygon_old(double... points) {
        if (points != null) {
            for (double p : points) {
                this.getPoints().add(p);
            }
        }
    }
	
	public void add(Point p){
		this.pts.add(p);
	}
	
	public void compute(){
		this.area = findArea(pts);
		this.moiX = findMOIX(pts);
		this.moiY = findMOIY(pts);
	}
	
    private final ObservableList<Double> points = new TrackableObservableList<Double>() {
        @Override
        protected void onChanged(Change<Double> c) {
            impl_markDirty(DirtyBits.NODE_GEOMETRY);
            impl_geomChanged();
        }
    };
    
    public final ObservableList<Double> getPoints() { return points; }
    
    @Deprecated
    protected NGNode impl_createPeer() {
        return new NGPolygon();
    }

    /**
     * @treatAsPrivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public BaseBounds impl_computeGeomBounds(BaseBounds bounds, BaseTransform tx) {
        if (impl_mode == NGShape.Mode.EMPTY || getPoints().size() <= 1) {
            return bounds.makeEmpty();
        }

        if (getPoints().size() == 2) {
            if (impl_mode == NGShape.Mode.FILL || getStrokeType() == StrokeType.INSIDE) {
                return bounds.makeEmpty();
            }
            double upad = getStrokeWidth();
            if (getStrokeType() == StrokeType.CENTERED) {
                upad /= 2.0f;
            }
            return computeBounds(bounds, tx, upad, 0.5f,
                getPoints().get(0), getPoints().get(1), 0.0f, 0.0f);
        } else {
            return computeShapeBounds(bounds, tx, impl_configShape());
        }
    }

    /**
     * @treatAsPrivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    @Override
    public Path2D impl_configShape() {
        double p1 = getPoints().get(0);
        double p2 = getPoints().get(1);
        shape.reset();
        shape.moveTo((float)p1, (float)p2);
        final int numValidPoints = getPoints().size() & ~1;
        for (int i = 2; i < numValidPoints; i += 2) {
            p1 = getPoints().get(i); p2 = getPoints().get(i+1);
            shape.lineTo((float)p1, (float)p2);
        }
        shape.closePath();
        return shape;
    }

    /**
     * @treatAsPrivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public void impl_updatePeer() {
        super.impl_updatePeer();
        if (impl_isDirty(DirtyBits.NODE_GEOMETRY)) {
            final int numValidPoints = getPoints().size() & ~1;
            float points_array[] = new float[numValidPoints];
            for (int i = 0; i < numValidPoints; i++) {
                points_array[i] = (float)getPoints().get(i).doubleValue();
            }
            final NGPolygon peer = impl_getPeer();
            peer.updatePolygon(points_array);
        }
    }

    /**
     * Returns a string representation of this {@code Polygon} object.
     * @return a string representation of this {@code Polygon} object.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Polygon[");

        String id = getId();
        if (id != null) {
            sb.append("id=").append(id).append(", ");
        }

        sb.append("points=").append(getPoints());

        sb.append(", fill=").append(getFill());

        Paint stroke = getStroke();
        if (stroke != null) {
            sb.append(", stroke=").append(stroke);
            sb.append(", strokeWidth=").append(getStrokeWidth());
        }

        return sb.append("]").toString();
    }
	
	private double findArea(ArrayList<Point> pts){
		
		double area=0;

		for( int i = 0; i < pts.size()-1; i ++ ){
			area += pts.get(i).x*pts.get(i+1).y-pts.get(i).y*pts.get(i+1).x;
		}
		area +=pts.get(pts.size()-1).x*pts.get(0).y-pts.get(pts.size()-1).y*pts.get(0).x;
		area /= -2;

		return area;

	}
	
	private double findMOIX(ArrayList<Point> pts){
		double moi = 0;
		
		for(int i=0; i<pts.size()-2; i++){
			moi +=((pts.get(i).y)^2+(pts.get(i).y)*(pts.get(i+1).y)+(pts.get(i+1).y)^2)+((pts.get(i).x)*(pts.get(i+1).y)-(pts.get(i+1).x)*(pts.get(i).y));
					
		}
		return moi/12;
	}
	
	private double findMOIY(ArrayList<Point> pts){
		double moi = 0;
		
		for(int i=0; i<pts.size()-2; i++){
			moi +=((pts.get(i).x)^2+(pts.get(i).x)*(pts.get(i+1).x)+(pts.get(i+1).x)^2)+((pts.get(i).x)*(pts.get(i+1).y)-(pts.get(i+1).x)*(pts.get(i).y));		
		}
		
		return moi/12;
	}

	@Override
	public com.sun.javafx.geom.Shape impl_configShape() {
		// TODO Auto-generated method stub
		return null;
	}

}

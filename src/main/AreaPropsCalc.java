package main;

import javafx.scene.shape.Rectangle;

public class AreaPropsCalc {
	

	public double IntertiaCalc(Rectangle[] rectangleArray){
		int length = rectangleArray.length;
		double[] Yarray = new double [length];
		double Ytop = 0;
		double[] widtharray = new double [length];
		double[] heightarray = new double [length];
		double[] areaarray = new double [length];
		double NAnum = 0;
		double NAdenom = 0;
		double Inertia = 0;
		
		for(int i = 0; i <= (length-1); i++){
			Yarray[i] = rectangleArray[i].getY();
			
			if( Ytop < Yarray[i]){
				Ytop = Yarray[i];
			}
			
			heightarray[i] = rectangleArray[i].getHeight();
			widtharray[i] = rectangleArray[i].getWidth();
			areaarray[i] = (heightarray[i]*widtharray[i]);
		}
		
		for(int i = 0; i<= (length-1); i++){
			NAnum = NAnum + areaarray[i]*(Ytop-Yarray[i]+heightarray[i]/2);
			NAdenom = NAdenom +areaarray[i];
		}
		
		double YNeutralAxis = Ytop-NAnum/NAdenom;
		
		for(int i = 0; i<= (length-1); i++){
			double deltaY = YNeutralAxis - (Yarray[i]-heightarray[i]/2);
			Inertia = Inertia + (1/12)*areaarray[i]*(heightarray[i])*(heightarray[i]) + areaarray[i]*deltaY*deltaY;
		}
		
		return Inertia;
	}

}

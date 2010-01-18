package org.xmlcml.cml.tools;

public class BoundingBoxBundle {
	public BoundingBoxBundle() {
		
	}
	
	private String stroke = "red";
	private double strokeWidth = 2.0;
	private double opacity = 1.0;
	public String getStroke() {
		return stroke;
	}
	public void setStroke(String stroke) {
		this.stroke = stroke;
	}
	public double getStrokeWidth() {
		return strokeWidth;
	}
	public void setStrokeWidth(double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
	public double getOpacity() {
		return opacity;
	}
	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}
}

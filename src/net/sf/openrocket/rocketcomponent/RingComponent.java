package net.sf.openrocket.rocketcomponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.openrocket.util.Coordinate;
import net.sf.openrocket.util.MathUtil;


/**
 * An inner component that consists of a hollow cylindrical component.  This can be
 * an inner tube, tube coupler, centering ring, bulkhead etc.
 * 
 * The properties include the inner and outer radii, length and radial position.
 * 
 * @author Sampo Niskanen <sampo.niskanen@iki.fi>
 */
public abstract class RingComponent extends StructuralComponent {

	protected boolean outerRadiusAutomatic = false;
	protected boolean innerRadiusAutomatic = false;
	
	
	private double radialDirection = 0;
	private double radialPosition = 0;
	
	private double shiftY = 0;
	private double shiftZ = 0;
	

	

	public abstract double getOuterRadius();
	public abstract void setOuterRadius(double r);
	
	public abstract double getInnerRadius();	
	public abstract void setInnerRadius(double r);
	
	public abstract double getThickness();
	public abstract void setThickness(double thickness);
	
	
	public final boolean isOuterRadiusAutomatic() {
		return outerRadiusAutomatic;
	}
	
	protected void setOuterRadiusAutomatic(boolean auto) {
		if (auto == outerRadiusAutomatic)
			return;
		outerRadiusAutomatic = auto;
		fireComponentChangeEvent(ComponentChangeEvent.MASS_CHANGE);
	}
	
	
	public final boolean isInnerRadiusAutomatic() {
		return innerRadiusAutomatic;
	}
	
	protected void setInnerRadiusAutomatic(boolean auto) {
		if (auto == innerRadiusAutomatic)
			return;
		innerRadiusAutomatic = auto;
		fireComponentChangeEvent(ComponentChangeEvent.MASS_CHANGE);
	}
	
	
	
	
	public final void setLength(double length) {
		double l = Math.max(length,0);
		if (this.length == l)
			return;
		
		this.length = l;
		fireComponentChangeEvent(ComponentChangeEvent.MASS_CHANGE);
	}

	
	/**
	 * Return the radial direction of displacement of the component.  Direction 0
	 * is equivalent to the Y-direction.
	 * 
	 * @return  the radial direction.
	 */
	public double getRadialDirection() {
		return radialDirection;
	}
	
	/**
	 * Set the radial direction of displacement of the component.  Direction 0
	 * is equivalent to the Y-direction.
	 * 
	 * @param dir  the radial direction.
	 */
	public void setRadialDirection(double dir) {
		dir = MathUtil.reduce180(dir);
		if (radialDirection == dir)
			return;
		radialDirection = dir;
		shiftY = radialPosition * Math.cos(radialDirection);
		shiftZ = radialPosition * Math.sin(radialDirection);
		fireComponentChangeEvent(ComponentChangeEvent.MASS_CHANGE);
	}

	
	
	
	/**
	 * Return the radial position of the component.  The position is the distance
	 * of the center of the component from the center of the parent component.
	 * 
	 * @return  the radial position.
	 */
	public double getRadialPosition() {
		return radialPosition;
	}
	
	/**
	 * Set the radial position of the component.  The position is the distance
	 * of the center of the component from the center of the parent component.
	 * 
	 * @param pos  the radial position.
	 */
	public void setRadialPosition(double pos) {
		pos = Math.max(pos, 0);
		if (radialPosition == pos)
			return;
		radialPosition = pos;
		shiftY = radialPosition * Math.cos(radialDirection);
		shiftZ = radialPosition * Math.sin(radialDirection);
		fireComponentChangeEvent(ComponentChangeEvent.MASS_CHANGE);
	}



	/**
	 * Return the number of times the component is multiplied.
	 */
	public int getClusterCount() {
		if (this instanceof Clusterable)
			return ((Clusterable)this).getClusterConfiguration().getClusterCount();
		return 1;
	}
	
	
	/**
	 * Shift the coordinates according to the radial position and direction.
	 */
	@Override
	public Coordinate[] shiftCoordinates(Coordinate[] array) {
		for (int i=0; i < array.length; i++) {
			array[i] = array[i].add(0, shiftY, shiftZ);
		}
		return array;
	}
	
	
	@Override
	public Collection<Coordinate> getComponentBounds() {
		List<Coordinate> bounds = new ArrayList<Coordinate>();
		addBound(bounds,0,getOuterRadius());
		addBound(bounds,length,getOuterRadius());
		return bounds;
	}
	

	
	@Override
	public Coordinate getComponentCG() {
		return new Coordinate(length/2, 0, 0, getComponentMass());
	}

	@Override
	public double getComponentMass() {
		return ringMass(getOuterRadius(), getInnerRadius(), getLength(),
				getMaterial().getDensity()) * getClusterCount();
	}
	

	@Override
	public double getLongitudalUnitInertia() {
		return ringLongitudalUnitInertia(getOuterRadius(), getInnerRadius(), getLength());
	}

	@Override
	public double getRotationalUnitInertia() {
		return ringRotationalUnitInertia(getOuterRadius(), getInnerRadius());
	}

}
package net.sf.openrocket.material;

import net.sf.openrocket.unit.Unit;
import net.sf.openrocket.unit.UnitGroup;
import net.sf.openrocket.util.MathUtil;

/**
 * A class for different material types.  Each material has a name and density.
 * The interpretation of the density depends on the material type.  For
 * {@link Type#BULK} it is kg/m^3, for {@link Type#SURFACE} km/m^2.
 * <p>
 * Objects of this type are immutable.
 * 
 * @author Sampo Niskanen <sampo.niskanen@iki.fi>
 */

public abstract class Material implements Comparable<Material> {

	public enum Type {
		LINE,
		SURFACE,
		BULK
	}
	
	public static class Line extends Material {
		public Line(String name, double density) {
			super(name, density);
		}

		@Override
		public UnitGroup getUnitGroup() {
			return UnitGroup.UNITS_DENSITY_LINE;
		}

		@Override
		public Type getType() {
			return Type.LINE;
		}
	}
	
	public static class Surface extends Material {
		
		public Surface(String name, double density) {
			super(name, density);
		}
		
		@Override
		public UnitGroup getUnitGroup() {
			return UnitGroup.UNITS_DENSITY_SURFACE;
		}

		@Override
		public Type getType() {
			return Type.SURFACE;
		}
		
		@Override
		public String toStorableString() {
			return super.toStorableString();
		}
	}
	
	public static class Bulk extends Material {
		public Bulk(String name, double density) {
			super(name, density);
		}

		@Override
		public UnitGroup getUnitGroup() {
			return UnitGroup.UNITS_DENSITY_BULK;
		}

		@Override
		public Type getType() {
			return Type.BULK;
		}
	}
	
	
	
	private final String name;
	private final double density;
	
	
	public Material(String name, double density) {
		this.name = name;
		this.density = density;
	}
	
	
	
	public double getDensity() {
		return density;
	}
	
	public String getName() {
		return name;
	}
	
	public String getName(Unit u) {
		return name + " (" + u.toStringUnit(density) + ")";
	}
	
	public abstract UnitGroup getUnitGroup();
	public abstract Type getType();
	
	@Override
	public String toString() {
		return getName(getUnitGroup().getDefaultUnit());
	}
	

	/**
	 * Compares this object to another object.  Material objects are equal if and only if
	 * their types, names and densities are identical.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (this.getClass() != o.getClass())
			return false;
		Material m = (Material)o;
		return ((m.name.equals(this.name)) && 
				MathUtil.equals(m.density, this.density)); 
	}


	/**
	 * A hashCode() method giving a hash code compatible with the equals() method.
	 */
	@Override
	public int hashCode() {
		return name.hashCode() + (int)(density*1000);
	}

	
	/**
	 * Order the materials according to their name, secondarily according to density.
	 */
	public int compareTo(Material o) {
		int c = this.name.compareTo(o.name);
		if (c != 0) {
			return c;
		} else {
			return (int)((this.density - o.density)*1000);
		}
	}
	
	
	
	public static Material newMaterial(Type type, String name, double density) {
		switch (type) {
		case LINE:
			return new Material.Line(name, density);
			
		case SURFACE:
			return new Material.Surface(name, density);
			
		case BULK:
			return new Material.Bulk(name, density);
			
		default:
			throw new IllegalArgumentException("Unknown material type: "+type);
		}
	}
	
	
	public String toStorableString() {
		return getType().name() + "|" + name.replace('|', ' ') + '|' + density;
	}
	
	public static Material fromStorableString(String str) {
		String[] split = str.split("\\|",3);
		if (split.length < 3)
			throw new IllegalArgumentException("Illegal material string: "+str);

		Type type = null;
		String name;
		double density;
		
		try {
			type = Type.valueOf(split[0]);
		} catch (Exception e) {
			throw new IllegalArgumentException("Illegal material string: "+str, e);
		}

		name = split[1];
		
		try {
			density = Double.parseDouble(split[2]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Illegal material string: "+str, e);
		}
		
		switch (type) {
		case BULK:
			return new Material.Bulk(name, density);
			
		case SURFACE:
			return new Material.Surface(name, density);
			
		case LINE:
			return new Material.Line(name, density);
			
		default:
			throw new IllegalArgumentException("Illegal material string: "+str);
		}
	}

}
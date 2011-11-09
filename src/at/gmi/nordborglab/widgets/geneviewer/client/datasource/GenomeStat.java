package at.gmi.nordborglab.widgets.geneviewer.client.datasource;

import java.util.List;

public class GenomeStat {
	
	private final String name;
	private final String label;
	private final String color;
	private final boolean isStepPlot;
	private final boolean isStackable;
	
	public GenomeStat(String name,String color,boolean isStackable,boolean isStepPlot,String label) {
		this.name = name;
		this.color = color;
		this.isStackable = isStackable;
		this.isStepPlot = isStepPlot;
		this.label = label;
	}
	
	public String  getName() {
		return name;
	}
	
	public String getLabel() {
		if (label == null || label.isEmpty())
			return name;
		return label;
	}
	
	public String  getColor() {
		return color;
	}
	
	public boolean isStackable() {
		return isStackable;
	}
	
	public boolean isStepPlot() {
		return isStepPlot;
	}
	
	public static GenomeStat getFromName(List<GenomeStat> stats,String name) {
		for (GenomeStat stat: stats) {
			if (stat != null && stat.getName().equals(name)){
				return stat;
			}
		}
		return null;
	}
}

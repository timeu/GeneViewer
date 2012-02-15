package at.gmi.nordborglab.widgets.geneviewer.client.datasource;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

public class GenomeStat extends JavaScriptObject{
	
	
	protected GenomeStat() {}
	
	public final native String getName() /*-{
		return this.name;
	}-*/;
	
	public final String getDisplayName() {
		String label = getLabel();
		if (label == null || label.isEmpty())
			return getName();
		return label;
	}
	
	public final native String getLabel() /*-{
		if (typeof this.label != 'undefined') {
			return this.label;
		}
		return null;
	}-*/;
	
	
	
	public final native String getColor() /*-{
		return this.color;
	}-*/;
	
	public final native boolean isStackable() /*-{
		if (typeof this.isStackable != 'undefined') {
			return this.isStackable;
		}
		return true;
	}-*/;
	
	public final native boolean isStepPlot() /*-{
		if (typeof this.isStepPlot != 'undefined') {
			return this.isStepPlot;
		}
		return false;
	}-*/;
	
	public static GenomeStat getFromName(List<GenomeStat> stats,String name) {
		for (GenomeStat stat: stats) {
			if (stat != null && stat.getName().equals(name)){
				return stat;
			}
		}
		return null;
	}
}

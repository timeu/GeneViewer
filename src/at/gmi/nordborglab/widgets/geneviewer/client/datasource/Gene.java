package at.gmi.nordborglab.widgets.geneviewer.client.datasource;

public class Gene {
	
	protected String name;
	protected String chromosome;
	protected int start;
	protected int end;
	protected String description;
	
	public Gene(String name,String chromosome,int start,int end,String description) {
		this.name = name;
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.description = "";
	}
	
	public String getName() {
		return name;
	}
	
	public String getChromosome() {
		return chromosome;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}

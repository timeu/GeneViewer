package at.gmi.nordborglab.widgets.geneviewer.client;

public class GeneViewerMouseMoveData {

	protected int position;
	protected String gene;
	
	public GeneViewerMouseMoveData(int position,String gene) {
		this.position = position;
	   	this.gene = gene;
	}
	
	public int getPosition() {
		return position;
	}
	
	public String getGene() {
		return gene;
	}
}

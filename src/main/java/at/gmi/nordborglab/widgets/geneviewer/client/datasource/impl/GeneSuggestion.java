package at.gmi.nordborglab.widgets.geneviewer.client.datasource.impl;

import at.gmi.nordborglab.widgets.geneviewer.client.datasource.Gene;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;

public class GeneSuggestion extends MultiWordSuggestion {
	
	protected Gene gene;
	public GeneSuggestion(Gene gene) {
		super(gene.getName(),gene.getName());
		this.gene = gene;
	}
	public Gene getGene() {
		return gene;
	}
}

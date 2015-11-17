package com.github.timeu.gwtlibs.geneviewer.client;

import com.github.timeu.gwtlibs.geneviewer.client.event.Gene;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by uemit.seren on 9/22/15.
 */
@RunWith(GwtMockitoTestRunner.class)
public class GeneInfoPopupTest {

    GeneInfoPopup sut;

    @Before
    public void setUp() {
        sut = new GeneInfoPopup();
    }

    @Test
    public void testSetUiValuesWhenGeneIsSet() {
        sut.setGene(getGene());
        verify(sut.infoLb).setText("Test");
        verify(sut.nameLb).setInnerText("AT1G10000");
        verify(sut.positionLb).setText("1000 - 2000 (Chr1)");
    }

    @Test
    public void testSetGeneInfo() {
        sut.setGeneInfo("new info");
        verify(sut.infoLb).setText("new info");
    }

    private Gene getGene() {
        Gene gene = Gene.create("AT1G10000","Chr1",1000,2000,"Test");
        return gene;
    }
}

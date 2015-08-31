package com.github.timeu.gwtlibs.geneviewer.client;

import com.github.timeu.gwtlibs.geneviewer.client.event.ClickGeneEvent;
import com.github.timeu.gwtlibs.geneviewer.client.event.FetchGeneEvent;
import com.github.timeu.gwtlibs.geneviewer.client.event.HighlightGeneEvent;
import com.github.timeu.gwtlibs.geneviewer.client.event.MouseMoveEvent;
import com.github.timeu.gwtlibs.geneviewer.client.event.UnhighlightGeneEvent;
import com.github.timeu.gwtlibs.geneviewer.client.event.ZoomResizeEvent;
import com.github.timeu.gwtlibs.processingjsgwt.client.Processing;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.testing.StubScheduler;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by uemit.seren on 8/28/15.
 */
@RunWith(GwtMockitoTestRunner.class)
public class GeneViewerTest {
    GeneViewer geneViewer;
    @Mock
    Processing<GeneViewerInstance> processing;
    @Mock GeneViewerInstance instance;
    @Mock
    JavaScriptObject jso;
    StubScheduler scheduler = new StubScheduler();
    @GwtMock
    Element element;


    @Before
    public void setUp() {
        given(processing.getInstance()).willReturn(instance);
        given(processing.isLoaded()).willReturn(true);
        geneViewer = new GeneViewer(processing,scheduler);
    }

    @Test
    public void testLoadAndCallOnLoad() throws ResourceException {
        Runnable onLoad = mock(Runnable.class);
        doAnswer(invocationOnMock -> {
            onLoad.run();
            return null;
        }).when(processing).load(Matchers.<ExternalTextResource>anyObject(), any(Runnable.class));
        geneViewer.load(onLoad);
        verify(onLoad).run();
    }

    @Test
    public void testSetData() throws ResourceException {
        JsArrayMixed data = getFakeData();
        geneViewer.setGeneData(data);
        verify(instance).api_setGeneData(data);
    }


    @Test
    public void testOnResize() {
        // Required otherwise not called
        GeneViewer spy = spy(geneViewer);
        given(spy.isAttached()).willReturn(true);
        spy.onResize();
        assertFalse(scheduler.executeCommands());
    }
    @Test
    public void testOnResizeWhenAttached() {
        // Required otherwise not called
        GeneViewer spy = spy(geneViewer);
        given(spy.isAttached()).willReturn(true);
        spy.onAttach();
        assertFalse(scheduler.executeCommands());
    }

    @Test
    public void testForceLayout() {
        GeneViewer spy = spy(geneViewer);
        given(spy.isAttached()).willReturn(true);
        given(spy.getElement()).willReturn(element);
        given(spy.isVisible()).willReturn(true);
        given(element.getParentElement()).willReturn(element);
        given(element.getClientWidth()).willReturn(1000);
        given(element.getClientHeight()).willReturn(500);
        spy.forceLayout();
        verify(instance).api_setSize(1000, 500);
    }

    @Test
    public void testSetViewRegion() {
        geneViewer.setViewRegion(400, 1700);
        verify(instance).api_setViewRegion(400, 1700);
    }

    @Test
    public void testSetViewRegionNoOpWhenSame() {
        geneViewer.setLength(200);
        geneViewer.setViewRegion(0, 200);
        verify(instance,never()).api_setViewRegion(anyInt(), anyInt());
    }

    @Test
    public void testUpdateZoom() {
        geneViewer.updateZoom(400, 1700);
        verify(instance).api_updateZoom(400, 1700);
    }

    @Test
    public void testResetZoom() {
        geneViewer.setViewRegion(400,1700);
        geneViewer.resetZoom();
        verify(instance).api_updateZoom(400, 1700);
    }

    @Test
    public void testUpdateZoomNoOpWhenSame() {
        given(instance.api_getZoomStart()).willReturn(400);
        given(instance.api_getZoomEnd()).willReturn(1700);
        geneViewer.updateZoom(400, 1700);
        verify(instance,never()).api_setViewRegion(anyInt(), anyInt());
    }

    @Test
    public void testRedraw() {
        geneViewer.redraw(true);
        verify(instance).api_redraw(true);
    }

    @Test
    public void testSetRangeSelectorPosition() {
        geneViewer.setRangeSelectorPosition(GeneViewer.SHOW_RANGE_SELECTOR.Bottom);
        verify(instance).api_setShowRangeSelector(GeneViewer.SHOW_RANGE_SELECTOR.Bottom.ordinal());
    }

    @Test
    public void testSetSelectionLine() {
        geneViewer.setSelectionLine(200);
        verify(instance).api_setSelectionLine(200);
    }

    @Test
    public void testHideSelectionLine() {
        geneViewer.hideSelectionLine();
        verify(instance).api_hideSelectionLine();
    }


    @Test
    public void testAddMouseMoveHandler() {
        MouseMoveEvent.Handler handler = mock(MouseMoveEvent.Handler.class);
        HandlerRegistration registration = geneViewer.addMouseMoveHandler(handler);
        verifyEventHandler("mouseMoveEvent", registration);
    }

    @Test
    public void testZoomResizeHandler() {
        ZoomResizeEvent.Handler handler = mock(ZoomResizeEvent.Handler.class);
        HandlerRegistration registration = geneViewer.addZoomResizeHandler(handler);
        verifyEventHandler("zoomResizeEvent", registration);
    }

    @Test
    public void testAddFetchGeneEventHandler() {
        FetchGeneEvent.Handler handler = mock(FetchGeneEvent.Handler.class);
        HandlerRegistration registration = geneViewer.addFetchGeneHandler(handler);
        verifyEventHandler("fetchGeneEvent",registration);
    }

    @Test
    public void testAddHighlightGeneEventHandler() {
        HighlightGeneEvent.Handler handler = mock(HighlightGeneEvent.Handler.class);
        HandlerRegistration registration = geneViewer.addHighlightGeneHandler(handler);
        verifyEventHandler("highlightGeneEvent",registration);
    }

    @Test
    public void testAddUnhighlightGeneEventHandler() {
        UnhighlightGeneEvent.Handler handler = mock(UnhighlightGeneEvent.Handler.class);
        HandlerRegistration registration = geneViewer.addUnhighlightGeneHandlers(handler);
        verifyEventHandler("unhighlightGeneEvent",registration);
    }

    @Test
    public void testAddClickGeneEventHandler() {
        ClickGeneEvent.Handler handler = mock(ClickGeneEvent.Handler.class);
        HandlerRegistration registration = geneViewer.addClickGeneHandler(handler);
        verifyEventHandler("clickGeneEvent",registration);
    }


    private void verifyEventHandler(String handler,HandlerRegistration registration) {
        verify(instance).api_addEventHandler(eq(handler), any());
        assertNotNull(registration);
    }

    private JsArrayMixed getFakeData() {
        JsArrayMixed data = mock(JsArrayMixed.class);
        return data;
    }
}

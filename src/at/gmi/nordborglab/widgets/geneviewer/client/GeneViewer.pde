
int fps = 1;
String data_raw = null;
GenomeBrowser browser = null;
Layout layout = null;

var eventHandler = {};
 
void setup()
{
   //size(1000,200);
   frameRate(fps);
   background(255)
   smooth();
   layout = new Layout(width,height);
   browser = new GenomeBrowser("Chr1");
   noLoop();
}


void draw()
{
    background(255)
    browser.render();     
}


// API for access via javascript

void api_addEventHandler(String handler,callback)
{
    eventHandler[handler] = callback;
}


void api_clearEventHandlers()
{
   eventHandler = {};
}

void api_setLength(int length)
{
  if (length > 0)
  	layout.setLength(length);
  	draw();
}
 

int api_getLength()
{
    return layout.length;
}

int api_getZoomStart(){
    return layout.zoomStart;
}

int api_getZoomEnd() {
    return layout.zoomEnd;
}

int api_getMaximumZoomToDraw() {
    return layout.maximumZoomToDraw;
}

void api_setMaximumZoomToDraw(int maximumZoomToDraw) {
    layout.maximumZoomToDraw = maximumZoomToDraw;
}

int api_getMaximumZoomToDrawFeatures() {
    return layout.maximumZoomToDrawFeatures;
}

void api_setMaximumZoomToDrawFeatures(int maximumZoomToDrawFeatures) {
    layout.maximumZoomToDrawFeatures = maximumZoomToDrawFeatures;
}

void api_updateZoom(int start, int end)
{
    layout.updateZoom(start,end);
    draw();
}

void api_setSelectionLine(int position)
{
     layout.mousePositionX  = layout.bpToPx(position - layout.zoomStart) + layout.area.x;
     draw();
}

void api_hideSelectionLine()
{
    api_setSelectionLinePx(-1);
    draw();
}

void api_setSelectionLinePx(int position_px)
{
     layout.mousePositionX = position_px;
}

void api_setGeneData(geneData)
{
    if (layout.maximumZoomToDraw == -1 || layout.getZoomLength() <= layout.maximumZoomToDraw )
    {
        ArrayList genes = new ArrayList();   
        for (int i =0;i<geneData.length;i++)
        {
        	ArrayList geneInfo = geneData[i];
        	ArrayList cds = new ArrayList();
        	ArrayList utrs = new ArrayList();
        	for (int j = 0;j<geneInfo[4].length;j++)
        	{
        		ArrayList featureInfo = geneInfo[4][j];
        		if (featureInfo[3] == "CDS")
        			cds.add(new CDS(featureInfo[0],featureInfo[1]));
        		else if (featureInfo[3] == "five_prime_UTR")
        		    utrs.add(new UTR5(featureInfo[0],featureInfo[1]));
        		else if (featureInfo[3] == "three_prime_UTR")
        		    utrs.add(new UTR3(featureInfo[0],featureInfo[1]));
        	}
        	String strand = "+";
        	if (geneInfo[2] == -1) 
        		strand = "-";
        	Gene gene = new Gene(geneInfo[3],geneInfo[3],"TestGene",strand,geneInfo[0],geneInfo[1],cds,utrs);
        	genes.add(gene);
        }
    	browser.setGenes(genes);
    	draw();
    }
}


// Classes


class DrawArea
{
   int x;
   int y;
   int width;
   int height;
  
   
   DrawArea(x,y,width,height)
   {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }
   
   int getEndPosX()
   {
      return x + width;
   } 
   int getEndPosY()
   {
      return y + height;
   }
}


// Responsible for the layout
class Layout
{
   // Width of browser in px
   int width;
   // Height of browser in px
   int height;
   // start of zoom area in bp
   int zoomStart = 0;
   // end of zoom area in bp
   int zoomEnd = 300000;
   
   // length of genome browser in bp
   int length = 300000;
   
   //int marginLeft = 26;
   //int marginRight = 5;
   int marginLeft = 0;
   int marginRight = 0;
   int marginTop = 0;
   int marginBottom = 0;
   
   // default Height of genes and cds in px
   int cdsHeight = 12;
   int utrHeight = 7;
   int geneLineWeight = 4;
      
   // mosuePositions
   float mousePositionX = -1;
   float mousePositionY = -1;
   
   // which mouse Button is Pressed
   int mouseButtonPressed = -1;
   int mouseButtonPressedX = -1;
   
   int labelScaleSize = 12;

   DrawArea area = null;
   
   float pxPerBp=1; 

   //where to display scale (0 = no display, 1 = bottom, 2 = top)
   int showScale = 1;

   // how many vertical grid lines should be drawn
   int gridLineCount = 5;
   
   int maximumZoomToDraw = 1500000;
   
   int maximumZoomToDrawFeatures = 100000;
   
   
   
   int laneCount = 0;
   int laneSize = 0;
   int activeLane = 0;
   int laneMarginTop = 20;
   PFont font = new PFont("Arial");
   
   int [] lanes = null;
   
   
   
   Layout(int width,int height)
   {
       zoomEnd = length;
       this.width = width;
       this.height = height;
       initArea();
       initPxPerBb();
       initLanes();
   }
   
   void initLanes()
   {
       laneSize = cdsHeight*2;
   	   laneCount = floor((this.area.height-laneMarginTop) / laneSize);
   	   lanes = new int[laneCount];
   	   resetLanes();
   }
   
   void resetLanes()
   {
   	   for (int i =0;i<laneCount;i++)
   	   {
   	   		lanes[i] = -1;
   	   }
   }
   
   void initArea()
   {
        this.area = new DrawArea(marginLeft,marginTop,(width - marginLeft - marginRight),(height - marginTop - marginBottom));
   }
   
   void setLength(length)
   {
       mustUpdateZoom = false;
       if ((zoomStart == 0 && zoomEnd == this.length) || this.length < length)
          mustUpdateZoom = true;
       this.length = length;
       if (mustUpdateZoom)
           zoomEnd = length;
       initPxPerBb();
   }

   void initPxPerBb()
   {
       this.pxPerBp  = (float)this.area.width / (this.zoomEnd - this.zoomStart);
   }
   
   void getFreeLane(x)  {
       for (int i = 0;i<laneCount;i++)
       {
       		
       		if (lanes[i] < x)
       			return i;
       }
       return 0;
   }
   
   PVector getPixelPosition(int start, int end) {
   	  start = (start - this.zoomStart);
      if (start < 0)
      	  start = 0;
      
      //calculate end;	  
      end = (end - zoomStart);
      if (end > zoomEnd)
      	   end = zoomEnd;
	   PVector vector = new PVector(round(layout.bpToPx(start)) + area.x,round(layout.bpToPx(end)) + area.x);
	   return vector;
   }
   
   int getLanePosY(int lane)
   {
   		return area.y + laneMarginTop + (lane*laneSize);
   }
   

   PVector[] getPositions(int start, int end,String type,int lane)
   {
      float startY;
      float endY;
      int y_pos = getLanePosY(lane);
      
      // calculate start 
      int startX = (start - this.zoomStart);
      if (startX < 0)
      	  startX = 0;
      
      //calculate end;	  
      int endX = (end - zoomStart);
      if (endX > zoomEnd)
      	   endX = zoomEnd;
      
      int startX_px = startX*pxPerBp + area.x;
      int endX_px = endX*pxPerBp + area.x;
      PVector [] positions = new PVector[2];
      if (type =="line")
      {
        startY = y_pos  ;
        endY = y_pos ;
      }
      else if (type == "utr")
      {
          startY = y_pos-(utrHeight/2);
          endY = y_pos + (utrHeight/2);
      }
      else if (type == "text")
      {
          startY = y_pos + utrHeight;
          endY = startY;
      }
      else 
      {
          startY = y_pos - (cdsHeight/2);
          endY = y_pos + (cdsHeight/2);
       }
      positions[0] = new PVector(round(startX_px),startY);
      positions[1] = new PVector(round(endX_px),endY);
      return positions;
   }
   
   PVector[] getStrandArrowPositions(int pos,String strand, int lane)
   {
       int y_pos = getLanePosY(lane);
       PVector [] positions = new PVector[3];
       float x_tip;
       if (strand == "+")
          x_tip = bpToPx(pos-zoomStart) + area.x + cdsHeight;
       else
          x_tip = bpToPx(pos-zoomStart) + area.x - cdsHeight;
       positions[0] = new PVector(round(bpToPx(pos-zoomStart))+area.x,y_pos - (cdsHeight/2));
       positions[1] = new PVector(round(bpToPx(pos-zoomStart))+area.x,y_pos + (cdsHeight/2));
       positions[3] = new PVector(round(x_tip),y_pos);
       return positions;   
   }

   int getGridLineBpSeperation()
   {
       return (int)(zoomEnd - zoomStart)/gridLineCount;
   }


   float pxToBp(float pixel)
   {
      return (float) pixel / pxPerBp;
   }

   float bpToPx(int bp)
   {
      return pxPerBp * bp;
   }
   
   int getGenomePosition(int position_px )
   {
   		return round(pxToBp(position_px-area.x) + zoomStart);
   }
   
   int getGenomePositionFromMouse()
   {
       return getGenomePosition(mousePositionX);
   }
   
   void updateZoom(int start,int end)
   {
      if (end < start)
      {
         end_tmp = end;
         end = start;
         start = end_tmp;
      }
      zoomStart = start;
      zoomEnd = end;
      initPxPerBb();
      fetchGenes();
   }
   
   void fetchGenes()
   {
      
      if (maximumZoomToDraw == -1 || getZoomLength() <= maximumZoomToDraw)
      {
          if (eventHandler['fetchGeneEvent'] != null)
              eventHandler['fetchGeneEvent'](zoomStart,zoomEnd);
      }
   }
   
   void calculateZoom()
   {
      int start_px = mouseButtonPressedX - area.x;
      int end_px = mouseX - area.x;
      if (mouseButtonPressedX > mouseX)
      {
          start_px = mouseX- area.x;
          end_px = mouseButtonPressedX - area.x;
      }
      if (start_px < 0) 
         start_px = 0;
      if (end_px > area.getEndPosX())
          end_px = area.getEndPosX();
      
      newZoomEnd = round(pxToBp(end_px)) + zoomStart;
      newZoomStart = round(pxToBp(start_px)) + zoomStart;
      updateZoom(newZoomStart,newZoomEnd);
   }
   
   void moveZoomWindow(int direction)
   {
      int moveRatio_px = round(width*0.05);
      int moveRatio = round(pxToBp(moveRatio_px));

      if (direction > 0) 
         moveRatio = 0 - moveRatio;
         
      /*println("Move Zoom Window");
       println("Ratio:"+moveRatio);
      println("zoomstart:"+zoomStart);
      println("zoomend:"+zoomEnd);
      println("length:"+length);*/
      if (zoomStart + moveRatio < 0)
         moveRatio = -1*zoomStart;
      if (zoomEnd + moveRatio > length)
         moveRatio = length - zoomEnd;
      if ((zoomStart + moveRatio) >= 0 && (moveRatio + zoomStart <= length))
      {
        zoomStart = zoomStart + moveRatio;
        zoomEnd = zoomEnd + moveRatio;
      }
      fetchGenes();
   }
   
   void convertToReadableBp(bp)
   {
   		String bp_converted = "";
        if (bp > 1000000)
           bp_converted = round(bp/1000000*1000)/1000 + " Mb";
        else if (bp > 1000)
           bp_converted = round(bp/1000*10)/10 + " Kb";
        else 
           bp_converted = bp +" b";
       return bp_converted;
   }
   
   void resetZoom()
   {
      zoomStart = 0;
      zoomEnd = length;
      initPxPerBb();
      browser.setGenes(new ArrayList());
      fetchGenes();
   }
   int getZoomLength()
   {
       return zoomEnd - zoomStart;
   }
}



class GenomeBrowser
{
   String name;
   ArrayList genes = null; 
   
   GenomeBrowser(String name)
   {
       this.name = name;
   }

   void render()
   {
        renderControls();
        if (genes != null)
        {
            for (int i = 0;i<genes.size();i++)
            {
                Gene gene = genes.get(i);
                gene.render();
            }
        }
        renderZoomArea();
        renderSelectionLine();
   }
   
   
   void renderZoomArea()
   {
       if(layout.mouseButtonPressed ==LEFT)
       {
           fill(100,30);
           rectMode(CORNERS);
           int startPosition = layout.mouseButtonPressedX;
           if (startPosition < layout.area.x)
               startPosition = layout.area.x;
           else if (startPosition > layout.area.getEndPosX())
               startPosition = layout.area.getEndPosX();
           int endPosition = layout.mousePositionX;
           if (endPosition  < layout.area.x)
               endPosition = layout.area.x;
           else if (endPosition > layout.area.getEndPosX())
               endPosition = layout.area.getEndPosX();
           rect(startPosition,layout.area.y+10,endPosition,layout.area.getEndPosY()-layout.labelScaleSize);
           fill(100);
           String Position = round(layout.pxToBp(startPosition-layout.area.x) + layout.zoomStart);
           String zoomWidth = layout.convertToReadableBp(Math.abs(round(layout.pxToBp(startPosition-layout.area.x) - layout.pxToBp(endPosition))));
           if ((layout.mouseButtonPressedX - layout.area.x - textWidth(Position)) >0)
              textAlign(RIGHT,BASELINE);
           else 
              textAlign(LEFT,BASELINE);
           text(Position,layout.mouseButtonPressedX,10);
           text(zoomWidth,startPosition + Math.abs((endPosition-startPosition)/2)+textWidth(zoomWidth)/2,layout.area.getEndPosY()-layout.labelScaleSize);
       }
   }
   
   void renderSelectionLine()
   {
      if (layout.mousePositionX != -1 && layout.mouseButtonPressed != RIGHT)
      {

          if (layout.mousePositionX < layout.area.x)
             layout.mousePositionX = layout.area.x;
          else if (layout.mousePositionX > layout.area.getEndPosX())
             layout.mousePositionX = layout.area.getEndPosX();
             
          stroke(0, 126, 255);
          line(layout.mousePositionX+0.5,layout.area.y,layout.mousePositionX+0.5,layout.area.height);
          fill(0, 126, 255);
          String Position =  layout.getGenomePositionFromMouse();
          if ((layout.mousePositionX + textWidth(Position) < layout.area.getEndPosX()))
              textAlign(LEFT,TOP);
          else
              textAlign(RIGHT,TOP);
          text(Position,layout.mousePositionX+0.5,0);
      }
   }

   void renderControls()
   {
        //render scale
        int scaleLineY = 0;
        int scaleTextY = 0;
        int gridLineSeperation = layout.getGridLineBpSeperation();
        float gridLineSeperationPx = layout.bpToPx(gridLineSeperation);
        if (layout.showScale == 1)
        {
           scaleTextY = layout.area.getEndPosY();
           scaleLineY = scaleTextY - layout.labelScaleSize;

        }
        else
        {
           scaleTextY = layout.area.getEndPosY();
           scaleLineY = scaleTextY + layout.labelScaleSize;
        }
        fill(0);  
        stroke(150);
        textFont(layout.font,layout.labelScaleSize);
        textAlign(LEFT,BASELINE);

        for (int i =0;i<=layout.gridLineCount;i++)
        {
            int x =  round(i*gridLineSeperationPx + layout.area.x)+0.5;
            line(x,0,x,layout.height);
            text(round(layout.zoomStart + i*gridLineSeperation),x,scaleTextY);
        }
        line(layout.area.x,scaleLineY+0.5,layout.area.getEndPosX(),scaleLineY+0.5);
   } 

   void setGenes(ArrayList genes)
   {
       this.genes = genes;
       layout.resetLanes();
   }
   
   String getGenesFromPosition(int position)
   {
       return "Has to be implemented";
   }
   
   void loadGeneData(String raw_data)
   {
       console.log(raw_data);
   }
   
}


class Gene
{
   String strand;
   int start;
   int end;
   String id;
   String name;
   String description;
   ArrayList cds = new ArrayList();
   ArrayList utrs = new ArrayList();
   int lane = -1;

   Gene(String id, String name,String description,String strand,int start,int end, ArrayList cds,ArrayList utrs)
   {
       this.id = id;
       this.name = name;
       this.description = description;
       this.strand = strand;
       this.start = start;
       this.end = end;
       this.cds = cds;
       this.utrs = utrs;
   }

   void render()
   {
      // render Line
      PVector vector = layout.getPixelPosition(start,end);
      int start_x = vector.x;
      int end_x = vector.y;
      if (strand == "-") 
          start_x = start_x - layout.cdsHeight;
      else 
          end_x = vector.y + layout.cdsHeight
          
      //println(end_x);
      // get Lane
      if (lane == -1)
      {
	      lane = layout.getFreeLane(start_x);
	      layout.lanes[lane] = end_x;
	  }
      renderGeneLine();
      
      
      if (layout.maximumZoomToDrawFeatures == -1 || layout.getZoomLength() <= layout.maximumZoomToDrawFeatures)
      {
	      for (int i = 0;i<utrs.size();i++)
	      {
	          UTR utr = utrs.get(i);
	          utr.render(lane);
	      }
	      for (int i = 0;i<cds.size();i++)
	      {
	          CDS cdsItem = cds.get(i);
	          cdsItem.render(lane);
	      }
	  }
      renderStrandArrow();
      renderName();
      
   }
   
   
   void renderName()
   {
       if (layout.getZoomLength() > layout.maximumZoomToDrawFeatures)
           return;
       PVector[] positions = layout.getPositions(start,end,"text",lane);
       PVector startPosition = (PVector)positions[0];
       fill(0, 126, 255);
       textAlign(LEFT,TOP);
       if (startPosition.x > 0)
       {
          text(name,startPosition.x,startPosition.y);
       }
   }
   void renderGeneLine()
   {
        PVector[] positions = layout.getPositions(start,end,"line",lane);
        PVector startPosition = (PVector)positions[0];
        PVector endPosition = (PVector)positions[1];
        stroke(0);
        strokeWeight(layout.geneLineWeight);
        strokeCap(SQUARE);
        line(startPosition.x,startPosition.y+0.5,endPosition.x,endPosition.y+0.5);
        strokeWeight(1);
   }
   
   void renderStrandArrow()
   {
         float pos;
         if (strand == "+")
             pos = end;
         else 
             pos = start;
         PVector[] positions = layout.getStrandArrowPositions(pos,strand,lane);
         //println(positions);
         fill(100);
         triangle(positions[0].x,positions[0].y,positions[1].x,positions[1].y,positions[3].x,positions[3].y);
   }
   
}


class GeneFeature
{
   int start;
   int end;
   color c;
   
   GeneFeature(int start,int end)
   {
      this.start = start;
      this.end = end;
   }
}

class UTR extends GeneFeature
{

  UTR(int start,int end)
  {
     super(start,end);
  }
  void render(int lane)
  {
       PVector[] positions = layout.getPositions(start,end,"utr",lane);
       PVector startPosition = (PVector)positions[0];
       PVector endPosition = (PVector)positions[1];
       fill(c);
       strokeCap(SQUARE);
       rectMode(CORNERS);
       noStroke();
       rect(startPosition.x,startPosition.y,endPosition.x,endPosition.y);
   }
  
}

class UTR5 extends UTR
{
    UTR5(int start,int end)
    {
       super(start,end);
       c = #ABF000;
    }
}

class UTR3 extends UTR
{
    UTR3(int start,int end)
    {
       super(start,end);
       c = #00BD39;
    }
}

class CDS extends GeneFeature
{
    CDS(int start,int end)
    {
       super(start,end);
       c = #FF8100;
    }
    
   void render(int lane)
   {
       PVector[] positions = layout.getPositions(start,end,"cds",lane);
       PVector startPosition = (PVector)positions[0];
       PVector endPosition = (PVector)positions[1];
       fill(c);
       strokeCap(SQUARE);
       rectMode(CORNERS);
       noStroke();
       rect(startPosition.x,startPosition.y,endPosition.x,endPosition.y);
   }
}


	
void mouseMoved() {
   layout.mousePositionX = mouseX;
   layout.mousePositionY = mouseY;
   
   int position = layout.getGenomePositionFromMouse();
   //int position = 100;
   String gene = browser.getGenesFromPosition(100);
   draw();
   if (eventHandler['mouseMoveEvent'] != null)
       eventHandler['mouseMoveEvent'](position,gene);
       
}

void mouseDragged() {
  if (layout.mouseButtonPressed == -1)
     layout.mouseButtonPressed = mouseButton;
  if (layout.mouseButtonPressedX == -1)
     layout.mouseButtonPressedX = mouseX;
  layout.mousePositionX = mouseX;
  if (mouseButton == RIGHT)
  {
     layout.moveZoomWindow(mouseX - layout.mouseButtonPressedX);
     if (eventHandler['zoomResizeEvent'] != null)
       	  eventHandler['zoomResizeEvent'](layout.zoomStart,layout.zoomEnd);
  }
  draw();
  
}

void mouseReleased()
{
   boolean isZoomResized = false; 
   if (layout.mouseButtonPressed == LEFT)
   {
       layout.calculateZoom();
       isZoomResized = true;
   }
   layout.mouseButtonPressed = -1;
   layout.mouseButtonPressedX = -1;
   draw();
   if (isZoomResized)
   {
   	 if (eventHandler['zoomResizeEvent'] != null)
       	  eventHandler['zoomResizeEvent'](layout.zoomStart,layout.zoomEnd);
   }
}

void mousePressed()
{
    if (mouseButton == CENTER)
    {
        layout.resetZoom();
        draw();
        if (eventHandler['zoomResizeEvent'] != null)
       	  eventHandler['zoomResizeEvent'](layout.zoomStart,layout.zoomEnd);
    }
}





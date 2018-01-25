import processing.core.PImage;

import java.util.List;

public interface Entity
{
   Point getPosition();
   void setPosition(Point p);
   PImage getCurrentImage();
   int getImageIndex();
   void setImageIndex(int i);
   List<PImage> getImages();
}


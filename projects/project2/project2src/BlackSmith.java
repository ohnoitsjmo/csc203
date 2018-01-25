import processing.core.PImage;

import java.util.List;

public class BlackSmith implements Entity {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod;

    private static final int SMITH_NUM_PROPERTIES = 4;
    private static final int SMITH_ID = 1;
    private static final int SMITH_COL = 2;
    private static final int SMITH_ROW = 3;
    private static final String SMITH_KEY = "blacksmith";

    public BlackSmith(String id, Point p, List<PImage> images ){
        this.id = id;
        this.position = p;
        this.images = images;
        this.resourceCount = 0;
        this.resourceLimit= 0;
        this.actionPeriod = 0;
        this.animationPeriod= 0;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point p) {
        position = p;
    }

    public PImage getCurrentImage() {
        return images.get(imageIndex);
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public void setImageIndex(int i) {
        imageIndex = i;
    }

    public List<PImage> getImages() {
        return images;
    }

    public static boolean parseSmith(String [] properties, WorldModel world,
                                     ImageStore imageStore)
    {
        if (properties.length == SMITH_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[SMITH_COL]),
                    Integer.parseInt(properties[SMITH_ROW]));
            Entity entity = new BlackSmith(properties[SMITH_ID], pt, imageStore.getImageList(SMITH_KEY));
            //Entity entity = pt.createBlacksmith(properties[SMITH_ID],

            world.tryAddEntity(entity);
        }

        return properties.length == SMITH_NUM_PROPERTIES;
    }


}

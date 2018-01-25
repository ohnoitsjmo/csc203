import processing.core.PImage;

import java.util.List;

public class Obstacle implements Entity {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    private static final int OBSTACLE_NUM_PROPERTIES = 4;
    private static final int OBSTACLE_ID = 1;
    private static final int OBSTACLE_COL = 2;
    private static final int OBSTACLE_ROW = 3;
    private static final String OBSTACLE_KEY = "obstacle";

    public static final String MINER_KEY = "miner";

    public Obstacle(String id, Point p, List<PImage> images){

        this.id = id;
        this.position = p;
        this.images = images;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point p) {
        this.position = p;
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

    public static boolean parseObstacle(String [] properties, WorldModel world,
                                        ImageStore imageStore)
    {
        if (properties.length == OBSTACLE_NUM_PROPERTIES)
        {
            Point pt = new Point(
                    Integer.parseInt(properties[OBSTACLE_COL]),
                    Integer.parseInt(properties[OBSTACLE_ROW]));
            Entity entity = new Obstacle(properties[OBSTACLE_ID], pt, imageStore.getImageList(OBSTACLE_KEY));
            world.tryAddEntity(entity);
        }

        return properties.length == OBSTACLE_NUM_PROPERTIES;
    }
}

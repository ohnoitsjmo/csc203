import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Ore implements Animate, Schedule {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    public static final String MINER_KEY = "miner";

    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod;

    private static final String BLOB_ID_SUFFIX = " -- blob";
    private static final int BLOB_PERIOD_SCALE = 4;
    private static final int BLOB_ANIMATION_MIN = 50;
    private static final int BLOB_ANIMATION_MAX = 150;
    private static final Random rand = new Random();
    private static final String ORE_ID_PREFIX = "ore -- ";
    private static final int ORE_CORRUPT_MIN = 20000;
    private static final int ORE_CORRUPT_MAX = 30000;
    public static final int ORE_REACH = 1;
    private static final int ORE_NUM_PROPERTIES = 5;
    private static final int ORE_ID = 1;
    private static final int ORE_COL = 2;
    private static final int ORE_ROW = 3;
    private static final int ORE_ACTION_PERIOD = 4;
    private static final String ORE_KEY = "ore";


    public Ore(String id, Point position, List<PImage> images, int actionPeriod){
        // return new Entity(EntityKind.ORE, id, this, images, 0, 0,
       // actionPeriod, 0);
        this.id = id;
        this.actionPeriod = actionPeriod;
        this.animationPeriod =0;
        this.images = images;
        this.position = position;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;

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

    public void executeOreActivity(WorldModel world,
                                   ImageStore imageStore, EventScheduler scheduler)
    {
        Point pos = position;  // store current position before removing

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        Entity blob = new OreBlob(id +BLOB_ID_SUFFIX, pos, imageStore.getImageList(Functions.BLOB_KEY),
                actionPeriod / BLOB_PERIOD_SCALE, BLOB_ANIMATION_MIN +
                rand.nextInt(BLOB_ANIMATION_MAX - BLOB_ANIMATION_MIN));


        world.addEntity(blob);
        if(blob instanceof OreBlob){
            ((OreBlob)blob).scheduleActions(scheduler, world, imageStore);
        }

    }

    public int getAnimationPeriod() {
        return animationPeriod;
    }

    public Action createActivityAction(WorldModel world, ImageStore imageStore) {
        return new Activity(this, world, imageStore);
    }

    public Action createAnimationAction(int repeatCount) {
        return new Animation(this, repeatCount);
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                actionPeriod);
    }

    public static boolean parseOre(String [] properties, WorldModel world,
                                   ImageStore imageStore)
    {
        if (properties.length == ORE_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[ORE_COL]),
                    Integer.parseInt(properties[ORE_ROW]));
            Entity entity =  new Ore(properties[ORE_ID], pt,imageStore.getImageList(ORE_KEY), Integer.parseInt(properties[ORE_ACTION_PERIOD] ));
            world.tryAddEntity(entity);
        }

        return properties.length == ORE_NUM_PROPERTIES;
    }
}

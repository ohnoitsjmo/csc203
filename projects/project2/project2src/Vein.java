import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

//should this be Animated??
public class Vein implements Animate {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private static final Random rand = new Random();

    public static final String MINER_KEY = "miner";

    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod;

    private static final String ORE_ID_PREFIX = "ore -- ";
    private static final int ORE_CORRUPT_MIN = 20000;
    private static final int ORE_CORRUPT_MAX = 30000;
    public static final int ORE_REACH = 1;

    private static final int VEIN_NUM_PROPERTIES = 5;
    private static final int VEIN_ID = 1;
    private static final int VEIN_COL = 2;
    private static final int VEIN_ROW = 3;
    private static final int VEIN_ACTION_PERIOD = 4;
    private static final String VEIN_KEY = "vein";

    public Vein(String id, Point position, List<PImage> images, int actionPeriod){
        this.id = id;
        this.position = position;
        this.images = images;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = 0;
        this.resourceCount=0;
        this.resourceLimit=0;
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

    public void executeVeinActivity(WorldModel world,
                                    ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Point> openPt = world.findOpenAround(position);

        if (openPt.isPresent())
        {



            Entity ore =  new Ore(ORE_ID_PREFIX + id, openPt.get(), imageStore.getImageList(Functions.ORE_KEY),
                    ORE_CORRUPT_MIN +
                            rand.nextInt(ORE_CORRUPT_MAX - ORE_CORRUPT_MIN));


            world.addEntity(ore);
            ((Ore)ore).scheduleActions(scheduler, world, imageStore);
        }

        scheduler.scheduleEvent(this,
                this.createActivityAction(world, imageStore),
                actionPeriod);
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

    public static boolean parseVein(String [] properties, WorldModel world,
                                    ImageStore imageStore)
    {
        if (properties.length == VEIN_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[VEIN_COL]),
                    Integer.parseInt(properties[VEIN_ROW]));
            Entity entity = new Vein(properties[VEIN_ID], pt, imageStore.getImageList(VEIN_KEY), Integer.parseInt(properties[VEIN_ACTION_PERIOD]));

            world.tryAddEntity(entity);
        }

        return properties.length == VEIN_NUM_PROPERTIES;
    }
}

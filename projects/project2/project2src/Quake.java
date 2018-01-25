import processing.core.PImage;

import java.util.List;


public class Quake implements Animate, Schedule {

    public static final String QUAKE_KEY = "quake";
    private static final String QUAKE_ID = "quake";
    private static final int QUAKE_ACTION_PERIOD = 1100;
    private static final int QUAKE_ANIMATION_PERIOD = 100;
    private static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    public static final String MINER_KEY = "miner";

    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod;
    private int repeatCount;

    public Quake(Point position, List<PImage> images){

        this.id = QUAKE_ID;
        this.position = position;
        this.animationPeriod = QUAKE_ACTION_PERIOD;
        this.actionPeriod = QUAKE_ACTION_PERIOD;
        this.repeatCount = QUAKE_ANIMATION_REPEAT_COUNT;
        this.resourceCount=0;
        this.resourceLimit=0;
        this.images = images;

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

    public void executeQuakeActivity(WorldModel world,
                                     ImageStore imageStore, EventScheduler scheduler)
    {
        scheduler.unscheduleAllEvents((Quake)this);
        world.removeEntity((Quake)this);
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
        scheduler.scheduleEvent(this ,
                createActivityAction(world, imageStore),
                actionPeriod);
        scheduler.scheduleEvent(this,
                createAnimationAction(repeatCount),
                getAnimationPeriod());
    }
}

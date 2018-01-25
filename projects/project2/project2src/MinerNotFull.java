import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class MinerNotFull implements Miner, Animate, Schedule {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    public static final String MINER_KEY = "miner";

    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod;

    private static final int MINER_NUM_PROPERTIES = 7;
    private static final int MINER_ID = 1;
    private static final int MINER_COL = 2;
    private static final int MINER_ROW = 3;
    private static final int MINER_LIMIT = 4;
    private static final int MINER_ACTION_PERIOD = 5;
    private static final int MINER_ANIMATION_PERIOD = 6;

    public MinerNotFull(String id, Point p, List<PImage> images, int resourceLimit, int actionPeriod, int animationPeriod
                        ){
        this.id = id;
        this.position = p;
        this.images = images;
        this.resourceLimit = resourceLimit;
        this.resourceCount = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public int getAnimationPeriod(){
        return animationPeriod;
    }

    public Action createActivityAction(WorldModel world, ImageStore imageStore) {
        return new Activity(this, world, imageStore);
    }

    public Action createAnimationAction(int repeatCount) {
        return new Animation(this, repeatCount);
    }

    public void scheduleActions(EventScheduler scheduler,
                                 WorldModel world, ImageStore imageStore)
    {

        scheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                actionPeriod);
        scheduler.scheduleEvent(this,
                createAnimationAction(0), getAnimationPeriod());
    }

    public List<PImage> getImages(){
        return images;
    }

    public void setImageIndex(int i){
        imageIndex = i;
    }

    public int getImageIndex(){
        return imageIndex;
    }

    public Point getPosition(){
        return position;
    }

    public void setPosition(Point p){
        position = p;
    }

    public PImage getCurrentImage(){
        if (this instanceof Entity)
        {
            return images.get(imageIndex);
        }
        else
        {
            throw new UnsupportedOperationException(
                    String.format("getCurrentImage not supported for %s",
                            this));
        }
    }

    public Point nextPositionMiner(WorldModel world,
                                   Point destPos)
    {
        int horiz = Integer.signum(destPos.x - position.x);
        Point newPos = new Point(position.x + horiz,
                position.y);

        if (horiz == 0 || world.isOccupied(newPos))
        {
            int vert = Integer.signum(destPos.y - position.y);
            newPos = new Point(position.x,
                    position.y + vert);

            if (vert == 0 || world.isOccupied(newPos))
            {
                newPos = position;
            }
        }

        return newPos;
    }

    private boolean moveToNotFull(WorldModel world,
                                  Entity target, EventScheduler scheduler)
    {
        if (position.adjacent(target.getPosition()))
        {
            resourceCount += 1;
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);

            return true;
        }
        else
        {
            Point nextPos = nextPositionMiner(world, target.getPosition());

            if (!position.equals(nextPos))
            {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent())
                {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    public void executeMinerNotFullActivity(
            WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> notFullTarget = world.findNearest(position, Ore.class);

        if (!notFullTarget.isPresent() ||
                !moveToNotFull(world, notFullTarget.get(), scheduler) ||
                !transformNotFull(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    actionPeriod);
        }
    }

    public boolean transformNotFull(WorldModel world,
                                    EventScheduler scheduler, ImageStore imageStore)
    {
        if (resourceCount >= resourceLimit)
        {
            Entity miner = new MinerFull(id, position, images, resourceLimit, actionPeriod, animationPeriod);

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            ((MinerFull)miner).scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public static boolean parseMiner(String [] properties, WorldModel world,
                                     ImageStore imageStore)
    {
        if (properties.length == MINER_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[MINER_COL]),
                    Integer.parseInt(properties[MINER_ROW]));
            Entity entity = new MinerNotFull(properties[MINER_ID], pt, imageStore.getImageList(MINER_KEY),
                    Integer.parseInt(properties[MINER_LIMIT]),  Integer.parseInt(properties[MINER_ACTION_PERIOD]),
                    Integer.parseInt(properties[MINER_ANIMATION_PERIOD]));
            world.tryAddEntity(entity);
        }

        return properties.length == MINER_NUM_PROPERTIES;
    }
}

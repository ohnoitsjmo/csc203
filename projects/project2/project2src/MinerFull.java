import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class MinerFull implements Miner, Animate, Schedule {

    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    public static final String MINER_KEY = "miner";

    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod;
    private Entity entity;
    private static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;

    public MinerFull(String id, Point position, List<PImage> images,
                     int resourceLimit,
                     int actionPeriod, int animationPeriod)
    {
        this.id = id;
        this.position = position;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceLimit;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.images = images;

    }


    public int getAnimationPeriod(){
        return animationPeriod;
    }

    public Action createActivityAction(WorldModel world,
                                       ImageStore imageStore)
    {
        return new Activity(this, world, imageStore);
    }

    public Action createAnimationAction(int repeatCount)

    {
        return new Animation(this, repeatCount);
    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                actionPeriod);
        scheduler.scheduleEvent(this, createAnimationAction(0),
                this.getAnimationPeriod());
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
        } else
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

    private boolean moveToFull(WorldModel world,
                               Entity target, EventScheduler scheduler)
    {
        if (position.adjacent(target.getPosition()))
        {
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

                world.moveEntity(this, nextPos); //this is the MinerFull object
            }
            return false;
        }
    }
    public void executeMinerFullActivity(WorldModel world,
                                         ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> fullTarget = world.findNearest(position,
                BlackSmith.class);

        if (fullTarget.isPresent() &&
                moveToFull(world, fullTarget.get(), scheduler))
        {
            transformFull(world, scheduler, imageStore);
        }
        else
        {
            scheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    this.actionPeriod);
        }
    }

    public void transformFull(WorldModel world,
                              EventScheduler scheduler, ImageStore imageStore)
    {
        Entity miner = new MinerNotFull(id, position, images, resourceLimit, actionPeriod, animationPeriod);

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        ((MinerNotFull)miner).scheduleActions(scheduler, world, imageStore);
    }
}
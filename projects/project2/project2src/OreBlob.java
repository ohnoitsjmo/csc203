import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class OreBlob implements Animate, Schedule {
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

    private static final String BLOB_ID_SUFFIX = " -- blob";
    private static final int BLOB_PERIOD_SCALE = 4;
    private static final int BLOB_ANIMATION_MIN = 50;
    private static final int BLOB_ANIMATION_MAX = 150;
    private static final Random rand = new Random();


    public OreBlob(String id, Point position, List<PImage> images,
                   int actionPeriod, int animationPeriod){

        this.id = id;
        this.position = position;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.images = images;

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
        scheduler.scheduleEvent(this,
                createAnimationAction(0), getAnimationPeriod());
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

    public void executeOreBlobActivity(WorldModel world,
                                       ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> blobTarget = world.findNearest(
                position, Vein.class);

        long nextPeriod = actionPeriod;

        if (blobTarget.isPresent())
        {
            Point tgtPos = blobTarget.get().getPosition();

            if (moveToOreBlob(world, blobTarget.get(), scheduler))
            {
                Entity quake = new Quake(tgtPos, imageStore.getImageList(Quake.QUAKE_KEY));

                world.addEntity(quake);
                nextPeriod += actionPeriod;
                if (quake instanceof Quake) {
                    ((Quake)quake).scheduleActions(scheduler, world, imageStore);
                }
            }
        }

        scheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                nextPeriod);
    }

    private boolean moveToOreBlob(WorldModel world,
                                  Entity target, EventScheduler scheduler)
    {
        if (position.adjacent(target.getPosition()))
        {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else
        {
            Point nextPos = nextPositionOreBlob(world, target.getPosition());

            if (!this.position.equals(nextPos))
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

    private Point nextPositionOreBlob(WorldModel world,
                                      Point destPos)
    {
        int horiz = Integer.signum(destPos.x - position.x);
        Point newPos = new Point(position.x + horiz,
                position.y);

        Optional<Entity> occupant = world.getOccupant(newPos);

        if (horiz == 0 ||
                (occupant.isPresent() && !(occupant.get() instanceof  Ore)))
        {
            int vert = Integer.signum(destPos.y - position.y);
            newPos = new Point(position.x, position.y + vert);
            occupant = world.getOccupant(newPos);

            if (vert == 0 ||
                    (occupant.isPresent() && !(occupant.get() instanceof  Ore)))
            {
                newPos = position;
            }
        }

        return newPos;
    }
}

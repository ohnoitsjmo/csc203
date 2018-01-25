public interface Animate extends Entity {
    int getAnimationPeriod();
    Action createActivityAction(WorldModel world, ImageStore imageStore);
    Action createAnimationAction(int repeatCount);
    void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
}

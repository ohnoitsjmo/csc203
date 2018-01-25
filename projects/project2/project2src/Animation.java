public class Animation implements Action {

    private Entity entity;
    private int repeatCount;

    public Animation(Entity entity, int repeatCount)
    {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler)
    {
        executeAnimationAction(scheduler);
    }

    public void executeAnimationAction(
            EventScheduler scheduler)
    {
        nextImage(entity);

        if (repeatCount != 1)
        {
            scheduler.scheduleEvent(entity,
                    new Animation(entity,
                            Math.max(repeatCount - 1, 0)),
                    ((Animate)entity).getAnimationPeriod());
        }
    }

    public void nextImage(Entity e)
    {
        e.setImageIndex((e.getImageIndex()+ 1) % e.getImages().size());
    }
}

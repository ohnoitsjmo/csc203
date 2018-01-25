public class Activity implements Action {

        private Entity entity;
        private WorldModel world;
        private ImageStore imageStore;

        public Activity(Entity entity, WorldModel world,
                      ImageStore imageStore)
        {
            this.entity = entity;
            this.world = world;
            this.imageStore = imageStore;
        }

        public void executeAction(EventScheduler scheduler)
        {
            executeActivityAction(scheduler);
        }

        public void executeActivityAction(
                EventScheduler scheduler)
        {
                if (entity instanceof MinerFull) {
                    ((MinerFull)entity).executeMinerFullActivity(world,
                            imageStore, scheduler);
                    return;
                }
                if (entity instanceof MinerNotFull) {
                    System.out.println(entity.getPosition());
                    ((MinerNotFull)entity).executeMinerNotFullActivity(world,
                            imageStore, scheduler);
                    return;
                }
                if (entity instanceof Ore) {
                    ((Ore)entity).executeOreActivity(world, imageStore,
                            scheduler);
                    return;
                }
                if (entity instanceof OreBlob) {
                    ((OreBlob)entity).executeOreBlobActivity(world,
                            imageStore, scheduler);
                    return;
                }
                if (entity instanceof Quake) {
                    ((Quake)entity).executeQuakeActivity(world, imageStore,
                            scheduler);
                    return;
                }
                if (entity instanceof Vein) {
                    ((Vein)entity).executeVeinActivity(world, imageStore,
                            scheduler);
                    return;
                }

                throw new UnsupportedOperationException(
                        String.format("executeActivityAction not supported for %s",
                                entity.getClass()));
            }
}

import processing.core.PImage;

import java.util.*;

final class WorldModel
{
   private int numRows;
   private int numCols;
   private Background background[][];
   private Entity occupancy[][];
   private Set<Entity> entities;

   private static final int ORE_REACH = 1;

   private static final int PROPERTY_KEY = 0;
   private static final String BGND_KEY = "background";
   private static final String MINER_KEY = "miner";
   private static final String OBSTACLE_KEY = "obstacle";
   private static final String ORE_KEY = "ore";
   private static final String SMITH_KEY = "blacksmith";
   private static final String VEIN_KEY = "vein";


   public WorldModel(int numRows, int numCols, Background defaultBackground)
   {
      this.numRows = numRows;
      this.numCols = numCols;
      this.background = new Background[numRows][numCols];
      this.occupancy = new Entity[numRows][numCols];
      this.entities = new HashSet<>();

      for (int row = 0; row < numRows; row++)
      {
         Arrays.fill(this.background[row], defaultBackground);
      }
   }

   public Set<Entity> getEntities() { return entities; }

   public int getNumRows() {
      return numRows;
   }

   public int getNumCols() {
      return numCols;
   }

   public void removeEntity(Entity entity)
   {
      removeEntityAt(entity.getPosition());
   }

   public void removeEntityAt(Point pos)
   {
      if (withinBounds(pos)
              && getOccupancyCell(pos) != null)
      {
         Entity entity = getOccupancyCell(pos);

         /* this moves the entity just outside of the grid for
            debugging purposes */
         entity.setPosition(new Point(-1, -1));
         entities.remove(entity);
         setOccupancyCell(pos, null);
      }
   }

   public boolean withinBounds(Point pos)
   {
      return pos.y >= 0 && pos.y < numRows && pos.x >= 0 && pos.x < numCols;
   }

   public Entity getOccupancyCell(Point pos)
   {
      return occupancy[pos.y][pos.x];
   }

   public void setOccupancyCell(Point pos,
                                       Entity entity)
   {
      occupancy[pos.y][pos.x] = entity;
   }

   public void addEntity(Entity entity)
   {
      if (withinBounds(entity.getPosition()))
      {
         setOccupancyCell(entity.getPosition(), entity);
         entities.add(entity);
      }
   }

   public boolean isOccupied(Point pos)
   {
      return withinBounds(pos) && getOccupancyCell(pos) != null;
   }

   public void moveEntity(Entity entity, Point pos)
   {
      Point oldPos = entity.getPosition();
      if (withinBounds(pos) && !pos.equals(oldPos))
      {
         setOccupancyCell(oldPos, null);
         removeEntityAt(pos);
         setOccupancyCell(pos, entity);
         entity.setPosition(pos);
      }
   }

   public Optional<Entity> getOccupant(Point pos)
   {
      if (isOccupied(pos))
      {
         return Optional.of(getOccupancyCell(pos));
      }
      else
      {
         return Optional.empty();
      }
   }

   public Optional<Point> findOpenAround(Point pos)
   {
      for (int dy = -ORE_REACH; dy <= ORE_REACH; dy++)
      {
         for (int dx = -ORE_REACH; dx <= ORE_REACH; dx++)
         {
            Point newPt = new Point(pos.x + dx, pos.y + dy);
            if (withinBounds(newPt) &&
                    !isOccupied(newPt))
            {
               return Optional.of(newPt);
            }
         }
      }

      return Optional.empty();
   }

   public void load(Scanner in,  ImageStore imageStore)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            if (!processLine(in.nextLine(), imageStore))
            {
               System.err.println(String.format("invalid entry on line %d",
                       lineNumber));
            }
         }
         catch (NumberFormatException e)
         {
            System.err.println(String.format("invalid entry on line %d",
                    lineNumber));
         }
         catch (IllegalArgumentException e)
         {
            System.err.println(String.format("issue on line %d: %s",
                    lineNumber, e.getMessage()));
         }
         lineNumber++;
      }
   }


   public boolean processLine(String line,
                                     ImageStore imageStore)
   {
      String[] properties = line.split("\\s");
      if (properties.length > 0)
      {
         switch (properties[PROPERTY_KEY])
         {
            case BGND_KEY:
               return Background.parseBackground(properties, this, imageStore);
            case MINER_KEY:
               return MinerNotFull.parseMiner(properties, this, imageStore);
            case OBSTACLE_KEY:
               return Obstacle.parseObstacle(properties, this, imageStore);
            case ORE_KEY:
               return Ore.parseOre(properties, this, imageStore);
            case SMITH_KEY:
               return BlackSmith.parseSmith(properties, this, imageStore);
            case VEIN_KEY:
               return Vein.parseVein(properties, this, imageStore);
         }
      }

      return false;
   }

   public void tryAddEntity(Entity entity)
   {
      if (isOccupied(entity.getPosition()))
      {
         // arguably the wrong type of exception, but we are not
         // defining our own exceptions yet
         throw new IllegalArgumentException("position occupied");
      }

      addEntity(entity);
   }

   public Optional<Entity> findNearest(Point pos,
                                              Object e)
   {
      List<Entity> ofType = new LinkedList<>();
      for (Entity entity : entities)
      {
         if (entity.getClass().equals(e))
         {
            ofType.add(entity);
         }
      }

      return pos.nearestEntity(ofType);
   }

   public Optional<PImage> getBackgroundImage(Point pos)
   {
      if (withinBounds(pos))
      {
         return Optional.of(getBackgroundCell(pos).getCurrentImage());
      }
      else
      {
         return Optional.empty();
      }
   }

   public void setBackground(Point pos,
                                    Background background)
   {
      if (withinBounds(pos))
      {
         setBackgroundCell(this, pos, background);
      }
   }

   public void setBackgroundCell(WorldModel world, Point pos,
                                        Background background)
   {
      world.background[pos.y][pos.x] = background;
   }

   public Background getBackgroundCell(Point pos)
   {
      return background[pos.y][pos.x];
   }

}
